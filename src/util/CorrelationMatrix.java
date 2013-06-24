package util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

public abstract class CorrelationMatrix<T>
{
	String rowInfo[];
	String cellInfo[][];
	Double matrix[][];
	Color color[][];

	private int minNumValues = 2;

	public int getMinNumValues()
	{
		return minNumValues;
	}

	public void setMinNumValues(int minNumValues)
	{
		this.minNumValues = minNumValues;
	}

	public abstract double correlation(T v1[], T v2[]);

	public abstract String rowInfo(T values[]);

	public abstract String cellInfo(T v1[], T v2[]);

	public abstract Color color(double correlation);

	public double rmse(CorrelationMatrix<?> m)
	{
		if (matrix == null || m.getMatrix() == null)
			throw new IllegalStateException();
		double sum = 0;
		double count = 0;
		int missmatchCount = 0;
		for (int i = 0; i < matrix.length; i++)
		{
			for (int j = 0; j < matrix.length; j++)
			{
				if (matrix[i][j] == null)
				{
					if (m.getMatrix()[i][j] != null)
						missmatchCount++;
				}
				else
				{
					if (m.getMatrix()[i][j] == null)
						missmatchCount++;
					else
					{
						sum += Math.pow(matrix[i][j] - m.getMatrix()[i][j], 2);
						count++;
					}
				}
			}
		}
		if (missmatchCount > 0)
		{
			System.err.println("warning, null values do not match in " + missmatchCount + "/"
					+ (int) (missmatchCount + count) + " cases");
			System.err.flush();
		}
		double dist = Math.sqrt(sum / (double) count);
		return dist;
	}

	public abstract static class BooleanCorrelationMatrix extends CorrelationMatrix<Boolean>
	{
		@Override
		public String rowInfo(Boolean values[])
		{
			if (values == null)
				return "0/0";
			int numTrue = 0, numFalse = 0;
			for (Boolean bb : values)
				if (bb)
					numTrue++;
				else
					numFalse++;
			return numFalse + "/" + numTrue;
		}

		@Override
		public String cellInfo(Boolean[] v1, Boolean[] v2)
		{
			return rowInfo(v1) + " " + rowInfo(v2);
		}
	}

	public abstract static class DoubleCorrelationMatrix extends CorrelationMatrix<Double>
	{
		@Override
		public String rowInfo(Double values[])
		{
			if (values == null)
				return "0";
			else
				return values.length + "";
		}

		@Override
		public String cellInfo(Double[] v1, Double[] v2)
		{
			return rowInfo(v1);
		}
	}

	public static double pearsonCorrelation(double d1[], double d2[])
	{
		PearsonsCorrelation pc = new PearsonsCorrelation();
		return pc.correlation(d1, d2);
	}

	public static Color pearsonColor(double cor)
	{
		return ColorUtil.getThreeColorGradient((cor + 1) / 2, Color.RED, Color.WHITE, Color.RED);
	}

	public static class PearsonDoubleCorrelationMatrix extends DoubleCorrelationMatrix
	{
		@Override
		public double correlation(Double[] v1, Double[] v2)
		{
			return pearsonCorrelation(ArrayUtil.toPrimitiveDoubleArray(v1), ArrayUtil.toPrimitiveDoubleArray(v2));
		}

		@Override
		public Color color(double correlation)
		{
			return pearsonColor(correlation);
		}
	}

	public static class PearsonBooleanCorrelationMatrix extends BooleanCorrelationMatrix
	{
		@Override
		public double correlation(Boolean[] v1, Boolean[] v2)
		{
			double d1[] = new double[v1.length];
			double d2[] = new double[v1.length];
			for (int i = 0; i < d2.length; i++)
			{
				d1[i] = v1[i] ? 1.0 : 0.0;
				d2[i] = v2[i] ? 1.0 : 0.0;
			}
			return pearsonCorrelation(d1, d2);
		}

		@Override
		public Color color(double correlation)
		{
			return pearsonColor(correlation);
		}
	}

	/**
	 * Object has to be either Double or Boolean 
	 * 
	 * @param v
	 * @return
	 */
	public void computeMatrix(List<T[]> v)
	{
		rowInfo = new String[v.size()];
		for (int i = 0; i < rowInfo.length; i++)
		{
			List<T> d1 = new ArrayList<T>();
			for (int k = 0; k < v.get(i).length; k++)
				if (v.get(i)[k] != null)
					d1.add(v.get(i)[k]);
			T v1[] = ArrayUtil.toArray(d1);
			rowInfo[i] = rowInfo(v1);
		}

		matrix = new Double[v.size()][v.size()];
		color = new Color[v.size()][v.size()];
		cellInfo = new String[v.size()][v.size()];
		for (int i = 0; i < matrix.length; i++)
		{
			for (int j = 0; j < matrix.length; j++)
			{
				if (i == j)
					continue;
				List<T> d1 = new ArrayList<T>();
				List<T> d2 = new ArrayList<T>();
				for (int k = 0; k < v.get(i).length; k++)
				{
					if (v.get(i)[k] != null && v.get(j)[k] != null)
					{
						d1.add(v.get(i)[k]);
						d2.add(v.get(j)[k]);
					}
				}
				T v1[] = d1.size() > 0 ? ArrayUtil.toArray(d1) : null;
				T v2[] = d2.size() > 0 ? ArrayUtil.toArray(d2) : null;
				cellInfo[i][j] = cellInfo(v1, v2);
				if (d1.size() >= minNumValues)
				{
					double cor = correlation(v1, v2);
					if (!Double.isNaN(cor) && !Double.isInfinite(cor))
					{
						matrix[i][j] = cor;
						color[i][j] = color(cor);
					}
				}
			}
		}
	}

	public String[] getRowInfo()
	{
		return rowInfo;
	}

	public String[][] getCellInfo()
	{
		return cellInfo;
	}

	public Double[][] getMatrix()
	{
		return matrix;
	}

	public Color[][] getColor()
	{
		return color;
	}
}
