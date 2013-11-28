package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HashMapUtil
{
	public static int keyIndex(LinkedHashMap<?, ?> map, Object key)
	{
		int count = 0;
		for (Object k : map.keySet())
		{
			if (k.equals(key))
				return count;
			count++;
		}
		return -1;
	}

	/**
	 * equal keys have equal values?, no equal keys => compatible
	 * 
	 * @param map1
	 * @param map2
	 * @return
	 */
	public static boolean isCompatible(Map<?, ?> map1, Map<?, ?> map2)
	{
		if (map1 == null || map2 == null)
			return true;

		Set<?> s1 = map1.keySet();
		Set<?> s2 = map2.keySet();
		for (Object key1 : s1)
		{
			if (s2.contains(key1))
			{
				Object val1 = map1.get(key1);
				Object val2 = map2.get(key1);

				if (val1 instanceof Map<?, ?> && val2 instanceof Map<?, ?>)
					if (!isCompatible((Map<?, ?>) val1, (Map<?, ?>) val2))
						return false;
					else if (!val1.equals(val2))
						return false;
			}
		}
		return true;
	}

	// public static HashMap<String, Object> merge(HashMap<?, ?> map)
	// {
	// HashMap<String, Object> m = new HashMap<String, Object>();
	//
	// for (Object key : map.keySet())
	// {
	// if (!(key instanceof String))
	// throw new IllegalArgumentException("not cloneable: key not a string");
	// Object val = map.get(key);
	//
	// if (val instanceof HashMap<?, ?>)
	// m.put((String) key, HashMapUtil.clone((HashMap<?, ?>) val));
	// else if (val instanceof String || val instanceof Number)
	// m.put((String) key, val);
	// else
	// throw new IllegalArgumentException("not cloneable: value : " + val + " "
	// + val.getClass());
	// }
	// return m;
	// }

	public static String toString(HashMap<?, ?> map)
	{
		return toString(map, "=", ",");
	}

	public static String toString(HashMap<?, ?> map, String is, String sep)
	{
		String s = "";
		List<Object> keys = new ArrayList<Object>(map.keySet());
		Collections.sort(keys, new Comparator<Object>()
		{
			@Override
			public int compare(Object arg0, Object arg1)
			{
				return arg0.toString().compareTo(arg1.toString());
			}
		});
		for (Object k : keys)
			s += k + is + map.get(k) + sep;
		if (s.length() > 0)
			s = s.substring(0, s.length() - 1);
		return s;
	}

	public static HashMap<String, Object> clone(HashMap<?, ?> map)
	{
		if (map == null)
			return null;

		HashMap<String, Object> m = new HashMap<String, Object>();

		for (Object key : map.keySet())
		{
			if (!(key instanceof String))
				throw new IllegalArgumentException("not cloneable: key not a string");
			Object val = map.get(key);

			if (val instanceof HashMap<?, ?>)
				m.put((String) key, HashMapUtil.clone((HashMap<?, ?>) val));
			else if (val instanceof String || val instanceof Number)
				m.put((String) key, val);
			else
				throw new IllegalArgumentException("not cloneable: value : " + val + " " + val.getClass());
		}
		return m;
	}
}
