package com.lbsapp.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.text.format.DateUtils;

import com.lbsapp.R;
import com.lbsapp.Utils.Constants;

/**
 * Setting menu with location settings options such as enable/disable GPS,
 * Network Option provided for the users to begin tracking at the boot of the
 * device Change the frequency of the tracking.
 * 
 * @author Deeson
 * 
 */
public class LocationSettingsActivity extends PreferenceActivity {

	static Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getApplicationContext();
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new LocationPrefsFragment()).commit();

	}

	public static class LocationPrefsFragment extends PreferenceFragment {

		private CheckBoxPreference gpsProviderPref;
		private CheckBoxPreference networkProviderPref;
		private CheckBoxPreference bootTrackingPref;

		private ListPreference alarmFreqPref;

		SharedPreferences preferences;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.location_pref);
			preferences = this.getActivity().getSharedPreferences(
					Constants.LOCATION_SETTINGS_PREF_FILE, MODE_PRIVATE);
			updatePreferences();
		}

		private void updatePreferences() {
			gpsProviderPref = (CheckBoxPreference) findPreference(getString(R.string.gps_provider_key));
			gpsProviderPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					editBooleanPreference(preference, newValue);
					return true;
				}
			});

			networkProviderPref = (CheckBoxPreference) findPreference(getString(R.string.network_provider_key));
			networkProviderPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					editBooleanPreference(preference, newValue);
					return true;
				}
			});

			bootTrackingPref = (CheckBoxPreference) findPreference(getString(R.string.boot_tracking_key));
			bootTrackingPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					editBooleanPreference(preference, newValue);
					return true;
				}
			});

			alarmFreqPref = (ListPreference) findPreference(getString(R.string.alarm_freq_key));
			alarmFreqPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					if (newValue instanceof String) {
						SharedPreferences.Editor editor = preferences.edit();
						editor.putString(preference.getKey(), (String) newValue);
						editor.commit();
					}

					return true;
				}
			});
		}

		private void editBooleanPreference(Preference preference, Object newValue) {
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean(preference.getKey(), (Boolean) newValue);
			editor.commit();
		}

	}
}
