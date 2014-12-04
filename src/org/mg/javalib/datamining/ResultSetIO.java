package org.mg.javalib.datamining;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ResultSetIO
{

	public static void printToFile(File f, ResultSet set, boolean overwrite)
	{
		try
		{
			boolean exists = f.exists();
			PrintStream out = new PrintStream(new FileOutputStream(f, !overwrite));
			out.print(set.toString(!overwrite && exists));
			out.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	public static ResultSet parseFromFile(File f)
	{
		try
		{
			BufferedReader b = new BufferedReader(new FileReader(f));
			boolean first = true;
			String s = null;
			List<String> properties = new ArrayList<String>();
			ResultSet set = new ResultSet();
			while ((s = b.readLine()) != null)
			{
				if (first)
				{
					StringTokenizer tok = new StringTokenizer(s, ",");
					while (tok.hasMoreTokens())
						properties.add(tok.nextToken());
					first = false;
				}
				else
				{
					StringTokenizer tok = new StringTokenizer(s, ",");
					int count = 0;
					int index = set.addResult();
					while (tok.hasMoreTokens())
					{
						String val = tok.nextToken();
						Double nVal = null;
						try
						{
							nVal = Double.parseDouble(val);
						}
						catch (NumberFormatException e)
						{
						}

						set.setResultValue(index, properties.get(count), nVal != null ? nVal : val);
						count++;
					}
				}
			}
			return set;
		}
		catch (Exception e)
		{
			System.err.println("error reading " + f);
			e.printStackTrace();
			return null;
		}

	}
}
