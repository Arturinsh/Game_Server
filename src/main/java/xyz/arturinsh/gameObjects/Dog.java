package xyz.arturinsh.gameObjects;

import xyz.arturinsh.packets.Packets.DogPositionUpdate;

public class Dog {
	public float x, y, z, r;
	public int ID = 0;
	public float moveSpeed = 0.5f;
	public float destX = 0, destY = 0, destZ = 0;
	public float startX = 0, startY = 0, startZ = 0;

	private boolean move = false;

	public Dog() {
		x = 0;
		y = 0;
		z = 0;
		r = 0;
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

	public DogPositionUpdate getDogPosUpdate() {
		DogPositionUpdate dogPosUpdate = new DogPositionUpdate();
		dogPosUpdate.x = x;
		dogPosUpdate.y = y;
		dogPosUpdate.z = z;
		dogPosUpdate.r = r;
		dogPosUpdate.ID = ID;
		return dogPosUpdate;
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

}
