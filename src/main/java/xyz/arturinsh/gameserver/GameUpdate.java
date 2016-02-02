package xyz.arturinsh.gameserver;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import xyz.arturinsh.gameserver.Main.PlayerConnection;
import xyz.arturinsh.packets.Packets.DogPositionUpdate;
import xyz.arturinsh.packets.Packets.PlayerPositionUpdate;
import xyz.arturinsh.packets.Packets.SnapShot;

public class GameUpdate extends TimerTask {

	private Server server;
	private GameWorld world;

	public GameUpdate(Server _server, GameWorld world) {
		server = _server;
		this.world = world;
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
			update.character.x = player.character.x;
			update.character.y = player.character.y;
			update.character.z = player.character.z;
			update.character.r = player.character.r;
			snapShot.snapshot.add(update);
		}
		
		snapShot.dogSnapshot = new ArrayList<DogPositionUpdate>();
		
		DogPositionUpdate dogPos= world.getDog().getDogPosUpdate();
		
		snapShot.dogSnapshot.add(dogPos);
		
		//server.sendToAllUDP(snapShot);
		snapShot.time = new Date();
		for(PlayerConnection player : characters){
			player.sendUDP(snapShot);
		}
	}


}
