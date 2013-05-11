package com.lbsapp.Services;

import com.lbsapp.Common.UserLocation;
import com.lbsapp.Utils.Constants;

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
		if(Constants.DEBUG){
			Log.d(Constants.LOG_TAG, TAG +" :onHandleIntent : Service Started");
		}
		Intent locationIntent = new Intent(UserLocation.brodacastMessage);
		getBaseContext().sendBroadcast(locationIntent,"android.permission.ACCESS_FINE_LOCATION");
		stopSelf();
	}
	

}
