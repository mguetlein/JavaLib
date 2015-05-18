package org.mg.javalib.datamining;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.batik.ext.awt.geom.Polygon2D;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.Layer;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ShapeUtilities;
import org.mg.javalib.freechart.FreeChartUtil;
import org.mg.javalib.util.ColorUtil;
import org.mg.javalib.util.DoubleKeyHashMap;
import org.mg.javalib.util.SwingUtil;

public class ResultSetLinePlot
{
	ResultSet set;
	String valueP[];
	String seriesP;
	String categoryP;

	String title = "Title";
	String yAxisLabel = null;
	String xAxisLabel = "Categories";
	boolean includeLegend = true;
	boolean tooltips = false;

	public static enum XLabelsRotation
	{
		normal, diagonal, vertical
	}

	XLabelsRotation rotateXLabels = XLabelsRotation.normal;
	double[] yAxisRange = null;
	HashMap<String, double[]> yAxisRangePerValue = new HashMap<>();
	HashMap<String, Double> yAxisTickUnitsPerValue = new HashMap<>();
	HashMap<String, HashMap<String, String>> drawShape = new HashMap<>();

	public DoubleKeyHashMap<String, String, String> markers = new DoubleKeyHashMap<>();

	public ResultSetLinePlot(ResultSet set, String valueP, String seriesP, String categoryP)
	{
		this(set, new String[] { valueP }, seriesP, categoryP);
	}

	public ResultSetLinePlot(ResultSet set, String valueP[], String seriesP, String categoryP)
	{
		this.set = set;
		this.valueP = valueP;
		this.seriesP = seriesP;
		this.categoryP = categoryP;
	}

	public static class DiamondItem implements Stroke
	{
		int size = 5;
		boolean diamond;

		public DiamondItem(int size, boolean diamond)
		{
			this.size = size;
			this.diamond = diamond;
		}

		@Override
		public Shape createStrokedShape(Shape p)
		{
			Shape p2;
			if (diamond)
			{
				Polygon2D p1 = new Polygon2D();
				p1.addPoint((float) p.getBounds2D().getX() + 0, (float) p.getBounds2D().getY() - size / 2.0f);
				p1.addPoint((float) p.getBounds2D().getX() + size / 2.0f, (float) p.getBounds2D().getY());
				p1.addPoint((float) p.getBounds2D().getX(), (float) p.getBounds2D().getY() + size / 2.0f);
				p1.addPoint((float) p.getBounds2D().getX() - size / 2.0f, (float) p.getBounds2D().getY());
				//p1.translate((int) p.getBounds2D().getX(), (int) p.getBounds2D().getY());
				p2 = p1;
			}
			else
			{
				p2 = new Ellipse2D.Float((float) p.getBounds2D().getX() - size / 2.0f, (float) p.getBounds2D().getY()
						- size / 2.0f, size, size);
			}
			return p2;
		}
	}

	//	private void addAnnotation(CategoryPlot plot, DefaultCategoryDataset dataset, int column, int row, Color cols[],
	//			Color back, int thick, boolean shape, String valueProperty)
	//	{
	//		CategoryLineAnnotation anno1 = new CategoryLineAnnotation(dataset.getColumnKey(column), dataset.getValue(row,
	//				column).doubleValue(), dataset.getColumnKey(column), dataset.getValue(row, column).doubleValue(),
	//				ColorUtil.transparent(cols[row], 255), new DiamondItem(thick, shape));
	//		plot.addAnnotation(anno1);
	//
	//		int seriesIdx = -1;
	//		for (int j = 0; j < dataset.getRowCount(); j++)
	//		{
	//			String seriesValue = dataset.getRowKey(j).toString();
	//			String categoryValue = dataset.getColumnKey(column).toString();
	//			if ((drawShape.containsKey(valueProperty) && seriesValue.equals(drawShape.get(valueProperty).get(
	//					categoryValue))))
	//			{
	//				seriesIdx = j;
	//				break;
	//			}
	//		}
	//		int minus = 2;
	//		if (seriesIdx == row)
	//			minus = 5;
	//
	//		anno1 = new CategoryLineAnnotation(dataset.getColumnKey(column), dataset.getValue(row, column).doubleValue(),
	//				dataset.getColumnKey(column), dataset.getValue(row, column).doubleValue(), ColorUtil.transparent(back,
	//						255), new DiamondItem(thick - minus, shape));
	//		plot.addAnnotation(anno1);
	//	}

