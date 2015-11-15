package xyz.arturinsh.gameserver;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import xyz.arturinsh.database.User;
import xyz.arturinsh.gameObjects.Player;
import xyz.arturinsh.gameserver.Main.PlayerConnection;
import xyz.arturinsh.helpers.SessionFactoryUtil;
import xyz.arturinsh.packets.Packets.LogIn;
import xyz.arturinsh.packets.Packets.LogInFailed;
import xyz.arturinsh.packets.Packets.LogInSuccess;
import xyz.arturinsh.packets.Packets.Register;

public class NetworkListener extends Listener {
	private Server server;
	private List<Player> loggedIn = new ArrayList<Player>();

	public NetworkListener(Server _server) {
		server = _server;
	}

	@Override
	public void connected(Connection connection) {
	}

	@Override
	public void disconnected(Connection connection) {

	}

	public void received(Connection connection, Object object) {
		PlayerConnection playerConnection = (PlayerConnection) connection;
		if (object instanceof LogIn) {
			logIn(playerConnection, (LogIn) object);
		}

		if (object instanceof Register) {
			Register temp = (Register) object;
			System.out.println(temp.userName);
			System.out.println(temp.password);
		}
	}

	private void logIn(PlayerConnection playerConnection, LogIn login) {
		if (canLogIn(playerConnection, login)) {
			Player nplayer = new Player();
			playerConnection.sendTCP(new LogInSuccess());
		}
	}

	private boolean canLogIn(PlayerConnection playerConnection, LogIn login) {
		Session session = SessionFactoryUtil.getSessionFactory().openSession();
		Query query = session.createQuery("FROM User WHERE username =:name");
		query.setParameter("name", login.userName);

		List<User> users = query.list();

		session.close();
		if (users.size() > 0) {
			if (users.get(0).getPassword().matches(login.password)) {
				Player temp = new Player();
				temp.username = login.userName;
				if (isInLoggedIn(login)) {
					Connection[] conList = server.getConnections();
					for (Connection con : conList) {
						PlayerConnection test = (PlayerConnection) con;
						if (test.player != null) {
							if (test.player.username.matches(login.userName)) {
								con.close();
							}
						}
					}
				}
				return true;
			}
		}
		LogInFailed fail = new LogInFailed();
		playerConnection.sendTCP(fail);
		return false;
	}

	private boolean isInLoggedIn(LogIn login) {
		for (Player player : loggedIn) {
			if (player.username.matches(login.userName))
				return true;
		}
		return false;
	}
}
