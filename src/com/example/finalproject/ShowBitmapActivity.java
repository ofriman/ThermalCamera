package com.example.finalproject;

import java.io.IOException;
import java.io.InputStream;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utility.Data;

public class ShowBitmapActivity extends ActionBarActivity {

	public static final String img_mode = "img_mode";
	public static final String frame_mode = "frame_mode";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_bitmap);

		Intent intent =  getIntent();
		if (intent != null ) {
			int img_id = intent.getIntExtra(img_mode,0 );
			int frame_id = intent.getIntExtra(frame_mode,0 );
			TextView tv_bmp = (TextView) findViewById(R.id.tv_bmp);
			ImageView iv_bmp = (ImageView) findViewById(R.id.iv_bmp);
			tv_bmp.setText("IMG: "+img_id+" FRAME: "+frame_id);
			try {
				InputStream ims = getAssets().open(img_id+"/"+Data.IMAGE_INDEX[frame_id]);
				iv_bmp.setImageBitmap(BitmapFactory.decodeStream(ims));
				ims.close();
			} catch (IOException e) {
				e.printStackTrace();
				Toast.makeText(ShowBitmapActivity.this,"error , loading bitmap.", 
						Toast.LENGTH_LONG).show();
			}
			
		}
	}
}
