package util;

import java.awt.Color;

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

}
