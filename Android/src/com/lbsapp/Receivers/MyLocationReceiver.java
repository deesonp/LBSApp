package com.lbsapp.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.lbsapp.utils.DatabaseAdapter;

public class MyLocationReceiver extends BroadcastReceiver {
	
	LocationManager lm;
	LocationListener locationListener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onLocationChanged(Location location) {
			Log.e("LocationListener", "LocationChanged :" + location.getLatitude() +" , "+ location.getLongitude());
		}
	};
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("MyLocationReceiver", "RECEIVED!");
		lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 1000*60*15, locationListener);
			Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			processLocation(context,loc);
		}

	}

	private void processLocation(Context context, Location loc) {
		if(loc != null){
			DatabaseAdapter db = new DatabaseAdapter(context);
			db.open();
			db.insertLocation(String.valueOf(loc.getLatitude()),String.valueOf(loc.getLongitude()));
			db.close();
			Log.e("MyLocationReceiver", "LocationChanged :" + loc.getLatitude() +" , "+ loc.getLongitude());

		}
	}

}
