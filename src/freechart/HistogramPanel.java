package freechart;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.IntervalXYDataset;

public class HistogramPanel extends JPanel
{
	JFreeChart chart;

	public HistogramPanel(String chartTitle, List<String> subtitle, String xAxisLabel, String yAxisLabel,
			List<String> captions, List<double[]> values, int bins)
	{
		IntervalXYDataset dataset = createDataset(captions, values, bins);
		init(chartTitle, subtitle, xAxisLabel, yAxisLabel, dataset, bins);
	}

	public HistogramPanel(String chartTitle, List<String> subtitle, String xAxisLabel, String yAxisLabel, String caption,
			double[] values, int bins)
	{
		this(chartTitle, subtitle, xAxisLabel, yAxisLabel, caption, values, bins, null);
	}

	public HistogramPanel(String chartTitle, List<String> subtitle, String xAxisLabel, String yAxisLabel, String caption,
			double[] values, int bins, double[] minMax)
	{
		IntervalXYDataset dataset = createDataset(caption, values, bins, minMax);
		init(chartTitle, subtitle, xAxisLabel, yAxisLabel, dataset, bins);
	}

	private void init(String chartTitle, List<String> subtitle, String xAxisLabel, String yAxisLabel,
			IntervalXYDataset dataset, int bins)
	{
		chart = createChart(chartTitle, subtitle, xAxisLabel, yAxisLabel, dataset);

		ChartPanel chartPanel = new ChartPanel(chart);

		setLayout(new BorderLayout());
		add(chartPanel);

	}

	// private IntervalXYDataset createDataset(String caption, double[] values, int bins)
	// {
	// return createDataset(caption, values, bins, null);
	// }

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
		JFreeChart chart = ChartFactory.createHistogram(title, xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL,
				true, false, false);

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
		chart.getXYPlot().setForegroundAlpha(0.33f);

		// chart.getXYPlot().getRangeAxis().setRange(0, 10);

		return chart;
	}
}
