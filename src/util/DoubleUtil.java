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

	public static double compute_variance(double old_variance, int n, double new_mean, double old_mean, double new_value)
	{
		// use revursiv formular for computing the variance
		// ( see Tysiak, Folgen: explizit und rekursiv, ISSN: 0025-5866
		//  http://www.frl.de/tysiakpapers/07_TY_Papers.pdf )
		return (n > 1 ? old_variance * (n - 2) / (n - 1) : 0) //
				+ Math.pow(new_mean - old_mean, 2) //
				+ (n > 1 ? Math.pow(new_value - new_mean, 2) / (n - 1) : 0);
	}
}
