package org.mg.javalib.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.mg.imagelib.ImageLoader;
import org.mg.javalib.util.SwingUtil;

public class BorderImageIcon extends ImageIcon
{

	ImageIcon img;
	int thickness;
	Color col;
	Insets insets;

	public BorderImageIcon(ImageIcon img, int thickness, Color col, Insets insets)
	{
		this.img = img;
		this.thickness = thickness;
		this.col = col;
		this.insets = insets;
	}

	public void setColor(Color col)
	{
		this.col = col;
	}

	public void setImg(ImageIcon img)
	{
		this.img = img;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y)
	{
		g.setColor(col);
		for (int i = 0; i < thickness; i++)
			g.drawRect(i, i, getIconWidth() - 2 * i, getIconHeight() - 2 * i);
		img.paintIcon(c, g, thickness + insets.left, thickness + insets.top);
	}

	@Override
	public Image getImage()
	{
		BufferedImage img = new BufferedImage(getIconWidth(), getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		paintIcon(null, img.getGraphics(), 0, 0);
		return img;
	}

	@Override
	public int getIconWidth()
	{
		return img.getIconWidth() + 2 * thickness + insets.left + insets.right;
	}

	@Override
	public int getIconHeight()
	{
		return img.getIconHeight() + 2 * thickness + insets.top + insets.bottom;
	}

	public static void main(String[] args)
	{
		SwingUtil.showInDialog(new JLabel(new BorderImageIcon(ImageLoader.getImage(org.mg.imagelib.ImageLoader.Image.error), 30,
				Color.RED, new Insets(20, 2, 5, 50))));
		System.exit(0);
	}
}
