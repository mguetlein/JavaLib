package freechart;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;

import util.ArrayUtil;
import util.SwingUtil;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class FreeChartUtil
{
	public static Color[] COLORS = ArrayUtil.cast(Color.class, ChartColor.createDefaultPaintArray());

	public static Color[] BRIGHT_COLORS = new Color[] {
			new Color(255, 40, 40),
			new Color(40, 40, 255),
			new Color(40, 255, 40),
			//new Color(0xFF, 0x55, 0x55), new Color(0x55, 0x55, 0xFF), new Color(0x55, 0xFF, 0x55), 
			new Color(0xFF, 0xFF, 0x55), new Color(0xFF, 0x55, 0xFF), new Color(0x55, 0xFF, 0xFF), Color.pink,
			Color.gray, ChartColor.DARK_RED, ChartColor.DARK_BLUE, ChartColor.DARK_GREEN, ChartColor.DARK_YELLOW,
			ChartColor.DARK_MAGENTA, ChartColor.DARK_CYAN, Color.darkGray, ChartColor.LIGHT_RED, ChartColor.LIGHT_BLUE,
			ChartColor.LIGHT_GREEN, ChartColor.LIGHT_YELLOW, ChartColor.LIGHT_MAGENTA, ChartColor.LIGHT_CYAN,
			Color.lightGray, ChartColor.VERY_DARK_RED, ChartColor.VERY_DARK_BLUE, ChartColor.VERY_DARK_GREEN,
			ChartColor.VERY_DARK_YELLOW, ChartColor.VERY_DARK_MAGENTA, ChartColor.VERY_DARK_CYAN,
			ChartColor.VERY_LIGHT_RED, ChartColor.VERY_LIGHT_BLUE, ChartColor.VERY_LIGHT_GREEN,
			ChartColor.VERY_LIGHT_YELLOW, ChartColor.VERY_LIGHT_MAGENTA, ChartColor.VERY_LIGHT_CYAN };

	public static String toTmpFile(ChartPanel cp, Dimension dim)
	{
		try
		{
			String f = File.createTempFile("pic", "png").getPath();
			return toFile(f, cp, dim);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static String toFile(String pngFile, ChartPanel cp, Dimension dim)
	{
		try
		{
			ChartUtilities.saveChartAsPNG(new File(pngFile), cp.getChart(), dim.width, dim.height);
			return pngFile;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String args[])
	{
		DefaultFormBuilder b = new DefaultFormBuilder(new FormLayout("p"));
		for (Color c : BRIGHT_COLORS)
		//for (Color c : COLORS)
		{
			JLabel l = new JLabel(c.toString());
			l.setBorder(new EmptyBorder(2, 2, 2, 2));
			l.setOpaque(true);
			l.setBackground(c);
			b.append(l);
		}
		b.setBackground(Color.BLACK);
		b.setBorder(new EmptyBorder(10, 10, 10, 10));
		SwingUtil.showInDialog(b.getPanel());
		System.exit(0);
	}

}
