package xyz.arturinsh.packets;

import java.util.Date;
import java.util.List;

import xyz.arturinsh.gameObjects.CharacterClass;
import xyz.arturinsh.gameObjects.MobType;

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
	}

	public static class RemovePlayer {
		public UserCharacter character;
	}

	public static class UserCharacter {
		public String charName;
		public CharacterClass charClass;
		public float x, y, z, r;
	}

	public static class CharacterCreateSuccess {
		public List<UserCharacter> characters;
	}

	public static class CharacterCreateFailed {

	}

	public static class EnterWorld {
		public UserCharacter character;
	}

	public static class PlayerPositionUpdate {
		public UserCharacter character;
	}

	public static class MobUpdate{
		public long ID;
		public float x,y,z,r;
		public MobType type;
	}

	public static class SnapShot {
		public List<MobUpdate> mobSnapshot;
		public List<PlayerPositionUpdate> snapshot;
		public Date time;
	}
}
