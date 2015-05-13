package com.example.finalproject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	public static final String PREF_FILE_NAME = "prefs";
	private static final int compression = 0 ;
	private static final int events = 1 ;
	private static final int image_improvement = 2 ;

	private ArrayAdapter<String> mMenuAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		String[] data = {
				"Compression",
				"Events",
				"Image Improvement"
		};
		List<String> mainMenuRows = new ArrayList<String>(Arrays.asList(data));
		mMenuAdapter =
				new ArrayAdapter<String>(
						this,
						R.layout.list_item,
						R.id.list_item_textview, 
						mainMenuRows);

		ListView listView = (ListView) findViewById(R.id.listview_main_menu);
		listView.setAdapter(mMenuAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {


				switch(position)
				{
				case compression:
					Intent intent = new Intent(MainActivity.this, RecordActivity.class);
					startActivity(intent);
					break ;
				case events:
					Toast.makeText(getApplicationContext(),  mMenuAdapter.getItem(position)+" , not ready ", 
							Toast.LENGTH_LONG).show();
					break ;
				case image_improvement:
					Toast.makeText(getApplicationContext(),  mMenuAdapter.getItem(position)+" , not ready ", 
							Toast.LENGTH_LONG).show();
					break ;
				}
			}
		});


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
