package com.lbsapp.Services;

import com.lbsapp.Common.UserLocation;
import com.lbsapp.Utils.Constants;

import android.app.IntentService;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

public class UserLocationBroadcastService extends IntentService {
	
	private static final String TAG = "UserLocationBroadcastService";


	public UserLocationBroadcastService() {
		super("LocationService");
	}

	private boolean isBatteryLow(Intent batteryStatus){
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE,-1);
		float batteryPct = level/(float) scale;
		return batteryPct < 0.15;
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		if(Constants.DEBUG){
			Log.d(Constants.LOG_TAG, TAG +" :onHandleIntent : Service Started");
		}
		
		IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = registerReceiver(null, iFilter);
		if(!isBatteryLow(batteryStatus)){
			Intent locationIntent = new Intent(UserLocation.brodacastMessage);
			getBaseContext().sendBroadcast(locationIntent,"android.permission.ACCESS_FINE_LOCATION");
		}
	}
	

}
