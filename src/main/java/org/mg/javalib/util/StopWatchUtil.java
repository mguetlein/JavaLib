package org.mg.javalib.util;

import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.LinkedHashMap;
import java.util.Random;

public class StopWatchUtil
{
	public static long getCpuTime()
	{
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		return bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadCpuTime() / 1000000L : 0L;
	}

	private static class Run
	{
		String name;

		int count;
		long currentStart = -1;
		long totalRuntime;

		public Run(String name)
		{
			this.name = name;
		}

		public void start(boolean strict)
		{
			if (currentStart != -1)
			{
				if (strict)
					System.err.println("SW > WARNING: " + name + " already started, resetting start point");
				else
					return;
			}
			currentStart = getTime();
		}

		public void stop(boolean strict)
		{
			if (currentStart == -1)
			{
				if (strict)
					throw new IllegalStateException(name + " not started");
				else
					return;
			}
			long runtime = getTime() - currentStart;
			totalRuntime += runtime;
			StopWatchUtil.totalRuntime += runtime;
			count++;
			currentStart = -1;
		}

		public String toString()
		{
			if (count == 0 || currentStart != -1)
				return "SW > " + name + " not initialized or still running";

			if (count == 1)
				return "SW > " + name + " - " + count + " run, runtime: " + formatTime(totalRuntime);
			else
			{
				long avgRuntime = (long) (totalRuntime / (double) count);
				return "SW > " + name + " - " + count + " runs, avg-runtime: " + formatTime(avgRuntime)
						+ ", total-runtime: " + formatTime(totalRuntime);
			}
		}
	}

	private static boolean useCpuTime = true;

	public static void setUseCpuTime(boolean b)
	{
		useCpuTime = b;
	}

	private static long getTime()
	{
		if (useCpuTime)
			return getCpuTime();
		else
			return System.currentTimeMillis();
	}

	private static long totalRuntime;

	private static LinkedHashMap<String, Run> runs = new LinkedHashMap<String, Run>();

	private static Run getRun(String property)
	{
		Run run = runs.get(property);
		if (run == null)
		{
			run = new Run(property);
			runs.put(property, run);
		}
		return run;
	}

	public static void start(String property)
	{
		start(property, true);
	}

	public static void start(String property, boolean strict)
	{
		getRun(property).start(strict);
	}

	public static void stop(String property)
	{
		stop(property, true);
	}

	public static void stop(String property, boolean strict)
	{
		getRun(property).stop(strict);
	}

	public static void print(String property)
	{
		print(property, System.out);
	}

	public static void print(String property, PrintStream out)
	{
		out.println(getRun(property));
	}

	public static void print()
	{
		print(System.out);
	}

	public static void print(PrintStream out)
	{
		for (String prop : runs.keySet())
			print(prop, out);
		if (runs.size() > 1)
			out.println("SW > total-stopped-runtime: " + formatTime(totalRuntime));
	}

	private static String formatTime(long time)
	{
		//		return StringUtil.formatTime(totalRuntime);
		return StringUtil.formatDouble(time / 1000.0, 2);
	}

	public static String toString(String property)
	{
		return getRun(property).toString();
	}

	public static void main(String args[])
	{
		Random r = new Random();

		for (int i = 0; i < 5; i++)
		{
			int n = 90000000;
			StopWatchUtil.start("sqrt");

			for (int j = 0; j < n; j++)
				Math.sqrt(r.nextDouble());

			StopWatchUtil.stop("sqrt");
			StopWatchUtil.print();
		}

	}

}
