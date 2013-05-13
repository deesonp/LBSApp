package com.lbsapp.Receivers;

import com.lbsapp.Utils.Constants;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Monitor network changes to disable location updates accordingly
 * @author Deeson
 *
 */
public class NetworkConnectivityReceiver extends BroadcastReceiver {
	
	private static final String TAG = "NetworkConnectivityReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if(Constants.DEBUG){
			Log.d(Constants.LOG_TAG, TAG + " : Checking network connections");
		}
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
