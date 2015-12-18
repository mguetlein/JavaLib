package org.mg.javalib.util;

public class RunnableUtil
{
	public static Runnable concat(final Runnable... r)
	{
		return new Runnable()
		{
			@Override
			public void run()
			{
				for (Runnable runnable : r)
					runnable.run();
			}
		};
	}
}
