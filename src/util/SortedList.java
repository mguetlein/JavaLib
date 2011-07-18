package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class SortedList<T> extends ArrayList<T>
{
	Comparator<T> comp;

	public SortedList(Comparator<T> comp)
	{
		this.comp = comp;
	}

	public SortedList()
	{
		this.comp = new Comparator<T>()
		{
			@SuppressWarnings("unchecked")
			@Override
			public int compare(T o1, T o2)
			{
				return ((Comparable<T>) o1).compareTo(o2);
			}
		};
	}

	@Override
	public boolean add(T elem)
	{
		int size = size();
		if (size == 0)
			return super.add(elem);
		else
		{
			int max = size;
			int min = 0;
			int index = min + (max - min) / 2;
			boolean checkedLowerIndex = false;
			boolean checkedHigherIndex = false;

			while (true)
			{
				int oldIndex = index;
				int c = comp.compare(elem, get(index));
				if (c == 0)
				{
					super.add(index, elem);
					return true;
				}
				else if (c < 0)
				{
					if (checkedLowerIndex || index == 0)
					{
						super.add(index, elem);
						return true;
					}
					else if (min == max - 1)
					{
						super.add(index, elem);
						return true;
					}
					else
					{
						max = index;
						index = min + (max - min) / 2;
						checkedLowerIndex = false;
						checkedHigherIndex = index == oldIndex - 1;
					}
				}
				else
				// >0
				{
					if (checkedHigherIndex || index == size - 1)
					{
						super.add(index + 1, elem);
						return true;
					}
					else if (min == max - 1)
					{
						super.add(index + 1, elem);
						return true;
					}
					else
					{
						min = index;
						index = min + (max - min) / 2;
						checkedHigherIndex = false;
						checkedLowerIndex = index - 1 == oldIndex;
					}
				}
			}
		}
	}

	public static void main(String args[])
	{
		int n = 30000;
		int d[] = new int[n];

		Random r = new Random();
		for (int i = 0; i < d.length; i++)
			d[i] = r.nextInt(n) * (r.nextBoolean() ? -1 : 1);

		//		String s = ArrayUtil.intToCSVString(d);
		//		//		String s = "0,2,-7,9,6,8,-5,9,3,-4,";
		//		System.out.println(s);
		//		d = ArrayUtil.intFromCSVString(s);

		System.out.println("sorted list");

		Comparator<Integer> comp = new Comparator<Integer>()
		{
			@Override
			public int compare(Integer o1, Integer o2)
			{
				return o1.compareTo(o2);
			}
		};

		StopWatchUtil.start("sorted list");
		SortedList<Integer> l = new SortedList<Integer>(comp);
		for (Integer dd : d)
			l.add(dd);
		System.out.println(CollectionUtil.toString(l));
		StopWatchUtil.stop("sorted list");

		System.out.println("post sort list");
		StopWatchUtil.start("post sort list");
		List<Integer> l2 = new ArrayList<Integer>();
		for (Integer dd : d)
			l2.add(dd);
		Collections.sort(l2, comp);
		System.out.println(CollectionUtil.toString(l));
		StopWatchUtil.stop("post sort list");
		StopWatchUtil.print();

	}
}
