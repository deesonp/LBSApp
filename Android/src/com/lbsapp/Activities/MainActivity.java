package com.lbsapp.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.androidchallenge.lbsapp.R;
import com.lbsapp.Services.GPSTracker;

public class MainActivity extends Activity {

	Button locateMeButton;
	TextView longitudeTextView;
	TextView latitudeTextView;
	
	GPSTracker gps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		gps = new GPSTracker(this);
		locateMeButton = (Button) findViewById(R.id.locateMeButton);
		longitudeTextView = (TextView) findViewById(R.id.gpsLongitude);
		latitudeTextView = (TextView) findViewById(R.id.gpsLatitude);
		
		locateMeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (gps.canGetLocation()) {
					longitudeTextView.setText(gps.getLongitude() + "");
					latitudeTextView.setText(gps.getLatitude() + "");
				} else {
					gps.showSettingsAlert();
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

}
