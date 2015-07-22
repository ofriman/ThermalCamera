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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utility.Data;
import com.example.utility.RangeSeekBar;
import com.example.utility.RangeSeekBar.OnRangeSeekBarChangeListener;

public class MyHistogramActivity extends ActionBarActivity {

	private TextView tv_res   ;
	private Spinner spin_img  ;
	private Spinner spin_frame  ;
	private ArrayAdapter<String> adapter_frame  ;

	private TextView tv_max1;
	private TextView tv_min1;
	private TextView tv_max2;
	private TextView tv_min2;
	private TextView tv_max3;
	private TextView tv_min3;
	private Button btn_min_change_1_up  ;
	private Button btn_min_change_1_down  ;
	private Button btn_max_change_1_up  ;
	private Button btn_max_change_1_down  ;
	private Button btn_min_change_2_up  ;
	private Button btn_min_change_2_down  ;
	private Button btn_max_change_2_up  ;
	private Button btn_max_change_2_down  ;
	private Button btn_min_change_3_up  ;
	private Button btn_min_change_3_down  ;
	private Button btn_max_change_3_up  ;
	private Button btn_max_change_3_down  ;

	private RelativeLayout rangebar1;
	private RelativeLayout rangebar2;
	private RelativeLayout rangebar3;

	private Button btn_calculate ;
	private RangeSeekBar<Integer> seekBar1;
	private RangeSeekBar<Integer> seekBar2;
	private RangeSeekBar<Integer> seekBar3;

	private static final int TEMPATURE_ABSOLUTE_MINIMUM = Data.camera_temperature_absolute_minimum ;
	private static final int TEMPATURE_ABSOLUTE_MAXIMUM = Data.camera_temperature_absolute_maximum ;
	/*
	 * USER_TEMPATURE_ABSOLUTE_MINIMUM , USER_TEMPATURE_MINIMUM
	 * USER_TEMPATURE_ABSOLUTE_MAXIMUM , USER_TEMPATURE_MAXIMUM
	 */
	private int [][] tempature_values = {          
			{  TEMPATURE_ABSOLUTE_MINIMUM,  TEMPATURE_ABSOLUTE_MINIMUM } ,
			{ TEMPATURE_ABSOLUTE_MAXIMUM , TEMPATURE_ABSOLUTE_MAXIMUM}  
	} ;
	private void setTempAbsoluteMin(int min){
		if ( min <=  tempature_values[0][1] )
			tempature_values[0][0]=min;
		else
			tempature_values[0][0]=tempature_values[0][1];
	}
	private void setTempAbsoluteMax(int max){
		if ( max >=  tempature_values[1][1] )
			tempature_values[1][0]=max;
		else
			tempature_values[1][0]=tempature_values[1][1];
	}
	private void setTempMin(int min){
		if ( tempature_values[0][0] <= min )
			tempature_values[0][1]=min;
		else
			tempature_values[0][1]= tempature_values[0][0];
	}
	private void setTempMax(int max){
		if ( tempature_values[1][0] >= max )
			tempature_values[1][1]=max;
		else
			tempature_values[1][1]= tempature_values[1][0];
	}
	private int getTempAbsoluteMin(){return tempature_values[0][0];}
	private int getTempAbsoluteMax(){return tempature_values[1][0];}
	private int getTempMin(){return tempature_values[0][1];}
	private int getTempMax(){return tempature_values[1][1];}

