package org.mg.javalib.freechart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;
import org.mg.javalib.util.SwingUtil;

public class Chart
{

	// @SuppressWarnings("unchecked")
	// public static void plotDoubleArrays(String title, String x, String y, List<String> captions, List<double[]> data,
	// boolean wait)
	// {
	// List<Iterator<Double>> l = new ArrayList<Iterator<Double>>();
	// for (double ds[] : data)
	// l.add(new ArrayIterator(ds));
	// plotIterators(title, x, y, captions, l, wait);
	// }
	//
	// @SuppressWarnings("unchecked")
	// public static <T> void plotArrays(String title, String x, String y, List<String> captions, List<T[]> data, boolean wait)
	// {
	// List<Iterator<T>> l = new ArrayList<Iterator<T>>();
	// for (T ds[] : data)
	// l.add(new ArrayIterator(ds));
	// plotIterators(title, x, y, captions, l, wait);
	// }
	//
	// public static <T> void plotIterables(String title, String x, String y, List<String> captions, List<Iterable<T>> data,
	// boolean wait)
	// {
	// List<Iterator<T>> l = new ArrayList<Iterator<T>>();
	// for (Iterable<T> iterable : data)
	// l.add(iterable.iterator());
	// plotIterators(title, x, y, captions, l, wait);
	// }

	public static void plot(String title, String x, String y, List<String> captions, List<Map<Double, Double>> data,
			boolean wait, double[] xMinMax, double[] yMinMax)
	{
		XYSeriesCollection collection = new XYSeriesCollection();

		for (int i = 0; i < data.size(); i++)
		{
			XYSeries series = new XYSeries((captions != null && i < captions.size()) ? captions.get(i) : "Data" + (i + 1));

			Map<Double, Double> d = data.get(i);

			for (Double k : d.keySet())
			{
				series.add(k, d.get(k));
			}

			collection.addSeries(series);
		}

		JFreeChart chart = ChartFactory.createXYLineChart(title, x, y, collection, PlotOrientation.VERTICAL, true, true,
				false);

		// Axis a = ((XYPlot) chart.getPlot()).get

		if (xMinMax != null)
			((XYPlot) chart.getPlot()).getDomainAxis().setRange(new Range(xMinMax[0], xMinMax[1]));
		if (yMinMax != null)
			((XYPlot) chart.getPlot()).getRangeAxis().setRange(new Range(yMinMax[0], yMinMax[1]));

		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

		JFrame f = new JFrame(title);

		f.setContentPane(chartPanel);

		// f.pack();
		f.setSize(1024, 768);

		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.setLocationRelativeTo(null);
		f.setVisible(true);

		if (wait)
			SwingUtil.waitWhileVisible(f);
	}

	public static JFreeChart createXYStepChart(String title, String xAxisLabel, String yAxisLabel, XYDataset dataset,
			PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls)
	{

		if (orientation == null)
		{
			throw new IllegalArgumentException("Null 'orientation' argument.");
		}
		NumberAxis xAxis = new LogarithmicAxis(xAxisLabel);
		// xAxis.setMarkerBand(new MarkerAxisBand())

		NumberAxis yAxis = new NumberAxis(yAxisLabel);
		// yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		XYToolTipGenerator toolTipGenerator = null;
		if (tooltips)
		{
			toolTipGenerator = new StandardXYToolTipGenerator();
		}

		XYURLGenerator urlGenerator = null;
		if (urls)
		{
			urlGenerator = new StandardXYURLGenerator();
		}
		XYItemRenderer renderer = new XYStepRenderer(toolTipGenerator, urlGenerator);

		XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);
		plot.setRenderer(renderer);
		plot.setOrientation(orientation);
		plot.setDomainCrosshairVisible(false);
		plot.setRangeCrosshairVisible(false);
		JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
		ChartFactory.getChartTheme().apply(chart);
		// currentTheme.apply(chart);
		return chart;

	}

	public static void plotStep(String title, String x, String y, List<String> captions, List<Map<Double, Double>> data,
			boolean wait, double[] xMinMax, double[] yMinMax)
	{

		XYSeriesCollection collection = new XYSeriesCollection();

		for (int i = 0; i < data.size(); i++)
		{
			XYSeries series = new XYSeries((captions != null && i < captions.size()) ? captions.get(i) : "Data" + (i + 1));

			Map<Double, Double> d = data.get(i);

			for (Double k : d.keySet())
			{
				series.add(k, d.get(k));
			}

			collection.addSeries(series);
		}

		JFreeChart chart = createXYStepChart(title, x, y, collection, PlotOrientation.VERTICAL, true, true, false);
		Color cols[] = new Color[] { Color.RED.brighter(), Color.BLUE.brighter(), Color.GREEN.darker() };

		for (int i = 0; i < data.size(); i++)
		{
			((XYPlot) chart.getPlot()).getRenderer().setSeriesStroke(i, new BasicStroke(1.8f));
			((XYPlot) chart.getPlot()).getRenderer().setSeriesPaint(i, cols[i]);
		}

		Marker currentEnd = new ValueMarker(5);
		currentEnd.setPaint(Color.BLACK);
		currentEnd.setLabelFont(currentEnd.getLabelFont().deriveFont(currentEnd.getLabelFont().getSize() + 4f));
		currentEnd.setLabel("5-NN");
		currentEnd.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
		currentEnd.setLabelTextAnchor(TextAnchor.TOP_LEFT);
		((XYPlot) chart.getPlot()).addDomainMarker(currentEnd);

		Marker target = new ValueMarker(0.5);
		target.setPaint(Color.BLACK);
		target.setLabelFont(target.getLabelFont().deriveFont(target.getLabelFont().getSize() + 4f));
		target.setLabel("class ratio 0.5");
		target.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
		target.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
		((XYPlot) chart.getPlot()).addRangeMarker(target);

		// Axis a = ((XYPlot) chart.getPlot()).get

		if (xMinMax != null)
			((XYPlot) chart.getPlot()).getDomainAxis().setRange(new Range(xMinMax[0], xMinMax[1]));
		if (yMinMax != null)
			((XYPlot) chart.getPlot()).getRangeAxis().setRange(new Range(yMinMax[0], yMinMax[1]));

		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

		JFrame f = new JFrame(title);

		f.setContentPane(chartPanel);

		// f.pack();
		f.setSize(1024, 768);

		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.setLocationRelativeTo(null);
		f.setVisible(true);

		if (wait)
			SwingUtil.waitWhileVisible(f);
	}

	public static void main(String args[])
	{
		List<Map<Double, Double>> data = new ArrayList<Map<Double, Double>>();
		List<String> captions = new ArrayList<String>();

		Random r = new Random();

		int length = r.nextInt(1000) + 10;

		while (data.size() < 2)// || r.nextBoolean())
		{
			captions.add("Data " + (data.size() + 1));

			double y = r.nextInt(10) - 5;

			Map<Double, Double> d = new HashMap<Double, Double>();
			for (int i = 0; i < length; i++)
			{
				y += r.nextDouble() * (r.nextBoolean() ? 1 : -1);
				d.put((double) i, y);
			}

			data.add(d);
		}

		plot("test", "x-wert", "y-wert", captions, data, true, null, null);// , new double[] { 0, 1 });
		System.exit(0);
	}
}
