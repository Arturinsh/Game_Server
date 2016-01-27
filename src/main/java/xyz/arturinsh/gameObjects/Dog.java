package xyz.arturinsh.gameObjects;

import xyz.arturinsh.packets.Packets.DogPositionUpdate;

public class Dog {
	public float x, y, z, r;
	public int ID = 0;
	public float moveSpeed=0.1f;
	public float destX = 0, destY = 0, destZ = 0;

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
		destX = nx;
		destY = ny;
		destZ = nz;

		calculateRotation(destX, destZ);
		System.out.println(r);
	}

	public void update() {
		this.x+=Math.sin(Math.toRadians(r))*moveSpeed;
		this.z+=Math.cos(Math.toRadians(r))*moveSpeed;
		System.out.println(this.x +" "+this.z); 
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

}
