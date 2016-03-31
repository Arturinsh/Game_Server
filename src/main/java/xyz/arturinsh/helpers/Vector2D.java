package xyz.arturinsh.helpers;

public class Vector2D {
	private float x, y;

	public Vector2D(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float magnitude() {
		return (float) Math.sqrt(x * x + y * y);
	}

	public void setMagnitude(float value) {
		double current_angle = getAngle();
		this.x = (float) (value * Math.cos(current_angle));
		this.y = (float) (value * Math.sin(current_angle));
	}

	// returns radians
	public double getAngle() {
		double angle = Math.atan2(y, x);
		return angle;
	}

	public void setAngle(int angle) {
		float currentMagnitude = magnitude();
		double angleInRad = Math.toRadians(angle);
		this.x = (float) (currentMagnitude * Math.cos(angleInRad));
		this.y = (float) (currentMagnitude * Math.cos(angleInRad));
	}

	public Vector2D getNormR() {
		return new Vector2D(-1 * this.y, this.x);
	}

	public Vector2D getNormL() {
		return new Vector2D(this.y, -1 * this.x);
	}

	public Vector2D getUnitVector() {
		float currentMagnitude = magnitude();
		return new Vector2D(x / currentMagnitude, y / currentMagnitude);
	}

	public static Vector2D add(Vector2D a, Vector2D b) {
		return new Vector2D(a.x + b.x, a.y + b.y);
	}

	public static Vector2D minus(Vector2D a, Vector2D b) {
		return new Vector2D(a.x - b.x, a.y - b.y);
	}

	public static Vector2D rotate(Vector2D a, double angle) {
		Vector2D b = a.clone();
		b.rotate(angle);
		return b;
	}

	// returnds degrees
	public static float angleBetween(Vector2D a, Vector2D b) {
		Vector2D aUnitVector = a.getUnitVector();
		Vector2D bUnitVector = b.getUnitVector();
		return (float) Math.toDegrees(Math.acos(a.dotProduct(b)));
	}

	public static Vector2D interpolate(Vector2D a, float value) {
		return new Vector2D(a.x * value, a.y * value);
	}

	public Vector2D clone() {
		return new Vector2D(this.x, this.y);
	}

	// Should add trace function
	// For debugging

	public void scale(float factor) {
		this.x *= factor;
		this.y *= factor;
	}

	public void invert(String type) {
		if (type.charAt(0) == 'x')
			this.x *= -1;
		if (type.charAt(0) == 'y' || type.charAt(1) == 'y')
			this.y *= -1;
	}

	public void add(Vector2D b) {
		this.x += b.x;
		this.y += b.y;
	}

	public void minus(Vector2D b) {
		this.x -= b.x;
		this.y -= b.y;
	}

	public void rotate(double degrees) {
		double radians = Math.toRadians(degrees);
		this.x = (float) (this.x * Math.cos(radians) - this.y * Math.sin(radians));
		this.y = (float) (this.x * Math.sin(radians) + this.y * Math.cos(radians));
	}

	public float dotProduct(Vector2D b) {
		return this.x * b.x + this.y * b.y;
	}

	public float perpProduct(Vector2D b) {
		return this.y * b.x + this.x * -b.y;
	}

	public float crossProduct(Vector2D b) {
		return this.x * b.y + this.y * b.x;
	}

	public boolean equivalent(Vector2D b) {
		double diff = Math.pow(4, -10);
		return (this.x - b.x < diff && this.y - b.y < diff);
	}
}