	public ChartPanel getChartPanel()
	{
		JFreeChart c = null;
		CombinedDomainCategoryPlot combinedP = null;

		for (final String valueProperty : valueP)
		{
			final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
			for (int i = 0; i < set.getNumResults(); i++)
				dataset.addValue((Double) set.getResultValue(i, valueProperty), set.getResultValue(i, seriesP)
						.toString(), set.getResultValue(i, categoryP).toString());
			JFreeChart chart = ChartFactory.createLineChart(title, xAxisLabel,
					(yAxisLabel == null) ? set.getNiceProperty(valueProperty) : yAxisLabel, dataset,
					PlotOrientation.VERTICAL, includeLegend, tooltips, false /*urls*/);

			chart.getPlot().setBackgroundPaint(Color.WHITE);
			((CategoryPlot) chart.getPlot()).setRangeGridlinePaint(Color.GRAY);
			//			((CategoryPlot) chart.getPlot()).setDomainGridlinesVisible(true);
			//			((CategoryPlot) chart.getPlot()).setDomainGridlinePaint(Color.GRAY);
			chart.setBackgroundPaint(new Color(0, 0, 0, 0));
			CategoryPlot plot = (CategoryPlot) chart.getPlot();

			Color cols[];
			Shape shapes[] = null;
			if (dataset.getRowCount() == 2)
			{
				cols = new Color[] { ColorUtil.bright(FreeChartUtil.COLORS[0]), ColorUtil.dark(FreeChartUtil.COLORS[1]) };
				shapes = new Shape[] { ShapeUtilities.createDiamond(4.5f), new Ellipse2D.Float(-3f, -3f, 6f, 6f) };
			}
			else if (dataset.getRowCount() == 3)
				cols = new Color[] { ColorUtil.bright(FreeChartUtil.COLORS[0]),
						ColorUtil.dark(FreeChartUtil.COLORS[1]), ColorUtil.mediumBrightness(FreeChartUtil.COLORS[2]) };
			else
				cols = FreeChartUtil.COLORS;

			//			for (int i = 0; i < dataset.getColumnCount(); i++)
			//			{
			//				//							int seriesIdx = -1;
			//				//							for (int j = 0; j < dataset.getRowCount(); j++)
			//				//							{
			//				//								String seriesValue = dataset.getRowKey(j).toString();
			//				//								String categoryValue = dataset.getColumnKey(i).toString();
			//				//								if ((drawShape.containsKey(valueProperty) && seriesValue.equals(drawShape.get(valueProperty).get(
			//				//										categoryValue))))
			//				//								{
			//				//									seriesIdx = j;
			//				//									break;
			//				//								}
			//				//							}
			//				//			
			//				//							if (seriesIdx != -1)
			//				//							{
			//				CategoryLineAnnotation anno = new CategoryLineAnnotation(dataset.getColumnKey(i), dataset
			//						.getValue(0, i).doubleValue(), dataset.getColumnKey(i), dataset.getValue(1, i).doubleValue(),
			//						ColorUtil.transparent(Color.GRAY, 200), new BasicStroke(1.0f, BasicStroke.CAP_ROUND,
			//								BasicStroke.JOIN_ROUND, 5.0f, new float[] { 2.0f, 2.0f }, 5.0f));
			//				int thick = 10;
			//				Color back = new Color(255, 255, 255);
			//
			//				//				CategoryLineAnnotation anno = new CategoryLineAnnotation(dataset.getColumnKey(i), dataset
			//				//						.getValue(0, i).doubleValue(), dataset.getColumnKey(i), dataset.getValue(1, i).doubleValue(),
			//				//						ColorUtil.transparent(back, 200), new BasicStroke(thick, BasicStroke.CAP_ROUND,
			//				//								BasicStroke.JOIN_ROUND));
			//				plot.addAnnotation(anno);
			//
			//				if (dataset.getValue(0, i).doubleValue() > dataset.getValue(1, i).doubleValue())
			//				{
			//					addAnnotation(plot, dataset, i, 1, cols, back, thick, true, valueProperty);
			//					addAnnotation(plot, dataset, i, 0, cols, back, thick, false, valueProperty);
			//				}
			//				else
			//				{
			//					addAnnotation(plot, dataset, i, 0, cols, back, thick, false, valueProperty);
			//					addAnnotation(plot, dataset, i, 1, cols, back, thick, true, valueProperty);
			//
			//				}
			//			}

			LineAndShapeRenderer renderer = new LineAndShapeRenderer()
			{
				@Override
				public boolean getItemShapeVisible(int series, int item)
				{
					//					//					return dataset.getValue(series, item).doubleValue() > dataset.getValue(1 - series, item)
					//					//							.doubleValue();
					//
					//					String seriesValue = dataset.getRowKey(series).toString();
					//					String categoryValue = dataset.getColumnKey(item).toString();
					//					return (drawShape.containsKey(valueProperty) && seriesValue.equals(drawShape.get(valueProperty)
					//							.get(categoryValue)));
					//					//					return new Random().nextBoolean();

					return false;
				}

				@Override
				public boolean getItemShapeFilled(int series, int item)
				{
					String seriesValue = dataset.getRowKey(series).toString();
					String categoryValue = dataset.getColumnKey(item).toString();
					return (drawShape.containsKey(valueProperty) && seriesValue.equals(drawShape.get(valueProperty)
							.get(categoryValue)));
				}

				@Override
				public Boolean getSeriesLinesVisible(int series)
				{
					return true;
				}

			};

			//			Polygon p1 = new Polygon();
			//			p1.addPoint(0, -9);
			//			p1.addPoint(-3, -5);
			//			p1.addPoint(3, -5);
			//			renderer.setBaseShape(p1);

			plot.setRenderer(renderer);
			//			LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
			//			renderer.set

			if (markers != null && markers.containsKey(valueProperty))
				for (String category : markers.keySet2(valueProperty))
				{
					CategoryMarker marker = new CategoryMarker(category);
					marker.setOutlinePaint(null);
					marker.setPaint(new Color(150, 150, 150));
					marker.setDrawAsLine(true);
					marker.setLabel(" " + markers.get(valueProperty, category));

					//				marker.setLabelFont(plot.getDomainAxis().getLabelFont());
					//				marker.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
					//				marker.setLabelOffset(new RectangleInsets(10.0, 0.0, 0.0, 0.0));

					marker.setLabelAnchor(RectangleAnchor.BOTTOM);
					marker.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
					marker.setLabelOffsetType(LengthAdjustmentType.CONTRACT);

					plot.addDomainMarker(marker, Layer.BACKGROUND);
				}
			//
			//			marker = new CategoryMarker("elephant");
			//			marker.setOutlinePaint(null);
			//			marker.setPaint(new Color(150, 150, 150));
			//			plot.addDomainMarker(marker, Layer.BACKGROUND);

			//			CategoryPointerAnnotation p = new CategoryPointerAnnotation("", "mouse", 0.5, Math.toRadians(270.0));
			//			p.setPaint(Color.RED);
			//			plot.addAnnotation(p);

			//			for (int i = 0; i < cols.length; i++)
			//				cols[i] = ColorUtil.grayscale(cols[i]);

			for (int i = 0; i < dataset.getRowCount(); i++)
			{
				//				renderer.setSeriesShape(i, p1);

				//				double ratio = i / (double) (dataset.getRowCount() - 1);
				//				System.out.println(i + " " + ratio);
				//				//			renderer.setSeriesPaint(i, ColorGradient.get2ColorGradient(ratio, Color.BLACK, Color.LIGHT_GRAY));
				//				//				renderer.setSeriesPaint(i, new ColorGradient(new Color(200, 0, 0), Color.LIGHT_GRAY, new Color(150,
				//				//						150, 255)).getColor(ratio));
				//				renderer.setSeriesPaint(i, new ColorGradient(new Color(0, 0, 200), Color.LIGHT_GRAY, new Color(150,
				//						150, 255)).getColor(ratio));
				renderer.setSeriesPaint(i, cols[i]);

				renderer.setSeriesOutlinePaint(i, Color.BLACK);
				renderer.setSeriesFillPaint(i, Color.BLACK);

				if (shapes != null)
					renderer.setSeriesShape(i, shapes[i]);

				float thick = 1.0f;

				//				if ((dataset.getRowCount() == 2 || dataset.getRowCount() == 3) && i == 1)
				//					renderer.setSeriesStroke(i, new BasicStroke(thick, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
				//							1.0f, new float[] { 6f * thick, 1.5f * thick }, 3f * thick));
				//				else if ((dataset.getRowCount() == 2 || dataset.getRowCount() == 3) && i == 2)
				//					renderer.setSeriesStroke(i, new BasicStroke(thick, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
				//							1.0f, new float[] { 1.5f * thick, 1.5f * thick }, 3f * thick));
				//				else
				renderer.setSeriesStroke(i, new BasicStroke(thick, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

				//, 1.0f,new float[] { dash, 2 * thick }, dash / 2));

			}

			if (yAxisRange != null)
				((NumberAxis) plot.getRangeAxis()).setRange(yAxisRange[0], yAxisRange[1]);
			if (yAxisRangePerValue.containsKey(valueProperty))
				((NumberAxis) plot.getRangeAxis()).setRange(yAxisRangePerValue.get(valueProperty)[0],
						yAxisRangePerValue.get(valueProperty)[1]);
			if (yAxisTickUnitsPerValue.containsKey(valueProperty))
				((NumberAxis) plot.getRangeAxis()).setTickUnit(new NumberTickUnit(yAxisTickUnitsPerValue
						.get(valueProperty)));

			//((NumberAxis) plot.getRangeAxis()).setAutoRangeIncludesZero(true);

			CategoryAxis axis = plot.getDomainAxis();
			if (rotateXLabels == XLabelsRotation.diagonal)
				axis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
			if (rotateXLabels == XLabelsRotation.vertical)
				axis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);

			//			axis.setTickLabelsVisible(true);

			if (c == null)
			{
				c = chart;
			}
			else if (combinedP == null)
			{
				combinedP = new CombinedDomainCategoryPlot(new CategoryAxis(xAxisLabel));
				combinedP.setOrientation(PlotOrientation.VERTICAL);
				combinedP.add(c.getCategoryPlot());
				combinedP.add(chart.getCategoryPlot());
				c = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, combinedP, false);
				c.setBackgroundPaint(new Color(0, 0, 0, 0));
				c.addLegend(chart.getLegend());
				axis = plot.getDomainAxis();
				if (rotateXLabels == XLabelsRotation.diagonal)
					axis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
				if (rotateXLabels == XLabelsRotation.vertical)
					axis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
			}
			else
			{
				combinedP.add(chart.getCategoryPlot());
			}
		}
		ChartPanel cp = new ChartPanel(c);
		return cp;
	}

