package xyz.arturinsh.gameserver;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.hibernate.Session;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import xyz.arturinsh.database.MobSpawn;
import xyz.arturinsh.gameObjects.Mob;
import xyz.arturinsh.gameserver.Main.PlayerConnection;
import xyz.arturinsh.helpers.SessionFactoryUtil;
import xyz.arturinsh.packets.Packets.PlayerPositionUpdate;

public class GameWorld {
	private Server server;

	private List<Mob> mobs;
	private int[][] boundingMap;

	public GameWorld(Server server) {
		this.server = server;
		// this.dog = new Dog();
		initMobs();
		initMapBounds();
	}

	private void initMapBounds() {
		System.out.println("Init Bounding Map");
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File("src/main/java/MapBounds.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		boundingMap = new int[img.getHeight()][img.getWidth()];
		for (int i = 0; i < img.getHeight(); i++) {
			for (int j = 0; j < img.getWidth(); j++) {

				Color pixelColor = new Color(img.getRGB(j, i));
				if (pixelColor.getRed() == 255 && pixelColor.getGreen() == 255 && pixelColor.getBlue() == 255)
					boundingMap[j][i] = 0;
				else
					boundingMap[j][i] = 1;
			}
		}

		System.out.println("Bounding Map Initialized");
	}

	public void update() {

		// float nx = (float)(Math.sin(angle)*40);
		// float nz = (float)(Math.cos(angle)*40);
		//
		//
		// dog.update();
		// followCoords();
		// angle+=0.1;

		List<PlayerConnection> list = new ArrayList<PlayerConnection>();

		for (Connection con : server.getConnections()) {
			PlayerConnection player = (PlayerConnection) con;
			if (player.character != null) {
				list.add(player);
			}
		}

		for (Mob mob : mobs) {
			mob.update(list);
		}
	}

	public void playerUpdate(PlayerPositionUpdate update, PlayerConnection playerConnection) {
		if (update.character.charName.matches(playerConnection.character.charName)
				&& update.character.charClass == playerConnection.character.charClass) {
			if (playerConnection.lastTimeStamp == null
					|| playerConnection.lastTimeStamp.getTime() < update.timestamp.getTime()) {

				if (update.character.x < 0)
					playerConnection.character.x = 0;
				else if (update.character.x > 1024)
					playerConnection.character.x = 1024;
				else if (boundingMap[(int) update.character.x][(int) update.character.z] == 1)
					playerConnection.character.x = update.character.x;
				else
					playerConnection.character.x = playerConnection.character.x;

				if (update.character.z < 0)
					playerConnection.character.z = 0;
				else if (update.character.z > 1024)
					playerConnection.character.z = 1024;
				else if (boundingMap[(int) update.character.x][(int) update.character.z] == 1)
					playerConnection.character.z = update.character.z;
				else
					playerConnection.character.z = playerConnection.character.z;

				playerConnection.character.y = update.character.y;
				playerConnection.character.r = update.character.r;
				playerConnection.lastTimeStamp = update.timestamp;
			}
		}
	}

	private void followCoords() {
		if (server.getConnections().length > 0) {
			PlayerConnection player = (PlayerConnection) server.getConnections()[0];
			if (player.character != null) {
				float len = length(player.character.x, player.character.y, player.character.z, 0f, 0f, 0f);
				// System.out.println(len+" "+player.x+" "+player.z);
				// if (len < 20) {
				// dog.move(player.character.x, player.character.y,
				// player.character.z);
				// } else
				// dog.move(0, 0, 0);
			}
		}
	}

	private float length(float x, float y, float z, float nx, float ny, float nz) {
		return (float) Math.sqrt(Math.pow(nx - x, 2) + Math.pow(ny - y, 2) + Math.pow(nz - z, 2));
	}

	private void initMobs() {
		System.out.println("Init Mobs");
		mobs = new ArrayList<Mob>();
		Session session = SessionFactoryUtil.getSessionFactory().openSession();
		List<MobSpawn> list = session.createCriteria(MobSpawn.class).list();

		for (MobSpawn spawn : list) {
			Mob temp = new Mob(spawn.getX(), spawn.getY(), spawn.getZ());
			temp.type = spawn.getType();
			temp.Id = spawn.getId();
			mobs.add(temp);
			System.out.println(temp.Id + " " + temp.x + " " + temp.y + " " + temp.z + " " + temp.type);
		}
		System.out.println("Mobs Initialized");
	}

	public List<Mob> getMobs() {
		return mobs;
	}

}
