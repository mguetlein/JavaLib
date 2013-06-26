package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import datamining.ResultSet;

public class DoubleArraySummary implements ArraySummary
{
	int num;
	double min;
	double max;
	double sum;
	double mean;
	double median;
	int numZero;
	int numNull;
	int numDistinct;

	private DoubleArraySummary(int num, double min, double max, double sum, double mean, double median, int numZero,
			int numNull, int numDistinct)
	{
		this.num = num;
		this.min = min;
		this.max = max;
		this.sum = sum;
		this.mean = mean;
		this.median = median;
		this.numZero = numZero;
		this.numNull = numNull;
		this.numDistinct = numDistinct;
	}

	public String toString()
	{
		// return "num: " + num + ", min: " + min + ", max: " + max + ", sum: " + sum + ", mean: " + mean + ", numZero: "
		// + numZero + ", numNull: " + numNull;

		//		return "[" + StringUtil.formatDouble(min) + "; " + StringUtil.formatDouble(max) + "] Median:"
		//				+ StringUtil.formatDouble(median);
		return format();
	}

	public String format()
	{
		if (numNull == num)
			return "null";
		else
			return "[" + StringUtil.formatDouble(min) + "; " + StringUtil.formatDouble(max) + "] Median:"
					+ StringUtil.formatDouble(median);
	}

	public int getNum()
	{
		return num;
	}

	public int getNumDistinct()
	{
		return numDistinct;
	}

	public double getMin()
	{
		return min;
	}

	public double getMax()
	{
		return max;
	}

	public double getSum()
	{
		return sum;
	}

	public double getMean()
	{
		return mean;
	}

	public double getMedian()
	{
		return median;
	}

	public int getNumZero()
	{
		return numZero;
	}

	public double getRange()
	{
		return max - min;
	}

	public int getNumNull()
	{
		return numNull;
	}

	public static <T> DoubleArraySummary create(Collection<List<T>> collectionOfLists)
	{
		return create(collectionOfLists, 0);
	}

	public static <T> DoubleArraySummary create(Collection<List<T>> collectionOfLists, int additionalZeros)
	{
		List<Integer> counts = new ArrayList<Integer>();
		for (List<T> list : collectionOfLists)
		{
			int n = 0;
			if (list != null)
				n = list.size();
			counts.add(n);
		}
		return create(counts, additionalZeros);
	}

	public static <T> DoubleArraySummary create(Iterable<T> numbers)
	{
		return create(numbers, 0);
	}

	public static <T> DoubleArraySummary create(Iterable<T> numbers, int additionalZeros)
	{
		return create(numbers.iterator(), additionalZeros);
	}

	public static <T> DoubleArraySummary create(Iterator<T> numbers)
	{
		return create(numbers, 0);
	}

	public static DoubleArraySummary create(double[] numbers)
	{
		return create(ArrayUtil.toDoubleArray(numbers));
	}

	public static DoubleArraySummary create(Double[] numbers)
	{
		return create(Arrays.asList(numbers));
	}

	public static <T> DoubleArraySummary create(Iterator<T> numbers, int additionalZeros)
	{
		assert (additionalZeros >= 0);

		double min = additionalZeros > 0 ? 0 : Double.MAX_VALUE;
		double max = additionalZeros > 0 ? 0 : -Double.MAX_VALUE;
		double avg = 0;
		int numZero = additionalZeros;
		int numNull = 0;
		double sum = 0;

		int i = additionalZeros;
		List<Double> l = new ArrayList<Double>();
		HashSet<Double> uniq = new HashSet<Double>();

		while (numbers.hasNext())
		{
			Number n = (Number) numbers.next();

			if (n == null)
			{
				numNull++;
			}
			else
			{
				double d = n.doubleValue();
				if (d == 0)
					numZero++;
				if (d < min)
					min = d;
				if (d > max)
					max = d;
				sum += d;
				l.add(d);
				uniq.add(d);
			}
			i++;
		}
		avg = sum / (double) (i - numNull);

		double median = 0;
		if (l.size() > 0)
		{
			Collections.sort(l);
			if (l.size() % 2 == 1)
				median = l.get((l.size() - 1) / 2);
			else
				median = (l.get(l.size() / 2) + l.get((l.size() - 2) / 2)) / 2.0;
		}
		return new DoubleArraySummary(i, min, max, sum, avg, median, numZero, numNull, uniq.size());
	}

	public void addToResult(ResultSet set, String description)
	{
		int index = set.addResult();
		set.setResultValue(index, "-", description);
		set.setResultValue(index, "num", num);
		set.setResultValue(index, "min", min);
		set.setResultValue(index, "max", max);
		set.setResultValue(index, "avg", mean);
		set.setResultValue(index, "num-zero", numZero);
	}
}
