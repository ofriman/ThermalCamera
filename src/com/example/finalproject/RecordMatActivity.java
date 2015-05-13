package com.example.finalproject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.RecordMatActivity.CheckboxListAdapter.SampleData;
import com.example.utility.Data;
import com.example.utility.RawCompression;

public class RecordMatActivity extends ActionBarActivity {

	private static final String media_path = Environment
			.getExternalStorageDirectory().getPath() + "/FinalProject/Media";

	private static final String flag_id = RecordActivity.flag_id;
	private static final int bitmap_arr = RecordActivity.bitmap_arr ;
	private static final int int_mat = RecordActivity.int_mat ;
	private static final int byte_buffer = 0 ;
	private static final int my_compress = 1 ;
	private static final int my_compress_frames = 0 ;
	private static final int my_compress_frames_intensive = 1 ;

	private static final int intensive_const = 3 ;

	private static final String bitmap_arr_info = "Compress bitmap arr to chosen mp4 \nFolder: "+media_path ;
	private static final String int_mat_info = "Compress int mat (Heatmap arr) to chosen compress and zip\nFolder: "+media_path ;

	private int mode ;
	private SharedPreferences prefs;
	private TextView tv_info ;
	private Spinner spin_img ;
	private ListView lv_frame ;
	private Spinner spin_format ;
	private Spinner spin_compress ;
	private Button btn_save ;
	private CheckboxListAdapter  adapter_frame = null ;
	private ArrayAdapter<String> adapter_image = null ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_mat);


		Intent intent =  getIntent();
		if (intent != null ) {
			mode = intent.getIntExtra(flag_id,0);

			tv_info = (TextView) findViewById(R.id.tv_info);
			spin_img = (Spinner) findViewById(R.id.spin_img);
			lv_frame = (ListView) findViewById(R.id.list_frame);
			spin_format = (Spinner) findViewById(R.id.spin_format);
			spin_compress= (Spinner) findViewById(R.id.spin_compress);
			btn_save = (Button) findViewById(R.id.btn_save);

			prefs = getSharedPreferences(MainActivity.PREF_FILE_NAME, MODE_PRIVATE);

			int number_image = Data.IMAGE_SIZE.length;
			List<String> img_temp_list =  new ArrayList<String>();
			for (int i = 0; i < number_image ; i++) {
				img_temp_list.add("IMG "+i);
			}
			adapter_image = new ArrayAdapter<String>(
					this, android.R.layout.simple_spinner_item, img_temp_list);
			adapter_image.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			List<SampleData> frame_temp_list = new ArrayList<SampleData>();
			adapter_frame = new CheckboxListAdapter(
					getLayoutInflater(),frame_temp_list );

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
				}

				@Override
				public void onNothingSelected(AdapterView<?> parentView) 
				{

				}
			});
			spin_img.setAdapter(adapter_image);
			spin_img.setSelection(0);	

			lv_frame.setAdapter(adapter_frame);


			switch(mode){
			case bitmap_arr :
				setBitmapArrLayout();
				break;
			case int_mat:
				setIntMatLayout();
				break;
			}

			File folder = new File(media_path);
			if (!folder.exists())
				folder.mkdirs();
		}
	}


	private void setIntMatLayout(){

		tv_info.setText(int_mat_info);

		String[] format_data = {"Byte Buffer","My Comoress"};
		List<String> format_temp_list = new ArrayList<String>(Arrays.asList(format_data));
		ArrayAdapter<String> format_temp_adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, format_temp_list);
		format_temp_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_format.setAdapter(format_temp_adapter);
		spin_format.setSelection(0);	
		spin_format.setVisibility(View.VISIBLE);

		String[] compress_data = {"my compress","my compress(intensive)"};
		List<String> compress_temp_list = new ArrayList<String>(Arrays.asList(compress_data));
		ArrayAdapter<String> compress_temp_adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, compress_temp_list);
		format_temp_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_compress.setAdapter(compress_temp_adapter);
		spin_compress.setSelection(0);	
		spin_compress.setVisibility(View.VISIBLE);

		btn_save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int folder_id = prefs.getInt("folder_id", 0 ) ;
				int pic_id = prefs.getInt("pic_id", 0 ) ;
				int img_id = spin_img.getSelectedItemPosition();
				boolean [] frame_id = adapter_frame.getChecked() ;
				int format_id = spin_format.getSelectedItemPosition();
				int compress_id = spin_compress.getSelectedItemPosition();
				new SaveMatArr(RecordMatActivity.this,img_id,frame_id,pic_id,folder_id,format_id,compress_id).execute();
			}
		});

	}
	private void setBitmapArrLayout(){

		tv_info.setText(bitmap_arr_info);
		spin_format.setVisibility(View.GONE);	
		spin_compress.setVisibility(View.GONE);
		btn_save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Toast.makeText(getApplicationContext(), "Not Ready", 
						Toast.LENGTH_LONG).show();
			}
		});


	}


	private class SaveMatArr extends AsyncTask<Void, String, String> {

		private WeakReference<RecordMatActivity> weakActivity = null;
		private RecordMatActivity activity = null ;
		private ProgressDialog dialog = null ;
		private int image_id ;
		private boolean []  frame_id  ;
		private int pic_id  ;
		private int folder_id  ;
		private int format_id  ;
		private int compress_id  ;

		public SaveMatArr (RecordMatActivity context, int image_id , boolean [] frame_id, int pic_id, int folder_id,int format_id,int compress_id) {
			this.weakActivity = new WeakReference<RecordMatActivity>(context);
			this.image_id = image_id; 
			this.frame_id = frame_id;
			this.pic_id = pic_id; 
			this.folder_id = folder_id;
			this.format_id = format_id;
			this.compress_id=compress_id;
		}

		@Override
		protected String doInBackground(Void... params) {
			Activity activity = weakActivity.get();
			if (activity != null) {

				publishProgress("Loading frames ...");

				int size = 0 ;
				for (int i = 0; i < frame_id.length; i++) {
					if(frame_id[i])size ++ ;
				}
				int [][] frames = new int [size][]; 

				for (int i = 0,u=0; i < frame_id.length; i++) {
					if(frame_id[i]){
						String txtfile = image_id+"/"+Data.RAW_INDEX[i];
						frames[u++] = Data.getRawArr(activity , txtfile ) ;
					}
				}

				publishProgress("checking frames ...");

				for (int i = 0; i < frames.length; i++) {
					if(frames[i] == null){
						return "error , frame is null "; 
					}
				}

				if(frames.length == 0)
				{
					return "error , you didnt pick frames "; 
				}

				publishProgress("Creating file ...");

				File folder = new File(media_path+ "/" + folder_id);
				if (!folder.exists()){
					folder.mkdir();
				}

				String zip_file_path = media_path +"/" + folder_id + "/ZIP"+pic_id+".zip" ;

				publishProgress("Compressing frames ...");

				int [][] frames_clone = new int [size][] ;

				switch(compress_id){
				case my_compress_frames :
					for (int i = 0; i < frames_clone.length; i++) {
						frames_clone[i] = new int [frames[i].length];
						frames_clone[i][0]=  frames[i][0];
						for (int j=1; j<frames_clone[i].length; j++){
							frames_clone[i][j] = frames[i][j] - frames[i][0];
						}
					}
					break ;
				case my_compress_frames_intensive :
				default:
					for (int i = 0; i < frames_clone.length; i++) {
						frames_clone[i] = new int [frames[i].length];
						frames_clone[i][0]= frames[i][0];
						for (int j=1; j<frames_clone[i].length; j++){
							frames_clone[i][j] = frames[i][j] - frames[i][0];
							if(Math.abs(frames_clone[i][j]) < intensive_const){
								frames_clone[i][j] = 0 ;
							}
						}
					}
					break ;
				}

				byte [][] compress_arr = new byte[size][]  ;

				switch(format_id){
				case my_compress :
					for (int i = 0; i < compress_arr.length; i++) {
						compress_arr[i] = RawCompression.Compress(frames_clone[i]);
					}
					break ;
				case byte_buffer :
				default:
					for (int i = 0; i < compress_arr.length; i++) {
						ByteBuffer byteBuffer = ByteBuffer.allocate(frames_clone[i].length*4);        
						IntBuffer intBuffer = byteBuffer.asIntBuffer();
						intBuffer.put(frames_clone[i]);
						compress_arr[i] = byteBuffer.array();
					}
					break ;
				}

				for (int i = 0; i < compress_arr.length; i++) {
					if(compress_arr[i] == null){
						return "error , compressing frames "+i;
					}
				}

				publishProgress("Ziping frames ...");
				try {
					//DeflaterOutputStream dest = new DeflaterOutputStream(
					//new FileOutputStream(zip_file_path));
					ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zip_file_path)));
					for (int i = 0; i < compress_arr.length; i++) {
						String filename = "frame"+i;
						ZipEntry entry = new ZipEntry(filename);
						zos.putNextEntry(entry);
						//zos.write(Arrays.toString(frames[i]).getBytes());
						zos.write(compress_arr[i]);
						zos.closeEntry();	
					}
					zos.close();

					publishProgress("checking array ...");
					//InflaterInputStream dis = new InflaterInputStream(new FileInputStream(zip_file_path));
					FileInputStream dis = new FileInputStream(zip_file_path);
					ZipInputStream zis = new ZipInputStream(dis);

					for (int i = 0; i < compress_arr.length; i++) {
						ZipEntry ze = zis.getNextEntry() ;
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						byte[] buffer = new byte[1024];
						int count;
						while ((count = zis.read(buffer)) != -1) {
							baos.write(buffer, 0, count);
						}
						byte[] decompressArr = baos.toByteArray();
						if(!Arrays.equals(compress_arr[i], decompressArr))
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

						if(decompressArr == null){
							return "error , decompressing frames "+i;
						}

						switch(compress_id){
						case my_compress_frames :
							for (int j=1; j<decompressFrame.length; j++){
								decompressFrame[j] +=  decompressFrame[0];
							}
							break ;
						case my_compress_frames_intensive :
						default:
							// intensive
							break ;
						}

						if(compress_id != my_compress_frames_intensive &&  !Arrays.equals(decompressFrame, frames[i]))
						{
							if (new File(zip_file_path).delete()){
								return "error , frames arrays not equals , file was deleted" ; 
							}
							else{
								return "error , frames arrays not equals , file was NOT deleted" ; 
							}
						}
						zis.closeEntry();
					}
					zis.close();

				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return  "error , zipping file not found";		
				} catch (IOException e) {
					e.printStackTrace();
					return  "error , zipping file";		
				}

				prefs.edit().putInt("pic_id", pic_id+1).commit();
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


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.record_mat, menu);
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

	public class CheckboxListAdapter extends BaseAdapter implements OnClickListener {

		/** The inflator used to inflate the XML layout */
		private LayoutInflater inflator;

		/** A list containing some sample data to show. */
		private List<SampleData> dataList;

		public CheckboxListAdapter(LayoutInflater inflator,List<SampleData> list) {
			super();
			this.inflator = inflator;
			dataList = list ;
		}

		public boolean[] getChecked(){
			int size = dataList.size();
			boolean [] ans = new boolean[size];
			for (int i = 0; i < size; i++) {
				SampleData s = dataList.get(i);
				ans[i]= s.isSelected();
			}
			return ans ;
		}
		public void add(String name) {
			dataList.add(new SampleData(name, false));
		}
		public void clear() {
			dataList.clear();
		}

		@Override
		public int getCount() {
			return dataList.size();
		}

		@Override
		public Object getItem(int position) {
			return dataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup viewGroup) {

			// We only create the view if its needed
			if (view == null) {
				view = inflator.inflate(R.layout.pick_list_item, null);

				// Set the click listener for the checkbox
				view.findViewById(R.id.list_item_checkbox).setOnClickListener(this);
			}

			SampleData data = (SampleData) getItem(position);

			// Set the example text and the state of the checkbox
			CheckBox cb = (CheckBox) view.findViewById(R.id.list_item_checkbox);
			cb.setChecked(data.isSelected());
			// We tag the data object to retrieve it on the click listener.
			cb.setTag(data);

			TextView tv = (TextView) view.findViewById(R.id.list_item_textview);
			tv.setText(data.getName());

			return view;
		}

		@Override
		/** Will be called when a checkbox has been clicked. */
		public void onClick(View view) {
			SampleData data = (SampleData) view.getTag();
			data.setSelected(((CheckBox) view).isChecked());
		}

		public class SampleData {

			private String name;
			private boolean selected;

			public SampleData(String name, boolean selected) {
				super();
				this.name = name;
				this.selected = selected;
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public boolean isSelected() {
				return selected;
			}

			public void setSelected(boolean selected) {
				this.selected = selected;
			}

		}

	}

}
