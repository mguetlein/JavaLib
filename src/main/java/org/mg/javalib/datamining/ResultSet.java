package org.mg.javalib.datamining;

import java.awt.Color;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.stat.inference.TTest;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.mg.javalib.freechart.BarPlotPanel;
import org.mg.javalib.util.ArrayUtil;
import org.mg.javalib.util.CountedSet;
import org.mg.javalib.util.DoubleUtil;
import org.mg.javalib.util.FileUtil;
import org.mg.javalib.util.ListUtil;
import org.mg.javalib.util.StringUtil;
import org.mg.javalib.util.TimeFormatUtil;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class ResultSet
{
	private int numDecimalPlaces = 2;

	private List<String> properties = new ArrayList<String>();

	private List<Result> results = new ArrayList<Result>();

	public static final String VARIANCE_SUFFIX = "_variance";

	public static final String SIGNIFICANCE_SUFFIX = "_significance";

	private static HashMap<String, String> niceProperties = new HashMap<String, String>();

	public ResultSet copy()
	{
		return copy(null);
	}

	public ResultSet copy(Set<String> acceptAttributes)
	{
		ResultSet rs = new ResultSet();
		for (String p : properties)
			if (acceptAttributes == null || acceptAttributes.contains(p))
				rs.properties.add(p);
		for (Result r : results)
			rs.results.add(r.copy(acceptAttributes));
		return rs;
	}

	public void toLong(String p)
	{
		if (!properties.contains(p))
			return;
		for (int i = 0; i < results.size(); i++)
		{
			if (getResultValue(i, p) != null)
			{
				Double d = DoubleUtil.parseDouble(getResultValue(i, p).toString());
				setResultValue(i, p, d == null ? null : d.longValue());
			}
		}
	}

	public void toInt(String p)
	{
		if (!properties.contains(p))
			return;
		for (int i = 0; i < results.size(); i++)
		{
			Double d = DoubleUtil.parseDouble(getResultValue(i, p).toString());
			setResultValue(i, p, d == null ? null : d.intValue());
		}
	}

	public void sortProperties(List<String> propertyOrder)
	{
		ListUtil.sort(properties, propertyOrder);
	}

	public void movePropertyBack(String string)
	{
		if (properties.indexOf(string) != -1)
		{
			properties.remove(string);
			properties.add(string);
		}
	}

	public List<String> getProperties()
	{
		return properties;
	}

	public String toNiceString()
	{
		return toNiceString(0, true);
	}

	public String toNiceString(int indent, boolean horizontalLine)
	{
		return toNiceString(indent, horizontalLine, false);
	}

	public String toNiceString(int indent, boolean horizontalLine, boolean longToTime)
	{
		String whitespace = "";
		if (indent > 0)
			whitespace = StringUtil.whitespace(indent);

		int maxLength[] = new int[properties.size()];
		for (int i = 0; i < properties.size(); i++)
		{
			maxLength[i] = Math.max(maxLength[i], getNiceProperty(properties.get(i)).length());

			for (Result r : results)
				maxLength[i] = Math.max(maxLength[i], niceValue(r.getValue(properties.get(i)), -1, true).length());
		}

		StringBuffer s = new StringBuffer();
		s.append(whitespace);
		for (int i = 0; i < properties.size(); i++)
		{
			if (i > 0)
				s.append(" | ");
			s.append(StringUtil.concatWhitespace(getNiceProperty(properties.get(i)), maxLength[i]));
		}
		s.append("\n");

		if (horizontalLine)
		{
			s.append(whitespace);
			for (int i = 0; i < properties.size(); i++)
			{
				if (i > 0)
					s.append("-|-");
				char[] line = new char[maxLength[i]];
				Arrays.fill(line, '-');
				s.append(new String(line));
			}
			s.append("\n");
		}

		for (Result r : results)
		{
			s.append(whitespace);
			for (int i = 0; i < properties.size(); i++)
			{
				if (i > 0)
					s.append(" | ");
				s.append(niceValue(r.getValue(properties.get(i)), maxLength[i], longToTime));
			}
			s.append("\n");
		}

		return s.toString();
	}

	public String getNiceProperty(String property)
	{
		if (property.endsWith(VARIANCE_SUFFIX))
			return "var";
		else if (niceProperties.containsKey(property))
			return niceProperties.get(property);
		else
			return property;
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

	private String niceValue(Object value)
	{
		return niceValue(value, -1);
	}

	private String niceValue(Object value, int maxLength)
	{
		return niceValue(value, maxLength, false);
	}

	private String niceValue(Object value, int maxLength, boolean longToTime)
	{
		String s;
		boolean alignLeft = true;

		if (value == null)
			s = "null";
		else if (value instanceof Number[])
		{
			s = "";
			for (Number n : (Number[]) value)
				s += (s.length() == 0 ? "" : "; ") + StringUtil.formatDouble((Double) n, numDecimalPlaces);
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
		else if (value instanceof Long && longToTime)
		{
			s = TimeFormatUtil.format((Long) value);
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
		try
		{
			StringWriter sw = new StringWriter();
			CsvWriter writer = new CsvWriter(sw, ',');
			if (!ommitPropertyString)
				writer.writeRecord(ArrayUtil.toArray(String.class, properties));
			for (Result r : results)
			{
				String s[] = new String[properties.size()];
				for (int i = 0; i < s.length; i++)
					s[i] = r.getValueToString(properties.get(i));
				writer.writeRecord(s);
			}
			return sw.toString();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static ResultSet fromCSV(String absolutePath)
	{
		return fromString(FileUtil.readStringFromFile(absolutePath));
	}

	public static ResultSet fromString(String csv)
	{
		System.err.println("reading " + csv);
		try
		{
			CsvReader content = new CsvReader(new StringReader(csv));
			ResultSet set = new ResultSet();
			List<String> properties = new ArrayList<String>();
			content.readHeaders();
			for (String p : content.getHeaders())
				properties.add(p);
			while (content.readRecord())
			{
				int index = set.addResult();
				int count = 0;
				for (String val : content.getValues())
				{
					Double nVal = null;
					try
					{
						nVal = Double.parseDouble(val);
					}
					catch (NumberFormatException e)
					{
					}
					set.setResultValue(index, properties.get(count), nVal != null ? nVal : val);
					count++;
				}
			}
			return set;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public String toMediaWikiString()
	{
		return toMediaWikiString(true, false);
	}

	public String toMediaWikiString(boolean niceProps, boolean longToTime)
	{
		return toMediaWikiString(true, false, false);
	}

	public String toMediaWikiString(boolean niceProps, boolean longToTime, boolean renderTime)
	{
		Locale l = Locale.getDefault();
		Locale.setDefault(new Locale("en"));

		String s = "{|cellpadding=\"3\" cellspacing=\"0\" border=\"1\"\n";
		for (String p : properties)
			s += "!" + getNiceProperty(p) + "\n";
		s += "|-\n";

		for (Result r : results)
		{
			for (String p : properties)
			{
				if (renderTime && r.getValue(p) instanceof Long)
				{
					double seconds = ((Long) r.getValue(p)).longValue() / 1000.0;
					String style = "";
					if (seconds <= 60)
						style = "style=\"background-color:rgb(122,255,122);\"| ";
					else if (seconds >= 60 * 5)
						style = "style=\"background-color:rgb(255,122,122);\"| ";
					else
						style = "style=\"background-color:rgb(255,255,122);\"| ";
					s += "| " + style + niceValue(r.getValue(p), -1, longToTime) + "\n";
				}
				else if (r.getValue(p) == null || r.getValue(p).toString().equals("null"))
				{
					String style = "style=\"background-color:rgb(255,122,122);\"| ";
					s += "| " + style + "''not tested''" + "\n";
					//s += "| -\n";
				}
				else
					s += "| " + niceValue(r.getValue(p), -1, longToTime) + "\n";
			}
			s += "|-\n";
		}

		s += "|}\n<br>";

		Locale.setDefault(l);

		return s;
	}

	public String toHtmlTable()
	{
		String s = "<table><tr>";
		for (String p : properties)
			s += "<th>" + getNiceProperty(p) + "</th>";
		s += "</tr>";
		for (Result r : results)
		{
			s += "<tr>";
			for (String p : properties)
				s += "<td>" + niceValue(r.getValue(p), -1) + "</td>";
			s += "</tr>";
		}
		s += "</table>";
		return s;
	}

	public void sortResults(final String property, final Comparator<Object> comp)
	{
		Collections.sort(results, new Comparator<Result>()
		{
			@Override
			public int compare(Result o1, Result o2)
			{
				return comp.compare(o1.getValue(property), o2.getValue(property));
			}
		});
	}

	public void sortResults(final String property)
	{
		sortResults(property, true, false, -1);
	}

	public void sortResults(final String property, final boolean ascending, final boolean numerical,
			final int classIndex)
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
							System.err.println(e.getMessage() + " : " + property + " -> " + o1.getValue(property)
									+ " / " + o2.getValue(property));
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
					return niceValue(o1.getValue(property)).compareTo(niceValue(o2.getValue(property)))
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

	public ResultSet join(String equalProperty)
	{
		return join(equalProperty, Result.JOIN_MODE_MEAN);
	}

	public ResultSet join(String equalProperty, int joinMode)
	{
		return join(ArrayUtil.toList(new String[] { equalProperty }), null, null, joinMode);
	}

	public ResultSet join(String equalProperties[], String ommitProperties[], String varianceProperties[])
	{
		return join(ArrayUtil.toList(equalProperties),
				ommitProperties == null ? null : ArrayUtil.toList(ommitProperties), varianceProperties == null ? null
						: ArrayUtil.toList(varianceProperties));
	}

	public ResultSet join(List<String> equalProperties, List<String> ommitProperties, List<String> varianceProperties)
	{
		return join(equalProperties, ommitProperties, varianceProperties, Result.JOIN_MODE_MEAN);
	}

	public ResultSet join(List<String> equalProperties, List<String> ommitProperties, List<String> varianceProperties,
			int joinMode)
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
				joined.results.set(
						group[i],
						joined.results.get(group[i]).join(results.get(i), joined.properties, equalProperties,
								varianceProperties, joinMode));
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

	public void concatCols(ResultSet set)
	{
		if (getNumResults() != set.getNumResults())
			throw new IllegalArgumentException("num results not equal");
		for (String p : set.getProperties())
			for (int i = 0; i < set.getNumResults(); i++)
				setResultValue(i, p, set.getResultValue(i, p));
	}

	public ResultSet merge(String mergeProperty, String thisMergePropValue, ResultSet set, String setMergePropValue)
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

	public void excludeProperties(List<String> list)
	{
		List<String> toDel = new ArrayList<String>();
		for (String p : properties)
		{
			if (!list.contains(p))
				toDel.add(p);
		}
		for (String p : toDel)
			removePropery(p);
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

	public ResultSet pairedTTestWinLoss(String compareProperty, List<String> equalProperties, String testProperty,
			double confidence, final String seriesProperty)
	{
		ResultSet ttest = pairedTTest(compareProperty, equalProperties, testProperty, confidence, seriesProperty);
		for (int i = 0; i < ttest.getNumResults(); i++)
			ttest.setResultValue(i, testProperty + SIGNIFICANCE_SUFFIX,
					ttest.getResultValue(i, testProperty + SIGNIFICANCE_SUFFIX) + "");
		ttest = ttest.join(new String[] { compareProperty + "_1", compareProperty + "_2" }, null, null);
		ResultSet r = new ResultSet();
		for (int i = 0; i < ttest.getNumResults(); i++)
		{
			int idx = r.addResult();
			r.setResultValue(idx, compareProperty + "_1", ttest.getResultValue(idx, compareProperty + "_1"));
			r.setResultValue(idx, compareProperty + "_2", ttest.getResultValue(idx, compareProperty + "_2"));
			int win = 0, loss = 0, draw = 0;
			for (String s : ttest.getResultValue(idx, testProperty + SIGNIFICANCE_SUFFIX).toString().split("/"))
			{
				if (s.equals("-1"))
					loss++;
				else if (s.equals("1"))
					win++;
				else if (s.equals("0"))
					draw++;
				else
					throw new IllegalStateException();
			}
			r.setResultValue(idx, testProperty + "_win", win);
			r.setResultValue(idx, testProperty + "_draw", draw);
			r.setResultValue(idx, testProperty + "_loss", loss);
		}
		return r;
	}

	public ResultSet pairedTTest(String compareProperty, List<String> equalProperties, String testProperty,
			double confidence, final String seriesProperty)
	{
		ResultSet res = null;
		for (final Object series : getResultValues(seriesProperty).values())
		{
			ResultSet r = filter(new ResultSetFilter()
			{
				@Override
				public boolean accept(Result result)
				{
					return result.getValue(seriesProperty).equals(series);
				}
			});
			ResultSet test = r.pairedTTest_All(compareProperty, equalProperties, testProperty, confidence);
			for (int i = 0; i < test.getNumResults(); i++)
				test.setResultValue(i, seriesProperty, series);
			if (res == null)
				res = test;
			else
				res.concat(test);
		}
		return res;
	}

	public ResultSet pairedTTest_All(String compareProperty, List<String> equalProperties, String testProperty,
			double confidence)
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
		LinkedHashMap<Object, List<Integer>> indexMap = new LinkedHashMap<Object, List<Integer>>();
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
		LinkedHashMap<Object, double[]> valuesMap = new LinkedHashMap<Object, double[]>();
		LinkedHashMap<Object, Double> meansMap = new LinkedHashMap<Object, Double>();

		int count = 0;
		for (Object p : indexMap.keySet())
		{
			compareProps[count] = p;
			double[] values = new double[size];

			List<Integer> indices = indexMap.get(p);
			for (int i = 0; i < values.length; i++)
				values[i] = ((Double) results.get(indices.get(i)).getValue(testProperty)).doubleValue();

			valuesMap.put(p, values);
			meansMap.put(p, ArrayUtil.getMean(values));

			count++;
		}

		// calculate ttest value
		TTest ttest = new TTest();

		for (int i = 0; i < compareProps.length; i++)
		{
			for (int j = i + 1; j < compareProps.length; j++)
			{
				try
				{
					int k = 0;
					//					for (int k = 0; k < 2; k++)
					//					{
					double ttestValue = ttest.pairedTTest(valuesMap.get(compareProps[k == 0 ? i : j]),
							valuesMap.get(compareProps[k == 0 ? j : i]));

					int index = result.addResult();

					result.setResultValue(index, compareProperty + "_1", compareProps[k == 0 ? i : j]);
					result.setResultValue(index, compareProperty + "_2", compareProps[k == 0 ? j : i]);

					int test = 0;
					if (ttestValue <= confidence)
					{
						if (meansMap.get(compareProps[k == 0 ? i : j]) > meansMap.get(compareProps[k == 0 ? j : i]))
							test = 1;
						else
							test = -1;
					}
					result.setResultValue(index, testProperty + SIGNIFICANCE_SUFFIX, test);

					result.setResultValue(index, "num pairs", valuesMap.get(compareProps[k == 0 ? i : j]).length);
					//						break;
					//					}
				}
				catch (IllegalArgumentException e)
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
						if (equalPlusProp.contains(p) || (ommitProperties != null && ommitProperties.contains(p)))
							continue;

						Object val1 = res1.getValue(p);
						Object val2 = res2.getValue(p);
						if (val1 instanceof Number)
						{
							double value = ((Number) val1).doubleValue() - ((Number) val2).doubleValue();
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
						if (equalProperties.contains(p) || (ommitProperties != null && ommitProperties.contains(p)))
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
					if (equalProperties.contains(p) || (ommitProperties != null && ommitProperties.contains(p)))
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

	public static ResultSet dummySet()
	{
		ResultSet set = new ResultSet();

		String features[] = new String[] { "fragments", "descriptor,s" };
		String datasets[] = new String[] { "mouse", "elephant", "crocodile()", "dog", "cat" };
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
						int idx = set.addResult();
						set.setResultValue(idx, "features", feature);
						set.setResultValue(idx, "dataset", dataset);
						set.setResultValue(idx, "algorithm", algorithm);
						set.setResultValue(idx, "fold", fold);

						double acc = 0.4 + 0.2 * random.nextDouble();
						// mouse works better, elephant works worse
						if (dataset.equals("mouse"))
							acc += 0.1 + 0.1 * random.nextDouble();
						else if (dataset.equals("elephant"))
							acc -= 0.1 + 0.2 * random.nextDouble();
						// SVM is slightly better
						if (algorithm.equals("SVM"))
							acc += 0.02 + 0.02 * random.nextDouble();
						set.setResultValue(idx, "accuracy", acc);
					}
				}
			}
		}
		return set;
	}

	private static void noVarianceTest()
	{
		ResultSet rs = new ResultSet();
		for (int i = 0; i < 2; i++)
		{
			int idx = rs.addResult();
			rs.setResultValue(idx, "odd", i % 2 == 0);
			rs.setResultValue(idx, "val", new Random().nextDouble());
			rs.setResultValue(idx, "name", StringUtil.randomString());
		}

		System.out.println(rs.toNiceString());
		rs = rs.join(new String[] { "odd" }, new String[0], new String[] { "val" });
		System.out.println(rs.getResultValue(0, "val" + VARIANCE_SUFFIX));
		Double d = (Double) null;
		rs.getVariance(0, "val");
		System.out.println(rs.toNiceString());
	}

	public static void demo()
	{
		ResultSet set = dummySet();

		System.out.println("\ncomplete dataset\n");
		System.out.println(set.toNiceString());

		{
			// JOIN

			List<String> equalProperties = new ArrayList<String>();
			equalProperties.add("features");
			equalProperties.add("dataset");
			equalProperties.add("algorithm");

			List<String> ommitProperties = new ArrayList<String>();
			ommitProperties.add("fold");

			List<String> varProperties = new ArrayList<String>();
			varProperties.add("accuracy");

			ResultSet joined = set.join(equalProperties, ommitProperties, varProperties);
			System.out.println("\njoined folds\n");
			System.out.println(joined.toNiceString());

			// SORT

			joined.sortProperties(new String[] { "dataset" });
			joined.sortResults("dataset");
			System.out.println("\nsorted\n");
			System.out.println(joined.toNiceString());
		}

		{
			// T-TEST

			List<String> equalProperties = new ArrayList<String>();
			equalProperties.add("features");
			equalProperties.add("fold");

			ResultSet tested = set.pairedTTest("algorithm", equalProperties, "accuracy", 0.01, "dataset");
			System.out.println("\npaired t-test with 0.01 (should not detect that svm is best)\n");
			System.out.println(tested.toNiceString());

			tested = set.pairedTTest("algorithm", equalProperties, "accuracy", 0.02, "dataset");
			System.out.println("\npaired t-test with 0.02\n");
			System.out.println(tested.toNiceString());

			tested = set.pairedTTest("algorithm", equalProperties, "accuracy", 0.05, "dataset");
			System.out.println("\npaired t-test with 0.05\n");
			System.out.println(tested.toNiceString());

			tested = set.pairedTTest("algorithm", equalProperties, "accuracy", 0.15, "dataset");
			System.out.println("\npaired t-test with 0.15  (should detect that svm is best)\n");
			System.out.println(tested.toNiceString());

			tested = set.pairedTTest("algorithm", equalProperties, "accuracy", 1, "dataset");
			System.out.println("\npaired t-test with 1 (compares mean)\n");
			System.out.println(tested.toNiceString());

			tested = set.pairedTTestWinLoss("algorithm", equalProperties, "accuracy", 1, "dataset");
			System.out.println("\npaired t-test win loss\n");
			System.out.println(tested.toNiceString());
			System.exit(1);
		}

		{
			// DIFF

			List<String> equalProperties = new ArrayList<String>();
			equalProperties.add("dataset");
			equalProperties.add("algorithm");

			List<String> ommitProperties = new ArrayList<String>();
			ommitProperties.add("fold");

			ResultSet diff = set.diff("features", equalProperties, ommitProperties);
			System.out.println("\ndiff\n");
			System.out.println(diff.toNiceString());

			// WIN-LOSS

			List<String> equalPropertiesDiff = new ArrayList<String>();
			equalPropertiesDiff.add("features");
			equalPropertiesDiff.add("algorithm");

			ResultSet winLoss = diff.winLoss(equalPropertiesDiff, null);
			System.out.println("\nwin loss (uses diff as input)\n");
			System.out.println(winLoss.toNiceString());
		}

	}

	public static void main(String args[])
	{
		demo();
		//noVarianceTest();

		//		ResultSet set1 = new ResultSet();
		//		String datasetName = "Dataset";
		//		String openBabel3D = "OpenBabel 3D";
		//		set1.properties.add(datasetName);
		//		set1.properties.add(openBabel3D);
		//		int index = set1.addResult();
		//		set1.setResultValue(index, datasetName, "dataset1");
		//		set1.setResultValue(index, openBabel3D, 3.0);
		//
		//		ResultSet set2 = new ResultSet();
		//		String cdk3D = "CDK 3D";
		//		set2.properties.add(datasetName);
		//		set2.properties.add(cdk3D);
		//		index = set2.addResult();
		//		set2.setResultValue(index, datasetName, "dataset1");
		//		set2.setResultValue(index, cdk3D, 2.0);
		//
		//		System.out.println(set1.toNiceString());
		//		System.out.println(set2.toNiceString());
		//
		//		set1.concat(set2);
		//		System.out.println(set1.toNiceString());
		//
		//		System.exit(0);

	}

	public void remove(String prop, Object val)
	{
		List<Result> rem = new ArrayList<Result>();
		for (Result res : results)
			if (res.getValue(prop).equals(val))
				rem.add(res);
		for (Result res : rem)
			results.remove(res);
	}

	public void exclude(String prop, Object val)
	{
		List<Result> rem = new ArrayList<Result>();
		for (Result res : results)
			if (!res.getValue(prop).equals(val))
				rem.add(res);
		for (Result res : rem)
			results.remove(res);
	}

	public ChartPanel barPlot(String title, String yAxis, String seriesProperty, List<String> categoryProperties,
			double[] range, Color[] cols)
	{
		List<String> seriesNames = new ArrayList<String>();
		ArrayList<?>[] values = new ArrayList<?>[getNumResults()];

		for (int i = 0; i < getNumResults(); i++)
		{
			seriesNames.add(getResultValue(i, seriesProperty).toString());
			ArrayList<Double> list = new ArrayList<Double>();
			for (String string : categoryProperties)
				list.add(Double.parseDouble(getResultValue(i, string).toString()));
			values[i] = list;
		}

		@SuppressWarnings("unchecked")
		BarPlotPanel p = new BarPlotPanel(title, yAxis, categoryProperties, seriesNames, true,
				(ArrayList<Double>[]) values);

		CategoryPlot categoryPlot = p.getChartPanel().getChart().getCategoryPlot();
		if (range != null)
		{
			NumberAxis rangeAxis = (NumberAxis) categoryPlot.getRangeAxis();
			rangeAxis.setRange(range[0], range[1]);
		}

		//		CategoryAxis domainAxis = categoryPlot.getDomainAxis();
		//		domainAxis.setCategoryMargin(0.4f);

		BarRenderer br = (BarRenderer) categoryPlot.getRenderer();
		br.setItemMargin(0.0f);

		if (cols != null)
			for (int i = 0; i < cols.length; i++)
				br.setSeriesPaint(i, cols[i]);

		return p.getChartPanel();
	}

	public CountedSet<Object> getResultValues(String prop)
	{
		CountedSet<Object> o = new CountedSet<Object>();
		for (Result r : results)
			o.add(r.getValue(prop));
		return o;
	}

	public Object getUniqueValue(String prop)
	{
		CountedSet<Object> o = getResultValues(prop);
		if (o.getNumValues() != 1)
			throw new IllegalStateException("'" + prop + "' not unique: " + o);
		return o.values().get(0);
	}

	public double getVariance(String prop)
	{
		if (!hasProperty(prop + VARIANCE_SUFFIX))
			throw new Error("no variance available for " + prop);
		Double d = (Double) getUniqueValue(prop + VARIANCE_SUFFIX);
		if (d == null)
			return Double.NaN;
		else
			return d;
	}

	public double getVariance(int resultIdx, String prop)
	{
		if (!hasProperty(prop + VARIANCE_SUFFIX))
			throw new Error("no variance available for " + prop);
		Double d = (Double) getResultValue(resultIdx, prop + VARIANCE_SUFFIX);
		if (d == null)
			return Double.NaN;
		else
			return d;
	}

	public static ResultSet build(List<Object[]> values)
	{
		ResultSet set = new ResultSet();
		String[] p = ArrayUtil.toStringArray(values.get(0));
		for (int i = 1; i < values.size(); i++)
		{
			Object[] v = values.get(i);
			if (p.length != v.length)
				throw new IllegalArgumentException(ArrayUtil.toString(p) + " does not fit as properties for "
						+ ArrayUtil.toString(v));
			int x = set.addResult();
			for (int j = 0; j < v.length; j++)
				set.setResultValue(x, p[j], v[j]);
		}
		return set;
	}

	public void clearMergeCountAndVariance()
	{
		List<String> todel = new ArrayList<String>();
		for (String p : properties)
			if (p.endsWith(VARIANCE_SUFFIX))
				todel.add(p);
		for (String p : todel)
			removePropery(p);
		for (Result res : results)
			res.clearMergeCount();

	}

	public ResultSet mergeVariance()
	{
		ResultSet rs = new ResultSet();
		for (int i = 0; i < results.size(); i++)
		{
			rs.addResult();
			for (String p : properties)
			{
				if (p.endsWith(VARIANCE_SUFFIX))
					continue;
				if (properties.contains(p + VARIANCE_SUFFIX))
					rs.setResultValue(i, p,
							niceValue(getResultValue(i, p)) + "+-" + niceValue(getResultValue(i, p + VARIANCE_SUFFIX)));
				else
					rs.setResultValue(i, p, getResultValue(i, p));
			}
		}
		return rs;
	}

	public Object[][] toNiceArray()
	{
		Object array[][] = new Object[results.size() + 1][properties.size()];
		int row = 0;
		for (int i = 0; i < properties.size(); i++)
			array[row][i] = getNiceProperty(properties.get(i));
		row++;
		for (Result result : results)
		{
			for (int i = 0; i < properties.size(); i++)
				array[row][i] = niceValue(result.getValue(properties.get(i)));
			row++;
		}
		return array;
	}

	public ResultSet translate()
	{
		ResultSet t = new ResultSet();
		for (String p : properties)
		{
			int idx = t.addResult();
			t.setResultValue(idx, "property", p);
			for (int i = 0; i < getNumResults(); i++)
				t.setResultValue(idx, "value" + (getNumResults() > 1 ? (i + 1) : ""), getResultValue(i, p));
		}
		return t;
	}

	public static ResultSet fromWekaDataset(Instances data)
	{
		ResultSet set = new ResultSet();
		for (Instance instance : data)
		{
			int rIdx = set.addResult();
			for (int a = 0; a < data.numAttributes(); a++)
			{
				Attribute att = data.attribute(a);
				if (att.isNumeric())
					set.setResultValue(rIdx, att.name(), instance.value(att));
				else
					set.setResultValue(rIdx, att.name(), instance.stringValue(att));
			}
		}
		return set;
	}

}
