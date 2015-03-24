package org.mg.javalib.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

public class ListUtil
{
	public static List<int[]> deserialize(String serialized)
	{
		List<int[]> res = new ArrayList<int[]>();
		if (serialized != null)
		{
			StringTokenizer tok = new StringTokenizer(serialized, ";");
			while (tok.hasMoreElements())
			{
				String elem = (String) tok.nextElement();
				if (elem.trim().length() > 0)
					res.add(ArrayUtil.intFromCSVString(elem));
			}
		}
		return res;
	}

	public static String serialize(List<int[]> l)
	{
		String s = "";
		for (int[] i : l)
			s += ArrayUtil.intToCSVString(i) + ";";
		return s;
	}

	public static <T> void sort(List<T> sortable, List<T> order)
	{
		int matches = 0;
		for (T t : order)
		{
			int index = sortable.indexOf(t);
			if (index != -1)
			{
				sortable.add(matches, sortable.remove(index));
				matches++;
			}
		}
	}

	public static List<?> cut(List<?> l1, List<?> l2)
	{
		List<Object> l = new ArrayList<Object>();
		for (Object object : l1)
			if (l2.contains(object))
				l.add(object);
		return l;
	}

	public static <T> List<T> cut2(List<T> l1, List<T> l2)
	{
		List<T> l = new ArrayList<T>();
		for (T object : l1)
			if (l2.contains(object))
				l.add(object);
		return l;
	}

	public static Double getMean(List<?> list)
	{
		if (list == null || list.size() == 0)
			return null;

		double mean = 0;
		for (int i = 0; i < list.size(); i++)
			mean = (mean * i + (Double) list.get(i)) / (double) (i + 1);
		return mean;
	}

	public static Double getMin(List<?> list)
	{
		if (list == null || list.size() == 0)
			return null;

		double min = Double.MAX_VALUE;
		for (int i = 0; i < list.size(); i++)
			min = Math.min(min, (Double) list.get(i));
		return min;
	}

	public static Double getMax(List<?> list)
	{
		if (list == null || list.size() == 0)
			return null;

		double max = -Double.MAX_VALUE;
		for (int i = 0; i < list.size(); i++)
			max = Math.max(max, (Double) list.get(i));
		return max;
	}

	public static String toString(List<?> l, String seperator)
	{
		if (l == null)
			return "null";
		else
		{
			String s = "[ ";
			for (Object object : l)
				s += object + seperator;
			if (l.size() > 0)
				s = s.substring(0, s.length() - seperator.length());
			s += " ]";
			return s;
		}
	}

	public static String toString(List<?> l)
	{
		return toString(l, "; ");
	}

	@SuppressWarnings("unchecked")
	public static <T, T2> List<T> cast(Class<T> type, List<T2> list)
	{
		List<T> l = new ArrayList<T>(list.size());
		for (T2 e : list)
			l.add((T) e);
		return l;
	}

	public static <T> List<T> clone(List<T> list)
	{
		List<T> l = new ArrayList<T>(list.size());
		for (T e : list)
			l.add((T) e);
		return l;
	}

	@SafeVarargs
	public static <T> List<T> concat(List<T>... lists)
	{
		List<T> l = new ArrayList<T>();
		for (List<T> list : lists)
			for (T t : list)
				l.add(t);
		return l;
	}

	public static <T> T[] toArray(Collection<T> list)
	{
		return CollectionUtil.toArray(list);
	}

	public static <T> T[] toArray(Class<T> type, Collection<T> list)
	{
		return CollectionUtil.toArray(type, list);
	}

	public static <T> List<T> compact(List<T> list)
	{
		List<T> l = new ArrayList<T>();
		for (T t : list)
			if (t != null)
				l.add(t);
		return l;
	}

	public static <T> T last(List<T> list)
	{
		return list.get(list.size() - 1);
	}

	public static void main(String[] args)
	{
		List<String> l = new ArrayList<String>();
		l.add("a");
		l.add("b");
		l.add("c");
		l.add("d");
		l.add("e");

		List<String> o = new ArrayList<String>();
		o.add("1");
		o.add("2");
		o.add("3");
		o.add("4");
		o.add("5");

		scramble(new Random(), l, o);
		System.out.println(l);
		System.out.println(o);

		//		List<String> l = new ArrayList<String>();
		//		l.add("vier");
		//		l.add("eins");
		//		l.add("fuenf");
		//		l.add("sieben");
		//		l.add("neun");
		//		l.add("drei");
		//
		//		List<String> o = new ArrayList<String>();
		//		o.add("eins");
		//		o.add("zwei");
		//		o.add("drei");
		//
		//		System.out.println(l);
		//		ListUtil.sort(l, o);
		//		System.out.println(l);
	}

	/**
	 * does not preserve order!
	 * 
	 * @param l
	 * @return
	 */
	public static <T> List<T> uniqValue(List<T> l)
	{
		return new ArrayList<T>(new HashSet<T>(l));
	}

	public static interface Filter<T>
	{
		public boolean accept(T object);
	}

	public static <T> List<T> filter(List<T> list, Filter<T> filter)
	{
		List<T> l = new ArrayList<T>();
		for (T t : list)
			if (filter.accept(t))
				l.add(t);
		return l;
	}

	public static <T> void scramble(List<T> list)
	{
		scramble(list, new Random());
	}

	public static <T> void scramble(List<T> list, Random r)
	{
		for (int i = 0; i < list.size(); i++)
		{
			int j = r.nextInt(list.size());
			T tmp = list.get(j);
			list.set(j, list.get(i));
			list.set(i, tmp);
		}
	}

	@SafeVarargs
	public static <T> void scramble(Random r, List<T>... list)
	{
		for (int k = 1; k < list.length; k++)
			if (list[k].size() != list[0].size())
				throw new IllegalArgumentException();

		for (int i = 0; i < list[0].size(); i++)
		{
			int j = r.nextInt(list[0].size());
			for (int k = 0; k < list.length; k++)
			{
				T tmp = list[k].get(j);
				list[k].set(j, list[k].get(i));
				list[k].set(i, tmp);
			}
		}
	}

	public static String toCSVString(List<String> l)
	{
		if (l.size() == 0)
			return "";
		StringBuffer s = new StringBuffer();
		for (String st : l)
		{
			s.append(st);
			s.append(",");
		}
		return s.substring(0, s.length() - 1);
	}

}
