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
			if (new File(dest).exists())
			{
				System.out.println("already extracted: " + dest);
				if (!overwrite)
					return;
			}
			else
				System.out.println("extract: '" + fileName + "' -> '" + dest + "'");
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
