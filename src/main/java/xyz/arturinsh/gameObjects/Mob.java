package xyz.arturinsh.gameObjects;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.ViewportLayout;

import xyz.arturinsh.gameserver.GameWorld;
import xyz.arturinsh.helpers.BoundingBox;
import xyz.arturinsh.helpers.Point;
import xyz.arturinsh.packets.Packets.MobUpdate;
import xyz.arturinsh.packets.Packets.UserCharacter;

public class Mob {

	public int defaultHP = 100;
	public float x, y, z, r;
	public float moveSpeed = 0.5f;
	public float destX, destY, destZ;
	public float startX, startY, startZ;
	public float defenseRadius = 20;
	public float closeRadius = 5;
	private float attack_center_radius = 3.5f;
	public long Id;
	public int hp = 100;
	public int attack = 10;
	public int attackTime = 1000;
	public int exp = 10;
	private int respawnTime = 5;
	private int hpUpdate = 10;

	private Calendar nextSpawnTime;
	private Calendar nextHPupgrade;

	private boolean move = false, attacking = false, dead = false, passiveMob = false;

	public MobType type;

	private float spawnX, spawnY, spawnZ;

	private String destinationPlayerName = null;
	private GameWorld world;

	public Mob(float _spawnX, float _spawnY, float _spawnZ, MobType _type, GameWorld _world) {
		this.spawnX = _spawnX;
		this.spawnY = _spawnY;
		this.spawnZ = _spawnZ;
		this.world = _world;
		this.type = _type;
		setMobType(type);
		setPosition(spawnX, spawnY, spawnZ);
	}

	private void setMobType(MobType type) {
		switch (type) {
		case VLADINATORS:
			defaultHP = 500;
			hp = defaultHP;
			attack = 40;
			attackTime = 2050;
			attack_center_radius = 11.5f;
			defenseRadius = 30;
			closeRadius = 15;
			exp = 100;
			respawnTime = 10;
			hpUpdate = 50;
			passiveMob = true;
			break;
		case DOG:
			defaultHP = 100;
			hp = defaultHP;
			attack = 10;
			attackTime = 1000;
			attack_center_radius = 3.5f;
			defenseRadius = 20;
			closeRadius = 5;
			exp = 10;
			respawnTime = 5;
			hpUpdate = 10;
			passiveMob = false;
			break;
		case CROCO:
			defaultHP = 200;
			hp = defaultHP;
			attack = 20;
			attackTime = 2100;
			attack_center_radius = 7f;
			defenseRadius = 50;
			closeRadius = 7;
			exp = 20;
			respawnTime = 10;
			hpUpdate = 20;
			passiveMob = true;
			break;
		}
	}

