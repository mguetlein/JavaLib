package util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

public class ArrayUtil
{
	public static String toCSVString(Object a[])
	{
		return toCSVString(a, false);
	}

	public static String toCSVString(Object a[], boolean addQuotes)
	{
		String s = "";
		for (Object st : a)
		{
			if (addQuotes)
				s += "\"" + st.toString() + "\",";
			else
				s += st.toString() + ",";
		}
		return s;
	}

	public static String intToCSVString(int a[])
	{
		String s = "";
		for (int i : a)
			s += i + ",";
		return s;
	}

	public static int[] intFromCSVString(String csv)
	{
		List<Integer> res = new ArrayList<Integer>();
		if (csv != null)
		{
			StringTokenizer tok = new StringTokenizer(csv, ",");
			while (tok.hasMoreElements())
			{
				String s = (String) tok.nextElement();
				if (s.trim().length() > 0)
					res.add(Integer.parseInt(s));
			}
		}
		return ArrayUtil.toPrimitiveIntArray(res);
	}

	public static int sum(int[] array)
	{
		int sum = 0;
		for (int i : array)
			sum += i;
		return sum;
	}

	public static int indexOf(int[] array, int elem)
	{
		for (int i = 0; i < array.length; i++)
			if (array[i] == elem)
				return i;
		return -1;
	}

	public static <T> int indexOf(T[] array, T elem)
	{
		for (int i = 0; i < array.length; i++)
			if (array[i].equals(elem))
				return i;
		return -1;
	}

	public static <T> int occurences(T[] array, T elem)
	{
		int o = 0;
		for (int i = 0; i < array.length; i++)
			if (array[i].equals(elem))
				o++;
		return o;
	}

	@SuppressWarnings("unchecked")
	public static <T, T2> T[] cast(Class<T> type, T2[] array)
	{
		T a[] = (T[]) Array.newInstance(type, array.length);
		for (int i = 0; i < array.length; i++)
			a[i] = (T) array[i];
		return a;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] reverse(T[] array)
	{
		T a[] = (T[]) Array.newInstance(array[0].getClass(), array.length);
		for (int i = 0; i < a.length; i++)
			a[i] = array[a.length - (i + 1)];
		return a;
	}

	public static Double[] toDoubleArray(Vector<Double> vector)
	{
		Double[] a = new Double[vector.size()];
		return vector.toArray(a);
	}

	public static Double[] toDoubleArray(double array[])
	{
		Double[] a = new Double[array.length];
		for (int i = 0; i < a.length; i++)
			a[i] = new Double(array[i]);
		return a;
	}

	public static Integer[] toIntegerArray(int array[])
	{
		Integer[] a = new Integer[array.length];
		for (int i = 0; i < a.length; i++)
			a[i] = new Integer(array[i]);
		return a;
	}

	/**
	 * no casting, +"" !
	 */
	public static String[] toStringArray(Object array[])
	{
		String[] a = new String[array.length];
		for (int i = 0; i < a.length; i++)
			a[i] = array + "";
		return a;
	}

	public static Double[] toDoubleArray(Integer array[])
	{
		Double[] a = new Double[array.length];
		for (int i = 0; i < a.length; i++)
			a[i] = new Double(array[i]);
		return a;
	}

	public static List<Double> removeNullValues(Double array[])
	{
		List<Double> d = new ArrayList<Double>();
		for (int i = 0; i < array.length; i++)
			if (array[i] != null)
				d.add(array[i]);
		return d;
	}

	public static double[] toPrimitiveDoubleArray(Double[] doubles)
	{
		double[] d = new double[doubles.length];
		for (int j = 0; j < d.length; j++)
			d[j] = doubles[j];
		return d;
	}

	public static double[] toPrimitiveDoubleArray(Collection<Double> doubles)
	{
		double[] d = new double[doubles.size()];
		Iterator<Double> it = doubles.iterator();
		int i = 0;
		while (it.hasNext())
		{
			d[i] = it.next();
			i++;
		}
		return d;
	}

