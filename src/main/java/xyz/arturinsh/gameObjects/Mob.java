package xyz.arturinsh.gameObjects;

import xyz.arturinsh.packets.Packets.MobUpdate;

public class Mob {
	public float x, y, z, r;
	public float moveSpeed;
	public float destX, destY, destZ;
	public float startX, startY, startZ;

	public long Id;

	private boolean move = false;

	public MobType type;

	public Mob() {

	}

	public void setPosition(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void move(float nx, float ny, float nz) {
		startX = x;
		startY = y;
		startZ = z;
		destX = nx;
		destY = ny;
		destZ = nz;

		calculateRotation(destX, destZ);
		move = true;
	}

	public void update() {
		if (move) {
			this.x += Math.sin(Math.toRadians(r)) * moveSpeed;
			this.z += Math.cos(Math.toRadians(r)) * moveSpeed;

			if (pointOnLine(x, y, z)) {
				move = false;
				this.x = destX;
				this.y = destY;
				this.z = destZ;
				move = false;
			}
		}
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
}
