package util;

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
		return fromCSVString(csv, true, -1);
	}

	public static Vector<String> fromCSVString(String csv, int expectedSize)
	{
		return fromCSVString(csv, true, expectedSize);
	}

	public static Vector<String> fromCSVString(String csv, boolean skipEmptyFields, int expectedSize)
	{
		Vector<String> res = new Vector<String>();
		if (csv != null)
		{
			String split[] = csv.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
			for (String s : split)
			{
				s = StringUtil.trimQuotes(s).trim();
				if (!skipEmptyFields || s.length() > 0)
					res.add(s.length() == 0 ? null : s);
			}
			if (!skipEmptyFields)
				for (int i = csv.length() - 1; i > 0; i--)
				{
					if (csv.charAt(i) == ',')
						res.add(null);
					else
						break;
				}
			//			StringTokenizer tok = new StringTokenizer(csv, ",");
			//			while (tok.hasMoreElements())
			//			{
			//				String s = (String) tok.nextElement();
			//				if (!skipEmptyFields || s.trim().length() > 0)
			//					res.add(s.trim());
			//			}
		}
		if (expectedSize != -1)
		{
			if (res.size() == expectedSize + 1 && res.get(res.size() - 1) == null)
				res.remove(res.size() - 1);
			if (res.size() != expectedSize)
				throw new IllegalStateException("csv string has not the expected length: " + res.size() + " != "
						+ expectedSize);
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