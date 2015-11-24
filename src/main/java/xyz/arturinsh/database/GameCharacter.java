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

}
