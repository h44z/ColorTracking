package at.uni.as.colotracking;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.samples.tutorial1.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Toast;
import at.uni.as.colotracking.constants.Color;

public class MainActivity extends Activity implements
		CvCameraViewListener2, OnTouchListener {
	private static final String TAG = "ColorTracking::MainActivity";
	
	private static final float RES_DISP_H = 1280;
	private static final float RES_DISP_W =  720;
	
	private static final float POS_TRACK_X = RES_DISP_H / 2;
	private static final float POS_TRACK_Y = RES_DISP_W / 2;

	private CameraBridgeViewBase mOpenCvCameraView;
	private ColorTracking colorTracking;
	
	// Menu Items
	private MenuItem menuCalibrateRed = null;
	private MenuItem menuCalibrateGreen = null;
	private MenuItem menuCalibrateBlue = null;
	private MenuItem menuCalibrateYellow = null;
	private MenuItem menuCalibrateOrange = null;
	private MenuItem menuCalibrateWhite = null;
	private MenuItem menuHomography = null;
	private MenuItem menuStartTracking = null;
	
	// flags
	private boolean displayCross = false;

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(TAG, "OpenCV loaded successfully");
				mOpenCvCameraView.enableView();
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};

	public MainActivity() {
		Log.i(TAG, "Instantiated new " + this.getClass());

	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.layout_main_acitivity);

		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);
		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		mOpenCvCameraView.setCvCameraViewListener(this);
		mOpenCvCameraView.setOnTouchListener(this);
		
		colorTracking = new ColorTracking();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this,
				mLoaderCallback);
	}

	public void onDestroy() {
		super.onDestroy();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i(TAG, "called onCreateOptionsMenu");
		
		this.menuStartTracking = menu.add("Start Tracking");
		this.menuCalibrateRed = menu.add("Cal. RED");
		this.menuCalibrateGreen = menu.add("Cal. GREEN");
		this.menuCalibrateBlue = menu.add("Cal. BLUE");
		this.menuCalibrateYellow = menu.add("Cal. YELLOW");
		this.menuCalibrateOrange = menu.add("Cal. ORANGE");
		this.menuCalibrateWhite = menu.add("Cal. WHITE");
		this.menuHomography = menu.add("Cal. HOMOGRAPHY");

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);

		if (item == this.menuStartTracking) {
			colorTracking.setTrackingActive(!colorTracking.getTrackingActive());
		} else if(item == this.menuHomography) {
			colorTracking.setCalcProbMap(true);
		} else if (item == this.menuCalibrateRed) {
			Toast toast = Toast.makeText(this,
					"Touch to calibrate the RED probability matrix",
					Toast.LENGTH_LONG);
			toast.show();
			
			colorTracking.trackColor(Color.RED);
			displayCross = true;
		} else if (item == this.menuCalibrateGreen) {
			Toast toast = Toast.makeText(this,
					"Touch to calibrate the GREEN probability matrix",
					Toast.LENGTH_LONG);
			toast.show();
			
			colorTracking.trackColor(Color.GREEN);
			displayCross = true;
		} else if (item == this.menuCalibrateBlue) {
			Toast toast = Toast.makeText(this,
					"Touch to calibrate the BLUE probability matrix",
					Toast.LENGTH_LONG);
			toast.show();
			
			colorTracking.trackColor(Color.BLUE);
			displayCross = true;
		} else if (item == this.menuCalibrateYellow) {
			Toast toast = Toast.makeText(this,
					"Touch to calibrate the YELLOW probability matrix",
					Toast.LENGTH_LONG);
			toast.show();
			
			colorTracking.trackColor(Color.YELLOW);
			displayCross = true;
		} else if (item == this.menuCalibrateOrange) {
			Toast toast = Toast.makeText(this,
					"Touch to calibrate the ORANGE probability matrix",
					Toast.LENGTH_LONG);
			toast.show();
			
			colorTracking.trackColor(Color.ORANGE);
			displayCross = true;
		} else if (item == this.menuCalibrateWhite) {
			Toast toast = Toast.makeText(this,
					"Touch to calibrate the WHITE probability matrix",
					Toast.LENGTH_LONG);
			toast.show();
			
			colorTracking.trackColor(Color.WHITE);
			displayCross = true;
		} 
		
		return true;
	}

	public void onCameraViewStarted(int width, int height) {
		//not used
	}

	public void onCameraViewStopped() {
		//not used
	}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		Mat image = colorTracking.processImage(inputFrame.rgba(), POS_TRACK_X, POS_TRACK_Y);
		
		if(colorTracking == null)
			return inputFrame.rgba();

		// Draw cross for color calibration.
		if (displayCross) {
			Core.line(image, new Point(0, RES_DISP_W / 2), new Point(
					RES_DISP_H, POS_TRACK_Y), new Scalar(255, 255, 255));
			Core.line(image, new Point(RES_DISP_H / 2, 0), new Point(POS_TRACK_X,
					RES_DISP_W), new Scalar(255, 255, 255));
		}

		return image;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				if(!colorTracking.getCalcProbMap())
					colorTracking.setCalcProbMap(true);
	
				return true;
			default:
				return true;
		}
	}
}