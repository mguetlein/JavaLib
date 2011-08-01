package datamining;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.apache.commons.math.MathException;
import org.apache.commons.math.stat.inference.TTestImpl;

import util.ArrayUtil;
import util.ListUtil;
import util.StringUtil;

public class ResultSet
{
	private int numDecimalPlaces = 2;

	private List<String> properties = new ArrayList<String>();

	private List<Result> results = new ArrayList<Result>();

	public static final String VARIANCE_SUFFIX = "_variance";

	public static final String SIGNIFICANCE_SUFFIX = "_significance";

	private static HashMap<String, String> niceProperties = new HashMap<String, String>();

	public void sortProperties(List<String> propertyOrder)
	{
		ListUtil.sort(properties, propertyOrder);
	}

	public String toNiceString()
	{
		return toNiceString(0, true);
	}

	public String toNiceString(int indent, boolean horizontalLine)
	{
		String whitespace = "";
		if (indent > 0)
			whitespace = StringUtil.whitespace(indent);

		int maxLength[] = new int[properties.size()];
		for (int i = 0; i < properties.size(); i++)
		{
			maxLength[i] = Math.max(maxLength[i], niceProperty(properties.get(i)).length());

			for (Result r : results)
				maxLength[i] = Math.max(maxLength[i], niceValue(r.getValue(properties.get(i)))
						.length());
		}

		String s = whitespace;
		for (int i = 0; i < properties.size(); i++)
		{
			if (i > 0)
				s += " | ";
			s += StringUtil.concatWhitespace(niceProperty(properties.get(i)), maxLength[i]);
		}
		s += "\n";

		if (horizontalLine)
		{
			s += whitespace;
			for (int i = 0; i < properties.size(); i++)
			{
				if (i > 0)
					s += "-|-";
				char[] line = new char[maxLength[i]];
				Arrays.fill(line, '-');
				s += new String(line);
			}
			s += "\n";
		}

		for (Result r : results)
		{
			s += whitespace;
			for (int i = 0; i < properties.size(); i++)
			{
				if (i > 0)
					s += " | ";
				s += niceValue(r.getValue(properties.get(i)), maxLength[i]);
			}
			s += "\n";
		}

		return s;
	}

	private static String niceProperty(String property)
	{
		if (property.endsWith(VARIANCE_SUFFIX))
			return "var";
		else if (niceProperties.containsKey(property))
			return niceProperties.get(property);
		else
			return property;
	}

	private String niceValue(Object value)
	{
		return niceValue(value, -1);
	}

	public void doubleToInt(String... prop)
	{
		for (String p : prop)
		{
			for (Result r : results)
			{
				if (r.getValue(p) != null)
				{
					if (!(r.getValue(p) instanceof Double))
						throw new IllegalArgumentException("no double " + p + " " + r.getValue(p));
					r.setValue(p, ((Double) r.getValue(p)).intValue());
				}
			}
		}
	}

	private String niceValue(Object value, int maxLength)
	{
		String s;
		boolean alignLeft = true;

		if (value == null)
			s = "null";
		else if (value instanceof Number[])
		{
			s = "";
			for (Number n : (Number[]) value)
				s += (s.length() == 0 ? "" : "; ")
						+ StringUtil.formatDouble((Double) n, numDecimalPlaces);
			// s += " ]";
			alignLeft = false;
		}
		else if (value instanceof Double)
		{
			s = StringUtil.formatDouble((Double) value, numDecimalPlaces);
			alignLeft = false;
		}
		else if (value instanceof Integer)
		{
			s = value.toString();
			alignLeft = false;
		}
		else
			s = value.toString();

		if (maxLength == -1)
			return s;
		else
			return StringUtil.concatWhitespace(s, maxLength, alignLeft);
	}

	public String toString()
	{
		return toString(false);
	}

	public String toString(boolean ommitPropertyString)
	{
		String s = "";
		if (!ommitPropertyString)
			s += getPropertyString() + "\n";
		for (Result r : results)
			s += r.toString(properties) + "\n";
		return s;
	}

	public String toMediaWikiString()
	{
		Locale l = Locale.getDefault();
		Locale.setDefault(new Locale("en"));

		String s = "{|cellpadding=\"3\" cellspacing=\"0\" border=\"1\"\n";
		for (String p : properties)
			s += "!" + p + "\n";
		s += "|-\n";

		for (Result r : results)
		{
			for (String p : properties)
			{
				s += "| " + niceValue(r.getValue(p)) + "\n";
			}
			s += "|-\n";
		}

		s += "|}\n<br>";

		Locale.setDefault(l);

		return s;
	}

