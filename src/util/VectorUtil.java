package util;

import java.util.StringTokenizer;
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

	public static Vector<String> fromCSVString(String csv)
	{
		Vector<String> res = new Vector<String>();
		if (csv != null)
		{
			StringTokenizer tok = new StringTokenizer(csv, ",");
			while (tok.hasMoreElements())
			{
				String s = (String) tok.nextElement();
				if (s.trim().length() > 0)
					res.add(s);
			}
		}
		return res;
	}

	public static String toCSVString(Vector<String> v)
	{
		String s = "";
		for (String st : v)
			s += st + ",";
		return s;
	}
}