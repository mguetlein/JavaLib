package freechart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import util.ArrayUtil;
import util.ListUtil;
import util.SwingUtil;

public class StackedBarPlot extends AbstractFreeChartPanel
{

	/**
	 * this is to transform data with total values to additive data 
	 * i.e.
	 * bar "category=active" will have height 5, if input is 
	 * "category=active","series=cluster","value=5" and 
	 * "category=active","series=selected-compound","value=1" 
	 */
	public static LinkedHashMap<String, List<Double>> convertTotalToAdditive(LinkedHashMap<String, List<Double>> data)
	{
		LinkedHashMap<String, List<Double>> additive = new LinkedHashMap<String, List<Double>>();
		int seriesCount = 0;
		for (String key : data.keySet())
		{
			@SuppressWarnings("unused")
			List<Double> additiveVals = ListUtil.clone(data.get(key));
			for (int i = 0; i < seriesCount; i++)
			{
				String oldKey = new ArrayList<String>(data.keySet()).get(i);
				for (int j = 0; j < additiveVals.size(); j++)
					additiveVals.set(j, additiveVals.get(j) - additive.get(oldKey).get(j));
			}
			additive.put(key, additiveVals);
			seriesCount++;
		}
		return additive;
	}

	/**
	 * data = additive 
	 * i.e.
	 * bar "category=active" will have height 5, if input is 
	 * "category=active","series=cluster","value=4" and 
	 * "category=active","series=selected-compound","value=1" 
	 */
	public StackedBarPlot(String title, String xCaption, String yCaption, LinkedHashMap<String, List<Double>> data,
			String[] categories)
	{
		//System.out.println("creating stacked bar plot");
		//System.out.println(ArrayUtil.toString(categories));
		//for (String key : data.keySet())
		//	System.out.println(key + " : " + ListUtil.toString(data.get(key)));

		CategoryDataset dataset = createDataset(data, categories);

		final JFreeChart chart = ChartFactory.createStackedBarChart(title, // chart title
				xCaption, // domain axis label
				yCaption, // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, // the plot orientation
				true, // legend
				true, // tooltips
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

		((CategoryPlot) chart.getPlot()).setRenderer(new StackedBarRenderer()
		{
			public Paint getItemPaint(final int row, final int column)
			{
				if (seriesColors.get(row) != null)
					return seriesColors.get(row)[column];
				else
					return super.getItemPaint(row, column);
			}
		});

		chartPanel.setMinimumDrawHeight(1);//otherwhise the chart gets scaled and the rendering info coordinates for chart mouse listener wont fit
		chartPanel.addChartMouseListener(new ChartMouseListener()
		{
			@Override
			public void chartMouseMoved(ChartMouseEvent chartMouseEvent)
			{
				selectedCategory = getSelected(chartMouseEvent.getTrigger().getX(), chartMouseEvent.getTrigger().getY());
				fireHoverEvent();
			}

			@Override
			public void chartMouseClicked(ChartMouseEvent chartMouseEvent)
			{
				if (SwingUtilities.isLeftMouseButton(chartMouseEvent.getTrigger()))
				{
					selectedCategory = getSelected(chartMouseEvent.getTrigger().getX(), chartMouseEvent.getTrigger()
							.getY());
					fireClickEvent(chartMouseEvent.getTrigger().isControlDown(), chartMouseEvent.getTrigger()
							.getClickCount() > 1);
				}
			}
		});

		setLayout(new BorderLayout());
		add(chartPanel);
	}

	private String getSelected(int x, int y)
	{
		String sel = null;
		EntityCollection entities = chartPanel.getChartRenderingInfo().getEntityCollection();
		if (entities != null)
		{
			//			ChartEntity entity = null;
			//			for (int i = 0; i < entities.getEntityCount(); i++)
			//			{
			//				ChartEntity e = entities.getEntity(i);
			//				if (e instanceof XYItemEntity && e.getArea().getBounds().getMinX() <= x
			//						&& x <= e.getArea().getBounds().getMaxX())
			//				{
			//					entity = e;
			//					break;
			//				}
			//			}
			ChartEntity entity = entities.getEntity(x, y);
			if (entity != null && entity instanceof CategoryItemEntity)
				sel = ((CategoryItemEntity) entity).getColumnKey().toString();
		}
		return sel;
	}

	String selectedCategory;

	public String getSelectedCategory()
	{
		return selectedCategory;
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

	HashMap<Integer, Color[]> seriesColors = new HashMap<Integer, Color[]>();

	public void setSeriesCategoryColors(int series, Color c[])
	{
		seriesColors.put(series, c);
	}

	public static void main(final String[] args)
	{
		LinkedHashMap<String, List<Double>> data = new LinkedHashMap<String, List<Double>>();
		data.put("test", ArrayUtil.toList(new double[] { 0, 1, 3, 2, 0 }));
		data.put("test2", ArrayUtil.toList(new double[] { 2, 1, 3.5, 9, 2 }));
		String[] categories = new String[] { "ene", "mene", "miste", "xy", "z" };

		StackedBarPlot demo = new StackedBarPlot("Stacked Bar Chart Demo 3", "xCaption", "yCaption",
				convertTotalToAdditive(data), categories);

		//		demo.setOpaqueFalse();
		//		demo.setForegroundColor(Color.GREEN.darker());
		//		demo.setShadowVisible(false);
		//		demo.setSeriesColor(0, Color.CYAN);
		//		demo.setSeriesCategoryColors(1, new Color[] { Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA });

		SwingUtil.showInDialog(demo, new Dimension(400, 300));

		System.out.println("done");
		System.exit(0);

	}
}
