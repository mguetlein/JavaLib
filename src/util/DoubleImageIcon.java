package util;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class DoubleImageIcon implements Icon
{
	ImageIcon icon1;
	ImageIcon icon2;

	public DoubleImageIcon(ImageIcon icon1, ImageIcon icon2)
	{
		this.icon1 = icon1;
		this.icon2 = icon2;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y)
	{
		icon1.paintIcon(c, g, x, y);
		icon2.paintIcon(c, g, x + icon1.getIconWidth(), y);
	}

	@Override
	public int getIconWidth()
	{
		return icon1.getIconWidth() + icon2.getIconWidth();
	}

	@Override
	public int getIconHeight()
	{
		return Math.max(icon1.getIconHeight(), icon2.getIconHeight());
	}

}
