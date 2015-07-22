package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.example.utility.LoadInfoManager;

public class FrameInfoActivity extends ActionBarActivity {

	public static final String img_mode = "img_mode";
	public static final String frame_mode = "frame_mode";

	private int img_id = -1 ; 
	private int frame_id = -1 ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frame_info);

		Intent intent =  getIntent();
		if (intent != null ) {
			 img_id = intent.getIntExtra(img_mode,0 );
			 frame_id = intent.getIntExtra(frame_mode,0 );
		}


	}
	@Override
	public void onStart() {
		super.onStart();
		if(img_id != -1 && frame_id != -1){
			new LoadInfoManager(FrameInfoActivity.this,img_id,frame_id,LoadInfoManager.frame_info_activity).execute();
		}
	}


	

}
