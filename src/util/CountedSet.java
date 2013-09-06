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

	public int size()
	{
		return map.size();
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
		if (!html)
		{
			String s = "";
			for (T elem : values())
				s += getCount(elem) + "\u00D7\u2009'" + String.valueOf(elem) + "', ";
			return s.substring(0, s.length() - 2) + "";
		}
		else
		{
			String s = "";
			for (T elem : values())
				s += getCount(elem) + "&times;&thinsp;<i>" + StringEscapeUtils.escapeHtml(String.valueOf(elem))
						+ "</i>, ";
			return s.substring(0, s.length() - 2) + "";
		}
	}

	public int getCount(T elem)
	{
		if (map.containsKey(elem))
			return map.get(elem);
		else
			return 0;
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

	@Override
	public int getNumNull()
	{
		if (!map.containsKey(null))
			return 0;
		else
			return map.get(null);
	}

	public boolean contains(T key)
	{
		return map.containsKey(key);
	}

	public void delete(T key)
	{
		map.remove(key);
	}

	public int sum()
	{
		int sum = 0;
		for (Integer value : map.values())
			sum += value;
		return sum;
	}

	public T getMode()
	{
		return values().get(0);
	}

}
