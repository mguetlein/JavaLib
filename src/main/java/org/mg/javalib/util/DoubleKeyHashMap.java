package org.mg.javalib.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Set;

public class DoubleKeyHashMap<T1, T2, T3>
{
	private LinkedHashMap<T1, LinkedHashMap<T2, T3>> map = new LinkedHashMap<T1, LinkedHashMap<T2, T3>>();

	public T3 get(T1 key1, T2 key2)
	{
		if (!map.containsKey(key1))
			return null;
		else
			return map.get(key1).get(key2);
	}

	public void put(T1 key1, T2 key2, T3 value)
	{
		if (!map.containsKey(key1))
			map.put(key1, new LinkedHashMap<T2, T3>());
		map.get(key1).put(key2, value);
	}

	public T3 remove(T1 key1, T2 key2)
	{
		if (!map.containsKey(key1))
			return null;
		else
		{
			T3 res = map.get(key1).remove(key2);
			if (map.get(key1).size() == 0)
				map.remove(key1);
			return res;
		}
	}

	public Set<T1> keySet1()
	{
		return map.keySet();
	}

	public Set<T2> keySet2(T1 key1)
	{
		if (!map.containsKey(key1))
			return null;
		return map.get(key1).keySet();
	}

	public boolean containsKey(T1 key1)
	{
		return map.containsKey(key1);
	}

	public boolean containsKeyPair(T1 key1, T2 key2)
	{
		if (!map.containsKey(key1))
			return false;
		return map.get(key1).containsKey(key2);
	}

	public void clear()
	{
		map.clear();
	}

	public void removeWithKey2(T2 key2)
	{
		for (HashMap<T2, T3> m : map.values())
			m.remove(key2);
	}

	public static void main(String[] args)
	{
		Random r = new Random();
		DoubleKeyHashMap<String, Integer, Double> test = new DoubleKeyHashMap<String, Integer, Double>();
		HashMap<String, Double> test2 = new HashMap<String, Double>();

		int inserts = 0;
		int inserts2 = 0;
		for (int j = 0; j < 1000000; j++)
		{
			String s = StringUtil.randomString(0, 3, r);
			Integer i = r.nextInt(10);
			Double d = r.nextDouble();

			StopWatchUtil.start("double");
			if (!test.containsKeyPair(s, i))
			{
				inserts++;
				test.put(s, i, d);
			}
			StopWatchUtil.stop("double");

			StopWatchUtil.start("string-key");
			StringBuffer b = new StringBuffer();
			b.append(s);
			b.append("#");
			b.append(i);
			String k = b.toString();
			if (!test2.containsKey(k))
			{
				inserts2++;
				test2.put(k, d);
			}
			StopWatchUtil.stop("string-key");
		}

		System.out.println(inserts);
		System.out.println(inserts2);
		StopWatchUtil.print();

	}

}
