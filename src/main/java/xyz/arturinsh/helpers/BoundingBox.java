package xyz.arturinsh.helpers;

import java.util.ArrayList;

public class BoundingBox {

	private Point center;
	private ArrayList<Point> points = new ArrayList<Point>();

	public BoundingBox(Point _center, Point... _points) {
		this.center = _center;
		points.add(center);
		for (Point point : _points) {
			Point tempPoint = new Point();
			tempPoint.x = center.x + point.x;
			tempPoint.y = center.y + point.y;
			points.add(tempPoint);
		}
	}

	public ArrayList<Point> getPoints() {
		return points;
	}

	public void addAngle(int angle) {
		double radians = Math.toRadians(angle);
		for (Point point : points) {
			float xLength = point.x - center.x;
			float yLength = point.y - center.y;

			point.x = (float) (xLength * Math.cos(radians) - yLength * Math.sin(radians));
			point.y = (float) (xLength * Math.sin(radians) + yLength * Math.cos(radians));

			point.x += center.x;
			point.y += center.y;
		}
	}

	public ArrayList<Vector2D> getNorm() {
		ArrayList<Vector2D> normals = new ArrayList<Vector2D>();
		for (int i = 1; i < points.size() - 1; i++) {
			Vector2D currentNormal = new Vector2D(points.get(i + 1).x - points.get(i).x,
					points.get(i + 1).y - points.get(i).y).getNormL();
			normals.add(currentNormal);
		}
		normals.add(new Vector2D(points.get(1).x - points.get(points.size() - 1).x,
				points.get(1).y - points.get(points.size() - 1).y).getNormL());
		return normals;
	}
}
