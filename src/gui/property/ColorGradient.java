package gui.property;

import java.awt.Color;

public class ColorGradient
{
	public final Color high;
	public final Color med;
	public final Color low;

	public ColorGradient()
	{
		this(Color.RED, Color.WHITE, Color.BLUE);
	}

	public ColorGradient(Color high, Color med, Color low)
	{
		this.high = high;
		this.med = med;
		this.low = low;
	}

	private static Color getColorGradient(double ratio, Color from, Color to)
	{
		if (ratio < 0 || ratio > 1)
			throw new IllegalArgumentException(ratio + "");
		int red = (int) (ratio * from.getRed()) + (int) ((1 - ratio) * to.getRed());
		int green = (int) (ratio * from.getGreen()) + (int) ((1 - ratio) * to.getGreen());
		int blue = (int) (ratio * from.getBlue()) + (int) ((1 - ratio) * to.getBlue());
		return new Color(red, green, blue);
	}

	public Color getColor(double ratio)
	{
		if (ratio >= 0.5)
			return getColorGradient((ratio - 0.5) * 2, high, med);
		else
			return getColorGradient(((1 - ratio) - 0.5) * 2, low, med);
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof ColorGradient))
			return false;
		return toString().equals(o.toString());
	}

	@Override
	public String toString()
	{
		return "hr:" + high.getRed() + "-hg:" + high.getGreen() + "-hb:" + high.getBlue() + "-mr:" + med.getRed()
				+ "-mg:" + med.getGreen() + "-mb:" + med.getBlue() + "-lr:" + low.getRed() + "-lg:" + low.getGreen()
				+ "-lb:" + low.getBlue();
	}

	public static Object parseColorGradient(String colorGradient)
	{
		String valueStrings[] = colorGradient.split("-");
		if (valueStrings.length != 9)
			throw new IllegalArgumentException("cannot parse color gradient: " + colorGradient);
		int valIndex = 0;
		int hmlIndex = 0;
		Color hml[] = new Color[3];
		for (String hmlString : new String[] { "h", "m", "l" })
		{
			int rgb[] = new int[3];
			int rgbIndex = 0;
			for (String rgbString : new String[] { "r", "g", "b" })
			{
				String valueString[] = valueStrings[valIndex].split(":");
				if (valueString.length != 2 || !(valueString[0].equals(hmlString + rgbString)))
					throw new IllegalArgumentException("cannot parse color gradient: " + colorGradient + " "
							+ valueStrings[valIndex]);
				rgb[rgbIndex] = Integer.parseInt(valueString[1]);
				valIndex++;
				rgbIndex++;
			}
			hml[hmlIndex] = new Color(rgb[0], rgb[1], rgb[2]);
			hmlIndex++;
		}
		return new ColorGradient(hml[0], hml[1], hml[2]);
	}

	public static void main(String args[])
	{
		ColorGradient gc = new ColorGradient(Color.GREEN, Color.BLACK, Color.RED);
		System.out.println(gc);
		System.out.println(ColorGradient.parseColorGradient(gc.toString()));
	}

	public Color getHigh()
	{
		return high;
	}

	public Color getMed()
	{
		return med;
	}

	public Color getLow()
	{
		return low;
	}

	public ColorGradient reverse()
	{
		return new ColorGradient(low, med, high);
	}

}
