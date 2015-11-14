package xyz.arturinsh.gameserver;

import java.io.File;
import java.io.IOException;

import org.hibernate.Session;

import com.esotericsoftware.kryonet.Server;

import xyz.arturinsh.database.User;
import xyz.arturinsh.helpers.SessionFactoryUtil;

public class Main {
	public static void main(String args[]) {
		//testKryo();
		System.out.println("Hello World");
//		testHibernate();
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

	private static void testHibernate() {
		Session session = SessionFactoryUtil.getSessionFactory().openSession();
		User test = new User();
		test.setPassword("t");
		test.setUsername("kaka");
		
		session.beginTransaction();
		
		session.save(test);
		
		session.getTransaction().commit();
		session.close();
	}

}
