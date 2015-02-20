package org.mg.javalib.freechart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.mg.javalib.util.ArrayUtil;
import org.mg.javalib.util.FileUtil;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

public class FreeChartUtil
{
	public static Color[] COLORS = ArrayUtil.cast(Color.class, ChartColor.createDefaultPaintArray());

	public static Color BRIGHT_RED = new Color(255, 40, 40);
	public static Color BRIGHT_BLUE = new Color(40, 40, 255);

	public static Color[] BRIGHT_COLORS = new Color[] {
			BRIGHT_RED,
			BRIGHT_BLUE,
			new Color(40, 255, 40),
			//new Color(0xFF, 0x55, 0x55), new Color(0x55, 0x55, 0xFF), new Color(0x55, 0xFF, 0x55), 
			new Color(0xFF, 0xFF, 0x55), new Color(0xFF, 0x55, 0xFF), new Color(0x55, 0xFF, 0xFF), Color.pink,
			Color.gray, ChartColor.DARK_RED, ChartColor.DARK_BLUE, ChartColor.DARK_GREEN, ChartColor.DARK_YELLOW,
			ChartColor.DARK_MAGENTA, ChartColor.DARK_CYAN, Color.darkGray, ChartColor.LIGHT_RED, ChartColor.LIGHT_BLUE,
			ChartColor.LIGHT_GREEN, ChartColor.LIGHT_YELLOW, ChartColor.LIGHT_MAGENTA, ChartColor.LIGHT_CYAN,
			Color.lightGray, ChartColor.VERY_DARK_RED, ChartColor.VERY_DARK_BLUE, ChartColor.VERY_DARK_GREEN,
			ChartColor.VERY_DARK_YELLOW, ChartColor.VERY_DARK_MAGENTA, ChartColor.VERY_DARK_CYAN,
			ChartColor.VERY_LIGHT_RED, ChartColor.VERY_LIGHT_BLUE, ChartColor.VERY_LIGHT_GREEN,
			ChartColor.VERY_LIGHT_YELLOW, ChartColor.VERY_LIGHT_MAGENTA, ChartColor.VERY_LIGHT_CYAN };

	public static String toTmpPNGFile(MessageChartPanel cp, Dimension dim)
	{
		try
		{
			String f = File.createTempFile("pic", "png").getPath();
			return toPNGFile(f, cp, dim);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static String toPNGFile(String pngFile, ChartPanel cp, Dimension dim)
	{
		try
		{
			ChartUtilities.saveChartAsPNG(new File(pngFile), cp.getChart(), dim.width, dim.height);
			return pngFile;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static void toSVGFile(String svgFile, MessageChartPanel cp, Dimension dim)
	{
		try
		{
			cp.setBounds(new Rectangle(dim));

			// Get a DOMImplementation and create an XML document
			DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
			Document document = domImpl.createDocument(null, "svg", null);

			// Create an instance of the SVG Generator
			SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

			// draw the chart in the SVG generator
			cp.getChart().draw(svgGenerator, cp.getBounds());

			// Write svg file
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			Writer out = new OutputStreamWriter(outputStream, "UTF-8");
			svgGenerator.stream(out, true /* use css */);
			outputStream.flush();
			String s = outputStream.toString();
			s = s.replaceFirst(
					"xmlns=\"http://www.w3.org/2000/svg\"",
					"xmlns=\"http://www.w3.org/2000/svg\"  width= \"100%\" height=\"100%\" viewBox=\"0 0 "
							+ dim.getWidth() + " " + dim.getHeight() + "\"");
			s = s.concat("<!--Warning:" + cp.getWarning() + "-->");
			outputStream.close();

			FileUtil.writeStringToFile(svgFile, s);
		}
		catch (DOMException e)
		{
			e.printStackTrace();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		catch (SVGGraphics2DIOException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String args[]) throws Exception
	{
		//		ResultSet rs = new ResultSet();
		//		for (int run = 0; run < 10; run++)
		//			for (String ser : new String[] { "eins", "zwei", "drei" })
		//			{
		//				int x = rs.addResult();
		//				rs.setResultValue(x, "run", run);
		//				rs.setResultValue(x, "ser", ser);
		//				rs.setResultValue(x, "val1", new Random().nextDouble());
		//				rs.setResultValue(x, "val2", new Random().nextDouble());
		//				rs.setResultValue(x, "val3", new Random().nextDouble());
		//			}
		//		MessageChartPanel cp = rs.boxPlot("test", "yLabel", new String[] { "subtitle1" }, "ser",
		//				ArrayUtil.toList(new String[] { "val1", "val2", "val3" }));
		//		toSVGFile("/home/martin/workspace/pps/models/acc99c57-ee24-4d82-a760-685571fb7c47.1.svg", cp, new Dimension(
		//				300, 300));

		//		DefaultFormBuilder b = new DefaultFormBuilder(new FormLayout("p"));
		//		for (Color c : BRIGHT_COLORS)
		//		//for (Color c : COLORS)
		//		{
		//			JLabel l = new JLabel(c.toString());
		//			l.setBorder(new EmptyBorder(2, 2, 2, 2));
		//			l.setOpaque(true);
		//			l.setBackground(c);
		//			b.append(l);
		//		}
		//		b.setBackground(Color.BLACK);
		//		b.setBorder(new EmptyBorder(10, 10, 10, 10));
		//		SwingUtil.showInDialog(b.getPanel());
		//		System.exit(0);
	}

}
