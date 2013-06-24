package util;


public class Enumerator
{
	//	@SuppressWarnings("unchecked")
	//	public static <T> LinkedHashMap<T[], Double> enumerate(T elements[], int length)
	//	{
	//		double totalNumPerm = Math.pow(elements.length, length);
	//
	//		//Vector<T[]> v = new Vector<T[]>();
	//		LinkedHashMap<T[], Double> h = new LinkedHashMap<T[], Double>();
	//		T tmp[] = null;
	//		while (tmp == null || tmp[0] != elements[elements.length - 1])
	//		{
	//			if (tmp == null)
	//			{
	//				tmp = (T[]) Array.newInstance(elements[0].getClass(), length);
	//				Arrays.fill(tmp, elements[0]);
	//			}
	//			else
	//			{
	//				tmp = Arrays.copyOf(tmp, length);
	//				int incIndex = -1;
	//				T incrementedElem = null;
	//				for (int i = length - 1; i >= 0; i--)
	//					if (tmp[i] != elements[elements.length - 1])
	//					{
	//						incIndex = i;
	//						incrementedElem = elements[ArrayUtil.indexOf(elements, tmp[i]) + 1];
	//						break;
	//					}
	//				if (incIndex == -1)
	//					throw new Error("should not happen");
	//				for (int i = incIndex; i < length; i++)
	//					tmp[i] = incrementedElem;
	//			}
	//
	//			// compute number of permutations
	//			long perm = MathUtils.factorial(length);
	//			for (int i = 0; i < length; i++)
	//			{
	//				int equals = 1;
	//				for (int j = i + 1; j < length; j++)
	//					if (tmp[i] == tmp[j])
	//						equals++;
	//				perm /= equals;
	//			}
	//
	//			h.put(tmp, perm / (double) totalNumPerm);
	//		}
	//		return h;
	//	}
	//
	//	public static void enumDemo()
	//	{
	//		String s[] = { "1", "2", "3", "4", "5", "W" };
	//		int l = 8;
	//		LinkedHashMap<String[], Double> enumeration = Enumerator.enumerate(s, l);
	//		double sumProp = 0;
	//		for (String[] e : enumeration.keySet())
	//		{
	//			sumProp += enumeration.get(e);
	//			System.out.println(ArrayUtil.toString(e) + " " + enumeration.get(e));
	//		}
	//		System.out.println(sumProp);
	//		System.out.println(Math.pow(s.length, l));
	//	}
	//
	//	public static void main(String args[])
	//	{
	//		Enumerator.enumDemo();
	//	}
}
