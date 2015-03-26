package org.mg.javalib.weka;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.IOUtils;

import weka.core.Instances;

public class ArffWriter
{
	public static boolean DEBUG = false;

	public static void writeToArffFile(File file, ArffWritable data) throws Exception
	{
		// if (file.exists())
		// throw new IllegalStateException("arff file exists: '" + file + "'");
		if (DEBUG)
			System.out.println("Writing arff file: '" + file.getAbsolutePath() + "'"); //(.tmp)");
		writeToArff(new FileWriter(file), data);
	}

	public static Instances toInstances(ArffWritable data) throws Exception
	{
		ByteArrayOutputStream out = null;
		InputStreamReader in = null;
		try
		{
			out = new ByteArrayOutputStream();
			writeToArff(new OutputStreamWriter(out), data);
			in = new InputStreamReader(new ByteArrayInputStream(out.toByteArray()));
			return new Instances(in);
		}
		finally
		{
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(in);
		}
	}

	public static void writeToArff(final Writer w, ArffWritable data) throws Exception
	{
		class LineWriter
		{
			public void println(String s) throws IOException
			{
				w.write(s);
				w.write("\n");
			}

			public void println() throws IOException
			{
				w.write("\n");
			}

			public void println(StringBuffer s) throws IOException
			{
				w.write(s.toString());
				w.write("\n");
			}
		}
		LineWriter out = new LineWriter();

		out.println("% generated: " + new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date()));

		if (data.getAdditionalInfo() != null)
			for (String info : data.getAdditionalInfo())
				out.println("% " + info);

		out.println();
		out.println("@relation \"" + data.getRelationName() + "\"");
		out.println();

		boolean numeric = false;

		for (int i = 0; i < data.getNumAttributes(); i++)
		{
			out.println("@attribute \"" + data.getAttributeName(i) + "\" " + data.getAttributeValueSpace(i));

			if (!numeric)
				numeric = data.getAttributeValueSpace(i).equalsIgnoreCase("numeric");
		}
		out.println();

		out.println("@data");

		boolean sparse = data.isSparse();

		if (sparse)
		{
			if (numeric)
				throw new Error("numeric and sparse is not supported, missing values must explicity represented as ?");

			for (int i = 0; i < data.getNumInstances(); i++)
			{
				// if (data.isInstanceWithoutAttributeValues(i))
				// continue;

				StringBuffer s = new StringBuffer("{");
				boolean first = true;

				for (int j = 0; j < data.getNumAttributes(); j++)
				{
					String value = data.getAttributeValue(i, j);
					if (value == null || !value.equals("0"))
					{
						if (!first)
							s.append(", ");
						else
							first = false;

						if (value == null)
							s.append(j + " " + data.getMissingValue(j));
						else
							s.append(j + " " + value);
					}
				}
				s.append("}");

				out.println(s);
			}
		}
		else
		{
			for (int i = 0; i < data.getNumInstances(); i++)
			{
				StringBuffer s = new StringBuffer();

				for (int j = 0; j < data.getNumAttributes(); j++)
				{
					if (j > 0)
						s.append(",");
					String value = data.getAttributeValue(i, j);
					if (value != null)
						s.append(value);
					else
						s.append(data.getMissingValue(j));
				}
				out.println(s);
			}
		}
		w.close();
	}
}
