package xyz.arturinsh.gameObjects;

public enum MobType {
	DOG;

	public static MobType getInt(int x) {
		switch (x) {
		case 0:
			return MobType.DOG;
		default:
			return null;
		}
	}
}
