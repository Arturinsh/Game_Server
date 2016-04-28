package xyz.arturinsh.gameserver;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import xyz.arturinsh.gameObjects.Mob;
import xyz.arturinsh.gameObjects.PlayerConnection;
import xyz.arturinsh.packets.Packets.MobUpdate;
import xyz.arturinsh.packets.Packets.PlayerPositionUpdate;
import xyz.arturinsh.packets.Packets.SnapShot;

public class GameUpdate extends TimerTask {

	private Server server;
	private GameWorld world;
	private List<Mob> mobs;

	public GameUpdate(Server _server, GameWorld world) {
		server = _server;
		this.world = world;
		mobs = world.getMobs();
	}

	@Override
	public void run() {
		world.update();
		createSnapshot();
	}

	private void createSnapshot() {
		List<PlayerConnection> characters = new ArrayList<PlayerConnection>();
		for (Connection con : server.getConnections()) {
			PlayerConnection player = (PlayerConnection) con;
			if (player.character != null)
				characters.add(player);
		}
		SnapShot snapShot = new SnapShot();
		snapShot.snapshot = new ArrayList<PlayerPositionUpdate>();

		for (PlayerConnection player : characters) {
			PlayerPositionUpdate update = new PlayerPositionUpdate();
			update.character = player.character;
			// if (player.character.x < 0)
			// update.character.x = 0;
			// else
			update.character.x = player.character.x;
			update.character.y = player.character.y;
			update.character.z = player.character.z;
			update.character.r = player.character.r;
			update.timestamp = player.lastTimeStamp;
			update.tick = player.tick;
			snapShot.snapshot.add(update);
		}

		snapShot.mobSnapshot = new ArrayList<MobUpdate>();
		for (Mob mob : mobs) {
				snapShot.mobSnapshot.add(mob.getMobUpdateData());
		}

		snapShot.time = new Date();
		snapShot.tick = world.getTick();
		for (PlayerConnection player : characters) {
			player.sendTCP(snapShot);
		}
		world.addTick();
	}

}
