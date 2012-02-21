package util;

import java.util.Random;

public class ThreadUtil
{
	public static void sleep(long milliSeconds)
	{
		try
		{
			Thread.sleep(milliSeconds);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public static boolean runAndWait(Runnable r, long maxRuntime)
	{
		try
		{
			Thread th = new Thread(r);
			th.start();
			th.join(maxRuntime);
			return !th.isAlive();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public static void main(String args[])
	{
		Runnable runner = new Runnable()
		{
			@Override
			public void run()
			{
				for (int i = 0; i < 2000000; i++)
					Math.sqrt(new Random().nextDouble());
			}
		};
		long t = System.currentTimeMillis();
		System.out.println(ThreadUtil.runAndWait(runner, 3000));
		System.out.println("Waited: " + (System.currentTimeMillis() - t));
	}
}
