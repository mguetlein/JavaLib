package util;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
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

	public static void resize(String src, String dest, double scale) throws IOException
	{
		ImageIcon input = new ImageIcon(src);
		//scale
		int scaledWidth = (int) (input.getIconWidth() * scale);
		int scaledHeight = (int) (input.getIconHeight() * scale);
		BufferedImage resizedImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = resizedImage.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawImage(input.getImage(), 0, 0, scaledWidth, scaledHeight, null);
		g.dispose();

		//ImageIO.write(resizedImage, "jpg", new File(dest));

		ImageOutputStream ios = ImageIO.createImageOutputStream(new File(dest));
		//System.err.println(ios);
		Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
		ImageWriter writer = iter.next();
		ImageWriteParam iwp = writer.getDefaultWriteParam();
		iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		iwp.setCompressionQuality(0.95f);
		writer.setOutput(ios);
		writer.write(null, new IIOImage(resizedImage, null, null), iwp);
		writer.dispose();
	}

	public static void main(String args[])
	{
		try
		{
			resize("/home/martin/Schreibtisch/new_zealand.jpg", "/home/martin/Schreibtisch/new_zealand_X.jpg", 0.5);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
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
