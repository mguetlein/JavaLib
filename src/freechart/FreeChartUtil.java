package freechart;

import java.awt.Color;

import org.jfree.chart.ChartColor;

import util.ArrayUtil;

public class FreeChartUtil
{
	public static Color[] COLORS = ArrayUtil.cast(Color.class, ChartColor.createDefaultPaintArray());
}
