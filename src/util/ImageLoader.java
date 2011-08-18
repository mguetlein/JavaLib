package util;

import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;

public class ImageLoader
{
	public static final ImageIcon DOWN = loadImageIcon("data/down.png", ImageLoader.class);
	public static final ImageIcon NUMERIC = loadImageIcon("data/numeric.png", ImageLoader.class);
	public static final ImageIcon DISTINCT = loadImageIcon("data/distinct.png", ImageLoader.class);
	public static final ImageIcon RIGHT = loadImageIcon("data/right.png", ImageLoader.class);
	public static final ImageIcon WARNING = loadImageIcon("data/warning.png", ImageLoader.class);
	public static final ImageIcon INFO = loadImageIcon("data/info.png", ImageLoader.class);
	public static final ImageIcon ERROR = loadImageIcon("data/error.png", ImageLoader.class);

	public static ImageIcon loadImageIcon(String imagePath, Class<?> relativeTo)
	{
		ImageIcon icon = null;
		try
		{
			// works for jar files: copy files into root dir of jar file
			int index = imagePath.lastIndexOf("/");
			if (index == -1)
				index = 0;
			//			System.out.println("look for: " + "/" + imagePath.substring(index + 1));
			icon = new ImageIcon(relativeTo.getResource("/" + imagePath.substring(index + 1)));
		}
		catch (Exception e)
		{
			// file lies in project folder
			icon = new ImageIcon(imagePath);
			if (icon.getIconWidth() == -1)
			{
				try
				{
					// hack: file lies in lib folder
					imagePath = new File(".").getCanonicalPath() + File.separator + ".." + File.separator + "JavaLib"
							+ File.separator + imagePath;
					icon = new ImageIcon(imagePath);
				}
				catch (IOException e1)
				{
				}
			}
		}
		if (icon == null || icon.getIconWidth() <= 0)
			System.err.println("could not load icon: " + imagePath);
		return icon;
	}
}
