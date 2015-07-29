package com.example.finalproject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.example.utility.Data;
import com.example.utility.ZipManager;

public class RecordActivity extends ActionBarActivity {

	private static final String media_path = MainActivity.media_path;

	private static final String bitmap_arr_info = "Compress bitmap arr to chosen format \nFolder: "+media_path ;
	private static final String int_mat_info =    "Compress int mat (Heatmap arr) to chosen format\nFolder: "+media_path ;

	private int mode ;
	private SharedPreferences prefs;
	private TextView tv_info ;
	private Spinner spin_img ;
	private Spinner spin_format ;
	private Spinner spin_compress ;
	private ListView lv_frame ;
	private Button btn_save ;
	private CheckboxListAdapter  adapter_frame  ;
	private ArrayAdapter<String> compress_adapter ;
	private ArrayAdapter<String> format_adapter ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);

		Intent intent =  getIntent();
		if (intent != null ) {

			mode = intent.getIntExtra(MainActivity.mode_id ,0 );
			tv_info = (TextView) findViewById(R.id.tv_info);
			spin_img = (Spinner) findViewById(R.id.spin_img);
			spin_format = (Spinner) findViewById(R.id.spin_format);
			spin_compress= (Spinner) findViewById(R.id.spin_compress);
			lv_frame = (ListView) findViewById(R.id.list_frame);
			btn_save = (Button) findViewById(R.id.btn_save);
			prefs = getSharedPreferences(MainActivity.PREF_FILE_NAME, MODE_PRIVATE);

			adapter_frame = new CheckboxListAdapter(
					getLayoutInflater());
			lv_frame.setAdapter(adapter_frame);

			int number_image = Data.IMAGE_SIZE.length;
			List<String> img_temp_list =  new ArrayList<String>();
			for (int i = 0; i < number_image ; i++) {
				img_temp_list.add("IMG "+i);
			}
			ArrayAdapter<String> adapter_image = new ArrayAdapter<String>(
					this, android.R.layout.simple_spinner_item, img_temp_list);
			adapter_image.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
				}

				@Override
				public void onNothingSelected(AdapterView<?> parentView) 
				{

				}
			});
			spin_img.setAdapter(adapter_image);
			spin_img.setSelection(0);	


			String[] format_data = {"Byte Buffer","My Comoress"};
			List<String> format_temp_list = new ArrayList<String>(Arrays.asList(format_data));
			ArrayAdapter<String> format_temp_adapter = new ArrayAdapter<String>(
					this, android.R.layout.simple_spinner_item, format_temp_list);
			format_temp_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spin_format.setAdapter(format_temp_adapter);
			spin_format.setSelection(0);	
			spin_format.setVisibility(View.VISIBLE);


			compress_adapter = new ArrayAdapter<String>(
					this, android.R.layout.simple_spinner_item, new ArrayList<String>());
			format_temp_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spin_compress.setAdapter(compress_adapter);

			format_adapter = new ArrayAdapter<String>(
					this, android.R.layout.simple_spinner_item, new ArrayList<String>());
			format_temp_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spin_format.setAdapter(format_adapter);


			switch(mode){
			case MainActivity.bitmap_arr :
				setBitmapArrLayout();
				break;
			case MainActivity.int_mat:
				setIntMatLayout();
				break;
			}

			File folder = new File(MainActivity.media_path);
			if (!folder.exists())
				folder.mkdirs();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.record, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		boolean [] frames = adapter_frame.getChecked();
		int frame_id = -1 ; 
		for (int i = 0; i < frames.length; i++) {
			if(frames[i])
			{
				frame_id = i ;
				break ;
			}
		}	
		
		int id = item.getItemId();
		
		if (id == R.id.action_show_bitmap) {
			if(frame_id != -1){
				Intent intent = new Intent(RecordActivity.this, ShowBitmapActivity.class)
				.putExtra(ShowBitmapActivity.img_mode, spin_img.getSelectedItemPosition())
				.putExtra(ShowBitmapActivity.frame_mode,frame_id);
				startActivity(intent);
			}
			
		}
		else if (id == R.id.action_frame_info){
			if(frame_id != -1){
				Intent intent = new Intent(RecordActivity.this, FrameInfoActivity.class)
				.putExtra(FrameInfoActivity.img_mode, spin_img.getSelectedItemPosition())
				.putExtra(FrameInfoActivity.frame_mode,frame_id);
				startActivity(intent);
			}
		}
		else if (id == R.id.action_show_chart){
			if(frame_id != -1){
				Intent intent = new Intent(RecordActivity.this, LineChartActivity.class)
				.putExtra(FrameInfoActivity.img_mode, spin_img.getSelectedItemPosition())
				.putExtra(FrameInfoActivity.frame_mode,frame_id)
				.putExtra(LineChartActivity.show_mode,LineChartActivity.show_fragment);
				startActivity(intent);
			}
		}
		else if(id == R.id.action_record_benchmark)
		{
			Intent intent = new Intent(RecordActivity.this, RecordBenchmarkActivity.class)
			.putExtra(FrameInfoActivity.img_mode, spin_img.getSelectedItemPosition())
			.putExtra(FrameInfoActivity.frame_mode,frames);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	private void setIntMatLayout(){

		tv_info.setText(int_mat_info);

		compress_adapter.clear();
		compress_adapter.addAll(ZipManager.int_manipulation_data);
		spin_compress.setSelection(0);	
		spin_compress.setVisibility(View.VISIBLE);

		format_adapter.clear();
		format_adapter.addAll(ZipManager.int_convert_data);
		spin_format.setSelection(0);	
		spin_format.setVisibility(View.VISIBLE);

		btn_save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int pic_id = prefs.getInt("pic_id", 0 ) ;
				int img_id = spin_img.getSelectedItemPosition();
				boolean [] frame_id = adapter_frame.getChecked() ;
				int format_id = spin_format.getSelectedItemPosition();
				int compress_id = spin_compress.getSelectedItemPosition();
				new ZipManager.SaveMatArr(RecordActivity.this,img_id,frame_id,pic_id,format_id,compress_id,false).execute();
				prefs.edit().putInt("pic_id", pic_id+1).commit();
			}
		});

	}

	private void setBitmapArrLayout(){

		tv_info.setText(bitmap_arr_info);

		compress_adapter.clear();
		spin_compress.setVisibility(View.GONE);

		format_adapter.clear();
		format_adapter.addAll(ZipManager.bitmap_format_data);
		spin_format.setSelection(0);	
		spin_format.setVisibility(View.VISIBLE);

		btn_save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int pic_id = prefs.getInt("pic_id", 0 ) ;
				int img_id = spin_img.getSelectedItemPosition();
				boolean [] frame_id = adapter_frame.getChecked() ;
				int format_id = spin_format.getSelectedItemPosition();
				new ZipManager.SaveBitmap(RecordActivity.this, pic_id, img_id, frame_id, format_id).execute();
				int size = 0 ;
				for (int i = 0; i < frame_id.length; i++) {
					if(frame_id[i])size++;
				}
				prefs.edit().putInt("pic_id", pic_id+size).commit();
			}
		});
	}

	public class CheckboxListAdapter extends BaseAdapter implements OnClickListener {

		/** The inflator used to inflate the XML layout */
		private LayoutInflater inflator;

		/** A list containing some sample data to show. */
		private List<SampleData> dataList;

		public CheckboxListAdapter(LayoutInflater inflator) {
			super();
			this.inflator = inflator;
			dataList = new ArrayList<SampleData>()  ;
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
			notifyDataSetChanged();
		}
		public void clear() {
			dataList.clear();
			notifyDataSetChanged();
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