	public void setPosition(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void move(float nx, float ny, float nz, boolean toAttack) {
		startX = x;
		startY = 0;
		startZ = z;

		if (!attacking) {
			calculateRotation(nx, nz);
		}

		destX = nx;
		destY = 0;
		destZ = nz;

		move = true;
	}

	private void reset() {
		setPosition(spawnX, spawnY, spawnZ);
		hp = defaultHP;
		dead = false;
		destinationPlayerName = null;
		move = false;
		attacking = false;
	}

	public void update(List<PlayerConnection> players) {

		// if (hp <= 0 && !dead)
		// kill();

		if (dead && Calendar.getInstance().getTimeInMillis() > nextSpawnTime.getTimeInMillis()) {
			reset();
		}

		if (!dead) {
			List<PlayerRange> ranges = new ArrayList<PlayerRange>();
			for (PlayerConnection player : players) {
				if (player.character.hp > 0) {
					float distanceToSpawn = length(spawnX, spawnY, spawnZ, player.character.x, player.character.y,
							player.character.z);
					if (distanceToSpawn < defenseRadius) {
						float distanceToMob = length(x, y, z, player.character.x, player.character.y,
								player.character.z);
						ranges.add(new PlayerRange(distanceToMob, player.character));
					}
				}
			}
			boolean agressive = false;
			if (ranges.size() > 0) {
				if (!passiveMob) {
					agressive = true;
					if (destinationPlayerName == null) {
						UserCharacter moveChar = minRangeUserCharacter(ranges);
						destinationPlayerName = moveChar.charName;
						move(moveChar.x, moveChar.y, moveChar.z, agressive);
					} else {
						UserCharacter moveChar = getCharacterInRanges(ranges, destinationPlayerName);
						if (moveChar != null) {
							move(moveChar.x, moveChar.y, moveChar.z, agressive);
						} else {
							moveChar = minRangeUserCharacter(ranges);
							destinationPlayerName = moveChar.charName;
							move(moveChar.x, moveChar.y, moveChar.z, agressive);
						}
					}
				} else {
					if (destinationPlayerName != null) {
						agressive = true;
						UserCharacter moveChar = getCharacterInRanges(ranges, destinationPlayerName);
						if (moveChar != null) {
							move(moveChar.x, moveChar.y, moveChar.z, agressive);
						} else {
							moveChar = minRangeUserCharacter(ranges);
							destinationPlayerName = moveChar.charName;
							move(moveChar.x, moveChar.y, moveChar.z, agressive);
						}
					}
				}
			} else if (spawnX != this.x && spawnY != this.z && spawnZ != this.z) {
				move(spawnX, spawnY, spawnZ, agressive);
				destinationPlayerName = null;
			}

			if (move && !attacking) {
				float tempX = this.x + (float) Math.sin(Math.toRadians(r)) * moveSpeed;
				float tempZ = this.z + (float) Math.cos(Math.toRadians(r)) * moveSpeed;

				if (agressive && (pointOnLine(tempX, y, tempZ)
						|| length(tempX, y, tempZ, destX, destY, destZ) < closeRadius)) {
					this.x = destX;
					this.y = destY;
					this.z = destZ;
					move = false;
				} else if (pointOnLine(tempX, y, tempZ)) {
					this.x = destX;
					this.y = destY;
					this.z = destZ;
					move = false;
				} else {
					this.x = tempX;
					this.z = tempZ;
				}

			}
			if (!move && agressive) {
				attacking = true;
				world.mobAttack(this);
			}
			if (hp < defaultHP && !agressive)

			{
				if (nextHPupgrade == null) {
					nextHPupgrade = Calendar.getInstance();
					nextHPupgrade.add(Calendar.SECOND, 1);
				} else if (Calendar.getInstance().getTimeInMillis() > nextHPupgrade.getTimeInMillis()) {
					if (hp + hpUpdate > defaultHP) {
						hp = defaultHP;
						nextHPupgrade = null;
					} else {
						hp += hpUpdate;
						nextHPupgrade = Calendar.getInstance();
						nextHPupgrade.add(Calendar.SECOND, 1);
					}
				}
			}

		}

	}

	private UserCharacter getCharacterInRanges(List<PlayerRange> list, String charName) {
		for (PlayerRange player : list) {
			if (charName.contentEquals(player.charater.charName)) {
				return player.charater;
			}
		}
		return null;
	}

	private UserCharacter minRangeUserCharacter(List<PlayerRange> list) {
		PlayerRange minRange = new PlayerRange(0, null);
		boolean first = true;
		for (PlayerRange player : list) {
			if (first) {
				minRange = player;
				first = false;
			}
			if (player.distanceToMob < minRange.distanceToMob) {
				minRange = player;
			}
		}
		return minRange.charater;
	}

	private void calculateRotation(float nx, float nz) {
		float tx = nx - x;
		float tz = nz - z;

		r = (float) (Math.atan2(tx, tz) * 180 / Math.PI);
	}

	private boolean pointOnLine(float eX, float eY, float eZ) {

		if (!((startX <= destX && destX <= eX) || (eX <= destX && destX <= startX)))
			return false;

		if (!((startY <= destY && destY <= eY) || (eY <= destY && destY <= startY)))
			return false;

		if (!((startZ <= destZ && destZ <= eZ) || (eZ <= destZ && destZ <= startZ)))
			return false;

		return true;
	}

	public MobUpdate getMobUpdateData() {
		MobUpdate update = new MobUpdate();
		update.x = x;
		update.y = y;
		update.z = z;
		update.r = r;
		update.ID = Id;
		update.hp = hp;
		update.type = type;
		return update;
	}

	public void attackEnded() {
		attacking = false;
	}

	public BoundingBox getBoundingBox() {
		Point center = new Point(x, z);
		BoundingBox box = null;
		if (type == MobType.DOG) {
			box = new BoundingBox(center, new Point(1, 2), new Point(1, -2), new Point(-1, -2), new Point(-1, 2));
			box.addAngle((int) r);
		}
		if (type == MobType.VLADINATORS) {
			box = new BoundingBox(center, new Point(3, 5), new Point(3, -5), new Point(-3, -5), new Point(-3, 5));
			box.addAngle((int) r);
		}
		if (type == MobType.CROCO) {
			box = new BoundingBox(center, new Point(2, 4), new Point(2, -4), new Point(-2, -4), new Point(-2, 4));
			box.addAngle((int) r);
		}
		return box;
	}

	public BoundingBox getAttackBox() {
		float rotToRadians = (float) Math.toRadians(r);
		float xOffset = (float) (attack_center_radius * Math.sin(rotToRadians));
		float zOffset = (float) (attack_center_radius * Math.cos(rotToRadians));

		float nx = x + xOffset;
		float nz = z + zOffset;

		Point center = new Point(nx, nz);
		BoundingBox box = null;
		if (type == MobType.DOG) {
			box = new BoundingBox(center, new Point(2, 3.5f), new Point(2, -3.5f), new Point(-2, -3.5f),
					new Point(-2, 3.5f));
		}
		if (type == MobType.VLADINATORS) {
			box = new BoundingBox(center, new Point(7, 5.5f), new Point(7, -5.5f), new Point(-7, -5.5f),
					new Point(-7, 5.5f));
		}
		if (type == MobType.CROCO) {
			box = new BoundingBox(center, new Point(3, 3f), new Point(3, -3f), new Point(-3, -3f), new Point(-3, 3f));
		}
		box.addAngle((int) r);
		return box;
	}

	public void kill() {
		dead = true;
		nextSpawnTime = Calendar.getInstance();
		nextSpawnTime.add(Calendar.SECOND, respawnTime);
	}

	public boolean isAlive() {
		return !dead;
	}

	private float length(float x, float y, float z, float nx, float ny, float nz) {
		return (float) Math.sqrt(Math.pow(nx - x, 2) + Math.pow(ny - y, 2) + Math.pow(nz - z, 2));
	}

	private class PlayerRange {
		public float distanceToMob;
		public UserCharacter charater;

		public PlayerRange(float _distance, UserCharacter _charater) {
			distanceToMob = _distance;
			charater = _charater;
		}
	}

	public void receiveAttack(PlayerConnection player) {
		hp -= player.getAttack();
		if (passiveMob) {
			destinationPlayerName = player.character.charName;
		}
		if (hp <= 0 && isAlive()) {
			kill();
			player.character.experience += exp;
		}
	}
}
