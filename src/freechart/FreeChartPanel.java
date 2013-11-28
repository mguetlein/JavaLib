package freechart;

import java.awt.Color;

import org.jfree.chart.ChartPanel;

public interface FreeChartPanel
{
	public void setShadowVisible(boolean b);

	public void setOpaqueFalse();

	public void setForegroundColor(Color c);

	public void setSeriesColor(int index, Color c);

	public void setIntegerTickUnits();

	public void setIntegerTickUnitsOnYAxis();

	public static interface ChartMouseSelectionListener
	{
		public void hoverEvent();

		public void clickEvent(boolean ctrlDown, boolean doubleClick);
	}

	public void addSelectionListener(ChartMouseSelectionListener l);

	public ChartPanel getChartPanel();
}
