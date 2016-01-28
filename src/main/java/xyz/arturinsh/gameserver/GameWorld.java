package xyz.arturinsh.gameserver;

import com.esotericsoftware.kryonet.Server;

import xyz.arturinsh.gameObjects.Dog;
import xyz.arturinsh.gameserver.Main.PlayerConnection;

public class GameWorld {
	private Server server;

	private Dog dog;

	public GameWorld(Server server) {
		this.server = server;
		this.dog = new Dog();
		dog.move(100, 0, 100);
	}

	public void update() {

		// float nx = (float)(Math.sin(angle)*40);
		// float nz = (float)(Math.cos(angle)*40);
		//
		//
		followCoords();
		dog.update();
		// angle+=0.1;
	}

	private void followCoords() {
		if (server.getConnections().length > 1) {
			PlayerConnection player = (PlayerConnection) server.getConnections()[1];
			dog.move(player.x, player.y, player.z);
		}
	}
	
	public Dog getDog() {
		return dog;
	}

}
