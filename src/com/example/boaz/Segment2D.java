package com.example.boaz;
/**
 * this class represents a simple 2D segment - can be seen as a skeleton of a rectangular light.
 * @author boaz
 *
 */
public class Segment2D {
	private Point2D _p1;
	private Point2D _p2;
	
	public Segment2D(Point2D a, Point2D b) {
		this(a,b, false);
	}
	public Segment2D(Point2D a, Point2D b, boolean deep_copy) {
		if(deep_copy) {
			a = new Point2D(a);
			b = new Point2D(b);
		}
		_p1 = a;
		_p2 = b;
	}
	public Segment2D(Segment2D ot) {
		this(ot._p1, ot._p2, true);
	}
	public double dist() {
		return _p1.distance(_p2);
	}
	public double angle() {
		return _p1.angleXY(_p2);
	}
	public Point2D p1() {return _p1;}
	public Point2D p2() {return _p2;}
	
}
