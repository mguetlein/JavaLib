package org.mg.javalib.datamining;

public class StatisticsUtil
{
	public static double getChangeDifferenceNemenyi(double confidence, int numClassifiers,
			int numDatasets)
	{
		return /* Math.pow(confidence, 2) */Math
				.sqrt((numClassifiers * (numClassifiers + 1)) / (double) (6 * numDatasets));
	}

	public static void main(String args[])
	{

		for (int i = 1; i <= 40; i++)
			System.out.println(i + ": " + getChangeDifferenceNemenyi(0.90, 2, i));
	}
}
