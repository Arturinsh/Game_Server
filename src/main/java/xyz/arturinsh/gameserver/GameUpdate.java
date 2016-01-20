package xyz.arturinsh.gameserver;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import xyz.arturinsh.gameserver.Main.PlayerConnection;
import xyz.arturinsh.packets.Packets.PlayersSnapShot;
import xyz.arturinsh.packets.Packets.PositionUpdate;

public class GameUpdate extends TimerTask {

	private Server server;

	public GameUpdate(Server _server) {
		server = _server;
	}

	@Override
	public void run() {
		// TestUDP test = new TestUDP();
		// DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		// Date date = new Date();
		// test.text = "works " + dateFormat.format(date);
		// server.sendToAllUDP(test);
		createSnapshot();
	}

	private void createSnapshot() {
		List<PlayerConnection> characters = new ArrayList<PlayerConnection>();
		for (Connection con : server.getConnections()) {
			PlayerConnection player = (PlayerConnection) con;
			if (player.character != null)
				characters.add(player);
		}
		PlayersSnapShot snapShot = new PlayersSnapShot();
		snapShot.snapshot = new ArrayList<PositionUpdate>();

		for (PlayerConnection player : characters) {
			PositionUpdate update = new PositionUpdate();
			update.character = player.character;
			update.x = player.x;
			update.y = player.y;
			update.z = player.z;
			update.qx = player.qx;
			update.qy = player.qy;
			update.qz = player.qz;
			update.qw = player.qw;
			snapShot.snapshot.add(update);
		}
		//server.sendToAllUDP(snapShot);
		
		for(PlayerConnection player : characters){
			player.sendUDP(snapShot);
		}
	}


}
