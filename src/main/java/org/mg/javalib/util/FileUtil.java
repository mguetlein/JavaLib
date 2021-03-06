package org.mg.javalib.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.codec.digest.DigestUtils;
import org.jfree.io.IOUtils;

public class FileUtil
{
	public static interface ColumnExclude
	{
		public boolean exclude(int columnIndex, String columnName);
	}

	public static interface RowRemove
	{
		public boolean remove(int rowIndex, String row[]);
	}

	public static class UnexpectedNumColsException extends Exception
	{
		public UnexpectedNumColsException(String msg)
		{
			super(msg);
		}
	}

	public static class CSVFile
	{
		public List<String> comments = new ArrayList<String>();
		public List<String[]> content = new ArrayList<String[]>();

		public CSVFile merge(CSVFile csvFile)
		{
			if (comments.size() > 0 || csvFile.comments.size() > 0)
				throw new Error("merging comments not yet implemented");
			if (content.size() != csvFile.content.size())
				throw new IllegalArgumentException();
			CSVFile newCsv = new CSVFile();
			for (int i = 0; i < content.size(); i++)
				newCsv.content.add(
						ArrayUtil.concat(String.class, content.get(i), csvFile.content.get(i)));
			return newCsv;
		}

		public String[] getHeader()
		{
			return content.get(0);
		}

		public int getColumnIndex(String colName)
		{
			return ArrayUtil.indexOf(getHeader(), colName);
		}

		public String[] getColumn(String colName)
		{
			int colIndex = ArrayUtil.indexOf(getHeader(), colName);
			if (colIndex == -1)
				throw new IllegalArgumentException("column '" + colName + "' not found, available: "
						+ ArrayUtil.toString(getHeader()));
			String s[] = new String[content.size() - 1];
			for (int i = 0; i < s.length; i++)
				s[i] = content.get(i + 1)[colIndex];
			return s;
		}

		public Double[] getDoubleColumn(String colName)
		{
			try
			{
				NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
				String s[] = getColumn(colName);
				Double d[] = new Double[s.length];
				for (int i = 0; i < d.length; i++)
				{
					Double v;
					if (s[i] == null || s[i].length() == 0)
						v = null;
					else
					{
						v = format.parse(s[i]).doubleValue();
						//v = Double.parseDouble(s[i]);
					}
					d[i] = v;
				}
				return d;
			}
			catch (ParseException e)
			{
				throw new RuntimeException(e);
			}
		}

		public CSVFile removeRow(RowRemove remove)
		{
			Set<Integer> rowIndex = new HashSet<Integer>();
			for (int i = 1; i < content.size(); i++)
				if (remove.remove(i, content.get(i)))
					rowIndex.add(i);
			return removeRow(ArrayUtil.toPrimitiveIntArray(rowIndex));
		}

		public CSVFile removeRow(int... rows)
		{
			CSVFile csv = new CSVFile();
			csv.comments = ListUtil.clone(comments);
			csv.content = ListUtil.clone(content);
			Arrays.sort(rows);
			for (int i = rows.length - 1; i >= 0; i--)
				csv.content.remove(rows[i]);
			return csv;
		}

		public CSVFile exclude(ColumnExclude exclude)
		{
			Set<Integer> colIndex = new HashSet<Integer>();
			for (int i = 0; i < getHeader().length; i++)
				if (exclude.exclude(i, getHeader()[i]))
					colIndex.add(i);
			return exclude(ArrayUtil.toPrimitiveIntArray(colIndex));
		}

		public CSVFile exclude(String... columns)
		{
			List<Integer> colIndex = new ArrayList<Integer>();
			for (String s : columns)
			{
				int i = ArrayUtil.indexOf(getHeader(), s);
				if (i == -1)
					throw new IllegalArgumentException(
							"not found: " + s + " in " + ArrayUtil.toString(getHeader()));
				colIndex.add(i);
			}
			return exclude(ArrayUtil.toPrimitiveIntArray(colIndex));
		}

		public CSVFile exclude(int... columns)
		{
			CSVFile csv = new CSVFile();
			csv.comments = ListUtil.clone(comments);
			csv.content = ListUtil.clone(content);
			Arrays.sort(columns);
			for (int i = columns.length - 1; i >= 0; i--)
			{
				//				System.err.println(ArrayUtil.toString(csv.getHeader()));
				//				System.err.println("remove " + columns[i] + ", length: " + csv.getHeader().length);
				List<String[]> contentNew = new ArrayList<String[]>();
				for (String s[] : csv.content)
					contentNew.add(ArrayUtil.removeAt(String.class, s, columns[i]));
				csv.content = contentNew;
			}
			return csv;
		}

