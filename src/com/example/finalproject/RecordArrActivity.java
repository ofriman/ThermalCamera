package com.example.finalproject;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utility.Data;
import com.example.utility.RawCompression;

public class RecordArrActivity extends ActionBarActivity {

	private static final String media_path = Environment
			.getExternalStorageDirectory().getPath() + "/FinalProject/Media";

	private static final String flag_id = RecordActivity.flag_id;
	private static final int bitmap = RecordActivity.bitmap ;
	private static final int int_arr = RecordActivity.int_arr ;
	private static final int png = 0 ;
	private static final int jpeg = 1 ;
	private static final int webp = 2 ;
	private static final int byte_buffer = 0 ;
	private static final int my_compress = 1 ;
	private static final String bitmap_info = "Compress bitmap to chosen format \nFolder: "+media_path ;
	private static final String int_arr_info = "Compress int arr (Heatmap) to chosen compress and zip\nFolder: "+media_path ;

	private int mode ;
	private SharedPreferences prefs;
	private TextView tv_info ;
	private Spinner spin_img ;
	private Spinner spin_frame ;
	private Spinner spin_format ;
	private Button btn_save ;
	private ArrayAdapter<String> adapter_frame = null ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_arr);

		Intent intent =  getIntent();
		if (intent != null ) {
			mode = intent.getIntExtra(flag_id,0);

			tv_info = (TextView) findViewById(R.id.tv_info);
			spin_img = (Spinner) findViewById(R.id.spin_img);
			spin_frame = (Spinner) findViewById(R.id.spin_frame);
			spin_format = (Spinner) findViewById(R.id.spin_format);
			btn_save = (Button) findViewById(R.id.btn_save);

			prefs = getSharedPreferences(MainActivity.PREF_FILE_NAME, MODE_PRIVATE);

			int number_image = Data.IMAGE_SIZE.length;
			List<String> img_temp_list =  new ArrayList<String>();
			for (int i = 0; i < number_image ; i++) {
				img_temp_list.add("IMG "+i);
			}
			ArrayAdapter<String> img_temp_adapter = new ArrayAdapter<String>(
					this, android.R.layout.simple_spinner_item, img_temp_list);
			img_temp_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spin_img.setAdapter(img_temp_adapter);
			spin_img.setSelection(0);	

			switch(mode){
			case bitmap :
				setBitmapLayout();
				break;
			case int_arr:
				setIntArrLayout();
				break;
			}

			File folder = new File(media_path);
			if (!folder.exists())
				folder.mkdirs();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.record_arr, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		//		int id = item.getItemId();
		//		if (id == R.id.action_settings) {
		//			return true;
		//		}
		return super.onOptionsItemSelected(item);
	}

	private void setIntArrLayout(){

		tv_info.setText(int_arr_info);

		spin_frame.setVisibility(View.VISIBLE);
		List<String> frame_temp_list = new ArrayList<>();
		adapter_frame = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, frame_temp_list );
		adapter_frame.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_frame.setAdapter(adapter_frame);
		spin_frame.setSelection(0);

		spin_img.setOnItemSelectedListener(new OnItemSelectedListener() 
		{
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
			{	

				adapter_frame.clear();
				int numbers_frames = Data.IMAGE_SIZE[position];

				for (int i = 0; i < numbers_frames ; i++) {
					adapter_frame.add("Frame "+i);
				}

				adapter_frame.notifyDataSetChanged();
				spin_frame.setSelection(0);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) 
			{

			}
		});

		String[] format_data = {"Byte Buffer","My Comoress"};
		List<String> format_temp_list = new ArrayList<String>(Arrays.asList(format_data));
		ArrayAdapter<String> format_temp_adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, format_temp_list);
		format_temp_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_format.setAdapter(format_temp_adapter);
		spin_format.setSelection(0);	

		btn_save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int folder_id = prefs.getInt("folder_id", 0 ) ;
				int pic_id = prefs.getInt("pic_id", 0 ) ;
				int img_id = spin_img.getSelectedItemPosition();
				int frame_id = spin_frame.getSelectedItemPosition();
				int format_id = spin_format.getSelectedItemPosition();
				new SaveIntArr(RecordArrActivity.this,img_id,frame_id,pic_id,folder_id,format_id).execute();

			}
		});

	}
	private void setBitmapLayout(){

		tv_info.setText(bitmap_info);
		spin_frame.setVisibility(View.GONE);

		String[] format_data = {"PNG","JPEG","WEBP"};
		List<String> format_temp_list = new ArrayList<String>(Arrays.asList(format_data));
		ArrayAdapter<String> format_temp_adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, format_temp_list);
		format_temp_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_format.setAdapter(format_temp_adapter);
		spin_format.setSelection(0);	

		btn_save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int folder_id = prefs.getInt("folder_id", 0 ) ;
				int pic_id = prefs.getInt("pic_id", 0 ) ;
				int img_id = spin_img.getSelectedItemPosition();
				int format_id = spin_format.getSelectedItemPosition();
				new SaveBitmap(RecordArrActivity.this,pic_id,folder_id,img_id,format_id).execute();
			}
		});


	}

	private class SaveBitmap extends AsyncTask<Void, String, String> {

		private WeakReference<RecordArrActivity> weakActivity;
		private RecordArrActivity activity = null ;
		private ProgressDialog dialog = null ;
		private int pic_id ;
		private int folder_id  ;
		private int format  ;
		private int image_id ;

		public SaveBitmap (RecordArrActivity context,int pic_id,int folder_id,int image_id,int format) {
			this.weakActivity = new WeakReference<RecordArrActivity>(context);	
			this.pic_id = pic_id ;
			this.folder_id = folder_id ;
			this.format = format ;
			this.image_id = image_id;
		}

		@Override
		protected String doInBackground(Void... params) {
			if (activity != null) {

				Bitmap bitmap ;
				try{
					publishProgress("Loading bitmap ...");
					InputStream ims = activity.getAssets().open(image_id+"/"+Data.IMAGE_INDEX[0]);
					bitmap = BitmapFactory.decodeStream(ims);
				} catch (IOException e1) {
					e1.printStackTrace();
					return "error , unable to load bitmap from assets" ;
				}

				if(bitmap == null){
					return "error , image is null"  ; 
				}

				publishProgress("Creating image ...");
				String img_file_path = "";
				File img_file = null ;
				File folder = new File(media_path+ "/" + folder_id);

				if (!folder.exists())
					folder.mkdirs();

				switch(format){
				case jpeg :
					img_file_path = media_path +"/" + folder_id + "/IMG"+pic_id+".jpeg";
					break ;
				case webp :
					img_file_path = media_path +"/" + folder_id + "/IMG"+pic_id+".webp";
					break ;	
				case png :
				default:
					img_file_path = media_path +"/" + folder_id + "/IMG"+pic_id+".png";
					break ;
				}

				img_file = new File(img_file_path) ;

				if (!img_file.exists()){
					try {
						img_file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
						return "error , unable to create image"  ;
					}
				} 

				try {
					FileOutputStream stream = new FileOutputStream(img_file);
					switch(format){
					case jpeg :
						bitmap.compress(CompressFormat.JPEG, 100, stream);	
						break ;
					case webp :
						bitmap.compress(CompressFormat.WEBP, 100, stream);	
						break ;	
					case png :
					default:
						bitmap.compress(CompressFormat.PNG, 100, stream);	
						break ;
					}

					if (stream != null) {
						stream.close();
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return  "error , unable to find file" ;
				} catch (IOException e) {
					e.printStackTrace();
					return "error , somthing went wrong" ;
				}
				prefs.edit().putInt("pic_id", pic_id+1).commit();
				return "SAVED : "+img_file_path+"\nIMG: "+image_id ;
			}
			else {
				return "error , activity is null";		
			}	
		}

		@Override
		protected void onPostExecute(String result) {
			if(activity != null){
				if(dialog!=null)
					dialog.dismiss();
				Toast.makeText(activity, result, 
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

	private class SaveIntArr extends AsyncTask<Void, String, String> {

		private WeakReference<RecordArrActivity> weakActivity = null;
		private RecordArrActivity activity = null ;
		private ProgressDialog dialog = null ;
		private int image_id ;
		private int frame_id  ;
		private int pic_id  ;
		private int folder_id  ;
		private int format_id  ;

		public SaveIntArr (RecordArrActivity context, int image_id , int frame_id, int pic_id, int folder_id,int format_id) {
			this.weakActivity = new WeakReference<RecordArrActivity>(context);
			this.image_id = image_id; 
			this.frame_id = frame_id;
			this.pic_id = pic_id; 
			this.folder_id = folder_id;
			this.format_id = format_id;
		}

		@Override
		protected String doInBackground(Void... params) {
			Activity activity = weakActivity.get();
			if (activity != null) {

				publishProgress("Loading frame ...");	
				String txtfile = image_id+"/"+Data.RAW_INDEX[frame_id];
				int [] frame = Data.getRawArr(activity , txtfile ) ;

				if(frame == null){
					return "error , frame is null "+txtfile ; 
				}

				publishProgress("Creating file ...");

				File folder = new File(media_path+ "/" + folder_id);
				if (!folder.exists()){
					folder.mkdir();
				}

				String zip_file_path = media_path +"/" + folder_id + "/ZIP"+pic_id+".zip" ;

				publishProgress("Compressing file ...");
				byte [] compress_arr = null  ;

				switch(format_id){
				case my_compress :
					compress_arr = RawCompression.Compress(frame);
					break ;
				case byte_buffer :
				default:
					ByteBuffer byteBuffer = ByteBuffer.allocate(frame.length*4);        
					IntBuffer intBuffer = byteBuffer.asIntBuffer();
					intBuffer.put(frame);
					compress_arr = byteBuffer.array();
					break ;
				}

				publishProgress("Ziping file ...");

				try {
					//DeflaterOutputStream dest = new DeflaterOutputStream(
					//new FileOutputStream(zip_file_path));
					ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zip_file_path)));
					String filename = "frame";
					ZipEntry entry = new ZipEntry(filename);
					zos.putNextEntry(entry);
					//zos.write(Arrays.toString(frames[i]).getBytes());
					zos.write(compress_arr);
					zos.closeEntry();
					zos.close();

					publishProgress("checking array ...");
					//InflaterInputStream dis = new InflaterInputStream(new FileInputStream(zip_file_path));
					FileInputStream dis = new FileInputStream(zip_file_path);
					ZipInputStream zis = new ZipInputStream(dis);
					ZipEntry ze = zis.getNextEntry() ;
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024];
					int count;
					while ((count = zis.read(buffer)) != -1) {
						baos.write(buffer, 0, count);
					}
					zis.closeEntry();
					zis.close();
					byte[] decompressArr = baos.toByteArray();
					if(!Arrays.equals(compress_arr, decompressArr))
					{
						if (new File(zip_file_path).delete()){
							return "error , compress arrays not equals , file was deleted" ; 
						}
						else{
							return "error , compress arrays not equals , file was NOT deleted" ; 
						}
					}
					int [] decompressFrame ;
					switch(format_id){
					case my_compress :
						decompressFrame = RawCompression.Decompress(decompressArr);
						break ;
					case byte_buffer :
					default:
						IntBuffer intBuf = ByteBuffer.wrap(decompressArr)
						.order(ByteOrder.BIG_ENDIAN)
						.asIntBuffer();
						decompressFrame = new int[intBuf.remaining()];
						intBuf.get(decompressFrame);
					}

					if(!Arrays.equals(decompressFrame, frame))
					{
						if (new File(zip_file_path).delete()){
							return "error , frames arrays not equals , file was deleted" ; 
						}
						else{
							return "error , frames arrays not equals , file was NOT deleted" ; 
						}
					}

				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return  "error , zipping file not found";		
				} catch (IOException e) {
					e.printStackTrace();
					return  "error , zipping file";		
				}

				prefs.edit().putInt("pic_id", pic_id+1).commit();
				return  "SAVED : "+zip_file_path+"\nIMG: "+image_id+" , frame: "+frame_id;
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
				Toast.makeText(activity,result, 
						Toast.LENGTH_LONG).show();
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



}
