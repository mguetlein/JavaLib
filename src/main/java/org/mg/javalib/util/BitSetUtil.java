package org.mg.javalib.util;

import java.util.BitSet;

public class BitSetUtil
{
	public static DoubleArraySummary cardinality(BitSet sets[])
	{
		double d[] = new double[sets.length];
		for (int i = 0; i < d.length; i++)
			d[i] = sets[i].cardinality();
		return DoubleArraySummary.create(d);
	}

	public static int nullBits(BitSet sets[])
	{
		int nullCount = 0;
		for (int i = 0; i < sets[0].size(); i++)
		{
			boolean allNull = true;
			for (BitSet bitSet : sets)
				if (bitSet.get(i))
				{
					allNull = false;
					break;
				}
			if (allNull)
				nullCount++;
		}
		return nullCount;
	}

	public static int redundantBits(BitSet sets[])
	{
		int redundantCount = 0;
		for (int i = 0; i < sets[0].size(); i++)
		{
			boolean allSet = true;
			for (BitSet bitSet : sets)
				if (!bitSet.get(i))
				{
					allSet = false;
					break;
				}
			if (allSet)
				redundantCount++;
		}
		return redundantCount;
	}

	public static double tanimotoSimilarity(BitSet b1, BitSet b2)
	{
		BitSet and = (BitSet) b1.clone();
		and.and(b2);
		BitSet or = (BitSet) b1.clone();
		or.or(b2);
		double numOr = or.cardinality();
		if (numOr == 0)
			return 0;
		return and.cardinality() / (double) numOr;
	}

}
