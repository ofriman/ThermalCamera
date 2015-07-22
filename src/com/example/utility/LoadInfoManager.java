package com.example.utility;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.TextView;

import com.example.finalproject.LineChartActivity;
import com.example.finalproject.PreviewImgActivity;
import com.example.finalproject.R;

public class LoadInfoManager extends AsyncTask<Void, String, String> {

	public static final int frame_info_activity = 0 ;
	public static final int show_chart_activity = 1 ;
	public static final int preview_img_activity = 2;

	private WeakReference<Activity> weakActivity;
	private Activity activity = null ;
	private ProgressDialog dialog = null ;
	private int image_id ;
	private int frames_id ;
	private int mode_id ;
	private int [] res ;
	private int [] frame ;

	public LoadInfoManager (Activity context,int image_id,int frame_id,int mode_id) {
		this.weakActivity = new WeakReference<Activity>(context);	
		this.image_id = image_id;
		this.frames_id = frame_id;
		this.mode_id=mode_id;
	}

	@Override
	protected String doInBackground(Void... params) {

		if (activity == null) {
			return "error , activity is null";		
		}	

		publishProgress("Loading frame ...");

		frame = Data.getRawArr(activity , image_id+"/"+Data.RAW_INDEX[frames_id]);

		if (frame == null) {
			return "error , frame is null";		
		}

		publishProgress("Calculate Data ...");
		
		if(mode_id == preview_img_activity)
		{
			int [] min_max = ((PreviewImgActivity)activity).getMinMax();
			
			if(min_max == null)
				return "error , min_max is null";
			
			Data.convertToImage(frame ,min_max[0], min_max[1],min_max[2],min_max[3],min_max[4], min_max[5]);
			return "finished" ;
		}

		res = new int [8503];

		for (int i = 0; i < frame.length; i++) {
			int temp = frame[i];
			if(temp < 500)
				res[8501]++;
			else if(9000 < temp)
				res[8502]++;
			else
				res[temp]++;
		}

		if(mode_id == show_chart_activity)
		{
			return "finished" ;
		}

		String txt = "" ;
		if(res[8501] > 0){
			txt += "error , there is temp smaller then 5 " ;	
		}
		if(res[8502] > 0){
			txt += "error , there is temp bigger then 90 " ;
		}
		for (int i = 0; i < res.length-2; i++) {
			if ( res[i] > 0 )
			{
				txt += i/100.0+" = "+res[i]+" times\n";
			}
		}

		return txt;
	}

	@Override
	protected void onPostExecute(String result) {
		if(activity != null){
			switch(mode_id){
			case frame_info_activity :
				((TextView) activity.findViewById(R.id.tv_info)).setText(result);
				break;
			case show_chart_activity:
				((LineChartActivity) activity).mfragment.generateValues(res);
				((LineChartActivity) activity).mfragment.generateData();
				break ;
			case preview_img_activity:
				((PreviewImgActivity) activity).setArr(frame);
				break ;
			}
			if(dialog!=null)
				dialog.dismiss();
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