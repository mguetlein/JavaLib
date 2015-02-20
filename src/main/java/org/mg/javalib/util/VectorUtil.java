package org.mg.javalib.util;

import java.util.Vector;

public class VectorUtil
{
	public static Vector<?> cut(Vector<?> v1, Vector<?> v2)
	{
		Vector<Object> v = new Vector<Object>();

		for (Object object : v1)
			if (v2.contains(object))
				v.add(object);

		return v;
	}

	public static Vector<?> clone(Vector<?> v1)
	{
		Vector<Object> v = new Vector<Object>();
		for (Object object : v1)
			v.add(object);
		return v;
	}

	public static String toString(Vector<Double> v, boolean format)
	{
		String s = "[ ";
		for (Double d : v)
		{
			s += (format ? StringUtil.formatDouble(d) : d) + "; ";
		}
		s = s.substring(0, s.length() - 2);
		s += " ]";
		return s;
	}

	public static String toCSVString(Vector<String> v)
	{
		String s = "";
		for (String st : v)
			s += st + ",";
		if (v.size() > 0)
			s = s.substring(0, s.length() - 1);
		return s;
	}

	public static void main(String[] args)
	{
		System.out.println(toCSVString(new Vector<String>(ArrayUtil.toList(new String[] { "a", "b" }))));
	}
}