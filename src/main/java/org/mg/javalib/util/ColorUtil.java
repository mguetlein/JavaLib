package org.mg.javalib.util;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.List;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class ColorUtil
{
	public static Color parseColor(String s)
	{
		if (s.matches("[0-9]+,[0-9]+,[0-9]+"))
		{
			try
			{
				String[] split = s.split(",");
				return new Color(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
			}
			catch (Exception e)
			{
				throw new Error("rgb parse error, string: " + s + " error: " + e.getMessage());
			}
		}
		else if (s.toLowerCase().matches("black"))
			return Color.BLACK;
		else if (s.toLowerCase().matches("red"))
			return Color.RED;
		else if (s.toLowerCase().matches("blue"))
			return Color.BLUE;
		else if (s.toLowerCase().matches("green"))
			return Color.GREEN;
		else
			throw new Error("unknown/not-supported color: " + s);
	}

	public static String toJMolString(Color c)
	{
		return "[" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + "]";
	}

	public static Color[] darker(Color c[])
	{
		Color col[] = new Color[c.length];
		for (int i = 0; i < col.length; i++)
			col[i] = new Color(c[i].getRGB()).darker();
		return col;
	}

	public static Color[] brighter(Color c[])
	{
		Color col[] = new Color[c.length];
		for (int i = 0; i < col.length; i++)
			col[i] = new Color(c[i].getRGB()).brighter();
		return col;
	}

	public static Color grayscale(Color c)
	{
		int rgbNum = (int) ((c.getRed() + c.getGreen() + c.getBlue()) / 3.0);
		return new Color(rgbNum, rgbNum, rgbNum);
	}

	public static int getAvgRgb(Color c)
	{
		return (int) ((c.getRed() + c.getGreen() + c.getBlue()) / 3.0);
	}

	public static Color getForegroundColor(Color background)
	{
		if (background.getRed() + background.getGreen() + background.getBlue() < 383)
			return Color.WHITE;
		else
			return Color.BLACK;
	}

	public static Color getColorGradient(double ratio, Color from, Color to)
	{
		if (ratio < 0 || ratio > 1)
			throw new IllegalArgumentException(ratio + "");
		int red = (int) (ratio * from.getRed()) + (int) ((1 - ratio) * to.getRed());
		int green = (int) (ratio * from.getGreen()) + (int) ((1 - ratio) * to.getGreen());
		int blue = (int) (ratio * from.getBlue()) + (int) ((1 - ratio) * to.getBlue());
		return new Color(red, green, blue);
	}

	public static Color getThreeColorGradient(double ratio, Color from, Color over, Color to)
	{
		if (ratio >= 0.5)
			return getColorGradient((ratio - 0.5) * 2, from, over);
		else
			return getColorGradient(((1 - ratio) - 0.5) * 2, to, over);
	}

	public static String toHtml(Color color)
	{
		String rgb = Integer.toHexString(color.getRGB());
		rgb = rgb.substring(2, rgb.length());
		return rgb;
	}

	public static Color getRandomColor(Random r)
	{
		return new Color(75 + (int) (150 * r.nextDouble()), 75 + (int) (150 * r.nextDouble()),
				75 + (int) (150 * r.nextDouble()));
	}

	public static Color transparent(Color c, int alpha)
	{
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
	}

	public static Color bright(Color c)
	{
		float hsbVals[] = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
		return Color.getHSBColor(hsbVals[0], hsbVals[1], 1.0f);
	}

	public static Color mediumBrightness(Color c)
	{
		float hsbVals[] = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
		return Color.getHSBColor(hsbVals[0], hsbVals[1], 0.75f);
	}

	public static Color dark(Color c)
	{
		float hsbVals[] = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
		return Color.getHSBColor(hsbVals[0], hsbVals[1], 0.5f);
	}

	public static void main(String[] args)
	{
		Color red = Color.RED;
		float hsbVals[] = Color.RGBtoHSB(red.getRed(), red.getGreen(), red.getBlue(), null);
		Color dark = Color.getHSBColor(hsbVals[0], hsbVals[1], 0.5f);
		Color med = Color.getHSBColor(hsbVals[0], hsbVals[1], 0.75f);
		Color light = Color.getHSBColor(hsbVals[0], hsbVals[1], 1f);

		Color c[] = new Color[] { light, med, dark };
		List<Color> cs = ArrayUtil.toList(c);
		for (Color color : c)
			cs.add(ColorUtil.grayscale(color));

		JPanel p = new JPanel(new GridLayout(2, c.length, 0, 0));
		for (Color col : cs)
		{
			JLabel l = new JLabel("             ");
			l.setOpaque(true);
			l.setBackground(col);
			p.add(l);
		}
		SwingUtil.showInDialog(p);
		System.exit(0);
	}
}
