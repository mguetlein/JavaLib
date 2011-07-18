package freechart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RefineryUtilities;

import util.ArrayUtil;
import util.SwingUtil;

public class BarPlot extends JFrame
{

	Map<String, List<Double>> data = new HashMap<String, List<Double>>();

	public BarPlot(final String title)
	{
		super(title);

	}

	public void plotChart()
	{
		final CategoryDataset dataset = createDataset();
		final JFreeChart chart = createChart(dataset);

		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(1200, 800));
		setContentPane(chartPanel);
		pack();
		RefineryUtilities.centerFrameOnScreen(this);
		setVisible(true);

		SwingUtil.waitWhileVisible(this);
	}

	public void addData(String name, List<Double> d)
	{
		data.put(name, d);
	}

	private CategoryDataset createDataset()
	{
		DefaultCategoryDataset d = new DefaultCategoryDataset();
		for (String name : data.keySet())
		{
			List<Double> vals = data.get(name);
			int i = 0;
			for (Double val : vals)
			{
				d.addValue(val, name, i + "");
				i++;
			}
		}
		return d;
	}

	private JFreeChart createChart(CategoryDataset dataset)
	{

		JFreeChart chart = ChartFactory.createStackedBarChart("Stacked Bar Chart Demo 3", // chart title
				"Category", // domain axis label
				"Value", // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, // the plot orientation
				true, // legend
				false, // tooltips
				false // urls
				);
		CategoryPlot plot = chart.getCategoryPlot();

		((BarRenderer) plot.getRenderer()).setShadowVisible(false);

		((NumberAxis) plot.getRangeAxis()).setTickUnit(new NumberTickUnit(1));
		// ((NumberAxis) plot.getDomainAxis()).setTickUnit(new NumberTickUnit(1));

		// plot.setRangeAxis(new LogarithmicAxis("bla"));

		// final CategoryItemRenderer renderer = new ExtendedStackedBarRenderer();
		// renderer.setToolTipGenerator(new StandardCategoryToolTipGenerator());
		// plot.setRenderer(renderer);

		// ValueAxis rangeAxis = plot.getRangeAxis();
		// rangeAxis.setLowerMargin(0.15);
		// rangeAxis.setUpperMargin(0.15);
		return chart;
	}

	public static void plot(Map<String, List<Double>> data)
	{
		BarPlot demo = new BarPlot("Stacked Bar Chart Demo 3");
		demo.data = data;
		demo.plotChart();
	}

	public static void main(final String[] args)
	{
		// BarPlot demo = new BarPlot("Stacked Bar Chart Demo 3");
		// demo.addData("test", new double[] { 0, 1, 2, 3 });
		// demo.addData("test2", new double[] { 2, 1, 0.5, 9 });
		// demo.plot();

		Map<String, List<Double>> data = new HashMap<String, List<Double>>();

		data.put("test", ArrayUtil.toList(new double[]
		{ 0, 1, 2, 3 }));
		data.put("test2", ArrayUtil.toList(new double[]
		{ 2, 1, 0.5, 9 }));
		plot(data);

		System.out.println("done");
		System.exit(0);

	}
}
