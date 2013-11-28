package util;

import java.util.ArrayList;
import java.util.List;

public class SequentialWorkerThread
{
	List<String> names = new ArrayList<String>();
	List<Runnable> jobs = new ArrayList<Runnable>();

	static boolean DEBUG = false;

	private Thread th;

	public SequentialWorkerThread()
	{
		th = new Thread(new Runnable()
		{
			public void run()
			{
				while (true)
				{
					Runnable job = null;
					String name = null;
					synchronized (jobs)
					{
						if (jobs.size() > 0)
						{
							job = jobs.remove(0);
							name = names.remove(0);
						}
					}
					if (job == null)
						try
						{
							Thread.sleep(50);
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					else
					{
						if (DEBUG)
							System.err.println("W> running '" + name + "'");
						job.run();
						if (DEBUG)
							System.err.println("W> done '" + name + "'");
					}
				}
			}
		}, "Sequential Worker Thread");
		th.start();
	}

	public boolean runningInThread()
	{
		return th == Thread.currentThread();
	}

	public void addJob(Runnable r, String name)
	{
		synchronized (jobs)
		{
			if (DEBUG)
			{
				if (jobs.size() > 0)
					System.err.println("W> adding '" + name + "' after '" + names.get(names.size() - 1) + "'");
				else
					System.err.println("W> adding '" + name + "' to empty job queue");
			}
			jobs.add(r);
			names.add(name);
		}
	}

	public void important(String string)
	{
		synchronized (jobs)
		{
			int idx = names.indexOf(string);
			System.err.println("move job " + string + " from pos " + idx + " to next job in queue");
			if (idx != -1)
			{
				jobs.add(0, jobs.remove(idx));
				names.add(0, names.remove(idx));
			}
		}
	}

	public void removeNotStartedJobs()
	{
		synchronized (jobs)
		{
			jobs.clear();
			names.clear();
		}
	}

	public void waitUntilDone()
	{
		final StringBuffer b = new StringBuffer();
		addJob(new Runnable()
		{

			@Override
			public void run()
			{
				b.append("done");
			}
		}, "Wait until done");
		while (true)
		{
			if (b.toString().equals("done"))
				return;
			try
			{
				Thread.sleep(50);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void main(String args[])
	{
		SequentialWorkerThread swt = new SequentialWorkerThread();
		swt.addJob(new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					Thread.sleep(5000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}, "Wait for 5 seconds");
		swt.addJob(new Runnable()
		{

			@Override
			public void run()
			{
				System.err.println("Hallo Leucin!");
			}
		}, "Print hallo leucin");
		swt.waitUntilDone();
		System.err.println("done!");
		System.exit(0);
	}

}
