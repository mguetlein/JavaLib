package util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ParallelHandler
{
	List<Runnable> list = new ArrayList<Runnable>();

	private List<Runnable> todo = new ArrayList<Runnable>();
	private List<Runnable> running = new ArrayList<Runnable>();
	private List<Runnable> done = new ArrayList<Runnable>();
	private static boolean DEBUG = true;

	private Long startTime;

	public ParallelHandler()
	{
		this(Runtime.getRuntime().availableProcessors());
	}

	private synchronized String info()
	{
		if (list.size() != todo.size() + running.size() + done.size())
			throw new IllegalStateException(list.size() + " != " + done.size() + " + " + running.size() + " + "
					+ todo.size());
		return "" + StringUtil.concatWhitespace(todo.size() + "", 2) + "/"
				+ StringUtil.concatWhitespace(running.size() + "", 2) + "/"
				+ StringUtil.concatWhitespace(done.size() + "", 2);
	}

	private void print(String s)
	{
		if (DEBUG)
		{
			System.out.println("Parallel " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " "
					+ info() + " > " + s);
		}
	}

	public ParallelHandler(int numParallel)
	{
		print("num parallel processes: " + numParallel);
		Runnable r = new Runnable()
		{
			public void run()
			{
				try
				{
					while (true)
					{
						Thread.sleep(100);
						Runnable r = null;
						int i = -1;
						synchronized (ParallelHandler.this)
						{
							if (todo.size() > 0)
							{
								r = todo.remove(0);
								i = list.indexOf(r);
								running.add(r);
							}
						}
						if (r != null)
						{
							if (startTime == null)
								startTime = System.currentTimeMillis();
							print("starting " + i);
							r.run();
							synchronized (ParallelHandler.this)
							{
								running.remove(r);
								done.add(r);
								if (todo.size() == 0)
									print("runtime "
											+ StringUtil.formatDouble(
													(System.currentTimeMillis() - startTime) / 1000.0, 2) + " seconds");
							}
							print("done " + i);
						}
					}
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		};
		for (int i = 0; i < numParallel; i++)
			new Thread(r).start();
	}

	public synchronized int numJobs()
	{
		return list.size();
	}

	public void waitFor(int i) throws InterruptedException
	{
		print("start waiting for " + i);
		Runnable r = list.get(i);
		while (true)
		{
			synchronized (this)
			{
				if (done.contains(r))
					break;
			}
			Thread.sleep(100);
		}
		print("done waiting for " + i);
	}

	public void waitForAll() throws InterruptedException
	{
		while (true)
		{
			synchronized (this)
			{
				if (done.size() == list.size())
					break;
			}
			Thread.sleep(100);
		}
	}

	public synchronized void addJob(Runnable r)
	{
		list.add(r);
		todo.add(r);
	}

	public static void main(String args[]) throws InterruptedException
	{
		final Random r = new Random();
		ParallelHandler h = new ParallelHandler(2);

		int count = 0;

		for (int i = 0; i < 8; i++)
		{
			final int fCount = count++;
			Runnable run = new Runnable()
			{
				@Override
				public void run()
				{
					System.out.println(fCount + " start");
					for (int j = 0; j < 5000000; j++)
					{
						Math.sqrt(r.nextDouble());
					}
					System.out.println(fCount + " done");
				}
			};
			h.addJob(run);
		}
		//		for (int i = 0; i < h.numJobs(); i++)
		//		{
		//			System.out.println("start waiting for " + i);
		//			h.waitFor(i);
		//			System.out.println("stop waiting for " + i);
		//		}

		h.waitForAll();

		System.exit(0);
	}
}
