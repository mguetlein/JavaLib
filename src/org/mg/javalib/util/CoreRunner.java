package org.mg.javalib.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CoreRunner
{
	public static void run(Runnable runnables[])
	{
		List<Runnable> l = new ArrayList<Runnable>();
		for (Runnable runnable : runnables)
			l.add(runnable);
		process(l);
	}

	public static void process(List<Runnable> runnables)
	{
		//Get number of cores
		Runtime runtime = Runtime.getRuntime();
		int numberOfCores = runtime.availableProcessors();
		System.out.println(numberOfCores);
		Thread[] threads = new Thread[numberOfCores];

		int started = 0;
		while (started < runnables.size())
		{
			for (int i = 0; i < numberOfCores; i++)
			{
				if (threads[i] == null || !threads[i].isAlive())
				{
					threads[i] = new Thread(runnables.get(started++));
					threads[i].start();
				}
				if (started >= runnables.size())
					break;
			}
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		//Joins threads
		for (int i = 0; i < numberOfCores; i++)
		{
			try
			{
				threads[i].join();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void main(String args[])
	{
		final Random r = new Random();
		class Work implements Runnable
		{
			String s;

			public Work(String s)
			{
				this.s = s;
			}

			@Override
			public void run()
			{
				try
				{
					Thread.sleep(3000 + r.nextInt(3000));
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("done " + s);
			}
		}
		Work w[] = new Work[20];
		for (int i = 0; i < w.length; i++)
			w[i] = new Work(i + "");
		CoreRunner.run(w);
		System.out.println("all done");
	}
}