		public String toString()
		{
			StringLineAdder s = new StringLineAdder();
			for (String st : comments)
				s.add(st);
			for (String[] st : content)
				s.add(ArrayUtil.toCSVString(st));
			return s.toString();
		}

		public CSVFile addColumn(String col, String[] values)
		{
			if (values.length != content.size() - 1)
				throw new IllegalArgumentException();
			CSVFile csv = new CSVFile();
			csv.comments = ListUtil.clone(comments);
			csv.content = new ArrayList<String[]>();
			csv.content.add(ArrayUtil.concat(String.class, content.get(0), new String[] { col }));
			for (int i = 1; i < content.size(); i++)
				csv.content.add(ArrayUtil.concat(String.class, content.get(i),
						new String[] { values[i - 1] }));
			return csv;
		}
	}

	public static void writeCSV(String file, CSVFile csv, boolean append)
	{
		if (csv.comments.size() > 0)
			throw new Error("merging comments not yet implemented");

		try
		{
			BufferedWriter w = new BufferedWriter(new FileWriter(file, append));
			for (String s[] : csv.content)
			{
				boolean first = true;
				for (String string : s)
				{
					if (first)
						first = false;
					else
						w.write(",");
					if (string != null)
					{
						w.write("\"");
						w.write(string);
						w.write("\"");
					}
				}
				w.newLine();
			}
			w.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static CSVFile readCSV(String filename)
	{
		return readCSV(filename, null);
	}

	public static CSVFile readCSV(String filename, String sep)
	{
		try
		{
			return readCSV(filename, -1, sep);
		}
		catch (UnexpectedNumColsException e)
		{
			throw new Error("should never happen");
		}
	}

	public static CSVFile readCSV(String filename, int expectedNumCols)
			throws UnexpectedNumColsException
	{
		return readCSV(filename, expectedNumCols, null);
	}

	public static CSVFile readCSV(String filename, int expectedNumCols, String sep)
			throws UnexpectedNumColsException
	{
		if (sep != null && sep.length() != 1)
			throw new IllegalArgumentException("seperator must have length 1");
		try
		{
			List<String[]> l = new ArrayList<String[]>();
			List<String> c = new ArrayList<String>();

			BufferedReader b = new BufferedReader(new FileReader(new File(filename)));
			String s = "";
			char seperator = ',';

			while ((s = b.readLine()) != null)
			{
				if (s.trim().length() == 0)
					l.add(new String[0]);
				else if (s.startsWith("#"))
					c.add(s);
				else
				{
					if (l.size() == 0)
					{
						if (sep != null)
							seperator = sep.charAt(0);
						else
						{
							if (StringUtil.split(s, ';').size() > StringUtil.split(s, ',').size())
								seperator = ';';
							else
								seperator = ',';
						}
					}
					List<String> line = StringUtil.split(s, false, expectedNumCols, seperator);
					//					if (l.size() > 0 && l.get(0).length != line.size())
					//						throw new IllegalArgumentException("error reading csv '" + filename + "', line no1 #"
					//								+ l.get(0).length + " != line no" + (l.size() + 1) + " #" + line.size()
					//								+ "\nfirst-line-parsed:\n" + ArrayUtil.toString(l.get(0)) + "\norig-string:\n" + s
					//								+ "\nparsed:\n" + line);
					l.add(ArrayUtil.toArray(String.class, line));
				}
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

	public static boolean robustRenameTo(String source, String dest)
	{
		return robustRenameTo(new File(source), new File(dest));
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
				String cmd[] = new String[] { "cmd", "/c", "MOVE", "/Y", source.getAbsolutePath(),
						dest.getAbsolutePath() };
				System.out.println(cmd);
				Process p = Runtime.getRuntime().exec(cmd);
				BufferedReader input = new BufferedReader(
						new InputStreamReader(p.getInputStream()));
				while ((line = input.readLine()) != null)
					System.out.println(line);
				input.close();
				BufferedReader input2 = new BufferedReader(
						new InputStreamReader(p.getErrorStream()));
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
		{
			//file.renameTo failed on gome as well, use streams! 
			if (!source.exists())
				return false;
			if (!copy(source, dest))
				return false;
			if (!dest.exists())
				return false;
			if (!source.delete())
				return false;
			return true;
		}
	}

	/**
	 * replace a backslash in windows with a double-backslash
	 * 
	 * @param f
	 * @return
	 */
	public static String getAbsolutePathEscaped(File f)
	{
		if (OSUtil.isWindows())
			return f.getAbsolutePath().replaceAll("\\\\", "\\\\\\\\");
		else
			return f.getAbsolutePath();
	}

	public static boolean concat(File dest, List<File> source)
	{
		return concat(dest, source, false);
	}

	public static boolean concat(File dest, File source, boolean append)
	{
		List<File> l = new ArrayList<File>();
		l.add(source);
		return concat(dest, l, append);
	}

	public static boolean concat(File dest, List<File> source, boolean append)
	{
		if (dest.exists() && !append)
			if (!dest.delete())
				return false;
		for (File s : source)
			if (!copy(s, dest, true))
				return false;
		return true;
	}

	public static boolean copy(String source, String dest)
	{
		return copy(new File(source), new File(dest));
	}

	public static boolean copy(File source, File dest)
	{
		return copy(source, dest, false);
	}

	public static boolean copy(File source, File dest, boolean append)
	{
		FileInputStream from = null;
		FileOutputStream to = null;
		try
		{
			from = new FileInputStream(source);
			to = new FileOutputStream(dest, append);
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = from.read(buffer)) != -1)
				to.write(buffer, 0, bytesRead); // write
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if (from != null)
				try
				{
					from.close();
				}
				catch (IOException e)
				{
				}
			if (to != null)
				try
				{
					to.close();
				}
				catch (IOException e)
				{
				}
		}
	}

	public static boolean isContentEqual(String file1, String file2)
	{
		File f1 = new File(file1);
		File f2 = new File(file2);
		if (!f1.exists() || !f2.exists())
			throw new IllegalArgumentException();
		if (f1.length() != f2.length())
			return false;
		return getMD5String(file1).equals(getMD5String(file2));
	}

	public static String getMD5String(String filename)
	{
		try
		{
			FileInputStream fis = new FileInputStream(new File(filename));
			return DigestUtils.md5Hex(fis);
		}
		catch (FileNotFoundException e1)
		{
			e1.printStackTrace();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		return null;
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

	/**
	 * without .
	 */
	public static String getFilenamExtension(String file)
	{
		String n = new File(file).getName();
		int index = n.lastIndexOf('.');
		if (index == -1)
			return "";
		else
			return n.substring(index + 1);
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
		try
		{
			InputStream i1 = new FileInputStream(new File(input1));
			InputStream i2 = new FileInputStream(new File(input2));
			InputStream i = new SequenceInputStream(i1, i2);
			IOUtils.getInstance().copyStreams(i, new FileOutputStream(output));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		//		FileUtil.writeStringToFile(output, FileUtil.readStringFromFile(input1));
		//		FileUtil.writeStringToFile(output, FileUtil.readStringFromFile(input2), true);
	}

	public static String readStringFromFile(String file)
	{
		try
		{
			StringBuffer res = new StringBuffer();
			String line;
			BufferedReader r = new BufferedReader(new FileReader(file));
			boolean firstLine = true;
			while ((line = r.readLine()) != null)
			{
				if (firstLine)
					firstLine = false;
				else
					res.append("\n");
				res.append(line);
			}
			r.close();
			return res.toString();
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
		CSVFile csv = FileUtil.readCSV("/tmp/caco2_20.csv.noCache.0.0.0.0.csv", ",");
		System.out.println(ArrayUtil.toString(csv.getHeader()));

		//copy(new File("/tmp/res1"), new File("/tmp/res3"));

		//CSVFile csv = FileUtil.readCSV("/home/martin/.ches-mapper/structural_fragments/ToxTree_CramerRules.csv");
		//		CSVFile csv = FileUtil
		//				.readCSV("/home/martin/workspace/MLC/data/RepDoseNeustoff-2013-03-28.fp34MACCSLin.obDesc.ID.csv");
		//
		//		System.out.println("comment");
		//		System.out.println(ListUtil.toString(csv.comments));
		//		System.out.println("content");
		//		for (String string[] : csv.content)
		//			System.out.println(ArrayUtil.toString(string));
		//System.out.println(getAbsolutePathEscaped(new File(".")));

		//		System.out.println(getMD5String("/home/martin/data/test8.csv"));

		// String s = "C:\\bla\\blub";
		// System.out.println(s);
		// System.out.println(toCygwinPosixPath(s));

		// System.out.println(FileUtil.isContentEqual(
		// "/home/martin/workspace/ClusterViewer/cluster_data/nctrer_small_3d/002.nctrer.distances.table",
		// "/home/martin/workspace/ClusterViewer/cluster_data/nctrer_small_3d/003.nctrer.distances.table"));
	}

	public static int getNumLines(String file)
	{
		int num = 0;
		try
		{
			BufferedReader r = new BufferedReader(new FileReader(file));
			while (r.readLine() != null)
			{
				num += 1;
			}
			r.close();
			return num;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return -1;
		}
	}

	public static boolean deleteDirectory(File directory)
	{
		if (directory.exists())
		{
			File[] files = directory.listFiles();
			if (files != null)
				for (int i = 0; i < files.length; i++)
					if (files[i].isDirectory())
						deleteDirectory(files[i]);
					else
						files[i].delete();
		}
		return directory.delete();
	}

}
