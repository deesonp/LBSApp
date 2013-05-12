package com.lbsapp.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.lbsapp.R;
import com.lbsapp.Common.UserLocation;
import com.lbsapp.Utils.Constants;
import com.lbsapp.Utils.DatabaseAdapter;

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

		if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && gpsProvideEnabled) {
			if (Constants.DEBUG) {
				Log.d(Constants.LOG_TAG, TAG + " :GPS_PROVIDER Enabled.");
			}

			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					Constants.MIN_LOCATION_UPDATE_DISTANCE, Constants.MIN_LOCATION_UPDATE_TIME,
					locationListener);
			Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			UserLocation.processLocation(context, loc);
		} else if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && networkProviderEnabled) {
			if (Constants.DEBUG) {
				Log.d(Constants.LOG_TAG, TAG + " :NETWORK_PROVIDER Enabled.");
			}
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
					Constants.MIN_LOCATION_UPDATE_DISTANCE, Constants.MIN_LOCATION_UPDATE_TIME,
					locationListener);
			Location loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			UserLocation.processLocation(context, loc);
		}

	}

	private String getBatteryState(Intent batteryStatus) {
		int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
				|| status == BatteryManager.BATTERY_STATUS_FULL;
		int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
		boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
		boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
		StringBuffer batteryState = new StringBuffer();
		if (isCharging) {
			batteryState.append("CHARGING_");
			if (usbCharge) {
				batteryState.append("USB_CHARGE");
			} else if (acCharge) {
				batteryState.append("AC_CHARGE");
			}
		}

		return batteryState.toString();
	}

	private float getBatteryPct(Intent batteryStatus) {
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		return level / (float) scale;
	}

	private Intent getBatteryIntent(Context context) {
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		return context.registerReceiver(null, ifilter);
	}

}
