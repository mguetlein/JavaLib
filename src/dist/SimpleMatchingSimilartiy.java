package dist;

public class SimpleMatchingSimilartiy implements BooleanSimilarityMeasure
{
	@Override
	public Double similarity(Boolean[] b1, Boolean[] b2)
	{
		if (b1.length == 0 || b1.length != b2.length)
			throw new IllegalArgumentException();
		int eq = 0;
		int len = 0;
		for (int i = 0; i < b2.length; i++)
		{
			if (b1[i] == null || b2[i] == null)
				continue;
			len++;
			if (b1[i] == b2[i])
				eq++;
		}
		if (len == 0)
			return null;
		double d = eq / (double) len;
		//		System.out.println("simple matching:\n" + ArrayUtil.toString(b1) + "\n" + ArrayUtil.toString(b2) + "\n" + d);
		return d;
	}
}
