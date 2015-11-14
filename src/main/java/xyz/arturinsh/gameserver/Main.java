package xyz.arturinsh.gameserver;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.Session;

import com.esotericsoftware.kryonet.Server;

import xyz.arturinsh.database.User;
import xyz.arturinsh.helpers.SessionFactoryUtil;

public class Main {
	public static void main(String args[]) {
		System.out.println("Server started");
		setLoggersToLogWarining();
		testHibernate();
		System.out.println("The end");
	}

	private static void testKryo() {
		Server server = new Server();
		server.start();
		try {
			server.bind(54555, 54777);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void setLoggersToLogWarining() {
		Logger log = Logger.getLogger("org.hibernate");
		log.setLevel(Level.WARNING);
		System.setProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "WARNING");
		System.setProperty("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
	}

	private static void testHibernate() {
		Session session = SessionFactoryUtil.getSessionFactory().openSession();
		User test = new User();
		test.setPassword("testdsdf");
		test.setUsername("kaka");

		session.beginTransaction();

		session.save(test);

		session.getTransaction().commit();
		session.close();
	}

}
