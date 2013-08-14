package babel;

import io.ExternalTool;
import io.Logger;
import io.SDFUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.ArrayUtil;
import util.FileUtil;
import util.StringUtil;

public class OBWrapper
{
	ExternalTool ext;

	public OBWrapper(Logger logger)
	{
		ext = new ExternalTool(logger);
	}

	private HashMap<String, String> version = new HashMap<String, String>();

	public String getVersion(String babelPath)
	{
		if (!version.containsKey(babelPath))
		{
			try
			{
				String v = ext.get("babel", new String[] { babelPath, "-V" });
				Pattern pattern = Pattern.compile("^.*([0-9]+\\.[0-9]+\\.[0-9]+).*$");
				for (String s : v.split("\n"))
				{
					Matcher matcher = pattern.matcher(s);
					if (matcher.matches())
					{
						version.put(babelPath, matcher.group(1));
						break;
					}
				}
			}
			catch (Exception e)
			{
				throw new Error(e);
			}
		}
		return version.get(babelPath);
	}

	public String[] computeInchiFromSmiles(String obabelPath, String[] smiles)
	{
		String inchi[] = new String[smiles.length];
		for (int i = 0; i < inchi.length; i++)
			inchi[i] = ext.get("obinchi", new String[] { obabelPath, "-:" + smiles[i] + "", "-oinchi" });
		return inchi;
	}

	public void computeInchiFromSDF(String babelPath, String sdfFile, String outputInchiFile)
	{
		System.out.println("computing openbabel inchi, source: " + sdfFile + ", dest: " + outputInchiFile);
		ext.run("obgen3d", new String[] { babelPath, "-d", "-isdf", sdfFile, "-oinchi", outputInchiFile });
	}

	private void compute3D(String babelPath, String cacheDir, String type, String content[], String outputSDFile,
			String title[])
	{
		if (new File(outputSDFile).exists())
			if (!new File(outputSDFile).delete())
				throw new Error("could not delete already existing file");
		String extendedCacheDir = cacheDir + File.separator + getVersion(babelPath) + File.separator + type;
		int cached = 0;
		int count = 0;
		for (String mol : content)
		{
			String digest = StringUtil.getMD5(mol);
			String file = extendedCacheDir + File.separator + digest;
			if (!new File(file).exists())
			{
				try
				{
					FileUtil.createParentFolders(file);
					File tmp = File.createTempFile(type + "file", type);
					File out = File.createTempFile("sdffile", "sdf");
					BufferedWriter b = new BufferedWriter(new FileWriter(tmp));
					b.write(mol + "\n");
					b.close();
					ext.run("obgen3d", new String[] { babelPath, "--gen3d", "-d", "-i" + type, tmp.getAbsolutePath(),
							"-osdf", out.getAbsolutePath() }, null, true, null);
					if (!FileUtil.robustRenameTo(out.getAbsolutePath(), file))
						throw new Error("cannot move obresult file");
				}
				catch (IOException e)
				{
					throw new Error(e);
				}
			}
			else
			{
				cached++;
				System.out.println("3d result cached: " + file);
			}
			if (title == null)
			{
				boolean merge = FileUtil.concat(new File(outputSDFile), new File(file), true);
				if (!merge)
					throw new Error("could not merge to sdf file");
			}
			else
			{
				String sdf[] = FileUtil.readStringFromFile(file).split("\n");
				if (sdf[0].length() > 0)
					throw new Error("already a title " + sdf[0]);
				sdf[0] = title[count];
				FileUtil.writeStringToFile(outputSDFile, ArrayUtil.toString(sdf, "\n", "", "", "") + "\n", true);
			}
			count++;
		}
		System.out.println(cached + "/" + content.length + " compounds were precomputed at '" + extendedCacheDir
				+ "', merged obgen3d result to: " + outputSDFile);
	}

	public void compute3DfromSDF(String babelPath, String cacheDir, String inputSDFile, String outputSDFile)
	{
		System.out.println("computing openbabel 3d, source: " + inputSDFile + ", dest: " + outputSDFile);
		compute3D(babelPath, cacheDir, "sdf", SDFUtil.readSdf(inputSDFile), outputSDFile, null);
	}

	public void compute3DfromSmiles(String babelPath, String cacheDir, String inputSmilesFile, String outputSDFile)
	{
		System.out.println("computing openbabel 3d, source: " + inputSmilesFile + ", dest: " + outputSDFile);
		List<String> content = new ArrayList<String>();
		List<String> title = new ArrayList<String>();
		for (String line : FileUtil.readStringFromFile(inputSmilesFile).split("\n"))
		{
			String words[] = line.split("\t");
			if (words.length < 1 || words.length > 2)
				throw new Error();
			content.add(words[0]);
			if (words.length > 1)
				title.add(words[1]);
			else
				title.add(null);
		}
		String[] titles = ArrayUtil.toArray(String.class, title);
		if (ArrayUtil.removeNullValues(titles).size() == 0)
			titles = null;
		compute3D(babelPath, cacheDir, "smi", ArrayUtil.toArray(content), outputSDFile, titles);
	}

	public void main(String args[])
	{
		try
		{
			OBWrapper obwrapper = new OBWrapper(null);

			File tmp = File.createTempFile("smiles", "smi");
			BufferedWriter b = new BufferedWriter(new FileWriter(tmp));
			b.write("c1cccc1\t123\n");
			b.write("c1ccnc1\t456\n");
			b.close();

			obwrapper.compute3DfromSmiles("/home/martin/software/openbabel-2.3.1/install/bin/babel", "/tmp/babel3d",
					tmp.getAbsolutePath(), "/tmp/delme.sdf");

			obwrapper.compute3DfromSDF("/home/martin/software/openbabel-2.3.1/install/bin/babel", "/tmp/babel3d",
					"/tmp/delme.sdf", "/tmp/delme.too.sdf");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
