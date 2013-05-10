package com.lbsapp.Activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lbsapp.R;
import com.lbsapp.Receivers.MyLocationReceiver;
import com.lbsapp.Services.GPSTracker;
import com.lbsapp.Services.MyLocationService;
import com.lbsapp.utils.DatabaseAdapter;

public class MainActivity extends Activity {

	Button locateMeButton;
	Button printDbButton;
	
	TextView printDBTextView;
	TextView longitudeTextView;
	TextView latitudeTextView;
	TextView batteryStatusTextView;
	
	GPSTracker gps;
	float batteryPct;
	PendingIntent pendingIntent;
	
	DatabaseAdapter dbAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		gps = new GPSTracker(this);
		dbAdapter = new DatabaseAdapter(this);
		
		locateMeButton = (Button) findViewById(R.id.locateMeButton);
		printDbButton = (Button) findViewById(R.id.printDBButton);
		
		longitudeTextView = (TextView) findViewById(R.id.gpsLongitude);
		latitudeTextView = (TextView) findViewById(R.id.gpsLatitude);
		printDBTextView = (TextView) findViewById(R.id.printDBText);
		printDBTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
		batteryStatusTextView = (TextView) findViewById(R.id.batteryLevel);
		
		Intent i = new Intent(this, MyLocationReceiver.class);
		pendingIntent = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
		
		printDbButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				StringBuffer allLocation = new StringBuffer();
				Cursor allLoc = dbAdapter.getAllLocation();
				allLoc.moveToFirst();
				printDBTextView.setText("");
				while(allLoc.isAfterLast() == false){
					printDBTextView.append("-" +allLoc.getString(0) +" ");
					printDBTextView.append(", " +allLoc.getString(1) +" ");
					printDBTextView.append(", "+ allLoc.getString(2) +"\n");
					allLoc.moveToNext();
				}
				allLoc.close();
			}
		});
		locateMeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (gps.canGetLocation()) {
					longitudeTextView.setText(gps.getLongitude() + "");
					latitudeTextView.setText(gps.getLatitude() + "");
					dbAdapter.insertLocation(gps.getLatitude()+"", gps.getLongitude()+"");
					batteryStatusTextView.setText(gps.getBatteryPct()+ "");
				} else {
					gps.showSettingsAlert();
				}

			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		startService(new Intent(getBaseContext(), MyLocationService.class));
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		stopService(new Intent(getBaseContext(), MyLocationService.class));
	}
	
	
	@Override
	protected void onResume() {
		dbAdapter.open();
		super.onResume();
	}
	
	
	@Override
	protected void onPause() {
		dbAdapter.close();
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
}
