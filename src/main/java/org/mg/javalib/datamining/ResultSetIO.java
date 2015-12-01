package org.mg.javalib.datamining;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;

import org.mg.javalib.util.FileUtil;

public class ResultSetIO
{
	public static void writeToFile(File f, ResultSet set)
	{
		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
			oos.writeObject(set);
			oos.flush();
			oos.close();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public static ResultSet readFromFile(File f)
	{
		try
		{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
			ResultSet s = (ResultSet) ois.readObject();
			ois.close();
			return s;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public static void printToTxtFile(File f, ResultSet set, boolean overwrite)
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
			throw new RuntimeException(e);
		}
	}

	public static ResultSet parseFromTxtFile(File f)
	{
		return ResultSet.fromString(FileUtil.readStringFromFile(f.getAbsolutePath()));
	}

	public static void main(String[] args)
	{
		ResultSet set = ResultSet.dummySet();

		System.out.println(set);
		System.out.println();

		printToTxtFile(new File("/tmp/results"), set, true);
		writeToFile(new File("/tmp/results2"), set);

		ResultSet set2 = parseFromTxtFile(new File("/tmp/results"));
		System.out.println(set2);
		ResultSet set3 = readFromFile(new File("/tmp/results2"));

		System.out.println(set.equals(set2));
		System.out.println(set.equals(set3));

	}
}