	public static int[] toPrimitiveIntArray(Collection<Integer> ints)
	{
		int[] d = new int[ints.size()];
		Iterator<Integer> it = ints.iterator();
		int i = 0;
		while (it.hasNext())
		{
			d[i] = it.next();
			i++;
		}
		return d;
	}

	public static boolean[] toPrimitiveBooleanArray(Collection<Boolean> bools)
	{
		boolean[] d = new boolean[bools.size()];
		Iterator<Boolean> it = bools.iterator();
		int i = 0;
		while (it.hasNext())
		{
			d[i] = it.next();
			i++;
		}
		return d;
	}

	//	public static List<Object> toList(Object[] o)
	//	{
	//		List<Object> d = new ArrayList<Object>();
	//		for (Object e : o)
	//			d.add(e);
	//		return d;
	//	}

	public static <T> List<T> toList(T[] o)
	{
		List<T> d = new ArrayList<T>();
		for (T e : o)
			d.add(e);
		return d;
	}

	public static List<Double> toList(double[] doubles)
	{
		List<Double> d = new ArrayList<Double>();
		for (double e : doubles)
			d.add(e);
		return d;
	}

	public static List<Integer> toList(int[] doubles)
	{
		List<Integer> d = new ArrayList<Integer>();
		for (int e : doubles)
			d.add(e);
		return d;
	}

	public static HashSet<Object> getDistinctValues(Object array[])
	{
		return new HashSet<Object>(ArrayUtil.toList(array));
	}

	public static Double[] normalize(Object array[])
	{
		if (array.length == 0)
			return new Double[0];
		if (array[0] instanceof Number)
			return normalize(ArrayUtil.cast(Double.class, array));
		else
		{
			// the old way sorted in order of number of occurences
			//List<Object> valuesInOrderOfCounts = CountedSet.fromArray(array).values();

			//remove duplicates and convert to list, sort list
			List<Object> s = new ArrayList<Object>(new HashSet<Object>(ArrayUtil.toList(array)));
			Collections.sort(s, new DefaultComparator<Object>());

			Double[] indices = new Double[array.length];
			for (int i = 0; i < indices.length; i++)
				indices[i] = (double) s.indexOf(array[i]);
			return normalize(indices);
		}
	}

	/**
	 * normalizes to 0-1<br>
	 * REPLACES NULL VALUES WITH 0.5
	 * 
	 * @param array
	 * @return
	 */
	public static Double[] normalize(Double array[])
	{
		return normalize(array, 0, 1);
	}

	public static Double[] normalizeLog(Double array[])
	{
		DoubleArraySummary summary = DoubleArraySummary.create(array);
		array = replaceNull(array, summary.mean);
		if (summary.min <= 0)
			array = translate(array, Math.abs(summary.min) + 1);
		array = log(array);
		return normalize(array, 0, 1);
	}

	public static Double[] replaceNull(Double array[], double replace)
	{
		Double a[] = new Double[array.length];
		for (int i = 0; i < a.length; i++)
			a[i] = array[i] == null ? replace : array[i];
		return a;
	}

	public static Double[] translate(Double array[], double offset)
	{
		Double a[] = new Double[array.length];
		for (int i = 0; i < a.length; i++)
			a[i] = array[i] + offset;
		return a;
	}

	public static Double[] log(Double array[])
	{
		Double a[] = new Double[array.length];
		for (int i = 0; i < a.length; i++)
			a[i] = Math.log10(array[i]);
		return a;
	}

	/**
	 * normalizes to MIN-MAX<br>
	 * REPLACES NULL VALUES WITH MEAN(MIN,MAX)
	 * 
	 * @param array
	 * @return
	 */
	public static Double[] normalize(Double array[], double min, double max)
	{
		double minVal = Double.MAX_VALUE;
		double maxVal = -Double.MAX_VALUE;
		for (int i = 0; i < array.length; i++)
		{
			if (array[i] != null && array[i] < minVal)
				minVal = array[i];
			if (array[i] != null && array[i] > maxVal)
				maxVal = array[i];
		}
		double deltaVal = maxVal - minVal;
		double delta = (max - min);
		Double a[] = new Double[array.length];
		if (minVal == maxVal)
		{
			Arrays.fill(a, min + delta / 2.0);
			return a;
		}
		else
		{
			for (int i = 0; i < a.length; i++)
			{
				if (array[i] == null || Double.isNaN(array[i]))
					a[i] = min + delta / 2.0;
				else
					a[i] = (array[i] - minVal) / deltaVal * delta + min;
			}
			return a;
		}
	}

