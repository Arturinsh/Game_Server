package xyz.arturinsh.gameserver;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import com.esotericsoftware.kryonet.Server;

import xyz.arturinsh.database.MobSpawn;
import xyz.arturinsh.gameObjects.Dog;
import xyz.arturinsh.gameObjects.Mob;
import xyz.arturinsh.gameserver.Main.PlayerConnection;
import xyz.arturinsh.helpers.SessionFactoryUtil;

public class GameWorld {
	private Server server;

	private Dog dog;

	private List<Mob> mobs;

	public GameWorld(Server server) {
		this.server = server;
		// this.dog = new Dog();
		initMobs();
	}

	public void update() {

		// float nx = (float)(Math.sin(angle)*40);
		// float nz = (float)(Math.cos(angle)*40);
		//
		//
		// dog.update();
		// followCoords();

		// angle+=0.1;
	}

	private void followCoords() {
		if (server.getConnections().length > 0) {
			PlayerConnection player = (PlayerConnection) server.getConnections()[0];
			if (player.character != null) {
				float len = length(player.character.x, player.character.y, player.character.z, 0f, 0f, 0f);
				// System.out.println(len+" "+player.x+" "+player.z);
				if (len < 20) {
					dog.move(player.character.x, player.character.y, player.character.z);
				} else
					dog.move(0, 0, 0);
			}
		}
	}

	private float length(float x, float y, float z, float nx, float ny, float nz) {
		return (float) Math.sqrt(Math.pow(nx - x, 2) + Math.pow(ny - y, 2) + Math.pow(nz - z, 2));
	}

	public Dog getDog() {
		return dog;
	}

	private void initMobs() {
		System.out.println("InitMobs");
		mobs = new ArrayList<Mob>();
		Session session = SessionFactoryUtil.getSessionFactory().openSession();
		List<MobSpawn> list = session.createCriteria(MobSpawn.class).list();

		for (MobSpawn spawn : list) {
			Mob temp = new Mob();
			temp.setPosition(spawn.getX(), spawn.getY(), spawn.getZ());
			temp.type = spawn.getType();
			temp.Id = spawn.getId();
			mobs.add(temp);
			System.out.println(temp.Id+" "+temp.x + " " + temp.y + " " + temp.z + " " + temp.type);
		}
		System.out.println("EndInit");
	}

	public List<Mob> getMobs() {
		return mobs;
	}

}
