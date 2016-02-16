package org.mg.javalib.datamining;

import java.awt.Color;
import java.io.IOException;
import java.io.Serializable;
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

import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
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
import org.mg.javalib.util.HashUtil;
import org.mg.javalib.util.ListUtil;
import org.mg.javalib.util.StringUtil;
import org.mg.javalib.util.TimeFormatUtil;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class ResultSet implements Serializable
{
	private static final long serialVersionUID = 1L;

	private int numDecimalPlaces = 2;

	protected List<String> properties = new ArrayList<String>();

	protected List<Result> results = new ArrayList<Result>();

	public static final String VARIANCE_SUFFIX = "_variance";

	public static final String SIGNIFICANCE_SUFFIX = "_significance";

	public static final String RANK_SUFFIX = "_rank";

	public static final String RANK_BEST_SUFFIX = "_best";

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
				maxLength[i] = Math.max(maxLength[i],
						niceValue(r.getValue(properties.get(i)), -1, true).length());
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
			s = "";
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
		//		System.err.println("reading " + csv);
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

	public String toLatexTable()
	{
		return toLatexTable(null, (Integer[]) null, null, false);
	}

	public String toLatexTable(Boolean[] centerColumn, Boolean[] hlineLeadingColumn,
			String preProperties)
	{
		Integer[] numLines = null;
		if (hlineLeadingColumn != null)
		{
			numLines = new Integer[hlineLeadingColumn.length];
			for (int i = 0; i < numLines.length; i++)
				numLines[i] = hlineLeadingColumn[i] ? 1 : 0;
		}
		return toLatexTable(centerColumn, numLines, preProperties, false);
	}

	public String toLatexTable(boolean renderTime)
	{
		return toLatexTable(null, (Integer[]) null, null, renderTime);
	}

	public String toLatexTable(Boolean[] centerColumn, Integer[] hlineLeadingColumn,
			String preProperties)
	{
		return toLatexTable(centerColumn, hlineLeadingColumn, preProperties, false);
	}

	public String toLatexTable(Boolean[] centerColumn, Integer[] hlineLeadingColumn,
			String preProperties, boolean renderTime)
	{
		return toLatexTable(new LatexTableSettings(centerColumn, hlineLeadingColumn, preProperties,
				renderTime));
	}

	public static class LatexTableSettings
	{
		public Boolean[] centerColumn;
		public Integer[] hlineLeadingColumn;
		public String preProperties;
		public boolean renderTime;
		public boolean headerBold;
		public boolean firstRowBold;

		public LatexTableSettings(Boolean[] centerColumn, Integer[] hlineLeadingColumn,
				String preProperties, boolean renderTime)
		{
			this.centerColumn = centerColumn;
			this.hlineLeadingColumn = hlineLeadingColumn;
			this.preProperties = preProperties;
			this.renderTime = renderTime;
		}

		public LatexTableSettings()
		{
		}
	}

	public String toLatexTable(LatexTableSettings settings)
	{
		StringBuffer s = new StringBuffer();
		s.append("\\centering\n");

		if (settings.renderTime)
		{
			s.append("\\arrayrulecolor{white}\n");
			s.append("\\setlength{\\arrayrulewidth}{1px}\n");
		}

		//		if (escapeUnderscore)
		//		{
		//			s.append("\\bgroup\n");
		//			s.append("\\catcode`\\_=13%\n");
		//			s.append("\\def_{\\textunderscore}%\n");
		//		}
		s.append("\\begin{tabular}{ ");
		if (settings.renderTime)
			s.append("@{\\extracolsep{1px}}");

		for (int i = 0; i < properties.size(); i++)
		{
			if (settings.hlineLeadingColumn != null)
			{
				for (int j = 0; j < settings.hlineLeadingColumn[i]; j++)
					s.append("| ");
			}
			if (settings.centerColumn != null && settings.centerColumn[i])
				s.append("c ");
			else
				s.append("l ");
		}
		s.append("}\n");
		if (settings.preProperties != null)
			s.append(settings.preProperties + "\n");
		for (int i = 0; i < properties.size(); i++)
		{
			boolean bold = settings.headerBold || (i == 0 && settings.firstRowBold);
			s.append(" ");
			if (bold)
				s.append("\\textbf{");
			s.append(getNiceProperty(properties.get(i)));
			if (bold)
				s.append("}");
			if (i < properties.size() - 1)
				s.append(" &");
		}
		s.append(" \\\\\n");
		s.append(" \\hline\n");
		for (Result r : results)
		{
			for (int i = 0; i < properties.size(); i++)
			{
				String p = properties.get(i);
				if (settings.renderTime && r.getValue(p) instanceof Long)
				{
					double seconds = ((Long) r.getValue(p)).longValue() / 1000.0;
					if (seconds < 0)
						s.append(" \\cellcolor{red}");
					else if (seconds <= 60)
						s.append(" \\cellcolor{green}");
					else if (seconds >= 60 * 5)
						s.append(" \\cellcolor{red}");
					else
						s.append(" \\cellcolor{yellow}");
					boolean bold = i == 0 && settings.firstRowBold;
					s.append(" ");
					if (bold)
						s.append("\\textbf{");
					s.append(niceValue(r.getValue(p), -1, true));
					if (bold)
						s.append("}");
					//s += "| " + style + niceValue(r.getValue(p), -1, longToTime) + "\n";
				}
				else
				{
					String v = niceValue(r.getValue(p), -1);
					if (v.equals("<"))
						v = "$<$";
					else if (v.equals(">"))
						v = "$>$";
					if (v.contains("+-"))
						v = "$" + v.replace("+-", "\\pm") + "$";
					if (v.contains(Character.toString((char) 0x2022)))
						v = v.replace(Character.toString((char) 0x2022), "\\bullet");
					if (v.contains(Character.toString((char) 0x25E6)))
						v = v.replace(Character.toString((char) 0x25E6), "\\circ");
					s.append(" ");
					boolean bold = i == 0 && settings.firstRowBold;
					if (bold)
						s.append("\\textbf{");
					s.append(v);
					if (bold)
						s.append("}");

				}
				if (i < properties.size() - 1)
					s.append(" &");
			}
			s.append(" \\\\\n");
			if (settings.renderTime)
				s.append("\\hline\n");
		}
		s.append("\\end{tabular}\n");
		//		if (escapeUnderscore)
		//			s.append("\\egroup\n");
		return s.toString();
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

	public void sortResults(String property)
	{
		sortResults(property, true, false, -1);
	}

	public void sortResults(String property, boolean ascending)
	{
		sortResults(property, ascending, false, -1);
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
					return niceValue(o1.getValue(property))
							.compareTo(niceValue(o2.getValue(property))) * (ascending ? 1 : -1);
			}
		});
	}

	private int[] getGrouping(List<String> equalProperties)
	{
		int[] group = new int[results.size()];

		HashMap<Integer, Integer> valCombinationToGroup = new HashMap<>();

		for (int i = 0; i < results.size(); i++)
		{
			Object[] vals = new Object[equalProperties.size()];
			for (int j = 0; j < vals.length; j++)
				vals[j] = getResultValue(i, equalProperties.get(j));
			int hashkey = HashUtil.hashCode(vals);
			if (!valCombinationToGroup.containsKey(hashkey))
				valCombinationToGroup.put(hashkey, valCombinationToGroup.size());
			group[i] = valCombinationToGroup.get(hashkey);

			//			int prevResultWithEqualValues = -1;
			//			for (int j = 0; j < i; j++)
			//			{
			//				if (results.get(i).equals(equalProperties, results.get(j)))
			//				{
			//					prevResultWithEqualValues = j;
			//					break;
			//				}
			//			}
			//
			//			if (prevResultWithEqualValues != -1)
			//				group[i] = group[prevResultWithEqualValues];
			//			else
			//			{
			//				group[i] = groupId;
			//				groupId++;
			//			}
		}

		//		for (int i = 0; i < results.size(); i++) // verify
		//			for (int j = 0; j < results.size(); j++)
		//				if (i != j && group[j] == group[i])
		//					if (!results.get(i).equals(equalProperties, results.get(j)))
		//						throw new IllegalStateException("not equal");

		return group;
	}

	private ResultSet filterGroup(int groupIdx, int grouping[])
	{
		ResultSet s = new ResultSet();
		s.properties = ListUtil.clone(properties);
		for (int i = 0; i < grouping.length; i++)
			if (grouping[i] == groupIdx)
				s.results.add(results.get(i));
		return s;
	}

	public ResultSet join(String equalProperty)
	{
		return join(equalProperty, Result.JOIN_MODE_MEAN);
	}

	public ResultSet join(String equalProperty, int joinMode)
	{
		return join(ArrayUtil.toList(new String[] { equalProperty }), null, null, joinMode);
	}

	public ResultSet join(String equalProperties[], String ommitProperties[],
			String varianceProperties[])
	{
		return join(ArrayUtil.toList(equalProperties),
				ommitProperties == null ? null : ArrayUtil.toList(ommitProperties),
				varianceProperties == null ? null : ArrayUtil.toList(varianceProperties));
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

	public void concatCols(ResultSet set)
	{
		if (getNumResults() != set.getNumResults())
			throw new IllegalArgumentException("num results not equal");
		for (String p : set.getProperties())
			for (int i = 0; i < set.getNumResults(); i++)
				setResultValue(i, p, set.getResultValue(i, p));
	}

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
				res.results.add(r.copy());

		return res;
	}

	public void filterLastResult(ResultSetFilter filter)
	{
		if (!filter.accept(results.get(results.size() - 1)))
			results.remove(results.size() - 1);
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

	/**
	 * if (correctTerm == null) -> normal un-corrected paired t-test
	 * in WEKA testTrainRatio is used as correctTerm (i.e., in ten-fold crossvalidation: 1/9.0)
	 */
	public ResultSet pairedTTestWinLoss(String compareProperty, String[] equalProperties,
			String testProperty, double confidence, Double correctTerm, String[] seriesProperty,
			boolean addNonSignificant)
	{
		return pairedTTestWinLoss(compareProperty, ArrayUtil.toList(equalProperties), testProperty,
				confidence, correctTerm, ArrayUtil.toList(seriesProperty), addNonSignificant);
	}

	public ResultSet pairedTTestWinLoss(String compareProperty, List<String> equalProperties,
			String testProperty, double confidence, Double correctTerm, List<String> seriesProperty,
			boolean addNonSignificant)
	{
		double confidences[] = new double[] { confidence };
		if (addNonSignificant)
			confidences = new double[] { 1.0, confidence };

		List<ResultSet> tests = new ArrayList<>();
		for (int c = 0; c < confidences.length; c++)
		{
			ResultSet ttest = pairedTTest(compareProperty, equalProperties, testProperty,
					confidences[c], correctTerm, seriesProperty);
			// 	convert to string to concat via / instead of compute mean when joining
			for (int i = 0; i < ttest.getNumResults(); i++)
				ttest.setResultValue(i, testProperty + SIGNIFICANCE_SUFFIX,
						ttest.getResultValue(i, testProperty + SIGNIFICANCE_SUFFIX) + "");

			ttest = ttest.join(new String[] { compareProperty + "_1", compareProperty + "_2" },
					null, null);
			tests.add(ttest);

			//			System.out.println(confidences[c] + " confidence\n" + ttest.toNiceString());
		}

		ResultSet r = new ResultSet();
		for (int i = 0; i < tests.get(0).getNumResults(); i++)
		{
			int idx = r.addResult();
			if (addNonSignificant && (!tests.get(0).getResultValue(idx, compareProperty + "_1")
					.equals(tests.get(1).getResultValue(idx, compareProperty + "_1"))
					|| (!tests.get(0).getResultValue(idx, compareProperty + "_2")
							.equals(tests.get(1).getResultValue(idx, compareProperty + "_2")))))
				throw new IllegalStateException();
			r.setResultValue(idx, compareProperty + "_1",
					tests.get(0).getResultValue(idx, compareProperty + "_1"));
			r.setResultValue(idx, compareProperty + "_2",
					tests.get(0).getResultValue(idx, compareProperty + "_2"));

			int win[] = new int[confidences.length];
			int loss[] = new int[confidences.length];
			int draw[] = new int[confidences.length];

			for (int c = 0; c < confidences.length; c++)
			{
				// count losses
				for (String s : tests.get(c).getResultValue(idx, testProperty + SIGNIFICANCE_SUFFIX)
						.toString().split("/"))
				{
					if (s.equals("-1"))
						loss[c]++;
					else if (s.equals("1"))
						win[c]++;
					else if (s.equals("0"))
						draw[c]++;
					else
						throw new IllegalStateException();
				}
			}
			String winStr = win[0] + "";
			String drawStr = (draw[0] > 0 && confidences[0] == 1.0) ? draw[0] + "" : "";
			String lossStr = loss[0] + "";
			if (addNonSignificant)
			{
				if (win[1] > 0)
					winStr += "(" + win[1] + ")";
				if (loss[1] > 0)
					lossStr += "(" + loss[1] + ")";
			}
			String winDrawLoss = winStr + "/" + lossStr;
			if (drawStr.length() > 0)
				winDrawLoss = winStr + "/" + drawStr + "/" + lossStr;
			r.setResultValue(idx, testProperty, winDrawLoss);
			//			r.setResultValue(idx, testProperty + "_win", win);
			//			r.setResultValue(idx, testProperty + "_draw", draw);
			//			r.setResultValue(idx, testProperty + "_loss", loss);
		}
		return r;
	}

	/**
	 * if (correctTerm == null) -> normal un-corrected paired t-test
	 * in WEKA testTrainRatio is used as correctTerm (i.e., in ten-fold crossvalidation: 1/9.0)
	 */
	public ResultSet pairedTTest(String compareProperty, String[] equalProperties,
			String testProperty, double confidence, Double correctTerm, String[] seriesProperty)
	{
		return pairedTTest(compareProperty, ArrayUtil.toList(equalProperties), testProperty,
				confidence, correctTerm, ArrayUtil.toList(seriesProperty));
	}

	public ResultSet pairedTTest(String compareProperty, List<String> equalProperties,
			String testProperty, double confidence, Double correctTerm, List<String> seriesProperty)
	{
		ResultSet res = null;

		int[] grp = getGrouping(seriesProperty);
		int grpIdx = 0;
		while (true)
		{
			ResultSet r = filterGroup(grpIdx, grp);
			if (r.getNumResults() == 0)
				break;
			ResultSet test = r.pairedTTest_All(compareProperty, equalProperties, testProperty,
					confidence, correctTerm);
			for (int i = 0; i < test.getNumResults(); i++)
				for (String p : seriesProperty)
					test.setResultValue(i, p, r.getResultValue(0, p));
			if (res == null)
				res = test;
			else
				res.concat(test);

			grpIdx++;
		}
		return res;
	}

	public static List<Object> listSeriesWins(ResultSet pairedTTestResult, String compareProperty,
			String testProperty, String seriesProperty, String compareProbValue1,
			String compareProbValue2)
	{
		List<Object> wins = new ArrayList<>();
		for (int i = 0; i < pairedTTestResult.getNumResults(); i++)
		{
			if (pairedTTestResult
					.getResultValue(i, compareProperty + "_1").equals(
							compareProbValue1)
					&& pairedTTestResult
							.getResultValue(i,
									compareProperty + "_2")
							.equals(compareProbValue2)
					&& pairedTTestResult
							.getResultValue(i, testProperty + ResultSet.SIGNIFICANCE_SUFFIX)
							.equals(1))
				wins.add(pairedTTestResult.getResultValue(i, seriesProperty));
			if (pairedTTestResult
					.getResultValue(i, compareProperty + "_2").equals(
							compareProbValue1)
					&& pairedTTestResult
							.getResultValue(i,
									compareProperty + "_1")
							.equals(compareProbValue2)
					&& pairedTTestResult
							.getResultValue(i, testProperty + ResultSet.SIGNIFICANCE_SUFFIX)
							.equals(-1))
				wins.add(pairedTTestResult.getResultValue(i, seriesProperty));
		}
		return wins;
	}

	public static Boolean isWinOrLoss(ResultSet pairedTTestResult, String compareProperty,
			String compareValue1, String compareValue2, String testProperty)
	{
		return isWinOrLoss(pairedTTestResult, compareProperty, compareValue1, compareValue2,
				testProperty, null, null);
	}

	public static Boolean isWinOrLoss(ResultSet pairedTTestResult, String compareProperty,
			String compareValue1, String compareValue2, String testProperty, String seriesProperty,
			String seriesValue)
	{
		for (int i = 0; i < pairedTTestResult.getNumResults(); i++)
		{
			if (seriesProperty == null
					|| pairedTTestResult.getResultValue(i, seriesProperty).equals(seriesValue))
			{
				Integer testResult = (Integer) pairedTTestResult.getResultValue(i,
						testProperty + ResultSet.SIGNIFICANCE_SUFFIX);
				String cmp1 = pairedTTestResult.getResultValue(i, compareProperty + "_1")
						.toString();
				String cmp2 = pairedTTestResult.getResultValue(i, compareProperty + "_2")
						.toString();
				if (cmp1.equals(compareValue1))
				{
					if (cmp2.equals(compareValue2))
					{
						if (testResult == 1)
							return true;
						else if (testResult == -1)
							return false;
						return null;
					}
				}
				else if (cmp1.equals(compareValue2))
				{
					if (cmp2.equals(compareValue1))
					{
						if (testResult == 1)
							return false;
						else if (testResult == -1)
							return true;
						return null;
					}
				}
			}
		}
		return null;
	}

	public static Boolean isWinOrLoss(ResultSet pairedTTestResult, String compareProperty,
			String compareValue, String testProperty)
	{
		return isWinOrLoss(pairedTTestResult, compareProperty, compareValue, testProperty, null,
				null);
	}

	/**
	 * true -> only wins or zero (at least one win)
	 * null -> only zero
	 * false -> at least one loss
	 */
	public static Boolean isWinOrLoss(ResultSet pairedTTestResult, String compareProperty,
			String compareValue, String testProperty, String seriesProperty, String seriesValue)
	{
		Boolean win = null;
		for (int i = 0; i < pairedTTestResult.getNumResults(); i++)
		{
			if (seriesProperty == null
					|| pairedTTestResult.getResultValue(i, seriesProperty).equals(seriesValue))
			{
				Integer testResult = (Integer) pairedTTestResult.getResultValue(i,
						testProperty + ResultSet.SIGNIFICANCE_SUFFIX);
				String cmp1 = pairedTTestResult.getResultValue(i, compareProperty + "_1")
						.toString();
				String cmp2 = pairedTTestResult.getResultValue(i, compareProperty + "_2")
						.toString();
				if (cmp1.equals(compareValue))
				{
					if (testResult == -1)
						return false;
					else if (testResult == 1)
						win = true;
				}
				else if (cmp2.equals(compareValue))
				{
					if (testResult == 1)
						return false;
					else if (testResult == -1)
						win = true;
				}
			}
		}
		return win;
	}

	/**
	 * if (correctTerm == null) -> normal un-corrected paired t-test
	 * in WEKA testTrainRatio is used as correctTerm (i.e., in ten-fold crossvalidation 1/9.0)
	 */
	public ResultSet pairedTTest_All(String compareProperty, List<String> equalProperties,
			String testProperty, double confidence, Double correctTerm)
	{
		//		System.err.println("ttest all " + testProperty + " for " + compareProperty);

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
		Object val = null;
		int size = -1;
		for (Object k : indexMap.keySet())
		{
			List<Integer> indices = indexMap.get(k);
			if (size == -1)
			{
				val = k;
				size = indices.size();
			}
			else if (size != indices.size())
			{
				System.err.println(toNiceString());
				throw new IllegalStateException("illegal num results " + val + ":" + size + " != "
						+ k + ":" + indices.size());
			}
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
				{
					System.err.println(toNiceString());
					throw new IllegalStateException("grouping doesnt fit");
				}
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
				values[i] = ((Double) results.get(indices.get(i)).getValue(testProperty))
						.doubleValue();

			valuesMap.put(p, values);
			meansMap.put(p, ArrayUtil.getMean(values));

			count++;
		}

		// calculate ttest value
		//		TTest ttest = new TTest();

		for (int i = 0; i < compareProps.length; i++)
		{
			for (int j = i + 1; j < compareProps.length; j++)
			{
				try
				{
					int k = 0;
					//					for (int k = 0; k < 2; k++)
					//					{

					//					int idx1 = k == 0 ? i : j;
					//					int idx2 = k == 0 ? j : i;
					//					System.err.println("Comparing: " + compareProps[idx1] + " to " + compareProps[idx2]);
					//					System.err.println(ArrayUtil.toString(
					//							ArrayUtil.toDoubleArray(valuesMap.get(compareProps[k == 0 ? i : j])), ",", "(", ")"));
					//					System.err.println(ArrayUtil.toString(
					//							ArrayUtil.toDoubleArray(valuesMap.get(compareProps[k == 0 ? j : i])), ",", "(", ")"));

					//					double ttestValue = ttest.pairedTTest(valuesMap.get(compareProps[k == 0 ? i : j]),
					//							valuesMap.get(compareProps[k == 0 ? j : i]));
					//					//					System.err.println(ttestValue);

					int index = result.addResult();
					result.setResultValue(index, compareProperty + "_1",
							compareProps[k == 0 ? i : j]);
					result.setResultValue(index, compareProperty + "_2",
							compareProps[k == 0 ? j : i]);
					int test = 0;
					//					if (ttestValue <= (0.5 * confidence)) // one tailed test -> divide by half
					//					{
					//						if (meansMap.get(compareProps[k == 0 ? i : j]) > meansMap.get(compareProps[k == 0 ? j : i]))
					//							test = 1;
					//						else
					//							test = -1;
					//					}

					double v1[] = valuesMap.get(compareProps[k == 0 ? i : j]);
					double v2[] = valuesMap.get(compareProps[k == 0 ? j : i]);
					double m1 = meansMap.get(compareProps[k == 0 ? i : j]);
					double m2 = meansMap.get(compareProps[k == 0 ? j : i]);

					//System.out.println("num values for t-test: " + v1.length);

					test = T_TESTER.ttest(v1, v2, m1, m2, confidence, correctTerm);

					result.setResultValue(index, testProperty + SIGNIFICANCE_SUFFIX, test);
					result.setResultValue(index, "num pairs",
							valuesMap.get(compareProps[k == 0 ? i : j]).length);
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

	public static interface TTester
	{
		public int ttest(double v1[], double v2[], double mean1, double mean2, double confidence,
				Double correctTerm);
	}

	public static TTester T_TESTER = new TTester()
	{
		public int ttest(double v1[], double v2[], double mean1, double mean2, double confidence,
				Double correctTerm)
		{
			TTest ttest = new TTest();
			double ttestValue = ttest.pairedTTest(v1, v2);
			int test = 0;
			if (ttestValue <= (0.5 * confidence)) // one tailed test -> divide by half
			{
				if (mean1 > mean2)
					test = 1;
				else
					test = -1;
			}
			//		System.err.println("check for one-tailed/two-tailed issue");
			return test;
		}
	};

	public ResultSet diff(String prop, List<String> equalProperties, List<String> diffProperties,
			List<String> ratioProperties)
	{
		if (equalProperties.contains(prop))
			throw new Error("not working");
		if (diffProperties == null)
			diffProperties = new ArrayList<>();
		if (ratioProperties == null)
			ratioProperties = new ArrayList<>();

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
					diff.setResultValue(x, prop, res1.getValue(prop)
							+ (diffProperties.size() > 0 ? " - " : " / ") + res2.getValue(prop));
					for (String p : equalProperties)
						diff.setResultValue(x, p, res1.getValue(p));
					for (String p : properties)
					{
						if (equalPlusProp.contains(p))// || (ommitProperties != null && ommitProperties.contains(p)))
							continue;

						if (diffProperties.contains(p) || ratioProperties.contains(p))
						{
							Object val1 = res1.getValue(p);
							Object val2 = res2.getValue(p);
							if (val1 instanceof Number)
							{
								double value;
								if (diffProperties.contains(p))
									value = ((Number) val1).doubleValue()
											- ((Number) val2).doubleValue();
								else
									value = ((Number) val1).doubleValue()
											/ ((Number) val2).doubleValue();
								diff.setResultValue(x, p, value);
							}
							else
								throw new IllegalArgumentException("not a numeric prop: " + p);
						}
					}
				}
			}
		}

		return diff;

	}

	//	/**
	//	 * use diff as input, >0 ist counted as win, <0 as loss
	//	 * 
	//	 * @param winLossProp
	//	 * @return
	//	 */
	//	public ResultSet winLoss(List<String> equalProperties, List<String> ommitProperties)
	//	{
	//		int[] group = this.getGrouping(equalProperties);
	//		ResultSet winLoss = new ResultSet();
	//
	//		int groupId = 0;
	//		while (true)
	//		{
	//			int matchIndex = -1;
	//
	//			boolean na[] = new boolean[properties.size()];
	//			int win[] = new int[properties.size()];
	//			int equal[] = new int[properties.size()];
	//			int loss[] = new int[properties.size()];
	//
	//			for (int i = 0; i < group.length; i++)
	//			{
	//				if (group[i] == groupId)
	//				{
	//					matchIndex = i;
	//
	//					for (int k = 0; k < properties.size(); k++)
	//					{
	//						String p = properties.get(k);
	//						if (equalProperties.contains(p) || (ommitProperties != null && ommitProperties.contains(p)))
	//							continue;
	//
	//						Object val = results.get(i).getValue(p);
	//						if (val instanceof Number)
	//						{
	//							double v = ((Number) val).doubleValue();
	//
	//							if (v > 0)
	//								win[k]++;
	//							else if (v < 0)
	//								loss[k]++;
	//							else
	//								equal[k]++;
	//						}
	//						else
	//							na[k] = true;
	//					}
	//				}
	//			}
	//
	//			if (matchIndex == -1)
	//				break;
	//			else
	//			{
	//				int x = winLoss.addResult();
	//
	//				for (String p : equalProperties)
	//					winLoss.setResultValue(x, p, results.get(matchIndex).getValue(p));
	//
	//				for (int k = 0; k < properties.size(); k++)
	//				{
	//					String p = properties.get(k);
	//					if (equalProperties.contains(p) || (ommitProperties != null && ommitProperties.contains(p)))
	//						continue;
	//
	//					if (na[k])
	//						continue;
	//
	//					winLoss.setResultValue(x, p + "-W", win[k]);
	//					winLoss.setResultValue(x, p + "-EQ", equal[k]);
	//					winLoss.setResultValue(x, p + "-L", loss[k]);
	//				}
	//			}
	//
	//			groupId++;
	//		}
	//
	//		return winLoss;
	//	}

	public ResultSet rank(String prop, String[] equalProperties)
	{
		return rank(prop, ArrayUtil.toList(equalProperties));
	}

	public ResultSet rank(String prop, List<String> equalProperties)
	{
		ResultSet s = copy();
		int[] group = getGrouping(equalProperties);
		int groupIdx = -1;
		while (true)
		{
			groupIdx++;
			List<Double> values = new ArrayList<>();
			for (int i = 0; i < results.size(); i++)
				if (group[i] == groupIdx)
					values.add((Double) getResultValue(i, prop));
			if (values.size() == 0)
				break;
			//			int ranks[] = ArrayUtil.getRanking(ArrayUtil.getOrdering(
			//					ArrayUtil.toPrimitiveDoubleArray(ArrayUtil.toArray(values)), false));
			int ranks[] = ArrayUtil
					.getRanking(ArrayUtil.toPrimitiveDoubleArray(ArrayUtil.toArray(values)), false);
			//System.err.println("ranking " + ranks.length + " methods");
			int idx = 0;
			for (int i = 0; i < results.size(); i++)
				if (group[i] == groupIdx)
				{
					s.setResultValue(i, prop + RANK_SUFFIX, ranks[idx] + 1);
					s.setResultValue(i, prop + RANK_BEST_SUFFIX, (ranks[idx] == 0) ? 1 : 0);
					idx++;
				}
		}
		return s;
	}

	public void setNumDecimalPlaces(int d)
	{
		numDecimalPlaces = d;
	}

	public static ResultSet dummySet()
	{
		ResultSet set = new ResultSet();

		String features[] = new String[] { "fragments", "descriptor,s", "other" };
		String datasets[] = new String[] { "mouse", "elephant", "crocodile()", "tiger", "cat" };
		// , "animal1", "animal2","animal3", "animal4", "animal5", "animal6" };
		String algorithms[] = new String[] { "C4.5", "SVM", "NB" }; //, "A", "B" 
		Random random = new Random();

		for (String feature : features)
		{
			for (String dataset : datasets)
			{
				for (String algorithm : algorithms)
				{
					//					for (int seed = 0; seed < 10; seed++)
					//					{

					for (int fold = 0; fold < 10; fold++)
					{
						int idx = set.addResult();
						set.setResultValue(idx, "features", feature);
						set.setResultValue(idx, "dataset", dataset);
						if (dataset.equals("mouse") || dataset.equals("cat"))
							set.setResultValue(idx, "animal-size", "small");
						else
							set.setResultValue(idx, "animal-size", "big");
						set.setResultValue(idx, "algorithm", algorithm);
						set.setResultValue(idx, "fold", fold);
						//							set.setResultValue(idx, "seed", seed);

						double acc = 0.4 + 0.2 * random.nextDouble();
						// mouse works better, elephant works worse
						if (dataset.equals("mouse"))
							acc += 0.1 + 0.1 * random.nextDouble();
						else if (dataset.equals("elephant"))
							acc -= 0.1 + 0.2 * random.nextDouble();
						// SVM is slightly better
						if (algorithm.equals("SVM"))
							acc += 0.02 + 0.02 * random.nextDouble();

						//						if (algorithm.length() == 1)
						//							acc = 0.1;

						set.setResultValue(idx, "accuracy", acc);
						//						}
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
			ommitProperties.add("seed");

			List<String> varProperties = new ArrayList<String>();
			varProperties.add("accuracy");

			ResultSet joined = set.join(equalProperties, ommitProperties, varProperties);
			joined.unifyJoinedStringValues("animal-size");
			System.out.println("\njoined folds\n");
			System.out.println(joined.toNiceString());

			// SORT

			joined.sortProperties(new String[] { "dataset" });
			joined.sortResults("dataset");
			System.out.println("\nsorted\n");
			System.out.println(joined.toNiceString());

			{
				// RANK
				System.out.println("\nranked\n");
				List<String> equalProperties2 = new ArrayList<String>();
				equalProperties2.add("features");
				equalProperties2.add("algorithm");
				ResultSet ranked = joined.rank("accuracy", equalProperties2);
				System.out.println(ranked.toNiceString());

				System.out.println("\nmean rank\n");
				ranked.clearMergeCountAndVariance();
				ResultSet rankedJoined = ranked.join("dataset");
				rankedJoined.unifyJoinedStringValues("animal-size");
				rankedJoined.removePropery("features");
				rankedJoined.removePropery("algorithm");
				System.out.println(rankedJoined.toNiceString());

				System.out.println("correlation between accuracy and rank: "
						+ rankedJoined.spearmanCorrelation("accuracy", "accuracy_rank"));
				System.out.println("correlation between animal-size and rank: "
						+ rankedJoined.spearmanCorrelation("animal-size", "accuracy_rank"));
			}

		}

		{
			// T-TEST

			String equalProperties[] = { "features", "fold" };
			String seriesProperties[] = { "dataset" };

			ResultSet tested = set.pairedTTest("algorithm", equalProperties, "accuracy", 0.01, null,
					seriesProperties);
			System.out.println("\npaired t-test with 0.01 (should not detect that svm is best)\n");
			System.out.println(tested.toNiceString());

			tested = set.pairedTTest("algorithm", equalProperties, "accuracy", 0.02, null,
					seriesProperties);
			System.out.println("\npaired t-test with 0.02\n");
			System.out.println(tested.toNiceString());

			tested = set.pairedTTest("algorithm", equalProperties, "accuracy", 0.05, null,
					seriesProperties);
			System.out.println("\npaired t-test with 0.05\n");
			System.out.println(tested.toNiceString());

			tested = set.pairedTTest("algorithm", equalProperties, "accuracy", 0.15, null,
					seriesProperties);
			System.out.println("\npaired t-test with 0.15  (should detect that svm is best)\n");
			System.out.println(tested.toNiceString());

			tested = set.pairedTTest("algorithm", equalProperties, "accuracy", 1, null,
					seriesProperties);
			System.out.println("\npaired t-test with 1 (compares mean)\n");
			System.out.println(tested.toNiceString());

			tested = set.pairedTTestWinLoss("algorithm", new String[] { "features", "fold" },
					"accuracy", 0.15, null, seriesProperties, true);
			System.out.println("\npaired t-test win loss - 1\n");
			System.out.println(tested.toNiceString());

			String equalProperties2[] = { "fold" };
			String seriesProperties2[] = { "dataset", "features" };

			tested = set.pairedTTestWinLoss("algorithm", equalProperties2, "accuracy", 0.15, null,
					seriesProperties2, true);
			System.out.println("\npaired t-test win loss - 2\n");
			System.out.println(tested.toNiceString());

			System.exit(1);
		}

		{
			// DIFF

			List<String> equalProperties = new ArrayList<String>();
			equalProperties.add("dataset");
			equalProperties.add("algorithm");

			List<String> diffProperties = new ArrayList<String>();
			diffProperties.add("accuracy");

			ResultSet diff = set.diff("features", equalProperties, diffProperties, null);
			System.out.println("\ndiff\n");
			System.out.println(diff.toNiceString());

			diff = set.diff("features", equalProperties, null, diffProperties);
			System.out.println("\nratio\n");
			System.out.println(diff.toNiceString());

			//			// WIN-LOSS
			//
			//			List<String> equalPropertiesDiff = new ArrayList<String>();
			//			equalPropertiesDiff.add("features");
			//			equalPropertiesDiff.add("algorithm");
			//
			//			ResultSet winLoss = diff.winLoss(equalPropertiesDiff, null);
			//			System.out.println("\nwin loss (uses diff as input)\n");
			//			System.out.println(winLoss.toNiceString());
		}

	}

	private void unifyJoinedStringValues(String property)
	{
		for (Result r : results)
			r.unifyJoinedStringValues(property);
	}

	public double spearmanCorrelation(String property1, String property2)
	{
		double d1[] = new double[getNumResults()];
		CountedSet<Object> s1 = (getResultValue(0, property1) instanceof Double) ? null
				: getResultValues(property1);
		double d2[] = new double[getNumResults()];
		CountedSet<Object> s2 = (getResultValue(0, property2) instanceof Double) ? null
				: getResultValues(property2);
		for (int i = 0; i < d2.length; i++)
		{
			if (getResultValue(i, property1) instanceof Double)
				d1[i] = (Double) getResultValue(i, property1);
			else
				d1[i] = s1.values().indexOf(getResultValue(i, property1));
			if (getResultValue(i, property2) instanceof Double)
				d2[i] = (Double) getResultValue(i, property2);
			else
				d2[i] = s2.values().indexOf(getResultValue(i, property2));
		}
		return new SpearmansCorrelation().correlation(d1, d2);
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

	public ChartPanel barPlot(String title, String yAxis, String seriesProperty,
			List<String> categoryProperties, double[] range, Color[] cols)
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

	public boolean isUniqueValue(String prop)
	{
		return isUniqueValue(prop, true);
	}

	public boolean isUniqueValue(String prop, boolean includingNull)
	{
		return getResultValues(prop).getNumValues(includingNull) == 1;
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
				throw new IllegalArgumentException(ArrayUtil.toString(p)
						+ " does not fit as properties for " + ArrayUtil.toString(v));
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
					rs.setResultValue(i, p, niceValue(getResultValue(i, p)) + "+-"
							+ niceValue(getResultValue(i, p + VARIANCE_SUFFIX)));
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
				t.setResultValue(idx, "value" + (getNumResults() > 1 ? (i + 1) : ""),
						getResultValue(i, p));
		}
		return t;
	}

	public boolean equals(Object o)
	{
		if (!(o instanceof ResultSet))
			return false;
		ResultSet r = (ResultSet) o;
		if (r.getNumResults() != getNumResults())
			return false;
		if (!r.properties.equals(properties))
			return false;
		if (!r.results.equals(results))
			return false;
		return true;
	}

}
