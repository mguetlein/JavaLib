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
}
