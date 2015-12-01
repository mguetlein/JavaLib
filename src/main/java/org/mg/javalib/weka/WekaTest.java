package org.mg.javalib.weka;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.apache.commons.lang3.ArrayUtils;
import org.mg.javalib.util.ArrayUtil;
import org.mg.javalib.util.StringUtil;
import org.mg.javalib.util.SwingUtil;

import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.ThresholdVisualizePanel;

public class WekaTest
{
	static double last = 0;

	static int x, y;

	static List<JPanel> panels = new ArrayList<>();
	static int rows;
	static int cols;

	public static void main(String args[]) throws Exception
	{
		//		String actual, predicted;
		//
		//		actual/*   */= "1,0,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";
		//		predicted/**/= "x,y,9,8,7,6,5,4,3,2,8,7,7,6,6,6,5,5,5,5,4,4,4,4,4,3,3,3,3,3,3,2,2,2,2,2,2,2,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0";
		//		x = 9;
		//		y = 0;
		//		eval(actual, predicted);
		//
		//		x = 6;
		//		y = 0;
		//		eval(actual, predicted);
		//
		//		x = 9;
		//		y = 8;
		//		eval(actual, predicted);

		panels.clear();
		rows = 0;
		cols = 2;

		System.out.println("\n1");
		eval2("11111101111110110001000000000000000000100000000000000000000000");
		eval2("11111101111110110001000000000000000000000100000000000000000000");
		//			eval2("11111101111110110001000000000000000000000000100000000000000000");
		last = 0;
		//		rows++;

		System.out.println("\n2");
		eval2("11111101111110110001000000000000000000100000000000000000000000");
		eval2("11111101111110110000001000000000000000100000000000000000000000");
		//			eval2("11111101111110110000000001000000000000100000000000000000000000");
		last = 0;
		//		rows++;

		System.out.println("\n3");
		eval2("11111101111110110001000000000000000000100000000000000000000000");
		eval2("11111101110111110001000000000000000000100000000000000000000000");
		//			eval2("11111100111111110001000000000000000000100000000000000000000000");
		last = 0;
		//		rows++;

		System.out.println("\n4");
		eval2("11111101111110110001000000000000000000100000000000000000000000");
		eval2("11101111111110110001000000000000000000100000000000000000000000");
		//			eval2("01111111111110110001000000000000000000100000000000000000000000");
		last = 0;
		//		rows++;

		plot();
		System.out.println("\n");
	}

	public static void eval2(String rank, String msg) throws Exception
	{
		System.out.print(msg + ": ");
		eval2(rank);
	}

	public static void eval2(String rank) throws Exception
	{
		System.out.println(rank);
		final String clazzes[] = ArrayUtil.toStringArray(ArrayUtils.toObject(rank.toCharArray()));
		final List<double[]> predictions = new ArrayList<>();
		double step = 1 / (double) (clazzes.length - 1);
		for (int i = 0; i < clazzes.length; i++)
		{
			double p = 1 - (i * step);
			//			System.out.println(p);
			predictions.add(new double[] { 1 - p, p });
		}
		eval(clazzes, predictions);
	}

	public static void eval(String actual, String predicted) throws Exception
	{
		final String clazzes[] = actual.split(",");
		final List<double[]> predictions = new ArrayList<>();
		for (String p : predicted.split(","))
		{
			double p1;
			if (p.equals("x"))
				p1 = x * 0.1;
			else if (p.equals("y"))
				p1 = y * 0.1;
			else
				p1 = Double.parseDouble("." + p);
			predictions.add(new double[] { 1.0 - p1, p1 });
		}
		eval(clazzes, predictions);
	}

