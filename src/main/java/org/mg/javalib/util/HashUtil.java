package org.mg.javalib.util;

public class HashUtil
{
	public static final Integer PRIMES[] = { 991, 997, 1009, 1013, 1019, 1021, 1031, 1033, 1039, 1049, 1051, 1061,
			1063, 1069, 1087, 1091, 1093, 1097, 1103, 1109, 1117, 1123, 1129, 1151, 1153, 1163, 1171, 1181, 1187, 1193,
			1201, 1213, 1217, 1223, 1229, 1231, 1237, 1249, 1259, 1277, 1279 };

	public static int hashCode(Object... os)
	{
		if (os.length == 0)
			return 0;
		if (os.length == 1)
			return os[0].hashCode();
		int h = os[0].hashCode() * PRIMES[0];
		for (int i = 1; i < os.length; i++)
			if (os[i] != null)
				h ^= (i * os[i].hashCode() * PRIMES[i]);
		return h;
	}

	public static int hashCode(Object o, Object[] os)
	{
		return hashCode(ArrayUtil.concat(Object.class, new Object[] { o }, os));
	}

	public static void main(String[] args)
	{
	}

}
