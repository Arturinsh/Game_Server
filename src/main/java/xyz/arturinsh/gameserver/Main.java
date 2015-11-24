package xyz.arturinsh.gameserver;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import xyz.arturinsh.database.User;
import xyz.arturinsh.gameObjects.CharacterClass;
import xyz.arturinsh.packets.Packets.AddPlayer;
import xyz.arturinsh.packets.Packets.CharacterCreateFailed;
import xyz.arturinsh.packets.Packets.CharacterCreateSuccess;
import xyz.arturinsh.packets.Packets.CreateCharacter;
import xyz.arturinsh.packets.Packets.LogIn;
import xyz.arturinsh.packets.Packets.LogInFailed;
import xyz.arturinsh.packets.Packets.LogInSuccess;
import xyz.arturinsh.packets.Packets.Register;
import xyz.arturinsh.packets.Packets.RegisterFailed;
import xyz.arturinsh.packets.Packets.RegisterSuccess;
import xyz.arturinsh.packets.Packets.RemovePlayer;

public class Main {
	private static Server server;

	public static void main(String args[]) {
		System.out.println("Server started");
		setLoggersToLogWarning();
		server = new Server() {
			protected Connection newConnection() {
				return new PlayerConnection();
			}
		};
		registerKryo();
		server.addListener(new NetworkListener(server));
		server.start();
		try {
			server.bind(54555, 54777);
		} catch (IOException e) {
			System.out.print(e);
		}
	}

	private static void registerKryo() {
		Kryo kryo = server.getKryo();
		kryo.register(LogIn.class);
		kryo.register(Register.class);
		kryo.register(LogInSuccess.class);
		kryo.register(RegisterSuccess.class);
		kryo.register(LogInFailed.class);
		kryo.register(RegisterFailed.class);
		kryo.register(AddPlayer.class);
		kryo.register(RemovePlayer.class);
		kryo.register(CharacterClass.class);
		kryo.register(CreateCharacter.class);
		kryo.register(CharacterCreateSuccess.class);
		kryo.register(CharacterCreateFailed.class);
	}

	private static void setLoggersToLogWarning() {
		Logger log = Logger.getLogger("org.hibernate");
		log.setLevel(Level.WARNING);
		System.setProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "WARNING");
		System.setProperty("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
	}

	static class PlayerConnection extends Connection {
		
		public User user;
		private ExecutorService tasks = Executors.newSingleThreadExecutor();
		
		public void addTask(Runnable newTask) {
			tasks.execute(newTask);
		}
	}
}
