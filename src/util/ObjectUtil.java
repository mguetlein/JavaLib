package util;

public class ObjectUtil
{
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static int compare(Comparable d1, Comparable d2)
	{
		if (d1 == null)
			if (d2 == null)
				return 0;
			else
				return 1;
		else if (d2 == null)
			return -1;
		else
			return d1.compareTo(d2);
	}

	public static boolean equals(Object o1, Object o2)
	{
		if (o1 == null)
			return o2 == null;
		else if (o2 == null)
			return false;
		else
			return o1.equals(o2);
	}
}
