package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.inference.TestUtils;

public class Binning
{
	double min;
	double max;
	double width;
	int bins;
	long counts[];

	public Binning(double[] values, int bins, boolean acceptNullBins)
	{
		double[] vals = Arrays.copyOf(values, values.length);
		Arrays.sort(vals);
		min = vals[0];
		max = vals[vals.length - 1];
		doBin(vals, bins, acceptNullBins);
	}

	private void doBin(double[] sortedValues, int bins, boolean acceptNullBins)
	{
		this.bins = bins;
		width = (max - min) / (double) bins;
		counts = new long[bins];
		double maxT = min + width;
		int valueIndex = 0;
		for (int i = 0; i < bins; i++)
		{
			while (valueIndex < sortedValues.length && sortedValues[valueIndex] <= maxT)
			{
				counts[i]++;
				valueIndex++;
			}
			if (!acceptNullBins && counts[i] == 0)
			{
				doBin(sortedValues, bins - 1, acceptNullBins);
				return;
			}
			maxT += width;
		}
	}

	public int getBin(double value)
	{
		if (value < min || value > max)
			return -1;
		if (value == max)
			return bins - 1;
		if (value == min)
			return 0;
		int bin = (int) ((value - min) / width);
		if (bin >= bins || bin < 0)
			throw new IllegalStateException("bin:" + bin + " for value " + value + "\n" + this);
		return bin;
	}

	public long[] getAllCounts()
	{
		return counts;
	}

	public long[] getSelectedCounts(double value)
	{
		int bin = getBin(value);
		if (bin == -1)
			return null;
		long[] l = new long[bins];
		l[bin]++;
		return l;
	}

	public String toString()
	{
		String s = "bins:   " + bins + "\n";
		s += "counts: " + ArrayUtil.toString(counts) + "\n";
		s += "min: " + min + "\n";
		s += "max: " + max + "\n";
		s += "width: " + width;
		return s;
	}

	public static void main(String[] args)
	{
		//double d[] = new double[] { -1, 0, 0, 0, 1, 1, 1, 2, 2, 3, 4, 5, 6, 6, 6, 7, 7, 7, 8, 8, 8, 9, 10 };
		double[] d = new NormalDistribution(0, 5).sample(30);
		d[d.length - 1] = 20;
		for (boolean b : new boolean[] { true, false })
		{
			Binning bin = new Binning(d, 20, b);
			System.out.println(bin);
			long l1[] = bin.getAllCounts();
			long l2[] = bin.getSelectedCounts(20);
			List<Long> l1n = new ArrayList<Long>();
			List<Long> l2n = new ArrayList<Long>();
			for (int i = 0; i < l1.length; i++)
			{
				if (l1[i] > 0 || l2[i] > 0)
				{
					l1n.add(l1[i]);
					l2n.add(l2[i]);
				}
			}
			System.out.println(ListUtil.toString(l1n));
			System.out.println(ListUtil.toString(l2n));
			double p = TestUtils.chiSquareTestDataSetsComparison(ArrayUtil.toPrimitiveLongArray(ListUtil.toArray(l1n)),
					ArrayUtil.toPrimitiveLongArray(ListUtil.toArray(l2n)));
			System.out.println(p);
		}
	}
}
