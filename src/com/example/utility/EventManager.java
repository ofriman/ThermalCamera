package com.example.utility;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.boaz.ImageProcessingAlgo;
import com.example.boaz.Image_light;
import com.example.finalproject.ObjectDetectionActivity;
import com.example.finalproject.R;

public  class EventManager extends AsyncTask<Void, String, String> {


	private final static int heat = 0 ;
	private final static int cold = 1 ;
	private final static int pick = 2 ;

	private WeakReference<Activity> weakActivity = null;
	private Activity activity = null ;
	private ProgressDialog dialog = null ;
	private int image_id ;
	private int  frame_id  ;
	private int min  ;
	private int max  ;
	private boolean [] fillters ;
	private Bitmap mutableBitmap ;
	private ArrayList<Image_light> ans;

	public EventManager (Activity context, int image_id , int frame_id, boolean [] fillters,int min,int max) {
		this.weakActivity = new WeakReference<Activity>(context);
		this.image_id = image_id; 
		this.frame_id = frame_id;
		this.fillters=fillters;
		this.min=min;
		this.max=max;
	}

	@Override
	protected String doInBackground(Void... params) {

		if (activity == null) 
		{
			return "error , activity null";
		}

		publishProgress("Loading data ...");

		String txtfile = image_id+"/"+Data.RAW_INDEX[frame_id-1];
		final int [] base_frame = Data.getRawArr(activity , txtfile ) ;
		txtfile = image_id+"/"+Data.RAW_INDEX[frame_id];
		final int [] frame = Data.getRawArr(activity , txtfile ) ;

		if(frame == null || base_frame == null){
			return "error , frame is null "; 
		}


		Bitmap bitmap;		
		int h = Data.camera_pic_width ;
		int w = Data.camera_pic_height;
		try {
			txtfile = image_id+"/"+Data.IMAGE_INDEX[frame_id];
			InputStream ims = activity.getAssets().open(txtfile);
			bitmap = BitmapFactory.decodeStream(ims);
		}
		catch(IOException ex) {
			ex.printStackTrace();
			return  "error bitmap path -- "+txtfile;
		}

		Bitmap workingBitmap = Bitmap.createBitmap(bitmap);
		mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);

		publishProgress("Calculate data ...");

		int a = Color.alpha(Color.BLACK);
		int r = Color.red(Color.BLACK);
		int g = Color.green(Color.BLACK);
		int b = Color.blue(Color.BLACK);


		for (int i = 0; i < frame.length; i++) {
			if(fillters[heat] && base_frame[i]<=frame_id)
			{
				mutableBitmap.setPixel(i%w, ((int)Math.floor((frame.length-1)/w)-(int)Math.floor(i/w)) , Color.argb(a,Color.red(Color.WHITE), g, b));
			}
			else if(fillters[cold] && base_frame[i]>=frame_id)
			{
				mutableBitmap.setPixel(i%w, ( (int)Math.floor((frame.length-1)/w)-(int)Math.floor(i/w)) , Color.argb(a,r, g, Color.blue(Color.WHITE)));
			}
			if (fillters[cold] && fillters[heat] && base_frame[i] == frame_id )
			{
				mutableBitmap.setPixel(i%w, ( (int)Math.floor((frame.length-1)/w)-(int)Math.floor(i/w)) , Color.argb(a,r,Color.green(Color.WHITE), b));
			}
			if(fillters[pick] && min<=frame[i] && frame[i]<=max)
			{
				frame[i]=1 ;
			}
			else{
				frame[i]=0;
			}
		}

		if ( fillters[pick]  ){
			int [][] image = new int [w][h];
			for (int i = 0; i < frame.length; i++) {
				image [i%w][((int)Math.floor((frame.length-1)/w)-(int)Math.floor(i/w)) ] = frame[i];
			}
			ans = ImageProcessingAlgo.Image2Lights(image);

			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(Color.YELLOW);
			paint.setAlpha(255);
			paint.setStyle(Paint.Style.STROKE);
			Canvas canvas = new Canvas(mutableBitmap);

			for (int i = 0; i < ans.size(); i++) {				
				canvas.drawCircle((float)ans.get(i).get_cen().x(),(float)ans.get(i).get_cen().y(),(float)ans.get(i).get_radius() , paint);
			}
		}
		return "finished calc";
	}
	@Override
	protected void onPostExecute(String result) {
		if(activity != null){
			String txt = "" ;
			if(ans != null){
				txt = "" ;
				for (int i = 0; i < ans.size(); i++) {
					txt+=i+") "+ans.get(i)+"\n";
				}
			}

			TextView tv_obj = (TextView)activity.findViewById(R.id.tv_obj);
			ImageView iv_res = (ImageView)activity.findViewById(R.id.iv_res);

			if(iv_res != null && tv_obj != null){
				tv_obj.setText(txt);
				if(mutableBitmap !=null){
					iv_res.setImageBitmap(mutableBitmap);
				}
			}
			else{
				Toast.makeText(activity,"error , view not found", 
						Toast.LENGTH_LONG).show();
			}
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
			dialog.setMessage("Calc...");
			dialog.show();  
		}
	}
	@Override
	protected void onProgressUpdate(String... values) {
		if(dialog !=null)
			dialog.setMessage(values[0]);
	}


}


