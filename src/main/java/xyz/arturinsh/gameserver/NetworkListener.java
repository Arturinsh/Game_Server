package xyz.arturinsh.gameserver;

import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import xyz.arturinsh.database.User;
import xyz.arturinsh.gameserver.Main.PlayerConnection;
import xyz.arturinsh.packets.Packets.EnterWorld;
import xyz.arturinsh.packets.Packets.LogIn;
import xyz.arturinsh.packets.Packets.PlayerPositionUpdate;
import xyz.arturinsh.packets.Packets.Register;
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
		new GameWorldBusiness(server).removeCharacter(player);
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

				if (object instanceof EnterWorld) {
					new GameWorldBusiness(server).addCharacter(playerConnection, (EnterWorld) object);
				}

				if (object instanceof PlayerPositionUpdate) {
					PlayerPositionUpdate update = (PlayerPositionUpdate) object;
					if (update.character.charName.matches(playerConnection.character.charName)
							&& update.character.charClass == playerConnection.character.charClass) {
						playerConnection.character.x = update.character.x;
						playerConnection.character.y = update.character.y;
						playerConnection.character.z = update.character.z;
						playerConnection.character.r = update.character.r;
						playerConnection.lastTimeStamp = update.timestamp;
//						System.out.println(playerConnection.character.x + " " + playerConnection.character.z + " "
//								+ playerConnection.character.r);
					}
				}
			}
		});
	}
}
