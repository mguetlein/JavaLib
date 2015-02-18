package org.mg.javalib.dist;

public class NonNullSimilartiy implements BooleanSimilarityMeasure
{
	@Override
	public Double similarity(Boolean[] b1, Boolean[] b2)
	{
		if (b1.length == 0 || b1.length != b2.length)
			throw new IllegalArgumentException();
		int nonNull = 0;
		for (int i = 0; i < b2.length; i++)
			if (b1[i] != null && b2[i] != null)
				nonNull++;
		double d = nonNull / (double) b1.length;
		//		System.out.println("simple matching:\n" + ArrayUtil.toString(b1) + "\n" + ArrayUtil.toString(b2) + "\n" + d);
		return d;
	}
}
