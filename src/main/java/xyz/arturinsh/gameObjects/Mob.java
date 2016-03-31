package xyz.arturinsh.gameObjects;

import java.util.ArrayList;
import java.util.List;

import xyz.arturinsh.packets.Packets.MobUpdate;
import xyz.arturinsh.packets.Packets.UserCharacter;

public class Mob {
	public float x, y, z, r;
	public float moveSpeed = 0.5f;
	public float destX, destY, destZ;
	public float startX, startY, startZ;
	public float defenseRadius = 20;
	public long Id;

	private boolean move = false;

	public MobType type;

	private float spawnX, spawnY, spawnZ;

	private String destinationPlayerName = null;

	public Mob(float _spawnX, float _spawnY, float _spawnZ) {
		this.spawnX = _spawnX;
		this.spawnY = _spawnY;
		this.spawnZ = _spawnZ;
		setPosition(spawnX, spawnY, spawnZ);
	}

	public void setPosition(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void move(float nx, float ny, float nz) {
		startX = x;
		startY = 0;
		startZ = z;
		destX = nx;
		destY = 0;
		destZ = nz;

		calculateRotation(destX, destZ);
		move = true;
	}

	public void update(List<PlayerConnection> players) {
		List<PlayerRange> ranges = new ArrayList<PlayerRange>();
		for (PlayerConnection player : players) {
			float distanceToSpawn = length(spawnX, spawnY, spawnZ, player.character.x, player.character.y,
					player.character.z);
			if (distanceToSpawn < defenseRadius) {
				float distanceToMob = length(x, y, z, player.character.x, player.character.y, player.character.z);
				ranges.add(new PlayerRange(distanceToMob, player.character));
			}
		}

		if (ranges.size() > 0) {
			if (destinationPlayerName == null) {
				UserCharacter moveChar = minRangeUserCharacter(ranges);
				destinationPlayerName = moveChar.charName;
				move(moveChar.x, moveChar.y, moveChar.z);
			} else {
				UserCharacter moveChar = getCharacterInRanges(ranges, destinationPlayerName);
				if (moveChar != null) {
					move(moveChar.x, moveChar.y, moveChar.z);
				} else {
					moveChar = minRangeUserCharacter(ranges);
					destinationPlayerName = moveChar.charName;
					move(moveChar.x, moveChar.y, moveChar.z);
				}
			}
		} else {
			move(spawnX, spawnY, spawnZ);
		}

		if (move)
		{
			this.x += Math.sin(Math.toRadians(r)) * moveSpeed;
			this.z += Math.cos(Math.toRadians(r)) * moveSpeed;

			if (pointOnLine(x, y, z)) {
				this.x = destX;
				this.y = destY;
				this.z = destZ;
				move = false;
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
		return update;
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
