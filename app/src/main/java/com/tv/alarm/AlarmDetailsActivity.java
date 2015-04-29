package com.tv.alarm;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TimePicker;

public class AlarmDetailsActivity extends Activity
{

	private AlarmDBHelper alarmDBHelper = new AlarmDBHelper(this);
	private Alarm alarmDetails;
	private TimePicker timePicker;

    @Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

		ActionBar actionBar  = getActionBar();
		if(actionBar!=null){
			actionBar.setTitle("Edit alarm");
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		timePicker = (TimePicker) findViewById(R.id.alarm_edit);
        CustomToggle weeklyToggle = (CustomToggle) findViewById(R.id.weeklyToggle);
        CustomToggle mondayToggle = (CustomToggle) findViewById(R.id.mondayToggle);
        CustomToggle tuesdayToggle = (CustomToggle) findViewById(R.id.tuesdayToggle);
        CustomToggle wednesdayToggle = (CustomToggle) findViewById(R.id.wednesdayToggle);
        CustomToggle thursdayToggle = (CustomToggle) findViewById(R.id.thursdayToggle);
        CustomToggle fridayToggle = (CustomToggle) findViewById(R.id.fridayToggle);
        CustomToggle saturdayToggle = (CustomToggle) findViewById(R.id.saturdayToggle);
        CustomToggle sundayToggle = (CustomToggle) findViewById(R.id.sundayToggle);

        weeklyToggle.setOnCheckedChangeListener(new CustomToggle.OnToggleChanged() {
            @Override
            public void onToggle(boolean on)
            {
                alarmDetails.repeatWeekly = on;
            }
        });
        mondayToggle.setOnCheckedChangeListener(new CustomToggle.OnToggleChanged() {
            @Override
            public void onToggle(boolean on)
            {
                alarmDetails.setDayState(Alarm.MONDAY, on);
            }
        });
        tuesdayToggle.setOnCheckedChangeListener(new CustomToggle.OnToggleChanged() {
            @Override
            public void onToggle(boolean on)
            {
                alarmDetails.setDayState(Alarm.TUESDAY, on);
            }
        });
        wednesdayToggle.setOnCheckedChangeListener(new CustomToggle.OnToggleChanged() {
            @Override
            public void onToggle(boolean on)
            {
                alarmDetails.setDayState(Alarm.WEDNESDAY, on);
            }
        });
        thursdayToggle.setOnCheckedChangeListener(new CustomToggle.OnToggleChanged() {
            @Override
            public void onToggle(boolean on)
            {
                alarmDetails.setDayState(Alarm.THURSDAY, on);
            }
        });
        fridayToggle.setOnCheckedChangeListener(new CustomToggle.OnToggleChanged() {
            @Override
            public void onToggle(boolean on)
            {
                alarmDetails.setDayState(Alarm.FRIDAY, on);
            }
        });
        saturdayToggle.setOnCheckedChangeListener(new CustomToggle.OnToggleChanged() {
            @Override
            public void onToggle(boolean on)
            {
                alarmDetails.setDayState(Alarm.SATURDAY, on);
            }
        });
        sundayToggle.setOnCheckedChangeListener(new CustomToggle.OnToggleChanged() {
            @Override
            public void onToggle(boolean on)
            {
                alarmDetails.setDayState(Alarm.SUNDAY, on);
            }
        });


		long id = getIntent().getExtras().getLong("ID");
//If the id is less than 0 the alarm is a new alarm
		if (id < 0)
		{
			alarmDetails = new Alarm();
			alarmDetails.id=-1;
		}
//Otherwise the alarm is an existing one and 
		else
		{
			alarmDetails = alarmDBHelper.getAlarm(id);

			timePicker.setCurrentMinute(alarmDetails.minute);
			timePicker.setCurrentHour(alarmDetails.hour);

			weeklyToggle.setChecked(alarmDetails.repeatWeekly);
			mondayToggle.setChecked(alarmDetails.getDayState(Alarm.MONDAY));
			tuesdayToggle.setChecked(alarmDetails.getDayState(Alarm.TUESDAY));
			wednesdayToggle.setChecked(alarmDetails.getDayState(Alarm.WEDNESDAY));
			thursdayToggle.setChecked(alarmDetails.getDayState(Alarm.THURSDAY));
			fridayToggle.setChecked(alarmDetails.getDayState(Alarm.FRIDAY));
			saturdayToggle.setChecked(alarmDetails.getDayState(Alarm.SATURDAY));
			sundayToggle.setChecked(alarmDetails.getDayState(Alarm.SUNDAY));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.alarm_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		switch (item.getItemId())
		{
			case android.R.id.home:
			{
//Action bar back button was pressed 
				finish();
				break;
			}
			case R.id.action_save_alarm_details:
			{
//Save button was pressed. Save alarm to the database
                alarmDetails.minute = timePicker.getCurrentMinute();
                alarmDetails.hour = timePicker.getCurrentHour();
                alarmDetails.enabled = true;

				AlarmManagerHelper.cancelAlarms(this);

				if (alarmDetails.id < 0)
				{
					alarmDBHelper.createAlarm(alarmDetails);
				}
				else
				{
					alarmDBHelper.updateAlarm(alarmDetails);
				}

				AlarmManagerHelper.setAlarms(this);

				setResult(RESULT_OK);
				finish();
			}
		}

		return super.onOptionsItemSelected(item);
	}
}
