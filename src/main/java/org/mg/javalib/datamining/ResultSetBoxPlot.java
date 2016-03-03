package org.mg.javalib.datamining;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.data.Range;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.ui.Layer;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;
import org.mg.javalib.freechart.FreeChartUtil;
import org.mg.javalib.freechart.MessageChartPanel;
import org.mg.javalib.util.ArrayUtil;
import org.mg.javalib.util.CountedSet;
import org.mg.javalib.util.DoubleArraySummary;
import org.mg.javalib.util.DoubleKeyHashMap;
import org.mg.javalib.util.SwingUtil;

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
	Double yTickUnit = null;
	double yRange[] = null;
	boolean zeroOneRange = false;
	boolean rotateXLabels = false;
	boolean hideMean = false;
	boolean printResultsPerPlot = true;
	boolean printMeanAndStdev = false;
	boolean drawLineForHighestMedian = false;
	boolean drawLineForFirstMedian = false;
	Float fontSize = null;

	String seriesProperty2;
	String categoryProperty;

	DoubleKeyHashMap<String, String, List<Double>> values;
	DefaultBoxAndWhiskerCategoryDataset dataset;
	HashMap<String, String> labels;

	Double highestMedian = 0.0;
	Double firstMedian = null;

	/**
	 * e.g.
	 * series = algorithm
	 * categories = accuracy,auc,recall
	 */
	public ResultSetBoxPlot(ResultSet set, String title, String yAxisLabel, String seriesProperty,
			List<String> categoryProperties)
	{
		this.set = set;
		this.title = title;
		this.seriesProperty = seriesProperty;
		this.yAxisLabel = yAxisLabel;
		this.categoryProperties = categoryProperties;
	}

	/**
	 * e.g.
	 * series = algorithm
	 * series2 = dataset
	 * category = auc
	 */
	public ResultSetBoxPlot(ResultSet set, String title, String yAxisLabel, String seriesProperty,
			String seriesProperty2, String categoryProperty)
	{
		this.set = set;
		this.title = title;
		this.seriesProperty = seriesProperty;
		this.yAxisLabel = yAxisLabel;
		this.seriesProperty2 = seriesProperty2;
		this.categoryProperty = categoryProperty;
	}

	public void setSubtitles(String[] subtitles)
	{
		this.subtitles = subtitles;
	}

	public void setZeroOneRange(boolean zeroOneRange)
	{
		this.zeroOneRange = zeroOneRange;
	}

	public void setPrintMeanAndStdev(boolean printMeanAndStdev)
	{
		this.printMeanAndStdev = printMeanAndStdev;
	}

	public void setYTickUnit(Double yTickUnit)
	{
		this.yTickUnit = yTickUnit;
	}

	public void setYRange(double min, double max)
	{
		this.yRange = new double[] { min, max };
	}

	public void setDisplayCategories(List<String> dispProps)
	{
		for (int i = 0; i < categoryProperties.size(); i++)
			set.setNicePropery(categoryProperties.get(i), dispProps.get(i));
	}

	private void initValues()
	{
		values = new DoubleKeyHashMap<String, String, List<Double>>();
		dataset = new DefaultBoxAndWhiskerCategoryDataset();

		if (seriesProperty2 == null)
		{
			for (int i = 0; i < categoryProperties.size(); i++)
				for (int r = 0; r < set.getNumResults(); r++)
				{
					String key1 = set.getNiceProperty(categoryProperties.get(i));
					String seriesVal = set.getResultValue(r, seriesProperty) + "";
					if (!values.containsKeyPair(key1, seriesVal))
						values.put(key1, seriesVal, new ArrayList<Double>());
					Object v = set.getResultValue(r, categoryProperties.get(i));
					if (v == null)
						throw new Error(
								"no value for " + key1 + " for " + categoryProperties.get(i));
					Double d = Double.parseDouble(v + "");
					if (!d.isNaN())
						values.get(key1, seriesVal).add(d);
				}
		}
		else
		{
			CountedSet<Object> series2 = set.getResultValues(seriesProperty2);

			for (final Object series2Value : series2.valuesInsertionOrder())
			{
				ResultSet filtered = set.filter(new ResultSetFilter()
				{
					@Override
					public boolean accept(Result result)
					{
						return result.getValue(seriesProperty2).equals(series2Value.toString());
					}
				});
				for (int r = 0; r < filtered.getNumResults(); r++)
				{
					String key1 = series2Value.toString();
					String seriesVal = filtered.getResultValue(r, seriesProperty) + "";
					if (!values.containsKeyPair(key1, seriesVal))
						values.put(key1, seriesVal, new ArrayList<Double>());
					Object v = filtered.getResultValue(r, categoryProperty);
					if (v == null)
						throw new Error("no value for " + key1);
					Double d = Double.parseDouble(v + "");
					if (!d.isNaN())
						values.get(key1, seriesVal).add(d);
				}
			}
		}

		labels = new HashMap<>();
		int minSize = Integer.MAX_VALUE;
		int maxSize = 0;
		KeyCounter keyCounter = new KeyCounter();
		for (String key1 : values.keySet1())
		{
			for (String key2 : values.keySet2(key1))
			{
				List<Double> v = values.get(key1, key2);
				String l = "";
				if (labels.containsKey(key1))
					l += labels.get(key1) + "  /  ";
				DoubleArraySummary valSum = DoubleArraySummary.create(v);
				labels.put(key1, l + valSum.toString());
				if (valSum.getMedian() > highestMedian)
					highestMedian = valSum.getMedian();
				if (firstMedian == null)
					firstMedian = valSum.getMedian();

				dataset.add(v, key2, key1);
				int s = values.get(key1, key2).size();

				keyCounter.add(key1, s);
				minSize = Math.min(s, minSize);
				maxSize = Math.max(s, maxSize);
			}
		}
		sizeStr = new String[] { "#results per plot: " + maxSize
				+ (maxSize > minSize ? (" " + keyCounter.toString(maxSize)) : "") };
		if (printResultsPerPlot)
		{
			if (this.subtitles == null)
				this.subtitles = sizeStr;
			else
				this.subtitles = ArrayUtil.concat(String.class, subtitles, sizeStr);
		}
	}

	public String getTitle()
	{
		return title;
	}

	public String getYAxisLabel()
	{
		return yAxisLabel;
	}

	public void setRotateXLabels(boolean rotateXLabels)
	{
		this.rotateXLabels = rotateXLabels;
	}

	public void setHideMean(boolean hideMean)
	{
		this.hideMean = hideMean;
	}

	public void printNumResultsPerPlot(boolean b)
	{
		this.printResultsPerPlot = b;
	}

	public void setDrawLineForFirstMedian(boolean drawLineForFirstMedian)
	{
		this.drawLineForFirstMedian = drawLineForFirstMedian;
	}

	public void setDrawLineForHighestMedian(boolean drawLineForHighestMedian)
	{
		this.drawLineForHighestMedian = drawLineForHighestMedian;
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
		else if (yRange != null)
			yAxis.setRange(yRange[0], yRange[1]);

		yAxis.setAutoRangeIncludesZero(false);

		final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
		renderer.setFillBox(true);

		renderer.setItemMargin(0.05);

		if (hideMean)
			renderer.setMeanVisible(false);

		CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

		//JFreeChart chart = new JFreeChart(title, new Font("SansSerif", Font.BOLD, 14), plot, true);
		JFreeChart chart = new JFreeChart(title, plot);
		MessageChartPanel chartPanel = new MessageChartPanel(chart);

		if (title != null && !title.isEmpty())
		{
			Font f = chart.getTitle().getFont();
			if (fontSize == null)
				chart.getTitle().setFont(f.deriveFont(f.getSize2D() - 2.0f));
			else
				chart.getTitle().setFont(f.deriveFont(fontSize * 1.4F));
		}

		if (printMeanAndStdev)
		{
			for (Object category : dataset.getColumnKeys())
			{
				if (labels.containsKey(category.toString()))
				{
					System.err.println("label for " + category.toString() + " "
							+ labels.get(category.toString()));
					CategoryMarker marker = new CategoryMarker(category.toString());
					//				marker.setOutlinePaint(null);
					marker.setPaint(new Color(0, 0, 0, 0));
					//				marker.setDrawAsLine(false);
					String lab = labels.get(category.toString());
					//					lab = lab.replaceAll("0\\.", ".");
					//					lab = lab.replaceAll("0,", ",");
					lab = lab.replaceAll("\\s", "");
					marker.setLabel(lab);
					//				marker.setLabelFont(plot.getDomainAxis().getLabelFont());
					marker.setLabelAnchor(RectangleAnchor.BOTTOM);
					marker.setLabelTextAnchor(TextAnchor.BOTTOM_CENTER);
					marker.setLabelOffsetType(LengthAdjustmentType.CONTRACT);
					plot.addDomainMarker(marker, Layer.BACKGROUND);
				}
				else
				{
					System.err.println("no label");
				}
			}
			if (!zeroOneRange && yRange == null)
			{
				Range r = yAxis.getRange();
				yAxis.setRange(r.getLowerBound() - (r.getLength() * 0.1), r.getUpperBound());
			}
		}

		if (drawLineForHighestMedian)
		{
			ValueMarker marker = new ValueMarker(highestMedian); // position is the value on the axis
			marker.setPaint(Color.DARK_GRAY);
			plot.addRangeMarker(marker);
		}
		if (drawLineForFirstMedian && firstMedian != null)
		{
			ValueMarker marker = new ValueMarker(firstMedian); // position is the value on the axis
			marker.setPaint(Color.DARK_GRAY);
			plot.addRangeMarker(marker);
		}
		//chartPanel.setBounds(new Rectangle(3000, 500));

		//chartPanel.setPreferredSize(new Dimension(3000, 500));

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
					TextTitle t = new TextTitle(s);
					sub.add(t);
					if (fontSize != null)
						t.setFont(t.getFont().deriveFont(fontSize * 0.8F));
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

		if (rotateXLabels)
			xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

		//		if (results.getResultValues(compareProp).size() == 1)
		//			boxPlot1.getChart().getCategoryPlot().getRenderer().setSeriesVisibleInLegend(0, Boolean.FALSE);

		if (dataset.getRowCount() == 1)
		{
			renderer.setSeriesPaint(0, Color.BLACK);
			renderer.setFillBox(false);
		}

		chartPanel.setMaximumDrawWidth(100000);
		chartPanel.setMaximumDrawHeight(100000);

		xAxis.setMaximumCategoryLabelWidthRatio(1.33f); // allow to draw larger label then main category
		if (fontSize != null)
		{
			if (chart.getLegend() != null)
				chart.getLegend().setItemFont(chart.getLegend().getItemFont().deriveFont(fontSize));
			CategoryPlot p = (CategoryPlot) plot;
			p.getRangeAxis().setLabelFont(
					p.getRangeAxis().getLabelFont().deriveFont(Font.BOLD, fontSize * 1.2F));
			p.getRangeAxis()
					.setTickLabelFont(p.getRangeAxis().getTickLabelFont().deriveFont(fontSize));
			p.getDomainAxis().setLabelFont(
					p.getDomainAxis().getLabelFont().deriveFont(Font.BOLD, fontSize * 1.2F));
			p.getDomainAxis()
					.setTickLabelFont(p.getDomainAxis().getTickLabelFont().deriveFont(fontSize));
			if (printMeanAndStdev)
				for (Object m : p.getDomainMarkers(Layer.BACKGROUND))
					((CategoryMarker) m).setLabelFont(
							((CategoryMarker) m).getLabelFont().deriveFont(fontSize * 0.8F));
		}

		return chartPanel;

	}

	public static void testBoxPlot()
	{
		Random r = new Random();
		ResultSet rs = new ResultSet();
		for (int datasets = 0; datasets < 20; datasets++)
			for (int folds = 0; folds < 10; folds++)
				for (String ser : new String[] { "eins", "zwei", "drei" })
				{
					int x = rs.addResult();
					rs.setResultValue(x, "dataset", "dataset" + datasets);
					rs.setResultValue(x, "fold", folds);
					rs.setResultValue(x, "algorithm", ser);
					rs.setResultValue(x, "auc", r.nextDouble() + 0.1);
					rs.setResultValue(x, "accuracy", r.nextDouble());
					rs.setResultValue(x, "recall", r.nextDouble());
					//					break;
				}

		{
			ResultSetBoxPlot p = new ResultSetBoxPlot(rs, "title", "yLabel", null,
					ArrayUtil.toList(new String[] { "auc", "accuracy", "recall" }));
			p.setSubtitles(new String[] { "subtitle1", "subtitle2" });
			SwingUtil.showInDialog(p.getChart(), new Dimension(400, 400));
		}
		{
			ResultSetBoxPlot p = new ResultSetBoxPlot(rs, "title", "auc", "algorithm", "dataset",
					"auc");
			ChartPanel pp = p.getChart();

			pp.setMaximumDrawWidth(100000);
			pp.setMaximumDrawHeight(100000);
			pp.setPreferredSize(new Dimension(2000, 1000));

			SwingUtil.showInDialog(pp);
		}
		{
			ResultSetBoxPlot p = new ResultSetBoxPlot(rs, "title", "yLabel", "algorithm",
					ArrayUtil.toList(new String[] { "auc", "accuracy", "recall" }));

			p.setSubtitles(new String[] { "subtitle1", "subtitle2" });
			p.setPrintMeanAndStdev(true);
			p.setFontSize(16F);
			SwingUtil.showInDialog(p.getChart());
		}

		System.exit(1);
	}

	public static void main(String[] args)
	{
		testBoxPlot();
	}

	public void setFontSize(float s)
	{
		fontSize = s;
	}

}
