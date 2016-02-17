package xyz.arturinsh.gameserver;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import xyz.arturinsh.database.GameCharacter;
import xyz.arturinsh.gameserver.Main.PlayerConnection;
import xyz.arturinsh.helpers.SessionFactoryUtil;
import xyz.arturinsh.packets.Packets.AddPlayer;
import xyz.arturinsh.packets.Packets.EnterWorld;
import xyz.arturinsh.packets.Packets.RemovePlayer;
import xyz.arturinsh.packets.Packets.UserCharacter;

public class GameWorldBusiness {
	private Server server;

	public GameWorldBusiness(Server _server) {
		this.server = _server;
	}

	public void addCharacter(PlayerConnection player, EnterWorld enter) {

		Session session = SessionFactoryUtil.getSessionFactory().openSession();
		GameCharacter character = getChar(session, enter.character);

		if (character != null) {
			enter.character.x = character.getX();
			enter.character.y = character.getY();
			enter.character.z = character.getZ();
			enter.character.r = character.getR();

			player.character = enter.character;

			EnterWorld success = new EnterWorld();
			success.character = player.character;
			player.sendTCP(success);

			AddPlayer ply = new AddPlayer();
			ply.character = enter.character;

			sendToAllExceptHim(ply, player);
		}
		session.close();
	}

	public void removeCharacter(PlayerConnection player) {
		if (player.character != null) {
			Session session = SessionFactoryUtil.getSessionFactory().openSession();

			GameCharacter dcChar = getChar(session, player.character);

			if (dcChar != null) {
				dcChar.setPosRot(player.character.x, player.character.y, player.character.z, player.character.r);

				session.beginTransaction();
				session.update(dcChar);
				session.getTransaction().commit();
			}

			session.close();
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

	private GameCharacter getChar(Session session, UserCharacter character) {

		Criteria criteria = session.createCriteria(GameCharacter.class);

		GameCharacter cc = (GameCharacter) criteria.add(Restrictions.eq("CharacterName", character.charName))
				.uniqueResult();
		if (cc != null)
			return cc;
		else
			return null;
	}

}
