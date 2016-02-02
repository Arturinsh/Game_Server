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
		dog.update();
		followCoords();

		// angle+=0.1;
	}

	private void followCoords() {
		if (server.getConnections().length > 0) {
			PlayerConnection player = (PlayerConnection) server.getConnections()[0];
			float len = length(player.x, player.y, player.z, 0f, 0f, 0f);
//			System.out.println(len+" "+player.x+" "+player.z);
			if (len < 20) {
				dog.move(player.x, player.y, player.z);
			} else
				dog.move(0, 0, 0);
		}
	}

	private float length(float x, float y, float z, float nx, float ny, float nz) {
		return (float) Math.sqrt(Math.pow(nx - x, 2) + Math.pow(ny - y, 2) + Math.pow(nz - z, 2));
	}   

	public Dog getDog() {
		return dog;
	}

}
