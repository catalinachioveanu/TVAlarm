package com.tv.alarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class AlarmDBHelper extends SQLiteOpenHelper
{
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "alarmClock.db";

	public static final String TABLE_NAME = "alarms";

	public static final String COLUMN_NAME_ID = "_id";
	public static final String COLUMN_NAME_ALARM_TIME_HOUR = "hour";
	public static final String COLUMN_NAME_ALARM_TIME_MINUTE = "minute";
	public static final String COLUMN_NAME_ALARM_REPEAT_DAYS = "days";
	public static final String COLUMN_NAME_ALARM_REPEAT_WEEKLY = "repeat_weekly";
	public static final String COLUMN_NAME_ALARM_ENABLED = "enabled";


	private static final String SQL_CREATE_ALARM = "CREATE TABLE " + TABLE_NAME + " (" +
			COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
			COLUMN_NAME_ALARM_TIME_HOUR + " INTEGER," +
			COLUMN_NAME_ALARM_TIME_MINUTE + " INTEGER," +
			COLUMN_NAME_ALARM_REPEAT_DAYS + " TEXT," +
			COLUMN_NAME_ALARM_REPEAT_WEEKLY + " BOOLEAN," +
			COLUMN_NAME_ALARM_ENABLED + " BOOLEAN" + " )";

	private static final String SQL_DELETE_ALARM = "DROP TABLE IF EXISTS " + TABLE_NAME;

	public AlarmDBHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(SQL_CREATE_ALARM);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL(SQL_DELETE_ALARM);
		onCreate(db);
	}


	/**
	 * Parses the database cursor to an Alarm model
	 *
	 * @param cursor The cursor to be parsed
	 * @return The parsed Alarm model
	 */
	private Alarm cursorToAlarm(Cursor cursor)
	{
		Alarm model = new Alarm();
		model.id = cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_ID));
		model.hour = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_ALARM_TIME_HOUR));
		model.minute = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_ALARM_TIME_MINUTE));
		model.repeatWeekly = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_ALARM_REPEAT_WEEKLY)) != 0;
		model.enabled = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_ALARM_ENABLED)) != 0;

		String[] repeatingDays = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ALARM_REPEAT_DAYS)).split(",");

		for (int i = 0; i < repeatingDays.length; ++i)
		{
			model.setDayState(i, !repeatingDays[i].equals("false"));
		}

		return model;
	}

	/**
	 * Parses the Alarm model to database ContentValues
	 *
	 * @param model The Alarm to be parsed
	 * @return The parsed ContentValues
	 */
	private ContentValues alarmToContentValues(Alarm model)
	{
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_ALARM_TIME_HOUR, model.hour);
		values.put(COLUMN_NAME_ALARM_TIME_MINUTE, model.minute);
		values.put(COLUMN_NAME_ALARM_REPEAT_WEEKLY, model.repeatWeekly);
		values.put(COLUMN_NAME_ALARM_ENABLED, model.enabled);

		String repeatingDays = "";
		for (int i = 0; i < 7; ++i)
		{
			repeatingDays += model.getDayState(i) + ",";
		}
		values.put(COLUMN_NAME_ALARM_REPEAT_DAYS, repeatingDays);

		return values;
	}

	/**
	 * Saves the given Alarm to the alarms table in the database
	 *
	 * @param model The alarm to be saved to the database
	 * @return The database ID of the alarm in the table
	 */
	public long createAlarm(Alarm model)
	{
		ContentValues values = alarmToContentValues(model);
		SQLiteDatabase database = getWritableDatabase();
		try
		{
			return database.insert(TABLE_NAME, null, values);

		}
		catch (Exception ex)
		{
			Log.v("EXCEPTION", ex.getMessage());
			return 0;
		}
	}


	/**
	 * Updates an existing alarm in the database with the given values
	 *
	 * @param model The alarm model that needs updating
	 * @return The ID of the updated alarm
	 */
	public long updateAlarm(Alarm model)
	{
		ContentValues values = alarmToContentValues(model);
		return getWritableDatabase().update(TABLE_NAME, values, COLUMN_NAME_ID + " = ?", new String[]{String.valueOf(model.id)});
	}

	/**
	 * Retrieves an Alarm from the database for the given ID
	 *
	 * @param id The ID of the alarm
	 * @return The alarm corresponding to the given ID; null if this is not found
	 */

	public Alarm getAlarm(long id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		String select = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME_ID + " = " + id;
		Cursor c = db.rawQuery(select, null);
		if (c.moveToNext())
		{
			return cursorToAlarm(c);
		}
		return null;
	}

	/**
	 * Retrieves a list of all the alarms found in the database
	 *
	 * @return List of existing alarms in the database; Empty list if none are found
	 */
	public List<Alarm> getAllAlarms()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		String select = "SELECT * FROM " + TABLE_NAME;
		Cursor c = db.rawQuery(select, null);
		List<Alarm> alarmList = new ArrayList<>();
		while (c.moveToNext())
		{
			alarmList.add(cursorToAlarm(c));
		}
		return alarmList;
	}

	/**
	 * Removes an alarm from the database for a given ID
	 *
	 * @param id The ID of the alarm to be removed
	 * @return The ID of the removed alarm
	 */
	public int removeAlarm(long id)
	{
		return getWritableDatabase().delete(TABLE_NAME, COLUMN_NAME_ID + " = ?", new String[]{String.valueOf(id)});
	}
}
