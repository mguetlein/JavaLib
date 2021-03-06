package org.mg.javalib.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.commons.math3.util.FastMath;
import org.mg.javalib.util.ListUtil.Filter;

public class ArrayUtil
{
	public static String toCSVString(Object a[])
	{
		return toCSVString(a, true);
	}

	public static String toCSVString(Object a[], boolean addQuotes)
	{
		StringBuffer s = new StringBuffer("");
		if (a == null || a.length == 0)
			s.append(",");
		else
			for (Object st : a)
			{
				if (addQuotes == false && (st + "").contains(","))
					throw new IllegalArgumentException(
							"cannot convert elem with ',' to csv string: " + st);
				if (addQuotes)
				{
					s.append("\"");
					s.append(st == null ? "" : st.toString());
					s.append("\",");
				}
				else
				{
					s.append(st == null ? "" : st.toString());
					s.append(",");
				}
			}
		String ss = s.toString();
		ss = ss.substring(0, ss.length() - 1);
		return ss;
	}

	public static Double[] doubleFromCSVString(String s)
	{
		List<String> vals = StringUtil.split(s);
		Double[] d = new Double[vals.size()];
		for (int i = 0; i < d.length; i++)
			if (vals.get(i) != null)
				d[i] = Double.parseDouble(vals.get(i));
		return d;
	}

	public static String intToCSVString(int a[])
	{
		StringBuffer s = new StringBuffer("");
		for (int i : a)
		{
			s.append(i);
			s.append(",");
		}
		String ss = s.toString();
		ss = ss.substring(0, ss.length() - 1);
		return ss;
	}

	public static String booleanToCSVString(boolean a[])
	{
		StringBuffer s = new StringBuffer("");
		for (boolean i : a)
		{
			s.append(i);
			s.append(",");
		}
		String ss = s.toString();
		ss = ss.substring(0, ss.length() - 1);
		return ss;
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
		return ArrayUtils.indexOf(array, elem);
	}

	public static int indexOf(double[] array, double elem)
	{
		return ArrayUtils.indexOf(array, elem);
	}

	public static int containsAt(String query, String[] search)
	{
		for (int i = 0; i < search.length; i++)
			if (query.contains(search[i]))
				return i;
		return -1;
	}

