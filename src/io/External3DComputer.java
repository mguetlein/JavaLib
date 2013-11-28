package io;

import java.io.File;
import java.io.IOException;

import util.FileUtil;

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
					new String[] { System.getProperty("user.home") + "/software/bash/external_3d.sh", smiles,
							f.getAbsolutePath() });
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
		System.out.println(get3D("CCC"));
	}
}
