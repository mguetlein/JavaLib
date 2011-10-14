package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class CountedSet<T> implements ArraySummary
{

	public HashMap<T, Integer> map;

	public CountedSet()
	{
		map = new HashMap<T, Integer>();
	}

	public void add(T elem)
	{
		if (map.get(elem) == null)
			map.put(elem, 1);
		else
			map.put(elem, map.get(elem) + 1);
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
		String s = "[ ";
		for (T elem : values())
			s += elem + "(#" + getCount(elem) + "), ";
		return s.substring(0, s.length() - 2) + " ]";
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

	public static <T> CountedSet<T> fromArray(T[] array)
	{
		CountedSet<T> set = new CountedSet<T>();
		for (T t : array)
			set.add(t);
		return set;
	}

	public static void main(String args[])
	{
		System.out.println(fromArray(new String[] { "a", "b", "b", "c", "a", "b", "c", "b" }));
	}
}
