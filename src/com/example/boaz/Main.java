package com.example.boaz;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * this project include simple 2D algorithms for image processing - to used by indoorgo ltd
 * @author boaz
 *
 */
public class Main {
	public static int SEED = 31;
	public static Random _rand = new Random(SEED);
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		testBinaryImage2Lights2();
	}
	public static int[][] get_image(int x, int y) {
		int[][] image = new int[x][y];
		return image;
	}
	public static void testBinaryImage2Lights2() {
		int X = 320, Y = 240;
		int num_of_frames = 1000;//1;//;200;
		long t0 = new Date().getTime();
		ArrayList<Image_light> ans = null;
		for(int i=0;i<num_of_frames;i++) {
			int[][] image = get_image(X,Y);
			random_lights(image, 10);
			ans = ImageProcessingAlgo.Image2Lights(image);
		}
		System.out.println("Last frame data:");
		for(int s=0;s<ans.size();s++) {
			System.out.println(s+") "+ans.get(s));
		}
		long t1 = new Date().getTime();
		double dt = (t1-t0)/1000.0;
		
		System.out.println("total time for "+num_of_frames+" frames:["+X+","+Y+"]= "+dt);
		if(dt>1.0) {fail("ERR:" +num_of_frames+" frames on PC should take less then 1 sec!!");}
	}
	public static void random_lights(int[][] image, int num){
		int[] xx = new int[num];
		int[] yy = new int[num];
		int[] rr = new int[num];
		double[] nn = new double[num];
		double M = Math.min(image.length, image[0].length)/8;
		for(int i=0;i<num;i++) {
			int x = _rand.nextInt()%image.length;
			int y = _rand.nextInt()%image[0].length;
			xx[i] = Math.abs(x);
			yy[i] = Math.abs(y);
			rr[i] = (int)(_rand.nextDouble()*M);
			nn[i] = _rand.nextDouble();
		}
		for(int i=0;i<num;i++) {
			for(int x=xx[i]-rr[i];x<xx[i]+rr[i];x++) {
				for(int y=yy[i]-rr[i];y<yy[i]+rr[i];y++) {
					if(x>=0 & y>=0 & x<image.length & y<image[0].length) {
						double dx = Math.abs(xx[i]-x);
						double dy = Math.abs(yy[i]-y);
						double r = dx*nn[i]+ dy*(1-nn[i]);
						double r2 = r*r;
						if(r2<dx*dx+dy*dy) {
							image[x][y] = ImageProcessingAlgo.WHITE;
					
						}
					}
				}
			}
			
		}
	}
}
