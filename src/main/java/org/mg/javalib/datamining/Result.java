package org.mg.javalib.datamining;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.mg.javalib.util.ArrayUtil;
import org.mg.javalib.util.CountedSet;

public class Result implements Serializable
{
	private static final long serialVersionUID = 1L;

	private HashMap<String, Object> values = new HashMap<String, Object>();

	// private HashMap<String, Double> variance = new HashMap<String, Double>();

	private HashMap<String, Integer> mergeCount;

	public static final int JOIN_MODE_MEAN = 0;
	public static final int JOIN_MODE_CONCAT = 1;
	public static final int JOIN_MODE_SUM = 2;

	public Result copy()
	{
		return copy(null);
	}

	public Result copy(Set<String> acceptAttributes)
	{
		Result r = new Result();
		for (String k : values.keySet())
			if (acceptAttributes == null || acceptAttributes.contains(k))
				r.values.put(k, values.get(k));
		if (mergeCount != null)
		{
			r.mergeCount = new HashMap<String, Integer>();
			for (String k : mergeCount.keySet())
				r.mergeCount.put(k, mergeCount.get(k));
		}
		return r;
	}

	public Result join(Result result, List<String> properties, List<String> propertiesToBeEqual,
			List<String> varianceProperties, int joinMode)
	{
		Result rs = new Result();

		if (result.mergeCount != null)
			throw new Error("not working, clear first");

		for (String prop : properties)
		{
			if (prop.endsWith(ResultSet.VARIANCE_SUFFIX))
				continue;

			if (propertiesToBeEqual.contains(prop))
				rs.values.put(prop, values.get(prop));
			else
			{
				Object val1 = values.get(prop);
				Object val2 = result.values.get(prop);
				Object val = null;

				Double var = null;

				if ((joinMode == JOIN_MODE_MEAN || joinMode == JOIN_MODE_SUM) && val1 instanceof Number)
				{
					if (val1 instanceof String && val1.equals("null"))
						val1 = Double.NaN;
					if (val2 instanceof String && val2.equals("null"))
						val2 = Double.NaN;
					Double d1 = ((Number) val1).doubleValue();
					Double d2 = ((Number) val2).doubleValue();
					if (Double.isNaN(d1))
					{
						val = d2;
					}
					else if (Double.isNaN(d2))
					{
						val = d1;
						if (joinMode == JOIN_MODE_MEAN && mergeCount != null && mergeCount.containsKey(prop))
						{
							if (rs.mergeCount == null)
								rs.mergeCount = new HashMap<String, Integer>();
							rs.mergeCount.put(prop, mergeCount.get(prop));
						}
					}
					else
					{
						if (joinMode == JOIN_MODE_MEAN)
						{
							double weight = (mergeCount != null && mergeCount.containsKey(prop)) ? mergeCount.get(prop)
									: 1;
							double value = ((d1 * weight) + d2) / ((double) (weight + 1));
							val = value;
							if (varianceProperties != null && varianceProperties.contains(prop))
							{
								double oldStdDev = 0;
								if (values.containsKey(prop + ResultSet.VARIANCE_SUFFIX))
									oldStdDev = Math.pow((Double) values.get(prop + ResultSet.VARIANCE_SUFFIX), 2);
								double stdDev = oldStdDev * (weight / (double) (weight + 1)) + Math.pow(d2 - value, 2)
										* (1 / (double) weight);
								var = Math.sqrt(stdDev);
							}
							if (rs.mergeCount == null)
								rs.mergeCount = new HashMap<String, Integer>();
							rs.mergeCount.put(prop, (int) weight + 1);
						}
						else if (joinMode == JOIN_MODE_SUM)
							val = d1 + d2;
					}
				}
				else if ((joinMode == JOIN_MODE_MEAN || joinMode == JOIN_MODE_SUM) && val1 instanceof Number[])
				{
					throw new Error("no array join implemented");
				}
				else
				{
					val = val1 + "/" + val2;
				}

				rs.values.put(prop, val);
				if (var != null)
					rs.values.put(prop + ResultSet.VARIANCE_SUFFIX, var);
			}
		}
		//		if (rs.mergeCount != null)
		//		{
		//			System.out.println("");
		//			for (String p : rs.mergeCount.keySet())
		//				System.out.println(p + " " + rs.mergeCount.get(p));
		//			System.out.println("");
		//		}
		return rs;
	}

	public Object getValue(String property)
	{
		return values.get(property);
	}

	public void setValue(String property, Object value)
	{
		values.put(property, value);
	}

	public void remove(String property)
	{
		values.remove(property);
	}

	public String getValueToString(String property)
	{
		Object value = values.get(property);
		if (value == null)
			return "null";
		else if (value instanceof Number[])
		{
			String s = "";
			for (Number n : (Number[]) value)
				s += (s.length() == 0 ? "" : ";") + n.toString();
			return s;
		}
		else
			return value.toString();
	}

	public String toString(List<String> propreties)
	{
		List<String> values = new ArrayList<String>();
		for (String p : propreties)
			values.add(getValueToString(p));
		return ArrayUtil.toCSVString(ArrayUtil.toArray(values));
	}

	public boolean equals(List<String> propertiesToBeEqual, Result result)
	{
		for (String p : propertiesToBeEqual)
		{
			Object v = values.get(p);
			if (v == null)
			{
				if (result.values.get(p) != null)
					return false;
			}
			else
			{
				if (!v.equals(result.values.get(p)))
					return false;
			}
		}
		return true;
	}

	public boolean isMergedResult()
	{
		return mergeCount != null;
	}

	public void clearMergeCount()
	{
		mergeCount = null;
	}

	public void unifyJoinedStringValues(String property)
	{
		if (getValue(property) instanceof String)
		{
			CountedSet<String> vals = CountedSet.create(getValue(property).toString().split("/"));
			if (vals.getNumValues() == 1)
				setValue(property, vals.values().get(0));
		}
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof Result))
			return false;
		Result r = (Result) o;
		if (!r.values.equals(values))
			return false;
		return true;
	}

}
