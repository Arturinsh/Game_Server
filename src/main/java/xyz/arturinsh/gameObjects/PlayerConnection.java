package xyz.arturinsh.gameObjects;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.esotericsoftware.kryonet.Connection;

import xyz.arturinsh.database.User;
import xyz.arturinsh.packets.Packets.UserCharacter;

public class PlayerConnection extends Connection {

	public User user;
	public UserCharacter character;
	public Date lastTimeStamp ;
	private ExecutorService tasks = Executors.newSingleThreadExecutor();

	public void addTask(Runnable newTask) {
		tasks.execute(newTask);
	}
}