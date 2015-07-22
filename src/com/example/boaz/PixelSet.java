package com.example.boaz;
/**
 * this class represents an ordered (i.e. Queue) set of pixel (int x, int y)
 * @author boaz
 *
 */
public class PixelSet {
	public static final int INIT_SIZE = 10000;
	private int _start=0, _end;//, _size=0;
	private int[] _xx, _yy;
	private int[] _pix ; // return value - avoid (new);
	

	public PixelSet() {this(INIT_SIZE);}
	public PixelSet(int size) {
		_end=0;
		_start=0;
		_xx = new int[size];
		_yy = new int[size];
		_pix = new int[2];
	}
	/**
	 * "remove" all pixels from queue.
	 */
	public void clear() {
		_start = 0;
		_end=0;
	}
	public int size() {return _end-_start;}
	public void add(int x, int y) {
		if(isFull()) {
			int size = Math.max(INIT_SIZE, size()*2);
			rescale(size);
		}
		_xx[_end] = x;
		_yy[_end] = y;
		_end++;
	}
	public boolean isEmpty() {return size()==0;}
	public int[] removeFirst() {
		if(size()==0) throw new RuntimeException("ERR: removing an element (pixel) from an empty Queue - check first");
		_pix[0] = _xx[_start];
		_pix[1] = _yy[_start];
		_start++;
		return _pix;
	}
	public int[] removeFirstUnsafe() {
		//if(size()==0) throw new RuntimeException("ERR: removing an element (pixel) from an empty Queue - check first");
		_pix[0] = _xx[_start];
		_pix[1] = _yy[_start];
		_start++;
		return _pix;
	}
	private boolean isFull() {
		return _end==_xx.length;
	}
	private void rescale(int size) {
		PixelSet ot = new PixelSet(size);
		int t=0;
		while(!isEmpty()) {
			int[] pix = this.removeFirst();
			ot.add(pix[0], pix[1]);
		}
		this._xx = ot._xx;
		this._yy = ot._yy;
		this._end = ot._end;
		this._start = ot._start;
	}
}
