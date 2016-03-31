package xyz.arturinsh.gameObjects;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.esotericsoftware.kryonet.Connection;

import xyz.arturinsh.database.User;
import xyz.arturinsh.helpers.BoundingBox;
import xyz.arturinsh.helpers.Point;
import xyz.arturinsh.packets.Packets.UserCharacter;

public class PlayerConnection extends Connection {

	public User user;
	public UserCharacter character;
	public Date lastTimeStamp;
	private ExecutorService tasks = Executors.newSingleThreadExecutor();

	public BoundingBox getBoundingBox() {
		Point center = new Point(character.x, character.z);
		BoundingBox box = new BoundingBox(center, new Point(10, 0), new Point(-10, 0), new Point(0, -10), new Point(0, 10));
		box.addAngle((int)character.r);
		return box;
	}

	public void addTask(Runnable newTask) {
		tasks.execute(newTask);
	}
}