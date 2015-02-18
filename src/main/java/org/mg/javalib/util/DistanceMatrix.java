package org.mg.javalib.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DistanceMatrix<T>
{
	private boolean symmetric = true;
	private List<T> elems = new ArrayList<T>();
	private HashMap<String, Double> distances = new HashMap<String, Double>();

	public DistanceMatrix()
	{
		this(true);
	}

	public <E> DistanceMatrix<E> cast(Class<E> type)
	{
		DistanceMatrix<E> m = new DistanceMatrix<E>();
		m.symmetric = symmetric;
		m.distances = distances;
		m.elems = ListUtil.cast(type, elems);
		return m;
	}

	public DistanceMatrix(boolean symmetric)
	{
		this.symmetric = symmetric;
	}

	public void setDistance(T t1, T t2, double distance)
	{
		distances.put(getKey(t1, t2, true), distance);
	}

	public double getDistance(T t1, T t2)
	{
		return distances.get(getKey(t1, t2, false));
	}

	public double[][] distances()
	{
		double d[][] = new double[elems.size()][elems.size()];
		for (int i = 0; i < d.length; i++)
		{
			for (int j = 0; j < d.length; j++)
			{
				if (i == j)
					d[i][j] = 0;
				else if (symmetric && i > j)
					d[i][j] = d[j][i];
				else
					d[i][j] = distances.get(i + "_" + j);
			}
		}
		return d;
	}

	private String getKey(T t1, T t2, boolean insert)
	{
		int index1 = elems.indexOf(t1);
		if (index1 == -1)
		{
			if (!insert)
				throw new IllegalArgumentException("not found: " + t1);
			elems.add(t1);
			index1 = elems.size() - 1;
		}
		int index2 = elems.indexOf(t2);
		if (index2 == -1)
		{
			if (!insert)
				throw new IllegalArgumentException("not found: " + t2);
			elems.add(t2);
			index2 = elems.size() - 1;
		}
		if (symmetric && index1 > index2)
		{
			int tmp = index1;
			index1 = index2;
			index2 = tmp;
		}
		return index1 + "_" + index2;
	}
}
