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
		return fromCSVString(csv, true);
	}

	public static Vector<String> fromCSVString(String csv, boolean skipEmptyFields)
	{
		Vector<String> res = new Vector<String>();
		if (csv != null)
		{
			String split[] = csv.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
			for (String s : split)
			{
				s = StringUtil.trimQuotes(s).trim();
				if (!skipEmptyFields || s.length() > 0)
					res.add(s);
			}
			//			StringTokenizer tok = new StringTokenizer(csv, ",");
			//			while (tok.hasMoreElements())
			//			{
			//				String s = (String) tok.nextElement();
			//				if (!skipEmptyFields || s.trim().length() > 0)
			//					res.add(s.trim());
			//			}
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