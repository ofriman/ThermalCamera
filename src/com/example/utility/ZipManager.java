package com.example.utility;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.MainActivity;
import com.example.finalproject.R;

public class ZipManager {

	public static final int byte_buffer = 0 ;
	public static final int my_compress = 1 ;
	public static final String[] int_format_data = {"Byte Buffer","My Comoress"};

	public static final int png = 0 ;
	public static final int jpeg = 1 ;
	public static final int webp = 2 ;
	public static final String[] bitmap_format_data = {"Png","Jpeg","Webp"};

	public static final int my_compress_frames = 0 ;
	public static final int my_compress_frames_intensive = 1 ;
	public static final String[] int_compress_data = {"My Comoress","My Comoress Intensive"};

	public static final int intensive_const = 3 ;


	private static final String media_path = MainActivity.media_path ;

	public static int[][] MyMatCompress(int [][] frames,boolean intensive ){
		//TODO DEEP COPY FOR TESTING WE CAN REMOVE FOR OPTIMIZTION
		int [][] new_frames = new int [frames.length][] ;
		for (int i=0; i < frames.length; i++) {
			new_frames[i] = new int [frames[i].length];
			new_frames[i][0] = frames[i][0] ;
			for (int j=1; j<frames[i].length; j++){
				new_frames[i][j] = frames[i][j] - new_frames[i][0];
				if(intensive && Math.abs(frames[i][j]) < intensive_const){
					new_frames[i][j] = 0 ;
				}
			}
		}
		return new_frames;
	}
	public static int[][] MyMatDeCompress(int [][] frames){
		for (int i=0; i < frames.length; i++) {
			for (int j=1; j<frames[i].length; j++){
				frames[i][j] += frames[i][0];
			}
		}
		return frames;
	}

	public static byte[][] IntToByte(int [][] frames , int zip_id){
		byte [][] new_frames = new byte[frames.length][]  ;
		switch(zip_id){
		case my_compress :
			for (int i = 0; i < new_frames.length; i++) {
				new_frames[i] = RawCompression.Compress(frames[i]);
			}
			break ;
		case byte_buffer :
		default:
			for (int i = 0; i < new_frames.length; i++) {
				ByteBuffer byteBuffer = ByteBuffer.allocate(frames[i].length*4);        
				IntBuffer intBuffer = byteBuffer.asIntBuffer();
				intBuffer.put(frames[i]);
				new_frames[i] = byteBuffer.array();
			}
			break ;
		}
		return new_frames ;
	}
	public static int[][] ByteToInt(byte[][] frames , int zip_id){
		int [][] new_frames = new int[frames.length][]  ;
		switch(zip_id){
		case my_compress :
			for (int i = 0; i < new_frames.length; i++) {
				new_frames[i] = RawCompression.Decompress(frames[i]);
			}
			break ;
		case byte_buffer :
		default:
			for (int i = 0; i < new_frames.length; i++) {
				IntBuffer intBuf = ByteBuffer.wrap(frames[i])
						.order(ByteOrder.BIG_ENDIAN)
						.asIntBuffer();
				new_frames[i] = new int[intBuf.remaining()];
				intBuf.get(new_frames[i]);
			}
			break ;
		}
		return new_frames ;
	}

