package util;

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

}
