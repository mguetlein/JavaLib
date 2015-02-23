package org.mg.javalib.util;

import java.lang.reflect.Array;
import java.util.Collection;

public class CollectionUtil
{
	public static String toString(Collection<?> l)
	{
		String s = "[ ";
		for (Object object : l)
			s += object + "; ";
		if (l.size() > 0)
			s = s.substring(0, s.length() - 2);
		s += " ]";
		return s;
	}

	public static <T> T[] toArray(Collection<T> list)
	{
		@SuppressWarnings("unchecked")
		T a[] = (T[]) Array.newInstance(list.iterator().next().getClass(), list.size());
		return list.toArray(a);
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(Class<T> type, Collection<T> list)
	{
		if (list.size() == 0)
			return (T[]) Array.newInstance(type, 0);
		T a[] = (T[]) Array.newInstance(type, list.size());
		return list.toArray(a);
	}

}
