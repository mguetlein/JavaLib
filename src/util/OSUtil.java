package util;

public class OSUtil
{
	public static final String OS = System.getProperty("os.name").toLowerCase();

	public static boolean isWindows()
	{
		return OS.contains("win");
	}

	public static boolean isMac()
	{
		return OS.contains("mac");
	}

	public static boolean isUnix()
	{
		return OS.contains("nix") || OS.contains("nux");
	}

	public static void main(String[] args)
	{
		System.out.println(OS);
	}
}
