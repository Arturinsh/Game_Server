package xyz.arturinsh.gameserver;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import xyz.arturinsh.gameObjects.PlayerConnection;
import xyz.arturinsh.packets.Packets.Attack;
import xyz.arturinsh.packets.Packets.EnterWorld;
import xyz.arturinsh.packets.Packets.LogIn;
import xyz.arturinsh.packets.Packets.LogInSuccess;
import xyz.arturinsh.packets.Packets.LogOut;
import xyz.arturinsh.packets.Packets.PlayerPositionUpdate;
import xyz.arturinsh.packets.Packets.Register;
import xyz.arturinsh.packets.Packets.SwitchCharacter;
import xyz.arturinsh.packets.Packets.UserCharacter;

public class NetworkListener extends Listener {
	private Server server;
	private GameWorld world;

	public NetworkListener(Server _server, GameWorld _world) {
		server = _server;
		world = _world;
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
					new LogRegBusiness(server).logIn(playerConnection, (LogIn) object);
				}

				if (object instanceof Register) {
					new LogRegBusiness(server).registerUser(playerConnection, (Register) object);
				}

				if (object instanceof UserCharacter) {
					new CharacterCreateBusiness(server).createCharacter(playerConnection, (UserCharacter) object);
				}

				if (object instanceof EnterWorld) {
					new GameWorldBusiness(server).addCharacter(playerConnection, (EnterWorld) object);
				}

				if (object instanceof PlayerPositionUpdate) {
					PlayerPositionUpdate update = (PlayerPositionUpdate) object;
					world.playerUpdate(update, playerConnection);
				}
				if (object instanceof Attack) {
					final Attack attack = (Attack) object;
					world.attack(attack, playerConnection);
				}
				if (object instanceof LogOut) {
					new GameWorldBusiness(server).removeCharacter(playerConnection);
					playerConnection.logOut();
				}
				if (object instanceof SwitchCharacter) {
					LogInSuccess loginSces = new LogInSuccess();
					loginSces.characters = new LogRegBusiness(server).convertChars(playerConnection.user);
					new GameWorldBusiness(server).removeCharacter(playerConnection);
					playerConnection.siwtchCharacter();
					playerConnection.sendTCP(loginSces);
				}
			}
		});
	}

	private void printTime() {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSSS");
		Date date = new Date();
		System.out.println(dateFormat.format(date));
	}
}
