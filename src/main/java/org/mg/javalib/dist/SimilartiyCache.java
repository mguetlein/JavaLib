package org.mg.javalib.dist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.mg.javalib.util.DoubleArraySummary;

public class SimilartiyCache<T>
{
	HashMap<String, Double> cachedDistances = new HashMap<String, Double>();

	SimilarityMeasure<T> sim;

	private static HashMap<SimilarityMeasure<?>, SimilartiyCache<?>> INSTANCES = new HashMap<SimilarityMeasure<?>, SimilartiyCache<?>>();

	private SimilartiyCache(SimilarityMeasure<T> sim)
	{
		this.sim = sim;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> SimilartiyCache<T> get(SimilarityMeasure<T> sim)
	{
		if (!INSTANCES.containsKey(sim))
			INSTANCES.put(sim, new SimilartiyCache(sim));
		return (SimilartiyCache<T>) INSTANCES.get(sim);
	}

	private String key(T[] t1, T[] t2)
	{
		int h1 = t1.hashCode();
		int h2 = t2.hashCode();
		return (h1 < h2 ? (h1 + "#" + h2) : (h2 + "#" + h1));
	}

	public Double similarity(List<T[]> t)
	{
		List<Double> distances = new ArrayList<Double>();
		for (int i = 0; i < t.size() - 1; i++)
			for (int j = i + 1; j < t.size(); j++)
			{
				Double d = similarity(t.get(i), t.get(j));
				if (d != null)
					distances.add(d);
			}
		if (distances.size() > 0)
			return DoubleArraySummary.create(distances).getMean();
		else
			return null;
	}

	public Double similarity(T[] t1, T[] t2)
	{
		String key = key(t1, t2);
		if (!cachedDistances.containsKey(key))
			cachedDistances.put(key, sim.similarity(t1, t2));
		return cachedDistances.get(key);
	}

}
