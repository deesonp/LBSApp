package com.lbsapp.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.lbsapp.R;
import com.lbsapp.Common.UserLocation;
import com.lbsapp.Utils.Constants;

/**
 * 
 * Get Location updates when the alarm is trigger and store in database
 * 
 * @author Deeson
 * 
 */
public class UserLocationReceiver extends BroadcastReceiver {

	private static final String TAG = "UserLocationReceiver";

	LocationManager lm;

	LocationListener locationListener = new LocationListener() {
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onLocationChanged(Location location) {
			if (Constants.DEBUG) {
				Log.d(Constants.LOG_TAG, "LocationListener : onLocationChanged latitude:"
						+ location.getLatitude() + " , longitude: " + location.getLongitude());
			}
			lm.removeUpdates(locationListener);
			lm = null;
		}
	};

	@Override
	public void onReceive(Context context, Intent intent) {
		if (Constants.DEBUG) {
			Log.d(Constants.LOG_TAG, TAG + " :onReceive");
		}
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean gpsProvideEnabled = prefs.getBoolean(context.getString(R.string.gps_provider_key),
				true);
		boolean networkProviderEnabled = prefs.getBoolean(
				context.getString(R.string.network_provider_key), true);

		lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		if (lm != null && locationListener != null) {
			lm.removeUpdates(locationListener);
		}

		if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && networkProviderEnabled) {
			if (Constants.DEBUG) {
				Log.d(Constants.LOG_TAG, TAG + " :NETWORK_PROVIDER Enabled.");
			}
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
					Constants.MIN_LOCATION_UPDATE_DISTANCE, Constants.MIN_LOCATION_UPDATE_TIME,
					locationListener);
			UserLocation.processLocation(context,
					lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
		} else {
			if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && gpsProvideEnabled) {
				if (Constants.DEBUG) {
					Log.d(Constants.LOG_TAG, TAG + " :GPS_PROVIDER Enabled.");
				}
				lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
						Constants.MIN_LOCATION_UPDATE_DISTANCE, Constants.MIN_LOCATION_UPDATE_TIME,
						locationListener);
				UserLocation.processLocation(context,
						lm.getLastKnownLocation(LocationManager.GPS_PROVIDER));
			}
		}

	}

}
