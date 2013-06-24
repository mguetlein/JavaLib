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

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import util.ArrayUtil;
import util.DistanceMatrix;

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
			for (int i = 0; i < featureValues.get(0).length; i++)
			{
				bf.write("\"" + (i + 1) + "\" ");
				for (String v[] : featureValues)
					if (v[i] == null || v[i].equals("null"))
						bf.write("NA ");
					else
						bf.write(v[i] + " ");
				bf.write("\n");
			}
			bf.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static List<Integer[]> readCluster(String matrixFile)
	{
		File f = new File(matrixFile);
		if (!f.exists())
			throw new IllegalStateException("matrix file not found: " + f.getAbsolutePath());
		List<Integer[]> l = new ArrayList<Integer[]>();
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
					Integer c[] = null;
					for (int i = 0; i < s.length; i++)
						if (i == 1)
						{
							String ss = s[i].replaceAll("^\"|\"$", "");
							if (ss.length() == 0)
								c = new Integer[0];
							else if (ss.contains("#"))
							{
								String vals[] = ss.split("#");
								c = new Integer[vals.length];
								for (int j = 0; j < vals.length; j++)
									c[j] = new Integer(vals[j]);
							}
							else
								c = new Integer[] { new Integer(ss) };
						}
					l.add(c);
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

	public static double[][] readMatrix(String matrixFile, double valueForNA)
	{
		File f = new File(matrixFile);
		if (!f.exists())
			throw new IllegalStateException("matrix file not found: " + f.getAbsolutePath());
		double dist[][] = null;
		try
		{
			BufferedReader bf = new BufferedReader(new FileReader(f));
			String line;
			boolean firstline = true;
			int count = 0;
			while ((line = bf.readLine()) != null)
			{
				String s[] = line.split(" ");
				if (firstline)
					firstline = false;
				else
				{
					if (dist == null)
						dist = new double[s.length - 1][s.length - 1];
					for (int i = 1; i < s.length; i++)
						if (s[i].equals("NA"))
							dist[count][i - 1] = valueForNA;
						else
							dist[count][i - 1] = Double.parseDouble(s[i]);
					count++;
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
		return dist;
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

		//System.out.println(ListUtil.toString(readRVectorMatrix("/home/martin/software/R/delme_dist")));

		System.out.println(ArrayUtil.toString(readMatrix("/tmp/testmatrix", 0)));
	}
}
