package io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

public class VerboseBufferedWrapper extends BufferedWriter implements Runnable
{

	boolean verbose;
	String vString;
	long lastWrite;
	Object sync = new Object();

	final long regen = 5000;
	Thread th;

	private VerboseBufferedWrapper(Writer out)
	{
		super(out);
		Thread th = new Thread(this);
		th.start();
	}

	public void run()
	{
		try
		{
			while (true)
			{
				Thread.sleep(50);
				synchronized (sync)
				{
					if (verbose && System.currentTimeMillis() - lastWrite > regen && vString != null)
					{
						verbose = false;
						write(vString);
						verbose = true;
					}
				}
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

	public void write(char c[]) throws IOException
	{
		write(new String(c));
	}

	public void write(String s) throws IOException
	{
		synchronized (sync)
		{
			if (verbose)
				vString = s;
			else
			{
				super.write(s);
				lastWrite = System.currentTimeMillis();
				vString = null;
			}
		}
	}

	public void newLine() throws IOException
	{
		synchronized (sync)
		{
			if (verbose)
				vString = "";
			else
			{
				super.newLine();
				lastWrite = System.currentTimeMillis();
				vString = null;
			}
		}
	}

}