	public static void normalize(double array[][])
	{
		normalize(array, 0, 1);
	}

	public static void normalize(double array[][], double min, double max)
	{
		double[] minMax = getMinMax(array);
		double deltaVal = minMax[1] - minMax[0];
		double delta = (max - min);
		for (int i = 0; i < array.length; i++)
			for (int j = 0; j < array[0].length; j++)
				array[i][j] = (array[i][j] - minMax[0]) / deltaVal * delta + min;
	}

	public static <T> int[] getOrdering(T[] array, Comparator<T> comp)
	{
		return getOrdering(array, comp, true);
	}

	/**
	 * not tested
	 * 
	 * @param <T>
	 * @param array
	 * @param comp
	 * @return
	 */
	public static <T> int[] getOrdering(T[] array, Comparator<T> comp, boolean ascending)
	{
		int order[] = new int[array.length];
		for (int i = 0; i < order.length; i++)
			order[i] = i;
		int invert = ascending ? 1 : -1;
		for (int i = 0; i < order.length - 1; i++)
			for (int j = i + 1; j < order.length; j++)
			{
				if (comp.compare(array[order[i]], array[order[j]]) * invert < 1)
				{
					int tmp = order[i];
					order[i] = order[j];
					order[j] = tmp;
				}
			}
		return order;
	}

	public static int[] getRanking(int[] ordering)
	{
		int ranking[] = new int[ordering.length];
		for (int i = 0; i < ranking.length; i++)
			ranking[ordering[i]] = i;
		return ranking;
	}

	public static int[] getOrdering(double[] array, boolean ascending)
	{
		int order[] = new int[array.length];
		for (int i = 0; i < order.length; i++)
			order[i] = i;
		for (int i = 0; i < order.length - 1; i++)
			for (int j = i + 1; j < order.length; j++)
			{
				if ((ascending && array[order[i]] > array[order[j]])
						|| (!ascending && array[order[i]] < array[order[j]]))
				{
					int tmp = order[i];
					order[i] = order[j];
					order[j] = tmp;
				}
			}
		return order;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] sortAccordingToOrdering(int[] order, T[] array)
	{
		T[] res = (T[]) Array.newInstance(array[0].getClass(), array.length);
		for (int j = 0; j < array.length; j++)
			res[j] = array[order[j]];
		return res;
	}

	public static double[] sortAccordingToOrdering(int[] order, double[] array)
	{
		double[] res = new double[array.length];
		for (int j = 0; j < array.length; j++)
			res[j] = array[order[j]];
		return res;
	}

	public static double getMean(double[] a)
	{
		double sum = 0;
		for (int i = 0; i < a.length; i++)
			sum += a[i];
		if (sum == 0)
			return 0;
		else
			return sum / (double) a.length;
	}

	public static double[] getMinMax(double[] a)
	{
		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;

		for (int i = 0; i < a.length; i++)
		{
			if (min > a[i])
				min = a[i];
			if (max < a[i])
				max = a[i];
		}
		return new double[] { min, max };
	}

	public static double[] getMinMax(double[][] a)
	{
		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;

		for (int i = 0; i < a.length; i++)
		{
			double mm[] = getMinMax(a[i]);
			if (min > mm[0])
				min = mm[0];
			if (max < mm[1])
				max = mm[1];
		}
		return new double[] { min, max };
	}

	public static int[] getMinMax(int[] a)
	{
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;

		for (int i = 0; i < a.length; i++)
		{
			if (min > a[i])
				min = a[i];
			if (max < a[i])
				max = a[i];
		}
		return new int[] { min, max };
	}

