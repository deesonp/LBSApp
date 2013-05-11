package com.lbsapp.Activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lbsapp.R;
import com.lbsapp.Common.UserLocation;
import com.lbsapp.Services.GPSTracker;
import com.lbsapp.Utils.DatabaseAdapter;

public class MainActivity extends Activity {

	Button locateMeButton;
	Button printDbButton;
	
	TextView printDBTextView;
	TextView longitudeTextView;
	TextView latitudeTextView;
	TextView batteryStatusTextView;
	
	AlarmManager alarmManager;
	
	GPSTracker gps;
	float batteryPct;
	PendingIntent pendingIntent;
	
	DatabaseAdapter dbAdapter;
	boolean alarmEnabled = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		dbAdapter = new DatabaseAdapter(this);
		
		locateMeButton = (Button) findViewById(R.id.locateMeButton);
		printDbButton = (Button) findViewById(R.id.printDBButton);
		
		longitudeTextView = (TextView) findViewById(R.id.gpsLongitude);
		latitudeTextView = (TextView) findViewById(R.id.gpsLatitude);
		printDBTextView = (TextView) findViewById(R.id.printDBText);
		printDBTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
		batteryStatusTextView = (TextView) findViewById(R.id.batteryLevel);
		
		UserLocation.initUserTracking(this);
		
		printDbButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Cursor allLoc = dbAdapter.getAllLocations();
				allLoc.moveToFirst();
				printDBTextView.setText("");
				while(allLoc.isAfterLast() == false){
					printDBTextView.append("-" +allLoc.getString(0) +" ");
					printDBTextView.append(", " +allLoc.getString(1) +" ");
					printDBTextView.append(", " +allLoc.getString(2) +" ");
					printDBTextView.append(", "+ allLoc.getString(3) +"\n");
					allLoc.moveToNext();
				}
				allLoc.close();
				printDBTextView.append("\n");
				Cursor allBattery = dbAdapter.getAllBatteryStats();
				allBattery.moveToFirst();
				while(allBattery.isAfterLast() == false){
					printDBTextView.append("-" + allBattery.getString(0)+ " ");
					printDBTextView.append("," + allBattery.getString(1)+ " ");
					printDBTextView.append("," + allBattery.getString(2)+ "\n");
					allBattery.moveToNext();
				}
				allBattery.close();
			}
		});
		
		
		locateMeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dbAdapter.inserBatteryStat(1,"TEST", "TEST");
				UserLocation.startAndStopAlarm(MainActivity.this, alarmEnabled = !alarmEnabled);
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
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