	public static <T> int indexOf(T[] array, T elem)
	{
		return ArrayUtils.indexOf(array, elem);
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
		if (array == null)
			return null;
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

	public static Double[] toDoubleArray(double array[])
	{
		return ArrayUtils.toObject(array);
	}

	public static Integer[] toIntegerArray(int array[])
	{
		return ArrayUtils.toObject(array);
	}

	public static <T> T[] toArray(Collection<T> list)
	{
		return CollectionUtil.toArray(list);
	}

	public static <T> T[] toArray(Class<T> type, Collection<T> list)
	{
		return CollectionUtil.toArray(type, list);
	}

	public static Integer[] toIntegerArray(List<Integer> values)
	{
		Integer[] i = new Integer[values.size()];
		return values.toArray(i);
	}

	public static Double[] toDoubleArray(List<Double> values)
	{
		Double[] i = new Double[values.size()];
		return values.toArray(i);
	}

	/**
	 * no casting, +"" !
	 */
	public static String[] toStringArray(Object array[])
	{
		String[] a = new String[array.length];
		for (int i = 0; i < a.length; i++)
			if (array[i] == null)
				a[i] = null;
			else
				a[i] = array[i] + "";
		return a;
	}

	public static Double[] toDoubleArray(Integer array[])
	{
		Double[] a = new Double[array.length];
		for (int i = 0; i < a.length; i++)
			a[i] = new Double(array[i]);
		return a;
	}

	public static <T> T[] removeDuplicates(T[] array)
	{
		if (array.length < 2)
			return array;
		return removeDuplicates(array[0].getClass(), array);
	}

	public static <T> T[] removeDuplicates(Class<?> type, T[] array)
	{
		if (array.length < 2)
			return array;
		LinkedHashSet<T> set = new LinkedHashSet<T>(toList(array));
		@SuppressWarnings("unchecked")
		T[] c = (T[]) Array.newInstance(type, set.size());
		set.toArray(c);
		return c;
	}

	public static <T> List<T> removeNullValues(T array[])
	{
		List<T> d = new ArrayList<T>();
		for (int i = 0; i < array.length; i++)
			if (array[i] != null)
				d.add(array[i]);
		return d;
	}

	public static double[] toPrimitiveDoubleArray(Double[] doubles)
	{
		return ArrayUtils.toPrimitive(doubles);
	}

	public static double[] toPrimitiveDoubleArray(int[] ints)
	{
		double[] d = new double[ints.length];
		for (int j = 0; j < d.length; j++)
			d[j] = ints[j];
		return d;
	}

	public static double[] toPrimitiveDoubleArray(float[] ints)
	{
		double[] d = new double[ints.length];
		for (int j = 0; j < d.length; j++)
			d[j] = ints[j];
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

	public static int[] toPrimitiveIntArray(Integer ints[])
	{
		int[] d = new int[ints.length];
		for (int i = 0; i < d.length; i++)
			d[i] = ints[i];
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

	public static long[] toPrimitiveLongArray(Long[] array)
	{
		long[] d = new long[array.length];
		for (int i = 0; i < d.length; i++)
			d[i] = array[i];
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
	//	 }

	public static <T> List<T> toList(T[] o)
	{
		ArrayList<T> list = new ArrayList<T>(o.length);
		for (T t : o)
			list.add(t);
		return list;
	}

	public static List<Double> toList(double[] doubles)
	{
		return toList(ArrayUtils.toObject(doubles));
	}

	public static List<Integer> toList(int[] doubles)
	{
		return toList(ArrayUtils.toObject(doubles));
	}

	public static <T> HashSet<T> getDistinctValues(T array[])
	{
		return new HashSet<T>(ArrayUtil.toList(array));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static HashSet<Double> getDistinctValues(double array[])
	{
		return new HashSet((List<Double>) ArrayUtil.toList(array));
	}

	public static Double[] normalizeObjectArray(Object array[])
	{
		if (array.length == 0)
			return new Double[0];
		// the old way sorted in order of number of occurences
		//List<Object> valuesInOrderOfCounts = CountedSet.fromArray(array).values();

		//remove duplicates and convert to list, sort list
		List<Object> s = new ArrayList<Object>(new HashSet<Object>(ArrayUtil.toList(array)));
		Collections.sort(s, new DefaultComparator<Object>());
		if (s.contains(null))
			s.remove(null);

		Double[] indices = new Double[array.length];
		for (int i = 0; i < indices.length; i++)
			if (array[i] != null)
				indices[i] = (double) s.indexOf(array[i]);
			else
				indices[i] = null;

		return normalize(indices, false);
	}

	/**
	 * normalizes to 0-1<br>
	 * REPLACES NULL VALUES WITH 0.5
	 * 
	 * @param array
	 * @return
	 */
	public static Double[] normalize(Double array[], boolean replaceNullWithMedian)
	{
		return normalize(array, 0, 1, replaceNullWithMedian);
	}

	public static Double[] normalizeLog(Double array[], boolean replaceNullWithMedian)
	{
		DoubleArraySummary summary = DoubleArraySummary.create(array);
		array = replaceNull(array, summary.mean);
		if (summary.min <= 0)
			array = translate(array, Math.abs(summary.min) + 1);
		array = log(array);
		return normalize(array, 0, 1, replaceNullWithMedian);
	}

	public static Double[] replaceNaN(Double[] array, Double replace)
	{
		Double a[] = new Double[array.length];
		for (int i = 0; i < a.length; i++)
			a[i] = (array[i] != null && Double.isNaN(array[i])) ? replace : array[i];
		return a;
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
			a[i] = array[i] == null ? null : Math.log10(array[i]);
		return a;
	}

	/**
	 * normalizes to MIN-MAX<br>
	 * REPLACES NULL VALUES WITH MEDIAN(MIN,MAX)
	 * 
	 * @param array
	 * @return
	 */
	public static Double[] normalize(Double array[], double min, double max,
			boolean replaceNullWithMedian)
	{
		DoubleArraySummary sum = DoubleArraySummary.create(array);
		double deltaVal = sum.max - sum.min;
		double delta = (max - min);
		Double a[] = new Double[array.length];
		if (sum.min == sum.max)
		{
			Arrays.fill(a, min + delta / 2.0);
			return a;
		}
		else
		{
			for (int i = 0; i < a.length; i++)
			{
				Double v = array[i];
				if (replaceNullWithMedian && (v == null || Double.isNaN(v)))
					v = sum.median;

				if (v == null)
					a[i] = null;
				else
					a[i] = (v - sum.min) / deltaVal * delta + min;
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

	public static <T> int[] getOrdering(T[] array, Comparator<T> comp, boolean ascending)
	{
		int order[] = new int[array.length];
		for (int i = 0; i < order.length; i++)
			order[i] = i;
		int invert = ascending ? -1 : 1;
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

	public static int[] getRanking(double[] elems, boolean ascending)
	{
		int ordering[] = getOrdering(elems, ascending);
		int ranking[] = new int[ordering.length];
		for (int i = 0; i < ranking.length; i++)
		{
			int idx = ordering[i];
			if (i == 0)
				ranking[idx] = i;
			else
			{
				int prevIdx = ordering[i - 1];
				if (elems[idx] == elems[prevIdx])
					ranking[idx] = ranking[prevIdx];
				else
					ranking[idx] = i;
			}
		}
		return ranking;
	}

	public static int[] getRanking(int[] ordering)
	{
		int ranking[] = new int[ordering.length];
		for (int i = 0; i < ranking.length; i++)
			ranking[ordering[i]] = i;
		return ranking;
	}

	public static int[] getOrdering(double[] array, final boolean ascending)
	{
		class Elem implements Comparable<Elem>
		{
			double d;
			int i;

			public Elem(double d, int i)
			{
				this.d = d;
				this.i = i;
			}

			public int compareTo(Elem o)
			{
				if (ascending)
					return Double.compare(d, o.d);
				else
					return Double.compare(o.d, d);
			}
		}
		Elem preds[] = new Elem[array.length];
		for (int i = 0; i < preds.length; i++)
			preds[i] = new Elem(array[i], i);
		Arrays.sort(preds);
		int order[] = new int[array.length];
		for (int i = 0; i < preds.length; i++)
			order[i] = preds[i].i;
		return order;

		// old way with bubble sort
		//		int order[] = new int[array.length];
		//		for (int i = 0; i < order.length; i++)
		//			order[i] = i;
		//		for (int i = 0; i < order.length - 1; i++)
		//			for (int j = i + 1; j < order.length; j++)
		//			{
		//				if ((ascending && array[order[i]] > array[order[j]])
		//						|| (!ascending && array[order[i]] < array[order[j]]))
		//				{
		//					int tmp = order[i];
		//					order[i] = order[j];
		//					order[j] = tmp;
		//				}
		//			}
		//		return order;
	}

	public static <T> T[] sort(T[] array, Comparator<T> comp)
	{
		return sort(null, array, comp);
	}

	public static <T> T[] sort(Class<T> type, T[] array, Comparator<T> comp)
	{
		return sortAccordingToOrdering(type, getOrdering(array, comp), array);
	}

	public static <T> T[] sortAccordingToOrdering(int[] order, T[] array)
	{
		return sortAccordingToOrdering(null, order, array);
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] sortAccordingToOrdering(Class<T> type, int[] order, T[] array)
	{
		T[] res = (T[]) Array.newInstance(type == null ? array[0].getClass() : type, array.length);
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

	public static boolean[] sortAccordingToOrdering(int[] order, boolean[] array)
	{
		boolean[] res = new boolean[array.length];
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

	public static double[] push(double[] array, double elem)
	{
		double res[] = Arrays.copyOf(array, array.length + 1);
		res[res.length - 1] = elem;
		return res;
	}

	public static int[] push(int[] array, int elem)
	{
		int res[] = Arrays.copyOf(array, array.length + 1);
		res[res.length - 1] = elem;
		return res;
	}

	public static <T> T[] push(T[] array, T elem)
	{
		if (array.length == 0)
			throw new IllegalArgumentException("no entry in first array");
		return push(array[0].getClass(), array, elem);
	}

	public static <T> T[] push(Class<?> type, T[] array, T elem)
	{
		@SuppressWarnings("unchecked")
		T[] mergedArray = (T[]) Array.newInstance(type, array.length + 1);
		System.arraycopy(array, 0, mergedArray, 0, array.length);
		mergedArray[mergedArray.length - 1] = elem;
		return mergedArray;
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

	public static <T> List<T> compact(T[] array)
	{
		List<T> list = new ArrayList<T>();
		for (int i = 0; i < array.length; i++)
			if (array[i] != null)
				list.add(array[i]);
		return list;
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

	public static void scramble(char[] array)
	{
		scramble(array, new Random());
	}

	public static void scramble(char[] array, Random r)
	{
		for (int i = 0; i < array.length; i++)
		{
			int j = r.nextInt(array.length);
			char tmp = array[j];
			array[j] = array[i];
			array[i] = tmp;
		}
	}

	public static void scramble(int[] array)
	{
		scramble(array, new Random());
	}

	public static void scramble(int[] array, Random r)
	{
		for (int i = 0; i < array.length; i++)
		{
			int j = r.nextInt(array.length);
			int tmp = array[j];
			array[j] = array[i];
			array[i] = tmp;
		}
	}

	public static String toString(long array[])
	{
		return toString(toLongArray(array));
	}

	private static Long[] toLongArray(long[] array)
	{
		Long a[] = new Long[array.length];
		for (int i = 0; i < a.length; i++)
			a[i] = array[i];
		return a;
	}

	public static String toString(int array[])
	{
		return toString(array, -1);
	}

	public static String toNiceString(double[] a)
	{
		return toNiceString(a, 2);
	}

	public static String toNiceString(float[] a)
	{
		return toNiceString(toPrimitiveDoubleArray(a), 2);
	}

	public static String toNiceString(double[] a, int dec)
	{
		String s[] = new String[a.length];
		for (int i = 0; i < s.length; i++)
			s[i] = StringUtil.formatDouble(a[i], dec);
		return toString(s);
	}

	public static String toString(int array[], int formatSpace)
	{
		String s = "[ ";
		for (int i = 0; i < array.length; i++)
			s += (formatSpace != -1 ? StringUtil.concatWhitespace(array[i] + "", formatSpace, false)
					: array[i]) + "; ";
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

	public static String toString(Object array[], String seperator, String openingBracket,
			String closingBracket)
	{
		return toString(array, seperator, openingBracket, closingBracket, " ");
	}

	public static String toString(Object array[], String seperator, String openingBracket,
			String closingBracket, String space)
	{
		String s = openingBracket;
		for (int i = 0; i < array.length; i++)
			s += array[i] + seperator + space;
		if (array.length > 0)
			s = s.substring(0, s.length() - (space.length() + seperator.length()));
		s += closingBracket;
		return s;
	}

	public static String toString(int array[], String seperator, String openingBracket,
			String closingBracket, String space)
	{
		String s = openingBracket;
		for (int i = 0; i < array.length; i++)
			s += array[i] + seperator + space;
		if (array.length > 0)
			s = s.substring(0, s.length() - (space.length() + seperator.length()));
		s += closingBracket;
		return s;
	}

	public static String toString(Object array[], String seperator)
	{
		return toString(array, seperator, "[ ", " ]");
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
			if (array[i] == null || array[i].equals("null")
					|| array[i].toString().trim().length() == 0)
				d[i] = null;
			else if (array[i].equals("nan"))
				d[i] = Double.NaN;
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

	/**
	 * trys to parse String array into Integer array (empty strings/null are inserted as null)
	 * returns null if a non-parsable String is included
	 * 
	 * @param array
	 * @return
	 */
	public static Integer[] parseIntegers(Object[] array)
	{
		Integer ints[] = new Integer[array.length];
		for (int i = 0; i < ints.length; i++)
		{
			if (array[i] == null || array[i].equals("null")
					|| array[i].toString().trim().length() == 0)
				ints[i] = null;
			else
			{
				Integer parse = IntegerUtil.parseInteger(array[i].toString());
				if (parse == null)
					return null;
				else
					ints[i] = parse;
			}
		}
		return ints;
	}

	public static boolean[] parseBoolean(String[] array)
	{
		boolean bArray[] = new boolean[array.length];
		for (int i = 0; i < bArray.length; i++)
		{
			Boolean b = Boolean.parseBoolean(array[i]);
			if (b == null)
				throw new IllegalArgumentException("not a bool: " + array[i]);
			else
				bArray[i] = b;
		}
		return bArray;
	}

	static EuclideanDistance dist = null;

	public static double euclDistance(double[] values, double[] values2)
	{
		if (dist == null)
			dist = new EuclideanDistance();
		return dist.compute(values, values2);
		//		double d = 0;
		//		for (int i = 0; i < values2.length; i++)
		//			d += Math.pow(values[i] - values2[i], 2);
		//		return Math.sqrt(d);
	}

	public static double euclDistance(BitSet values1, BitSet values2)
	{
		BitSet bs = (BitSet) values1.clone();
		bs.xor(values2);
		return FastMath.sqrt(bs.cardinality());
	}

	//	public static Double simpleMatchingSimilarity(boolean[] b1, boolean[] b2)
	//	{
	//		if (b1.length == 0 || b1.length != b2.length)
	//			throw new IllegalArgumentException();
	//		int eq = 0;
	//		for (int i = 0; i < b2.length; i++)
	//			if (b1[i] == b2[i])
	//				eq++;
	//		double d = eq / (double) b1.length;
	//		//		System.out.println("tanimoto:\n" + ArrayUtil.toString(b1) + "\n" + ArrayUtil.toString(b2) + "\n" + d);
	//		return d;
	//	}

	//	public static Double tanimotoSimilarity(boolean[] b1, boolean[] b2)
	//	{
	//		if (b1.length == 0 || b1.length != b2.length)
	//			throw new IllegalArgumentException();
	//		int and = 0;
	//		int or = 0;
	//		for (int i = 0; i < b2.length; i++)
	//		{
	//			if (b1[i] && b2[i])
	//				and++;
	//			if (b1[i] || b2[i])
	//				or++;
	//		}
	//		if (or == 0)
	//			return null;
	//		double d = and / (double) or;
	//		//		System.out.println("tanimoto:\n" + ArrayUtil.toString(b1) + "\n" + ArrayUtil.toString(b2) + "\n" + d);
	//		return d;
	//	}

	//	public static Object analyze(Object[] v)
	//	{
	//		try
	//		{
	//			return DoubleArraySummary.create(ArrayUtil.cast(Double.class, v)).format();
	//		}
	//		catch (ArrayStoreException e)
	//		{
	//			CountedSet<Object> set = CountedSet.fromArray(v);
	//			if (v.length <= 3)
	//				return set;
	//			else if (v.length < 10 && set.size() < v.length * 2 / 3)
	//				return set;
	//			else if (set.size() < v.length * 2 / 3)
	//				return set;
	//			return "...";
	//		}
	//	}

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

	public static <T> T[] filter(Class<T> type, T[] a, Filter<T> filter)
	{
		return ArrayUtil.toArray(type, ListUtil.filter(ArrayUtil.toList(a), filter));
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] removeAt(Class<T> type, T[] a, int index)
	{
		if (a == null || a.length == 0)
			throw new IllegalArgumentException();
		if (a.length == 1)
			return (T[]) Array.newInstance(type, 0);
		if (index == 0)
			return Arrays.copyOfRange(a, 1, a.length);
		if (index == a.length - 1)
			return Arrays.copyOfRange(a, 0, a.length - 1);
		return ArrayUtil.concat(type, Arrays.copyOfRange(a, 0, index),
				Arrays.copyOfRange(a, index + 1, a.length));
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] remove(Class<T> type, T[] a1, T... a2)
	{
		List<T> rem = new ArrayList<T>();
		for (int i = 0; i < a1.length; i++)
			if (ArrayUtil.indexOf(a2, a1[i]) == -1)
				rem.add(a1[i]);
		T[] c = (T[]) Array.newInstance(type, rem.size());
		rem.toArray(c);
		return c;
	}

	private static class Permutation<T>
	{
		T[] input;
		int outputIndex = 0;
		List<T[]> output;

		@SuppressWarnings("unchecked")
		private Permutation(T[] input)
		{
			this.input = input;
			int faculty = faculty(input.length);
			output = new ArrayList<T[]>(faculty);
			for (int i = 0; i < faculty; i++)
				output.add((T[]) Array.newInstance(input[0].getClass(), input.length));
			permutations(0);
		}

		private void permutations(int offset)
		{
			if (input.length - offset == 1)
			{
				// Input now contains a permutation, here I store it in output,
				// but you can do anything you like with it
				System.arraycopy(input, 0, output.get(outputIndex++), 0, input.length);
				return;
			}
			T a = input[offset];
			for (int i = offset; i < input.length; i++)
			{
				// Swap elements
				T b = input[i];
				input[i] = a;
				input[offset] = b;

				permutations(offset + 1);
				// Restore element
				input[i] = b;
			}
			input[offset] = a;
		}

		private static int faculty(int n)
		{
			return n == 1 ? n : n * faculty(n - 1);
		}
	}

	public static <T> List<T[]> permute(T input[])
	{
		Permutation<T> p = new Permutation<T>(input);
		return p.output;
	}

	/**
	 * returns ordering to sort input array2
	 * 
	 * Integer[] input1 = new Integer[] { 1, 2, 3, 4, 5, 6 };
	 * Integer[] input2 = new Integer[] { 7, 8, 5, 1, 11, 1 };
	 * int[] ordering = naiveMap(input1, input2, new Comparator<Integer>()
	 * {
	 * 	public int compare(Integer o1, Integer o2)
	 * 	{
	 * 		return Math.abs(o1 - o2);
	 * 	}
	 * });
	 * System.out.println(toString(ordering) + "\n");
	 * System.out.println(toString(input1));
	 * System.out.println(toString(sortAccordingToOrdering(ordering, input2)));
	 * 
	 * @param input1
	 * @param input2
	 * @param comp
	 * @return
	 */
	public static <T> int[] naiveMap(T input1[], T input2[], Comparator<T> comp)
	{
		if (input1.length != input2.length)
			throw new IllegalArgumentException();
		int[] ordering = new int[input1.length];
		Arrays.fill(ordering, -1);
		for (int i = 0; i < input1.length; i++)
		{
			for (int j = 0; j < input2.length; j++)
			{
				if (indexOf(ordering, j) == -1 && comp.compare(input1[i], input2[j]) == 0)
				{
					ordering[i] = j;
					break;
				}
			}
		}
		for (int i = 0; i < input1.length; i++)
		{
			if (ordering[i] != -1)
				continue;
			for (int j = 0; j < input2.length; j++)
			{
				if (indexOf(ordering, j) != -1)
					continue;
				ordering[i] = j;
				break;
			}
		}
		return ordering;
	}

	public static int[] indexArray(int size)
	{
		int array[] = new int[size];
		for (int i = 0; i < array.length; i++)
			array[i] = i;
		return array;
	}

	public static <T> T uniqValue(T[] array)
	{
		if (array.length == 0)
			throw new IllegalArgumentException();
		T tmp = array[0];
		for (int i = 1; i < array.length; i++)
			if (!ObjectUtil.equals(tmp, array[i]))
				return null;
		return tmp;
	}

	public static boolean isUniq(double[] array)
	{
		if (array.length == 0)
			throw new IllegalArgumentException();
		double d = array[0];
		for (int i = 1; i < array.length; i++)
			if (d != array[i])
				return false;
		return true;
	}

	public static <T> T last(T[] array)
	{
		if (array == null || array.length == 0)
			return null;
		else
			return array[array.length - 1];
	}

	public static <T> T first(T[] array)
	{
		if (array == null || array.length == 0)
			return null;
		else
			return array[0];
	}

	public static Boolean[] toBooleanArray(boolean[] array)
	{
		Boolean b[] = new Boolean[array.length];
		for (int i = 0; i < b.length; i++)
			b[i] = array[i];
		return b;
	}

	public static Double[] computeMean(List<Double[]> results)
	{
		if (results.size() == 0)
			throw new IllegalArgumentException();
		if (results.size() == 1)
			return results.get(0);
		Double d[] = new Double[results.get(0).length];
		for (int i = 0; i < d.length; i++)
		{
			double sum = 0;
			int count = 0;
			for (Double dd[] : results)
				if (dd[i] != null)
				{
					sum += dd[i];
					count++;
				}
			d[i] = sum / (double) count;
		}
		return d;
	}

	public static String[] trim(String[] split)
	{
		String s[] = new String[split.length];
		for (int i = 0; i < s.length; i++)
			s[i] = split[i].trim();
		return s;
	}

	public static Double[] parseDoubleArray(String[] v)
	{
		Double d[] = new Double[v.length];
		for (int i = 0; i < d.length; i++)
			d[i] = DoubleUtil.parseDouble(v[i]);
		return d;
	}

	public static <T> boolean equals(T[] d1, T[] d2)
	{
		if (d1.length != d2.length)
			return false;
		for (int i = 0; i < d2.length; i++)
			if (!ObjectUtil.equals(d1[i], d2[i]))
				return false;
		return true;
	}

	public static <T> boolean equals(T[] d1, T[] d2, int indices[])
	{
		if (indices == null)
			return equals(d1, d2);
		for (int i = 0; i < indices.length; i++)
			if (!ObjectUtil.equals(d1[indices[i]], d2[indices[i]]))
				return false;
		return true;
	}

	public static boolean equals(int[] a1, int[] a2)
	{
		if (a1.length != a2.length)
			return false;
		for (int i = 0; i < a2.length; i++)
			if (a1[i] != a2[i])
				return false;
		return true;
	}

	public static boolean redundant(String[] a1, String[] a2)
	{
		return redundant(a1, a2, null);
	}

	public static boolean redundant(String[] a1, String[] a2, int indices[])
	{
		if (a1.length != a2.length)
			return false;
		HashMap<String, String> map1 = new HashMap<String, String>();
		HashMap<String, String> map2 = new HashMap<String, String>();

		for (int j = 0; j < (indices != null ? indices.length : a2.length); j++)
		{
			int i = indices != null ? indices[j] : j;

			if (a1[i] == null || a2[i] == null)
			{
				if (a1[i] != null || a2[i] != null)
					return false;
			}
			else if (map1.containsKey(a1[i]) || map2.containsKey(a2[i]))
			{
				if (!map1.containsKey(a1[i]) || !map1.get(a1[i]).equals(a2[i])
						|| !map2.containsKey(a2[i]) || !map2.get(a2[i]).equals(a1[i]))
					return false;
			}
			else
			{
				map1.put(a1[i], a2[i]);
				map2.put(a2[i], a1[i]);
			}
		}
		return true;
	}

	public static double[] flatten(double[][] a)
	{
		double r[] = new double[a.length * a[0].length];
		for (int i = 0; i < a.length; i++)
			System.arraycopy(a[i], 0, r, i * a[0].length, a[0].length);
		return r;
	}

	public static double[][] removeDiagonale(double[][] a)
	{
		if (a.length != a[0].length)
			throw new IllegalArgumentException();
		double r[][] = new double[a.length][a.length - 1];
		for (int i = 0; i < a.length; i++)
		{
			System.arraycopy(a[i], 0, r[i], 0, i);
			System.arraycopy(a[i], i + 1, r[i], i, a.length - (i + 1));
		}
		return r;
	}

	/**
	 * without diagonale
	 * 
	 * @param a
	 * @return
	 */
	public static double[] concatUpperTriangular(double[][] a)
	{
		if (a.length != a[0].length || a[0][a.length - 1] != a[a.length - 1][0]) // check corner entries
			throw new IllegalArgumentException();

		int l = (a.length * (a.length - 1)) / 2;
		double r[] = new double[l];
		int posR = 0;
		for (int i = 0; i < a.length - 1; i++)
		{
			int posA = 1 + i;
			l = a[0].length - posA;
			if (a[i][posA] != a[posA][i]) // check at least a few entries to make sure its symmetrical
				throw new IllegalArgumentException();
			System.arraycopy(a[i], posA, r, posR, l);
			posR += l;
		}
		return r;
	}

	public static int getMinIndex(float[] array)
	{
		float min = Float.MAX_VALUE;
		int idx = -1;
		for (int i = 0; i < array.length; i++)
		{
			if (array[i] < min)
			{
				min = array[i];
				idx = i;
			}
		}
		return idx;
	}

	public static int getMaxIndex(double[] array)
	{
		double max = -Double.MAX_VALUE;
		int idx = -1;
		for (int i = 0; i < array.length; i++)
		{
			if (array[i] > max)
			{
				max = array[i];
				idx = i;
			}
		}
		return idx;
	}

	public static List<Object> flatten(Object[] elements)
	{
		List<Object> o = new ArrayList<>();
		for (Object e : elements)
			if (e == null || !e.getClass().isArray())
				o.add(e);
			else
				o.addAll(flatten((Object[]) e));
		return o;
	}

	public static void main(String args[])
	{
		//		int[] a = new int[] { 1, 2, 3 };
		//		System.out.println(ArrayUtil.toString(push(a, 4)));

		//		Random r = new Random();
		//		int n = 30000;
		//		double a[] = new double[n];
		//		for (int i = 0; i < a.length; i++)
		//			a[i] = r.nextDouble();
		//		//		System.out.println(ArrayUtil.toString(a));
		//		StopWatchUtil.start("new");
		//		int o1[] = getOrdering(a, false);
		//		StopWatchUtil.stop("new");
		//		StopWatchUtil.start("old");
		//		int o2[] = getOrdering_(a, false);
		//		StopWatchUtil.stop("old");
		//		StopWatchUtil.print();
		//		System.out.println(ArrayUtil.toString(o1));
		//		System.out.println(ArrayUtil.toString(o2));

		//		int[] a = new int[] { 1 };
		//		System.out.println(ListUtil.toString(ArrayUtil.toList(a)));
		//		System.out.println(ArrayUtil.toList(a).getClass());
		//		ArrayUtil.toList(a).add((Integer) 2);

		//		Double[] a = new Double[] { 0.0, 1.0, 2.0, 3.0, 1.0, 0.0, 4.0, 5.0 };
		//		System.out.println(ArrayUtil.toString(removeDuplicates(a)));

		//		double[][] a = new double[][] { //
		//		/*    */{ 0.0, 1.0, 2.0, 3.0 }, //
		//				{ 1.0, 0.0, 4.0, 5.0 }, //
		//				{ 2.0, 4.0, 0.0, 6.0 }, //
		//				{ 3.0, 5.0, 6.0, 0.0 } };
		//		System.out.println(ArrayUtil.toString(concatUpperTriangular(a)));
		//		System.out.println(ArrayUtil.toString(removeDiagonale(a)));

		//		String a1[] = { "a", "d", null, "a", "d", "y", "a" };
		//		String a2[] = { "b", "c", null, "b", "c", "x", "b" };
		//		System.out.println(redundant(a1, a2));

		//		System.out.println(toString(toArray(getDistinctValues(new Double[] { 1.0, 1.0, 2.0, 2.0, 2.0, 3.0, 5.0 }))));
		//		System.out.println(toString(toArray(getDistinctValues(new String[] { "a", "a", "b" }))));

		//		System.out.println(toString(toArray(getDistinctValues(new Vector3f[] { new Vector3f(3.0f, 2.0f, 3.0f),
		//				new Vector3f(1.0f, 2.0f, 3.0f), new Vector3f(3.0f, 2.0f, 3.0f) }))));
		//		System.out.println(new HashSet<Vector3f>(toList(new Vector3f[] { new Vector3f(3.0f, 2.0f, 3.0f),
		//				new Vector3f(1.0f, 2.0f, 3.0f), new Vector3f(3.0f, 2.0f, 3.0f) })).size());
		//		Object o[] = { "a", null };
		//		Object o[] = null;
		//		System.out.println(ArrayUtil.toString(o));

		//		System.out.println(toString(getEntropy(new double[] { 1.0, 1.0, 2.0, 2.0, 2.0, 3.0 })));

		//		Integer[] input1 = new Integer[] { 1, 2, 3, 4, 5, 6 };
		//		Integer[] input2 = new Integer[] { 7, 8, 5, 1, 11, 1 };
		//		int[] ordering = naiveMap(input1, input2, new Comparator<Integer>()
		//		{
		//			public int compare(Integer o1, Integer o2)
		//			{
		//				return Math.abs(o1 - o2);
		//			}
		//		});
		//		System.out.println(toString(ordering) + "\n");
		//		System.out.println(toString(input1));
		//		System.out.println(toString(sortAccordingToOrdering(ordering, input2)));

		//		String s[] = { "ene", "miste", "mene" };
		//		Integer[] rank = new Integer[] { 1, 3, 2 };
		//		int order[] = getOrdering(rank, new Comparator<Integer>()
		//		{
		//			@Override
		//			public int compare(Integer o1, Integer o2)
		//			{
		//				return o1.compareTo(o2);
		//			}
		//		}, true);
		//		System.out.println(toString(order) + "\n");
		//		System.out.println(toString(sortAccordingToOrdering(order, rank)) + "\n");
		//		System.out.println(toString(sortAccordingToOrdering(order, s)) + "\n");

		//		Integer[] input = new Integer[] { 1, 2, 3, 4, 5, 6 };
		//		List<Integer[]> output = permute(input);

		//		for (Integer[] a : output)
		//			System.out.println(toString(a));

		//		Object o[] = { "a", "b", "a", "c", "b", "b" };
		//		System.out.println(toString(normalize(o)));

		//		Double d[] = new Double[] { -4.0, 2.0, 0.0, -5.0, 1.0, 5.0, null };
		//		System.out.println(toString(normalize(d, false)));

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
