package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import com.example.utility.ZipManager;

public class RecordBenchmarkActivity extends ActionBarActivity {

	public static final String img_mode = "img_mode";
	public static final String frame_mode = "frame_mode";
	private TextView tv_info  ;
	private int img_id = -1 ; 
	private boolean[] frame_id = null ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frame_info);

		Intent intent =  getIntent();
		if (intent != null ) {
			 img_id = intent.getIntExtra(img_mode,0 );
			 frame_id = intent.getBooleanArrayExtra(frame_mode);
		}
		tv_info = ((TextView) findViewById(R.id.tv_info)) ;
	}
	@Override
	public void onStart() {
		super.onStart();
		if(img_id != -1 && frame_id != null){
			
			for (int i = 0; i < ZipManager.int_convert_data.length; i++) {
				for (int j = 0; j < ZipManager.int_manipulation_data.length; j++) {
							new ZipManager.SaveMatArr(this,img_id,frame_id,0,i,j,true).execute();
					}		
				} 
			}
		else {
			tv_info.setText("error in picking");
		}
	}


	

}
