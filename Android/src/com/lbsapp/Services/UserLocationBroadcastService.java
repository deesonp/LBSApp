package com.lbsapp.Services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class UserLocationBroadcastService extends IntentService {
	
	private static final String TAG = "UserLocationBroadcastService";


	public UserLocationBroadcastService() {
		super("LocationService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.e(TAG, "Service Started");
		Intent locationIntent = new Intent("com.lbsapp.Services.LOCATION_CHAGNED");
		getBaseContext().sendBroadcast(locationIntent,"android.permission.ACCESS_FINE_LOCATION");	
	}
	

}
