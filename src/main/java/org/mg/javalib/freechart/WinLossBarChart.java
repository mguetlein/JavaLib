package org.mg.javalib.freechart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SubCategoryAxis;
import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.Range;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.Layer;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.mg.javalib.datamining.Result;
import org.mg.javalib.datamining.ResultSet;
import org.mg.javalib.datamining.ResultSetFilter;
import org.mg.javalib.util.StringUtil;
import org.mg.javalib.util.SwingUtil;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class WinLossBarChart
{
	public static final int SPACE_FOR_TITLE = 28;
	public static final int SPACE_FOR_LEGEND_AND_X_AXIS = 55;
	public static final String SPACE_BETWEEN_CAT1_MARKER = "      ";

	ResultSet rs;
	String winLossCmp;
	String winLossMsre;
	String mainCategory;
	String subCategory;

	String title = "Title";

	public WinLossBarChart(ResultSet rs, String winLossCmp, String winLossMsre, String mainCategory,
			String subCategory)
	{
		this.rs = rs;
		this.winLossCmp = winLossCmp;
		this.winLossMsre = winLossMsre;
		this.mainCategory = mainCategory;
		this.subCategory = subCategory;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public void toPNGFile(String file, Dimension dim)
	{
		SwingUtil.toFile(file, getChart(), dim);
	}

	public JPanel getChart()
	{
		//		Color colWinSignificant = ChartColor.GREEN;
		//		Color colWin = new Color(187, 255, 187);

		//		Color colLossSignificant = ChartColor.RED;
		//		Color colLoss = new Color(255, 187, 187);

		Color colWinSignificant = new Color(24, 90, 169);
		Color colWin = new Color(155, 220, 255);

		Color colLossSignificant = new Color(225, 46, 47);
		Color colLoss = new Color(255, 155, 161);

		CombinedDomainCategoryPlot combinedPlot = new CombinedDomainCategoryPlot();
		if (datasets == null)
			createDataset();

		List<CategoryPlot> plots = new ArrayList<>();
		int maxRange = -1;
		for (String vsLabel : datasets.keySet())
		{
			JFreeChart chart = ChartFactory.createStackedBarChart("", // chart title
					null, // domain axis label
					null, // range axis label
					datasets.get(vsLabel), // data
					PlotOrientation.VERTICAL, // the plot orientation
					false, // legend
					false, // tooltips
					false // urls
			);

			GroupedStackedBarRenderer renderer = new GroupedStackedBarRenderer();
			renderer.setBarPainter(new StandardBarPainter());
			renderer.setDrawBarOutline(true);
			renderer.setBaseOutlineStroke(new BasicStroke(0.5f));
			int n = 0;
			for (@SuppressWarnings("unused")
			String s : subCategoryValues)
			{
				renderer.setSeriesPaint(n++, colWin);
				renderer.setSeriesPaint(n++, colWinSignificant);
				renderer.setSeriesPaint(n++, colLoss);
				renderer.setSeriesPaint(n++, colLossSignificant);
			}
			renderer.setSeriesToGroupMap(map);
			renderer.setItemMargin(0.1);

			CategoryPlot plot = (CategoryPlot) chart.getPlot();
			plot.setRenderer(renderer);
			plot.setRangeGridlinePaint(Color.GRAY);
			plot.setBackgroundPaint(Color.WHITE);
			plot.getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			plot.getRangeAxis()
					.setTickLabelFont(plot.getRangeAxis().getTickLabelFont().deriveFont(fontSize));

			// remember maxRange to adjust range of all plots
			Range r = plot.getRangeAxis().getRange();
			if (r.getUpperBound() + 1 > maxRange)
				maxRange = (int) r.getUpperBound() + 1;
			if (Math.abs(r.getLowerBound()) + 1 > maxRange)
				maxRange = (int) Math.abs(r.getLowerBound()) + 1;

			// draw black line at zero
			ValueMarker marker = new ValueMarker(0);
			marker.setPaint(Color.BLACK);
			plot.addRangeMarker(marker);

			// draw significant win losses
			for (Object category : datasets.get(vsLabel).getColumnKeys())
			{
				{
					CategoryMarker markerX = new CategoryMarker(category.toString());
					markerX.setPaint(new Color(0, 0, 0, 0));// set transparent background
					markerX.setLabel(sigWinLossLabels.get(vsLabel).get(category.toString()));
					markerX.setLabelAnchor(RectangleAnchor.TOP);
					markerX.setLabelTextAnchor(TextAnchor.TOP_CENTER);
					markerX.setLabelOffsetType(LengthAdjustmentType.CONTRACT);
					markerX.setLabelFont(markerX.getLabelFont().deriveFont(fontSize * 0.8F));
					plot.addDomainMarker(markerX, Layer.BACKGROUND);
				}
			}

			plots.add(plot);
			combinedPlot.add(plot);
		}

		// create legend
		LegendItemCollection coll = new LegendItemCollection();
		Object o[][] = new Object[][] { { "Significant Win", colWinSignificant }, { "Win", colWin },
				{ "Loss", colLoss }, { "Significant Loss", colLossSignificant } };
		for (Object[] os : o)
		{
			LegendItem i = new LegendItem((String) os[0], (Color) os[1]);
			// create line around legend items
			i.setLinePaint(Color.GRAY);
			i.setLineVisible(true);
			i.setLineStroke(new BasicStroke(10.0f));
			coll.add(i);
		}
		combinedPlot.setFixedLegendItems(coll);

		// set axis
		combinedPlot.setDataset(datasets.values().iterator().next()); // HACK set dataset to render sub-category labels in combinedPlot
		SubCategoryAxis domainAxis = new SubCategoryAxis(null);
		for (String v : subCategoryValues)
			domainAxis.addSubCategory(v);
		domainAxis.setMaximumCategoryLabelWidthRatio(1.33f); // allow to draw larger label then main category
		domainAxis.setTickLabelFont(domainAxis.getTickLabelFont().deriveFont(fontSize * 1.2F));
		domainAxis.setSubLabelFont(domainAxis.getSubLabelFont().deriveFont(fontSize));
		combinedPlot.setDomainAxis(domainAxis);

		// adjust range for all plots, add factor 1.2 to postive range to draw marker
		for (CategoryPlot categoryPlot : plots)
			categoryPlot.getRangeAxis().setRange(-maxRange, maxRange * 1.2);

		// hack: build range axis label manually as they do not allow line breaks in jfreechart
		String layout = SPACE_FOR_TITLE + "px,";
		for (@SuppressWarnings("unused")
		String k : datasets.keySet())
			layout += "fill:p:grow,";
		layout += SPACE_FOR_LEGEND_AND_X_AXIS + "px";
		DefaultFormBuilder buildRangeAxisLabelPanel = new DefaultFormBuilder(
				new FormLayout("p", layout));
		buildRangeAxisLabelPanel.nextLine();
		for (String vsLabel : datasets.keySet())
		{
			JLabel l = new JLabel(vsLabel);
			l.setHorizontalAlignment(SwingConstants.CENTER);
			l.setFont(domainAxis.getTickLabelFont());
			buildRangeAxisLabelPanel.append(l);
		}
		buildRangeAxisLabelPanel.getPanel().setOpaque(false);

		JFreeChart fc = new JFreeChart(combinedPlot);
		fc.setTitle(new TextTitle(title,
				domainAxis.getTickLabelFont().deriveFont(Font.BOLD, fontSize * 1.4F)));
		fc.setBackgroundPaint(Color.WHITE);
		fc.getLegend().setBorder(0, 0, 0, 0);
		fc.getLegend().setItemFont(fc.getLegend().getItemFont().deriveFont(fontSize));

		ChartPanel cp = new ChartPanel(fc);
		cp.setPreferredSize(new Dimension(100, 100)); // set low dimension and scale up, otherwise it cannot be decreased with this layout
		fc.setPadding(new RectangleInsets(0, -5, 0, 0)); // hack remove space of plot to the left

		cp.setMaximumDrawWidth(100000);
		cp.setMaximumDrawHeight(100000);
		cp.setMinimumDrawWidth(100);
		cp.setMinimumDrawHeight(100);

		DefaultFormBuilder buildChartPanel = new DefaultFormBuilder(
				new FormLayout("p,0dlu,fill:p:grow", "fill:p:grow"));
		buildChartPanel.append(buildRangeAxisLabelPanel.getPanel(), 1);
		buildChartPanel.append(cp, 1);
		buildChartPanel.getPanel().setBackground(Color.WHITE);
		buildChartPanel.getPanel().setPreferredSize(new Dimension(800, 400));

		return buildChartPanel.getPanel();
	}

	/**
	 * 
	 * @param winLoss
	 * @return [[winPln, winSig], [lossPln, lossSig]]
	 */
	private static int[][] parseWinLoss(String winLoss)
	{
		String wins = winLoss.substring(0, winLoss.indexOf('/'));
		String losses = winLoss.substring(winLoss.lastIndexOf('/') + 1);
		return new int[][] { parseWin(wins), parseWin(losses) };
	}

	/**
	 * 
	 * @param wins
	 * @return [pln,sig]
	 */
	private static int[] parseWin(String wins)
	{
		String winPln = wins;
		String winSig = "";
		if (wins.contains("("))
		{
			winPln = wins.substring(0, wins.indexOf('('));
			winSig = wins.substring(wins.indexOf('(') + 1, wins.indexOf(')'));
		}
		return new int[] { Integer.parseInt(winPln),
				winSig.isEmpty() ? 0 : Integer.parseInt(winSig) };
	}

	private HashMap<String, DefaultCategoryDataset> datasets;
	private Set<String> subCategoryValues;
	private HashMap<String, HashMap<String, String>> sigWinLossLabels;
	private KeyToGroupMap map = null;

	public int getNumSubPlots()
	{
		if (datasets == null)
			createDataset();
		return datasets.size();
	}

	private void createDataset()
	{
		datasets = new LinkedHashMap<>();
		subCategoryValues = new LinkedHashSet<>();
		sigWinLossLabels = new HashMap<>();

		for (int resIdx = 0; resIdx < rs.getNumResults(); resIdx++)
		{
			Object vsProp1 = rs.getResultValue(resIdx, winLossCmp + "_1");
			Object vsProb2 = rs.getResultValue(resIdx, winLossCmp + "_2");
			String vsLabel = "<html><div style='text-align: center;'>" + vsProp1 + "<br>vs<br>"
					+ vsProb2 + "</div></html>";

			if (!datasets.containsKey(vsLabel))
			{
				datasets.put(vsLabel, new DefaultCategoryDataset());
				sigWinLossLabels.put(vsLabel, new HashMap<String, String>());
			}

			String mainCat = rs.getResultValue(resIdx, mainCategory).toString();
			String subCat = rs.getResultValue(resIdx, subCategory).toString();
			String winLoss = rs.getResultValue(resIdx, winLossMsre).toString();
			int[][] winLosses = parseWinLoss(winLoss);
			//			System.err.println(vsProp1 + " vs " + vsProb2 + " : w:" + ArrayUtil.toString(winLosses[0]) + " l:"
			//					+ ArrayUtil.toString(winLosses[1]) + " " + mainCat + " " + subCat);

			datasets.get(vsLabel).addValue(winLosses[0][0] - winLosses[0][1], subCat + " Wins",
					mainCat);
			datasets.get(vsLabel).addValue(winLosses[0][1], subCat + " Wins-Sig", mainCat);
			datasets.get(vsLabel).addValue(-(winLosses[1][0] - winLosses[1][1]), subCat + " Losses",
					mainCat);
			datasets.get(vsLabel).addValue(-winLosses[1][1], subCat + " Losses-Sig", mainCat);

			if (!sigWinLossLabels.get(vsLabel).containsKey(mainCat))
				sigWinLossLabels.get(vsLabel).put(mainCat, "");
			else
				sigWinLossLabels.get(vsLabel).put(mainCat,
						sigWinLossLabels.get(vsLabel).get(mainCat) + SPACE_BETWEEN_CAT1_MARKER);
			sigWinLossLabels.get(vsLabel).put(mainCat,
					sigWinLossLabels.get(vsLabel).get(mainCat)
							+ StringUtil.concatWhitespace(winLosses[0][1] + "", 2) + "/"
							+ StringUtil.concatWhitespace(winLosses[1][1] + "", 2));

			subCategoryValues.add(rs.getResultValue(resIdx, subCategory).toString());

			if (map == null)
				map = new KeyToGroupMap(subCat);
			map.mapKeyToGroup(subCat + " Wins-Sig", subCat);
			map.mapKeyToGroup(subCat + " Wins", subCat);
			map.mapKeyToGroup(subCat + " Losses-Sig", subCat);
			map.mapKeyToGroup(subCat + " Losses", subCat);
		}

		// add white space to shortest win loss labels
		int l = 0;
		for (String k1 : sigWinLossLabels.keySet())
			for (String k2 : sigWinLossLabels.get(k1).keySet())
				l = Math.max(l, sigWinLossLabels.get(k1).get(k2).length());
		for (String k1 : sigWinLossLabels.keySet())
			for (String k2 : sigWinLossLabels.get(k1).keySet())
				sigWinLossLabels.get(k1).put(k2,
						StringUtil.concatWhitespace(sigWinLossLabels.get(k1).get(k2), l));
	}

	public static void main(String[] args)
	{
		ResultSet tot = null;
		for (int run = 0; run < 3; run++)
		{
			ResultSet set = ResultSet.dummySet();
			System.out.println("\ncomplete dataset\n");
			System.out.println(set.toNiceString());

			ResultSet sum = null;
			for (final Object features : set.getResultValues("features").values())
			{
				System.out.println("features " + features);
				ResultSet setX = set.filter(new ResultSetFilter()
				{
					@Override
					public boolean accept(Result result)
					{
						return result.getValue("features").equals(features);
					}
				});
				ResultSet tested = setX.pairedTTestWinLoss("algorithm", new String[] { "fold" },
						"accuracy", 0.15, null, new String[] { "dataset" }, true);
				System.out.println("\npaired t-test win loss - 1\n");
				System.out.println(tested.toNiceString());

				for (int i = 0; i < tested.getNumResults(); i++)
					tested.setResultValue(i, "features", features);
				if (sum == null)
					sum = tested;
				else
					sum.concat(tested);
			}
			System.out.println(sum.toNiceString());
			for (int i = 0; i < sum.getNumResults(); i++)
				sum.setResultValue(i, "run", run);

			if (tot == null)
				tot = sum;
			else
				tot.concat(sum);
		}

		WinLossBarChart c = new WinLossBarChart(tot, "algorithm", "accuracy", "features", "run");
		c.setFontSize(15F);

		//		c.toPNGFile("/tmp/chart.png", new Dimension(800, 500));
		SwingUtil.showInFrame(c.getChart(), new Dimension(1200, 600));
		System.exit(1);
	}

	Float fontSize = 12.0F;

	public void setFontSize(float f)
	{
		fontSize = f;
	}

}
