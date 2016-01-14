package org.mg.javalib.babel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.mg.javalib.io.ExternalTool;
import org.mg.javalib.io.Logger;
import org.mg.javalib.io.SDFUtil;
import org.mg.javalib.util.ArrayUtil;
import org.mg.javalib.util.FileUtil;

public class OBSmartsMatcher
{
	String babelDataDir;
	String babelLocation;
	Logger logger;

	public OBSmartsMatcher(String babelLocation, String babelDataDir, Logger logger)
	{
		this.babelLocation = babelLocation;
		this.babelDataDir = babelDataDir;
		this.logger = logger;
	}

	public List<boolean[]> match(List<String> smarts, String smilesOrSdFile)
	{
		int num;
		if (smilesOrSdFile.endsWith(".smi"))
			num = FileUtil.getNumLines(smilesOrSdFile);
		else if (smilesOrSdFile.endsWith(".sdf"))
			num = SDFUtil.readSdf(smilesOrSdFile).length;
		else
			throw new IllegalArgumentException("not a smiles or an sdf file");
		return match(smarts, smilesOrSdFile, num);
	}

	public List<boolean[]> match(List<String> smarts, String smilesOrSdFile, int numCompounds)
	{
		List<Integer> minNumMatches = new ArrayList<Integer>();
		for (int i = 0; i < smarts.size(); i++)
			minNumMatches.add(0);
		return match(smarts, minNumMatches, smilesOrSdFile, numCompounds);
	}

	public List<boolean[]> match(List<String> smarts, List<Integer> minNumMatches,
			String smilesOrSdFile, int numCompounds)
	{
		registerFP(smarts);
		createFPFile(smarts, minNumMatches);
		return matchSmarts(smarts, minNumMatches, smilesOrSdFile, numCompounds);
	}

	private String getKey(List<String> smarts)
	{
		return "smarts" + smarts.hashCode();
	}

	private String getFPFile(List<String> smarts)
	{
		return babelDataDir + File.separator + getKey(smarts) + ".txt";
	}

	private void registerFP(List<String> smarts)
	{
		String file = babelDataDir + File.separator + "plugindefines.txt";
		try
		{
			boolean found = false;
			if (new File(file).exists())
			{
				BufferedReader buffy = new BufferedReader(new FileReader(file));
				String line = null;
				while ((line = buffy.readLine()) != null)
					if (line.contains(getFPFile(smarts)))
					{
						found = true;
						break;
					}
				buffy.close();
			}
			if (!found)
			{
				BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
				out.write("\n\nPatternFP\n" + getKey(smarts) + "\n" + getFPFile(smarts) + "\n\n");
				out.close();
			}
		}
		catch (IOException e)
		{
			throw new Error(e);
		}
	}

	private void createFPFile(List<String> smarts, List<Integer> minNumMatches)
	{
		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(getFPFile(smarts)));
			out.write("#Comments after SMARTS\n");
			int i = 0;
			for (String smart : smarts)
			{
				out.write(
						"  " + i + ":('" + smart + "'," + minNumMatches.get(i) + ") # " + i + "\n");
				i++;
			}
			out.close();
		}
		catch (IOException e)
		{
			throw new Error(e);
		}
	}

	private List<boolean[]> matchSmarts(List<String> smarts, List<Integer> minNumMatches,
			String inputFile, int numCompounds)
	{
		List<boolean[]> l = new ArrayList<boolean[]>();
		for (int i = 0; i < smarts.size(); i++)
			l.add(new boolean[numCompounds]);

		File tmp = null;
		try
		{
			tmp = File.createTempFile("sdf" + numCompounds, "OBsmarts");
			String cmd[];
			if (inputFile.endsWith(".sdf"))
				cmd = new String[] { babelLocation, "-isdf", inputFile, "-ofpt", "-xf",
						getKey(smarts), "-xs" };
			else if (inputFile.endsWith(".smi"))
				cmd = new String[] { babelLocation, "-ismi", inputFile, "-ofpt", "-xf",
						getKey(smarts), "-xs" };
			else
				throw new IllegalArgumentException("input neither sdf nor smi");
			logger.debug("Running babel: " + ArrayUtil.toString(cmd, " ", "", ""));
			ExternalTool ext = new ExternalTool(logger);
			ext.run("ob-fingerprints", cmd, tmp, true,
					new String[] { "BABEL_DATADIR=" + babelDataDir });
			logger.debug("Parsing match of " + smarts.size() + " smarts on " + numCompounds
					+ " molecules");
			BufferedReader buffy = new BufferedReader(new FileReader(tmp));
			String line = null;
			int compoundIndex = -1;
			while ((line = buffy.readLine()) != null)
			{
				if (line.startsWith(">"))
				{
					compoundIndex++;
					line = line.replaceAll("^>[^\\t]*", "").trim();
				}
				if (line.length() > 0)
				{
					// Settings.LOGGER.warn("frags: " + line);
					boolean minFreq = false;
					for (String s : line.split("\\t"))
					{
						if (s.trim().length() == 0)
							continue;
						if (minFreq && s.matches("^\\*[2-4].*"))
							s = s.substring(2);
						int smartsIndex = Integer.parseInt(s.split(":")[0]);
						l.get(smartsIndex)[compoundIndex] = true;
						minFreq = s.matches(".*>(\\s)*[1-3].*");
					}
				}
			}
		}
		catch (Exception e)
		{
			throw new Error("Error while matching smarts with OpenBabel: " + e.getMessage(), e);
		}
		finally
		{
			tmp.delete();
		}
		return l;
	}

	public static void main(String[] args)
	{
		String babel = "/home/martin/software/openbabel-2.3.1/install/bin/babel";
		//String babel = "/home/martin/opentox-ruby/openbabel-2.2.3/bin/babel";

		//		String file = "/home/martin/data/stahl-estrogen.smi";
		String file = "/home/martin/data/artificial/test23.smi";

		OBSmartsMatcher obwrapper = new OBSmartsMatcher(babel, "/tmp", new Logger(null, true));
		List<String> smarts = new ArrayList<String>();
		smarts.add("cc");
		smarts.add("S");
		smarts.add("oc");
		List<boolean[]> res = obwrapper.match(smarts, file);
		for (boolean[] bs : res)
			System.out.println(ArrayUtil.toString(bs));

	}
}
