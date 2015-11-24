package xyz.arturinsh.gameserver;

import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import xyz.arturinsh.database.User;
import xyz.arturinsh.gameObjects.Player;
import xyz.arturinsh.gameserver.Main.PlayerConnection;
import xyz.arturinsh.packets.Packets.CreateCharacter;
import xyz.arturinsh.packets.Packets.LogIn;
import xyz.arturinsh.packets.Packets.Register;

public class NetworkListener extends Listener {
	private Server server;
	private List<User> loggedIn = new ArrayList<User>();

	public NetworkListener(Server _server) {
		server = _server;
	}

	@Override
	public void connected(Connection connection) {
	}

	@Override
	public void disconnected(Connection connection) {

	}

	@Override
	public void received(final Connection connection, final Object object) {
		final PlayerConnection playerConnection = (PlayerConnection) connection;
		playerConnection.addTask(new Runnable() {
			public void run() {
				if (object instanceof LogIn) {
					new LogRegBusiness(server, loggedIn).logIn(playerConnection, (LogIn) object);
				}

				if (object instanceof Register) {
					new LogRegBusiness(server, loggedIn).registerUser(playerConnection, (Register) object);
				}
				if (object instanceof CreateCharacter) {
					new CharacterCreateBusiness(server).createCharacter(playerConnection, (CreateCharacter) object);
				}
			}
		});
	}
}
