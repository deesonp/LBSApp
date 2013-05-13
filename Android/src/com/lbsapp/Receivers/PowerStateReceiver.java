package com.lbsapp.Receivers;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.lbsapp.Services.UserLocationBroadcastService;
import com.lbsapp.Utils.Constants;

/**
 * Monitor power changes to disable location services on low power
 * @author Deeson
 *
 */
public class PowerStateReceiver extends BroadcastReceiver {

	private static final String TAG = "PowerStateReceiever";

	@Override
	public void onReceive(Context context, Intent intent) {
		if(Constants.DEBUG){
			Log.d(Constants.LOG_TAG, TAG + " : Battery status changed");
		}
		boolean lowBattery = intent.getAction().equals(Intent.ACTION_BATTERY_LOW);
		
		PackageManager packageManager = context.getPackageManager();
		
		ComponentName myLocationReceiver = new ComponentName(context, UserLocationReceiver.class);
		ComponentName userLocationBroadcastService = new ComponentName(context,
				UserLocationBroadcastService.class);
		
				
		packageManager.setComponentEnabledSetting(userLocationBroadcastService,
				lowBattery ? PackageManager.COMPONENT_ENABLED_STATE_DISABLED
						: PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
				PackageManager.DONT_KILL_APP);
		packageManager.setComponentEnabledSetting(myLocationReceiver,
				lowBattery ? PackageManager.COMPONENT_ENABLED_STATE_DISABLED
						: PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
				PackageManager.DONT_KILL_APP);
	}
}
