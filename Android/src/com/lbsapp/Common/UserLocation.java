package com.lbsapp.Common;

import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.lbsapp.R;
import com.lbsapp.Services.UserLocationBroadcastService;
import com.lbsapp.Utils.Constants;
import com.lbsapp.Utils.DatabaseAdapter;


/**
 * User Location tracking and storing information
 * @author Deeson
 *
 */
public class UserLocation {

	private static final String TAG = "UserLocation";
	public static final String brodacastMessage = "com.lbsapp.broadcast.LOCATION_CHANGED";

	
	/**
	 * Get the best last known location based from the best provider
	 * @param context
	 * @return
	 */
	public static Location getBestLocation(final Context context) {
		if (Constants.DEBUG) {
			Log.d(Constants.LOG_TAG, TAG + " : getBestLocation");
		}
		Location bestLocation = null;
		final LocationManager lm = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		if (lm != null) {
			final List<String> providers = lm.getAllProviders();
			if (providers.size() > 0) {
				for (String provider : lm.getAllProviders()) {
					final Location lastLocation = lm.getLastKnownLocation(provider);
					if (lastLocation != null) {
						if (bestLocation == null
								|| !bestLocation.hasAccuracy()
								|| (lastLocation.hasAccuracy() && lastLocation.getAccuracy() < bestLocation
										.getAccuracy())) {
							bestLocation = lastLocation;
						}
					}
				}

				if (bestLocation != null) {
					if (Constants.DEBUG) {
						Log.d(Constants.LOG_TAG, "BEST LOCATION :" + bestLocation.getLatitude()
								+ ", " + bestLocation.getLongitude());
					}

					SharedPreferences prefs = PreferenceManager
							.getDefaultSharedPreferences(context);
					SharedPreferences.Editor editor = prefs.edit();
					editor.putFloat(Constants.LAST_LOCATION_UPDATE_LAT,
							(float) bestLocation.getLatitude());
					editor.putFloat(Constants.LAST_LOCATION_UPDATE_LNG,
							(float) bestLocation.getLongitude());
					editor.commit();
				}
			}
		}
		return bestLocation;
	}
	
	
	/**
	 * Enable and disable alarm to trigger the location update
	 * @param context
	 * @param enableAlarm true to enable, false to disable
	 */
	public static void startAndStopAlarm(final Context context, boolean enableAlarm) {

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		String alarmFrequency = pref.getString(context.getString(R.string.alarm_freq_key),
				Constants.DEFAULT_ALARM_FREQ);

		final Intent broadcastServiceIntent = new Intent(context,
				UserLocationBroadcastService.class);
		final PendingIntent alarmIntent = PendingIntent.getService(context,
				Constants.LOCATION_BROADCAST_REQ_CODE, broadcastServiceIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		if (enableAlarm) {
			am.cancel(alarmIntent);
			am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),
					Long.parseLong(alarmFrequency), alarmIntent);
			if (Constants.DEBUG) {
				Log.d(Constants.LOG_TAG, TAG + ": startAlarm called : alarmFrequency="
						+ alarmFrequency + " secs");
			}
		} else {
			am.cancel(alarmIntent);
			if (Constants.DEBUG) {
				Log.d(Constants.LOG_TAG, TAG + ": stopAlarm called");
			}
		}
	}

	/**
	 * Store the given location into the database
	 * @param context
	 * @param loc
	 */
	public static void processLocation(Context context, Location loc) {
		if (loc != null) {
			DatabaseAdapter db = new DatabaseAdapter(context);
			db.open();
			db.insertLocation(String.valueOf(loc.getLatitude()), String.valueOf(loc.getLongitude()));
			db.close();
			if (Constants.DEBUG) {
				Log.d(Constants.LOG_TAG, "processLocation: latitude: " + loc.getLatitude()
						+ " , longitude: " + loc.getLongitude());
			}
		}
	}

}
