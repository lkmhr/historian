package com.lkmhr.historian.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseManager extends SQLiteOpenHelper {

	private final static String DATABASE_NAME = "historian";
	private final static int DATABASE_VERSION = 1;
	private final static String TABLE_EVENTS = "events_db";
	private final static String TABLE_FAVS = "favs_db";
	
	private final static String KEY_ID = "_id";
	private final static String KEY_DATE = "date";
	private final static String KEY_DATA = "data";
	private final static String KEY_TYPE = "type";
	private final static String KEY_FAV = "fav";
	
	
	public DatabaseManager(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createEventsDb = "CREATE TABLE " + TABLE_EVENTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
				+ KEY_DATE + " TEXT, "
				+ KEY_DATA + " TEXT, "
				+ KEY_TYPE + " TEXT, "
				+ KEY_FAV + " TEXT "
                + ")";
		String createFavsDb = "CREATE TABLE " + TABLE_FAVS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
				+ KEY_DATE + " TEXT, "
				+ KEY_DATA + " TEXT, "
				+ KEY_TYPE + " TEXT, "
				+ KEY_FAV + " TEXT "
                + ")";
		
		db.execSQL(createEventsDb);
		db.execSQL(createFavsDb);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVS);
		 
        onCreate(db);
	}

	public void insertTodaysEvents(List<HistoryEvent> events){
		SQLiteDatabase db = this.getWritableDatabase();
		
		Log.v("DB", "SIZE " + events.size());
		
		for(HistoryEvent event : events){
			ContentValues values = new ContentValues();
			values.put(KEY_DATE, event.getEventDate());
			values.put(KEY_DATA, event.getEventData());
			values.put(KEY_TYPE, event.getEventType());
			values.put(KEY_FAV, String.valueOf(event.isFav()));

			db.insert(TABLE_EVENTS, null, values);
		}
		
		db.close();
	}
	
	public void addToFav(HistoryEvent event) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_DATE, event.getEventDate());
		values.put(KEY_DATA, event.getEventData());
		values.put(KEY_TYPE, event.getEventType());
		values.put(KEY_FAV, String.valueOf(event.isFav()));
		
		db.insert(TABLE_FAVS, null, values);
		db.close();
		
		setEventFav(event, true);
	}
	
	public void setEventFav(HistoryEvent event, boolean flag) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();		
		values.put(KEY_FAV, String.valueOf(flag));
		
		db.update(TABLE_EVENTS, values, KEY_ID + " = ?", new String[] {event.getEventId()});
		db.close();
	}	
	
	public List<HistoryEvent> getDailyEventsByType(String type){
		List<HistoryEvent> list = new ArrayList<HistoryEvent>();
		
		String query = "SELECT * FROM " + TABLE_EVENTS + " WHERE " + KEY_TYPE + " = '" + type + "'";
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		
		
		int iId = cursor.getColumnIndex(KEY_ID);
		int iDate = cursor.getColumnIndex(KEY_DATE);
		int iData = cursor.getColumnIndex(KEY_DATA);
		int iType = cursor.getColumnIndex(KEY_TYPE);
		int iFav = cursor.getColumnIndex(KEY_FAV);
		
		if(cursor.moveToFirst()){
			do {
				HistoryEvent event = new HistoryEvent();
				event.setEventId(cursor.getString(iId));
				event.setEventDate(cursor.getString(iDate));
				event.setEventData(cursor.getString(iData));
				event.setEventType(cursor.getString(iType));
				event.setFav(cursor.getString(iFav).equals("true")? true : false);
				
				list.add(event);
			} while(cursor.moveToNext());
		}
		
		cursor.close();
		db.close();
		
		return list;
	}
//	
//	public List<String> getAllEventByType(String type){
//		List<String> list = new ArrayList<String>();
//		
//		String query = "SELECT * FROM " + TABLE_EVENTS + " WHERE type = '" + type + "'";		
//		
//		SQLiteDatabase db = this.getReadableDatabase();
//		Cursor cursor = db.rawQuery(query, null);
//		
//		int iData = cursor.getColumnIndex(KEY_DATA);
//		
//		if(cursor.moveToFirst()){
//			do {
//				list.add(cursor.getString(iData));
//			} while(cursor.moveToNext());
//		} else {
//			Log.v("DB", type + " EMPTY");
//		}
//		cursor.close();
//		db.close();
//		return list;
//		
//	}	
	public boolean hasOfType(String type) {
		
		String query = "SELECT * FROM " + TABLE_EVENTS + " WHERE type = '" + type + "'" ;		
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		
		if(cursor.moveToFirst()){
			cursor.close();
			db.close();
			return true;
		} 
		cursor.close();
		db.close();
		
		return false;
	}
	
//	public void getDailyEventByType(String type){
//		
//	}
	
}
