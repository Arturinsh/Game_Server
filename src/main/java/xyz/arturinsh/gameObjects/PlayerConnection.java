package xyz.arturinsh.gameObjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.esotericsoftware.kryonet.Connection;

import xyz.arturinsh.database.User;
import xyz.arturinsh.helpers.BoundingBox;
import xyz.arturinsh.helpers.Point;
import xyz.arturinsh.packets.Packets.UserCharacter;

public class PlayerConnection extends Connection {

	private final float ATTACK_CENTER_DISTANCE = 3;

	public User user;
	public UserCharacter character;
	public Date lastTimeStamp;
	private ExecutorService tasks = Executors.newSingleThreadExecutor();
	private List<Date> attackTimes = new ArrayList<Date>();

	private boolean attacking = false;

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
		System.out.println(character.charName + " attack count=" + attackTimes.size());
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
	
	public void attackEnded(){
		attacking = false;
	}

	public void addTask(Runnable newTask) {
		tasks.execute(newTask);
	}
}