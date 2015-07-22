package com.example.finalproject;


import java.lang.ref.WeakReference;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.utility.Data;
import com.example.utility.LoadInfoManager;

public class PreviewImgActivity extends ActionBarActivity {

	public static final String min0 = "min0";
	public static final String min1 = "min1";
	public static final String min2 = "min2";
	public static final String min3 = "min3";
	public static final String min4 = "min4";
	public static final String min5 = "min5";

	public static final String img_mode = "img_mode";
	public static final String frame_mode = "frame_mode";

	private Spinner spin_color = null ;
	private CheckBox negative_box = null ;
	private int [] arr ;
	private int [] min_max ;
	private int img_id ;
	private int frame_id ;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview_img);

		Intent intent =  getIntent();
		if (intent != null && 
				intent.hasExtra(min0) && 
				intent.hasExtra(min1) &&
				intent.hasExtra(min2) &&
				intent.hasExtra(min3) &&
				intent.hasExtra(min4) &&
				intent.hasExtra(min5)  ) {

			min_max = new int [6] ;
			
			min_max[0]=			intent.getIntExtra(min0,0);
			min_max[1]=			intent.getIntExtra(min1,0);
			min_max[2]=			intent.getIntExtra(min2,0);
			min_max[3]=			intent.getIntExtra(min3,0);
			min_max[4]=			intent.getIntExtra(min4,0);
			min_max[5]=			intent.getIntExtra(min5,0);

			img_id = intent.getIntExtra(img_mode,0);
			frame_id = intent.getIntExtra(frame_mode,0 );


			spin_color =(Spinner)findViewById(R.id.spin_color);
			spin_color.setSelection(0);
			spin_color.setOnItemSelectedListener(new OnItemSelectedListener() 
			{
				@Override
				public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
				{
					new CalculateImg(PreviewImgActivity.this,spin_color.getSelectedItemPosition(),negative_box.isChecked()).execute();
					
				}

				@Override
				public void onNothingSelected(AdapterView<?> parentView) {
				}
			});
			negative_box = (CheckBox)findViewById(R.id.cb_negative);
			negative_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
					new CalculateImg(PreviewImgActivity.this,spin_color.getSelectedItemPosition(),negative_box.isChecked()).execute();
					
				}
			}
					);  
			new LoadInfoManager(PreviewImgActivity.this,img_id,frame_id,LoadInfoManager.preview_img_activity).execute();
		}

	}
	public void setArr(int [] arr){this.arr=arr;}
	public int[] getArr(){return this.arr;}
	public int[] getMinMax(){return this.min_max;}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.preview_img, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	public static class CalculateImg extends AsyncTask<Void, String, String> {

		private WeakReference<PreviewImgActivity> weakActivity;
		private PreviewImgActivity activity ;
		private ProgressDialog dialog  ;
		private ImageView  iv_res ;
		private int color_type ;
		private boolean cb_checked ;
		private Bitmap bitmap ;
		
		public CalculateImg (PreviewImgActivity context,int color_type,boolean cb_checked ) {
			this.weakActivity = new WeakReference<PreviewImgActivity>(context);	
			this.color_type=color_type;
			this.cb_checked=cb_checked;
		}

		@Override
		protected String doInBackground(Void... params) {

			if (activity == null) {
				return "error , activity is null";		
			}	

			int [] arr = activity.getArr() ;

			if(arr == null)
				return "error , frame is null";	

			publishProgress("Calculate pixels ...");

			// opsite saved values ??
			int h = Data.camera_pic_width ;
			int w = Data.camera_pic_height;

			 bitmap = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);

			int color,color1, a, r, g, b;

			if(cb_checked)
			{
				color = Color.WHITE;	
				color1 = Color.BLACK;
			}
			else
			{
				color = Color.BLACK;	
				color1 = Color.WHITE;
			}


			for (int i = 0; i < arr.length; i++) {

				a = Color.alpha(color);
				r = Color.red(color);
				g = Color.green(color);
				b = Color.blue(color);


				if ( arr[i] >  511 )
				{
					a = Color.alpha(Color.BLACK);
					r = Math.abs(Color.red(color) - (arr[i] -512) );
					g = Color.green(Color.BLACK);
					b = Color.blue(Color.BLACK);
				}
				else if ( arr[i] >  255)
				{
					a = Color.alpha(Color.BLACK);
					r = Color.red(Color.BLACK);
					g = Color.green(Color.BLACK);
					b = Math.abs(Color.blue(color1) - (arr[i]-256) );
				}
				else {

					switch(color_type){
					case 0:
						a = Math.abs(Color.alpha(color)-arr[i]);
						break ;
					case 1:
						g = Math.abs(Color.green(color)-arr[i]);
						break ;
					}

				}

				/*
			 float xyTemp = Temperatures[y * height + x]; H3884 W288
			X384    ..  X767
				X1 X2 X3 .. X383
				 */

				bitmap.setPixel(i%w, ( (int)Math.floor((arr.length-1)/w)-(int)Math.floor(i/w)) , Color.argb(a, r, g, b));
			}
			

			return "finished";
		}

		@Override
		protected void onPostExecute(String result) {
			if(activity != null){
				if(iv_res!=null)
				iv_res.setImageBitmap(bitmap);
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
				iv_res = (ImageView)activity.findViewById(R.id.bitmap_preview);
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
