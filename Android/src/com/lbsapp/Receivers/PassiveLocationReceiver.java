package com.lbsapp.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;

public class PassiveLocationReceiver extends BroadcastReceiver{

	private static final String TAG = "PassiveLocationReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		
	}
	
	private void requestPassiveLocationUpdates(Context context){
		LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		LocationListener listener = new LocationListener(){

			@Override
			public void onLocationChanged(Location location) {
				Log.i(TAG, "[PASSIVE] " + location.toString());
				if(LocationManager.GPS_PROVIDER.equals(location.getProvider())){
					if(location.hasAccuracy() && (location.getAccuracy() < 10.0f)){
						
					}
				}
			}

			@Override
			public void onProviderDisabled(String provider) {
				
			}

			@Override
			public void onProviderEnabled(String provider) {
				
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				
			}
			
		};
		
		Log.i(TAG, "Requesting passive location updates");
		lm.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, DateUtils.MINUTE_IN_MILLIS*30, 10, listener);
	}

}
