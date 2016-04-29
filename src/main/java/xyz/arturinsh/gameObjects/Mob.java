package xyz.arturinsh.gameObjects;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import xyz.arturinsh.gameserver.GameWorld;
import xyz.arturinsh.helpers.BoundingBox;
import xyz.arturinsh.helpers.Point;
import xyz.arturinsh.packets.Packets.MobUpdate;
import xyz.arturinsh.packets.Packets.UserCharacter;

public class Mob {
	private final float ATTACK_CENTER_DISTANCE = 3.5f;

	public float x, y, z, r;
	public float moveSpeed = 0.5f;
	public float destX, destY, destZ;
	public float startX, startY, startZ;
	public float defenseRadius = 20;
	public float closeRadius = 5;
	public long Id;
	public int hp = 100;
	public int attack = 10;

	private Calendar nextSpawnTime;

	private boolean move = false, attacking = false, dead = false;

	public MobType type;

	private float spawnX, spawnY, spawnZ;

	private String destinationPlayerName = null;
	private GameWorld world;

	public Mob(float _spawnX, float _spawnY, float _spawnZ, GameWorld _world) {
		this.spawnX = _spawnX;
		this.spawnY = _spawnY;
		this.spawnZ = _spawnZ;
		this.world = _world;
		setPosition(spawnX, spawnY, spawnZ);
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

		if (!attacking)
			calculateRotation(nx, nz);
		// System.out.println("Dx=" + nx + "Dz=" + nz + "R=" + r);
		float ln = length(x, y, z, nx, ny, nz);
		// System.out.println(ln);
		if (toAttack) {
			if (ln > closeRadius) {
				float reverseRot = this.r + 180;
				float rotToRadians = (float) Math.toRadians(reverseRot);
				float xOffset = (float) (closeRadius * Math.sin(rotToRadians));
				float zOffset = (float) (closeRadius * Math.cos(rotToRadians));
				float px = nx + xOffset;
				float pz = nz + zOffset;

				destX = px;
				destY = 0;
				destZ = pz;
			}
			// System.out.println("Px=" + px + "Pz=" + pz + "R=" + reverseRot);
		} else {
			destX = nx;
			destY = 0;
			destZ = nz;
		}
		move = true;
	}

	private void reset() {
		setPosition(spawnX, spawnY, spawnZ);
		hp = 100;
		dead = false;
		destinationPlayerName = null;
		move = false;
		attacking = false;
	}

	public void update(List<PlayerConnection> players) {

		if (hp <= 0 && !dead)
			kill();

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
			} else if (spawnX != this.x && spawnY != this.z && spawnZ != this.z) {
				move(spawnX, spawnY, spawnZ, agressive);
			}

			if (move && !attacking) {
				float tempX = this.x + (float) Math.sin(Math.toRadians(r)) * moveSpeed;
				float tempZ = this.z + (float) Math.cos(Math.toRadians(r)) * moveSpeed;

				if (pointOnLine(tempX, y, tempZ)) {
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
		return update;
	}

	public void attackEnded() {
		attacking = false;
	}

	public BoundingBox getBoundingBox() {
		Point center2 = new Point(x, z);
		BoundingBox box2 = new BoundingBox(center2, new Point(1, 2), new Point(1, -2), new Point(-1, -2),
				new Point(-1, 2));
		box2.addAngle((int) r);
		return box2;
	}

	public BoundingBox getAttackBox() {
		float rotToRadians = (float) Math.toRadians(r);
		float xOffset = (float) (ATTACK_CENTER_DISTANCE * Math.sin(rotToRadians));
		float zOffset = (float) (ATTACK_CENTER_DISTANCE * Math.cos(rotToRadians));

		float nx = x + xOffset;
		float nz = z + zOffset;

		Point center = new Point(nx, nz);
		BoundingBox box = new BoundingBox(center, new Point(2, 3.5f), new Point(2, -3.5f), new Point(-2, -3.5f),
				new Point(-2, 3.5f));
		box.addAngle((int) r);
		return box;
	}

	public void kill() {
		dead = true;
		nextSpawnTime = Calendar.getInstance();
		nextSpawnTime.add(Calendar.SECOND, 5);
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
}
