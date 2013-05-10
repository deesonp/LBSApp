package com.lbsapp.Receivers;

import com.lbsapp.Activities.MainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver{
	
	private static final String TAG = "BootBroadcastReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Alarm started at boot");
		MainActivity.startAlarmListener(context);
	}

}
