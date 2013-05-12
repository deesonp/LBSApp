package com.lbsapp.Receivers;

import com.lbsapp.R;
import com.lbsapp.Common.UserLocation;
import com.lbsapp.Utils.Constants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver{
	
	private static final String TAG = "BootBroadcastReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(Constants.DEBUG){
			Log.d(Constants.LOG_TAG, TAG + " : Starting Alarm ...");
		}
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		boolean enableBootTracking = pref.getBoolean(context.getString(R.string.boot_tracking_key), false);
		UserLocation.startAndStopAlarm(context, enableBootTracking);
	}

}
