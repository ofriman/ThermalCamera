package com.example.finalproject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class RecordActivity extends ActionBarActivity {

	public static final String flag_id = "mode";
	public static final int bitmap = 0 ;
	public static final int bitmap_arr = 1 ;
	public static final int int_arr = 2 ;
	public static final int int_mat = 3 ;
	private static final int overall_format = 4 ;

	private ArrayAdapter<String> mMenuAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);

		String[] data = {
				"Bitmap",
				"Bimap []",
				"int []",
				"int [][]",
				"overall format"
		};

		List<String> mainMenuRows = new ArrayList<String>(Arrays.asList(data));

		mMenuAdapter =
				new ArrayAdapter<String>(
						this,
						R.layout.list_item,
						R.id.list_item_textview, 
						mainMenuRows);

		ListView listView = (ListView) findViewById(R.id.listview_record_menu);
		listView.setAdapter(mMenuAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

				Intent intent ; 
				switch(position)
				{
				case bitmap:
					intent = new Intent(RecordActivity.this, RecordArrActivity.class).putExtra(flag_id, bitmap);
					startActivity(intent);
					break ;
				case bitmap_arr:
					intent = new Intent(RecordActivity.this, RecordMatActivity.class).putExtra(flag_id, bitmap_arr);
					startActivity(intent);
					break ;
				case int_arr:
					intent = new Intent(RecordActivity.this, RecordArrActivity.class).putExtra(flag_id, int_arr);
					startActivity(intent);
					break ;
				case int_mat:
					intent = new Intent(RecordActivity.this, RecordMatActivity.class).putExtra(flag_id, int_mat);
					startActivity(intent);
					break ;
				case overall_format:
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
		getMenuInflater().inflate(R.menu.record, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		//		int id = item.getItemId();
		//		if (id == R.id.action_settings) {
		//			return true;
		//		}
		return super.onOptionsItemSelected(item);
	}
}
