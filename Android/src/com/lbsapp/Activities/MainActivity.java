package com.lbsapp.Activities;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lbsapp.R;
import com.lbsapp.Common.UserLocation;
import com.lbsapp.Utils.Constants;
import com.lbsapp.Utils.DatabaseAdapter;

/**
 * Application entry point. Display current and last location with map
 * indication Allows users to track the device and shows status of the service
 * 
 * @author Deeson
 * 
 */
public class MainActivity extends Activity {

	TextView currentLocation;
	TextView lastLocation;
	TextView trackingStatus;
	TextView totalDistanceTravelled;

	Button startStopTracking;
	Button viewHistory;

	DatabaseAdapter dbAdapter;
	boolean alarmEnabled = false;
	boolean gpsEnabled = false;
	boolean networkEnabled = false;

	LocationManager lm;
	Geocoder geoCoder;

	SharedPreferences prefs;

	private GoogleMap map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		dbAdapter = new DatabaseAdapter(this);

		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		geoCoder = new Geocoder(this);

		currentLocation = (TextView) findViewById(R.id.current_location);
		lastLocation = (TextView) findViewById(R.id.last_location);
		trackingStatus = (TextView) findViewById(R.id.tracking_status);
		totalDistanceTravelled = (TextView) findViewById(R.id.total_distance_travelled);

		startStopTracking = (Button) findViewById(R.id.start_stop_tracking_button);
		viewHistory = (Button) findViewById(R.id.view_history_button);

