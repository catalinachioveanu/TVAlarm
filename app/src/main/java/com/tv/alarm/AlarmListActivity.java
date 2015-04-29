package com.tv.alarm;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.tv.alarm.R;

public class AlarmListActivity extends ListActivity
{
	private AlarmListAdapter adapter;
	private AlarmDBHelper alarmDBHelper = new AlarmDBHelper(this);
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm_list);
		Button addButton = (Button) findViewById(R.id.addButton);
		addButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				startAlarmDetailsActivity(-1);
			}
		});

		context = this;

		ListView list = (ListView) findViewById(android.R.id.list);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				selectItem(position);
			}
		});
		list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
			{
				longCLickItem(position);
				return true;
			}
		});

		adapter = new AlarmListAdapter(this, alarmDBHelper.getAllAlarms());
		setListAdapter(adapter);
	}

	private void longCLickItem(int position)
	{
		Alarm alarm = (Alarm) adapter.getItem(position);
		removeAlarm(alarm.id);
	}

	private void selectItem(int position)
	{
		Alarm alarm = (Alarm) adapter.getItem(position);
		startAlarmDetailsActivity(alarm.id);
	}




	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_add, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		switch (item.getItemId())
		{
			case R.id.add_menu_item:
			{
				startAlarmDetailsActivity(-1);
				break;
			}
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK)
		{
			adapter.setAlarms(alarmDBHelper.getAllAlarms());
			adapter.notifyDataSetChanged();
		}
	}

	public void setAlarmEnabled(long id, boolean isEnabled)
	{
		AlarmManagerHelper.cancelAlarms(this);

		Alarm model = alarmDBHelper.getAlarm(id);
		model.enabled = isEnabled;
		alarmDBHelper.updateAlarm(model);

		AlarmManagerHelper.setAlarms(this);
	}

	public void startAlarmDetailsActivity(long id)
	{
		Intent intent = new Intent(this, AlarmDetailsActivity.class);
		intent.putExtra("ID", id);
		startActivityForResult(intent, 0);
	}

	public void removeAlarm(long id)
	{
		final long alarmId = id;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Please confirm")
				.setTitle("Delete set?")
				.setCancelable(true)
				.setNegativeButton("Cancel", null)
				.setPositiveButton("Ok", new OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						//Cancel Alarms
						AlarmManagerHelper.cancelAlarms(context);
						alarmDBHelper.removeAlarm(alarmId);
						adapter.setAlarms(alarmDBHelper.getAllAlarms());
						adapter.notifyDataSetChanged();
						AlarmManagerHelper.setAlarms(context);
					}
				}).show();
	}
}
