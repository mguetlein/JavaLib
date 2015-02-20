package org.mg.javalib.util;

public class MathUtil
{
	/**
	 * return array of size numBins+1, starting at 0, ending at 1.0
	 */
	public static Double[] logBinning(int numBins)
	{
		return logBinning(numBins, 2);
	}

	/**
	 * return array of size numBins+1, starting at 0, ending at 1.0
	 */
	public static Double[] logBinning(int numBins, double base)
	{
		Double[] d = new Double[numBins + 1];
		for (int i = 0; i < numBins + 1; i++)
		{
			d[i] = Math.pow(base, i);
		}
		//return d;
		return ArrayUtil.normalize(d, 0, 1, false);

	}

	public static void main(String[] args)
	{
		System.out.println(ArrayUtil.toNiceString(ArrayUtil.toPrimitiveDoubleArray(logBinning(10, 1.2)), 4));
	}
}
