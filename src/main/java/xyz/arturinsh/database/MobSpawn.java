package xyz.arturinsh.database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
	
	@ManyToOne
	@JoinColumn(name = "MobId")
	private MobType MobType;
	
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
