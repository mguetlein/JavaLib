package gui;

import java.awt.Component;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import util.ImageLoader;
import util.SwingUtil;

public class MultiImageIcon extends ImageIcon
{
	public static enum Layout
	{
		horizontal, vertical
	}

	public static enum Orientation
	{
		top, left, bottom, right, center
	}

	List<ImageIcon> icons;
	Layout layout;
	int space;
	Orientation orientation;

	int width;
	int height;

	public MultiImageIcon(List<ImageIcon> icons, Layout layout, Orientation orientation, int space)
	{
		this.icons = icons;
		this.layout = layout;
		this.space = space;
		this.orientation = orientation;
		if (layout == Layout.horizontal && (orientation == Orientation.right || orientation == Orientation.left))
			throw new IllegalArgumentException();
		if (layout == Layout.vertical && (orientation == Orientation.top || orientation == Orientation.bottom))
			throw new IllegalArgumentException();

		if (layout == Layout.horizontal)
		{
			for (ImageIcon ic : icons)
				height = Math.max(height, ic.getIconHeight());
			for (ImageIcon ic : icons)
				width += ic.getIconWidth();
			width += (icons.size() - 1) * space;
		}
		else
		{
			for (ImageIcon ic : icons)
				width = Math.max(width, ic.getIconWidth());
			for (ImageIcon ic : icons)
				height += ic.getIconHeight();
			height += (icons.size() - 1) * space;
		}
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y)
	{
		int xOffset = x;
		int yOffset = y;

		for (int i = 0; i < icons.size(); i++)
		{
			int xLoc = x;
			int yLoc = y;

			if (layout == Layout.horizontal)
			{
				if (orientation == Orientation.center)
					yLoc += (int) Math.round((height - icons.get(i).getIconHeight()) / 2.0);
				else if (orientation == Orientation.bottom)
					yLoc += height - icons.get(i).getIconHeight();
				icons.get(i).paintIcon(c, g, xOffset, yLoc);
				xOffset += icons.get(i).getIconWidth() + space;
			}
			else
			{
				if (orientation == Orientation.center)
					xLoc += (int) Math.round((width - icons.get(i).getIconWidth()) / 2.0);
				else if (orientation == Orientation.right)
					xLoc += width - icons.get(i).getIconWidth();
				icons.get(i).paintIcon(c, g, xLoc, yOffset);
				yOffset += icons.get(i).getIconHeight() + space;
			}
		}
	}

	@Override
	public int getIconWidth()
	{
		return width;
	}

	@Override
	public int getIconHeight()
	{
		return height;
	}

	public static void main(String args[])
	{
		List<ImageIcon> icons = new ArrayList<ImageIcon>();
		icons.add(ImageLoader.CHES_MAPPER_ICON);
		icons.add(ImageLoader.ERROR);
		icons.add(ImageLoader.OPENTOX);
		JLabel l = new JLabel(new MultiImageIcon(icons, MultiImageIcon.Layout.horizontal,
				MultiImageIcon.Orientation.bottom, 10));
		SwingUtil.showInDialog(l);
		System.exit(0);
	}
}