	public void addMarker(String valueProperty, String category, String name)
	{
		markers.put(valueProperty, category, name);
	}

	public void toSVGFile(String svgfilename, Dimension dim)
	{
		FreeChartUtil.toSVGFile(svgfilename, getChartPanel(), dim);
	}

	public void setYAxisRange(double min, double max)
	{
		yAxisRange = new double[] { min, max };
	}

	public void setYAxisRange(String valueProperty, double min, double max)
	{
		yAxisRangePerValue.put(valueProperty, new double[] { min, max });
	}

	public void setYAxisTickUnits(String valueProperty, double tick)
	{
		yAxisTickUnitsPerValue.put(valueProperty, tick);
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public void setXAxisLabel(String xAxisLabel)
	{
		this.xAxisLabel = xAxisLabel;
	}

	public void setYAxisLabel(String yAxisLabel)
	{
		this.yAxisLabel = yAxisLabel;
	}

	public void setRotateXLabels(XLabelsRotation rotateXLabels)
	{
		this.rotateXLabels = rotateXLabels;
	}

	//	public static ChartPanel getCombinedPlot(ResultSet set, String valueP1, String valueP2, String seriesP,
	//			String categoryP)
	//	{
	//		ResultSetLinePlot p1 = new ResultSetLinePlot(set, valueP1, seriesP, categoryP);
	//		ResultSetLinePlot p2 = new ResultSetLinePlot(set, valueP2, seriesP, categoryP);
	//
	//		CombinedDomainCategoryPlot plot = new CombinedDomainCategoryPlot();
	//		plot.setOrientation(PlotOrientation.VERTICAL);
	//		plot.add(p1.getChartPanel().getChart().getCategoryPlot());
	//		plot.add(p2.getChartPanel().getChart().getCategoryPlot());
	//		JFreeChart chart = new JFreeChart("CombinedDomainXYPlot Demo", JFreeChart.DEFAULT_TITLE_FONT, plot, false);
	//		chart.addLegend(p1.getChartPanel().getChart().getLegend());
	//
	//		return new ChartPanel(chart);
	//	}

	public static void demo2()
	{
		ResultSet set = new ResultSet();
		for (int c = 1; c <= 10; c++)
		{
			for (int s = 1; s <= 3; s++)
			{
				int idx = set.addResult();
				set.setResultValue(idx, "Category", "C" + c);
				set.setResultValue(idx, "Series", "Series" + s);
				set.setResultValue(idx, "Value", new Random().nextDouble());
			}
		}
		ResultSetLinePlot plot = new ResultSetLinePlot(set, new String[] { "Value" }, "Series", "Category");
		SwingUtil.showInFrame(plot.getChartPanel());
		System.exit(0);
	}

	public static void demo()
	{
		ResultSet set = ResultSet.dummySet();

		List<String> equalProperties = new ArrayList<String>();
		equalProperties.add("dataset");
		equalProperties.add("algorithm");

		List<String> ommitProperties = new ArrayList<String>();
		ommitProperties.add("fold");
		ommitProperties.add("features");

		List<String> varProperties = new ArrayList<String>();
		varProperties.add("accuracy");

		ResultSet joined = set.join(equalProperties, ommitProperties, varProperties);
		System.out.println("\njoined folds\n");
		System.out.println(joined.toNiceString());

		ResultSetLinePlot plot = new ResultSetLinePlot(joined, new String[] { "accuracy", "accuracy", "accuracy" },
				"algorithm", "dataset");
		plot.addMarker("accuracy", "mouse", "mark");
		{
			List<String> eqProperties = new ArrayList<String>();
			eqProperties.add("fold");
			ResultSet test = set.pairedTTest("algorithm", eqProperties, "accuracy", 0.01, null, "dataset");
			for (Object datasetWins : ResultSet.listSeriesWins(test, "algorithm", "accuracy", "dataset", "SVM", "NB"))
				plot.setDrawShape("accuracy", datasetWins.toString(), "SVM");
			System.out.println(test.toNiceString());
		}

		SwingUtil.showInFrame(plot.getChartPanel());

		//		SwingUtil.showInDialog(ResultSetLinePlot.getCombinedPlot(set, "accuracy", "accuracy", "algorithm", "dataset"));

		System.exit(0);
	}

	public void setDrawShape(String valueP, String categoryV, String seriesV)
	{
		if (!drawShape.containsKey(valueP))
			drawShape.put(valueP, new HashMap<String, String>());
		drawShape.get(valueP).put(categoryV, seriesV);
	}

	public static void main(String[] args)
	{
		demo2();
	}

}