	private String getPropertyString()
	{
		String s = null;
		for (String p : properties)
			if (s == null)
				s = p;
			else
				s += "," + p;
		return s;
	}

	public void sortResults(final String property)
	{
		sortResults(property, true, false, -1);
	}

	public void sortResults(final String property, final boolean ascending,
			final boolean numerical, final int classIndex)
	{
		if (!properties.contains(property))
			return;

		Collections.sort(results, new Comparator<Result>()
		{
			@Override
			public int compare(Result o1, Result o2)
			{
				// System.out.println("sorting according to " + property + " "
				// + o1.getValue(property).getClass() + ":  " + o1.getValue(property) + " "
				// + o1.getValueToString(property));

				if (numerical)
				{
					Number n1 = null;
					Number n2 = null;
					if (o1.getValue(property) instanceof Number[])
					{
						n1 = ((Number[]) o1.getValue(property))[classIndex];
						n2 = ((Number[]) o2.getValue(property))[classIndex];
					}
					else
					{
						try
						{
							n1 = ((Number) o1.getValue(property));
							n2 = ((Number) o2.getValue(property));
						}
						catch (ClassCastException e)
						{
							System.err.println(e.getMessage() + " : " + property + " -> "
									+ o1.getValue(property) + " / " + o2.getValue(property));
							e.printStackTrace();
							System.exit(1);
						}
					}
					double d1 = Double.NaN;
					if (n1 != null)
						d1 = n1.doubleValue();
					double d2 = Double.NaN;
					if (n2 != null)
						d2 = n2.doubleValue();
					if (Double.isNaN(d1))
						return 1;
					else if (Double.isNaN(d2))
						return -1;
					else if (d1 == d2)
						return 0;
					else if (d1 < d2)
					{
						if (ascending)
							return -1;
						else
							return 1;
					}
					else
					{
						if (ascending)
							return 1;
						else
							return -1;
					}
				}
				else
					return niceValue(o1.getValue(property)).compareTo(
							niceValue(o2.getValue(property)))
							* (ascending ? 1 : -1);
			}
		});
	}

	private int[] getGrouping(List<String> equalProperties)
	{
		int groupId = 0;
		int[] group = new int[results.size()];

		for (int i = 0; i < results.size(); i++)
		{
			int match = -1;
			for (int j = 0; j < i; j++)
			{
				if (results.get(i).equals(equalProperties, results.get(j)))
				{
					match = j;
					break;
				}
			}

			if (match != -1)
				group[i] = group[match];
			else
			{
				group[i] = groupId;
				groupId++;
			}
		}
		return group;
	}

	public ResultSet join(List<String> equalProperties, List<String> ommitProperties,
			List<String> varianceProperties)
	{
		return join(equalProperties, ommitProperties, varianceProperties, Result.JOIN_MODE_MEAN);
	}

	public ResultSet join(List<String> equalProperties, List<String> ommitProperties,
			List<String> varianceProperties, int joinMode)
	{
		ResultSet joined = new ResultSet();

		joined.properties = new ArrayList<String>();
		for (String p : properties)
			if (equalProperties.contains(p))
				joined.properties.add(p);
			else if (ommitProperties == null || !ommitProperties.contains(p))
			{
				joined.properties.add(p);
				if (varianceProperties != null && varianceProperties.contains(p))
					joined.properties.add(p + VARIANCE_SUFFIX);
			}

		int[] group = getGrouping(equalProperties);

		for (int i = 0; i < results.size(); i++)
		{
			if (joined.results.size() <= group[i])
				joined.results.add(results.get(i));
			else
				joined.results.set(group[i], joined.results.get(group[i]).join(results.get(i),
						joined.properties, equalProperties, varianceProperties, joinMode));
		}

		return joined;
	}

	public void concat(ResultSet set)
	{
		for (int j = 0; j < set.properties.size(); j++)
			if (!properties.contains(set.properties.get(j)))
				properties.add(set.properties.get(j));

		for (int i = 0; i < set.getNumResults(); i++)
			results.add(set.results.get(i));
	}

	// public boolean containsProperty(String property)
	// {
	// return properties.contains(property);
	// }
	//
	// public void addProperty(String property)
	// {
	// properties.add(property);
	// }

