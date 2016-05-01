package xyz.arturinsh.gameObjects;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.esotericsoftware.kryonet.Connection;

import xyz.arturinsh.database.User;
import xyz.arturinsh.helpers.BoundingBox;
import xyz.arturinsh.helpers.Point;
import xyz.arturinsh.packets.Packets.ServerMessage;
import xyz.arturinsh.packets.Packets.UserCharacter;

public class PlayerConnection extends Connection {

	private final float ATTACK_CENTER_DISTANCE = 3;
	private final int DEFAULT_HP = 100;

	public User user;
	public UserCharacter character;
	public Date lastTimeStamp;
	public long tick;
	public int attack = 20;
	private ExecutorService tasks = Executors.newSingleThreadExecutor();
	private List<Date> attackTimes = new ArrayList<Date>();
	private boolean attacking = false, dead = false;
	private Calendar nextSpawnTime;

	public void logOut() {
		user = null;
		character = null;
		lastTimeStamp = null;
		tick = 0;
		attackTimes = new ArrayList<Date>();
		attacking = false;
		dead = false;
		nextSpawnTime = null;
	}

	public void siwtchCharacter() {
		character = null;
		lastTimeStamp = null;
		tick = 0;
		attackTimes = new ArrayList<Date>();
		attacking = false;
		dead = false;
		nextSpawnTime = null;
	}

	public BoundingBox getBoundingBox() {
		Point center = new Point(character.x, character.z);
		BoundingBox box = new BoundingBox(center, new Point(2, 1), new Point(2, -1), new Point(-2, -1),
				new Point(-2, 1));
		box.addAngle((int) character.r);
		return box;
	}

	public BoundingBox getAttackBox() {

		float rotToRadians = (float) Math.toRadians(character.r);
		float xOffset = (float) (ATTACK_CENTER_DISTANCE * Math.sin(rotToRadians));
		float zOffset = (float) (ATTACK_CENTER_DISTANCE * Math.cos(rotToRadians));

		float nx = character.x + xOffset;
		float nz = character.z + zOffset;

		Point center = new Point(nx, nz);
		BoundingBox box = new BoundingBox(center, new Point(2, 2), new Point(2, -2), new Point(-2, -2),
				new Point(-2, 2));
		box.addAngle((int) character.r);
		return box;
	}

	private void addAttack(Date attackDate) {
		attackTimes.add(attackDate);
		attacking = true;
		if (attackTimes.size() > 100) {
			for (int i = 0; i < 50; i++) {
				attackTimes.remove(0);
			}
		}
	}

	public boolean isNewAttack(Date attackDate) {
		if (!attacking) {
			for (Date date : attackTimes) {
				if (date.getTime() == attackDate.getTime())
					return false;
			}
			addAttack(attackDate);
			return true;
		}
		return false;
	}

	public void kill() {
		dead = true;
		nextSpawnTime = Calendar.getInstance();
		nextSpawnTime.add(Calendar.SECOND, 5);
		ServerMessage message = new ServerMessage();
		message.message = "You will be revived in few seconds!";
		this.sendTCP(message);
	}

	public void attackEnded() {
		attacking = false;
	}

	public void addTask(Runnable newTask) {
		tasks.execute(newTask);
	}

	public void update() {
		if (character.hp <= 0 && !dead)
			kill();

		if (dead && Calendar.getInstance().getTimeInMillis() > nextSpawnTime.getTimeInMillis()) {
			reset();
		}
	}

	public void reset() {
		character.x = 200;
		character.z = 200;
		character.y = 0;
		character.hp = DEFAULT_HP;
		dead = false;
	}
}