package com.lbsapp.Receivers;

import com.lbsapp.Common.UserLocation;
import com.lbsapp.Utils.Constants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver{
	
	private static final String TAG = "BootBroadcastReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(Constants.DEBUG){
			Log.d(Constants.LOG_TAG, TAG + " : Starting Alarm ...");
		}
		UserLocation.startAndStopAlarm(context, true);
	}

}
