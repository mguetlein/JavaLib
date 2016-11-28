package org.mg.javalib.freechart;

import java.awt.Color;
import java.awt.Dimension;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.function.Function2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.mg.javalib.util.SwingUtil;

public class FunctionPlot
{
	private Function2D function2d;
	private double lowerBound = 0;
	private double upperBound = 1;
	private int functionSamples = 1000;

	public FunctionPlot(Function2D function2d)
	{
		this.function2d = function2d;
	}

	public ChartPanel getChartPanel()
	{
		XYDataset result = DatasetUtilities.sampleFunction2D(function2d, lowerBound, upperBound,
				functionSamples, "Function");
		JFreeChart chart = ChartFactory.createXYLineChart("Title", "X", "Y", result);
		XYPlot plot = chart.getXYPlot();

		plot.setBackgroundPaint(Color.WHITE);
		plot.setDomainGridlinePaint(Color.GRAY);
		plot.setRangeGridlinePaint(Color.GRAY);

		ChartPanel chartPanel = new ChartPanel(chart);
		return chartPanel;
	}

	public void setUpperBound(double upperBound)
	{
		this.upperBound = upperBound;
	}

	public static void main(String[] args)
	{
		FunctionPlot fp = new FunctionPlot(new Function2D()
		{
			@Override
			public double getValue(double x)
			{
				return Math.pow(x, 2);
			}
		});
		fp.setUpperBound(10.0);
		SwingUtil.showInFrame(fp.getChartPanel(), new Dimension(600, 800));
		System.exit(0);
	}

}
