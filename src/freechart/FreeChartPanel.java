package freechart;

import java.awt.Color;

public interface FreeChartPanel
{
	public void setShadowVisible(boolean b);

	public void setOpaqueFalse();

	public void setForegroundColor(Color c);

	public void setSeriesColor(int index, Color c);
}
