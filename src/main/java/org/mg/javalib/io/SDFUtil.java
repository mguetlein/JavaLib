package org.mg.javalib.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.mg.javalib.util.ArrayUtil;
import org.mg.javalib.util.DoubleKeyHashMap;
import org.mg.javalib.util.FileUtil;
import org.mg.javalib.util.FileUtil.CSVFile;
import org.mg.javalib.util.ListUtil;

public class SDFUtil
{
	public static String[] readSdf(String file)
	{
		return readSdf(file, false);
	}

	public static String[] readSdf(String file, boolean stripProps)
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

			if (stripProps)
			{
				for (int i = 0; i < s.length; i++)
				{
					if (!s[i].matches("(?s)(.*)\nM  END\n.*"))
						throw new Error("not matching");
					s[i] = s[i].replaceAll("(?s)(.*)\nM  END\n.*", "$1\nM  END\n");
				}
			}

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

	public static void filter_exclude(String infile, String outfile, int[] excludeIndices,
			boolean stripIncludedProperties)
	{
		SDFUtil.filter_exclude(infile, outfile, ArrayUtil.toList(excludeIndices), stripIncludedProperties);
	}

	public static void filter_exclude(String infile, String outfile, List<Integer> excludeIndices,
			boolean stripIncludedProperties)
	{
		filter(infile, outfile, excludeIndices, false, null, stripIncludedProperties, null, false);
	}

	public static void filter(String infile, String outfile, int[] includeIndices, boolean stripIncludedProperties)
	{
		SDFUtil.filter(infile, outfile, ArrayUtil.toList(includeIndices), stripIncludedProperties);
	}

	public static void filter(String infile, String outfile, List<Integer> includeIndices,
			boolean stripIncludedProperties)
	{
		filter(infile, outfile, includeIndices, stripIncludedProperties, false);
	}

	public static void filter(String infile, String outfile, List<Integer> includeIndices,
			boolean stripIncludedProperties, boolean append)
	{
		filter(infile, outfile, includeIndices, true, null, stripIncludedProperties, null, append);
	}

	public static void filter(String infile, String outfile, int[] includeIndices,
			DoubleKeyHashMap<Integer, String, Object> featureValues, boolean stripIncludedProperties,
			HashMap<Integer, Object> newTitle)
	{
		SDFUtil.filter(infile, outfile, ArrayUtil.toList(includeIndices), featureValues, stripIncludedProperties,
				newTitle);
	}

	public static void filter(String infile, String outfile, List<Integer> includeIndices,
			DoubleKeyHashMap<Integer, String, Object> featureValues, boolean stripIncludedProperties,
			HashMap<Integer, Object> newTitle)
	{
		filter(infile, outfile, includeIndices, true, featureValues, stripIncludedProperties, newTitle, false);
	}

	//	private static void filter(String infile, String outfile, List<Integer> indices, boolean include,
	//			DoubleKeyHashMap<Integer, Object, Object> featureValues)
	//	{
	//		File in = new File(infile);
	//		if (!in.exists())
	//			throw new IllegalArgumentException("file not found" + infile);
	//
	//		System.out.println("filter sdf file to: " + outfile + ", indices-size: " + indices.size() + ", "
	//				+ ListUtil.toString(indices));
	//		File out = new File(outfile);
	//		//		if (out.exists())
	//		//			System.err.println("overwriting " + outfile);
	//
	//		try
	//		{
	//			BufferedWriter w = new BufferedWriter(new FileWriter(out));
	//			BufferedReader b = new BufferedReader(new FileReader(in));
	//			String line;
	//			int index = 0;
	//			while ((line = b.readLine()) != null)
	//			{
	//				if ((indices.indexOf(index) != -1 && include) || (indices.indexOf(index) == -1 && !include))
	//				{
	//					if (line.equals("$$$$"))
	//					{
	//						if (featureValues != null && featureValues.keySet1().size() > 0
	//								&& featureValues.keySet2(index).size() > 0)
	//						{
	//							for (Object key : featureValues.keySet2(index))
	//							{
	//								if (featureValues.get(index, key) != null
	//										&& featureValues.get(index, key).toString().trim().length() > 0)
	//								{
	//									w.write(">  <" + key + ">\n");
	//									w.write(featureValues.get(index, key) + "\n");
	//									w.write("\n");
	//								}
	//							}
	//						}
	//						index++;
	//					}
	//					w.write(line + "\n");
	//				}
	//				else if (line.equals("$$$$"))
	//					index++;
	//			}
	//			b.close();
	//			w.close();
	//		}
	//		catch (FileNotFoundException e)
	//		{
	//			e.printStackTrace();
	//		}
	//		catch (IOException e)
	//		{
	//			e.printStackTrace();
	//		}
	//	}

