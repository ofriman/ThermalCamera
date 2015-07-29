package com.example.finalproject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity {

	public static final String media_path = Environment
			.getExternalStorageDirectory().getPath() + "/FinalProject/Media";

	public static final String PREF_FILE_NAME = "prefs";
	public static final String mode_id = "mode";

	private static final int main_menu = 0;
	private static final int compression_menu = 1;
	private static final int events_menu = 2;
	private static final int image_improvement_menu = 3;
	private static final int broadcast_menu = 4;

	private static final int compression = 0 ;
	private static final int events = 1 ;
	private static final int image_improvement = 2 ;
	private static final int broadcast = 3 ;
	private static final String [] main_menu_val  = {"Compression","Events","Image Improvement","Broadcast"};


	public static final int bitmap_arr = 0;
	public static final int int_mat = 1;
	private static final String [] compression_menu_val  = {"Bitmap","Heatmap"};

	public static final int object_detection = 0;
	private static final String [] events_menu_val  = {"Object Detection"};

	public static final int histogram = 0;
	private static final String [] image_improvement_menu_val  = {"Histogram"};
	
	public static final int wifi_direct_file_transfer = 0;
	private static final String [] broadcast_menu_val  = {"WiFi-Direct-File-Transfer"};

	private ArrayAdapter<String> mMenuAdapter ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Intent intent =  getIntent();
		if (intent != null ) {

			int	mode = intent.getIntExtra(MainActivity.mode_id ,0 );
			ListView listView = (ListView) findViewById(R.id.listview_main_menu);
			String[] data ;

			switch(mode){
			case compression_menu :
				data = compression_menu_val ;
				listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
						Intent intent ;
						switch(position)
						{
						case bitmap_arr:
							intent = new Intent(MainActivity.this, RecordActivity.class).putExtra(mode_id, bitmap_arr);
							startActivity(intent);
							break ;
						case int_mat:
							intent = new Intent(MainActivity.this, RecordActivity.class).putExtra(mode_id, int_mat);
							startActivity(intent);
							break ;
						}
					}
				});
				break ;
			case events_menu :
				data = events_menu_val ;
				listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
						Intent intent ;
						switch(position)
						{
						case object_detection:
							intent = new Intent(MainActivity.this, ObjectDetectionActivity.class);
							startActivity(intent);
							break ;
						}
					}
				});
				break ;
			case image_improvement_menu :
				data = image_improvement_menu_val ;
				listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
						Intent intent ;
						switch(position)
						{
						case histogram:
							intent = new Intent(MainActivity.this, MyHistogramActivity.class);
							startActivity(intent);
							break ;
						}
					}
				});
				break ;
			case broadcast_menu:
				data = broadcast_menu_val ;
				listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
						Intent intent ;
						switch(position)
						{
						case wifi_direct_file_transfer:
							intent = new Intent(MainActivity.this, TransferActivity.class);
							startActivity(intent);
							break ;
						}
					}
				});
				break;
			case main_menu :
			default :
				data = main_menu_val ;
				listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
						Intent intent ;
						switch(position)
						{
						case compression:
							intent = new Intent(MainActivity.this, MainActivity.class).putExtra(mode_id, compression_menu);
							startActivity(intent);
							break ;
						case events:
							intent = new Intent(MainActivity.this, MainActivity.class).putExtra(mode_id, events_menu);
							startActivity(intent);
							break ;
						case image_improvement:
							intent = new Intent(MainActivity.this, MainActivity.class).putExtra(mode_id, image_improvement_menu);
							startActivity(intent);
							break ;
						case broadcast:
							intent = new Intent(MainActivity.this, MainActivity.class).putExtra(mode_id, broadcast_menu);
							startActivity(intent);
							break;
						}
					}
				});

				break ;
			}

			List<String> mainMenuRows = new ArrayList<String>(Arrays.asList(data));

			mMenuAdapter =
					new ArrayAdapter<String>(
							this,
							R.layout.list_item,
							R.id.list_item_textview, 
							mainMenuRows);

			listView.setAdapter(mMenuAdapter);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		//int id = item.getItemId();
		//		if (id == R.id.action_settings) {
		//			return true;
		//		}
		return super.onOptionsItemSelected(item);
	}
}
