package xyz.arturinsh.gameserver;

import com.esotericsoftware.kryonet.Server;

import xyz.arturinsh.gameObjects.Dog;

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
		//dog.move(move, 0, move);
		dog.update();
		// angle+=0.1;
	}

	private void followCoords(float x, float z) {
	
		
	}

	public Dog getDog() {
		return dog;
	}

}
