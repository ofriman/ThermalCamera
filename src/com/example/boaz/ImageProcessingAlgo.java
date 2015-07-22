package com.example.boaz;
import java.util.ArrayList;

/**
 * this class implements a set of low level Image / matrix algorithms including:
 * 1. binary image --> connected compounds
 * 2. edge detection 
 * 3. binary image --> all lights
 * @author boaz
 *
 */

public class ImageProcessingAlgo {
	public static final int Ni4 = 0, Ni8 = 1, BLACK=0, WHITE=1, GRAY=2, MAX=256*256;
	public static final int NUM_OF_PIX = 1, NUM_OF_EDGE = 2, CEN_X=3, CEN_Y=4, ON_BORDER=5, RADIUS=6;

	public static double NIM_RADIUS_NEON = 4;
	public static double NAX_FATNESS_NEON = 0.5;
	
	public static int Ni_flag = Ni4; //Ni8 is not implemented! - currently seems to be no so needed.
	public static PixelSet _front = new PixelSet(); 
	
	/**
	 * 
	 */
	public static ArrayList<Image_light> Image2Lights(int[][] image) {
		int size = BinaryImage2Lights(image);
		ArrayList<Image_light> ans = CC_Image2List(image, size);
		return ans;
	}
	/**
	 * this function performs the main connected compounds algorithm over an image - resulting
	 * with a modified image with a color for each light (CC).
	 * Note this function changes the image it gets as a function - consider duplicating if needed.
	 * @param image
	 * @return the number of connected compounds (colors)
	 */
	public static int BinaryImage2Lights(int[][] image) {
		int ans = 0;
		int x= image.length;
		int y = image[0].length;
		int first_c = GRAY+1;
		int color = first_c;
		for(int xi=0;xi<x;xi++) {
			for(int yi=0;yi<y;yi++) {
				if(image[xi][yi]==WHITE) {
					int size = fillColor(image, xi, yi, color);
					color++;
				}
				
			}
		}
		return color-first_c;
	}
	private static int fillColor(int[][] image, int xi, int yi, int color) {
		int ans = 0;
		if(image[xi][yi]==WHITE) {
			_front.clear(); // clear the BFS queue
			_front.add(xi, yi);
			image[xi][yi] = color;
			while(!_front.isEmpty()) {
				int[] pix = _front.removeFirst();
				int x = pix[0];
				int y = pix[1];
				if(x-1>=0) {
					if(image[x-1][y]==WHITE) {
						_front.add(x-1, y);
						image[x-1][y] = color;
					}
					if(image[x-1][y]==BLACK) {image[x][y] = color+MAX; // edge
					}
				}
				if(y-1>=0) {
						if(image[x][y-1]==WHITE) {
							_front.add(x, y-1);
							image[x][y-1] = color;
						}
						if(image[x][y-1]==BLACK) {image[x][y] = color+MAX; // edge
						}
					}
				if(y+1<image[0].length) {
					if(image[x][y+1]==WHITE) {
						_front.add(x, y+1);
						image[x][y+1] = color;
					}
					if(image[x][y+1]==BLACK) {image[x][y] = color+MAX; // edge
					}
				}
				if(x+1<image.length) {
					if(image[x+1][y]==WHITE) {
						_front.add(x+1, y);
						image[x+1][y] = color;
					}
					if(image[x+1][y]==BLACK) {image[x][y] = color+MAX; // edge
					}
				}
			}
		}
		return ans;
	}
	/**
	 * 
	 * 
	 * 
	 * @param image
	 * @param size
	 * @return
	 *  int NUM_OF_PIX = 1, NUM_OF_EDGE = 2, CEN_X=3, CEN_Y=4, ON_BORDER=5;
	 */
	private static ArrayList<Image_light> CC_Image2List(int[][] image, int size) {
		ArrayList<Image_light>  ans = new ArrayList<Image_light>(size);
		double[][] info = new double[size+GRAY+1][9];
		for(int x=0; x< image.length;x++) {
			for(int y=0;y<image[0].length;y++) {
				int c = image[x][y];
				if(c>GRAY) {
					if(c>=MAX) {
						c=c-MAX; 
						info[c][NUM_OF_EDGE]++;
					}
					
					info[c][NUM_OF_PIX]++;
					info[c][CEN_X]+=x;
					info[c][CEN_Y]+=y;
					if(info[c][ON_BORDER]==0 && (x==0||y==0||x==image.length-1||y==image[0].length-1)) {
						info[c][ON_BORDER]=1;
					}
				}
			}
		}
		for(int i=GRAY+1;i<=GRAY+size;i++) {
			info[i][CEN_X] = info[i][CEN_X]/info[i][NUM_OF_PIX];
			info[i][CEN_Y] = info[i][CEN_Y]/info[i][NUM_OF_PIX];
		}

		for(int x=0; x< image.length;x++) {
			for(int y=0;y<image[0].length;y++) {
				int c = image[x][y];
				if(c>=MAX) {c=c-MAX;
					double dx = x-info[c][CEN_X];
					double dy = y-info[c][CEN_Y];
					double r2 = dx*dx+dy*dy;
					if(info[c][RADIUS]<r2) {info[c][RADIUS]=r2;}
				}
			}
		}
		for(int i=GRAY+1;i<=GRAY+size;i++) {
			info[i][RADIUS] = Math.sqrt(info[i][RADIUS]);
			int id = i;
			// Image_light(int num_pix, int num_edges, Point2D cen, double diameter, boolean in_cen, double fat)
			int num_of_pix = (int)info[i][NUM_OF_PIX];
			int num_of_edge = (int)info[i][NUM_OF_EDGE];
			Point2D cen = new Point2D(info[i][CEN_X], info[i][CEN_Y]);
			double rad = info[i][RADIUS];
			boolean in_cen = true; if(info[i][ON_BORDER]==1){in_cen=false;}
			double fat = Math.min(1, num_of_pix / (rad*rad*Math.PI));
			Image_light il = new Image_light(num_of_pix,num_of_edge, cen, rad, in_cen, fat);
			il.set_id(i);
			ans.add(il);
		}	
		// Skeleton is not yet implemented!!
		// should be using MIN_RADIUS_NEON & MAX_FATNESS_NEON...
		return ans;
	}
}
