package org.mg.javalib.freechart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.mg.javalib.util.ArrayUtil;
import org.mg.javalib.util.ListUtil;
import org.mg.javalib.util.SwingUtil;

public class BarPlotPanel extends AbstractFreeChartPanel
{
	Map<String, List<Double>> data = new HashMap<String, List<Double>>();

	public BarPlotPanel(String title, String yAxisLabel, double values[], String names[])
	{
		this(title, yAxisLabel, values, names, true);
	}

	public BarPlotPanel(String title, String yAxisLabel, double values[], String names[], boolean opaque)
	{
		this(title, yAxisLabel, ArrayUtil.toList(values), ArrayUtil.toList(names), opaque);
	}

	public BarPlotPanel(String title, String yAxisLabel, List<Double> values, List<String> names)
	{
		this(title, yAxisLabel, values, names, true);
	}

	@SuppressWarnings("unchecked")
	public BarPlotPanel(String title, String yAxisLabel, List<Double> values, List<String> names, boolean opaque)
	{
		this(title, yAxisLabel, names, opaque, values);
	}

	public BarPlotPanel(String title, String yAxisLabel, List<String> categoryNames, boolean opaque,
			List<Double>... values)
	{
		this(title, yAxisLabel, categoryNames, null, opaque, values);
	}

	public BarPlotPanel(String title, String yAxisLabel, List<String> categoryNames, List<String> seriesNames,
			boolean opaque, List<Double>... values)
	{
		if (seriesNames != null && seriesNames.size() != values.length)
			throw new IllegalArgumentException(ListUtil.toString(seriesNames) + " length != " + values.length);
		//		System.out.println(ListUtil.toString(categoryNames));
		for (List<Double> vals : values)
		{
			//			System.out.println(ListUtil.toString(vals));
			if (vals.size() != categoryNames.size())
				throw new IllegalArgumentException(vals.size() + " != " + categoryNames.size() + "\n"
						+ ListUtil.toString(vals) + "\n" + ListUtil.toString(categoryNames));
		}

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		int count = 0;
		for (List<Double> vals : values)
		{
			String rowKey = (seriesNames != null ? seriesNames.get(count) : count + "");
			for (int i = 0; i < categoryNames.size(); i++)
			{
				dataset.addValue(vals.get(i), rowKey, categoryNames.get(i) + "");
			}
			count++;
		}

		JFreeChart chart = ChartFactory.createBarChart(title, // chart title
				null, // domain axis label
				yAxisLabel, // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, // the plot orientation
				seriesNames != null, // legend
				false, // tooltips
				false // urls
				);
		//		CategoryPlot plot = chart.getCategoryPlot();
		//		((BarRenderer) plot.getRenderer()).setShadowVisible(false);
		//		((NumberAxis) plot.getRangeAxis()).setTickUnit(new NumberTickUnit(1));

		chart.setBackgroundPaint(new Color(255, 255, 255, 0));
		chartPanel = new ChartPanel(chart);
		chartPanel.setOpaque(false);
		chartPanel.setPreferredSize(null);
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);
		add(chartPanel);

		if (!opaque)
		{
			setOpaque(false);
			chartPanel.setOpaque(false);
			chartPanel.setBackground(new Color(0, 0, 0, 0f));
			chartPanel.getChart().getPlot().setBackgroundAlpha(0f);
			chartPanel.getChart().setBackgroundPaint(new Color(0, 0, 0, 0f));
		}
	}

	public void setMaximumBarWidth(double d)
	{
		CategoryPlot categoryPlot = getChartPanel().getChart().getCategoryPlot();
		BarRenderer br = (BarRenderer) categoryPlot.getRenderer();
		br.setMaximumBarWidth(d);
	}

	public ChartPanel getChartPanel()
	{
		return chartPanel;
	}

	// ((NumberAxis) plot.getDomainAxis()).setTickUnit(new NumberTickUnit(1));

	// plot.setRangeAxis(new LogarithmicAxis("bla"));

	// final CategoryItemRenderer renderer = new ExtendedStackedBarRenderer();
	// renderer.setToolTipGenerator(new StandardCategoryToolTipGenerator());
	// plot.setRenderer(renderer);

	// ValueAxis rangeAxis = plot.getRangeAxis();
	// rangeAxis.setLowerMargin(0.15);
	// rangeAxis.setUpperMargin(0.15);

	@SuppressWarnings("unchecked")
	public static void main(final String[] args)
	{
		// BarPlot demo = new BarPlot("Stacked Bar Chart Demo 3");
		// demo.addData("test", new double[] { 0, 1, 2, 3 });
		// demo.addData("test2", new double[] { 2, 1, 0.5, 9 });
		// demo.plot();

		JPanel p = new JPanel(new BorderLayout());
		p.setBackground(Color.YELLOW);
		BarPlotPanel plot = new BarPlotPanel("Title", "Axis-Name", ArrayUtil.toList(new String[] { "asdf", "ene",
				"mene", "miste" }), ArrayUtil.toList(new String[] { "first", "second" }), false,
				ArrayUtil.toList(new double[] { 3, 5, 7, 2 }), ArrayUtil.toList(new double[] { 4, 3, 7, 1 }));
		plot.setFontSize(20);
		p.add(plot);
		SwingUtil.showInDialog(p, new Dimension(400, 300));
		System.out.println("done");
		System.exit(0);

	}
}
