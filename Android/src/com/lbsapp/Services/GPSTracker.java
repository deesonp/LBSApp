package com.lbsapp.Services;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class GPSTracker extends Service implements LocationListener {

	private Context mContext;

	boolean isGPSEnabled = false;
	boolean isNetworkEnabled = false;
	boolean canGetLocation = false;

	Location location;
	private double latitude;
	private double longitude;

	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

	protected LocationManager locationManager;

	private float batteryPct = 0.0f;

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public GPSTracker(Context context) {
		this.mContext = context;
		getLocation();
	}

	private Location getLocation() {
		float beforeBatteryStatus = getBatteryStatus();
		try {
			locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			
			if(isGPSEnabled || isNetworkEnabled){
				this.canGetLocation = true;
				
				if(isNetworkEnabled){
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					Log.d("LBSApp", "Network");
					if(locationManager != null){
						location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if(location != null){
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
				
				if(isGPSEnabled){
					if(location == null){
						locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						Log.d("LBSApp", "GPS Enabled");
						if(locationManager != null){
							location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if(location != null){
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				}
			}
			
		} catch(Exception e){ 
			Log.e("LBSApp", e.getMessage());
		}
		
		setBatteryPct(beforeBatteryStatus - getBatteryStatus());
		return location;
	}
	
	
	public boolean canGetLocation(){
		return this.canGetLocation;
	}

	public void showSettingsAlert(){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
		alertDialog.setTitle("GPS is settings");
		alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
		
		alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				mContext.startActivity(intent);
			}
		});
		
		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		alertDialog.show();
	}
	
	
	public void stopUsingGPS(){
		if(locationManager != null){
			locationManager.removeUpdates(GPSTracker.this);
		}
	}
	
	public double getLatitude() {
		if(location != null){
			latitude = location.getLatitude();
		}
		return latitude;
	}

	public double getLongitude() {
		if(location != null){
			longitude = location.getLongitude();
		}
		return longitude;
	}

	
	public float getBatteryStatus(){
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = mContext.registerReceiver(null, ifilter);
		
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
			
		return  (level/ (float)scale);
	}

	public float getBatteryPct() {
		return batteryPct;
	}

	public void setBatteryPct(float batteryPct) {
		this.batteryPct = batteryPct;
	}

}