	public ResultSet merge(String mergeProperty, String thisMergePropValue, ResultSet set,
			String setMergePropValue)
	{
		for (String p : properties)
			if (!p.equals(mergeProperty) && !set.properties.contains(p))
				throw new Error("cannot merge result sets, inconsistent: " + p);
		for (String p : set.properties)
			if (!p.equals(mergeProperty) && !properties.contains(p))
				throw new Error("cannot merge result sets");
		for (Result r : results)
			if (r.isMergedResult())
				throw new Error("cannot merge result sets");
		for (Result r : set.results)
			if (r.isMergedResult())
				throw new Error("cannot merge result sets");

		if (!properties.contains(mergeProperty))
			for (int i = 0; i < results.size(); i++)
				setResultValue(i, mergeProperty, thisMergePropValue);
		if (!set.properties.contains(mergeProperty))
			for (int i = 0; i < set.results.size(); i++)
				set.setResultValue(i, mergeProperty, setMergePropValue);

		ResultSet newSet = new ResultSet();
		for (Result r : results)
		{
			int i = newSet.addResult();
			for (String p : properties)
				newSet.setResultValue(i, p, r.getValue(p));
		}
		for (Result r : set.results)
		{
			int i = newSet.addResult();
			for (String p : properties)
				newSet.setResultValue(i, p, r.getValue(p));
		}
		return newSet;
	}

	public ResultSet filter(ResultSetFilter filter)
	{
		ResultSet res = new ResultSet();

		res.properties = new ArrayList<String>();
		for (String p : properties)
			res.properties.add(p);

		for (Result r : results)
			if (filter.accept(r))
				res.results.add(r);

		return res;
	}

	public int addResult()
	{
		results.add(new Result());
		return results.size() - 1;
	}

	public void setResultValue(int index, String property, Object value)
	{
		if (!properties.contains(property))
			properties.add(property);
		results.get(index).setValue(property, value);
	}

	public Object getResultValue(int index, String property)
	{
		return results.get(index).getValue(property);
	}

	public int getNumResults()
	{
		return results.size();
	}

	public void removePropery(String property)
	{
		if (properties.contains(property))
		{
			for (Result r : results)
				r.remove(property);
			properties.remove(property);
		}
	}

	public void sortProperties(String[] sortedProps)
	{
		ListUtil.sort(properties, Arrays.asList(sortedProps));
	}

	public boolean hasProperty(String property)
	{
		return properties.contains(property);
	}

	public void setNicePropery(String property, String niceProperty)
	{
		niceProperties.put(property, niceProperty);
	}

	// static class PairedTTestResult
	// {
	// List<Object> comparePropertyValues;
	// List<List<Object>> inequalPropertyValues;
	//
	// HashMap<List<Object>, Double> values;
	//		
	// public PairedTTestResult(List<Object> comparePropertyValues, List<List<Object>> inequalPropertyValues)
	// {
	// this.comparePropertyValues = comparePropertyValues;
	// this.inequalPropertyValues = inequalPropertyValues;
	// values = new HashMap<List<Object>, Double>();
	// }
	//		
	// public void set(Object comparePropVal1, Object comparePropVal2, List<Object> inequalPopertyValues )
	// {
	// List<Object> key = new ArrayList<Object>();
	// }
	// }

