package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import util.SwingUtil;

public class StringImageIcon extends ImageIcon
{
	private String s;
	private Font f;
	private Color c;
	private Color background;
	private Insets insets = new Insets(0, 0, 0, 0);
	private boolean drawBorder = false;
	FontMetrics fm;

	public StringImageIcon(String s, Font f, Color c)
	{
		this.s = s;
		this.f = f;
		this.c = c;
		fm = new JLabel().getFontMetrics(f);
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y)
	{
		if (background != null)
		{
			g.setColor(background);
			g.fillRect(x, y, getIconWidth(), getIconHeight());
		}
		g.setColor(this.c);
		g.setFont(f);
		if (drawBorder)
			g.drawRect(x, y, getIconWidth() - 1, getIconHeight() - 1);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawString(s, x + insets.left, y + fm.getAscent() + insets.top);
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
		return fm.stringWidth(s) + insets.left + insets.right;
	}

	@Override
	public int getIconHeight()
	{
		return fm.getAscent() + fm.getDescent() + insets.top + insets.bottom;
	}

	public void setBackground(Color c)
	{
		background = c;
	}

	public void setInsets(Insets insets)
	{
		this.insets = insets;
	}

	public void setDrawBorder(boolean b)
	{
		this.drawBorder = b;
	}

	public static void main(String[] args)
	{
		StringImageIcon ic = new StringImageIcon("Test blub mit g und Ä", new JLabel().getFont(), Color.BLACK);
		ic.setDrawBorder(true);
		ic.setInsets(new Insets(2, 2, 2, 2));
		SwingUtil.showInDialog(new JLabel(ic));
		System.exit(0);
	}
}
