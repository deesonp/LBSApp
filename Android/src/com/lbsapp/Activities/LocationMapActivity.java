package com.lbsapp.Activities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.lbsapp.R;
import com.lbsapp.Common.UserLocation;
import com.lbsapp.Utils.Constants;
import com.lbsapp.Utils.DatabaseAdapter;

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
	   new LoadAllLocations().execute();
	}

	private class LoadAllLocations extends AsyncTask<Void, Void, ArrayList<LatLng>> {

		@Override
		protected ArrayList<LatLng> doInBackground(Void... arg0) {
			ArrayList<LatLng> allLocationTraveled = new ArrayList<LatLng>();
			DatabaseAdapter db = new DatabaseAdapter(LocationMapActivity.this);
			db.open();
			Cursor allLocation = db.getAllLocations();
			allLocation.moveToFirst();
			while (allLocation.isAfterLast() == false) {
				String latitude = allLocation.getString(Constants.LAT_COLUMN_INDEX);
				String longitude = allLocation.getString(Constants.LNG_COLUMN_INDEX);
				LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
				allLocationTraveled.add(latLng);
				Log.d(Constants.LOG_TAG, "FROM DB :"
						+latitude+ ", "
						+longitude);
				allLocation.moveToNext();
			}
			db.close();
			return allLocationTraveled;

		}
		
		@Override
		protected void onPostExecute(ArrayList<LatLng> result) {
			super.onPostExecute(result);
			int i = 1;
			for (LatLng ll : result) {
				Log.d(Constants.LOG_TAG, "FROM LL :"
						+ll.latitude+ ", "
						+ll.longitude);
				map.addMarker(new MarkerOptions().position(ll).title(String.valueOf(i)));
				i++;
			}
			if(result.size() >0) {
				LatLng last = result.get(result.size() - 1);
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(last, 15));
				map.animateCamera(CameraUpdateFactory.zoomTo(10), 200, null);
				map.addPolyline(new PolylineOptions().addAll(result).width(2).color(Color.RED));
			} 
			

		}

	}

	
}
