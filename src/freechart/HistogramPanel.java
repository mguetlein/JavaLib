package freechart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.IntervalXYDataset;

import util.SwingUtil;

public class HistogramPanel extends AbstractFreeChartPanel
{
	JFreeChart chart;

	public HistogramPanel(String chartTitle, List<String> subtitle, String xAxisLabel, String yAxisLabel,
			List<String> captions, List<double[]> values, int bins)
	{
		IntervalXYDataset dataset = createDataset(captions, values, bins);
		init(chartTitle, subtitle, xAxisLabel, yAxisLabel, dataset, bins, false);
	}

	public HistogramPanel(String chartTitle, List<String> subtitle, String xAxisLabel, String yAxisLabel,
			String caption, double[] values, int bins)
	{
		this(chartTitle, subtitle, xAxisLabel, yAxisLabel, caption, values, bins, null);
	}

	public HistogramPanel(String chartTitle, List<String> subtitle, String xAxisLabel, String yAxisLabel,
			String caption, double[] values, int bins, double[] minMax)
	{
		this(chartTitle, subtitle, xAxisLabel, yAxisLabel, caption, values, bins, minMax, false);
	}

	public HistogramPanel(String chartTitle, List<String> subtitle, String xAxisLabel, String yAxisLabel,
			String caption, double[] values, int bins, double[] minMax, boolean hideLegend)
	{
		IntervalXYDataset dataset = createDataset(caption, values, bins, minMax);
		init(chartTitle, subtitle, xAxisLabel, yAxisLabel, dataset, bins, hideLegend);
	}

	private void init(String chartTitle, List<String> subtitle, String xAxisLabel, String yAxisLabel,
			IntervalXYDataset dataset, int bins, boolean hideLegend)
	{
		chart = createChart(chartTitle, subtitle, xAxisLabel, yAxisLabel, dataset);
		chart.setBackgroundPaint(new Color(255, 255, 255, 0));
		chartPanel = new ChartPanel(chart);
		chartPanel.setOpaque(false);

		chartPanel.addChartMouseListener(new ChartMouseListener()
		{
			@Override
			public synchronized void chartMouseMoved(ChartMouseEvent chartMouseEvent)
			{
				double[] sel = getInterval(chartMouseEvent.getTrigger().getX(), chartMouseEvent.getTrigger().getY());
				selectedMin = sel[0];
				selectedMax = sel[1];
				fireHoverEvent();
			}

			@Override
			public void chartMouseClicked(ChartMouseEvent chartMouseEvent)
			{
				if (SwingUtilities.isLeftMouseButton(chartMouseEvent.getTrigger()))
				{
					double sel[] = getInterval(chartMouseEvent.getTrigger().getX(), chartMouseEvent.getTrigger().getY());
					selectedMin = sel[0];
					selectedMax = sel[1];
					fireClickEvent(chartMouseEvent.getTrigger().isControlDown());
				}
			}
		});

		setBackground(Color.WHITE);
		setLayout(new BorderLayout());
		if (hideLegend)
			chart.removeLegend();
		add(chartPanel);
	}

	private double[] getInterval(int x, int y)
	{
		double[] sel = new double[] { 1.0, 0.0 };
		EntityCollection entities = chartPanel.getChartRenderingInfo().getEntityCollection();
		if (entities != null)
		{
			ChartEntity entity = entities.getEntity(x, y);
			if (entity != null && entity instanceof XYItemEntity)
			{
				XYItemEntity e = (XYItemEntity) entity;
				HistogramDataset d = ((HistogramDataset) ((XYPlot) chart.getPlot()).getDataset(0));
				sel[0] = d.getStartXValue(e.getSeriesIndex(), e.getItem());
				sel[1] = d.getEndXValue(e.getSeriesIndex(), e.getItem());
			}
		}
		return sel;
	}

	double selectedMin = 1.0;
	double selectedMax = 0.0;

	public double getSelectedMin()
	{
		return selectedMin;
	}

	public double getSelectedMax()
	{
		return selectedMax;
	}

	private IntervalXYDataset createDataset(String caption, double[] values, int bins, double[] minMax)
	{
		List<String> captions = new ArrayList<String>();
		captions.add(caption);
		List<double[]> v = new ArrayList<double[]>();
		v.add(values);
		return createDataset(captions, v, bins, minMax);
	}

