package xyz.arturinsh.database;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "MobType")
public class MobType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MobId")
	private long Id;

	@Column(name = "MoveSpeed")
	private float MoveSpeed;

	@Column(name = "Name")
	private String Name;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "MobType")
	private Set<MobSpawn> Spawns;
	
	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}

	public float getMoveSpeed() {
		return MoveSpeed;
	}

	public void setMoveSpeed(float moveSpeed) {
		MoveSpeed = moveSpeed;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}
}
