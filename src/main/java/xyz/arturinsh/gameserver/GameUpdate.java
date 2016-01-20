package xyz.arturinsh.gameserver;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

import com.esotericsoftware.kryonet.Server;

import xyz.arturinsh.packets.Packets.TestUDP;

public class GameUpdate extends TimerTask {

	private Server server;

	public GameUpdate(Server _server) {
		server = _server;
	}

	@Override
	public void run() {
		TestUDP test = new TestUDP();
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		test.text = "works "+dateFormat.format(date);
		server.sendToAllUDP(test);
	}

}
