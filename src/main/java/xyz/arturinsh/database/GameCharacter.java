package xyz.arturinsh.database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import xyz.arturinsh.gameObjects.CharacterClass;

@Entity
@Table(name = "Characters")
public class GameCharacter {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CharId")
	private long Id;

	@Column(name = "CharName")
	private String CharacterName;

	@ManyToOne
	@JoinColumn(name = "UserId")
	private User User;

	@Column(name = "Class")
	private CharacterClass charClass;

	@Column(name = "X")
	private float X = 0;

	@Column(name = "Y")
	private float Y = 0;

	@Column(name = "Z")
	private float Z = 0;

	@Column(name = "R")
	private float R = 0;

	@Column(name = "Experience")
	private int Experience = 0;
	
	@Column(name ="HP")
	private int HP = 0;

	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}

	public String getCharacterName() {
		return CharacterName;
	}

	public void setCharacterName(String characterName) {
		CharacterName = characterName;
	}

	public User getUser() {
		return User;
	}

	public void setUser(User user) {
		User = user;
	}

	public CharacterClass getCharClass() {
		return charClass;
	}

	public void setCharClass(CharacterClass charClass) {
		this.charClass = charClass;
	}

	public float getX() {
		return X;
	}

	public float getY() {
		return Y;
	}

	public float getZ() {
		return Z;
	}

	public float getR() {
		return R;
	}

	public void setPosRot(float x, float y, float z, float r) {
		X = x;
		Y = y;
		Z = z;
		R = r;
	}

	public int getExperience() {
		return Experience;
	}

	public void setExperience(int experience) {
		Experience = experience;
	}

	public int getHP() {
		return HP;
	}

	public void setHP(int hP) {
		HP = hP;
	}

}
