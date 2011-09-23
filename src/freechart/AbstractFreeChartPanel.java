package freechart;

import java.awt.Color;

import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;

public abstract class AbstractFreeChartPanel extends JPanel implements FreeChartPanel
{
	protected ChartPanel chartPanel;

	@Override
	public void setShadowVisible(boolean b)
	{
		Plot plot = chartPanel.getChart().getPlot();
		if (plot instanceof XYPlot)
		{
			XYPlot p = (XYPlot) plot;
			((XYBarRenderer) p.getRenderer()).setShadowVisible(b);
		}
		else if (plot instanceof CategoryPlot)
		{
			CategoryPlot p = (CategoryPlot) plot;
			((BarRenderer) p.getRenderer()).setShadowVisible(b);
		}
		else
			throw new IllegalStateException("unknown plot" + plot.getClass());
	}

	@Override
	public void setOpaqueFalse()
	{
		setOpaque(false);
		chartPanel.setOpaque(false);
		chartPanel.setBackground(new Color(0, 0, 0, 0f));
		chartPanel.getChart().getPlot().setBackgroundAlpha(0f);
		chartPanel.getChart().setBackgroundPaint(new Color(0, 0, 0, 0f));
		chartPanel.getChart().getLegend().setBackgroundPaint(new Color(0, 0, 0, 0f));
	}

	@Override
	public void setForegroundColor(Color col)
	{
		chartPanel.getChart().getLegend().setItemPaint(col);
		chartPanel.getChart().getLegend()
				.setFrame(new BlockBorder(chartPanel.getChart().getLegend().getFrame().getInsets(), col));

		Plot plot = chartPanel.getChart().getPlot();
		plot.setOutlinePaint(col);

		Axis a1;
		Axis a2;
		if (plot instanceof XYPlot)
		{
			XYPlot p = (XYPlot) plot;
			a1 = p.getDomainAxis();
			a2 = p.getRangeAxis();
		}
		else if (plot instanceof CategoryPlot)
		{
			CategoryPlot p = (CategoryPlot) plot;
			a1 = p.getDomainAxis();
			a2 = p.getRangeAxis();
		}
		else
			throw new IllegalStateException("unknown plot " + plot.getClass());

		a1.setAxisLinePaint(col);
		a1.setLabelPaint(col);
		a1.setTickLabelPaint(col);
		a1.setTickMarkPaint(col);

		a2.setAxisLinePaint(col);
		a2.setLabelPaint(col);
		a2.setTickLabelPaint(col);
		a2.setTickMarkPaint(col);
	}

	@Override
	public void setIntegerTickUnits()
	{
		Plot plot = chartPanel.getChart().getPlot();

		Axis a2;
		if (plot instanceof XYPlot)
		{
			XYPlot p = (XYPlot) plot;
			a2 = p.getRangeAxis();
		}
		else if (plot instanceof CategoryPlot)
		{
			CategoryPlot p = (CategoryPlot) plot;
			a2 = p.getRangeAxis();
		}
		else
			throw new IllegalStateException("unknown plot " + plot.getClass());

		((NumberAxis) a2).setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	}

	@Override
	public void setSeriesColor(int index, Color c)
	{
		AbstractRenderer renderer;

		Plot plot = chartPanel.getChart().getPlot();
		if (plot instanceof XYPlot)
			renderer = (AbstractRenderer) ((XYPlot) plot).getRenderer();
		else if (plot instanceof CategoryPlot)
			renderer = (AbstractRenderer) ((CategoryPlot) plot).getRenderer();
		else
			throw new IllegalStateException("unknown plot" + plot.getClass());

		renderer.setSeriesPaint(index, c);
	}

}