	private static void filter(String infile, String outfile, List<Integer> indices, boolean include,
			DoubleKeyHashMap<Integer, String, Object> featureValues, boolean stripIncludedProperties,
			HashMap<Integer, Object> molName, boolean append)
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
			String s[] = readSdf(infile, stripIncludedProperties);

			List<Integer> includeIdx;
			if (include)
				includeIdx = indices;
			else
			{
				includeIdx = new ArrayList<Integer>();
				for (int index = 0; index < s.length; index++)
					if (indices.indexOf(index) == -1)
						includeIdx.add(index);
			}

			BufferedWriter w = new BufferedWriter(new FileWriter(out, append));
			//			BufferedReader b = new BufferedReader(new FileReader(in));

			for (Integer index : includeIdx)
			{
				String m = s[index];
				if (molName != null)
				{
					int idx = m.indexOf('\n');
					if (idx != 0)
						System.err.println(index + " replacing title '" + m.substring(0, idx) + "' with '"
								+ molName.get(index) + "'");
					m = molName.get(index) + m.substring(idx);
				}
				w.write(m);

				if (featureValues != null && featureValues.keySet1().size() > 0
						&& featureValues.keySet2(index).size() > 0)
					for (String key : featureValues.keySet2(index))
						if (featureValues.get(index, key) != null
								&& featureValues.get(index, key).toString().trim().length() > 0)
						{
							w.write(">  <" + key + ">\n");
							w.write(featureValues.get(index, key) + "\n");
							w.write("\n");
						}

				w.write("$$$$\n");
			}
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
		List<String[]> values = new ArrayList<String[]>();
		for (double[] ds : featureValues)
			values.add(ArrayUtil.toStringArray(ArrayUtil.toDoubleArray(ds)));
		addStringFeatures(infile, outfile, featureNames, values);
	}

	public static void addStringFeatures(String infile, String outfile, String[] featureNames,
			List<String[]> featureValues)
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

			if (!FileUtil.robustRenameTo(out, new File(outfile)))
				throw new Error("renaming or delete file error");
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

	public static void reduce(String sdfFile, String outfile, double percentage, Random r)
	{
		int size = countCompounds(sdfFile);
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < size; i++)
			if (r.nextDouble() < percentage)
				list.add(i);
		//		System.out.println("orig " + size + " " + sdfFile);
		//		System.out.println("new  " + list.size() + " " + outfile);
		filter(sdfFile, outfile, list, false);
	}

	public static interface SDChecker
	{
		public boolean invalid(String compoundString, int sdFileIndex);
	}

	public static class NanSDChecker implements SDChecker
	{
		@Override
		public boolean invalid(String compoundString, int sdFileIndex)
		{
			return compoundString.matches("(?s).*nan.*nan.*nan.*");
		}
	}

	public static void checkForNans(String search, String replace, String result)
	{
		checkSDFile(search, replace, result, new NanSDChecker());
	}

	public static void checkSDFile(String search, String replace, String result, SDChecker sdChecker)
	{
		checkSDFile(search, new DefaultSDReplacer(replace), result, sdChecker);
	}

	public static interface SDReplacer
	{
		public int numCompounds();

		public String getCompound(int i, String id);
	}

	public static class DefaultSDReplacer implements SDReplacer
	{
		String repl[];

		public DefaultSDReplacer(String sdFile)
		{
			repl = readSdf(sdFile);
		}

		@Override
		public int numCompounds()
		{
			return repl.length;
		}

		@Override
		public String getCompound(int i, String id)
		{
			return repl[i];
		}
	}

	public static class ExternalSDReplacer implements SDReplacer
	{
		FileUtil.CSVFile smi;
		String smiles[];

		public ExternalSDReplacer(String smilesFile)
		{
			smi = FileUtil.readCSV(smilesFile, "	");
			for (int j = 0; j < smi.content.size(); j++)
				if (smi.content.get(j).length != 2)
					throw new Error("no ids in smiles file, line " + j + ":" + ArrayUtil.toString(smi.content.get(j)));
		}

		public ExternalSDReplacer(String smiles[])
		{
			this.smiles = smiles;
		}

		@Override
		public int numCompounds()
		{
			if (smi != null)
				return -1;
			else
				return smiles.length;
		}

		@Override
		public String getCompound(int i, String id)
		{
			String smilesStr;
			if (smi != null)
			{
				int idx = -1;
				for (int j = 0; j < smi.content.size(); j++)
					if (smi.content.get(j)[1].equals(id))
					{
						idx = j;
						break;
					}
				if (idx == -1)
					throw new Error("id " + id + " not found in smiles file");
				smilesStr = smi.content.get(idx)[0];
			}
			else
				smilesStr = smiles[i];
			String s = External3DComputer.get3D(smilesStr);
			if (!s.endsWith("\n"))
				s += "\n";
			s = id + s.substring(s.indexOf("\n"));
			return s;
		}
	}

	public static void checkSDFileExternal(String search, String smilesFile, String result, SDChecker sdChecker)
	{
		checkSDFile(search, new ExternalSDReplacer(smilesFile), result, sdChecker);
	}

	public static void checkSDFileExternal(String search, String smiles[], String result, SDChecker sdChecker)
	{
		checkSDFile(search, new ExternalSDReplacer(smiles), result, sdChecker);
	}

	public static void checkSDFile(String search, SDReplacer replace, String result, SDChecker sdChecker)
	{
		String sear[] = readSdf(search);
		if (replace.numCompounds() != -1 && replace.numCompounds() != sear.length)
			throw new IllegalStateException("no equal number of compounds " + sear.length + " != "
					+ replace.numCompounds());
		try
		{
			boolean replaceIndices[] = new boolean[sear.length];
			int count = 0;
			for (int i = 0; i < sear.length; i++)
			{
				if (sdChecker.invalid(sear[i], i))
				{
					replaceIndices[i] = true;
					count++;
				}
			}
			if (count > 0)
			{
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(result)));
				for (int i = 0; i < sear.length; i++)
				{
					if (replaceIndices[i])
					{
						String repl;
						try
						{
							repl = replace.getCompound(i, sear[i].split("\n")[0]);
						}
						catch (Error e)
						{
							System.err.println("WARNING : failed to get replacement compound " + e.getMessage());
							e.printStackTrace();
							repl = sear[i];
						}
						//						System.out.println("sdf that should be replaced: ");
						//						System.out.println(sear[i]);
						//						System.out.println("id: ");
						//						System.out.println(sear[i].split("\n")[0]);
						bw.write(repl);
					}
					else
						bw.write(sear[i]);
					bw.write("$$$$\n");
				}
				bw.close();
				System.err.println("replaced " + count + " compounds with invalid coordinates");
			}
			else
				System.err.println("all " + sear.length + " passed the check");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void joinCSVProps(String sdfInFile, String csvInFile, String[] csvProps, String outfile)
	{
		CSVFile csv = FileUtil.readCSV(csvInFile);
		List<String[]> featureValues = new ArrayList<String[]>();
		for (int i = 0; i < csv.content.size() - 1; i++)
			featureValues.add(new String[csvProps.length]);
		int colIndex = 0;
		for (String prop : csvProps)
		{
			String[] s = csv.getColumn(prop);
			for (int i = 0; i < s.length; i++)
				featureValues.get(i)[colIndex] = s[i];
			colIndex++;
		}
		addStringFeatures(sdfInFile, outfile, csvProps, featureValues);
	}

	public static void replaceCompounds(String origFile, String replaceFile, List<Integer> origIndices)
	{
		String orig[] = readSdf(origFile);
		String replace[] = readSdf(replaceFile);
		if (replace.length != origIndices.size())
			throw new IllegalStateException("no compounds in replace " + replace.length + " != number of orig indices "
					+ origIndices.size());
		int replaceIndices[] = new int[orig.length];
		for (int i = 0; i < replaceIndices.length; i++)
			replaceIndices[i] = -1;
		for (int i = 0; i < origIndices.size(); i++)
			replaceIndices[origIndices.get(i)] = i;

		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(origFile)));
			for (int i = 0; i < orig.length; i++)
			{
				if (replaceIndices[i] != -1)
					bw.write(replace[replaceIndices[i]]);
				else
					bw.write(orig[i]);
				bw.write("$$$$\n");
			}
			bw.close();
			System.err.println("replaced " + replace.length + " compounds with invalid coordinates");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	public static void main(String args[])
	{

		//		CSVFile smiles = FileUtil.readCSV("/home/martin/data/david/RDT_oral_studies.smi", "	");
		//		System.out.println(smiles.content.size());

		//		String test = "asdf\nM  END\naskfj\nasdfkljasfd\n$$$$\n";
		//		System.out.println(test);
		//		//test.replaceAll("(?s).*\nM  END\n.*\n$$$$\n$", "\nM  END\n$$$$\n");
		//		test = test.replaceAll("(?s)(.*)\nM  END.*$$$$\n$", "$1\nM  END\n\\$\\$\\$\\$\n");
		//		System.out.println("'" + test + "'");

		//		readSdf("/home/martin/workspace/BMBF-MLC/data/CPDBA/dataY.sdf", true);

		//		Random r = new Random(1234);
		//		reduce("/home/martin/data/caco2.sdf", "/tmp/test_new.sdf", 0.3, r);

		//		DoubleKeyHashMap<Integer, Object, Object> vals = new DoubleKeyHashMap<Integer, Object, Object>();
		//		vals.put(0, "bla", "ene");
		//		vals.put(1, "bla", "mene");
		//		vals.put(2, "bla", "miste");
		//		HashMap<Integer, Object> name = new HashMap<Integer, Object>();
		//		name.put(0, "1");
		//		name.put(1, "2");
		//		name.put(2, "3");
		//		filter("/home/martin/data/caco2.sdf", "/tmp/test_new.sdf", new int[] { 0, 1, 2 }, vals, true, name);

		//		joinCSVProps("/home/martin/data/caco2.sdf", "/home/martin/documents/diss/visu_vali/data/caco2data.csv",
		//				new String[] { "caco2-prediction", "set" }, "/home/martin/documents/diss/visu_vali/data/caco2.ext.sdf");

		//		int i[] = new int[50];
		//		for (int j = 0; j < i.length; j++)
		//			i[j] = j;
		//		SDFUtil.filter("/home/martin/workspace/BMBF-MLC/data/CPDBAS/dataC.sdf",
		//				"/home/martin/workspace/BMBF-MLC/data/CPDBAS/dataX.sdf", i);

		reduce("/home/martin/data/Tox21/TOX21S_v2a_8193_22Mar2012_cleanded.half.sdf",
				"/home/martin/data/Tox21/TOX21S_v2a_8193_22Mar2012_cleanded.half2.sdf", 0.5, new Random());

		//		reduce("/home/martin/data/chembl_artif/caffeine2505.sdf",
		//				"/home/martin/data/chembl_artif/caffeine2505_small.sdf", 0.02, new Random());

		//		SDChecker sdCheck = new SDChecker()
		//		{
		//			@Override
		//			public boolean invalid(String compoundString)
		//			{
		//				try
		//				{
		//					String s[] = compoundString.split("\n");
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
		//							compoundString.getBytes())));
		//					IChemFile content = (IChemFile) reader.read((IChemObject) new ChemFile());
		//					List<IAtomContainer> list = ChemFileManipulator.getAllAtomContainers(content);
		//					if (list.size() != 1)
		//						throw new Exception("Cannot parse compound");
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
		//		checkSDFile("/home/martin/data/repdose/repdose.cdk.endpoints.ob3d.sdf",
		//				"/home/martin/data/repdose/repdose.cdk.endpoints.sdf", "/tmp/test.sdf", sdCheck);

		//		try
		//		{
		//			io.MDLV2000Reader reader = new io.MDLV2000Reader(new FileReader(new File(
		//					"/home/martin/data/repdose/repdose.cdk.endpoints.ob3d.sdf")));
		//			IChemFile content = (IChemFile) reader.read((IChemObject) new ChemFile());
		//			int count = 0;
		//			for (IAtomContainer ac : ChemFileManipulator.getAllAtomContainers(content))
		//			{
		//				count++;
		//			}
		//			System.out.println(count);
		//		}
		//		catch (Exception e)
		//		{
		//			e.printStackTrace();
		//		}
		//		checkSDFile(
		//				"/home/martin/.ches-mapper/home/martin/workspace/BMBF/ILLEGAL_RepDoseNeustoff.csv_pc_descriptors_2013-01-28_17-24-01.IDs.clean.SMILES.68fbb9012df539adc642383d08ef2285.ob3d.sdf",
		//				"/home/martin/.ches-mapper/home/martin/workspace/BMBF/ILLEGAL_RepDoseNeustoff.csv_pc_descriptors_2013-01-28_17-24-01.IDs.clean.SMILES.68fbb9012df539adc642383d08ef2285.sdf",
		//				//"/home/martin/.ches-mapper/home/martin/workspace/BMBF/ILLEGAL_RepDoseNeustoff.csv_pc_descriptors_2013-01-28_17-24-01.IDs.clean.SMILES.68fbb9012df539adc642383d08ef2285.ob3d.sdf",
		//				"/tmp/test.sdf", null);

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