	public JFreeChart getChart()
	{
		return chart;
	}

	private IntervalXYDataset createDataset(List<String> captions, List<double[]> values, int bins)
	{
		return createDataset(captions, values, bins, null);
	}

	private IntervalXYDataset createDataset(List<String> captions, List<double[]> values, int bins, double[] minMax)
	{
		HistogramDataset dataset = new HistogramDataset();

		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;

		if (minMax != null)
		{
			min = minMax[0];
			max = minMax[1];
		}
		else
		{
			for (double[] array : values)
			{
				for (double d : array)
				{
					if (d < min)
						min = d;
					if (d > max)
						max = d;
				}
			}
		}
		if (max == min)
		{
			double e = Math.abs(min) * 0.1;
			if (e == 0)
				e = 1;
			min -= e;
			max += e;
		}
		else if (max - min < 0.001)
		{
			double e = Math.abs(min) * 0.0001;
			if (e == 0)
				e = 1;
			min -= e;
			max += e;
		}
		if (max < min)
		{
			//no values
			min = 0;
			max = 0;
		}
		HashMap<Double, Object> unique = new HashMap<Double, Object>();
		for (double[] d : values)
			for (double e : d)
				unique.put(e, null);
		bins = Math.max(5, Math.min(20, unique.size()));
		for (int i = values.size() - 1; i >= 0; i--)
		{
			String c = "Data" + (i + 1);
			if (captions != null)
				c = captions.get(i);
			dataset.addSeries(c, values.get(i), bins, min, max);
		}

		return dataset;
	}

	public void updateSubtitle(List<String> subtitle)
	{
		if (subtitle != null)
		{
			List<Title> subtitles = new ArrayList<Title>();
			for (String s : subtitle)
			{
				if (s != null)
				{
					Title t = new TextTitle(s);
					subtitles.add(t);
				}
			}
			if (subtitles.size() > 0)
				chart.setSubtitles(subtitles);
		}
	}

	private JFreeChart createChart(String title, List<String> subtitle, String xAxisLabel, String yAxisLabel,
			IntervalXYDataset dataset)
	{
		JFreeChart chart = ChartFactory.createHistogram(title, xAxisLabel, yAxisLabel, dataset,
				PlotOrientation.VERTICAL, true, false, false);

		if (subtitle != null)
		{
			List<Title> subtitles = new ArrayList<Title>();
			for (String s : subtitle)
			{
				if (s != null)
				{
					Title t = new TextTitle(s);
					subtitles.add(t);
				}
			}
			if (subtitles.size() > 0)
				chart.setSubtitles(subtitles);
		}
		//		if (dataset.getSeriesCount() > 1)
		//			chart.getXYPlot().setForegroundAlpha(0.33f);

		// chart.getXYPlot().getRangeAxis().setRange(0, 10);

		return chart;
	}

	public static void main(String args[])
	{
		//double d[][] = new double[][] { { 11.850000000000003, 11.850000000000007, 11.849999999999998 } };
		//, { -5 }, { 0.0001 }, { 4 }, { 2324323 }, { -0.45 }, { 0 } };
		//		for (double[] e : d)

		List<double[]> vals = new ArrayList<double[]>();
		List<String> captions = new ArrayList<String>();
		captions.add("a");
		vals.add(new double[] { 1, 2, 3, 1, 2, 3, 4, 5, 5, 4, 2, 4, 4, 5, 6, 3, 2, 1, 2, 3, 4, 5, 6, 5, 4, 3, 1 });
		captions.add("b");
		vals.add(new double[] { 1, 2, 7 });

		{
			HistogramPanel p = new HistogramPanel(null, null, "property", "#compounds", captions, vals, 20);

			//			Plot plot = p.getChart().getPlot();
			//			XYPlot p2 = (XYPlot) plot;
			//			Axis a = p2.getRangeAxis();

			p.setIntegerTickUnits();

			//		p.setForegroundColor(Color.GREEN);
			p.setShadowVisible(false);
			SwingUtil.showInDialog(p);
		}
		//		HistogramPanel p2 = new HistogramPanel(null, null, "property", "#compounds", "", new double[] { 1, 3, 2, 4, 1,
		//				2, 3, 4, 5, 3, 2, 4, 5, 3, 2 }, 5, null, true);
		//		p2.setShadowVisible(false);
		//		SwingUtil.showInDialog(p2);

	}
}
