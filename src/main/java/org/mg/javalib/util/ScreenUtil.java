package org.mg.javalib.util;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class ScreenUtil
{
	private static GraphicsEnvironment ge = null;
	private static GraphicsDevice[] gs = null;

	static
	{
		try
		{
			ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			gs = ge.getScreenDevices();
		}
		catch (HeadlessException e)
		{
			System.err.println("no screen!");
		}
	}

	public static int getNumMonitors()
	{
		if (gs == null)
			return 0;
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
		if (gs == null)
			return new Dimension(0, 0);
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
		if (gs == null)
			return new Point(0, 0);
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

	public static GraphicsDevice getGraphicsDevice(int screen)
	{
		if (gs == null)
			return null;
		return gs[screen];
	}

	public static int getScreen(Window w)
	{
		if (gs == null)
			return 0;
		try
		{
			int i = 0;
			for (GraphicsDevice d : gs)
			{
				if (d.getConfigurations()[0].getBounds().contains(w.getLocation()))
					return i;
				i++;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}

	public static void centerWindowsOnScreen()
	{
		centerWindowsOnScreen(Window.getWindows());
	}

	public static void centerWindowsOnScreen(Window... window)
	{
		centerWindowsOnScreen(window, getLargestScreen());
	}

	public static void centerWindowsOnScreen(Window[] window, int screen)
	{
		int w = 0;
		int h = 0;
		for (Window win : window)
		{
			if (win.isVisible())
			{
				w += win.getSize().width;
				h = Math.max(h, win.getSize().height);
			}
		}
		Dimension size = getScreenSize(screen);
		int x = Math.max(0, (size.width - w) / 2);
		int y = Math.max(0, (size.height - h) / 2);
		Point loc = getScreenLocation(screen);

		w = 0;
		for (Window win : window)
		{
			if (win.isVisible())
			{
				win.setLocation(loc.x + w + x, loc.y + y);
				w += win.getWidth();
			}
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
		final JButton b = new JButton("which screen");
		b.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				System.out.println(getScreen((Window) b.getTopLevelAncestor()));
			}
		});
		SwingUtil.showInDialog(b);

		for (int i = 0; i < getNumMonitors(); i++)
		{
			System.out.println(i + ":");
			System.out.println(getScreenLocation(i));
			System.out.println(getScreenSize(i));
		}
		System.out.println("largest screen: " + getLargestScreen());
	}
}
