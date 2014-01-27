package dist;

import util.ArrayUtil;

public class EuclideanDistance implements DoubleSimilarityMeasure
{

	@Override
	public java.lang.Double similarity(Double[] t1, Double[] t2)
	{
		return ArrayUtil.euclDistance(ArrayUtil.toPrimitiveDoubleArray(t1), ArrayUtil.toPrimitiveDoubleArray(t2));
	}

}