	public ResultSet pairedTTest(String compareProperty, List<String> equalProperties,
			String testProperty, double confidence)
	{
		// HashMap<String, List<Object>> differentPropValues = new HashMap<String, List<Object>>();
		// for (int i = 0; i < results.size(); i++)
		// {
		// for (String p : differentProperties)
		// {
		// List<Object> values = differentPropValues.get(p);
		// if (p == null)
		// {
		// values = new ArrayList<Object>();
		// differentPropValues.put(p, values);
		// }
		// Object v = results.get(i).getValue(p);
		// if (!values.contains(v))
		// values.add(v);
		// }
		// }
		// int combinations = 1;
		// for (List<Object> values : differentPropValues.values())
		// combinations *= values.size();

		ResultSet result = new ResultSet();

		// get indices for each compare prop
		HashMap<Object, List<Integer>> indexMap = new HashMap<Object, List<Integer>>();
		for (int i = 0; i < results.size(); i++)
		{
			Object cmp = results.get(i).getValue(compareProperty);
			List<Integer> indices = indexMap.get(cmp);
			if (indices == null)
			{
				indices = new ArrayList<Integer>();
				indexMap.put(cmp, indices);
			}
			indices.add(i);
		}

		// check for equal number of indices
		int size = -1;
		for (List<Integer> indices : indexMap.values())
		{
			if (size == -1)
				size = indices.size();
			else if (size != indices.size())
				throw new IllegalStateException("illegal num results");
		}

		// check for equality, should be fine as long as the order of the results (equalProps, e.g. folds,seed) is correct
		int[] group = getGrouping(equalProperties);
		int groupForIndex[] = new int[size];
		Arrays.fill(groupForIndex, -1);

		for (Object p : indexMap.keySet())
		{
			List<Integer> indices = indexMap.get(p);
			for (int i = 0; i < groupForIndex.length; i++)
			{
				if (groupForIndex[i] == -1)
					groupForIndex[i] = group[indices.get(i)];
				else if (groupForIndex[i] != group[indices.get(i)])
					throw new IllegalStateException("grouping doesnt fit");
			}
		}

		// get values for inidices
		Object[] compareProps = new Object[indexMap.size()];
		HashMap<Object, double[]> valuesMap = new HashMap<Object, double[]>();
		HashMap<Object, Double> meansMap = new HashMap<Object, Double>();

		int count = 0;
		for (Object p : indexMap.keySet())
		{
			compareProps[count] = p;
			double[] values = new double[size];

			List<Integer> indices = indexMap.get(p);
			for (int i = 0; i < values.length; i++)
				values[i] = ((Double) results.get(indices.get(i)).getValue(testProperty))
						.doubleValue();

			valuesMap.put(p, values);
			meansMap.put(p, ArrayUtil.getMean(values));

			count++;
		}

		// calculate ttest value
		TTestImpl ttest = new TTestImpl();

		for (int i = 0; i < compareProps.length; i++)
		{
			for (int j = i + 1; j < compareProps.length; j++)
			{
				try
				{
					for (int k = 0; k < 2; k++)
					{
						double ttestValue = ttest.pairedTTest(valuesMap.get(compareProps[k == 0 ? i
								: j]), valuesMap.get(compareProps[k == 0 ? j : i]));

						int index = result.addResult();

						result.setResultValue(index, compareProperty + "_1",
								compareProps[k == 0 ? i : j]);
						result.setResultValue(index, compareProperty + "_2",
								compareProps[k == 0 ? j : i]);

						int test = 0;
						if (ttestValue <= confidence)
						{
							if (meansMap.get(compareProps[k == 0 ? i : j]) > meansMap
									.get(compareProps[k == 0 ? j : i]))
								test = 1;
							else
								test = -1;
						}

						result.setResultValue(index, testProperty + SIGNIFICANCE_SUFFIX, test);

						break;
					}
				}
				catch (IllegalArgumentException e)
				{
					e.printStackTrace();
				}
				catch (MathException e)
				{
					e.printStackTrace();
				}

			}
		}

		return result;

	}

	public ResultSet diff(String prop, List<String> equalProperties, List<String> ommitProperties)
	{
		if (equalProperties.contains(prop))
			throw new Error("not working");

		List<String> equalPlusProp = new ArrayList<String>();
		equalPlusProp.add(prop);
		for (String p : equalProperties)
			equalPlusProp.add(p);

		ResultSet joined = join(equalPlusProp, null, null);
		int[] group = joined.getGrouping(equalProperties);

		ResultSet diff = new ResultSet();

		for (int i = 0; i < group.length - 1; i++)
		{
			for (int j = i + 1; j < group.length; j++)
			{
				if (group[i] == group[j])
				{
					Result res1 = joined.results.get(i);
					Result res2 = joined.results.get(j);

					int x = diff.addResult();
					diff.setResultValue(x, prop, res1.getValue(prop) + " - " + res2.getValue(prop));
					for (String p : equalProperties)
						diff.setResultValue(x, p, res1.getValue(p));
					for (String p : properties)
					{
						if (equalPlusProp.contains(p)
								|| (ommitProperties != null && ommitProperties.contains(p)))
							continue;

						Object val1 = res1.getValue(p);
						Object val2 = res2.getValue(p);
						if (val1 instanceof Number)
						{
							double value = ((Number) val1).doubleValue()
									- ((Number) val2).doubleValue();
							diff.setResultValue(x, p, value);
						}
						else
							diff.setResultValue(x, p, "n/a");
					}
				}
			}
		}

		return diff;

	}

