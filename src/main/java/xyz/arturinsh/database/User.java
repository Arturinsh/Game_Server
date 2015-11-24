package xyz.arturinsh.database;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="UserId")
	private long Id;
	
	@Column(name="Username")
	private String Username;
	
	@Column(name="Password")
	private String Password;

	@OneToMany(mappedBy="User")
	private Set<Character> Characters;
	
	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}

	public String getUsername() {
		return Username;
	}

	public void setUsername(String username) {
		Username = username;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	public Set<Character> getCharacters() {
		return Characters;
	}

	public void setCharacters(Set<Character> characters) {
		Characters = characters;
	}
}
