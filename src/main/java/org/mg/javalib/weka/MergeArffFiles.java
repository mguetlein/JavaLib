package org.mg.javalib.weka;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mg.javalib.util.ArrayUtil;
import org.mg.javalib.util.FileUtil;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.CSVSaver;

public class MergeArffFiles
{
	public static void csvToArff(String inputCSVFile, String outputArffFile) throws IOException
	{
		CSVLoader loader = new CSVLoader();
		loader.setSource(new File(inputCSVFile));
		Instances merged = loader.getDataSet();

		ArffSaver saver = new ArffSaver();
		saver.setInstances(merged);
		saver.setFile(new File(outputArffFile));
		saver.writeBatch();
	}

	public static void merge(String infileDirectory, FilenameFilter filter, String outfilePath)
			throws FileNotFoundException, IOException
	{
		List<String> files = new ArrayList<String>();
		for (String n : new File(infileDirectory).list())
		{
			String s = infileDirectory + File.separator + n;
			if (s.endsWith(".arff") && !s.equals(outfilePath) && filter.accept(new File(infileDirectory), n))
				files.add(s);
		}
		Collections.sort(files);
		if (files.size() > 0)
			merge(ArrayUtil.toArray(files), outfilePath);
	}

	public static void merge(String[] infiles, String outfilePath) throws FileNotFoundException, IOException
	{
		String completeCsv[] = null;

		//		int idx = 0;
		//		System.out.print("reading ");
		for (String s : infiles)
		{
			//System.out.println("reading " + (idx++) + " " + s);
			//			System.out.print(".");
			CSVSaver.runFileSaver(new CSVSaver(), ("-i " + s + " -o " + s + ".csv").split(" "));

			if (completeCsv == null)
				completeCsv = FileUtil.readStringFromFile(s + ".csv").split("\n");
			else
			{
				String c[] = FileUtil.readStringFromFile(s + ".csv").split("\n");
				c = ArrayUtil.removeAt(String.class, c, 0);
				completeCsv = ArrayUtil.concat(completeCsv, c);
			}

			new File(s + ".csv").delete();
		}
		StringBuffer b = new StringBuffer();
		for (String s : completeCsv)
		{
			b.append(s);
			b.append("\n");
		}
		FileUtil.writeStringToFile(outfilePath + ".csv", b.toString());

		CSVLoader loader = new CSVLoader();
		loader.setSource(new File(outfilePath + ".csv"));
		Instances merged = loader.getDataSet();

		new File(outfilePath + ".csv").delete();

		//		Instances inst = new Instances(new FileReader(s));
		//		if (merged == null)
		//			merged = inst;
		//		else
		//			merged = Instances.mergeInstances(merged, inst);
		//
		ArffSaver saver = new ArffSaver();
		saver.setInstances(merged);
		saver.setFile(new File(outfilePath));
		saver.writeBatch();

		//		System.out.println("\nwriting " + outfilePath);

	}

	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		//csvToArff("/home/martin/data/arffs/nctrer.csv", "/home/martin/data/arffs/nctrer.arff");
		merge("/home/martin/workspace/CFPMiner/results", new FilenameFilter()
		{

			@Override
			public boolean accept(File dir, String name)
			{
				return name.contains("RaF") && name.contains("fold");
			}
		}, "/home/martin/workspace/CFPMiner/results/merged.arff");
	}
}
