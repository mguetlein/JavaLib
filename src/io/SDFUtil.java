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
import util.ListUtil;

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
		filter(infile, outfile, excludeIndices, false, null);
	}

	public static void filter(String infile, String outfile, int[] includeIndices)
	{
		SDFUtil.filter(infile, outfile, ArrayUtil.toList(includeIndices));
	}

	public static void filter(String infile, String outfile, List<Integer> includeIndices)
	{
		filter(infile, outfile, includeIndices, true, null);
	}

	public static void filter(String infile, String outfile, int[] includeIndices,
			DoubleKeyHashMap<Integer, Object, Object> featureValues)
	{
		SDFUtil.filter(infile, outfile, ArrayUtil.toList(includeIndices), featureValues);
	}

	public static void filter(String infile, String outfile, List<Integer> includeIndices,
			DoubleKeyHashMap<Integer, Object, Object> featureValues)
	{
		filter(infile, outfile, includeIndices, true, featureValues);
	}

	private static void filter(String infile, String outfile, List<Integer> indices, boolean include,
			DoubleKeyHashMap<Integer, Object, Object> featureValues)
	{
		File in = new File(infile);
		if (!in.exists())
			throw new IllegalArgumentException("file not found" + infile);

		System.out.println("filter sdf file to: " + outfile + ", indices-size: " + indices.size() + ", "
				+ ListUtil.toString(indices));
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
						if (featureValues != null && featureValues.keySet1().size() > 0
								&& featureValues.keySet2(index).size() > 0)
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

	public static interface SDChecker
	{
		public boolean invalid(String moleculeString);
	}

	public static class NanSDChecker implements SDChecker
	{
		@Override
		public boolean invalid(String moleculeString)
		{
			return moleculeString.matches("(?s).*nan.*nan.*nan.*");
		}
	}

	public static void checkForNans(String search, String replace, String result)
	{
		checkSDFile(search, replace, result, new NanSDChecker());
	}

	public static void checkSDFile(String search, String replace, String result, SDChecker sdChecker)
	{
		String sear[] = readSdf(search);
		String repl[] = readSdf(replace);
		if (repl.length != sear.length)
		{
			System.err.println("no equal number of compounds");
			return;
		}
		try
		{
			boolean replaceIndices[] = new boolean[repl.length];
			int count = 0;
			for (int i = 0; i < sear.length; i++)
				if (sdChecker.invalid(sear[i]))
				{
					replaceIndices[i] = true;
					count++;
				}
			if (count > 0)
			{
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(result)));
				for (int i = 0; i < sear.length; i++)
				{
					if (replaceIndices[i])
						bw.write(repl[i]);
					else
						bw.write(sear[i]);
					bw.write("$$$$\n");
				}
				bw.close();
				System.err.println("replaced " + count + " molecules with invalid coordinates");
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String args[])
	{
		//		reduce("/home/martin/.ches-mapper/home/martin/data/ches-mapper/ISSCAN_v3a_1153_19Sept08.1222179139.cleaned.sdf",
		//				"/home/martin/data/ches-mapper/ISSCAN_v3a_1153_19Sept08.1222179139.cleaned.small.sdf", 0.2);

		//		reduce("/home/martin/data/cox2_3d_lc50num.sdf", "/home/martin/data/cox2_3d_46.sdf", 0.01);

		//		SDChecker sdCheck = new SDChecker()
		//		{
		//			@Override
		//			public boolean invalid(String moleculeString)
		//			{
		//				try
		//				{
		//					String s[] = moleculeString.split("\n");
		//					int numAtoms = -1;
		//					for (String line : s)
		//						if (line.contains("V2000"))
		//						{
		//							numAtoms = Integer.parseInt(line.substring(0, 3).trim());
		//							break;
		//						}
		//					if (numAtoms == -1)
		//						throw new Exception("could not parse num atoms");
		//					MDLV2000Reader reader = new MDLV2000Reader(new InputStreamReader(new ByteArrayInputStream(
		//							moleculeString.getBytes())));
		//					IChemFile content = (IChemFile) reader.read((IChemObject) new ChemFile());
		//					List<IAtomContainer> list = ChemFileManipulator.getAllAtomContainers(content);
		//					if (list.size() != 1)
		//						throw new Exception("Cannot parse molecule");
		//					if (list.get(0).getAtomCount() != numAtoms)
		//						throw new Exception("Num atoms " + list.get(0).getAtomCount() + " != " + numAtoms);
		//					for (int i = 0; i < list.get(0).getBondCount(); i++)
		//					{
		//						if (list.get(0).getBond(i).getAtomCount() != 2)
		//							throw new Exception("Num atoms for bond is " + list.get(0).getBond(i).getAtomCount());
		//						IAtom a = list.get(0).getBond(i).getAtom(0);
		//						IAtom b = list.get(0).getBond(i).getAtom(1);
		//						Point3d pa = a.getPoint3d();
		//						if (pa == null)
		//							pa = new Point3d(a.getPoint2d().x, a.getPoint2d().y, 0.0);
		//						Point3d pb = b.getPoint3d();
		//						if (pb == null)
		//							pb = new Point3d(b.getPoint2d().x, b.getPoint2d().y, 0.0);
		//						double d = pa.distance(pb);
		//						if (d > 2.5 || d < 0.9)
		//							throw new Exception("Distance between atoms is " + d);
		//					}
		//					return false;
		//				}
		//				catch (Exception e)
		//				{
		//					e.printStackTrace();
		//					return true;
		//				}
		//			}
		//		};
		checkSDFile(
				"/home/martin/.ches-mapper/home/martin/workspace/BMBF/ILLEGAL_RepDoseNeustoff.csv_pc_descriptors_2013-01-28_17-24-01.IDs.clean.SMILES.68fbb9012df539adc642383d08ef2285.ob3d.sdf",
				"/home/martin/.ches-mapper/home/martin/workspace/BMBF/ILLEGAL_RepDoseNeustoff.csv_pc_descriptors_2013-01-28_17-24-01.IDs.clean.SMILES.68fbb9012df539adc642383d08ef2285.sdf",
				//"/home/martin/.ches-mapper/home/martin/workspace/BMBF/ILLEGAL_RepDoseNeustoff.csv_pc_descriptors_2013-01-28_17-24-01.IDs.clean.SMILES.68fbb9012df539adc642383d08ef2285.ob3d.sdf",
				"/tmp/test.sdf", null);

		//		filter_exclude("/home/martin/data/3d/bzr/data/bzr_3d.sd", "/home/martin/data/3d/bzr/data/bzr.sdf", new int[] {
		//				191, 192 });

		//		filter_match("/home/martin/results/cox2_cv#f-0_n-10_r-1_s-true_t-false#.sdf",
		//				"/home/martin/workspace/external/moss/br_example.sdf", new String[] { "3-24", "3-36", "3-68", "3-78",
		//						"4-42", "4-49", "9-44", "9-45" });

		//		int include[] = { 103 };
		//		SDFUtil.filter("/home/martin/data/CPDBAS_v5d_1547_20Nov2008.ob.sdf", "/home/martin/data/CPDBAS_104__.sdf",
		//				include);

		//		String f[] = { "newFeatureX" };
		//		List<double[]> l = new ArrayList<double[]>();
		//		l.add(new double[] { 2.0 });
		//		l.add(new double[] { 7.0 });
		//		l.add(new double[] { 1.0 });
		//		SDFUtil.addFeatures("/home/martin/tmp/delme2.sdf", "/home/martin/tmp/delme2.sdf", f, l);
	}
}
