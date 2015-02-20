package org.mg.javalib.datamining;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.mg.javalib.freechart.FreeChartUtil;
import org.mg.javalib.freechart.MessageChartPanel;
import org.mg.javalib.util.ArrayUtil;
import org.mg.javalib.util.DoubleArraySummary;
import org.mg.javalib.util.DoubleKeyHashMap;

public class ResultSetBoxPlot
{
	private static class KeyCounter
	{
		LinkedHashMap<String, Integer[]> map = new LinkedHashMap<String, Integer[]>();

		public void add(String key, int count)
		{
			if (map.containsKey(key))
				map.put(key, ArrayUtil.concat(map.get(key), new Integer[] { count }));
			else
				map.put(key, new Integer[] { count });
		}

		public String toString(int norm)
		{
			Set<String> keys = map.keySet();
			String s = "( ";
			for (String k : keys)
			{
				Integer[] count = map.get(k);
				Integer u = ArrayUtil.uniqValue(count);
				if (u == null)
					s += k + " #" + ArrayUtil.toString(count) + ", ";
				else if (u != norm)
					s += k + " #" + u + ", ";
			}
			s = s.substring(0, s.length() - 2);
			s += " )";
			return s;
		}
	}

	ResultSet set;

	String title;
	String subtitles[];
	String sizeStr[];
	String seriesProperty;
	String yAxisLabel;

	List<String> categoryProperties;
	List<String> displayCategories;
	Double yTickUnit = null;
	boolean zeroOneRange = false;

	DoubleKeyHashMap<String, String, List<Double>> values;
	DefaultBoxAndWhiskerCategoryDataset dataset;

	public ResultSetBoxPlot(ResultSet set, String title, String yAxisLabel, String seriesProperty,
			List<String> categoryProperties)
	{
		this.set = set;
		this.title = title;
		this.seriesProperty = seriesProperty;
		this.yAxisLabel = yAxisLabel;
		this.categoryProperties = categoryProperties;
		this.displayCategories = categoryProperties;
	}

	public void setSubtitles(String[] subtitles)
	{
		this.subtitles = subtitles;
	}

	public void setZeroOneRange(boolean zeroOneRange)
	{
		this.zeroOneRange = zeroOneRange;
	}

	public void setYTickUnit(Double yTickUnit)
	{
		this.yTickUnit = yTickUnit;
	}

	public void setDisplayCategories(List<String> displayCategories)
	{
		this.displayCategories = displayCategories;
	}

	private void initValues()
	{
		values = new DoubleKeyHashMap<String, String, List<Double>>();
		dataset = new DefaultBoxAndWhiskerCategoryDataset();

		for (int i = 0; i < categoryProperties.size(); i++)
			for (int r = 0; r < set.getNumResults(); r++)
			{
				String key1 = displayCategories.get(i);
				String seriesVal = set.getResultValue(r, seriesProperty) + "";
				if (!values.containsKeyPair(key1, seriesVal))
					values.put(key1, seriesVal, new ArrayList<Double>());
				Object v = set.getResultValue(r, categoryProperties.get(i));
				if (v == null)
					throw new Error("no value for " + key1);
				Double d = Double.parseDouble(v + "");
				if (!d.isNaN())
					values.get(key1, seriesVal).add(d);
			}
		int minSize = Integer.MAX_VALUE;
		int maxSize = 0;
		KeyCounter keyCounter = new KeyCounter();
		for (String key1 : values.keySet1())
		{
			System.out.println("* " + key1);
			for (String key2 : values.keySet2(key1))
			{
				List<Double> v = values.get(key1, key2);
				System.out.println(key2 + " * " + DoubleArraySummary.create(v));
				dataset.add(v, key2, key1);
				int s = values.get(key1, key2).size();

				keyCounter.add(key1, s);
				minSize = Math.min(s, minSize);
				maxSize = Math.max(s, maxSize);
			}
		}
		sizeStr = new String[] { "#results per plot: " + maxSize
				+ (maxSize > minSize ? (" " + keyCounter.toString(maxSize)) : "") };
		if (this.subtitles == null)
			this.subtitles = sizeStr;
		else
			this.subtitles = ArrayUtil.concat(String.class, subtitles, sizeStr);
	}

	public String getTitle()
	{
		return title;
	}

	public String getYAxisLabel()
	{
		return yAxisLabel;
	}

	public DoubleKeyHashMap<String, String, List<Double>> getValues()
	{
		if (values == null)
			initValues();
		return values;
	}

	public void ToPNGFile(String pngfilename, Dimension dim)
	{
		FreeChartUtil.toPNGFile(pngfilename, getChart(), dim);
	}

	public void boxPlotToSVGFile(String svgfilename, Dimension dim)
	{
		FreeChartUtil.toSVGFile(svgfilename, getChart(), dim);
	}

	public MessageChartPanel getChart()
	{
		if (values == null)
			initValues();

		final CategoryAxis xAxis;
		if (dataset.getRowCount() > 1) // show series property as axis-label only if there is more than one series
			xAxis = new CategoryAxis(seriesProperty);
		else
			xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis(yAxisLabel);

		if (yTickUnit != null)
			yAxis.setTickUnit(new NumberTickUnit(yTickUnit));

		if (zeroOneRange)
			yAxis.setRange(0, 1);

		yAxis.setAutoRangeIncludesZero(false);
		final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
		renderer.setFillBox(true);

		renderer.setItemMargin(0.05);

		//		renderer.setMeanVisible(false);

		CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

		//JFreeChart chart = new JFreeChart(title, new Font("SansSerif", Font.BOLD, 14), plot, true);
		JFreeChart chart = new JFreeChart(title, plot);
		MessageChartPanel chartPanel = new MessageChartPanel(chart);
		//chartPanel.setPreferredSize(new java.awt.Dimension(450, 270));

		chartPanel.setWarning(ArrayUtil.toString(sizeStr));

		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryMargin(0.3);

		if (subtitles != null)
		{
			List<Title> sub = new ArrayList<Title>();
			for (String s : subtitles)
			{
				if (s != null)
				{
					Title t = new TextTitle(s);
					sub.add(t);
				}
			}
			if (sub.size() > 0)
			{
				if (dataset.getRowCount() > 1) // show legend only if there is more than one series
					sub.add(chart.getLegend());
				chart.setSubtitles(sub);
			}
		}
		else if (dataset.getRowCount() == 1) // remove legend only if there one series
			chart.setSubtitles(new ArrayList<Title>());

		chartPanel.getChart().getPlot().setBackgroundPaint(Color.WHITE);
		if (chartPanel.getChart().getPlot() instanceof XYPlot)
			((XYPlot) chartPanel.getChart().getPlot()).setRangeGridlinePaint(Color.GRAY);
		else
			((CategoryPlot) chartPanel.getChart().getPlot()).setRangeGridlinePaint(Color.GRAY);
		chartPanel.getChart().setBackgroundPaint(new Color(0, 0, 0, 0));

		//		if (results.getResultValues(compareProp).size() == 1)
		//			boxPlot1.getChart().getCategoryPlot().getRenderer().setSeriesVisibleInLegend(0, Boolean.FALSE);
		return chartPanel;

	}
}
