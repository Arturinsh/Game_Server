package xyz.arturinsh.gameserver;

import org.hibernate.Session;

import com.esotericsoftware.kryonet.Server;

import xyz.arturinsh.database.GameCharacter;
import xyz.arturinsh.database.User;
import xyz.arturinsh.gameObjects.CharacterClass;
import xyz.arturinsh.gameserver.Main.PlayerConnection;
import xyz.arturinsh.helpers.SessionFactoryUtil;
import xyz.arturinsh.packets.Packets.UserCharacter;

public class CharacterCreateBusiness {
	private Server server;

	public CharacterCreateBusiness(Server _server) {
		server = _server;
	}

	public void createCharacter(PlayerConnection playerConnection, UserCharacter newChar) {
		String charName = newChar.charName;
		CharacterClass charClass = newChar.charClass;
		GameCharacter ch = new GameCharacter();
		ch.setCharacterName(charName);
		ch.setCharClass(charClass);
		User user = playerConnection.user;
		ch.setUser(user);
		
		Session session = SessionFactoryUtil.getSessionFactory().openSession();
		session.beginTransaction();

		session.save(ch);
		session.getTransaction().commit();
	}
}
