package freechart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import util.ArrayUtil;
import util.SwingUtil;

public class StackedBarPlot extends AbstractFreeChartPanel
{

	public StackedBarPlot(String title, String xCaption, String yCaption, Map<String, List<Double>> data,
			String[] categories)
	{
		CategoryDataset dataset = createDataset(data, categories);

		JFreeChart chart = ChartFactory.createStackedBarChart(title, // chart title
				xCaption, // domain axis label
				yCaption, // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, // the plot orientation
				true, // legend
				false, // tooltips
				false // urls
				);
		//		CategoryPlot plot = chart.getCategoryPlot();
		//		((BarRenderer) plot.getRenderer()).setShadowVisible(false);
		//		((NumberAxis) plot.getRangeAxis()).setTickUnit(new NumberTickUnit(1));
		// ((NumberAxis) plot.getDomainAxis()).setTickUnit(new NumberTickUnit(1));

		// plot.setRangeAxis(new LogarithmicAxis("bla"));

		// final CategoryItemRenderer renderer = new ExtendedStackedBarRenderer();
		// renderer.setToolTipGenerator(new StandardCategoryToolTipGenerator());
		// plot.setRenderer(renderer);

		// ValueAxis rangeAxis = plot.getRangeAxis();
		// rangeAxis.setLowerMargin(0.15);
		// rangeAxis.setUpperMargin(0.15);

		chartPanel = new ChartPanel(chart);

		setLayout(new BorderLayout());
		add(chartPanel);
	}

	private CategoryDataset createDataset(Map<String, List<Double>> data, String[] categories)
	{
		DefaultCategoryDataset d = new DefaultCategoryDataset();
		for (String name : data.keySet())
		{
			List<Double> vals = data.get(name);
			int i = 0;
			for (Double val : vals)
			{
				d.addValue(val, name, categories[i]);
				i++;
			}
		}
		return d;
	}

	public static void main(final String[] args)
	{
		Map<String, List<Double>> data = new HashMap<String, List<Double>>();
		data.put("test", ArrayUtil.toList(new double[] { 0, 1, 3, 2, 0 }));
		data.put("test2", ArrayUtil.toList(new double[] { 2, 1, 0.5, 9, 2 }));
		String[] categories = new String[] { "ene", "mene", "miste", "xy", "z" };

		StackedBarPlot demo = new StackedBarPlot("Stacked Bar Chart Demo 3", "xCaption", "yCaption", data, categories);

		demo.setOpaqueFalse();
		demo.setForegroundColor(Color.GREEN.darker());
		demo.setShadowVisible(false);
		demo.setSeriesColor(0, Color.CYAN);

		SwingUtil.showInDialog(demo, new Dimension(400, 300));

		System.out.println("done");
		System.exit(0);

	}
}
