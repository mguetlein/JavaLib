package org.mg.javalib.util;

import java.util.HashSet;
import java.util.Set;

public class SetUtil
{

	@SuppressWarnings("unchecked")
	@SafeVarargs
	public static <T, T2> HashSet<T> createHashSetExplicit(Class<T> type, T2... entries){
	HashSet<T> t = new HashSet<>();
	for (T2 e : entries)
		t.add((T) e);
	return t;
}

@SafeVarargs
public static <T> HashSet<T> createHashSet(T... entries)
{
		HashSet<T> t = new HashSet<>();
		for (T e : entries)
			t.add(e);
		return t;
	}

	public static <T> int intersectSize(Set<T> set1, Set<T> set2)
	{
		int num = 0;
		for (T t : set1)
			if (set2.contains(t))
				num++;
		return num;
	}

	public static <T> boolean isSubSet(Set<T> superSet, Set<T> subSet)
	{
		if (subSet.size() > superSet.size())
			return false;
		for (T t : subSet)
			if (!superSet.contains(t))
				return false;
		return true;
	}

	public static <T> T firstEntry(Set<T> set)
	{
		return set.iterator().next();
	}

	public static void main(String[] args)
	{
		String a1[] = "a b c d e".split(" ");
		String a2[] = "b b d e f f".split(" ");
		System.out.println(intersectSize(new HashSet<String>(ArrayUtil.toList(a2)),
				new HashSet<String>(ArrayUtil.toList(a1))));
	}
}
