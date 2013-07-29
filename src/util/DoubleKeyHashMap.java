package util;

import java.util.LinkedHashMap;
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

}
