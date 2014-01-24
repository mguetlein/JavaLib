package dist;

public class TanimotoSimilartiy implements BooleanSimilarityMeasure
{
	@Override
	public Double similarity(Boolean[] b1, Boolean[] b2)
	{
		if (b1.length == 0 || b1.length != b2.length)
			throw new IllegalArgumentException();
		int and = 0;
		int or = 0;
		for (int i = 0; i < b2.length; i++)
		{
			if (b1[i] == null || b2[i] == null)
				continue;
			if (b1[i] && b2[i])
				and++;
			if (b1[i] || b2[i])
				or++;
		}
		if (or == 0)
			return null;
		double d = and / (double) or;
		//		System.out.println("tanimoto:\n" + ArrayUtil.toString(b1) + "\n" + ArrayUtil.toString(b2) + "\n" + d);
		return d;
	}
}
