package com.tv.alarm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AlarmService extends Service
{
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.v("TAG", "1");
		Intent alarmIntent = new Intent(getBaseContext(), AlarmScreen.class);
		Log.v("TAG", "2");
		alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Log.v("TAG", "3");
		if (intent != null)
		{
			alarmIntent.putExtras(intent);
			Log.v("TAG", "4");
			getApplication().startActivity(alarmIntent);
		}
		else
		{
			Log.v("TAG", "Intent was null");
			return START_REDELIVER_INTENT;
		}
		Log.v("TAG", "5");
		AlarmManagerHelper.setAlarms(this);
		Log.v("TAG", "6");
		return super.onStartCommand(intent, flags, startId);
	}

}