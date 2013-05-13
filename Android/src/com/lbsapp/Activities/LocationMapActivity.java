package com.lbsapp.Activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.lbsapp.R;
import com.lbsapp.Utils.Constants;
import com.lbsapp.Utils.DatabaseAdapter;

/**
 * Display the user's history on a map with a marker. The marker displays the
 * date and time the location was tracked.
 * 
 * @author Deeson
 * 
 */
public class LocationMapActivity extends Activity {

	private GoogleMap map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_map);
		MapFragment mapFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		map = mapFrag.getMap();

	}

	@Override
	protected void onStart() {
		super.onStart();
		new LoadAllLocations(this).execute();
	}

	/**
	 * Load all the locations from the database onto the map with a marker
	 * 
	 * @author Deeson
	 * 
	 */
	private class LoadAllLocations extends AsyncTask<Void, Void, ArrayList<MarkerMetaData>> {

		Context mContext;

		public LoadAllLocations(Context context) {
			mContext = context;
		}

		@Override
		protected ArrayList<MarkerMetaData> doInBackground(Void... arg0) {
			Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
			ArrayList<MarkerMetaData> allLocationTraveled = new ArrayList<MarkerMetaData>();
			DatabaseAdapter db = new DatabaseAdapter(LocationMapActivity.this);
			db.open();
			Cursor allLocation = db.getAllLocations();
			allLocation.moveToFirst();
			while (allLocation.isAfterLast() == false) {
				String timestamp = allLocation.getString(Constants.TIMESTAMP_COLUMN_INDEX);
				double latitude = Double.parseDouble(allLocation
						.getString(Constants.LAT_COLUMN_INDEX));
				double longitude = Double.parseDouble(allLocation
						.getString(Constants.LNG_COLUMN_INDEX));
				LatLng latLng = new LatLng(latitude, longitude);
				
				List<Address> addresses = null;
				try {
					addresses = geocoder.getFromLocation(latitude, longitude, 1);
				} catch (IOException e) {
					Log.e(Constants.LOG_TAG, e.getMessage());
				}
				
				if (addresses != null && addresses.size() > 0) {
					Address address = addresses.get(0);
					String addressText = String.format("%s, %s, %s",
							address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
							address.getLocality(), address.getCountryName());
					allLocationTraveled.add(new MarkerMetaData(latLng, timestamp,addressText));	
				} else {
					allLocationTraveled.add(new MarkerMetaData(latLng, timestamp));	
				}
				allLocation.moveToNext();
			}
			db.close();
			return allLocationTraveled;

		}

		@Override
		protected void onPostExecute(ArrayList<MarkerMetaData> result) {
			super.onPostExecute(result);
			int i = 1;
			for (MarkerMetaData m : result) {
				map.addMarker(new MarkerOptions().position(m.latlng).title(m.address).snippet(m.timeStamp));
				i++;
			}

			// Create a line between all points
			if (result.size() > 0) {
				MarkerMetaData last = (MarkerMetaData) result.get(result.size() - 1);
				moveAndAnimateCamera(last.latlng);
			} else {
				// If the result set of size zero, show current location and if
				// current not found show last location
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(LocationMapActivity.this);
				double lat = prefs.getFloat(Constants.CURRENT_LOCATION_LAT, 0.0f);
				double lng = prefs.getFloat(Constants.CURRENT_LOCATION_LNG, 0.0f);
				String tag = getString(R.string.current_location);
				if (lat == 0 && lng == 0) {
					lat = prefs.getFloat(Constants.LAST_LOCATION_UPDATE_LAT, 0.0f);
					lng = prefs.getFloat(Constants.LAST_LOCATION_UPDATE_LNG, 0.0f);
					tag = getString(R.string.last_known_location);
				}
				LatLng ll = new LatLng(lat, lng);
				map.addMarker(new MarkerOptions().position(ll).title(tag));
				moveAndAnimateCamera(ll);
			}

		}

		// Move the map camera and zoom to the location
		private void moveAndAnimateCamera(LatLng latLng) {
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
			map.animateCamera(CameraUpdateFactory.zoomTo(10), 200, null);
		}

	}
	
	public class MarkerMetaData {
		private LatLng latlng;
		private String timeStamp;
		private String address;

		public MarkerMetaData(LatLng ll, String time) {
			this.latlng = ll;
			this.timeStamp = time;
		}

		public MarkerMetaData(LatLng ll, String time, String address) {
			this.latlng = ll;
			this.timeStamp = time;
			this.address = address;
		}
	}

}
