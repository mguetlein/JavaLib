package util;

import java.util.HashMap;
import java.util.Set;

public class DoubleKeyHashMap<T1, T2, T3>
{
	private HashMap<T1, HashMap<T2, T3>> map = new HashMap<T1, HashMap<T2, T3>>();

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
			map.put(key1, new HashMap<T2, T3>());
		map.get(key1).put(key2, value);
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

	public boolean containsKeyPair(T1 key1, T2 key2)
	{
		if (!map.containsKey(key1))
			return false;
		return map.get(key1).containsKey(key2);
	}
}
