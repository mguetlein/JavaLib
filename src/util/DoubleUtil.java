package util;

public class DoubleUtil
{
	/*
	 * return null if no double
	 */
	public static Double parseDouble(String d)
	{
		try
		{
			return new Double(d);
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}

	public static int compare(Double d1, Double d2)
	{
		if (d1 == null)
			if (d2 == null)
				return 0;
			else
				return -1;
		else if (d2 == null)
			return 1;
		else
			return d1.compareTo(d2);
	}
}
