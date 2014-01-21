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
	double var;
	int numZero;
	int numNull;
	int numDistinct;

	private DoubleArraySummary(int num, double min, double max, double sum, double mean, double median, double var,
			int numZero, int numNull, int numDistinct)
	{
		this.num = num;
		this.min = min;
		this.max = max;
		this.sum = sum;
		this.mean = mean;
		this.median = median;
		this.var = var;
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
		return format(false, 2);
	}

	public String toString(boolean html)
	{
		return format(html, 2);
	}

	public String toString(boolean html, int numDecimalPlaces)
	{
		return format(html, numDecimalPlaces);
	}

	public String format()
	{
		return format(false, 2);
	}

	public String format(boolean html, int numDecimalPlaces)
	{
		if (num == 0)
			return "";
		else if (numNull == num)
			return "null";
		else
		{
			//			String medStr = html ? "&Oslash;" : "Median: ";
			//			return medStr + StringUtil.formatDouble(median) + " [" + StringUtil.formatDouble(min) + "; "
			//					+ StringUtil.formatDouble(max) + "]";

			//String s = StringUtil.formatDouble(median, numDecimalPlaces);
			String s = StringUtil.formatDouble(mean, numDecimalPlaces);
			if (getNum() - getNullCount() > 1)
			{
				String plusMinus = html ? "&thinsp;&#177;" : "\u2009\u00B1";
				s += "" + plusMinus + "" + StringUtil.formatDouble(getStdev(), numDecimalPlaces);
			}
			return s;
		}
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

	public double getVariance()
	{
		return var;
	}

	public double getStdev()
	{
		return Math.sqrt(var);
	}

	public int getNumZero()
	{
		return numZero;
	}

	public double getRange()
	{
		return max - min;
	}

	public int getNullCount()
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
		double mean = 0;
		double var = 0;
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
				l.add(d);
				uniq.add(d);

				sum += d;
				double old_mean = mean;
				mean = sum / (double) ((i + 1) - numNull);
				var = DoubleUtil.compute_variance(var, i + 1, mean, old_mean, d);
			}
			i++;
		}

		double median = 0;
		if (l.size() > 0)
		{
			Collections.sort(l);
			if (l.size() % 2 == 1)
				median = l.get((l.size() - 1) / 2);
			else
				median = (l.get(l.size() / 2) + l.get((l.size() - 2) / 2)) / 2.0;
		}

		if (numNull == i)
		{
			min = Double.NaN;
			max = Double.NaN;
			mean = Double.NaN;
			median = Double.NaN;
			var = Double.NaN;
		}
		return new DoubleArraySummary(i, min, max, sum, mean, median, var, numZero, numNull, uniq.size());
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

	public static void main(String[] args)
	{
		System.out.println(DoubleArraySummary.create(new double[] { 1, 2, 3, 4, 5, 13 }));
	}

}
