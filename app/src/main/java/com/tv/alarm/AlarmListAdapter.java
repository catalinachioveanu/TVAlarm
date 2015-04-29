package com.tv.alarm;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import com.tv.alarm.R;

public class AlarmListAdapter extends BaseAdapter
{
	private Context context;
	private List<Alarm> alarms;

	public AlarmListAdapter(Context context, List<Alarm> alarms)
	{
		this.context = context;
		this.alarms = alarms;
	}

	public void setAlarms(List<Alarm> alarms)
	{
		this.alarms = alarms;
	}

	@Override
	public int getCount()
	{
		if (alarms != null)
		{
			return alarms.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position)
	{
		if (alarms != null)
		{
			return alarms.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		if (alarms != null)
		{
			return alarms.get(position).id;
		}
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent)
	{
		if (view == null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			ViewHolder holder = new ViewHolder();
			view = inflater.inflate(R.layout.alarm_list_item, parent, false);
			holder.alarm_item_monday = (TextView) view.findViewById(R.id.alarm_item_monday);
			holder.alarm_item_tuesday = (TextView) view.findViewById(R.id.alarm_item_tuesday);
			holder.alarm_item_wednesday = (TextView) view.findViewById(R.id.alarm_item_wednesday);
			holder.alarm_item_thursday = (TextView) view.findViewById(R.id.alarm_item_thursday);
			holder.alarm_item_friday = (TextView) view.findViewById(R.id.alarm_item_friday);
			holder.alarm_item_saturday = (TextView) view.findViewById(R.id.alarm_item_saturday);
			holder.alarm_item_sunday = (TextView) view.findViewById(R.id.alarm_item_sunday);
			holder.timeTextView = (TextView) view.findViewById(R.id.alarm_item_time);
			holder.enabledToggle = (CustomToggle) view.findViewById(R.id.alarm_item_toggle);


			view.setTag(holder);
		}
		final ViewHolder holder = (ViewHolder) view.getTag();
		Alarm alarm = (Alarm) getItem(position);

		holder.timeTextView.setText(String.format("%02d : %02d", alarm.hour, alarm.minute));

		setTextColor(holder.alarm_item_monday, alarm.getDayState(Alarm.MONDAY));
		setTextColor(holder.alarm_item_tuesday, alarm.getDayState(Alarm.TUESDAY));
		setTextColor(holder.alarm_item_wednesday, alarm.getDayState(Alarm.WEDNESDAY));
		setTextColor(holder.alarm_item_thursday, alarm.getDayState(Alarm.THURSDAY));
		setTextColor(holder.alarm_item_friday, alarm.getDayState(Alarm.FRIDAY));
		setTextColor(holder.alarm_item_saturday, alarm.getDayState(Alarm.SATURDAY));
		setTextColor(holder.alarm_item_sunday,  alarm.getDayState(Alarm.SUNDAY));

		holder.enabledToggle.setChecked(alarm.enabled);
		holder.enabledToggle.setTag(alarm.id);
		holder.enabledToggle.setOnCheckedChangeListener(new CustomToggle.OnToggleChanged()
		{
			@Override
			public void onToggle(boolean on)
			{((AlarmListActivity) context).setAlarmEnabled((Long) holder.enabledToggle.getTag(), on);	}
		});

		return view;
	}

	private class ViewHolder
	{
		public TextView alarm_item_monday;
		public TextView alarm_item_tuesday;
		public TextView alarm_item_wednesday;
		public TextView alarm_item_thursday;
		public TextView alarm_item_friday;
		public TextView alarm_item_saturday;
		public TextView alarm_item_sunday;
		public TextView timeTextView;
		public CustomToggle enabledToggle;
	}

	private void setTextColor(TextView view, boolean enabled)
	{
		if (enabled)
		{
			view.setTextColor(Color.BLACK);
		}
		else
		{
			view.setTextColor(Color.GRAY);
		}
	}
}