	public static double[] concat(double[] a, double[] b)
	{
		double[] c = new double[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}

	public static int[] concat(int[] a, int[] b)
	{
		int[] c = new int[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}

	public static <T> T[] concat(T[]... arrays)
	{
		if (arrays[0].length == 0)
			throw new IllegalArgumentException("no entry in first array");
		return concat(arrays[0][0].getClass(), arrays);
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] concat(Class<?> type, T[]... arrays)
	{

		// Determine required size of new array
		int count = 0;
		for (T[] array : arrays)
		{
			//System.out.println(toString(array));
			count += array.length;
		}

		// create new array of required class
		T[] mergedArray = (T[]) Array.newInstance(type, count);

		// Merge each array into new array
		int start = 0;
		for (T[] array : arrays)
		{
			System.arraycopy(array, 0, mergedArray, start, array.length);
			start += array.length;
		}
		return (T[]) mergedArray;
	}

	public static <T> List<T[]> split(T[] array, int n)
	{
		int length = array.length / n;
		int rest = array.length - (length * n);

		List<T[]> list = new ArrayList<T[]>();
		int array_index = 0;

		for (int i = 0; i < n; i++)
		{
			int a_length = length;
			if (i < rest)
				a_length++;

			list.add(Arrays.copyOfRange(array, array_index, array_index + a_length));
			array_index += a_length;
		}
		return list;
	}

	public static <T> void scramble(T[] array)
	{
		scramble(array, new Random());
	}

	public static <T> void scramble(T[] array, Random r)
	{
		for (int i = 0; i < array.length; i++)
		{
			int j = r.nextInt(array.length);
			T tmp = array[j];
			array[j] = array[i];
			array[i] = tmp;
		}
	}

	public static void scramble(float[] array)
	{
		scramble(array, new Random());
	}

	public static void scramble(float[] array, Random r)
	{
		for (int i = 0; i < array.length; i++)
		{
			int j = r.nextInt(array.length);
			float tmp = array[j];
			array[j] = array[i];
			array[i] = tmp;
		}
	}

	public static String toString(int array[])
	{
		return toString(array, -1);
	}

	public static String toString(int array[], int formatSpace)
	{
		String s = "[ ";
		for (int i = 0; i < array.length; i++)
			s += (formatSpace != -1 ? StringUtil.concatWhitespace(array[i] + "", formatSpace, false) : array[i]) + "; ";
		if (array.length > 0)
			s = s.substring(0, s.length() - 2);
		s += " ]";
		return s;
	}

	public static String toString(boolean array[])
	{
		return toString(array, new String[] { "true", "false" });
	}

	public static String toString(boolean array[], String trueFalse[])
	{
		String t = trueFalse[0];
		String f = trueFalse[1];
		String s = "[ ";
		for (int i = 0; i < array.length; i++)
			s += (array[i] ? t : f) + "; ";
		if (array.length > 0)
			s = s.substring(0, s.length() - 2);
		s += " ]";
		return s;
	}

	public static String toString(Object array[], String seperator)
	{
		String s = "[ ";
		for (int i = 0; i < array.length; i++)
			s += array[i] + seperator + " ";
		if (array.length > 0)
			s = s.substring(0, s.length() - (1 + seperator.length()));
		s += " ]";
		return s;
	}

	public static String toString(Object array[])
	{
		return toString(array, ";");
	}

	public static String toString(Double array[], boolean format)
	{
		return toString(ArrayUtil.toPrimitiveDoubleArray(array), format);
	}

	public static String toString(double array[])
	{
		return toString(array, false);
	}

	public static String toString(double array[], boolean format)
	{
		String s = "[ ";
		for (int i = 0; i < array.length; i++)
			s += (format ? StringUtil.formatDouble(array[i]) : array[i]) + "; ";
		if (array.length > 0)
			s = s.substring(0, s.length() - 2);
		s += " ]";
		return s;
	}

	public static String toString(double array[][])
	{
		return toString(array, false);
	}

	public static String toString(double array[][], boolean format)
	{
		String s = "[\n";
		for (int i = 0; i < array.length; i++)
			s += " " + ArrayUtil.toString(array[i], format) + "\n";
		s += "]";
		return s;
	}

	public static String toString(String array[][])
	{
		String s = "";
		int length[] = new int[array[0].length];
		for (int j = 0; j < array[0].length; j++)
		{
			int l = -1;
			for (int i = 0; i < array.length; i++)
				if (array[i][j].length() > l)
					l = array[i][j].length();
			length[j] = l;
		}
		for (int i = 0; i < array.length; i++)
		{
			for (int j = 0; j < array[0].length; j++)
				s += StringUtil.concatWhitespace(array[i][j], length[j]) + " ";
			s += "\n";
		}
		return s;
	}

	public static String toString(String desc[], Object vals[])
	{
		if (desc.length != vals.length)
			throw new IllegalArgumentException();
		String s = "";
		for (int i = 0; i < vals.length; i++)
		{
			if (i > 0)
				s += "_";
			s += desc[i] + "-" + vals[i];
		}
		return s;
	}

	/**
	 * trys to parse String array into Double array (empty strings/null are inserted as null)
	 * returns null if a non-parsable String is included
	 * 
	 * @param array
	 * @return
	 */
	public static Double[] parse(Object[] array)
	{
		Double d[] = new Double[array.length];
		for (int i = 0; i < d.length; i++)
		{
			if (array[i] == null || array[i].toString().trim().length() == 0)
				d[i] = null;
			else
			{
				Double doub = DoubleUtil.parseDouble(array[i].toString());
				if (doub == null)
					return null;
				else
					d[i] = doub;
			}
		}
		return d;
	}

	public static double euclDistance(double[] values, double[] values2)
	{
		double d = 0;
		for (int i = 0; i < values2.length; i++)
			d += Math.pow(values[i] - values2[i], 2);
		return Math.sqrt(d);
	}

	public static Object analyze(Object[] v)
	{

		try
		{
			return DoubleArraySummary.create(ArrayUtil.cast(Double.class, v)).format();
		}
		catch (ArrayStoreException e)
		{
			CountedSet<Object> set = CountedSet.fromArray(v);
			if (v.length <= 3)
				return set;
			else if (v.length < 10 && set.size() < v.length * 2 / 3)
				return set;
			else if (set.size() < v.length * 2 / 3)
				return set;
			return "...";

		}

	}

	public static <T> int getMedianIndex(T[] array, Comparator<T> cmp)
	{
		int[] order = ArrayUtil.getOrdering(array, cmp);
		return order[(order.length + 1) / 2 - 1];
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] cut(Class<T> type, T[] a1, T[] a2)
	{
		List<T> cut = new ArrayList<T>();
		for (int i = 0; i < a1.length; i++)
			if (ArrayUtil.indexOf(a2, a1[i]) != -1)
				cut.add(a1[i]);
		T[] c = (T[]) Array.newInstance(type, cut.size());
		cut.toArray(c);
		return c;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] remove(Class<T> type, T[] a1, T[] a2)
	{
		List<T> rem = new ArrayList<T>();
		for (int i = 0; i < a1.length; i++)
			if (ArrayUtil.indexOf(a2, a1[i]) == -1)
				rem.add(a1[i]);
		T[] c = (T[]) Array.newInstance(type, rem.size());
		rem.toArray(c);
		return c;
	}

	public static void main(String args[])
	{
		Object o[] = { "a", "b", "a", "c", "b", "b" };
		System.out.println(toString(normalize(o)));

		//		Object s[] = { new Double(5), new Double(3) };
		//		Double o[] = ArrayUtil.cast(Double.class, s);
		//		System.out.println(o[0].getClass());

		// double d[] = new double[] { 0.0, 100.0, 50, 22 };
		// System.out.println(Arrays.toString(ArrayUtil.normalize(d)));

		// Integer[] test = new Integer[66];
		// for (int i = 0; i < test.length; i++)
		// {
		// test[i] = i;
		// }
		//
		// scramble(test);
		//
		// List<Integer[]> l = split(test, 10);
		//
		// for (Integer[] integers : l)
		// {
		// System.out.print("[");
		// for (Integer integer : integers)
		// {
		// System.out.printf(" %2s", integer.toString());
		// }
		// System.out.println(" ]");
		// }
	}

}
