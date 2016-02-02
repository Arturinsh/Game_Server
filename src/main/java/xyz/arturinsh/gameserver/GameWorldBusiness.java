package xyz.arturinsh.gameserver;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import xyz.arturinsh.gameserver.Main.PlayerConnection;
import xyz.arturinsh.packets.Packets.AddPlayer;
import xyz.arturinsh.packets.Packets.EnterWorld;

public class GameWorldBusiness {
	private Server server;

	public GameWorldBusiness(Server _server) {
		this.server = _server;
	}

	public void addCharacter(PlayerConnection player, EnterWorld enter) {
		player.x = 0;
		player.y = 0;
		player.z = 0;
		player.character = enter.character;
		
		AddPlayer ply = new AddPlayer();
		ply.character = enter.character;
		ply.character.x = 0;
		ply.character.y = 0;
		ply.character.z = 0;
		ply.character.r = 0;
		
		sendToAllExceptHim(ply, player);
	}

	private void sendToAllExceptHim(Object object, Connection connection) {
		for (Connection con : server.getConnections()) {
			if (con != connection)
				con.sendTCP(object);
		}
	}
}
