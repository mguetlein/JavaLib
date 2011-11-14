package util;

public class IntegerUtil
{
	/*
	 * return null if no integer
	 */
	public static Integer parseInteger(String d)
	{
		try
		{
			return new Integer(d);
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}
}
