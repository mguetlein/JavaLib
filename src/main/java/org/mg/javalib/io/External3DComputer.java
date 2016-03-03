package org.mg.javalib.io;

import java.io.File;
import java.io.IOException;

import org.mg.javalib.util.FileUtil;
import org.mg.javalib.util.StringUtil;

public class External3DComputer
{
	public static String get3D(String smiles)
	{
		File f = null;
		try
		{
			f = File.createTempFile("cori", "out");
			ExternalTool ext = new ExternalTool(null);
			ext.run("curl",
					new String[] {
							System.getProperty("user.home") + "/software/bash/external_3d.sh",
							smiles, f.getAbsolutePath() });
			return FileUtil.readStringFromFile(f.getAbsolutePath());
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			if (f != null && f.exists())
				f.delete();
		}
	}

	public static void main(String[] args)
	{
		String smiles = "CN(C)C(=S)S[Zn]SC(=S)N(C)C";
		String mol = get3D(smiles);
		mol += "\n$$$$\n";
		FileUtil.writeStringToFile(
				"/home/martin/.ches-mapper/babel3d/2.3.2/smi/" + StringUtil.getMD5(smiles), mol);
	}
}
