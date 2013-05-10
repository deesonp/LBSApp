package com.lbsapp.Services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import com.lbsapp.Receivers.MyLocationReceiver;

public class MyLocationService extends Service {

	private static final String LOG_TAG = "MyLocationService";

	LocationManager lm;
	PendingIntent pendingIntent;

	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

	private boolean isGPSEnabled = false;
	private boolean isNetworkEnabled = false;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(LOG_TAG, "Service Started");
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Intent i = new Intent(this, MyLocationReceiver.class);
		pendingIntent = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

		isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		isNetworkEnabled = lm
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if (isGPSEnabled || isNetworkEnabled) {

			if (isNetworkEnabled) {
				lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
						MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES,
						pendingIntent);
				Log.d("LBSApp", "Network");
				
			} 
			
			if (isGPSEnabled) {
				lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
						MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES,
						pendingIntent);
				Log.d("LBSApp", "GPS Enabled");
			}
		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		lm.removeUpdates(pendingIntent);
		super.onDestroy();
		Log.i(LOG_TAG, "Service Stopped");

	}

}
