package com.example.finalproject;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.utility.Data;
import com.example.utility.EventManager;
import com.example.utility.RangeSeekBar;
import com.example.utility.RangeSeekBar.OnRangeSeekBarChangeListener;

public class ObjectDetectionActivity extends ActionBarActivity {


	private TextView tv_info ;
	private Spinner spin_img ;
	private Spinner spin_frame ;
	private CheckBox cb1 ;
	private CheckBox cb2 ;
	private CheckBox cb3 ;
	private Button btn_min_change_up  ;
	private Button btn_min_change_down  ;
	private Button btn_max_change_up  ;
	private Button btn_max_change_down  ;
	private RelativeLayout rangebar ;
	private TextView tv_max  ;
	private TextView tv_min  ;
	private RangeSeekBar<Integer> seekBar ;
	private Button btn_calc  ;

	private ArrayAdapter<String> adapter_frame  ;

	private final static int abs_max = 9000 ;
	private final static int abs_min = 500 ;
	private int min = abs_min ;
	private int max = abs_max;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_object_detection);

		tv_info = (TextView)findViewById(R.id.tv_info);
		spin_img = (Spinner)findViewById(R.id.spin_img);
		spin_frame = (Spinner)findViewById(R.id.spin_frame);
		cb1 = (CheckBox)findViewById(R.id.cb_1);
		cb2 = (CheckBox)findViewById(R.id.cb_2);
		cb3 = (CheckBox)findViewById(R.id.cb_3);
		btn_min_change_up  = (Button)findViewById(R.id.btn_rangebar_min_up);
		btn_min_change_down  = (Button)findViewById(R.id.btn_rangebar_min_down);
		btn_max_change_up  = (Button)findViewById(R.id.btn_rangebar_max_up);
		btn_max_change_down  = (Button)findViewById(R.id.btn_rangebar_max_down);
		rangebar = (RelativeLayout)findViewById(R.id.bar_rangebar);
		tv_max = (TextView)findViewById(R.id.tv_rangebar_max);
		tv_min = (TextView)findViewById(R.id.tv_rangebar_min);
		btn_calc = (Button)findViewById(R.id.btn_calc);
		tv_info.setText("hello");

		adapter_frame = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, new ArrayList<String>() );
		adapter_frame.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_frame.setAdapter(adapter_frame);
		spin_frame.setSelection(0);

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
		spin_img.setOnItemSelectedListener(new OnItemSelectedListener() 
		{
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
			{	

				adapter_frame.clear();
				int numbers_frames = Data.IMAGE_SIZE[position];

				for (int i = 1; i < numbers_frames ; i++) {
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

		// create RangeSeekBar as Integer range between 500 and 9000
		seekBar = new RangeSeekBar<Integer>(abs_min, abs_max, ObjectDetectionActivity.this );
		seekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
			@Override
			public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
				// handle changed range values
				min = minValue;
				max = maxValue;
				tv_min.setText(""+ (min /100.0) );
				tv_max.setText(""+ (max /100.0) );
			}
		});
		// add RangeSeekBar to pre-defined layout
		rangebar.addView(seekBar);

		btn_max_change_up.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(max<abs_max){
					max++ ;
					seekBar.setSelectedMaxValue(max);
					tv_max.setText(""+ (max /100.0) );
				}
			}
		});
		btn_max_change_down.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(max > min){
					max-- ;
					seekBar.setSelectedMaxValue(max);
					tv_max.setText(""+ (max /100.0) );
				}
			}
		});
		btn_min_change_up.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(min<max){
					min++;
					seekBar.setSelectedMinValue(min);
					tv_min.setText(""+ (min /100.0) );
				}
			}
		});
		btn_min_change_down.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(abs_min<min){
					min--;
					seekBar.setSelectedMinValue(min);
					tv_min.setText(""+ (min /100.0) );
				}
			}
		});
		btn_calc.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				int img_id = spin_img.getSelectedItemPosition();
				int frame_id = spin_frame.getSelectedItemPosition()+1;
				boolean [] fillters = {cb1.isChecked(),cb2.isChecked(),cb3.isChecked()};
				new EventManager(ObjectDetectionActivity.this, img_id, frame_id, fillters, min, max).execute();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.object_detection, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		int id = item.getItemId();

		if (id == R.id.action_show_bitmap) {
			Intent intent = new Intent(ObjectDetectionActivity.this, ShowBitmapActivity.class)
			.putExtra(ShowBitmapActivity.img_mode, spin_img.getSelectedItemPosition())
			.putExtra(ShowBitmapActivity.frame_mode,spin_frame.getSelectedItemPosition()+1);
			startActivity(intent);		
		}
		else if (id == R.id.action_frame_info){
			Intent intent = new Intent(ObjectDetectionActivity.this, FrameInfoActivity.class)
			.putExtra(ShowBitmapActivity.img_mode, spin_img.getSelectedItemPosition())
			.putExtra(ShowBitmapActivity.frame_mode,spin_frame.getSelectedItemPosition()+1);
			startActivity(intent);
		}
		else if (id == R.id.action_show_chart){
			Intent intent = new Intent(ObjectDetectionActivity.this, LineChartActivity.class)
			.putExtra(ShowBitmapActivity.img_mode, spin_img.getSelectedItemPosition())
			.putExtra(ShowBitmapActivity.frame_mode,spin_frame.getSelectedItemPosition()+1)
			.putExtra(LineChartActivity.show_mode,LineChartActivity.show_fragment);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}




}
