package com.lbsapp.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.lbsapp.Utils.Constants;
import com.lbsapp.Utils.DatabaseAdapter;

public class MyLocationReceiver extends BroadcastReceiver {
	
	private static final String TAG = "MyLocationReceiver";
	
	LocationManager lm;
	
	LocationListener locationListener = new LocationListener() {
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onLocationChanged(Location location) {
			Log.e(Constants.LOG_TAG,
					"LocationListener : onLocationChanged latitude:"
							+ location.getLatitude() + " , longitude: "
							+ location.getLongitude());
		}
	};

	@Override
	public void onReceive(Context context, Intent intent) {
		if(Constants.DEBUG){
			Log.d(Constants.LOG_TAG, TAG +" :onReceive");
		}
		
		lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 1000*60*15, locationListener);
			Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			processLocation(context,loc);
			lm.removeUpdates(locationListener);
		}

	}

	private void processLocation(Context context, Location loc) {
		if (loc != null) {
			DatabaseAdapter db = new DatabaseAdapter(context);
			db.open();
			db.insertLocation(String.valueOf(loc.getLatitude()),
					String.valueOf(loc.getLongitude()));
			db.close();
			if(Constants.DEBUG){
				Log.d(Constants.LOG_TAG, "processLocation: latitude: " + loc.getLatitude()
					+ " , longitude: " + loc.getLongitude());
			}
		}
	}

}
