package com.example.utility;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.Toast;


public class Data {

	public static final int camera_pic_height = 384 ;
	public static final int camera_pic_width = 288 ;

	public static final int camera_temperature_absolute_minimum = 5;
	public static final int camera_temperature_absolute_maximum = 90;
	public static final int value_absolute_minimum = 0;
	public static final int value_absolute_maximum  = 255;

	private  static final int type_size_00 = 8 ;
	private  static final int type_size_01 = 6 ;
	public static final int [] IMAGE_SIZE = {type_size_00,type_size_01} ;

	private static final String raw_00 = "RAW0.txt" ;
	private static final String raw_01 = "RAW1.txt" ;
	private static final String raw_02 = "RAW2.txt" ;
	private static final String raw_03 = "RAW3.txt" ;
	private static final String raw_04 = "RAW4.txt" ;
	private static final String raw_05 = "RAW5.txt" ;
	private static final String raw_06 = "RAW6.txt" ;
	private static final String raw_07 = "RAW7.txt" ;
	private static final String raw_08 = "RAW8.txt" ;
	private static final String raw_09 = "RAW9.txt" ;
	private static final String raw_10 = "RAW10.txt" ;
	private static final String raw_11 = "RAW11.txt" ;
	private static final String raw_12 = "RAW12.txt" ;
	private static final String raw_13 = "RAW13.txt" ;
	private static final String raw_14 = "RAW14.txt" ;
	private static final String raw_15 = "RAW15.txt" ;
	private static final String raw_16 = "RAW16.txt" ;
	private static final String raw_17 = "RAW17.txt" ;
	private static final String raw_18 = "RAW18.txt" ;
	private static final String raw_19 = "RAW19.txt" ;
	private static final String raw_20 = "RAW20.txt" ;

	private static final String img_00 = "IMG0.png" ;
	private static final String img_01 = "IMG1.png" ;
	private static final String img_02 = "IMG2.png" ;
	private static final String img_03 = "IMG3.png" ;
	private static final String img_04 = "IMG4.png" ;
	private static final String img_05 = "IMG5.png" ;
	private static final String img_06 = "IMG6.png" ;
	private static final String img_07 = "IMG7.png" ;
	private static final String img_08 = "IMG8.png" ;
	private static final String img_09 = "IMG9.png" ;
	private static final String img_10 = "IMG10.png" ;
	private static final String img_11 = "IMG11.png" ;
	private static final String img_12 = "IMG12.png" ;
	private static final String img_13 = "IMG13.png" ;
	private static final String img_14 = "IMG14.png" ;
	private static final String img_15 = "IMG15.png" ;
	private static final String img_16 = "IMG16.png" ;
	private static final String img_17 = "IMG17.png" ;
	private static final String img_18 = "IMG18.png" ;
	private static final String img_19 = "IMG19.png" ;
	private static final String img_20 = "IMG20.png" ;

	public static final String [] RAW_INDEX =   {raw_00,raw_01 , raw_02 , raw_03 , raw_04 , raw_05 , raw_06 , raw_07 , raw_08, raw_09 , raw_10 , raw_11 , raw_12 , raw_13 , raw_14 , raw_15 , raw_16 , raw_17 , raw_18 , raw_19 , raw_20};
	public static final String [] IMAGE_INDEX = {img_00,img_01 , img_02 , img_03 , img_04 , img_05 , img_06 , img_07 , img_08, img_09 , img_10 , img_11 , img_12 , img_13 , img_14 , img_15 , img_16 , img_17 , img_18 , img_19 , img_20};

	public static String getRawString(Activity activity  ,String file)
	{
		// load text
		try {
			// get input stream for text
			InputStream is = activity.getAssets().open(file);
			// check size
			int size = is.available();
			// create buffer for IO
			byte[] buffer = new byte[size];
			// get data to buffer
			is.read(buffer);
			// close stream
			is.close();
			// set result to TextView
			String line = new String(buffer);
			return line;
		}
		catch (IOException ex) {
			Toast.makeText(activity, "error raw path",
					Toast.LENGTH_LONG).show();
			return null;
		}
	}
	public static int[] getRawArr(Activity activity , String path){
		return convertLinetoInt(getRawString(activity,path));
	}
	private static int[] convertLinetoInt(String line)
	{
		if(line == null)
			return null ;

		String[] items = line.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "").split(",");
		int size = camera_pic_height * camera_pic_width , itemssize = items.length ;

		if(itemssize != size)
			return null ;

		int [] arr = new int [size];