	public static void eval(final String[] clazzes, final List<double[]> predictions) throws Exception
	{
		Instances inst = ArffWriter.toInstances(new ArffWritable()
		{
			@Override
			public boolean isSparse()
			{
				return false;
			}

			@Override
			public boolean isInstanceWithoutAttributeValues(int instance)
			{
				return false;
			}

			@Override
			public String getRelationName()
			{
				return "bla";
			}

			@Override
			public int getNumInstances()
			{
				return clazzes.length;
			}

			@Override
			public int getNumAttributes()
			{
				return 1;
			}

			@Override
			public String getMissingValue(int attribute)
			{
				return null;
			}

			@Override
			public String getAttributeValueSpace(int attribute)
			{
				return "{0,1}";
			}

			@Override
			public String getAttributeValue(int instance, int attribute) throws Exception
			{
				return clazzes[instance];
			}

			@Override
			public String getAttributeName(int attribute)
			{
				return "clazz";
			}

			@Override
			public List<String> getAdditionalInfo()
			{
				return null;
			}
		});
		inst.setClassIndex(0);

		Classifier cl = new Classifier()
		{
			int predCount = 0;

			@Override
			public Capabilities getCapabilities()
			{
				return null;
			}

			@Override
			public double[] distributionForInstance(Instance instance) throws Exception
			{
				return predictions.get(predCount++);
			}

			@Override
			public double classifyInstance(Instance instance) throws Exception
			{
				throw new RuntimeException();
			}

			@Override
			public void buildClassifier(Instances data) throws Exception
			{
			}
		};

		Evaluation eval = new Evaluation(inst);
		eval.evaluateModel(cl, inst);
		double times = 100;
		int dec = 1;
		//		eval.setMetricsToDisplay(ArrayUtil.toList(Evaluation.BUILT_IN_EVAL_METRICS));
		//		System.out.println("num       " + eval.numInstances());
		//		System.out.println("accuracy  " + StringUtil.formatDouble(0.01 * eval.pctCorrect(), dec));

		String p;
		double d;
		double diff = 0;

		for (Boolean auc : new Boolean[] { true, false })
		{
			if (auc)
			{
				p = "AUC";
				d = eval.areaUnderROC(1);
			}
			else
			{
				p = "AUP";
				d = eval.areaUnderPRC(1);
			}

			d *= times;
			if (last != 0)
				diff = d - last;
			last = d;
			String s = StringUtil.formatDouble(d, dec);
			if (diff != 0)
			{
				if (diff > 0)
					s += " +" + StringUtil.formatDouble(diff, dec);
				else
					s += " " + StringUtil.formatDouble(diff, dec);
			}
			System.out.println(p + " " + s);

			ThresholdCurve tc = new ThresholdCurve();
			// method visualize
			ThresholdVisualizePanel vmc = new ThresholdVisualizePanel();
			vmc.setROCString("(Area under ROC = " + Utils.doubleToString(eval.areaUnderPRC(1), 4) + ")");
			vmc.setName("name");
			Instances curve = tc.getCurve(eval.predictions());
			PlotData2D tempd = new PlotData2D(curve);
			tempd.setPlotName("name2");
			tempd.addInstanceNumberAttribute();
			// specify which points are connected
			boolean[] cp = new boolean[curve.numInstances()];
			for (int n = 1; n < cp.length; n++)
				cp[n] = true;
			tempd.setConnectPoints(cp);
			// add plot
			vmc.addPlot(tempd);
			if (!auc)
			{
				cp[cp.length - 1] = false;
				vmc.setXIndex(8);
				vmc.setYIndex(7);
			}
			JPanel panel = vmc.getPlotPanel(); // vmc;
			panel.setPreferredSize(new Dimension(220, 220));
			panels.add(panel);
		}
		//		System.out.println("f-measure " + StringUtil.formatDouble(eval.fMeasure(1)));
		//		System.out.println("recall    " + StringUtil.formatDouble(eval.recall(1)));
		//		System.out.println("precision " + StringUtil.formatDouble(eval.precision(1)));

		//		System.out.println();

	}

	public static void plot()
	{
		JPanel p = new JPanel(new GridLayout(rows, cols, 5, 5));
		for (JPanel pa : panels)
			p.add(pa);
		SwingUtil.showInFrame(p);
		//		SwingUtil.toFile("/tmp/aup-" + auc + ".png", p, p.getPreferredSize());
	}
}
