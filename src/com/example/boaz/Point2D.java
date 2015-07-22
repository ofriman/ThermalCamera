package com.example.boaz;


/** this class represents a 2D point, might be represented as a pixel or sub pixel 
 * */ 

public class Point2D {
// ***** private data *****
    private double _x, _y;
    // ****** constructors ******
    public Point2D (double x1, double y1)   {_x = x1;   _y = y1;}
    public static final double _epsilon = 0.01f;
    
    /** copy constructor */
    public Point2D (Point2D p) { _x = p._x; _y = p._y;}


    public double OldWeight;
    // ***** public methods *****
    public double x() {return _x;}
    public double y() {return _y;}
    
    public int ix() {return (int)_x;}
    public int iy() {return (int)_y;}

    /** dirty walk a round the lock!! */
	public void set(Point2D p) {
		_x=p._x;_y=p._y;
	}
	public void offset(Point2D p) { offset(p._x,p._y);}
	public void set(double x, double y) {
		_x=x;_y=y;
	}
	public void offset(double dx, double dy) {
		_x+=dx;_y+=dy;
	}
	public void setX(double x) {_x=x;}
	public void setY(double y) {_y=y;}
	
     /** logical equals
     * @param p othe Point.
     * @return true iff this is logicly equals to p) */
   public boolean equals (Point2D p)
    {return p._x == _x && p._y == _y;}
   
    
    public boolean equalsIntXY (Point2D p)
    {return p.ix() == (int) _x && p.iy() ==(int) _y;}
    
    
    public boolean smallerXY(Point2D p) {
	if(_x<p._x ||(_x==p._x && _y<p._y)) return true;
	else return false;
    }

    public boolean close2equalsXY(Point2D p)
    {return Math.abs(p._x- _x) < _epsilon && Math.abs(p._y -_y) <_epsilon;}
    /** key is [x y z] use for hashtables */
    
    public String key() {return _x+" "+_y;}
    
    /** translate this by the Point p (as it was a vector from 0,0)
     * <br> NOTE! this method return a new Point3D.*/
     public void move (double dx,double dy) {
 		_x+=dx; _y+=dy;}
   /** translate this by the Point p (as it was a vector from 0,0)
    * <br> NOTE! this method return a new Point3D.*/
    public Point2D translate (Point2D p) {
		if(p==null) return null;
		return new Point2D(_x+p._x, _y+p._y);}    
    /** @return the 2D square distance - for efficiency */
    public double dist2 (Point2D p)
    {
    double dx = _x-p._x;
    double dy = _y-p._y;
	return dx*dx+dy*dy;
    }
  
    /** @return the L2 3D distance */
    public double distance (Point2D p)
    {
    double temp = Math.pow (p._x - _x, 2) + Math.pow (p._y - _y, 2);
	return (double)Math.sqrt (temp);
    }
   /** @return the L2 2D distance (x,y) */
    public double distance2D (Point2D p)
    {
	double temp = Math.pow (p._x - _x, 2) + Math.pow (p._y - _y, 2);
	return (double)Math.sqrt (temp);
    }
    public String toString() {return "[" + (int)_x + "," + (int)_y+"]";}
    public String toString(boolean all) {
	if(all) return "[" + _x + "," +_y+"]";
	else return "[" + (int)_x + "," + (int)_y+"]";
    } 
    public String toFile()  {return _x+" "+_y+" ";}
    public String toFile1()  {return "Point3D "+_x+" "+_y;}

    ////////////////////////////////////////////////////////////////////////////////////////

public final static int ONSEGMENT = 0,  LEFT = 1, RIGHT = 2, INFRONTOFA = 3, BEHINDB = 4, ERROR = 5;
public final static int DOWN = 6, UP = 7;

/** return up iff this point is above the SEGMENT (not the line) */
    public int pointLineTest2(Point2D a, Point2D b) {
    	int flag = this.pointLineTest(a,b);
    	if(a._x < b._x ) {
    		if(a._x<=_x && b._x>_x) {
    			if (flag == LEFT) return DOWN;
    			if (flag == RIGHT) return UP;
    		}
    	}
    	else 
    	if(a._x > b._x ) {
    		if(b._x<=_x && a._x>_x) {
    			if (flag == RIGHT) return DOWN;
    			if (flag == LEFT) return UP;
    		}
    	}	
    	return flag;
	}



