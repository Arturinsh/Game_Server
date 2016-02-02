package xyz.arturinsh.gameserver;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import xyz.arturinsh.gameserver.Main.PlayerConnection;
import xyz.arturinsh.packets.Packets.AddPlayer;
import xyz.arturinsh.packets.Packets.EnterWorld;
import xyz.arturinsh.packets.Packets.RemovePlayer;

public class GameWorldBusiness {
	private Server server;

	public GameWorldBusiness(Server _server) {
		this.server = _server;
	}

	public void addCharacter(PlayerConnection player, EnterWorld enter) {
		player.character = enter.character;
		
		EnterWorld success = new EnterWorld();
		success.character = player.character;
		player.sendTCP(success);
		
		
		AddPlayer ply = new AddPlayer();
		ply.character = enter.character;
		ply.character.x = 0;
		ply.character.y = 0;
		ply.character.z = 0;
		ply.character.r = 0;

		sendToAllExceptHim(ply, player);
	}

	public void removeCharacter(PlayerConnection player) {
		if (player.character != null) {
			RemovePlayer rmv = new RemovePlayer();
			rmv.character = player.character;
			server.sendToAllTCP(rmv);
		}
	}

	private void sendToAllExceptHim(Object object, Connection connection) {
		for (Connection con : server.getConnections()) {
			if (con != connection)
				con.sendTCP(object);
		}
	}

}
