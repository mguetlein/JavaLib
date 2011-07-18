package io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import util.FileUtil;

public class FileResultCacher
{
	String finalInfile;
	String finalOutfile;

	public interface InFileWriter
	{
		public void write(String infile);
	}

	public interface OutFileWriter
	{
		public void write(String infile, String outfile);
	}

	public FileResultCacher(String infileName, String outfileName, InFileWriter infileWriter,
			OutFileWriter outfileWriter)
	{
		//		System.out.println("infile: " + infileName);
		//		System.out.println("outfile: " + outfileName);

		List<String[]> existingPairs = new ArrayList<String[]>();
		int increment = 1;
		while (true)
		{
			boolean in = new File(filename(infileName, increment)).exists();
			boolean out = new File(filename(outfileName, increment)).exists();
			if (in && out)
				existingPairs.add(new String[] { filename(infileName, increment), filename(outfileName, increment) });
			if (!in && !out)
				break;
			increment++;
		}
		//		System.out.println("found " + (increment - 1) + " already exising input-output file pairs");

		finalInfile = filename(infileName, increment);
		//		System.out.println("inputfilename -> " + finalInfile + ", write to infile now");
		infileWriter.write(finalInfile);

		finalOutfile = null;
		for (String[] exists : existingPairs)
		{
			if (FileUtil.isContentEqual(finalInfile, exists[0]))
			{
				finalOutfile = exists[1];
				System.out.println("equal inputfile found (" + exists[0] + "), using corresponding outputfile: "
						+ finalOutfile + " (deleting new inputfile again)");
				new File(finalInfile).delete();
				finalInfile = exists[0];
				break;
			}
			//			else
			//				System.out.println("not equal: " + exists[0]);
		}

		if (finalOutfile == null)
		{
			finalOutfile = filename(outfileName, increment);
			//			System.out.println("no equal inputfile found");
			//			System.out.println("outputfilename -> " + finalOutfile + ", write to outfile now");
			outfileWriter.write(finalInfile, finalOutfile);
		}

	}

	public String getInfile()
	{
		return finalInfile;
	}

	public String getOufile()
	{
		return finalOutfile;
	}

	private static String filename(String filename, int increment)
	{
		String i = increment + "";
		while (i.length() < 3)
			i = "0" + i;
		String p = FileUtil.getParent(filename);
		if (p == null)
			return i + "." + FileUtil.getFilename(filename);
		else
			return p + File.separator + i + "." + FileUtil.getFilename(filename);
	}

	public static void main(String args[])
	{

		FileResultCacher h = new FileResultCacher("/home/martin/tmp/in.txt", "/home/martin/tmp/out.txt",
				new InFileWriter()
				{

					@Override
					public void write(String filename)
					{
						FileUtil.writeStringToFile(filename, "frage?");
					}
				}, new OutFileWriter()
				{

					@Override
					public void write(String infile, String outfile)
					{
						FileUtil.writeStringToFile(outfile, "antwort!");
					}
				});
		System.out.println("result is in: " + h.getOufile());

	}

}
