package org.mg.javalib.util;

public class CharUtil
{
	public static boolean isHexChar(char c)
	{
		return Character.isDigit(c)
				|| ArrayUtil.indexOf(new Character[] { 'A', 'B', 'C', 'D', 'E', 'F' },
						new Character(Character.toUpperCase(c))) != -1;
	}
}
