package xyz.arturinsh.packets;

import java.util.Date;
import java.util.List;

import xyz.arturinsh.gameObjects.CharacterClass;

public class Packets {
	public static class LogIn {
		public String userName;
		public String password;
	}

	public static class Register {
		public String userName;
		public String password;
	}

	public static class LogInSuccess {
		public List<UserCharacter> characters;
	}

	public static class RegisterSuccess {
	}

	public static class LogInFailed {
	}

	public static class RegisterFailed {
	}

	public static class AddPlayer {
		public UserCharacter character;
		public float x, y, z;
		// TODO add rotation// change to another class
	}

	public static class RemovePlayer {
		public UserCharacter character;
	}

	public static class UserCharacter {
		public String charName;
		public CharacterClass charClass;
	}

	public static class CharacterCreateSuccess {
		public List<UserCharacter> characters;
	}

	public static class CharacterCreateFailed {

	}

	public static class TestUDP {
		public String text;
	}

	public static class EnterWorld {
		public UserCharacter character;
	}

	public static class PositionUpdate {
		public UserCharacter character;
		public float x, y, z, r;
	}

	public static class PlayersSnapShot {
		public List<PositionUpdate> snapshot;
		public Date time;
	}
}
