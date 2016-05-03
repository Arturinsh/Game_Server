package xyz.arturinsh.gameObjects;

public enum MobType {
	VLADINATORS, DOG, CROCO;

	public static MobType getInt(int x) {
		switch (x) {
		case 0:
			return MobType.VLADINATORS;
		case 1: 
			return MobType.DOG;
		case 2: 
			return MobType.CROCO;
		default:
			return null;
		}
	}
}