	private static final int VALUE_ABSOLUTE_MINIMUM = Data.value_absolute_minimum ;
	private static final int VALUE_ABSOLUTE_MAXIMUM = Data.value_absolute_maximum ;		
	/*
	 * USER_VALUE_MINIMUM 
	 * USER_VALUE_MINIMUM 
	 */
	private int [][] values = {          
			{  VALUE_ABSOLUTE_MINIMUM } ,
			{  VALUE_ABSOLUTE_MAXIMUM }  
	} ;
	private void setValueMin(int min){values[0][0]=min;}
	private void setValueMax(int max){values[1][0]=max;}
	private int getValueMin(){return values[0][0];}
	private int getValueMax(){return values[1][0];}	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_histogram);


		spin_img = (Spinner)findViewById(R.id.spin_img);
		spin_frame = (Spinner)findViewById(R.id.spin_frame);

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

				for (int i = 0 ; i < numbers_frames ; i++) {
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

		tv_max1 = (TextView) findViewById(R.id.tv_rangebar1_max);
		tv_min1 = (TextView) findViewById(R.id.tv_rangebar1_min);

		btn_min_change_1_down= (Button) findViewById(R.id.btn_rangebar1_min_down);
		btn_min_change_1_down.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int min = getTempAbsoluteMin();
				if(min > TEMPATURE_ABSOLUTE_MINIMUM ){
					setTempAbsoluteMin(--min);
					seekBar1.setSelectedMinValue(min);
					updateViewValues();

				}
			}
		});
		btn_min_change_1_up= (Button) findViewById(R.id.btn_rangebar1_min_up);
		btn_min_change_1_up.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int min = getTempAbsoluteMin();
				if(min < getTempMin()){
					setTempAbsoluteMin(++min);
					seekBar1.setSelectedMinValue(min);
					updateViewValues();
				}
			}
		});
		btn_max_change_1_down = (Button) findViewById(R.id.btn_rangebar1_max_down);
		btn_max_change_1_down.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int max = getTempAbsoluteMax();
				if(max >getTempMax()){
					setTempAbsoluteMax(--max);
					seekBar1.setSelectedMaxValue(max);
					updateViewValues();
				}
			}
		});
		btn_max_change_1_up = (Button) findViewById(R.id.btn_rangebar1_max_up);
		btn_max_change_1_up.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int max =  getTempAbsoluteMax();
				if(max < TEMPATURE_ABSOLUTE_MAXIMUM){
					setTempAbsoluteMax(++max);
					seekBar1.setSelectedMaxValue(max);
					updateViewValues();
				}
			}
		});

		seekBar1 = new RangeSeekBar<Integer>(TEMPATURE_ABSOLUTE_MINIMUM, TEMPATURE_ABSOLUTE_MAXIMUM, MyHistogramActivity.this);
		seekBar1.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
			@Override
			public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
				setTempAbsoluteMin(minValue);
				setTempAbsoluteMax(maxValue);
				updateViewValues();
			}
		});
		rangebar1 = (RelativeLayout) findViewById(R.id.bar_rangebar1);
		rangebar1.addView(seekBar1);

		tv_max2 = (TextView) findViewById(R.id.tv_rangebar2_max);
		tv_min2 = (TextView) findViewById(R.id.tv_rangebar2_min);
		btn_min_change_2_down= (Button) findViewById(R.id.btn_rangebar2_min_down);
		btn_min_change_2_down.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int min = getTempMin();
				if(min > getTempAbsoluteMin() ){
					setTempMin(--min);
					seekBar2.setSelectedMinValue(min);
					updateViewValues();
				}
			}
		});
		btn_min_change_2_up= (Button) findViewById(R.id.btn_rangebar2_min_up);
		btn_min_change_2_up.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int min = getTempMin();
				if(min < getTempMax()){
					setTempMin(++min);
					seekBar2.setSelectedMinValue(min);
					updateViewValues();

				}
			}
		});
		btn_max_change_2_down = (Button) findViewById(R.id.btn_rangebar2_max_down);
		btn_max_change_2_down.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int max = getTempMax();
				if(max > getTempMin()){
					setTempMax(--max);
					seekBar2.setSelectedMaxValue(max);
					updateViewValues();
				}
			}
		});
		btn_max_change_2_up = (Button) findViewById(R.id.btn_rangebar2_max_up);
		btn_max_change_2_up.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int max = getTempMax();
				if(max < getTempAbsoluteMax() ){
					setTempMax(++max);
					seekBar2.setSelectedMaxValue(max);
					updateViewValues();
				}
			}
		});

		seekBar2 = new RangeSeekBar<Integer>(TEMPATURE_ABSOLUTE_MINIMUM, TEMPATURE_ABSOLUTE_MAXIMUM, MyHistogramActivity.this);
		seekBar2.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
			@Override
			public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
				setTempMin(minValue);
				setTempMax(maxValue);
				updateViewValues();
			}
		});
		rangebar2 = (RelativeLayout) findViewById(R.id.bar_rangebar2);
		rangebar2.addView(seekBar2);

		tv_max3 = (TextView) findViewById(R.id.tv_rangebar3_max);
		tv_min3 = (TextView) findViewById(R.id.tv_rangebar3_min);
		btn_min_change_3_down = (Button) findViewById(R.id.btn_rangebar3_min_down);
		btn_min_change_3_down.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int min = getValueMin();
				if(min > VALUE_ABSOLUTE_MINIMUM ){
					setValueMin(--min);
					seekBar3.setSelectedMinValue(min);
					updateViewValues();	
				}
			}
		});
		btn_min_change_3_up = (Button) findViewById(R.id.btn_rangebar3_min_up);
		btn_min_change_3_up.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int min = getValueMin();
				if(min < getValueMax()){
					setValueMin(++min);
					seekBar3.setSelectedMinValue(min);
					updateViewValues();	
				}
			}
		});
		btn_max_change_3_up = (Button) findViewById(R.id.btn_rangebar3_max_up);
		btn_max_change_3_up.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int max = getValueMax();
				if(max < VALUE_ABSOLUTE_MAXIMUM ){
					setValueMax(++max);
					seekBar3.setSelectedMaxValue(max);
					updateViewValues();	
				}
			}
		});
		btn_max_change_3_down = (Button) findViewById(R.id.btn_rangebar3_max_down);
		btn_max_change_3_down.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int max = getValueMax();
				if(max > getValueMin()){
					setValueMax(--max);
					seekBar3.setSelectedMaxValue(max);
					updateViewValues();	
				}
			}
		});

		seekBar3 = new RangeSeekBar<Integer>(VALUE_ABSOLUTE_MINIMUM, VALUE_ABSOLUTE_MAXIMUM,MyHistogramActivity.this);
		seekBar3.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
			@Override
			public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
				setValueMin(minValue);
				setValueMax(maxValue);
				updateViewValues();
			}
		});
		rangebar3 = (RelativeLayout) findViewById(R.id.bar_rangebar3);
		rangebar3.addView(seekBar3);

		tv_res = (TextView)findViewById(R.id.tv_res);

		btn_calculate = (Button) findViewById(R.id.btn_start);
		btn_calculate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
								Intent intent = new Intent(MyHistogramActivity.this, PreviewImgActivity.class)
								.putExtra(PreviewImgActivity.min0, getTempAbsoluteMin())
								.putExtra(PreviewImgActivity.min1,  getTempAbsoluteMax())
								.putExtra(PreviewImgActivity.min2, getTempMin())
								.putExtra(PreviewImgActivity.min3, getTempMax())
								.putExtra(PreviewImgActivity.min4, getValueMin())
								.putExtra(PreviewImgActivity.min5, getValueMax()) ;
								startActivity(intent);
			}
		}); 
		updateViewValues();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_histogram, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		int id = item.getItemId();

		if (id == R.id.action_show_bitmap) {
			Intent intent = new Intent(MyHistogramActivity.this, ShowBitmapActivity.class)
			.putExtra(ShowBitmapActivity.img_mode, spin_img.getSelectedItemPosition())
			.putExtra(ShowBitmapActivity.frame_mode,spin_frame.getSelectedItemPosition()+1);
			startActivity(intent);		
		}
		else if (id == R.id.action_frame_info){
			Intent intent = new Intent(MyHistogramActivity.this, FrameInfoActivity.class)
			.putExtra(ShowBitmapActivity.img_mode, spin_img.getSelectedItemPosition())
			.putExtra(ShowBitmapActivity.frame_mode,spin_frame.getSelectedItemPosition()+1);
			startActivity(intent);
		}
		else if (id == R.id.action_show_chart){
			Intent intent = new Intent(MyHistogramActivity.this, LineChartActivity.class)
			.putExtra(ShowBitmapActivity.img_mode, spin_img.getSelectedItemPosition())
			.putExtra(ShowBitmapActivity.frame_mode,spin_frame.getSelectedItemPosition()+1)
			.putExtra(LineChartActivity.show_mode,LineChartActivity.show_fragment);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}


	private void updateViewValues(){

		tv_min1.setText("MIN:"+getTempAbsoluteMin());
		tv_max1.setText("MAX:"+getTempAbsoluteMax());
		tv_min2.setText("MIN:"+getTempMin());
		tv_max2.setText("MAX:"+getTempMax());
		tv_min3.setText("MIN:"+getValueMin());
		tv_max3.setText("MAX:"+getValueMax());

		tv_res.setText("tempature: "+getTempAbsoluteMin()+" to "+getTempMin()+" value: "+VALUE_ABSOLUTE_MINIMUM+" to "+getValueMin() +"\n"+
				"tempature: "+getTempMin()+" to "+getTempMax()+" value: "+getValueMin()+" to "+getValueMax() +"\n"+
				"tempature: "+getTempMax()+" to "+getTempAbsoluteMax()+" value: "+getValueMax()+" to "+VALUE_ABSOLUTE_MAXIMUM);	
	}

}
