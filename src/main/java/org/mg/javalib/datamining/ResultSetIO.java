package org.mg.javalib.datamining;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.mg.javalib.util.FileUtil;

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
		return ResultSet.fromString(FileUtil.readStringFromFile(f.getAbsolutePath()));
	}

	public static void main(String[] args)
	{
		ResultSet set = ResultSet.dummySet();

		System.out.println(set);
		System.out.println();

		printToFile(new File("/tmp/results"), set, true);

		ResultSet set2 = parseFromFile(new File("/tmp/results"));
		System.out.println(set2);
	}
}
