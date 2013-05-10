package com.lbsapp.Receivers;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class PowerStateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		boolean lowBattery = intent.getAction().equals(
				Intent.ACTION_BATTERY_LOW);
		PackageManager packageManager = context.getPackageManager();
		ComponentName myLocationReceiver = new ComponentName(context,
				MyLocationReceiver.class);
		packageManager.setComponentEnabledSetting(myLocationReceiver,
				lowBattery ? PackageManager.COMPONENT_ENABLED_STATE_DISABLED
						: PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
				PackageManager.DONT_KILL_APP);
	}

}
