package com.lbsapp.Receivers;

import com.lbsapp.utils.DatabaseAdapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class MyLocationReceiver extends BroadcastReceiver {


	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("MyLocationReceiver", "RECEIVED!");
		String key = LocationManager.KEY_LOCATION_CHANGED;
		String providerEnabledKey = LocationManager.KEY_PROVIDER_ENABLED;
		if (intent.hasExtra(providerEnabledKey)) {
			if (!intent.getBooleanExtra(providerEnabledKey, true)) {
				Log.e("MyLocationReceiver", "Provider disabled");
			} else {
				Log.e("MyLocationReceiver", "Provider enabled");
			}
		}

		if (intent.hasExtra(key)) {
			Location loc = (Location) intent.getExtras().get(key);
			DatabaseAdapter db = new DatabaseAdapter(context);
			db.open();
			db.insertLocation(String.valueOf(loc.getLatitude()), String.valueOf(loc.getLongitude()));
			db.close();
			Log.e("MyLocationReceiver","Location changed : Lat: " + loc.getLatitude() + " Lng: "
					+ loc.getLongitude());
		}

	}

}
