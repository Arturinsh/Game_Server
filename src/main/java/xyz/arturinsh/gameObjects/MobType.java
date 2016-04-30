package xyz.arturinsh.gameObjects;

public enum MobType {
	VLADINATORS, DOG;

	public static MobType getInt(int x) {
		switch (x) {
		case 0:
			return MobType.VLADINATORS;
		case 1: 
			return MobType.DOG;
		default:
			return null;
		}
	}
}