	/**
	 * use diff as input, >0 ist counted as win, <0 as loss
	 * 
	 * @param winLossProp
	 * @return
	 */
	public ResultSet winLoss(List<String> equalProperties, List<String> ommitProperties)
	{
		int[] group = this.getGrouping(equalProperties);
		ResultSet winLoss = new ResultSet();

		int groupId = 0;
		while (true)
		{
			int matchIndex = -1;

			boolean na[] = new boolean[properties.size()];
			int win[] = new int[properties.size()];
			int equal[] = new int[properties.size()];
			int loss[] = new int[properties.size()];

			for (int i = 0; i < group.length; i++)
			{
				if (group[i] == groupId)
				{
					matchIndex = i;

					for (int k = 0; k < properties.size(); k++)
					{
						String p = properties.get(k);
						if (equalProperties.contains(p)
								|| (ommitProperties != null && ommitProperties.contains(p)))
							continue;

						Object val = results.get(i).getValue(p);
						if (val instanceof Number)
						{
							double v = ((Number) val).doubleValue();

							if (v > 0)
								win[k]++;
							else if (v < 0)
								loss[k]++;
							else
								equal[k]++;
						}
						else
							na[k] = true;
					}
				}
			}

			if (matchIndex == -1)
				break;
			else
			{
				int x = winLoss.addResult();

				for (String p : equalProperties)
					winLoss.setResultValue(x, p, results.get(matchIndex).getValue(p));

				for (int k = 0; k < properties.size(); k++)
				{
					String p = properties.get(k);
					if (equalProperties.contains(p)
							|| (ommitProperties != null && ommitProperties.contains(p)))
						continue;

					if (na[k])
						continue;

					winLoss.setResultValue(x, p + "-W", win[k]);
					winLoss.setResultValue(x, p + "-EQ", equal[k]);
					winLoss.setResultValue(x, p + "-L", loss[k]);
				}
			}

			groupId++;
		}

		return winLoss;

	}

	public void setNumDecimalPlaces(int d)
	{
		numDecimalPlaces = d;
	}

	public static void main(String args[])
	{
		ResultSet set = new ResultSet();
		set.properties.add("features");
		set.properties.add("dataset");
		set.properties.add("algorithm");
		set.properties.add("fold");
		set.properties.add("accuracy");

		String features[] = new String[] { "fragments", "distance-pairs" };
		String datasets[] = new String[] { "mouse", "elephant", "crocodile" };
		String algorithms[] = new String[] { "C4.5", "SVM", "NB" };
		Random random = new Random();

		for (String feature : features)
		{
			for (String dataset : datasets)
			{
				for (String algorithm : algorithms)
				{
					for (int fold = 0; fold < 10; fold++)
					{
						Result r = new Result();
						r.setValue("features", feature);
						r.setValue("dataset", dataset);
						r.setValue("algorithm", algorithm);
						r.setValue("fold", fold);
						r.setValue("accuracy", random.nextDouble());
						set.results.add(r);
					}
				}
			}
		}

		System.out.println(set);
		System.out.println();

		List<String> equalProperties = new ArrayList<String>();
		equalProperties.add("features");
		equalProperties.add("dataset");
		equalProperties.add("algorithm");

		List<String> ommitProperties = new ArrayList<String>();
		ommitProperties.add("fold");

		ResultSet joined = set.join(equalProperties, ommitProperties, null);
		joined.sortResults("algorithm");
		joined.sortResults("dataset");
		System.out.println(joined.toNiceString());
		System.out.println();

		// --------------------

		List<String> equalProperties2 = new ArrayList<String>();
		equalProperties2.add("dataset");
		equalProperties2.add("algorithm");

		List<String> ommitProperties2 = new ArrayList<String>();
		ommitProperties2.add("fold");

		ResultSet diff = set.diff("features", equalProperties2, ommitProperties2);
		System.out.println(diff.toNiceString());
		System.out.println();

		// File temp = null;
		// try
		// {
		// temp = File.createTempFile("test", ".tmp");
		// }
		// catch (IOException e)
		// {
		// e.printStackTrace();
		// }
		// temp.deleteOnExit();
		// ResultSetIO.printToFile(temp, set);
		// ResultSet set2 = ResultSetIO.parseFromFile(temp);

		// System.out.println(set2);
		// System.out.println();
		//
		// ResultSet joined2 = set2.join(equalProperties, ommitProperties, null);
		// System.out.println(joined2.toNiceString());
		// System.out.println();

		// --------------------

		List<String> equalProperties3 = new ArrayList<String>();
		equalProperties3.add("features");
		equalProperties3.add("algorithm");

		ResultSet winLoss = diff.winLoss(equalProperties3, null);
		System.out.println(winLoss.toNiceString());
		System.out.println();

	}

}