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
}
