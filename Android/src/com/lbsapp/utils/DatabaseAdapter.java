package com.lbsapp.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Create database tables and entries 
 * Retrieve tuples
 * @author Deeson
 *
 */
public class DatabaseAdapter {
	
	private static final String TAG = "DatabaseAdapter"; 
			
	private Context mContext;
	
	private static final String DB_NAME = "LBSApp.db";
	
	//Location Table & Columns
	private static final String DB_TABLE = "locations";
	private static final String KEY_TIMESTAMP = "timestamp";
	private static final String KEY_LOCATION_ID = "locationId";
	private static final String KEY_LAT = "lat";
	private static final String KEY_LNG = "lng";

	private final int DB_VER = 2;
	
	private static final String TABLE_CREATE = "CREATE TABLE "
			+ DB_TABLE + " (" + KEY_LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
			+ KEY_TIMESTAMP + " TEXT NOT NULL,"
			+ KEY_LAT +" TEXT," + KEY_LNG +" TEXT);";
	
	
	private MySQLiteHelper mySqlitHelper;
	private SQLiteDatabase db;
	private Calendar currentDate;
	private SimpleDateFormat formatter;
	
	public DatabaseAdapter(Context ctx){
		this.mContext = ctx;
		mySqlitHelper = new MySQLiteHelper(mContext);
		currentDate = Calendar.getInstance();
		formatter = new SimpleDateFormat("yyyy/MMM/dd HH:mm:ss");
	}

	public DatabaseAdapter open(){
		db = mySqlitHelper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		mySqlitHelper.close();
	}
	
	public long insertLocation(String lat, String lng){
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TIMESTAMP, formatter.format(currentDate.getTime()));
		initialValues.put(KEY_LAT, lat);
		initialValues.put(KEY_LNG, lng);
		return db.insert(DB_TABLE, null, initialValues);
	}
	
	
	public Cursor getAllLocations(){
		return db.query(DB_TABLE, new String [] {KEY_LOCATION_ID, KEY_TIMESTAMP, KEY_LAT, KEY_LNG},null,null,null,null, null);
	}

	public class MySQLiteHelper extends SQLiteOpenHelper{

		public MySQLiteHelper(Context context) {
			super(context, DB_NAME, null, DB_VER);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			if(Constants.DEBUG){
				Log.d(Constants.LOG_TAG , TAG+ " : onCreate");
			}
			db.execSQL(TABLE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if(Constants.DEBUG){
				Log.w(Constants.LOG_TAG, TAG +": "+ MySQLiteHelper.class.getName()+ ": Upgrading from db version");
			}
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
			onCreate(db);
		}
		
	}

}
