package util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.GrayFilter;
import javax.swing.ImageIcon;

public class ImageLoader
{
	public enum Image
	{
		down, numeric, distinct, right, warning, info, error, hourglass, ok, tool, ches_mapper, ches_mapper_icon,
		opentox, filter14_black, filter14, sort_black, sort, sort_bar, sort_bar_black, sort_bar14, sort_bar14_black,
		sort_arrow, sort_arrow_black, down14, down14_black, up14
	}

	private static HashMap<Image, ImageIcon> map = new HashMap<Image, ImageIcon>();
	private static HashMap<Image, ImageIcon> grayMap = new HashMap<Image, ImageIcon>();

	public static ImageIcon getImage(Image img)
	{
		if (!map.containsKey(img))
			map.put(img, loadImageIcon("data/" + img + ".png", ImageLoader.class));
		return map.get(img);
	}

	public static ImageIcon getGrayImage(Image img)
	{
		if (!grayMap.containsKey(img))
			grayMap.put(img, new ImageIcon(GrayFilter.createDisabledImage(getImage(img).getImage())));
		return grayMap.get(img);
	}

	public static ImageIcon loadImageIcon(String imagePath, Class<?> relativeTo)
	{
		ImageIcon icon = null;
		try
		{
			icon = new ImageIcon(relativeTo.getResource("/" + imagePath));
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
