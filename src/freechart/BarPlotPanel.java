package freechart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import util.ArrayUtil;
import util.SwingUtil;

public class BarPlotPanel extends JPanel
{

	Map<String, List<Double>> data = new HashMap<String, List<Double>>();

	public BarPlotPanel(String title, String yAxisLabel, double values[], String names[])
	{
		this(title, yAxisLabel, ArrayUtil.toList(values), ArrayUtil.toList(names));
	}

	public BarPlotPanel(String title, String yAxisLabel, List<Double> values, List<String> names)
	{
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < names.size(); i++)
			dataset.setValue(values.get(i), "", names.get(i) + "");

		JFreeChart chart = ChartFactory.createBarChart(title, // chart title
				null, // domain axis label
				yAxisLabel, // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, // the plot orientation
				false, // legend
				false, // tooltips
				false // urls
				);
		//		CategoryPlot plot = chart.getCategoryPlot();
		//		((BarRenderer) plot.getRenderer()).setShadowVisible(false);
		//		((NumberAxis) plot.getRangeAxis()).setTickUnit(new NumberTickUnit(1));

		chart.setBackgroundPaint(new Color(255, 255, 255, 0));
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setOpaque(false);
		chartPanel.setPreferredSize(null);
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);
		add(chartPanel);
	}

	// ((NumberAxis) plot.getDomainAxis()).setTickUnit(new NumberTickUnit(1));

	// plot.setRangeAxis(new LogarithmicAxis("bla"));

	// final CategoryItemRenderer renderer = new ExtendedStackedBarRenderer();
	// renderer.setToolTipGenerator(new StandardCategoryToolTipGenerator());
	// plot.setRenderer(renderer);

	// ValueAxis rangeAxis = plot.getRangeAxis();
	// rangeAxis.setLowerMargin(0.15);
	// rangeAxis.setUpperMargin(0.15);

	public static void main(final String[] args)
	{
		// BarPlot demo = new BarPlot("Stacked Bar Chart Demo 3");
		// demo.addData("test", new double[] { 0, 1, 2, 3 });
		// demo.addData("test2", new double[] { 2, 1, 0.5, 9 });
		// demo.plot();

		Map<String, List<Double>> data = new HashMap<String, List<Double>>();

		SwingUtil.showInDialog(new BarPlotPanel("Title", "Axis-Name", new double[] { 3, 5, 7, 2 }, new String[] {
				"asdf", "ene", "mene", "miste" }));

		System.out.println("done");
		System.exit(0);

	}
}
