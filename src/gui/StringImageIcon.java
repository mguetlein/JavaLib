package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
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
		g.setColor(this.c);
		g.setFont(f);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawString(s, x, y + fm.getAscent());
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
		return fm.stringWidth(s);
	}

	@Override
	public int getIconHeight()
	{
		return fm.getAscent() + fm.getDescent();
	}

	public static void main(String[] args)
	{
		SwingUtil.showInDialog(new JLabel(new StringImageIcon("Test blub mit g und Ä", new JLabel().getFont(),
				Color.BLACK)));
		System.exit(0);
	}
}
