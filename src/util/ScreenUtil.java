package util;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;

public class ScreenUtil
{
	private static GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	private static GraphicsDevice[] gs = ge.getScreenDevices();

	public static int getNumMonitors()
	{
		return gs.length;
	}

	public static Dimension getScreenSize()
	{
		return getScreenSize(0);
	}

	public static int getLargestScreen()
	{
		int max = -1;
		int maxSize = 0;
		for (int i = 0; i < getNumMonitors(); i++)
		{
			Dimension dim = getScreenSize(i);
			int size = dim.width * dim.height;
			if (max == -1 || maxSize < size)
			{
				max = i;
				maxSize = size;
			}
		}
		return max;
	}

	public static Dimension getScreenSize(int screen)
	{
		try
		{
			DisplayMode mode = gs[screen].getDisplayMode();
			return new Dimension(mode.getWidth(), mode.getHeight());
		}
		catch (Exception e)
		{
			System.err.println("could not get screen size for screen: " + screen);
			e.printStackTrace();
			return Toolkit.getDefaultToolkit().getScreenSize();
		}
	}

	public static Point getScreenLocation(int screen)
	{
		try
		{
			Rectangle r = gs[screen].getConfigurations()[0].getBounds();
			return new Point(r.x, r.y);
		}
		catch (Exception e)
		{
			System.err.println("could not get screen location for screen: " + screen);
			e.printStackTrace();
			return new Point(0, 0);
		}
	}

	public static void centerOnScreen(Window w, int screen)
	{
		Dimension size = getScreenSize(screen);
		int x = Math.max(0, (size.width - w.getSize().width) / 2);
		int y = Math.max(0, (size.height - w.getSize().height) / 2);
		Point loc = getScreenLocation(screen);
		w.setLocation(loc.x + x, loc.y + y);
	}

	public static void main(String args[])
	{
		for (int i = 0; i < getNumMonitors(); i++)
		{
			System.out.println(i + ":");
			System.out.println(getScreenLocation(i));
			System.out.println(getScreenSize(i));
		}
		System.out.println("largest screen: " + getLargestScreen());
	}
}
