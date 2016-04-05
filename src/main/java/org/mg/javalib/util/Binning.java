package org.mg.javalib.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.inference.TestUtils;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.mg.javalib.freechart.HistogramPanel;

public class Binning implements Serializable
{
	private static final long serialVersionUID = 1L;

	double min;
	double max;
	double width;
	int bins;
	long counts[];
	double values[];

	public Binning(double[] values, int bins, boolean acceptNullBins)
	{
		this.values = values;
		double[] vals = Arrays.copyOf(values, values.length);
		Arrays.sort(vals);
		min = vals[0];
		max = vals[vals.length - 1];
		doBin(vals, bins, acceptNullBins);
	}

	private void doBin(double[] sortedValues, int bins, boolean acceptNullBins)
	{
		this.bins = bins;
		width = (max - min) / (double) bins;
		counts = new long[bins];
		double maxT = min + width;
		int valueIndex = 0;
		for (int i = 0; i < bins; i++)
		{
			while (valueIndex < sortedValues.length && sortedValues[valueIndex] <= maxT)
			{
				counts[i]++;
				valueIndex++;
			}
			if (!acceptNullBins && counts[i] == 0)
			{
				doBin(sortedValues, bins - 1, acceptNullBins);
				return;
			}
			maxT += width;
		}
	}

	public int getBin(double value)
	{
		if (value < min || value > max)
			return -1;
		if (value == max)
			return bins - 1;
		if (value == min)
			return 0;
		int bin = (int) ((value - min) / width);
		if (bin >= bins || bin < 0)
			throw new IllegalStateException("bin:" + bin + " for value " + value + "\n" + this);
		return bin;
	}

	public long[] getAllCounts()
	{
		return counts;
	}

	public long[] getSelectedCounts(double value)
	{
		int bin = getBin(value);
		if (bin == -1)
			return null;
		long[] l = new long[bins];
		l[bin]++;
		return l;
	}

	public long[] getSelectedCounts(double values[])
	{
		long[] l = new long[bins];
		for (double v : values)
		{
			int bin = getBin(v);
			if (bin == -1)
				return null;
			l[bin]++;
		}
		return l;
	}

	public String toString()
	{
		String s = "bins:   " + bins + "\n";
		s += "counts: " + ArrayUtil.toString(counts) + "\n";
		s += "min: " + min + "\n";
		s += "max: " + max + "\n";
		s += "width: " + width;
		return s;
	}

	public ChartPanel plot()
	{
		return plot(null);
	}

	public ChartPanel plot(Double val)
	{
		HistogramPanel p = new HistogramPanel(null, null, "x", "y", ListUtil.createList("Bins"),
				ListUtil.createList(values), bins);
		JFreeChart chart = p.getChart();
		XYPlot plot = (XYPlot) chart.getPlot();

		chart.removeLegend();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setRangeGridlinePaint(Color.GRAY);
		plot.setDomainGridlinePaint(Color.GRAY);
		chart.setBackgroundPaint(new Color(0, 0, 0, 0));

		XYBarRenderer render = new XYBarRenderer();
		render.setShadowVisible(false);
		StandardXYBarPainter painter = new StandardXYBarPainter();
		render.setBarPainter(painter);
		render.setSeriesPaint(0, new Color(0, 0, 0, 0));
		render.setDrawBarOutline(true);
		render.setSeriesOutlinePaint(0, Color.BLACK);
		plot.setRenderer(render);

		if (val != null)
		{
			JFreeChart c = p.getChartPanel().getChart();
			ValueMarker marker = new ValueMarker(val);
			marker.setPaint(Color.RED);
			marker.setStroke(new BasicStroke(2.0F));
			plot.addDomainMarker(marker);
		}
		return p.getChartPanel();
	}

	public static void main(String[] args)
	{
		//double d[] = new double[] { -1, 0, 0, 0, 1, 1, 1, 2, 2, 3, 4, 5, 6, 6, 6, 7, 7, 7, 8, 8, 8, 9, 10 };
		double[] d = new NormalDistribution(0, 5).sample(30);
		d[d.length - 1] = 20;
		for (boolean b : new boolean[] { true, false })
		{
			Binning bin = new Binning(d, 20, b);
			System.out.println(bin);
			long l1[] = bin.getAllCounts();
			long l2[] = bin.getSelectedCounts(20);
			List<Long> l1n = new ArrayList<Long>();
			List<Long> l2n = new ArrayList<Long>();
			for (int i = 0; i < l1.length; i++)
			{
				if (l1[i] > 0 || l2[i] > 0)
				{
					l1n.add(l1[i]);
					l2n.add(l2[i]);
				}
			}
			System.out.println(ListUtil.toString(l1n));
			System.out.println(ListUtil.toString(l2n));
			double p = TestUtils.chiSquareTestDataSetsComparison(
					ArrayUtil.toPrimitiveLongArray(ListUtil.toArray(l1n)),
					ArrayUtil.toPrimitiveLongArray(ListUtil.toArray(l2n)));
			System.out.println(p);

			SwingUtil.showInFrame(bin.plot());
		}
	}
}
