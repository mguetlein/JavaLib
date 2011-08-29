package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;

import util.ArrayUtil;
import util.FileUtil;

public class ExternalTool
{

	public void run(final String processName, String command)
	{
		runWithArrayOrString(processName, command, null, null, null, true);
	}

	public Process run(final String processName, String command, File stdOutfile, boolean wait)
	{
		return runWithArrayOrString(processName, command, stdOutfile, null, null, wait);
	}

	protected void stdout(String s)
	{
		System.out.println(s);
	}

	protected void stderr(String s)
	{
		System.err.println(s);
	}

	protected Process runWithArrayOrString(final String processName, Object arrayOrString, File stdOutfile,
			String env[], File workingDirectory, boolean wait)
	{
		if (stdOutfile != null && wait == false)
			throw new IllegalStateException("illegal param combination");

		try
		{
			final File tmpStdOutfile = stdOutfile != null ? new File(stdOutfile + ".tmp") : null;
			//			final long starttime = new Date().getTime();
			final Process child;

			if (arrayOrString instanceof String)
			{
				String command = (String) arrayOrString;
				System.out.println(processName + " > " + command);
				if (env == null && workingDirectory == null)
					child = Runtime.getRuntime().exec(command);
				else if (env != null && workingDirectory == null)
					child = Runtime.getRuntime().exec(command, env);
				else
					child = Runtime.getRuntime().exec(command, env, workingDirectory);
			}
			else if (arrayOrString instanceof String[])
			{
				String[] cmdArray = (String[]) arrayOrString;
				System.out.println(processName + " > " + ArrayUtil.toString(cmdArray, " "));
				if (env == null && workingDirectory == null)
					child = Runtime.getRuntime().exec(cmdArray);
				else if (env != null && workingDirectory == null)
					child = Runtime.getRuntime().exec(cmdArray, env);
				else
					child = Runtime.getRuntime().exec(cmdArray, env, workingDirectory);
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
						while (true)
						{
							String s = buffy.readLine();
							if (s != null)
								if (tmpStdOutfile != null)
									print.println(s);
								else
									stdout(s);
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
								stderr(s);
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