package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

public class JarUtil
{
	public static void extractFromJAR(String fileName, String dest, boolean overwrite)
	{
		try
		{
			System.err.println("extract: '" + fileName + "' -> '" + dest + "'");
			if (new File(dest).exists())
			{
				System.err.println("already extracted: " + dest);
				if (!overwrite)
					return;
			}
			URL u = JarUtil.class.getResource("/" + fileName);
			BufferedReader r;
			if (u == null)
				r = new BufferedReader(new FileReader(new File(fileName)));
			else
				r = new BufferedReader(new InputStreamReader(u.openStream()));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(dest))));
			String s = null;
			while ((s = r.readLine()) != null)
				out.write(s + "\n");
			//				System.out.println(s);
			out.flush();
			out.close();
			r.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
