package org.mg.javalib.util;

import java.util.Comparator;

public class ToStringComparator implements Comparator<Object>
{
	private static int compareStatic(Object o1, Object o2)
	{
		if (o1 == null)
		{
			if (o2 == null)
				return 0;
			else
				return 1;
		}
		else if (o2 == null)
			return -1;
		return o1.toString().compareTo(o2.toString());
	}

	public int compare(Object o1, Object o2)
	{
		return compareStatic(o1, o2);
	}

	public static boolean equals(Object o1, Object o2)
	{
		return compareStatic(o1, o2) == 0;
	}
}
