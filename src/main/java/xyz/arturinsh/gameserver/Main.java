package xyz.arturinsh.gameserver;

import java.io.IOException;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import xyz.arturinsh.gameObjects.CharacterClass;
import xyz.arturinsh.gameObjects.MobType;
import xyz.arturinsh.gameObjects.PlayerConnection;
import xyz.arturinsh.packets.Packets.AddPlayer;
import xyz.arturinsh.packets.Packets.Attack;
import xyz.arturinsh.packets.Packets.AttackStarted;
import xyz.arturinsh.packets.Packets.CharacterCreateFailed;
import xyz.arturinsh.packets.Packets.CharacterCreateSuccess;
import xyz.arturinsh.packets.Packets.EnterWorld;
import xyz.arturinsh.packets.Packets.LogIn;
import xyz.arturinsh.packets.Packets.LogInFailed;
import xyz.arturinsh.packets.Packets.LogInSuccess;
import xyz.arturinsh.packets.Packets.LogOut;
import xyz.arturinsh.packets.Packets.MobAttack;
import xyz.arturinsh.packets.Packets.MobUpdate;
import xyz.arturinsh.packets.Packets.PlayerPositionUpdate;
import xyz.arturinsh.packets.Packets.Register;
import xyz.arturinsh.packets.Packets.RegisterFailed;
import xyz.arturinsh.packets.Packets.RegisterSuccess;
import xyz.arturinsh.packets.Packets.RemovePlayer;
import xyz.arturinsh.packets.Packets.ServerMessage;
import xyz.arturinsh.packets.Packets.SnapShot;
import xyz.arturinsh.packets.Packets.SwitchCharacter;
import xyz.arturinsh.packets.Packets.UserCharacter;

public class Main {
	private static Server server;
	
	final static int PORT1 = 2300;
	final static int PORT2 = 54777;
	
	public static void main(String args[]) {
		System.out.println("Server started");
		setLoggersToLogWarning();
		server = new Server() {
			protected Connection newConnection() {
				return new PlayerConnection();
			}
		};
		registerKryo();
		GameWorld world = new GameWorld(server);
		server.addListener(new NetworkListener(server, world));
		server.start();
		try {
			server.bind(PORT1, PORT2);
		} catch (IOException e) {
			System.out.print(e);
		}
		Timer timer = new Timer();
		timer.schedule(new GameUpdate(server, world), 0, 50);
	}

	private static void registerKryo() {
		Kryo kryo = server.getKryo();
		kryo.register(java.util.ArrayList.class);
		kryo.register(java.util.Date.class);
		kryo.register(LogIn.class);
		kryo.register(Register.class);
		kryo.register(LogInSuccess.class);
		kryo.register(RegisterSuccess.class);
		kryo.register(LogInFailed.class);
		kryo.register(RegisterFailed.class);
		kryo.register(AddPlayer.class);
		kryo.register(RemovePlayer.class);
		kryo.register(CharacterClass.class);
		kryo.register(UserCharacter.class);
		kryo.register(CharacterCreateSuccess.class);
		kryo.register(CharacterCreateFailed.class);
		kryo.register(EnterWorld.class);
		kryo.register(PlayerPositionUpdate.class);
		kryo.register(MobType.class);
		kryo.register(MobUpdate.class);
		kryo.register(SnapShot.class);
		kryo.register(Attack.class);
		kryo.register(AttackStarted.class);
		kryo.register(MobAttack.class);
		kryo.register(ServerMessage.class);
		kryo.register(LogOut.class);
		kryo.register(SwitchCharacter.class);
	}

	private static void setLoggersToLogWarning() {
		Logger log = Logger.getLogger("org.hibernate");
		log.setLevel(Level.WARNING);
		System.setProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "WARNING");
		System.setProperty("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
	}

}
