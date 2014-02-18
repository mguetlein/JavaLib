package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import util.SwingUtil;

public class SimpleImageIcon extends ImageIcon
{
	enum Type
	{
		plus, minus, cross;
	}

	int size;
	Color col = Color.black;
	Type type;

	private SimpleImageIcon(Type type, int size)
	{
		this.type = type;
		this.size = size;
	}

	public static SimpleImageIcon plusImageIcon()
	{
		return new SimpleImageIcon(Type.plus, 12);
	}

	public static SimpleImageIcon minusImageIcon()
	{
		return new SimpleImageIcon(Type.minus, 12);
	}

	public static SimpleImageIcon crossImageIcon()
	{
		return new SimpleImageIcon(Type.cross, 12);
	}

	public void setColor(Color col)
	{
		this.col = col;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y)
	{
		g.setColor(this.col);
		if (type == Type.plus)
		{
			g.drawLine(x + 0, y + size / 2, x + size, y + size / 2);
			g.drawLine(x + size / 2, y + 0, x + size / 2, y + size);
		}
		else if (type == Type.minus)
		{
			g.drawLine(x + 0, y + size / 2, x + size, y + size / 2);
		}
		else if (type == Type.cross)
		{
			g.drawLine(x + 0, y + 0, x + size, y + size);
			g.drawLine(x + size, y + 0, x + 0, y + size);
		}
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
		return size;
	}

	@Override
	public int getIconHeight()
	{
		return size;
	}

	public static void main(String args[])
	{
		JPanel p = new JPanel();
		p.add(new JButton(new SimpleImageIcon(Type.plus, 20)));
		p.add(new JButton(new SimpleImageIcon(Type.minus, 20)));
		p.add(new JButton(new SimpleImageIcon(Type.cross, 20)));
		SwingUtil.showInDialog(p);
		System.exit(0);
	}

	public void setSize(int i)
	{
		this.size = i;
	}
}
