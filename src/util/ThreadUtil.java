package util;

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
}
