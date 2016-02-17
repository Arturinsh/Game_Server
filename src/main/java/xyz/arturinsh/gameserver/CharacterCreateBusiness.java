package xyz.arturinsh.gameserver;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.esotericsoftware.kryonet.Server;

import xyz.arturinsh.database.GameCharacter;
import xyz.arturinsh.database.User;
import xyz.arturinsh.gameObjects.CharacterClass;
import xyz.arturinsh.gameserver.Main.PlayerConnection;
import xyz.arturinsh.helpers.SessionFactoryUtil;
import xyz.arturinsh.packets.Packets.CharacterCreateFailed;
import xyz.arturinsh.packets.Packets.CharacterCreateSuccess;
import xyz.arturinsh.packets.Packets.UserCharacter;

public class CharacterCreateBusiness {
	private Server server;

	public CharacterCreateBusiness(Server _server) {
		server = _server;
	}

	public void createCharacter(PlayerConnection playerConnection, UserCharacter newChar) {
		String charName = newChar.charName;

		Session session = SessionFactoryUtil.getSessionFactory().openSession();
		Query query = session.createQuery("From GameCharacter Where CharName =:name");
		query.setParameter("name", charName);
		List<GameCharacter> characters = query.list();
		if (characters.size() < 1) {
			CharacterClass charClass = newChar.charClass;
			GameCharacter ch = new GameCharacter();
			ch.setCharacterName(charName);
			ch.setCharClass(charClass);
			User user = playerConnection.user;
			ch.setUser(user);
			session.beginTransaction();
			session.save(ch);
			session.getTransaction().commit();
			
			Query usrQuery = session.createQuery("FROM User WHERE username =:name");
			usrQuery.setParameter("name", user.getUsername());
			
			User updatedUser = (User) usrQuery.list().get(0);
			playerConnection.user = updatedUser;
			
			CharacterCreateSuccess success = new CharacterCreateSuccess();
			success.characters = convertChars(updatedUser);
			playerConnection.sendTCP(success);
		} else {
			playerConnection.sendTCP(new CharacterCreateFailed());
		}
		session.close();
	}
	
	//TODO dublicate of method
	private List<UserCharacter> convertChars(User user) {
		List<UserCharacter> characters = new ArrayList<UserCharacter>();
		
		for (GameCharacter usrChar : user.getCharacters()) {
			UserCharacter newChar = new UserCharacter();
			newChar.charName = usrChar.getCharacterName();
			newChar.charClass = usrChar.getCharClass();
			characters.add(newChar);
		}
		return characters;
	}
}
