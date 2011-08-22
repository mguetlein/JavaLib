package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

public class FileUtil
{
	public static class CSVFile
	{
		public List<String> comments;
		public List<String[]> content;
	}

	public static CSVFile readCSV(String filename)
	{
		try
		{
			List<String[]> l = new ArrayList<String[]>();
			List<String> c = new ArrayList<String>();

			BufferedReader b = new BufferedReader(new FileReader(new File(filename)));
			String s = "";
			while ((s = b.readLine()) != null)
			{
				if (s.trim().length() == 0)
					continue;
				if (s.startsWith("#"))
					c.add(s);
				else
					l.add(ArrayUtil.cast(String.class, VectorUtil.fromCSVString(s, false).toArray()));
			}
			b.close();

			CSVFile csv = new CSVFile();
			csv.comments = c;
			csv.content = l;
			return csv;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * renameto is not reliable to windows
	 * 
	 * @param source
	 * @param dest
	 * @return
	 */
	public static boolean robustRenameTo(File source, File dest)
	{
		if (OSUtil.isWindows())
		{
			try
			{
				String line;
				String cmd = "cmd /c MOVE /Y " + source.getAbsolutePath() + " " + dest.getAbsolutePath();
				System.out.println(cmd);
				Process p = Runtime.getRuntime().exec(cmd);
				BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
				while ((line = input.readLine()) != null)
					System.out.println(line);
				input.close();
				BufferedReader input2 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				while ((line = input2.readLine()) != null)
					System.out.println(line);
				input2.close();
				p.waitFor();
				// System.err.println(p.exitValue());
				return p.exitValue() == 0;
			}
			catch (Exception err)
			{
				err.printStackTrace();
				return false;
			}
		}
		else
			return source.renameTo(dest);
	}

	public static boolean isContentEqual(String file1, String file2)
	{
		File f1 = new File(file1);
		File f2 = new File(file2);
		if (!f1.exists() || !f2.exists())
			throw new IllegalArgumentException();
		if (f1.length() != f2.length())
			return false;
		return Arrays.equals(getMd5(file1), getMd5(file2));
	}

	public static String getMD5String(String filename)
	{
		byte[] b = getMd5(filename);
		String result = "";
		for (int i = 0; i < b.length; i++)
		{
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

	public static byte[] getMd5(String filename)
	{
		MessageDigest md = null;
		InputStream is = null;
		try
		{
			md = MessageDigest.getInstance("MD5");
			try
			{
				is = new FileInputStream(filename);
				is = new DigestInputStream(is, md);
			}
			finally
			{
				if (is != null)
					is.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return md.digest();
	}

	public static String getFilename(String file)
	{
		return getFilename(file, true);
	}

	public static String getFilename(String file, boolean withExtension)
	{
		String n = new File(file).getName();
		if (withExtension)
			return n;
		else
		{
			int index = n.lastIndexOf('.');
			if (index == -1)
				return n;
			else
				return n.substring(0, index);
		}
	}

	public static String getParent(String file)
	{
		return new File(file).getParent();
	}

	public static void writeStringToFile(String file, String content)
	{
		writeStringToFile(file, content, false);
	}

	public static void writeStringToFile(String file, String content, boolean append)
	{
		try
		{
			BufferedWriter w = new BufferedWriter(new FileWriter(file, append));
			w.write(content);
			w.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void join(String input1, String input2, String output)
	{
		FileUtil.writeStringToFile(output, FileUtil.readStringFromFile(input1));
		FileUtil.writeStringToFile(output, FileUtil.readStringFromFile(input2), true);
	}

	public static String readStringFromFile(String file)
	{
		try
		{
			String res = "";
			String line;
			BufferedReader r = new BufferedReader(new FileReader(file));
			while ((line = r.readLine()) != null)
				res += line + "\n";
			r.close();
			return res;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static Vector<File> getFilesRecursiv(String commaSeperatedFilesOrDirectories)
	{
		StringTokenizer tok = new StringTokenizer(commaSeperatedFilesOrDirectories, ",");
		Vector<File> res = new Vector<File>();
		while (tok.hasMoreTokens())
			res.addAll(FileUtil.getFilesRecursiv(new File(tok.nextToken())));
		return res;
	}

	public static Vector<File> getFilesRecursiv(File fileOrDirectory)
	{
		Vector<File> res = new Vector<File>();
		if (!fileOrDirectory.exists())
			throw new IllegalStateException("file does not exist: " + fileOrDirectory);
		if (fileOrDirectory.isDirectory())
		{
			File dir[] = fileOrDirectory.listFiles();
			for (File file : dir)
				res.addAll(FileUtil.getFilesRecursiv(file));
		}
		else if (fileOrDirectory.isHidden())
			System.err.println("omitting hidden file: " + fileOrDirectory);
		else
			res.add(fileOrDirectory);
		return res;
	}

	public static void createParentFolders(String file)
	{
		createParentFolders(new File(file));
	}

	public static void createParentFolders(File file)
	{
		File p = new File(file.getParent());
		if (!p.exists())
		{
			boolean b = p.mkdirs();
			if (!b)
				throw new Error("could not create folder: " + p);
		}
	}

	public static String toCygwinPosixPath(String path)
	{
		String s = path.replaceAll("\\\\", "/");
		if (Character.isLetter(s.charAt(0)) && s.charAt(1) == ':' && s.charAt(2) == '/')
			s = "/cygdrive/" + s.charAt(0) + s.substring(2);
		return s;
	}

	public static String getCygwinPosixPath(File f)
	{
		if (SystemUtil.isWindows())
			return toCygwinPosixPath(f.getAbsolutePath());
		else
			return f.getAbsolutePath();
	}

	public static void main(String args[])
	{
		//		String s = "C:\\bla\\blub";
		//		System.out.println(s);
		//		System.out.println(toCygwinPosixPath(s));

		System.out.println(FileUtil.isContentEqual(
				"/home/martin/workspace/ClusterViewer/cluster_data/nctrer_small_3d/002.nctrer.distances.table",
				"/home/martin/workspace/ClusterViewer/cluster_data/nctrer_small_3d/003.nctrer.distances.table"));
	}

}
