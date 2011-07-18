package util;

import java.util.Comparator;

public class DefaultComparator<T> implements Comparator<T>
{

	@SuppressWarnings("unchecked")
	@Override
	public int compare(T o1, T o2)
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
		return ((Comparable<T>) o1).compareTo(o2);
	}
}
