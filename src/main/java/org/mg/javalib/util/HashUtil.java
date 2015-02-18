package org.mg.javalib.util;

public class HashUtil
{
	public static int hashCode(Object o1, Object o2)
	{
		return 997 * o1.hashCode() ^ 991 * o2.hashCode();
	}

	public static int hashCode(Object o1, Object o2, Object o3)
	{
		return 997 * o1.hashCode() ^ 991 * o2.hashCode() ^ 1009 * o3.hashCode();
	}

	public static int hashCode(Object o1, Object o2, Object o3, Object o4)
	{
		return 997 * o1.hashCode() ^ 991 * o2.hashCode() ^ 1009 * o3.hashCode() ^ 1013 * o4.hashCode();
	}

}
