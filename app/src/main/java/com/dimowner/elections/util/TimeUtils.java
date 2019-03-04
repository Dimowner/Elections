package com.dimowner.elections.util;

import android.content.Context;

import com.dimowner.elections.AppConstants;
import com.dimowner.elections.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

	/** Date format: May 16, 10:30 AM */
	private static SimpleDateFormat messageDateFormat = new SimpleDateFormat("MMM dd, hh:mm aa", Locale.US);

	/** Date format: May 16, 03:30 PM */
	private static SimpleDateFormat dateFormat12H = new SimpleDateFormat("MMM dd, hh:mm aa", Locale.US);

	/** Date format: May 16, 15:30 */
	private static SimpleDateFormat dateFormat24H = new SimpleDateFormat("MMM dd, HH:mm", Locale.US);

	/** Date format: 15:30 04.03.2019 */
	private static SimpleDateFormat dateTimeFormatEU = new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.FRANCE);

	/** Time format: 11:30 */
	private static SimpleDateFormat timeFormatEU = new SimpleDateFormat("HH:mm", Locale.FRANCE);

	/** Time format: 22.11.2018 */
	private static SimpleDateFormat dateFormatEU = new SimpleDateFormat("dd.mm.yyyy", Locale.FRANCE);


	public static final int INTERVAL_SECOND = 1000; //mills
	public static final int INTERVAL_MINUTE = 60 * INTERVAL_SECOND;
	public static final int INTERVAL_HOUR = 60 * INTERVAL_MINUTE;
	public static final int INTERVAL_DAY = 24 * INTERVAL_HOUR;

	private TimeUtils() {}

	public static String formatTimeGMT(long timeMillsGmt) {
		if (timeMillsGmt <= 0) {
			return "";
		}
		Calendar calendar = Calendar.getInstance();
		long offset = calendar.getTimeZone().getRawOffset();
		long dstSavings = calendar.getTimeZone().getDSTSavings();

		Calendar date = Calendar.getInstance();
		date.setTimeInMillis(timeMillsGmt + offset + dstSavings);

		Calendar dayYesterday = Calendar.getInstance();
		dayYesterday.setTimeInMillis(date.getTimeInMillis());
		dayYesterday.set(Calendar.DAY_OF_YEAR, dayYesterday.get(Calendar.DAY_OF_YEAR) - 1);

		Calendar prevYear = Calendar.getInstance();
		prevYear.setTimeInMillis(date.getTimeInMillis());
		prevYear.set(Calendar.YEAR, prevYear.get(Calendar.YEAR) - 1);

		return messageDateFormat.format(new Date(timeMillsGmt + offset + dstSavings));

	}

	public static String formatTime(long timeMills) {
		if (timeMills <= 0) {
			return "";
		}
		return timeFormatEU.format(new Date(timeMills));
	}

	public static String formatTime(long timeMills, int timeFormat) {
		if (timeMills <= 0) {
			return "";
		}
		if (timeFormat == AppConstants.TIME_FORMAT_12H) {
			return dateFormat12H.format(new Date(timeMills));
		} else {
			return dateTimeFormatEU.format(new Date(timeMills));
		}
	}

	public static String formatDateSmart(long time, Context ctx) {
		if (time <= 0) {
			return "Wrong date!";
		}
		Calendar today = Calendar.getInstance();
		Calendar date = Calendar.getInstance();
		date.setTimeInMillis(time);
		if (isSameYear(today, date)) {
			if (isSameDay(today, date)) {
				return ctx.getResources().getString(R.string.today);
			} else {
				today.add(Calendar.DAY_OF_YEAR, -1); //Make yesterday
				//Check is yesterday
				if (isSameDay(today, date)) {
					return ctx.getResources().getString(R.string.yesterday);
				} else {
					return dateFormat24H.format(new Date(time));
				}
			}
		} else {
			return dateFormatEU.format(new Date(time));
		}
	}

	/**
	 * <p>Checks if two calendars represent the same day ignoring time.</p>
	 * @param cal1  the first calendar, not altered, not null
	 * @param cal2  the second calendar, not altered, not null
	 * @return true if they represent the same day
	 * @throws IllegalArgumentException if either calendar is <code>null</code>
	 */
	public static boolean isSameDay(Calendar cal1, Calendar cal2) {
		if (cal1 == null || cal2 == null) {
			throw new IllegalArgumentException("The dates must not be null");
		}
		return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
				cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
				cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
	}

	/**
	 * <p>Checks if two calendars represent the same year ignoring time.</p>
	 * @param cal1  the first calendar, not altered, not null
	 * @param cal2  the second calendar, not altered, not null
	 * @return true if they represent the same day
	 * @throws IllegalArgumentException if either calendar is <code>null</code>
	 */
	public static boolean isSameYear(Calendar cal1, Calendar cal2) {
		if (cal1 == null || cal2 == null) {
			throw new IllegalArgumentException("The dates must not be null");
		}
		return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
				cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR));
	}
}