    public int pointLineTest(Point2D a, Point2D b) {

	if(a== null || b==null || a.equals(b)) return ERROR;

	double dx = b._x-a._x;
	double dy = b._y-a._y;
	double res = dy*(_x-a._x)-dx*(_y-a._y);

	if (res < 0) return LEFT;
	if (res > 0) return RIGHT;
	
	if (dx > 0) {
	    if (_x < a._x) return INFRONTOFA;
	    if (b._x < _x) return BEHINDB;
	    return ONSEGMENT;
	}
	if (dx < 0) {
	    if (_x > a._x) return INFRONTOFA;
	    if (b._x > _x) return BEHINDB;
	    return ONSEGMENT;
	}
	if (dy > 0) {
	    if (_y < a._y) return INFRONTOFA;
	    if (b._y < _y) return BEHINDB;
	    return ONSEGMENT;
	}
	if (dy < 0) {
	    if (_y > a._y) return INFRONTOFA;
	    if (b._y > _y) return BEHINDB;
	    return ONSEGMENT;
	}
	return ERROR;
    }
	
	
	///////////////////////////// NEW METHODS ///////////////////////////////////
	public void rescale(Point2D center, Point2D vec) {
		if(center!=null && vec != null)
			rescale(center,vec.x(),vec.y());
	}
	
	public void rescale(Point2D center, double size) {
		if(center!=null && size>0)
			rescale(center,size,size);
	}
	private void rescale(Point2D center, double sizeX,double sizeY) {
		_x = center._x + ((_x - center._x) * sizeX);
		_y = center._y + ((_y - center._y) * sizeY);
	} 
	
	public void rotate2D(Point2D center, double angle) {
 	// angle -1/2PI .. 1/2Piregular degrees. 
		_x = _x - center.x();
		_y = _y - center.y();
		double a = Math.atan2(_y,_x);
	//	System.out.println("Angle: "+a);
		double radius = Math.sqrt((_x*_x) + (_y*_y));
		_x = (double)(center.x() +  radius * Math.cos(a+angle));
		_y = (double)(center.y() +  radius * Math.sin(a+angle));
	}								
	/** computes the angleXY between p1 and p2 in RADIANS: <br><br>
	up:(PI/2)  , down (-PI/2) , right(0),  left(+- PI).   [-PI, +PI]	*/
	public double angleXY(Point2D p) {
		if(p==null) throw new RuntimeException("** Error: Point3D angle got null **");
		return Math.atan2((p._y-_y), (p._x-_x));
	}
	/** computes the angleXY between p1 and p2 in RADIANS: <br><br>
	up:(PI/2)  , down (1.5PI) , right(0),  left(PI).   [0,2PI].	*/
	public double angleXY_2PI(Point2D p) {
		if(p==null) throw new RuntimeException("** Error: Point3D angle got null **");
		double ans = Math.atan2((p._y-_y), (p._x-_x));
		if (ans<0) ans = 2*Math.PI+ans;
		return ans;
	}
	
/** return the (planer angle of the vector between this --> p, in DEGREES, in a
 * compass order: north 0, east 90, south 180, west 270.
 * @param p is the end point of the vector (z value is ignored). 
 * @return angle in compass styye [0,360).
 */
	public double north_angle(Point2D p) {
		double ans = 0;
		double a_rad = Math.atan2((p._y-_y), (p._x-_x));
		double a_deg = Math.toDegrees(a_rad);
		if(a_deg<=90) ans = 90-a_deg;
		else ans = 450-a_deg;
		return ans;
	}

	/** transform from radians to angles */
	public double r2d_old(double a) { return (a*180.0) / Math.PI;}
	/** transform from radians to angles */
	public static double r2d(double a) { return Math.toDegrees(a);}
	/** transform from radians to angles */
	public static double d2r(double a) { return Math.toRadians(a);}
	
	
	////////////////////////////////////////////////////////////////////////////////
	
}  // class Point3D


