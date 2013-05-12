package com.lbsapp.Utils;

import android.app.AlarmManager;

public class Constants {

	public static final boolean DEBUG = true;
	public static final String LOG_TAG = "LBSApp";
	public static final int LOCATION_BROADCAST_REQ_CODE = 1;
	public static final String LOCATION_SETTINGS_PREF_FILE = "LocationSettingsPrefFile";
	public static final String DEFAULT_ALARM_FREQ = String.valueOf(AlarmManager.INTERVAL_HALF_HOUR);
	public static final long MIN_LOCATION_UPDATE_TIME = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
	public static final long MIN_LOCATION_UPDATE_DISTANCE = 10;
	public static final String LAST_LOCATION_UPDATE_LAT = "lastLocationUpdateLat";
	public static final String LAST_LOCATION_UPDATE_LNG = "lastLocationUpdateLng";
	public static final int LNG_COLUMN_INDEX = 3;
	public static final int LAT_COLUMN_INDEX = 2;

}