package xyz.arturinsh.gameObjects;

public enum CharacterClass {
	RED, GREEN, BLUE;

	public static CharacterClass getInt(int x) {
		switch (x) {
		case 0:
			return CharacterClass.RED;
		case 1:
			return CharacterClass.GREEN;
		case 2:
			return CharacterClass.BLUE;
		default:
			return null;
		}
	}
}
