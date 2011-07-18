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
}
