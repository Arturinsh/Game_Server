package xyz.arturinsh.helpers;

import java.util.ArrayList;

public class BoundingBox {

	private Point center;
	private ArrayList<Point> points = new ArrayList<Point>();

	public BoundingBox(Point _center, Point... _points) {
		this.center = _center;

		for (Point point : _points) {
			points.add(point);
		}
	}

	public ArrayList<Point> getPoints() {
		return points;
	}

	public void addAngle(int angle) {
		for (Point point : points) {
			double radians = Math.toRadians(angle);
			float xLength = point.x - center.x;
			float yLength = point.y - center.y;

			point.x = (float) (xLength * Math.cos(radians) - yLength * Math.sin(radians));
			point.y = (float) (xLength * Math.sin(radians) + yLength * Math.cos(radians));

			point.x += center.x;
			point.y += center.y;
		}
	}
}
