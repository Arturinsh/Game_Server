package xyz.arturinsh.gameserver;

import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import xyz.arturinsh.database.User;
import xyz.arturinsh.gameserver.Main.PlayerConnection;
import xyz.arturinsh.packets.Packets.AddPlayer;
import xyz.arturinsh.packets.Packets.EnterWorld;
import xyz.arturinsh.packets.Packets.LogIn;
import xyz.arturinsh.packets.Packets.PositionUpdate;
import xyz.arturinsh.packets.Packets.Register;
import xyz.arturinsh.packets.Packets.RemovePlayer;
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
		PlayerConnection player = (PlayerConnection) connection;

		if (player.character != null) {
			RemovePlayer rmv = new RemovePlayer();
			rmv.character = player.character;
			server.sendToAllTCP(rmv);
		}

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
					// TestUDP test = (TestUDP) object;
					// System.out.println(test.text+"
					// "+playerConnection.getRemoteAddressUDP());
					// TestUDP test2 = new TestUDP();
					// test2.text = test.text;
					// playerConnection.sendUDP(test2);
				}
				if (object instanceof EnterWorld) {
					EnterWorld enterW = (EnterWorld) object;

					playerConnection.x = 0;
					playerConnection.y = 0;
					playerConnection.z = 0;
					playerConnection.character = enterW.character;

					AddPlayer ply = new AddPlayer();
					ply.character = enterW.character;
					ply.x = 0;
					ply.y = 0;
					ply.z = 0;
					sendToAllExceptHim(ply, connection);
					System.out.println("enterWorld");
				}
				if (object instanceof PositionUpdate) {
					PositionUpdate update = (PositionUpdate) object;
					if (update.character.charName.matches(playerConnection.character.charName)
							&& update.character.charClass == playerConnection.character.charClass) {
						playerConnection.x = update.x;
						playerConnection.y = update.y;
						playerConnection.z = update.z;
						playerConnection.r = update.r;
					}
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