		for (int i = 0; i < size; i++) {
			try {
				arr[i] = Integer.parseInt(items[i]);
			} catch (NumberFormatException nfe) {
				return null ;		    	
			};
		}
		return arr ;
	}

	public static int[] convertToHistogram(int [] pic,int temp_user_abs_min,int temp_user_abs_max,int temp_min,int temp_max,int value_min,int value_max)
	{
		int minMax [] = {temp_user_abs_min,temp_min,temp_max,temp_user_abs_max} ;
		int newMinMax [] = {value_absolute_minimum,value_min,value_max,value_absolute_maximum} ;

		int [] hist = new int [newMinMax[3]+1+1];

		for (int i = 0; i < pic.length; i++) {
			int tempature = pic[i] ;
			float index  ;

			if(tempature < minMax[0] *100 )
			{
				index = newMinMax[3]+1 ;
			}
			else if( tempature < minMax[1]*100)
			{
				// 0 - min1
				index = ((tempature/100)-minMax[0])*( ((float)newMinMax[1]-newMinMax[0])/(minMax[1]-minMax[0]) ) +newMinMax[0] ;

			}
			else if(tempature < minMax[2]*100)
			{
				//min1-max1
				index = ((tempature/100)-minMax[1])*( ((float)newMinMax[2]-newMinMax[1])/(minMax[2]-minMax[1]) ) +newMinMax[1] ;
			}
			else if(tempature < minMax[3]*100)
			{
				//max1-max
				index = ((tempature/100)-minMax[2])*( ((float)newMinMax[3]-newMinMax[2])/(minMax[3]-minMax[2]) ) +newMinMax[2] ;
			}
			else{
				index = newMinMax[3]+1 ;
			}
			hist[(int)Math.floor( index )]++ ;
		}
		return hist ;
	}
	public static int[] convertToImage(int [] pic,int temp_user_abs_min,int temp_user_abs_max,int temp_min,int temp_max,int value_min,int value_max)
	{
		int min [] = {temp_user_abs_min*100,temp_min*100,temp_max*100,temp_user_abs_max*100,camera_temperature_absolute_maximum*100} ;
		int max [] = {camera_temperature_absolute_minimum*100,min[0],min[1],min[2],min[3]} ;
		
		for (int i = 0; i < pic.length; i++) {
			if ( pic[i] < temp_user_abs_min *100 )
			{
				if(pic[i] < min[0]){
					min[0]=pic[i];
				}
				else if (pic[i] > max[0]){
					max[0]=pic[i];
				}
			}
			else if ( pic[i] < temp_min *100 )
			{
				if(pic[i] < min[1]){
					min[1]=pic[i];
				}
				else if (pic[i] > max[1]){
					max[1]=pic[i];
				}
			}
			else if ( pic[i] < temp_max *100 )
			{
				if(pic[i] < min[2]){
					min[2]=pic[i];
				}
				else if (pic[i] > max[2]){
					max[2]=pic[i];
				}
			}
			else if ( pic[i] < temp_user_abs_max *100 )
			{
				if(pic[i] < min[3]){
					min[3]=pic[i];
				}
				else if (pic[i] > max[3]){
					max[3]=pic[i];
				}
			}
			else 
			{
				if(pic[i] < min[4]){
					min[4]=pic[i];
				}
				else if (pic[i] > max[4]){
					max[4]=pic[i];
				}
			}
		}

		int newMinMax [] = {value_absolute_minimum,value_min,value_max,value_absolute_maximum} ;
		int [] hist = new int [pic.length];

		for (int i = 0; i < pic.length; i++) {

			int tempature = pic[i] ;
			float index  ;

			if(tempature < min[1])
			{	
				
			  index = 256 + ((  ((float)tempature-min[0])/100  )*(((float)newMinMax[3]-newMinMax[0])/ (((float)(max[0]-min[0]))/100) )+newMinMax[0]) ;
			
			}
			else if( tempature < min[2] )
			{
				index = (  ((float)tempature-min[1])/100 )*(((float)newMinMax[1]-newMinMax[0])/ (((float)(max[1]-min[1]))/100) )+newMinMax[0] ;

			}
			else if(tempature < min[3] )
			{

				index = (  ((float)tempature-min[2])/100 )*(((float)newMinMax[2]-newMinMax[1])/ (((float)(max[2]-min[2]))/100) )+newMinMax[1] ;
			}
			else if(tempature < min[4])
			{
				index = (  ((float)tempature-min[3])/100 )*(((float)newMinMax[3]-newMinMax[2])/ (((float)(max[3]-min[3]))/100) )+newMinMax[2] ;
			}
			else{
				 index = 512 + ((  ((float)tempature-min[4])/100  )*(((float)newMinMax[3]-newMinMax[0])/ (((float)(max[4]-min[4]))/100) )+newMinMax[0]) ;
			}

			hist[i] = (int)Math.floor(index) ;
		}
		return hist ;
	}

	public static Bitmap convertToDiffView(Activity activity , int img_type)
	{

		int numbers_frames = Data.IMAGE_SIZE[img_type];
		int [][] pic = new int [numbers_frames][] ;
		for (int i = 0; i < numbers_frames; i++) {
			String path = img_type+"/"+Data.RAW_INDEX[i];
			pic[i] = getRawArr(activity, path);
			if(pic[i]==null)
				return null;
		}
		int ans [] = new int [camera_pic_height*camera_pic_width];
		int min = 90 ;
		int max = 0 ;
		for (int i = 0; i < pic[0].length; i++) {
			boolean cold = true ;
			boolean heat = true ;
			int avg = 0;
			int c = 0 ;

			for (int j = 0; j < numbers_frames-1; j++) {
				int temp = pic[j][i] ;
				int temp1 = pic[j+1][i];
				avg+=temp ;
				if(temp > temp1 ){
					heat = false ;
					c--;
				}
				else if(temp1 > temp){
					cold = false;
					c++ ;
				}
			}
			avg+=pic[pic.length-1][i];
			avg/=pic.length;
			if( cold & heat  ){
				ans[i] = 258;
			}
			else if(cold || numbers_frames+c < (int)numbers_frames*0.2  ){
				ans[i] = 256;
			}
			else if(heat || numbers_frames-c < (int)numbers_frames*0.2 ){
				ans[i] = 257;
			} 
			else{
				ans[i] = avg ;
				if(min>avg)
					min=avg;
				else if(max<avg)
					max=avg;
			}
		}
		return convetToBitmapDiffView(ans,min/100,max/100) ;
	}
	private static Bitmap convetToBitmapDiffView(int [] arr,int min,int max)
	{
		int h = camera_pic_width ;
		int w = camera_pic_height;
		Bitmap bitmap = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);

		int minMax [] = {camera_temperature_absolute_minimum,min,max,camera_temperature_absolute_maximum} ;
		int newMinMax [] = {value_absolute_minimum,0,255,value_absolute_maximum} ;
		int color = Color.BLACK, a, r, g, b;

		for (int i = 0; i < arr.length; i++) {

			float tempature = (((float)arr[i])/100) ;
			float index  ;
			a = Color.alpha(color);
			r = Color.red(color);
			g = Color.green(color);
			b = Color.blue(color);

			if(arr[i]==256)
			{
				bitmap.setPixel(i%w,((int)Math.floor(i/w)), Color.BLUE );
			}
			else if(arr[i]==257)
			{
				bitmap.setPixel(i%w,((int)Math.floor(i/w)), Color.RED );
			}
			else if(arr[i]==258)
			{
				bitmap.setPixel(i%w,((int)Math.floor(i/w)), Color.GREEN );
			}
			else if( camera_temperature_absolute_minimum <= ((int)Math.floor(tempature)) && ((int)Math.floor(tempature)) < minMax[1])
			{
				// 0 - min1
				index = (tempature-minMax[0])*( ((float)newMinMax[1]-newMinMax[0])/(minMax[1]-minMax[0]) ) +newMinMax[0] ;
				a = Math.abs(Color.alpha(color)- (int)Math.floor(index) );
				bitmap.setPixel(i%w,((int)Math.floor(i/w)), Color.argb(a, r, g, b));

			}
			else if( ((int)Math.floor(tempature)) <minMax[2])
			{
				//min1-max1
				index = (tempature-minMax[1])*( ((float)newMinMax[2]-newMinMax[1])/(minMax[2]-minMax[1]) ) +newMinMax[1] ;
				a = Math.abs(Color.alpha(color)- (int)Math.floor(index) );
				bitmap.setPixel(i%w,((int)Math.floor(i/w)), Color.argb(a, r, g, b));
			}
			else if(((int)Math.floor(tempature)) <= camera_temperature_absolute_maximum)
			{
				//max1-max
				index = (tempature-minMax[2])*( ((float)newMinMax[3]-newMinMax[2])/(minMax[3]-minMax[2]) ) +newMinMax[2] ;
				a = Math.abs(Color.alpha(color)- (int)Math.floor(index) );
				bitmap.setPixel(i%w,((int)Math.floor(i/w)), Color.argb(a, r, g, b));
			}
			else if( ((int)Math.floor(tempature)) < camera_temperature_absolute_minimum ) {
				index = 0 ;
				a = Math.abs(Color.alpha(color)- (int)Math.floor(index) );
				bitmap.setPixel(i%w,((int)Math.floor(i/w)), Color.argb(a, r, g, b));
			}
			else{
				index = 255 ;
				a = Math.abs(Color.alpha(color)- (int)Math.floor(index) );
				bitmap.setPixel(i%w,((int)Math.floor(i/w)), Color.argb(a, r, g, b));
			}
		}
		return bitmap ;
	}


}
