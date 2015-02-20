package org.mg.javalib.weka;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ArffWriter
{
	public static boolean DEBUG = false;

	public static void writeToArffFile(File file, ArffWritable data) throws Exception
	{
		//		File tmp = new File(file + ".tmp");

		// if (file.exists())
		// throw new IllegalStateException("arff file exists: '" + file + "'");

		PrintStream out = null;
		try
		{
			out = new PrintStream(file);//tmp);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}

		if (DEBUG)
			System.out.println("Writing arff file: '" + file.getAbsolutePath() + "'"); //(.tmp)");

		out.println("% file: " + file);
		out.println("% generated: " + new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date()));

		if (data.getAdditionalInfo() != null)
			for (String info : data.getAdditionalInfo())
				out.println("% " + info);

		out.println();
		out.println("@relation \"" + file.getName() + "\"");
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
			//			ProgressDialog progress = ProgressDialog.showProgress(Status.INFO, "writing arff file instances",
			//					Status.INDENT + "> ", data.getNumInstances());
			for (int i = 0; i < data.getNumInstances(); i++)
			{
				// if (data.isInstanceWithoutAttributeValues(i))
				// continue;

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

				//				if (i % 10 == 0)
				//					progress.update(i);
			}
			//			progress.close(data.getNumInstances());
		}

		//		boolean res = tmp.renameTo(file);
		//		res |= tmp.delete();
		//		if (!res)
		//			throw new Error("renaming or delete file error");

		// Status.INFO.println("done");
	}
}
