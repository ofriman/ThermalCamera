package com.example.finalproject;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.animation.ChartAnimationListener;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.Chart;
import lecho.lib.hellocharts.view.LineChartView;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.utility.LoadInfoManager;

public class LineChartActivity extends ActionBarActivity {

	public static final String img_mode = "img_mode";
	public static final String frame_mode = "frame_mode";
	public static final String show_mode = "show_mode";

	public static final int show_fragment = 0 ;

	private int show_id = -1 ; 
	private int img_id = -1 ; 
	private int frame_id = -1 ;
	public PlaceholderFragment mfragment ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_line_chart);

		Intent intent =  getIntent();
		if (intent != null ) {
			img_id = intent.getIntExtra(img_mode,0 );
			show_id = intent.getIntExtra(show_mode,0 );
			frame_id = intent.getIntExtra(frame_mode,0 );
		}
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container,mfragment =  new PlaceholderFragment()).commit();
		}

	}
	@Override
	public void onStart() {
		super.onStart();

		switch(show_id){
		case show_fragment:
			if(img_id != -1 && frame_id != -1){
				new LoadInfoManager(LineChartActivity.this,img_id,frame_id,LoadInfoManager.show_chart_activity).execute();
			}
			break;
		}
	}
	/**
	 * A fragment containing a line chart.
	 */
	public static class PlaceholderFragment extends Fragment {

		private LineChartView chart;
		private LineChartData data;
		private int numberOfLines = 1;
		private int maxNumberOfLines = 4;
		private int numberOfPoints = 12;
		private int [] numbers ; 

		private boolean hasAxes = true;
		private boolean hasAxesNames = true;
		private boolean hasLines = true;
		private boolean hasPoints = true;
		private ValueShape shape = ValueShape.CIRCLE;
		private boolean isFilled = false;
		private boolean hasLabels = false;
		private boolean isCubic = false;
		private boolean hasLabelForSelected = false;

		public PlaceholderFragment() {
		}
		public void generateValues(int [] numbers)
		{
			this.numbers=numbers;
			numberOfLines = 1;
			numberOfPoints = numbers.length -2 ;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			setHasOptionsMenu(true);
			View rootView = inflater.inflate(R.layout.fragment_line_chart, container, false);

			chart = (LineChartView) rootView.findViewById(R.id.chart);
			chart.setOnValueTouchListener(new ValueTouchListener());

			// Disable viewpirt recalculations, see toggleCubic() method for more info.
			chart.setViewportCalculationEnabled(false);

			return rootView;
		}

		// MENU
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			inflater.inflate(R.menu.line_chart, menu);
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			int id = item.getItemId();
			if (id == R.id.action_reset) {
				reset();
				generateData();
				return true;
			}
			if (id == R.id.action_add_line) {
				addLineToData();
				return true;
			}
			if (id == R.id.action_toggle_lines) {
				toggleLines();
				return true;
			}
			if (id == R.id.action_toggle_points) {
				togglePoints();
				return true;
			}
			if (id == R.id.action_toggle_cubic) {
				toggleCubic();
				return true;
			}
			if (id == R.id.action_toggle_area) {
				toggleFilled();
				return true;
			}
			if (id == R.id.action_shape_circles) {
				setCircles();
				return true;
			}
			if (id == R.id.action_shape_square) {
				setSquares();
				return true;
			}
			if (id == R.id.action_shape_diamond) {
				setDiamonds();
				return true;
			}
			if (id == R.id.action_toggle_labels) {
				toggleLabels();
				return true;
			}
			if (id == R.id.action_toggle_axes) {
				toggleAxes();
				return true;
			}
			if (id == R.id.action_toggle_axes_names) {
				toggleAxesNames();
				return true;
			}
			if (id == R.id.action_animate) {
				prepareDataAnimation();
				chart.startDataAnimation();
				return true;
			}
			if (id == R.id.action_toggle_selection_mode) {
				toggleLabelForSelected();

				Toast.makeText(getActivity(),
						"Selection mode set to " + chart.isValueSelectionEnabled() + " select any point.",
						Toast.LENGTH_SHORT).show();
				return true;
			}
			if (id == R.id.action_toggle_touch_zoom) {
				chart.setZoomEnabled(!chart.isZoomEnabled());
				Toast.makeText(getActivity(), "IsZoomEnabled " + chart.isZoomEnabled(), Toast.LENGTH_SHORT).show();
				return true;
			}
			if (id == R.id.action_zoom_both) {
				chart.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
				return true;
			}
			if (id == R.id.action_zoom_horizontal) {
				chart.setZoomType(ZoomType.HORIZONTAL);
				return true;
			}
			if (id == R.id.action_zoom_vertical) {
				chart.setZoomType(ZoomType.VERTICAL);
				return true;
			}
			return super.onOptionsItemSelected(item);
		}

		private void reset() {
			numberOfLines = 1;

			hasAxes = true;
			hasAxesNames = true;
			hasLines = true;
			hasPoints = true;
			shape = ValueShape.CIRCLE;
			isFilled = false;
			hasLabels = false;
			isCubic = false;
			hasLabelForSelected = false;

			chart.setValueSelectionEnabled(hasLabelForSelected);
			resetViewport();
		}

		private void resetViewport() {
			// Reset viewport height range to (0,100)
			final Viewport v = new Viewport(chart.getMaximumViewport());

			if ( numbers  == null ) {
				return ;
			}

			int min_value = 9000 ;
			int max_value = 500 ;
			int max_size = 0 ;

			for (int i = 0; i < numbers.length; i++) {
				if(numbers[i] > 0 && i < min_value)
					min_value = i ;
				if(numbers[i] > 0 && i > max_value)
					max_value = i ;
				if(numbers[i] > max_size)
					max_size = numbers[i];

			}

			v.bottom = 0;
			v.top = max_size + 1;
			v.left = (int)((min_value+500)/100)-1+5;
			v.right =(int)((max_value+500)/100)+1+5;
			chart.setMaximumViewport(v);
			chart.setCurrentViewport(v);
		}

		public void generateData() {


			if ( numbers == null ) {
				return ;
			}

			List<Line> lines = new ArrayList<Line>();
			for (int i = 0; i < numberOfLines; ++i) {
				List<PointValue> values = new ArrayList<PointValue>();
				for (int j = 0; j < numberOfPoints; ++j) {
					if(numbers[j]>0)
						values.add(new PointValue( ((float)(j+500))/100+5 , numbers[j] ));
				}

				Line line = new Line(values);
				line.setColor(ChartUtils.COLORS[i]);
				line.setShape(shape);
				line.setCubic(isCubic);
				line.setFilled(isFilled);
				line.setHasLabels(hasLabels);
				line.setHasLabelsOnlyForSelected(hasLabelForSelected);
				line.setHasLines(hasLines);
				line.setHasPoints(hasPoints);
				lines.add(line);
			}

			data = new LineChartData(lines);

			if (hasAxes) {
				Axis axisX = new Axis();
				Axis axisY = new Axis().setHasLines(true);
				if (hasAxesNames) {
					axisX.setName("Axis X");
					axisY.setName("Axis Y");
				}
				data.setAxisXBottom(axisX);
				data.setAxisYLeft(axisY);
			} else {
				data.setAxisXBottom(null);
				data.setAxisYLeft(null);
			}

			data.setBaseValue(Float.NEGATIVE_INFINITY);
			chart.setLineChartData(data);
			resetViewport();
		}

		/**
		 * Adds lines to data, after that data should be set again with
		 * {@link LineChartView#setLineChartData(LineChartData)}. Last 4th line has non-monotonically x values.
		 */
		private void addLineToData() {
			if (data.getLines().size() >= maxNumberOfLines) {
				Toast.makeText(getActivity(), "Samples app uses max 4 lines!", Toast.LENGTH_SHORT).show();
				return;
			} else {
				++numberOfLines;
			}

			generateData();
		}

		private void toggleLines() {
			hasLines = !hasLines;

			generateData();
		}

		private void togglePoints() {
			hasPoints = !hasPoints;

			generateData();
		}

		private void toggleCubic() {
			isCubic = !isCubic;

			generateData();

			if (isCubic) {
				// It is good idea to manually set a little higher max viewport for cubic lines because sometimes line
				// go above or below max/min. To do that use Viewport.inest() method and pass negative value as dy
				// parameter or just set top and bottom values manually.
				// In this example I know that Y values are within (0,100) range so I set viewport height range manually
				// to (-5, 105).
				// To make this works during animations you should use Chart.setViewportCalculationEnabled(false) before
				// modifying viewport.
				// Remember to set viewport after you call setLineChartData().
				final Viewport v = new Viewport(chart.getMaximumViewport());
				v.bottom = -5;
				v.top = 105;
				// You have to set max and current viewports separately.
				chart.setMaximumViewport(v);
				// I changing current viewport with animation in this case.
				chart.setCurrentViewportWithAnimation(v);
			} else {
				// If not cubic restore viewport to (0,100) range.
				final Viewport v = new Viewport(chart.getMaximumViewport());
				v.bottom = 0;
				v.top = 100;

				// You have to set max and current viewports separately.
				// In this case, if I want animation I have to set current viewport first and use animation listener.
				// Max viewport will be set in onAnimationFinished method.
				chart.setViewportAnimationListener(new ChartAnimationListener() {

					@Override
					public void onAnimationStarted() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationFinished() {
						// Set max viewpirt and remove listener.
						chart.setMaximumViewport(v);
						chart.setViewportAnimationListener(null);

					}
				});
				// Set current viewpirt with animation;
				chart.setCurrentViewportWithAnimation(v);
			}

		}

		private void toggleFilled() {
			isFilled = !isFilled;

			generateData();
		}

		private void setCircles() {
			shape = ValueShape.CIRCLE;

			generateData();
		}

		private void setSquares() {
			shape = ValueShape.SQUARE;

			generateData();
		}

		private void setDiamonds() {
			shape = ValueShape.DIAMOND;

			generateData();
		}

		private void toggleLabels() {
			hasLabels = !hasLabels;

			if (hasLabels) {
				hasLabelForSelected = false;
				chart.setValueSelectionEnabled(hasLabelForSelected);
			}

			generateData();
		}

		private void toggleLabelForSelected() {
			hasLabelForSelected = !hasLabelForSelected;

			chart.setValueSelectionEnabled(hasLabelForSelected);

			if (hasLabelForSelected) {
				hasLabels = false;
			}

			generateData();
		}

		private void toggleAxes() {
			hasAxes = !hasAxes;

			generateData();
		}

		private void toggleAxesNames() {
			hasAxesNames = !hasAxesNames;

			generateData();
		}

		/**
		 * To animate values you have to change targets values and then call {@link Chart#startDataAnimation()}
		 * method(don't confuse with View.animate()). If you operate on data that was set before you don't have to call
		 * {@link LineChartView#setLineChartData(LineChartData)} again.
		 */
		private void prepareDataAnimation() {
			for (Line line : data.getLines()) {
				for (PointValue value : line.getValues()) {
					// Here I modify target only for Y values but it is OK to modify X targets as well.
					value.setTarget(value.getX(), (float) Math.random() * 100);
				}
			}
		}

		private class ValueTouchListener implements LineChartOnValueSelectListener {

			@Override
			public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
				Toast.makeText(getActivity(), "Selected: " + value, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onValueDeselected() {
				// TODO Auto-generated method stub

			}

		}
	}
}