	public static boolean MyZip(byte [][] frames ,String zip_file_path){
		if(frames == null)
			return false ;
		try {
			ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zip_file_path)));
			zos.setLevel(Deflater.BEST_COMPRESSION); // This compression level gives the best compression, but takes the most time
			for (int i = 0; i < frames.length; i++) {
				String filename = "frame"+i;
				ZipEntry entry = new ZipEntry(filename);
				zos.putNextEntry(entry);
				zos.write(frames[i]);
				zos.closeEntry();	
			}
			zos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false ;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true ;
	}
	public static byte[][] MyUnZip(int size ,String zip_file_path){

		if( size <= 0)
			return null ;

		byte [][] decompressMat = new byte [size][];
		try {
			FileInputStream dis = new FileInputStream(zip_file_path);
			ZipInputStream zis = new ZipInputStream(dis);
			int i = 0 ;
			while ((zis.getNextEntry()) != null && i< size) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int count;
				while ((count = zis.read(buffer)) != -1) {
					baos.write(buffer, 0, count);
				}
				decompressMat[i++] = baos.toByteArray();
			}
			zis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null ;
		} catch (IOException e) {
			e.printStackTrace();
			return null ;
		}
		return decompressMat ; 
	}

	public static class SaveMatArr extends AsyncTask<Void, String, String> {

		private WeakReference<Activity> weakActivity = null;
		private Activity activity = null ;
		private ProgressDialog dialog = null ;
		private int image_id ;
		private boolean []  frame_id  ;
		private int pic_id  ;
		private int format_id  ;
		private int compress_id  ;
		private boolean test ;

		public SaveMatArr (Activity context, int image_id , boolean [] frame_id, int pic_id,int format_id,int compress_id,boolean test) {
			this.weakActivity = new WeakReference<Activity>(context);
			this.image_id = image_id; 
			this.frame_id = frame_id;
			this.pic_id = pic_id; 
			this.format_id = format_id;
			this.compress_id=compress_id;
			this.test = test ;
		}

		@Override
		protected String doInBackground(Void... params) {

			if (activity != null) {
					
				publishProgress("Loading frames ...");
				
				int size = 0 ;
				String test_txt =  "Format "+ZipManager.int_format_data[format_id]+
									"\nCompress "+ZipManager.int_compress_data[compress_id]+
									"\nFrames "+Arrays.toString(frame_id)+"\n";
				
				for (int i = 0; i < frame_id.length; i++) {
					if(frame_id[i])size ++ ;
				}
				if(size <= 0){
					return "error , you didnt pick any frames \n"; 
				}
			
				int [][] frames = new int [size][]; 
				for (int i = 0,u=0; i < frame_id.length; i++) {
					if(frame_id[i]){
						String txtfile = image_id+"/"+Data.RAW_INDEX[i];
						frames[u++] = Data.getRawArr(activity , txtfile ) ;
					}
				}
				test_txt += "Frames brfore - Length: "+(frames.length*frames[0].length*4)+" Byets\n";	
				
				publishProgress("checking Loaded frames ...");
				for (int i = 0; i < frames.length; i++) {
					if(frames[i] == null){
						return "error , frame "+i+" is null "; 
					}
					for(int j =0 ; j < frames[i].length ; j++){
						if(frames[i][j] < RawCompression._MIN_PIXEL_VAL)
						{
							frames[i][j] = RawCompression._MIN_PIXEL_VAL;
						}
						else if (frames[i][j] > RawCompression._MAX_PIXEL_VAL){
							frames[i][j] = RawCompression._MAX_PIXEL_VAL;
						}
					}
				}
				// END CHECK
				
				publishProgress("Creating file ...");
				File folder = new File(media_path);
				if (!folder.exists()){
					folder.mkdir();
				}
				String zip_file_path = media_path + "/ZIP"+pic_id+".zip" ;
				publishProgress("Compressing frames ...");
		
				int [][] compressed_frames_int ;
				if ( format_id == my_compress ){
					compressed_frames_int = frames ;
				}
				else{
					 compressed_frames_int = MyMatCompress(frames,compress_id==my_compress_frames_intensive);
				}

				byte [][] compressed_frames_byte = IntToByte(compressed_frames_int, format_id);
							
				for (int i = 0; i < compressed_frames_byte.length; i++) {
					if(compressed_frames_byte[i] == null){
						return "error , IntToByte frame "+i+" is null";
					}
				}

				publishProgress("Ziping frames ...");

				if ( !MyZip(compressed_frames_byte,zip_file_path)){
					return  "error , zipping file";
				}
				
				test_txt+="After Zip ="+new File(zip_file_path).length()+" Bytes\n";
				
				publishProgress("checking compressed_frames  ...");

				byte [][] uncompressed_frames_byte ;

				if ((uncompressed_frames_byte =  MyUnZip(compressed_frames_byte.length,zip_file_path)) == null ){
					return  "error , unzipping file";	
				}
				for (int i = 0; i < compressed_frames_byte.length; i++) {
					if(uncompressed_frames_byte == null)
					{
						return "error , unzip frame is null" ; 
					}
					if(!Arrays.equals(compressed_frames_byte[i], uncompressed_frames_byte[i]))
					{
						if (new File(zip_file_path).delete()){
							return "error , unzip byte compressed frame "+i+" not equals , file was deleted" ; 
						}
						else{
							return "error , unzip byte compressed frame "+i+" not equals , file was NOT deleted" ; 
						}
					}
				}

				int [][] uncompressed_frames_int = ByteToInt(uncompressed_frames_byte, format_id) ; 

				for (int i = 0; i < compressed_frames_int.length; i++) {
					if(uncompressed_frames_int[i] == null)
					{
						return "error ,unzip ByteToInt compressed frame "+i+" is null" ; 
					}
					if(!Arrays.equals(compressed_frames_int[i], uncompressed_frames_int[i]))
					{
						if (new File(zip_file_path).delete()){
							return "error , unzip ByteToInt compressed frame "+i+" not equals , file was deleted" ; 
						}
						else{
							return "error , unzip ByteToInt compressed frame "+i+" not equals , file was NOT deleted" ; 
						}
					}
				}

				if(compress_id==my_compress_frames && format_id != my_compress ){

					int [][] uncompressed_frames = MyMatDeCompress(uncompressed_frames_int) ;

					for (int i = 0; i < frames.length; i++) {
						if(uncompressed_frames == null)
						{
							return "error ,unzip uncompressed frame "+i+" is null" ; 
						}
						if(!Arrays.equals(uncompressed_frames[i], frames[i]))
						{
							if (new File(zip_file_path).delete()){
								return "error ,unzip uncompressed frame "+i+" not equals , file was deleted" ; 
							}
							else{
								return "error ,unzip uncompressed frame "+i+" not equals , file was NOT deleted" ; 
							}
						}
					}
				}
				
				if(test)
				{
					new File(zip_file_path).delete();
					return test_txt ;
				}

				return  "SAVED : "+zip_file_path+"\nIMG: "+image_id+" , frame: "+Arrays.toString(frame_id);
			}
			else {
				return "error , activity is null";		
			}
		}
		@Override
		protected void onPostExecute(String result) {
			if(activity != null){
				if(dialog!=null){
					dialog.dismiss();
				}
				if(test)
				{
					((TextView) activity.findViewById(R.id.tv_info)).append(result+"\n");
				}
				else{
				Toast.makeText(activity,result, 
						Toast.LENGTH_LONG).show();
				}
			}
		}
		@Override
		protected void onPreExecute() {
			activity = weakActivity.get();
			if (activity != null) {
				dialog = new ProgressDialog(activity);
				dialog.setMessage("SAVING FRAME...");
				dialog.show();  
			}
		}
		@Override
		protected void onProgressUpdate(String... values) {
			if(dialog !=null)
				dialog.setMessage(values[0]);
		}

	}

	public static class SaveBitmap extends AsyncTask<Void, String, String> {

		private WeakReference<Activity> weakActivity;
		private Activity activity = null ;
		private ProgressDialog dialog = null ;
		private int save_id ;
		private int image_id ;
		private boolean [] frames_id ;
		private int format  ;


		public SaveBitmap (Activity context,int save_id,int image_id,boolean [] frames_id,int format) {
			this.weakActivity = new WeakReference<Activity>(context);	
			this.save_id = save_id ;
			this.image_id = image_id;
			this.frames_id = frames_id;
			this.format = format ;
		}

		@Override
		protected String doInBackground(Void... params) {

			if (activity == null) {
				return "error , activity is null";		
			}	

			int size = 0 ;
			for (int i = 0; i < frames_id.length; i++) {
				if(frames_id[i])size ++ ;
			}
			if(size <= 0 ){
				return "pick some frames\n";		
			}

			Bitmap bitmaps [] = new Bitmap[size] ;
			try{
				publishProgress("Loading bitmap ...");
				for (int i = 0,j=0; i < frames_id.length && j <bitmaps.length; i++) {
					if(frames_id[i]){
						InputStream ims = activity.getAssets().open(image_id+"/"+Data.IMAGE_INDEX[i]);
						bitmaps[j++] = BitmapFactory.decodeStream(ims);
						ims.close();
					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
				return "error , unable to load bitmap from assets" ;
			}

			if(bitmaps == null){
				return "error , bitmaps is null"  ; 
			}
			for (int i = 0; i < bitmaps.length; i++) {
				if(bitmaps[i] == null){
					return "error , bitmaps "+i+" is null"  ; 
				}
			}

			publishProgress("Creating image ...");

			File folder = new File(media_path);
			if (!folder.exists())
				folder.mkdirs();

			for (int i = 0; i < bitmaps.length; i++) {

				String img_file_path = "";
				File img_file = null ;

				switch(format){
				case jpeg :
					img_file_path = media_path  + "/IMG"+(save_id+i)+".jpeg";
					break ;
				case webp :
					img_file_path = media_path  + "/IMG"+(save_id+i)+".webp";
					break ;	
				case png :
				default:
					img_file_path = media_path  + "/IMG"+(save_id+i)+".png";
					break ;
				}

				img_file = new File(img_file_path) ;

				if (!img_file.exists()){
					try {
						img_file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
						return "error , unable to create image "+i  ;
					}
				} 

				try {
					FileOutputStream stream = new FileOutputStream(img_file);
					switch(format){
					case jpeg :
						bitmaps[i].compress(CompressFormat.JPEG, 100, stream);	
						break ;
					case webp :
						bitmaps[i].compress(CompressFormat.WEBP, 100, stream);	
						break ;	
					case png :
					default:
						bitmaps[i].compress(CompressFormat.PNG, 100, stream);	
						break ;
					}
					if (stream != null) {
						stream.close();
					}
				}
				catch (FileNotFoundException e) {
					e.printStackTrace();
					return  "error , unable to find file "+i ;
				} catch (IOException e) {
					e.printStackTrace();
					return "error , somthing went wrong "+i ;
				}
			}
			return "SAVED IMG:\n"+image_id+"\n"+Arrays.toString(frames_id);
		}

		@Override
		protected void onPostExecute(String result) {
			if(activity != null){
				if(dialog!=null)
					dialog.dismiss();
				Toast.makeText(activity,result, 
						Toast.LENGTH_LONG).show();
			}
		}
		@Override
		protected void onPreExecute() {
			activity = weakActivity.get();
			if (activity != null) {
				dialog = new ProgressDialog(activity);
				dialog.setMessage("Saving Img...");
				dialog.show();  
			}
		}
		@Override
		protected void onProgressUpdate(String... values) {
			if(dialog !=null)
				dialog.setMessage(values[0]);
		}
	}

}
