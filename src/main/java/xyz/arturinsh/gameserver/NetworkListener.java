package xyz.arturinsh.gameserver;

import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import xyz.arturinsh.database.User;
import xyz.arturinsh.gameObjects.CharacterClass;
import xyz.arturinsh.gameserver.Main.PlayerConnection;
import xyz.arturinsh.packets.Packets.AddPlayer;
import xyz.arturinsh.packets.Packets.EnterWorld;
import xyz.arturinsh.packets.Packets.LogIn;
import xyz.arturinsh.packets.Packets.Register;
import xyz.arturinsh.packets.Packets.TestUDP;
import xyz.arturinsh.packets.Packets.UserCharacter;

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
				if (object instanceof UserCharacter) {
					new CharacterCreateBusiness(server).createCharacter(playerConnection, (UserCharacter) object);
				}
				if (object instanceof TestUDP) {
					 TestUDP test = (TestUDP) object;
					 System.out.println(test.text+" "+playerConnection.getRemoteAddressUDP());
					// TestUDP test2 = new TestUDP();
					// test2.text = test.text;
					// playerConnection.sendUDP(test2);
				}
				if (object instanceof EnterWorld) {
					AddPlayer ply = new AddPlayer();
					ply.charClass = CharacterClass.GREEN;
					ply.username = "IDK";
					ply.x = 0;
					ply.y = 0;
					ply.z = 0;
					sendToAllExceptHim(ply, connection);
					System.out.println("enterWorld");
				}
			}
		});
	}

	private void sendToAllExceptHim(Object object, Connection connection) {
		for (Connection con : server.getConnections()) {
			if (con != connection)
				con.sendTCP(object);
		}

	}
}
