package com.example.boaz;

public class Image_light {
	private int _num_of_pix;
	private int _num_of_edge_pix;
	private Point2D _cen;
	private int _id;
	private double _radius;
	private boolean _in_center;
	private double _fatness;
	
	private Segment2D _skeleton;
	
	
	public Image_light(int[][] image, int x, int y) {
		/// to do
		
	}
	public Image_light(int num_pix, int num_edges, Point2D cen, double rad, boolean in_cen, double fat){
		this.set_num_of_pix(num_pix);
		this.set_num_of_edge_pix(num_edges);
		this.set_radius(rad);
		this.set_cen(cen);
		this.set_in_center(in_cen);
		this.set_fatness(fat);
		
	}
	public int get_num_of_pix() {
		return _num_of_pix;
	}
	public void set_num_of_pix(int _num_of_pix) {
		this._num_of_pix = _num_of_pix;
	}
	public int get_num_of_edge_pix() {
		return _num_of_edge_pix;
	}
	public void set_num_of_edge_pix(int _num_of_edge_pix) {
		this._num_of_edge_pix = _num_of_edge_pix;
	}
	public Point2D get_cen() {
		return _cen;
	}
	public void set_cen(Point2D c) {
		_cen = new Point2D(c);
	}
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public double get_radius() {
		return _radius;
	}
	public void set_radius(double rad) {
		this._radius = rad;
	}
	public boolean is_in_center() {
		return _in_center;
	}
	public void set_in_center(boolean _in_center) {
		this._in_center = _in_center;
	}
	public double get_fatness() {
		return _fatness;
	}
	public void set_fatness(double _fatness) {
		this._fatness = _fatness;
	}
	public Segment2D get_skeleton() {
		return _skeleton;
	}
	public void set_skeleton(Segment2D _skeleton) {
		this._skeleton = _skeleton;
	}
	public String toString() {
		String ans = "";
		ans +="Image Light: ID: "+this.get_id()+" , pix= "+this.get_num_of_pix()+" , edge= "+this.get_num_of_edge_pix();
		ans +=" , radius= "+this.get_radius()+" , fat= "+this.get_fatness()+"  cen= "+this.get_cen();
		if(!this._in_center) {ans+=" Border";}

		return ans;
	}
}
