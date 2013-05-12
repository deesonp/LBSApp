package com.lbsapp.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lbsapp.R;
import com.lbsapp.Common.UserLocation;
import com.lbsapp.Utils.DatabaseAdapter;

public class MainActivity extends Activity{

	Button locateMeButton;
	Button printDbButton;

	TextView printDBTextView;
	TextView longitudeTextView;
	TextView latitudeTextView;
	TextView batteryStatusTextView;
	TextView serviceStatusTextView;
	
	DatabaseAdapter dbAdapter;
	boolean alarmEnabled = false;
	boolean locationServiceEnabled = false;

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
		printDBTextView
				.setMovementMethod(ScrollingMovementMethod.getInstance());
		batteryStatusTextView = (TextView) findViewById(R.id.batteryLevel);
		
		serviceStatusTextView = (TextView) findViewById(R.id.ServiceStatus);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		serviceStatusTextView.setText(String.valueOf(pref.getBoolean(getString(R.id.ServiceStatus), true)));
		
		UserLocation.getBestLocation(this);

		printDbButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Cursor allLoc = dbAdapter.getAllLocations();
				allLoc.moveToFirst();
				printDBTextView.setText("");
				while (allLoc.isAfterLast() == false) {
					printDBTextView.append("-" + allLoc.getString(0) + " ");
					printDBTextView.append(", " + allLoc.getString(1) + " ");
					printDBTextView.append(", " + allLoc.getString(2) + " ");
					printDBTextView.append(", " + allLoc.getString(3) + "\n");
					allLoc.moveToNext();
				}
				allLoc.close();
				printDBTextView.append("\n");
				Cursor allBattery = dbAdapter.getAllBatteryStats();
				allBattery.moveToFirst();
				while (allBattery.isAfterLast() == false) {
					printDBTextView.append("-" + allBattery.getString(0) + " ");
					printDBTextView.append("," + allBattery.getString(1) + "\n");
					allBattery.moveToNext();
				}
				allBattery.close();
			}
		});
		locateMeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				UserLocation.startAndStopAlarm(MainActivity.this,
						alarmEnabled = !alarmEnabled);
				serviceStatusTextView.setText(alarmEnabled+"");
			}
		});
		
	}

	@Override
	protected void onStart() {
		super.onStart();
		checkForLocationServices();
	}

	private void checkForLocationServices() {
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationServiceEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if(!locationServiceEnabled){
			new EnableGPSDialog().show(getFragmentManager(), "enableLocationService");
		}
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_map:
			openLocationMap();
			return true;
		case R.id.action_settings:
			openLocationSettings();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	
	public static class EnableGPSDialog extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
			dialogBuilder.setTitle(R.string.enable_location_service);
			dialogBuilder.setMessage(R.string.enable_location_dialog_msg);
			dialogBuilder.setPositiveButton(R.string.enable_location_service, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(settingsIntent);
					dismiss();
				}

			});
			dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dismiss();
				}
			});
			return dialogBuilder.create();
		}
	}

	private void openLocationSettings() {
		Intent locationPref = new Intent(this, LocationSettingsActivity.class);
		startActivity(locationPref);
	}
	
	private void openLocationMap(){
		Intent locationMap = new Intent(this, LocationMapActivity.class);
		startActivity(locationMap);
	}

}
