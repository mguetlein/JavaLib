package org.mg.javalib.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;

public class CountedSet<T> implements ArraySummary
{
	public LinkedHashMap<T, Integer> map;

	T toBack;

	public CountedSet()
	{
		map = new LinkedHashMap<T, Integer>();
	}

	@SuppressWarnings({ "unchecked", "hiding" })
	public <T> CountedSet<T> copy()
	{
		CountedSet<T> r = new CountedSet<T>();
		r.map = (LinkedHashMap<T, Integer>) map.clone();
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

	public Set<T> valuesInsertionOrder()
	{
		return map.keySet();
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

	public String toStringDeviationFromMax()
	{
		if (map.size() == 0)
			return "";
		List<T> values = values();
		if (values.size() > 1
				&& (values.get(0) == null || (toBack != null && (values.get(0).equals(toBack)))))
			values.add(values.remove(0)); // move null to the back
		StringBuffer sb = new StringBuffer();
		int max = getMaxCount(true);
		sb.append(max);
		sb.append("\u00D7");

		StringBuffer dev = new StringBuffer();
		for (T elem : values)
		{
			int count = getCount(elem);
			if (count == max)
				continue;
			dev.append(count);
			dev.append("\u00D7\u2009'");
			dev.append(String.valueOf(elem));
			dev.append("', ");
		}
		String devStr = dev.toString();
		if (!devStr.isEmpty())
			devStr = " (" + devStr.substring(0, devStr.length() - 2) + ")";
		return sb.toString() + devStr;
	}

	public String toString()
	{
		return toString(false);
	}

	public void setToBack(T val)
	{
		toBack = val;
	}

	public String toString(boolean html)
	{
		if (map.size() == 0)
			return "";
		List<T> values = values();
		if (values.size() > 1
				&& (values.get(0) == null || (toBack != null && (values.get(0).equals(toBack)))))
			values.add(values.remove(0)); // move null to the back
		if (!html)
		{
			StringBuffer sb = new StringBuffer();
			for (T elem : values)
			{
				sb.append(getCount(elem));
				sb.append("\u00D7\u2009'");
				sb.append(String.valueOf(elem));
				sb.append("', ");
			}
			String s = sb.toString();
			return s.substring(0, s.length() - 2) + "";
		}
		else
		{
			StringBuffer sb = new StringBuffer();
			for (T elem : values)
			{
				sb.append(getCount(elem));
				sb.append("&times;&thinsp;<i>");
				sb.append(StringEscapeUtils.escapeHtml4(String.valueOf(elem)));
				sb.append("</i>, ");
				if (sb.length() > 300)
				{
					sb.append("..."); // long html string may cause problems when used in swing components 
					break; // not informative anyway
				}
			}
			String s = sb.toString();
			return s.substring(0, s.length() - 2) + "";
		}
	}

	@Override
	public int getNullCount()
	{
		return getCount(null);
	}

	@Override
	public boolean isAllNull()
	{
		return getSum(false) == 0;
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
		CountedSet<Object> o = new CountedSet<Object>();
		o.add("B");
		o.add("A");
		System.out.println(o.values());
		System.out.println(o.valuesInsertionOrder());

		//		SwingUtil.showInDialog(new JLabel("<html>"
		//				+ CountedSet.create(new String[] { "asdf", "asdf", "ene", "mene" }).toString(true) + "</html>"));
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

	/**
	 * returns the non-null mode when there is no second non-null mode (with equal counts), else null
	 */
	public T getUniqueMode()
	{
		List<T> values = values();
		T uniqueMode = null;
		for (T t : values)
		{
			if (t != null)
			{
				if (uniqueMode == null)
					uniqueMode = t;
				else
				{
					if (getCount(uniqueMode) > getCount(t))
						return uniqueMode;
					else
						return null;
				}
			}
		}
		return uniqueMode;
	}

	//	private int[] getCounts(boolean includingNull)
	//	{
	//		int counts[] = new int[getNumValues(includingNull)];
	//		int i = 0;
	//		for (T t : values())
	//			if (includingNull || t != null)
	//				counts[i++] = getCount(t);
	//		return counts;
	//	}
	//
	//	public boolean redundant(CountedSet<?> cs)
	//	{
	//		if (getNumValues() != cs.getNumValues())
	//			return false;
	//		if (getSum() != cs.getSum())
	//			return false;
	//		if (getNullCount() != cs.getNullCount())
	//			return false;
	//		return ArrayUtil.equals(getCounts(false), cs.getCounts(false));
	//
	//	}
}
