package io;

import java.io.PrintStream;

public class SmartIOInfo implements Runnable
{
	private long sleep = 10000;
	private String info = null;
	private PrintStream out;

	public SmartIOInfo(PrintStream out)
	{
		this.out = out;
		Thread th = new Thread(this);
		th.start();

	}

	public void latestInfo(String info)
	{
		synchronized (this)
		{
			this.info = info;
		}
	}

	@Override
	public void run()
	{
		String oldInfo = info;

		while (true)
		{
			synchronized (this)
			{
				if (info != null && !info.equals(oldInfo))
					out.println(info);
				oldInfo = info;
			}

			try
			{
				Thread.sleep(sleep);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void main(String args[])
	{
		SmartIOInfo info = new SmartIOInfo(System.out);
		long count = 0;

		while (true)
		{
			count++;
			info.latestInfo(count + "");
		}
	}

}
