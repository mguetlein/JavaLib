package gui.property;

import util.StringUtil;

public class PropertyUtil
{
	public static String getPropertyMD5(Property p[])
	{
		if (p == null)
			return "no-props";
		String s = "";
		for (Property property : p)
			s += property.getUniqueName() + " " + property.getValue();
		return StringUtil.getMD5(s);
	}
}
