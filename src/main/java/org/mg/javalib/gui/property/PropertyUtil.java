package org.mg.javalib.gui.property;

import org.mg.javalib.util.StringUtil;

public class PropertyUtil
{
	public static String getPropertyMD5(Property p[])
	{
		return getPropertyMD5(p, "");
	}

	public static String getPropertyMD5(Property p[], String additionalParam)
	{
		String s = additionalParam;
		if (p == null)
			s += "no-props";
		else
			for (Property property : p)
				s += property.getUniqueName() + " " + property.getValue();
		return StringUtil.getMD5(s);
	}

	public static String toString(Property p[])
	{
		if (p == null || p.length == 0)
			return "";
		String s = "";
		for (Property property : p)
			s += property.getName() + " " + property.getValue() + ", ";
		s = s.substring(0, s.length() - 2);
		return s;
	}

	public static Property getProperty(Property[] properties, String name)
	{
		if (properties == null || properties.length == 0)
			return null;
		for (Property p : properties)
			if (p.getName().equals(name))
				return p;
		return null;
	}
}
