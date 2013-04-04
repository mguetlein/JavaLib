package util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DefaultComparator<T> implements Comparator<T>
{
	boolean ascending;

	public DefaultComparator()
	{
		this(true);
	}

	public DefaultComparator(boolean ascending)
	{
		this.ascending = ascending;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int compare(T o1, T o2)
	{
		if (o1 == null)
		{
			if (o2 == null)
				return 0;
			else
				return ascending ? 1 : -1;
		}
		else if (o2 == null)
			return ascending ? -1 : 1;
		if (ascending)
			return ((Comparable<T>) o1).compareTo(o2);
		else
			return ((Comparable<T>) o2).compareTo(o1);
	}

	public static void main(String[] args)
	{
		List<String> l = ArrayUtil.toList(new String[] { "b", "a", "c", "0", "1" });
		Collections.sort(l, new DefaultComparator<String>(false));
		System.out.println(ListUtil.toString(l));
	}
}
