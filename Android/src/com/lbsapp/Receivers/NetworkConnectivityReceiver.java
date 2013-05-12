package com.lbsapp.Receivers;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkConnectivityReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null
				&& activeNetwork.isConnectedOrConnecting();

		if (isConnected) {
			PackageManager pm = context.getPackageManager();

			ComponentName networkConnectivityReceiver = new ComponentName(
					context, NetworkConnectivityReceiver.class);
			ComponentName userLocationReceiver = new ComponentName(context,
					UserLocationReceiver.class);

			pm.setComponentEnabledSetting(networkConnectivityReceiver,
					PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
					PackageManager.DONT_KILL_APP);
			
			pm.setComponentEnabledSetting(userLocationReceiver,
					PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
					PackageManager.DONT_KILL_APP);
		}
	}

}
