package org.mg.javalib.freechart;

/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * -------------------
 * HistogramDemo2.java
 * -------------------
 * (C) Copyright 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: HistogramDemo2.java,v 1.1 2005/04/28 16:29:15 harrym_nu Exp $
 *
 * Changes
 * -------
 * 01-Mar-2004 : Version 1 (DG);
 *
 */

import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.swing.JDialog;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.IntervalXYDataset;

public class HistogramDemo extends JDialog
{

	public boolean exitAndBack = false;

	// private boolean exitedWithKeys = false;

	JFreeChart chart;

	public HistogramDemo(Frame owner, String chartTitle, List<String> subtitle, String dialogTitle,
			String xAxisLabel, String yAxisLabel, List<String> captions, List<double[]> values,
			int bins)
	{
		this(owner, chartTitle, subtitle, dialogTitle, xAxisLabel, yAxisLabel, captions, values,
				bins, false);
	}

	public HistogramDemo(Frame owner, String chartTitle, List<String> subtitle, String dialogTitle,
			String xAxisLabel, String yAxisLabel, List<String> captions, List<double[]> values,
			int bins, boolean yScaleLogarithmic)
	{
		super(owner, dialogTitle);
		setModal(true);
		IntervalXYDataset dataset = createDataset(captions, values, bins);
		chart = createChart(chartTitle, subtitle, xAxisLabel, yAxisLabel, dataset,
				yScaleLogarithmic);

		ChartPanel chartPanel = new ChartPanel(chart);

		chartPanel.setPreferredSize(new java.awt.Dimension(800, 500));
		setContentPane(chartPanel);

		KeyListener l = new KeyAdapter()
		{
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_RIGHT)
				{
					// exitedWithKeys = true;
					HistogramDemo.this.setVisible(false);
				}
				else if (e.getKeyCode() == KeyEvent.VK_LEFT)
				{
					// exitedWithKeys = true;
					exitAndBack = true;
					HistogramDemo.this.setVisible(false);
				}
			}
		};
		addKeyListener(l);
		getContentPane().addKeyListener(l);
		chartPanel.addKeyListener(l);

		// addWindowListener(new WindowAdapter()
		// {
		// public void windowClosing(WindowEvent e)
		// {
		// System.exit(0);
		// }
		// });

		pack();
		setLocationRelativeTo(null);
	}

	private IntervalXYDataset createDataset(List<String> captions, List<double[]> values, int bins)
	{
		HistogramDataset dataset = new HistogramDataset();

		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;

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

		for (int i = 0; i < values.size(); i++)
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

	private JFreeChart createChart(String title, List<String> subtitle, String xAxisLabel,
			String yAxisLabel, IntervalXYDataset dataset, boolean logrithmicYAxis)
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
		chart.getXYPlot().setForegroundAlpha(0.33f);

		if (logrithmicYAxis)
			chart.getXYPlot().setDomainAxis(
					new LogarithmicAxis(chart.getXYPlot().getDomainAxis().getLabel()));

		return chart;
	}

	public static boolean showHistogram(double[] values, int bins)
	{
		Vector<double[]> v = new Vector<double[]>();
		v.add(values);
		return showHistogram(v, bins);
	}

	public static boolean showHistogram(Vector<double[]> values, int bins)
	{
		return showHistogram(null, null, values, bins);
	}

	public static boolean showHistogram(String title, Vector<String> captions,
			Vector<double[]> values, int bins)
	{
		return showHistogram(title, captions, null, null, values, bins);
	}

	public static boolean showHistogram(String title, Vector<String> captions, String xAxisLabel,
			String yAxisLabel, Vector<double[]> values, int bins)
	{
		return showHistogram(title, title, captions, xAxisLabel, yAxisLabel, values, bins);
	}

	public static boolean showHistogram(String chartTitle, String dialogTitle,
			Vector<String> captions, String xAxisLabel, String yAxisLabel, Vector<double[]> values,
			int bins)
	{
		return showHistogram(chartTitle, (String) null, dialogTitle, captions, xAxisLabel,
				yAxisLabel, values, bins);
	}

	public static boolean showHistogram(String chartTitle, String subtitle, String dialogTitle,
			Vector<String> captions, String xAxisLabel, String yAxisLabel, Vector<double[]> values,
			int bins)
	{
		List<String> l = new ArrayList<String>();
		l.add(subtitle);
		return showHistogram(chartTitle, l, dialogTitle, captions, xAxisLabel, yAxisLabel, values,
				bins);
	}

	public static boolean showHistogram(String chartTitle, List<String> subtitles,
			String dialogTitle, Vector<String> captions, String xAxisLabel, String yAxisLabel,
			Vector<double[]> values, int bins)
	{
		HistogramDemo demo = new HistogramDemo(null, chartTitle, subtitles, dialogTitle,
				xAxisLabel, yAxisLabel, captions, values, bins);
		demo.setVisible(true);
		return demo.exitAndBack;
	}

	public static void main(String[] args)
	{
		Random r = new Random();
		double values[] = new double[10000];
		for (int i = 0; i < 10000; i++)
		{
			values[i] = r.nextDouble();
		}

		double values2[] = new double[10000];
		for (int i = 0; i < 10000; i++)
		{
			values2[i] = (r.nextDouble() + r.nextDouble()) / 2;
		}

		double values3[] = new double[10000];
		for (int i = 0; i < 10000; i++)
		{
			values3[i] = (r.nextDouble() + r.nextDouble() + r.nextDouble() + r.nextDouble()) / 4;
		}

		double values4[] = new double[10000];
		for (int i = 0; i < 10000; i++)
		{
			values4[i] = (r.nextDouble() + r.nextDouble() + r.nextDouble() + r.nextDouble()
					+ r.nextDouble() + r.nextDouble() + r.nextDouble() + r.nextDouble()) / 8;
		}

		Vector<double[]> v = new Vector<double[]>();
		v.add(values4);
		v.add(values3);
		v.add(values2);
		v.add(values);

		boolean b = showHistogram(v, 25);

		System.out.println("demo close, exiting program " + b);
		System.exit(0);
	}

}
