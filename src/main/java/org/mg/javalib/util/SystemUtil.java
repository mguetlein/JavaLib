package org.mg.javalib.util;

public class SystemUtil
{
	public static boolean isWindows()
	{
		return System.getProperty("os.name").matches("(?i).*windows.*");
	}

	public static boolean isMac()
	{
		return System.getProperty("os.name").toLowerCase().startsWith("mac");
	}
}
