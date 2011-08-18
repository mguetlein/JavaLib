package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Date;

import util.ArrayUtil;
import util.FileUtil;
import util.StringUtil;

public class ExternalTool
{

	public static void run(final String processName, File stdOutfile, final String errorOutMatch, String command)
	{
		run(processName, stdOutfile, errorOutMatch, command, null);
	}

	public static Process run(final String processName, String command, boolean wait)
	{
		return runWithArrayOrString(processName, null, null, command, null, null, wait);
	}

	public static Process run(final String processName, String command, File stdOutFile, boolean wait)
	{
		return runWithArrayOrString(processName, stdOutFile, null, command, null, null, wait);
	}

	public static void run(final String processName, File stdOutfile, final String errorOutMatch, String[] cmdArray)
	{
		run(processName, stdOutfile, errorOutMatch, cmdArray, null);
	}

	public static void run(final String processName, File stdOutfile, final String errorOutMatch, String command,
			String env[])
	{
		run(processName, stdOutfile, errorOutMatch, command, env, null);
	}

	public static void run(final String processName, File stdOutfile, final String errorOutMatch, String[] cmdArray,
			String env[])
	{
		run(processName, stdOutfile, errorOutMatch, cmdArray, env, null);
	}

	public static void run(final String processName, File stdOutfile, final String errorOutMatch, String command,
			String env[], File dir)
	{
		runWithArrayOrString(processName, stdOutfile, errorOutMatch, command, env, dir, true);
	}

	public static void run(final String processName, File stdOutfile, final String errorOutMatch, String[] cmdArray,
			String env[], File dir)
	{
		runWithArrayOrString(processName, stdOutfile, errorOutMatch, cmdArray, env, dir, true);
	}

	private static Process runWithArrayOrString(final String processName, File stdOutfile, final String errorOutMatch,
			Object arrayOrString, String env[], File dir, boolean wait)
	{
		if (stdOutfile != null && wait == false)
			throw new IllegalStateException("illegal param combination");

		try
		{
			final File tmpStdOutfile = stdOutfile != null ? new File(stdOutfile + ".tmp") : null;
			final long starttime = new Date().getTime();
			final Process child;

			if (arrayOrString instanceof String)
			{
				String command = (String) arrayOrString;
				System.out.println(processName + " > " + command);
				if (env == null && dir == null)
					child = Runtime.getRuntime().exec(command);
				else if (env != null && dir == null)
					child = Runtime.getRuntime().exec(command, env);
				else
					child = Runtime.getRuntime().exec(command, env, dir);
			}
			else if (arrayOrString instanceof String[])
			{
				String[] cmdArray = (String[]) arrayOrString;
				System.out.println(processName + " > " + ArrayUtil.toString(cmdArray, " "));
				if (env == null && dir == null)
					child = Runtime.getRuntime().exec(cmdArray);
				else if (env != null && dir == null)
					child = Runtime.getRuntime().exec(cmdArray, env);
				else
					child = Runtime.getRuntime().exec(cmdArray, env, dir);
			}
			else
				throw new IllegalArgumentException();

			Thread th = null;
			th = new Thread(new Runnable()
			{
				public void run()
				{
					try
					{
						BufferedReader buffy = new BufferedReader(new InputStreamReader(child.getInputStream()));
						PrintStream print = null;
						if (tmpStdOutfile != null)
							print = new PrintStream(tmpStdOutfile);
						else
							print = System.out;
						while (true)
						{
							String s = buffy.readLine();
							if (s != null)
								print.println(s);
							else
								break;
						}
						buffy.close();
						if (tmpStdOutfile != null)
							print.close();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			});
			Thread thError = new Thread(new Runnable()
			{
				public void run()
				{
					try
					{
						BufferedReader buffy = new BufferedReader(new InputStreamReader(child.getErrorStream()));
						// Status.INFO.println();
						while (true)
						{
							String s = buffy.readLine();
							if (s != null)
							{
								if (errorOutMatch == null || s.matches(errorOutMatch))
									System.out.println(processName + " "
											+ StringUtil.formatTime(new Date().getTime() - starttime) + " > " + s);
							}
							else
								break;
						}
						buffy.close();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			});

			if (th != null)
				th.start();
			thError.start();

			if (wait)
			{
				child.waitFor();
				while (thError.isAlive())
				{
					try
					{
						Thread.sleep(50);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				if (child.exitValue() != 0)
					throw new Error(processName + " exited with error: " + child.exitValue());
				if (tmpStdOutfile != null && !FileUtil.robustRenameTo(tmpStdOutfile, stdOutfile))
					throw new Error("cannot rename tmp file");
			}
			return child;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}