		viewHistory.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openLocationMap();
			}
		});

		startStopTracking.setOnClickListener(new StartStopTrackingListener());

		MapFragment mapFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		map = mapFrag.getMap();
		totalDistanceTravelled.setText(R.string.no_meters);
		
		Location loc = UserLocation.getBestLocation(this);
		if (loc != null) {
			lastLocation.setText(R.string.last_location_default);
			new ReverseGeocodingTask(this, lastLocation).execute(new Location[] { loc });
		} else {
			lastLocation.setText(R.string.last_location_unable);
		}

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		alarmEnabled = prefs.getBoolean(Constants.ALARM_ENABLED_KEY, false);
	}

	@Override
	protected void onStart() {
		super.onStart();
		setTrackingStatus();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		dbAdapter.open();
		gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (gpsEnabled || networkEnabled) {
			updateCurrentLocation();
		} else {
			currentLocation.setText(R.string.location_services_unavil);
		}

		updateTotalDistanceTraveled();
	}

	@Override
	protected void onPause() {
		super.onPause();
		dbAdapter.close();
		lm.removeUpdates(locListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_map:
			openLocationMap();
			return true;
		case R.id.action_settings:
			openLocationSettings();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	LocationListener locListener = new LocationListener() {
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
			if (location != null) {
				saveCurrentLocToPref(prefs, location);
				(new ReverseGeocodingTask(MainActivity.this, currentLocation))
						.execute(new Location[] { location });
				LatLng newLoc = new LatLng(location.getLatitude(), location.getLongitude());
				map.addMarker(new MarkerOptions().position(newLoc).title(getString(R.string.current_location)));
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(newLoc, 15));
				map.animateCamera(CameraUpdateFactory.zoomTo(10), 200, null);
			}

		}
	};

	/**
	 * AsyncTask encapsulating the reverse-geocoding API. Since the geocoder API
	 * is blocked,
	 * we do not want to invoke it from the UI thread.
	 * @author http://developer.android.com/training/basics/location/currentlocation.html
	 *
	 */
	private class ReverseGeocodingTask extends AsyncTask<Location, Void, Void> {
		Context mContext;
		String addressText = null;
		TextView txtView;

		public ReverseGeocodingTask(Context context,TextView txtView) {
			super();
			mContext = context;
			this.txtView = txtView;
		}

		@Override
		protected Void doInBackground(Location... params) {
			Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

			Location loc = params[0];
			List<Address> addresses = null;
			try {
				// Call the synchronous getFromLocation() method by passing in
				// the lat/long values.
				addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
			} catch (IOException e) {
				Log.e(Constants.LOG_TAG, e.getMessage());
			}
			if (addresses != null && addresses.size() > 0) {
				Address address = addresses.get(0);
				// Format the first line of address (if available), city, and
				// country name.
				addressText = String.format("%s, %s, %s",
						address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
						address.getLocality(), address.getCountryName());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (addressText != null) {
				txtView.setText(addressText);
			}
		}
	}


	/**
	 * Find total distance travelled based on past location points
	 */
	private void updateTotalDistanceTraveled() {
		List<Location> locationHistory = getLocationHistory();
		float[] results = new float[5];
		float totalDist = 0.0f;
		for (int i = 0; i < locationHistory.size(); i++) {
			if ((i + 1) < locationHistory.size()) {
				Location startLocation = locationHistory.get(i);
				Location endLocation = locationHistory.get(i + 1);
				Location.distanceBetween(startLocation.getLatitude(), startLocation.getLongitude(),
						endLocation.getLatitude(), endLocation.getLongitude(), results);
				totalDist += results[0];
			} else {
				break;
			}
		}

		NumberFormat numberFormat = NumberFormat.getNumberInstance();
		numberFormat.setMinimumFractionDigits(2);
		numberFormat.setMaximumFractionDigits(2);
		totalDistanceTravelled.setText(numberFormat.format(totalDist) + " meters");

	}

	private List<Location> getLocationHistory() {
		List<Location> locationHistory = new ArrayList<Location>();
		Cursor allLoc = dbAdapter.getAllLocations();
		allLoc.moveToFirst();
		while (allLoc.isAfterLast() == false) {
			double lat = Double.parseDouble(allLoc.getString(Constants.LAT_COLUMN_INDEX));
			double lng = Double.parseDouble(allLoc.getString(Constants.LNG_COLUMN_INDEX));
			Location loc = new Location("HISTORY");
			loc.setLatitude(lat);
			loc.setLongitude(lng);
			locationHistory.add(loc);
			allLoc.moveToNext();
		}
		return locationHistory;
	}

	private void updateCurrentLocation() {
		Criteria currentLocReqCriteria = new Criteria();
		currentLocReqCriteria.setPowerRequirement(Criteria.POWER_LOW);
		currentLocation.setText(R.string.req_current_location_msg);
		lm.requestSingleUpdate(currentLocReqCriteria, locListener, null);
	}

	private class StartStopTrackingListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (!gpsEnabled) {
				new EnableGPSDialog().show(getFragmentManager(), "enableLocationService");
			} else {
				UserLocation.startAndStopAlarm(MainActivity.this, alarmEnabled = !alarmEnabled);
				prefs.edit().putBoolean(Constants.ALARM_ENABLED_KEY, alarmEnabled).commit();
				setTrackingStatus();
			}
		}

	}

	/**
	 * Dialog to allow users to enable Location services if turned off
	 * 
	 * @author Deeson
	 * 
	 */
	public static class EnableGPSDialog extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
			dialogBuilder.setTitle(R.string.enable_location_service);
			dialogBuilder.setMessage(R.string.enable_location_dialog_msg);
			dialogBuilder.setPositiveButton(R.string.enable_location_service,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							Intent settingsIntent = new Intent(
									Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivity(settingsIntent);
							dismiss();
						}

					});
			dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dismiss();
				}
			});
			return dialogBuilder.create();
		}
	}

	private void setTrackingStatus() {
		if (alarmEnabled) {
			trackingStatus.setText(getString(R.string.tracking_enabled));
			trackingStatus.setTextColor(Color.GREEN);
		} else {
			trackingStatus.setText(getString(R.string.tracking_disabled));
			trackingStatus.setTextColor(Color.RED);
		}
	}

	private static void saveCurrentLocToPref(SharedPreferences prefs, Location loc) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putFloat(Constants.CURRENT_LOCATION_LAT, (float) loc.getLatitude());
		editor.putFloat(Constants.CURRENT_LOCATION_LNG, (float) loc.getLongitude());
		editor.commit();
	}

	private void openLocationSettings() {
		Intent locationPref = new Intent(this, LocationSettingsActivity.class);
		startActivity(locationPref);
	}

	private void openLocationMap() {
		Intent locationMap = new Intent(this, LocationMapActivity.class);
		startActivity(locationMap);
	}

}
