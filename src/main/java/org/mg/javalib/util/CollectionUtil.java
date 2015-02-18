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

	public static <T> T[] toArray(Collection<T> l)
	{
		if (l.size() == 0)
			throw new IllegalArgumentException("collection size is 0");
		return toArray(l.iterator().next().getClass(), l);
	}

	public static <T> T[] toArray(Class<?> type, Collection<T> l)
	{
		@SuppressWarnings("unchecked")
		T a[] = (T[]) Array.newInstance(type, l.size());
		return l.toArray(a);
	}

}
