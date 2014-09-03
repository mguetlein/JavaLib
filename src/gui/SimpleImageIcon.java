package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
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
		plus, minus, cross, left, right, home;
	}

	int origSize;
	int size;
	int halfSize;
	int inset;
	Color col = Color.black;
	Type type;

	private SimpleImageIcon(Type type, int s)
	{
		this.type = type;
		setSize(s);
	}

	public void setSize(int s)
	{
		this.origSize = s;
		if (type == Type.left || type == Type.right || type == Type.home)
			size = s % 2 == 0 ? s : (s - 1);
		else
			size = s;
		this.halfSize = size / 2;
		this.inset = size / 6;
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

	public static SimpleImageIcon homeImageIcon()
	{
		return new SimpleImageIcon(Type.home, 12);
	}

	public static SimpleImageIcon leftImageIcon()
	{
		return new SimpleImageIcon(Type.left, 12);
	}

	public static SimpleImageIcon rightImageIcon()
	{
		return new SimpleImageIcon(Type.right, 12);
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
			g.drawLine(x + 0, y + halfSize, x + size, y + halfSize);
			g.drawLine(x + halfSize, y + 0, x + halfSize, y + size);
		}
		else if (type == Type.minus)
		{
			g.drawLine(x + 0, y + halfSize, x + size, y + halfSize);
		}
		else if (type == Type.cross)
		{
			g.drawLine(x + 0, y + 0, x + size, y + size);
			g.drawLine(x + 0, y + size, x + size, y);
		}
		else if (type == Type.left)
		{
			g.drawLine(x, y + halfSize, x + halfSize, y + 0);
			g.drawLine(x, y + halfSize, x + halfSize, y + size);

			g.drawLine(x + halfSize, y + halfSize, x + size, y + 0);
			g.drawLine(x + halfSize, y + halfSize, x + size, y + size);
		}
		else if (type == Type.right)
		{
			g.drawLine(x + halfSize, y + halfSize, x, y + 0);
			g.drawLine(x + halfSize, y + halfSize, x, y + size);

			g.drawLine(x + size, y + halfSize, x + halfSize, y + 0);
			g.drawLine(x + size, y + halfSize, x + halfSize, y + size);
		}
		else if (type == Type.home)
		{
			g.drawLine(x + 0, y + halfSize, x + halfSize, y + 0);
			g.drawLine(x + halfSize, y + 0, x + size, y + halfSize);
			g.drawLine(x + 0, y + halfSize, x + size, y + halfSize);

			g.drawRect(x + inset, y + halfSize, size - (2 * inset), halfSize);
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
		return origSize;
	}

	@Override
	public int getIconHeight()
	{
		return origSize;
	}

	public static void main(String args[])
	{
		int ns[] = new int[] { 5, 8, 11, 14, 17, 20, 23, 26 };
		JPanel pp = new JPanel(new GridLayout(ns.length, 1));
		for (int n : ns)
		{
			JPanel p = new JPanel();
			p.add(new JButton(new SimpleImageIcon(Type.home, n)));
			p.add(new JButton(new SimpleImageIcon(Type.left, n)));
			p.add(new JButton(new SimpleImageIcon(Type.right, n)));
			p.add(new JButton(new SimpleImageIcon(Type.plus, n)));
			p.add(new JButton(new SimpleImageIcon(Type.minus, n)));
			p.add(new JButton(new SimpleImageIcon(Type.cross, n)));
			pp.add(p);
		}
		SwingUtil.showInDialog(pp);
		System.exit(0);
	}

}
