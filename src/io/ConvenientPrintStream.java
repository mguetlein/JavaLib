package io;

import java.io.PrintStream;

public class ConvenientPrintStream implements Runnable
{
	PrintStream charmingOut;
	PrintStream logOut;

	long lastPrint = -1;
	final long regen = 5000;
	String verbose = null;

	Object sync = new Object();
	Thread th;

	public ConvenientPrintStream(PrintStream charmingOut)
	{
		this(charmingOut, null);
	}

	public ConvenientPrintStream(PrintStream charmingOut, PrintStream logOut)
	{
		this.charmingOut = charmingOut;
		this.logOut = logOut;

		th = new Thread(this);
		th.start();
	}

	public void run()
	{
		while (true)
		{
			try
			{
				Thread.sleep(50);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			synchronized (sync)
			{
				if (System.currentTimeMillis() - lastPrint > regen && verbose != null)
				{
					println(verbose);
				}
			}
		}
	}

	public void verbosePrintln(Object o)
	{
		synchronized (sync)
		{
			verbose = String.valueOf(o);
		}
	}

	public void println(Object o)
	{
		synchronized (sync)
		{
			if (logOut != null)
				logOut.println(o);
			charmingOut.println(o);
			verbose = null;
			lastPrint = System.currentTimeMillis();
		}
	}

	public void verbosePrintln(String s)
	{
		synchronized (sync)
		{
			verbose = s;
		}
	}

	public void println(String s)
	{
		synchronized (sync)
		{
			if (logOut != null)
				logOut.println(s);
			charmingOut.println(s);
			verbose = null;
			lastPrint = System.currentTimeMillis();
		}
	}

	public void verbosePrintln()
	{
		synchronized (sync)
		{
			verbose = "";
		}
	}

	public void println()
	{
		synchronized (sync)
		{
			if (logOut != null)
				logOut.println();
			charmingOut.println();
			verbose = null;
			lastPrint = System.currentTimeMillis();
		}
	}

	public static void main(String args[])
	{
		ConvenientPrintStream out = new ConvenientPrintStream(System.out);
		int count = 0;
		while (true)
		{
			try
			{
				Thread.sleep(7);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}

			if (count % 500 == 0)
				out.println("test multiples ten " + count);
			else
				out.verbosePrintln("test " + count);
			count++;
		}
	}
}
