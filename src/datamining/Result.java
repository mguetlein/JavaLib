package datamining;

import java.util.HashMap;
import java.util.List;

public class Result
{
	private HashMap<String, Object> values = new HashMap<String, Object>();

	// private HashMap<String, Double> variance = new HashMap<String, Double>();

	private int mergeCount = 1;

	public static final int JOIN_MODE_MEAN = 0;
	public static final int JOIN_MODE_CONCAT = 1;

	public Result join(Result result, List<String> properties, List<String> propertiesToBeEqual,
			List<String> varianceProperties, int joinMode)
	{
		Result rs = new Result();

		int weight = mergeCount;
		if (result.mergeCount > 1)
			throw new Error("not working");

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

				if (joinMode == JOIN_MODE_MEAN && val1 instanceof Number)
				{
					double value = ((((Number) val1).doubleValue() * weight) + ((Number) val2).doubleValue())
							/ ((double) (weight + 1));
					val = value;

					if (varianceProperties != null && varianceProperties.contains(prop))
					{
						double oldStdDev = 0;
						if (values.containsKey(prop + ResultSet.VARIANCE_SUFFIX))
							oldStdDev = Math.pow((Double) values.get(prop + ResultSet.VARIANCE_SUFFIX), 2);

						double stdDev = oldStdDev * (weight / (double) (weight + 1))
								+ Math.pow(((Number) val2).doubleValue() - value, 2) * (1 / (double) weight);
						var = Math.sqrt(stdDev);
					}
				}
				else if (joinMode == JOIN_MODE_MEAN && val1 instanceof Number[])
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

		rs.mergeCount = weight + 1;
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
		String s = "";
		for (String p : propreties)
		{
			if (s.length() > 0)
				s += ",";
			// s += values.get(p);
			s += getValueToString(p);
		}
		return s;
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
		return mergeCount > 1;
	}

}
