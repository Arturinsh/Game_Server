package xyz.arturinsh.gameserver;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import xyz.arturinsh.database.GameCharacter;
import xyz.arturinsh.database.User;
import xyz.arturinsh.gameObjects.PlayerConnection;
import xyz.arturinsh.helpers.SessionFactoryUtil;
import xyz.arturinsh.packets.Packets.LogIn;
import xyz.arturinsh.packets.Packets.LogInFailed;
import xyz.arturinsh.packets.Packets.LogInSuccess;
import xyz.arturinsh.packets.Packets.Register;
import xyz.arturinsh.packets.Packets.RegisterFailed;
import xyz.arturinsh.packets.Packets.RegisterSuccess;
import xyz.arturinsh.packets.Packets.UserCharacter;

public class LogRegBusiness {
	private Server server;

	public LogRegBusiness(Server _server) {
		server = _server;
	}

	public void logIn(PlayerConnection playerConnection, LogIn login) {
		User user = canLogIn(playerConnection, login);
		if (user != null) {
			playerConnection.user = user;
			LogInSuccess loginSces = new LogInSuccess();
			loginSces.characters = convertChars(user);
			playerConnection.sendTCP(loginSces);

			System.out.println(user.getUsername() + " logged into server.");
		}
	}

	private User canLogIn(PlayerConnection playerConnection, LogIn login) {
		Session session = SessionFactoryUtil.getSessionFactory().openSession();

		Query query = session.createQuery("FROM User WHERE username =:name");
		query.setParameter("name", login.userName);

		List<User> users = query.list();

		if (userNameOk(login.userName) && pswOk(login.password) && users.size() > 0) {
			if (users.get(0).getPassword().matches(login.password)) {
				Connection[] conList = server.getConnections();
				for (Connection con : conList) {
					PlayerConnection test = (PlayerConnection) con;
					if (test.user != null) {
						if (test.user.getUsername().matches(login.userName)) {
							con.close();
						}
					}
				}
				User loginUser = users.get(0);
				loginUser.getCharacters().size();
				session.close();
				return loginUser;
			}
		}
		LogInFailed fail = new LogInFailed();
		playerConnection.sendTCP(fail);
		session.close();
		return null;
	}

	public void registerUser(PlayerConnection playerConnection, Register newUser) {
		Session session = SessionFactoryUtil.getSessionFactory().openSession();
		Query query = session.createQuery("FROM User WHERE username =:name");
		query.setParameter("name", newUser.userName);
		List<User> users = query.list();
		if (userNameOk(newUser.userName) && pswOk(newUser.password) && users.size() < 1) {
			User test = new User();
			// TODO generate hash, check password and username regex
			test.setPassword(newUser.password);
			test.setUsername(newUser.userName);
			session.beginTransaction();

			session.save(test);
			session.getTransaction().commit();
			playerConnection.sendTCP(new RegisterSuccess());
		} else {
			RegisterFailed fail = new RegisterFailed();
			playerConnection.sendTCP(fail);
		}
		session.close();
	}

	private boolean userNameOk(String username) {
		if (username.length() >= 5 && username.length() <= 32 && username.matches("[a-zA-Z0-9]*"))
			return true;
		return false;
	}

	private boolean pswOk(String psw) {
		if (psw.length() >= 5 && psw.length() <= 32 && psw.matches("[a-zA-Z0-9_.@]*"))
			return true;
		return false;
	}

	// TODO dublicate of method
	public List<UserCharacter> convertChars(User user) {
		List<UserCharacter> characters = new ArrayList<UserCharacter>();

		for (GameCharacter usrChar : user.getCharacters()) {
			UserCharacter newChar = new UserCharacter();
			newChar.charName = usrChar.getCharacterName();
			newChar.charClass = usrChar.getCharClass();
			newChar.experience = usrChar.getExperience();
			newChar.hp = usrChar.getHP();
			characters.add(newChar);
		}
		return characters;
	}
}
