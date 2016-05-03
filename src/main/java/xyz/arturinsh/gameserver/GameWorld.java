package xyz.arturinsh.gameserver;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.hibernate.Session;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import xyz.arturinsh.database.MobSpawn;
import xyz.arturinsh.gameObjects.Mob;
import xyz.arturinsh.gameObjects.PlayerConnection;
import xyz.arturinsh.helpers.BoundingBox;
import xyz.arturinsh.helpers.Point;
import xyz.arturinsh.helpers.SessionFactoryUtil;
import xyz.arturinsh.helpers.Vector2D;
import xyz.arturinsh.packets.Packets.Attack;
import xyz.arturinsh.packets.Packets.AttackStarted;
import xyz.arturinsh.packets.Packets.MobAttack;
import xyz.arturinsh.packets.Packets.PlayerPositionUpdate;

public class GameWorld {
	private final float MAXT_TRAVEL_DISTANCE = 3;

	private Server server;

	private List<Mob> mobs;
	private int[][] boundingMap;

	private long tick = 0;

	private static List<PlayerConnection> playersInWorld = new ArrayList<PlayerConnection>();;

	public GameWorld(Server server) {
		this.server = server;
		initMobs();
		initMapBounds();
	}

	private void initMapBounds() {
		System.out.println("Init Bounding Map");
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File("res/MapBounds.png"));
		} catch (IOException e) {
			System.out.println("Failed to initialize bounding map");
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

		updatePlayerWorldList();

		for (Mob mob : mobs) {
			mob.update(playersInWorld);
		}

	}

	private void updatePlayerWorldList() {

		List<PlayerConnection> list = new ArrayList<PlayerConnection>();

		for (Connection con : server.getConnections()) {
			PlayerConnection player = (PlayerConnection) con;
			if (player.character != null) {
				list.add(player);
			}
		}

		playersInWorld = list;
	}

	private boolean checkBounding(BoundingBox box1, BoundingBox box2) {

		ArrayList<Vector2D> normals_box1 = box1.getNorm();
		ArrayList<Vector2D> normals_box2 = box2.getNorm();

		ArrayList<Vector2D> vecs_box1 = prepareVector(box1);
		ArrayList<Vector2D> vecs_box2 = prepareVector(box2);

		boolean isSeperated = false;

		for (int i = 0; i < normals_box1.size(); i++) {
			MinMax result_box1 = getMinMax(vecs_box1, normals_box1.get(i));
			MinMax result_box2 = getMinMax(vecs_box2, normals_box1.get(i));

			isSeperated = result_box1.maxProj < result_box2.minProj || result_box2.maxProj < result_box1.minProj;
			if (isSeperated)
				break;
		}
		if (!isSeperated) {
			for (int i = 0; i < normals_box2.size(); i++) {
				MinMax result_P1 = getMinMax(vecs_box1, normals_box2.get(i));
				MinMax result_P2 = getMinMax(vecs_box2, normals_box2.get(i));

				isSeperated = result_P1.maxProj < result_P2.minProj || result_P2.maxProj < result_P1.minProj;
				// System.out.println(result_P1.maxProj+
				// "<"+result_P2.minProj
				// +"||"+result_P2.maxProj+"<"+result_P1.minProj);
				if (isSeperated)
					break;
			}
		}

		return !isSeperated;
	}

	private ArrayList<Vector2D> prepareVector(BoundingBox box) {
		ArrayList<Vector2D> vecs_box = new ArrayList<Vector2D>();

		for (int i = 0; i < box.getPoints().size(); i++) {
			Point corner_box = box.getPoints().get(i);
			vecs_box.add(new Vector2D(corner_box.x, corner_box.y));
		}

		return vecs_box;
	}

	private class MinMax {
		public float minProj, maxProj;
		public int minIndex, maxIndex;
	}

	private MinMax getMinMax(ArrayList<Vector2D> vecs_box, Vector2D axis) {
		float min_proj_box = vecs_box.get(1).dotProduct(axis);
		int min_dot_box = 1;
		float max_proj_box = vecs_box.get(1).dotProduct(axis);
		int max_dot_box = 1;

		for (int i = 2; i < vecs_box.size(); i++) {
			float curr_proj = vecs_box.get(i).dotProduct(axis);

			if (min_proj_box > curr_proj) {
				min_proj_box = curr_proj;
				min_dot_box = i;
			}
			if (curr_proj > max_proj_box) {
				max_proj_box = curr_proj;
				max_dot_box = i;
			}

		}

		MinMax result = new MinMax();
		result.minProj = min_proj_box;
		result.maxProj = max_proj_box;
		result.minIndex = min_dot_box;
		result.maxIndex = max_dot_box;

		return result;
	}

	public void playerUpdate(PlayerPositionUpdate update, PlayerConnection playerConnection) {
		if (update.character.charName.matches(playerConnection.character.charName)
				&& update.character.charClass == playerConnection.character.charClass) {
			if (playerConnection.lastTimeStamp == null
					|| playerConnection.lastTimeStamp.getTime() < update.timestamp.getTime()) {
				float travelDistance = length(playerConnection.character.x, playerConnection.character.y,
						playerConnection.character.z, update.character.x, update.character.y, update.character.z);
				if (playerConnection.character.hp > 0 && travelDistance <= MAXT_TRAVEL_DISTANCE) {
					if (update.character.x < 0)
						update.character.x = 0;
					else if (update.character.x > 1024)
						update.character.x = 1024;

					if (update.character.z < 0)
						update.character.z = 0;
					else if (update.character.z > 1024)
						update.character.z = 1024;

					if (boundingMap[(int) update.character.x][(int) update.character.z] == 1)
						playerConnection.character.x = update.character.x;
					else
						playerConnection.character.x = playerConnection.character.x;

					if (boundingMap[(int) update.character.x][(int) update.character.z] == 1)
						playerConnection.character.z = update.character.z;
					else
						playerConnection.character.z = playerConnection.character.z;

					playerConnection.character.y = update.character.y;
					playerConnection.character.r = update.character.r;
				}
				playerConnection.lastTimeStamp = update.timestamp;
				playerConnection.tick++;
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
			Mob temp = new Mob(spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getType(), this);
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

	public void attack(final Attack attack, final PlayerConnection playerConnection) {
		if (attack.character.charName.matches(playerConnection.character.charName)
				&& attack.character.charClass == playerConnection.character.charClass
				&& playerConnection.character.hp > 0) {

			if (playerConnection.isNewAttack(attack.time)) {
				AttackStarted startAttack = new AttackStarted();
				startAttack.character = attack.character;

				updatePlayerWorldList();
				for (PlayerConnection player : playersInWorld) {
					player.sendTCP(startAttack);
				}
			}
			new java.util.Timer().schedule(new java.util.TimerTask() {
				@Override
				public void run() {
					executeAttack(attack, playerConnection);
				}
			}, 1050);
		}
	}

	private void executeAttack(Attack attack, PlayerConnection playerConnection) {
		if (playersInWorld.size() > 1) {
			for (PlayerConnection player : playersInWorld) {
				if (!player.character.charName.matches(playerConnection.character.charName)) {
					if (player.character.hp > 0)
						if (checkBounding(playerConnection.getAttackBox(), player.getBoundingBox())) {
							player.character.hp -= playerConnection.attack;
//							System.out.println(
//									player.character.charName + " attacked by " + playerConnection.character.charName);
						}
				}
			}
		}
		for (Mob mob : mobs) {
			if (mob.isAlive())
				if (checkBounding(playerConnection.getAttackBox(), mob.getBoundingBox())) {
					mob.receiveAttack(playerConnection);
//					System.out.println("mob " + mob.Id + " attacked by " + playerConnection.character.charName);
				}
		}
		playerConnection.attackEnded();
	}

	public void mobAttack(final Mob mob) {
		MobAttack attack = new MobAttack();
		attack.mob = mob.getMobUpdateData();
		for (PlayerConnection player : playersInWorld) {
			player.sendTCP(attack);
		}
		new java.util.Timer().schedule(new java.util.TimerTask() {
			@Override
			public void run() {
				executeMobAttack(mob);
			}
		}, mob.attackTime);
	}

	private void executeMobAttack(Mob mob) {
		if (playersInWorld.size() > 0) {
			for (PlayerConnection player : playersInWorld) {
				if (checkBounding(mob.getAttackBox(), player.getBoundingBox())) {
					if (player.character.hp > 0) {
						player.character.hp -= mob.attack;
						System.out.println(player.character.charName + " attacked by mob " + mob.Id);
					}
				}
			}
		}
		mob.attackEnded();
	}

	private void printTime() {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSSS");
		Date date = new Date();
		System.out.println(dateFormat.format(date));
	}

	public void addTick() {
		this.tick = tick + 1;
	}

	public long getTick() {
		return tick;
	}
}
