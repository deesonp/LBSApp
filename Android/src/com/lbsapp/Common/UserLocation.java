package com.lbsapp.Common;

import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;
import android.util.Log;

import com.lbsapp.Services.UserLocationBroadcastService;
import com.lbsapp.Utils.Constants;

public class UserLocation {

	private static final String TAG = "UserLocation";
	public static final String brodacastMessage = "com.lbsapp.broadcast.LOCATION_CHANGED";

	private static long alarmFrequency = (long) (1000*60.);

	public static long getAlarmFrequency() {
		return alarmFrequency;
	}

	public static void initUserTracking(final Context context) {
		if (Constants.DEBUG) {
			Log.d(Constants.LOG_TAG, TAG + " : beginUserTracking");
		}
		final LocationManager lm = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		if (lm != null) {
			final List<String> providers = lm.getAllProviders();
			if (providers.size() > 0) {
				Location bestLocation = null;
				for (String provider : lm.getAllProviders()) {
					final Location lastLocation = lm
							.getLastKnownLocation(provider);
					if (lastLocation != null) {
						if (bestLocation == null
								|| !bestLocation.hasAccuracy()
								|| (lastLocation.hasAccuracy() && lastLocation
										.getAccuracy() < bestLocation
										.getAccuracy())) {
							bestLocation = lastLocation;
						}
					}
				}
				
				if (Constants.DEBUG) {
					if (bestLocation != null) {
						Log.d(Constants.LOG_TAG,
								"BEST LOCATION :" + bestLocation.getLatitude()
										+ ", " + bestLocation.getLongitude());
					}
				}
			}
		}
	}

	public static void startAndStopAlarm(final Context context,
			boolean enableAlarm) {

		final Intent broadcastServiceIntent = new Intent(context,
				UserLocationBroadcastService.class);
		final PendingIntent alarmIntent = PendingIntent.getService(context,
				Constants.LOCATION_BROADCAST_REQ_CODE, broadcastServiceIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		final AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		if (enableAlarm) {
			am.cancel(alarmIntent);
			am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime(), getAlarmFrequency(),
					alarmIntent);
			if (Constants.DEBUG) {
				Log.d(Constants.LOG_TAG, TAG
						+ ": startAlarm called : alarmFrequency="
						+ alarmFrequency + " secs");
			}
		} else {
			am.cancel(alarmIntent);
			if (Constants.DEBUG) {
				Log.d(Constants.LOG_TAG, TAG + ": stopAlarm called");
			}
		}
	}

}
