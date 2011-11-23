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
import java.util.Random;

import util.ArrayUtil;
import util.DoubleKeyHashMap;

public class SDFUtil
{
	public static String[] readSdf(String file)
	{
		List<String> sdf = new ArrayList<String>();
		File in = new File(file);
		if (!in.exists())
			throw new IllegalArgumentException("file not found" + file);
		try
		{
			String mol = "";
			BufferedReader b = new BufferedReader(new FileReader(in));
			String line;
			int end = 0;
			int dollars = 0;
			while ((line = b.readLine()) != null)
			{
				if (line.matches(".*M.*END.*"))
					end++;
				if (!line.equals("$$$$"))
					mol += line + "\n";
				else
				{
					sdf.add(mol);
					mol = "";
					dollars++;
				}
			}
			b.close();

			if (dollars == end - 1)
				dollars++;
			if (dollars != end)
				throw new IllegalArgumentException("not a valid sdf file");

			String s[] = new String[sdf.size()];
			sdf.toArray(s);
			return s;
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static int countCompounds(String file)
	{
		File in = new File(file);
		if (!in.exists())
			throw new IllegalArgumentException("file not found" + file);
		try
		{
			BufferedReader b = new BufferedReader(new FileReader(in));
			String line;
			int end = 0;
			int dollars = 0;
			while ((line = b.readLine()) != null)
			{
				if (line.matches(".*M.*END.*"))
					end++;
				if (line.equals("$$$$"))
					dollars++;
			}
			b.close();

			if (dollars == end - 1)
				dollars++;
			if (dollars != end)
				throw new IllegalArgumentException("not a valid sdf file");
			return dollars;
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return -1;
	}

	public static void filter_exclude(String infile, String outfile, int[] excludeIndices)
	{
		SDFUtil.filter_exclude(infile, outfile, ArrayUtil.toList(excludeIndices));
	}

	public static void filter_exclude(String infile, String outfile, List<Integer> excludeIndices)
	{
		filter(infile, outfile, excludeIndices, false, null, null);
	}

	public static void filter(String infile, String outfile, int[] includeIndices)
	{
		SDFUtil.filter(infile, outfile, ArrayUtil.toList(includeIndices));
	}

	public static void filter(String infile, String outfile, List<Integer> includeIndices)
	{
		filter(infile, outfile, includeIndices, true, null, null);
	}

	public static void filter(String infile, String outfile, int[] includeIndices, List<Object> featureNames,
			DoubleKeyHashMap<Integer, Object, Object> featureValues)
	{
		SDFUtil.filter(infile, outfile, ArrayUtil.toList(includeIndices), featureNames, featureValues);
	}

	public static void filter(String infile, String outfile, List<Integer> includeIndices, List<Object> featureNames,
			DoubleKeyHashMap<Integer, Object, Object> featureValues)
	{
		filter(infile, outfile, includeIndices, true, featureNames, featureValues);
	}

	private static void filter(String infile, String outfile, List<Integer> indices, boolean include,
			List<Object> featureNames, DoubleKeyHashMap<Integer, Object, Object> featureValues)
	{
		File in = new File(infile);
		if (!in.exists())
			throw new IllegalArgumentException("file not found" + infile);

		System.out.println("filter sdf file to: " + outfile);
		File out = new File(outfile);
		//		if (out.exists())
		//			System.err.println("overwriting " + outfile);

		try
		{
			BufferedWriter w = new BufferedWriter(new FileWriter(out));
			BufferedReader b = new BufferedReader(new FileReader(in));
			String line;
			int index = 0;
			while ((line = b.readLine()) != null)
			{
				if ((indices.indexOf(index) != -1 && include) || (indices.indexOf(index) == -1 && !include))
				{
					if (line.equals("$$$$"))
					{
						if (featureNames != null)
						{
							for (Object key : featureValues.keySet2(index))
							{
								w.write(">  <" + key + ">\n");
								w.write(featureValues.get(index, key) + "\n");
								w.write("\n");
							}
						}
						index++;
					}
					w.write(line + "\n");
				}
				else if (line.equals("$$$$"))
					index++;
			}
			b.close();
			w.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void filter_match(String infile, String outfile, String[] matches)
	{
		File in = new File(infile);
		if (!in.exists())
			throw new IllegalArgumentException("file not found" + infile);

		System.out.println("filter sdf file to: " + outfile);
		File out = new File(outfile);
		//		if (out.exists())
		//			System.err.println("overwriting " + outfile);

		try
		{
			BufferedWriter w = new BufferedWriter(new FileWriter(out));
			BufferedReader b = new BufferedReader(new FileReader(in));
			String line;
			int index = 0;
			String sdf = "";
			while ((line = b.readLine()) != null)
			{
				sdf += line + "\n";
				if (line.equals("$$$$"))
				{
					System.err.println(index);
					for (String string : matches)
					{
						if (sdf.contains(string))
						{
							System.err.println("matching: " + string);
							w.write(sdf);
							break;
						}
					}
					sdf = "";
					index++;
				}
			}
			b.close();
			w.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void addFeatures(String infile, String outfile, String[] featureNames, List<double[]> featureValues)
	{
		File in = new File(infile);
		if (!in.exists())
			throw new IllegalArgumentException("file not found" + infile);

		//		System.out.println("filter sdf file to: " + outfile);

		//		if (out.exists())
		//			System.err.println("overwriting " + outfile);

		try
		{
			File out = File.createTempFile("outfile", "tmp");

			BufferedWriter w = new BufferedWriter(new FileWriter(out));
			BufferedReader b = new BufferedReader(new FileReader(in));
			String line;
			int index = 0;
			while ((line = b.readLine()) != null)
			{
				if (line.equals("$$$$"))
				{
					int c = 0;
					for (String f : featureNames)
					{
						w.write(">  <" + f + ">\n");
						w.write(featureValues.get(index)[c] + "\n");
						w.write("\n");
						c++;
					}
					index++;
				}
				w.write(line + "\n");
			}
			b.close();
			w.close();

			out.renameTo(new File(outfile));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void reduce(String sdfFile, String outfile, double percentage)
	{
		int size = countCompounds(sdfFile);
		Random r = new Random();
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < size; i++)
			if (r.nextDouble() < percentage)
				list.add(i);
		System.out.println("orig " + size + " " + sdfFile);
		System.out.println("new  " + list.size() + " " + outfile);
		filter(sdfFile, outfile, list);
	}

	public static void main(String args[])
	{
		reduce("/home/martin/.ches-mapper/home/martin/data/ches-mapper/ISSCAN_v3a_1153_19Sept08.1222179139.cleaned.sdf",
				"/home/martin/data/ches-mapper/ISSCAN_v3a_1153_19Sept08.1222179139.cleaned.small.sdf", 0.2);

		//reduce("/home/martin/data/cox2_3d_WithReals.sdf", "/home/martin/data/cox2_3d_WithReals.m.sdf", 0.33);

		//		filter_exclude("/home/martin/data/3d/bzr/data/bzr_3d.sd", "/home/martin/data/3d/bzr/data/bzr.sdf", new int[] {
		//				191, 192 });

		//		filter_match("/home/martin/results/cox2_cv#f-0_n-10_r-1_s-true_t-false#.sdf",
		//				"/home/martin/workspace/external/moss/br_example.sdf", new String[] { "3-24", "3-36", "3-68", "3-78",
		//						"4-42", "4-49", "9-44", "9-45" });

		//		int include[] = { 0, 1, 400 };
		//		SDFUtil.filter(
		//				"/home/martin/workspace/ClusterViewer/geclusterteDatensaetze/cox2_3d_WithReals/Dataset/cox2_3d_WithReals.sdf",
		//				"/home/martin/tmp/delme.sdf", include);

		//		String f[] = { "newFeatureX" };
		//		List<double[]> l = new ArrayList<double[]>();
		//		l.add(new double[] { 2.0 });
		//		l.add(new double[] { 7.0 });
		//		l.add(new double[] { 1.0 });
		//		SDFUtil.addFeatures("/home/martin/tmp/delme2.sdf", "/home/martin/tmp/delme2.sdf", f, l);
	}
}
