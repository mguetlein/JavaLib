package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.swing.JLabel;

import org.apache.commons.lang.StringEscapeUtils;

public class CountedSet<T> implements ArraySummary
{

	public HashMap<T, Integer> map;

	public CountedSet()
	{
		map = new HashMap<T, Integer>();
	}

	@SuppressWarnings({ "unchecked", "hiding" })
	public <T> CountedSet<T> copy()
	{
		CountedSet<T> r = new CountedSet<T>();
		r.map = (HashMap<T, Integer>) map.clone();
		return r;
	}

	public void add(T elem)
	{
		if (map.get(elem) == null)
			map.put(elem, 1);
		else
			map.put(elem, map.get(elem) + 1);
	}

	public void remove(T elem)
	{
		map.remove(elem);
	}

	public void rename(T elem, T replace)
	{
		map.put(replace, map.remove(elem));
	}

	public void add(T elem, int times)
	{
		for (int i = 0; i < times; i++)
			add(elem);
	}

	public int getNumValues()
	{
		return getNumValues(true);
	}

	public int getNumValues(boolean includingNull)
	{
		if (includingNull)
			return map.size();
		else
			return map.size() - (map.containsKey(null) ? 1 : 0);
	}

	public int getSum()
	{
		return getSum(true);
	}

	public int getSum(boolean includingNull)
	{
		return sum(includingNull);
	}

	public int sum()
	{
		return sum(true);
	}

	public int sum(boolean includingNull)
	{
		int sum = 0;
		for (T key : map.keySet())
			if (includingNull || key != null)
				sum += map.get(key);
		return sum;
	}

	/**
	 * in order of counts
	 * 
	 * @return
	 */
	public List<T> values()
	{
		return values(new Comparator<T>()
		{
			@Override
			public int compare(T o1, T o2)
			{
				int res = map.get(o1).compareTo(map.get(o2));
				if (res == 0)
					return (o1 + "").compareTo(o2 + ""); //accepts null values 
				else
					return (-1) * res;
			}
		});
	}

	public List<T> values(Comparator<T> comp)
	{
		List<T> elems = new ArrayList<T>(map.keySet());
		Collections.sort(elems, comp);
		return elems;
	}

	public String toString()
	{
		return toString(false);
	}

	public String toString(boolean html)
	{
		if (map.size() == 0)
			return "";
		List<T> values = values();
		if (values.size() > 1 && values.get(0) == null)
			values.add(values.remove(0)); // move null to the back
		if (!html)
		{
			String s = "";
			for (T elem : values)
				s += getCount(elem) + "\u00D7\u2009'" + String.valueOf(elem) + "', ";
			return s.substring(0, s.length() - 2) + "";
		}
		else
		{
			String s = "";
			for (T elem : values)
				s += getCount(elem) + "&times;&thinsp;<i>" + StringEscapeUtils.escapeHtml(String.valueOf(elem))
						+ "</i>, ";
			return s.substring(0, s.length() - 2) + "";
		}
	}

	@Override
	public int getNullCount()
	{
		return getCount(null);
	}

	public int getCount(T elem)
	{
		if (map.containsKey(elem))
			return map.get(elem);
		else
			return 0;
	}

	public int getMaxCount(boolean includingNull)
	{
		if (getNumValues(includingNull) == 0)
			return 0;
		else
		{
			List<T> values = values();
			if (!includingNull && values.size() > 1 && values.get(0) == null)
				return map.get(values.get(1));
			else
				return map.get(values.get(0));
		}
	}

	public static <T> CountedSet<T> create(Iterable<T> objects)
	{
		CountedSet<T> set = new CountedSet<T>();
		for (T t : objects)
			set.add(t);
		return set;
	}

	public static <T> CountedSet<T> create(T[] array)
	{
		return fromArray(array);
	}

	public static <T> CountedSet<T> fromArray(T[] array)
	{
		CountedSet<T> set = new CountedSet<T>();
		for (T t : array)
			set.add(t);
		return set;
	}

	public static void main(String args[])
	{
		SwingUtil.showInDialog(new JLabel("<html>"
				+ CountedSet.create(new String[] { "asdf", "asdf", "ene", "mene" }).toString(true) + "</html>"));
		//		System.out.println(fromArray(new String[] { "a", "b", "b", "c", "a", "b", "c", "b" }));
	}

	public boolean contains(T key)
	{
		return map.containsKey(key);
	}

	public void delete(T key)
	{
		map.remove(key);
	}

	public T getMode(boolean includingNull)
	{
		if (getNumValues(includingNull) == 0)
			return null;
		else
		{
			List<T> values = values();
			if (!includingNull && values.size() > 1 && values.get(0) == null)
				return values.get(1);
			else
				return values.get(0);
		}
	}

}
