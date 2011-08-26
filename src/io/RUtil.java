package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.geometry.Vector3D;

import util.DistanceMatrix;
import util.ListUtil;

public class RUtil
{
	public static void toRMatrixTable(DistanceMatrix<?> matrix, String destinationFile)
	{
		toRMatrixTable(matrix.distances(), destinationFile);
	}

	public static void toRMatrixTable(double[][] matrix, String destinationFile)
	{
		String header[] = new String[matrix.length];
		for (int i = 0; i < matrix.length; i++)
			header[i] = (i + 1) + "";

		File f = new File(destinationFile);
		// if (f.exists())
		// throw new IllegalStateException("file " + f.getAbsolutePath() +
		// " already exists");
		try
		{
			BufferedWriter bf = new BufferedWriter(new FileWriter(f));
			for (String h : header)
				bf.write("\"" + h + "\" ");
			bf.write("\n");
			for (int i = 0; i < matrix.length; i++)
			{
				bf.write("\"" + header[i] + "\" ");
				for (int j = 0; j < matrix[0].length; j++)
					bf.write(matrix[i][j] + " ");
				bf.write("\n");
			}
			bf.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void toRTable(Iterable<?> features, List<String[]> featureValues, String destinationFile)
	{
		File f = new File(destinationFile);
		// if (f.exists())
		// throw new IllegalStateException("file " + f.getAbsolutePath() +
		// " already exists");
		try
		{
			BufferedWriter bf = new BufferedWriter(new FileWriter(f));
			for (Object o : features)
				bf.write("\"" + o + "\" ");
			bf.write("\n");
			for (int i = 0; i < featureValues.size(); i++)
			{
				bf.write("\"" + (i + 1) + "\" ");
				for (String v : featureValues.get(i))
					bf.write(v + " ");
				bf.write("\n");
			}
			bf.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static List<Vector3D> readRVectorMatrix(String matrixFile)
	{
		File f = new File(matrixFile);
		if (!f.exists())
			throw new IllegalStateException("matrix file not found: " + f.getAbsolutePath());
		List<Vector3D> l = new ArrayList<Vector3D>();
		try
		{
			BufferedReader bf = new BufferedReader(new FileReader(f));
			String line;
			boolean firstline = true;
			while ((line = bf.readLine()) != null)
			{
				// System.out.println(line);
				String s[] = line.split(" ");
				// System.out.println(ArrayUtil.toString(s));

				if (firstline)
					firstline = false;
				else
				{
					double x = 0, y = 0, z = 0;
					for (int i = 0; i < s.length; i++)
						if (i == 1)
							x = Double.parseDouble(s[i]);
						else if (i == 2)
							y = Double.parseDouble(s[i]);
						else if (i == 3)
							z = Double.parseDouble(s[i]);
					l.add(new Vector3D(x, y, z));
				}
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return l;

	}

	public static void main(String args[])
	{
		// String h[] = { "test1", "t2", "t3", "t4" };
		// double d[][] = { { 0, 0.5, 1, 1 }, { 0.5, 0, 1, 1 }, { 1, 1, 0, 0 },
		// { 1, 1, 0, 0 } };
		// toRTable(h, d, "/home/martin/tmp/delme");

		System.out.println(ListUtil.toString(readRVectorMatrix("/home/martin/software/R/delme_dist")));
	}
}
