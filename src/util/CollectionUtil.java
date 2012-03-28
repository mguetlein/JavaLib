package util;

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
		@SuppressWarnings("unchecked")
		T a[] = (T[]) Array.newInstance(l.iterator().next().getClass(), l.size());
		return l.toArray(a);
	}

}
