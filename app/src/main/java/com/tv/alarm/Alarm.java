package com.tv.alarm;

public class Alarm {

	public static final int MONDAY = 0;
	public static final int TUESDAY = 1;
	public static final int WEDNESDAY = 2;
	public static final int THURSDAY = 3;
	public static final int FRIDAY = 4;
	public static final int SATURDAY = 5;
    public static final int SUNDAY = 6;

    public long id;
	public int hour;
	public int minute;
	private boolean enabledDays[];
	public boolean repeatWeekly;
	public boolean enabled;
	
	public Alarm() {
		enabledDays = new boolean[7];
	}

	/**
	 * Sets the state for a specific day
	 * @param dayOfWeek Day for which the information is being set ex: Alarm.MONDAY
	 * @param value The value for the specified day ex: true( for enabled), false (for disabled)
	 */
	public void setDayState(int dayOfWeek, boolean value) {
		enabledDays[dayOfWeek] = value;
	}

	/**
	 * Retrieves the state of a given day. Enabled/disabled
	 * @param dayOfWeek Day for which the information is requested ex: Alarm.MONDAY
	 * @return The state if the day. True if the alarm for the given day is enabled, false otherwise
	 */
	public boolean getDayState(int dayOfWeek) {
		return enabledDays[dayOfWeek];
	}
	
}
