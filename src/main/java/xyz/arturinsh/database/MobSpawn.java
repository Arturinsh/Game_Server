package xyz.arturinsh.database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import xyz.arturinsh.gameObjects.MobType;

@Entity
@Table(name = "MobSpawn")
public class MobSpawn {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SpawnId")
	private long Id;
	
	@Column(name = "X")
	private float X;
	
	@Column(name = "Y")
	private float Y;
	
	@Column(name = "Z")
	private float Z;
	
	@Column(name ="Type")
	private MobType mobType;
	
	public float getX() {
		return X;
	}

	public float getY() {
		return Y;
	}

	public float getZ() {
		return Z;
	}
	
	
}
