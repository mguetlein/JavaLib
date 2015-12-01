package org.mg.javalib.util;

public class TimeFormatUtil
{
	public static final long SECOND = 1000;
	public static final long MINUTE = SECOND * 60;
	public static final long HOUR = MINUTE * 60;
	public static final long DAY = HOUR * 24;
	public static final long WEEK = DAY * 7;
	public static final long MONTH = DAY * 31;
	public static final long YEAR = DAY * 365;

	public static String formatHumanReadable(long frequency)
	{
		if (frequency >= YEAR)
			return freqString(frequency, YEAR, MONTH, "Jahr/e", "Monat/e");
		else if (frequency >= MONTH)
			return freqString(frequency, MONTH, DAY, "Monat/e", "Tag/e");
		else if (frequency >= WEEK)
			return freqString(frequency, WEEK, DAY, "Woche/n", "Tag/e");
		else if (frequency >= DAY)
			return freqString(frequency, DAY, HOUR, "Tag/e", "Stunde/n");
		else if (frequency >= HOUR)
			return freqString(frequency, HOUR, MINUTE, "Stunde/n", "Minute/n");
		else
			return freqString(frequency, MINUTE, SECOND, "Minute/n", "Sekunde/n");
	}

	private static String freqString(long t, long t1, long t2, String s1, String s2)
	{
		long o1 = t / t1;
		long o2 = (t % t1) / t2;

		String s = o1 + "" + (o1 == 1 ? s1.substring(0, s1.lastIndexOf("/")) : s1.replace("/", ""));
		if (o2 > 0)
			s += ", " + o2 + "" + (o2 == 1 ? s2.substring(0, s2.lastIndexOf("/")) : s2.replace("/", ""));
		else
			s = "genau " + s;
		return s;
	}

	public static String format(long d)
	{
		if (d < 0)
			return "n/a";
		if (d >= YEAR)
			return formatTime(d, YEAR, MONTH, "years", "months");
		else if (d >= MONTH)
			return formatTime(d, MONTH, DAY, "months", "days");
		else if (d >= WEEK)
			return formatTime(d, WEEK, DAY, "weeks", "d");
		else if (d >= DAY)
			return formatTime(d, DAY, HOUR, "d", "h");
		else if (d >= HOUR)
			return formatTime(d, HOUR, MINUTE, "h", "m");
		else if (d >= MINUTE)
			return formatTime(d, MINUTE, SECOND, "m", "s");
		else if (d >= SECOND)
			return (d / SECOND) + "s";
		else
			return "<1s";
	}

	private static String formatTime(long t, long t1, long t2, String s1, String s2)
	{
		long o1 = t / t1;
		long o2 = (t % t1) / t2;
		String s = o1 + "" + s1;
		if (o2 > 0)
			s += ", " + o2 + "" + s2;
		return s;
	}

	public static void main(String args[])
	{
		long[] l = { 12L, 3456L, 10959L, 60000L, 61000L, 4398743L, };
		for (long ll : l)
			System.out.println(TimeFormatUtil.format(ll));
	}
}
