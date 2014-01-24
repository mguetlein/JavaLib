package dist;

public interface SimilarityMeasure<T>
{
	public Double similarity(T[] t1, T[] t2);
}
