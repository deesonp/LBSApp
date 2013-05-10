package com.lbsapp.Activities;

import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lbsapp.R;
import com.lbsapp.Receivers.MyLocationReceiver;
import com.lbsapp.Services.GPSTracker;
import com.lbsapp.Services.UserLocationBroadcastService;
import com.lbsapp.utils.DatabaseAdapter;

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
		
		// i = new Intent(this, MyLocationReceiver.class);
		//pendingIntent = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
		
		
		final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if(lm != null){
			final List<String> providers = lm.getAllProviders();
			if(providers.size() > 0){
				Location bestLocation = null;
				for(String provider: lm.getAllProviders()){
					final Location lastLocation = lm.getLastKnownLocation(provider);
					if(lastLocation != null){
						
						if(bestLocation == null || !bestLocation.hasAccuracy() || (lastLocation.hasAccuracy() && lastLocation.getAccuracy() < bestLocation.getAccuracy())){
							bestLocation = lastLocation;
						}
					}
				}
				
				if(bestLocation != null){
					Log.d("LBSAPP","BEST LOCATION :" + bestLocation.getLatitude() +", " +bestLocation.getLongitude());
				}
			}
		}
		
		startAlarmListener(this);

		
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
	
	
	public static void startAlarmListener(Context context){
		Intent i = new Intent(context, UserLocationBroadcastService.class);
		PendingIntent alarmIntent = PendingIntent.getService(context, 2, i, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.cancel(alarmIntent);
		am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 1000*60, alarmIntent);
		
	}

	public static void stopAlarmListener(Context context){
		Intent i = new Intent(context,UserLocationBroadcastService.class);
		PendingIntent alarmIntent = PendingIntent.getService(context, 2, i, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.cancel(alarmIntent);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
}
