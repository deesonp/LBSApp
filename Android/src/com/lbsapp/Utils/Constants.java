package com.lbsapp.Utils;

import android.app.AlarmManager;

public class Constants {

	public static final boolean DEBUG = false;
	public static final String LOG_TAG = "MyTracker";
	public static final int LOCATION_BROADCAST_REQ_CODE = 1;
	public static final String LOCATION_SETTINGS_PREF_FILE = "LocationSettingsPrefFile";
	public static final String ALARM_ENABLED_KEY = "alarmEnabled";
	public static final String DEFAULT_ALARM_FREQ = String.valueOf(AlarmManager.INTERVAL_HALF_HOUR);
	public static final long MIN_LOCATION_UPDATE_TIME = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
	public static final long MIN_LOCATION_UPDATE_DISTANCE = 100;
	public static final String CURRENT_LOCATION_LAT = "currentLocationLat";
	public static final String CURRENT_LOCATION_LNG = "currentLocationLng";
	public static final String LAST_LOCATION_UPDATE_LAT = "lastLocationUpdateLat";
	public static final String LAST_LOCATION_UPDATE_LNG = "lastLocationUpdateLng";
	public static final int TIMESTAMP_COLUMN_INDEX = 1;
	public static final int LNG_COLUMN_INDEX = 3;
	public static final int LAT_COLUMN_INDEX = 2;

}
