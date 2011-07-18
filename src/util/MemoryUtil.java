package util;

public class MemoryUtil
{
	public static String getUsedMemoryString()
	{
		return formatByte(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
	}

	public static String formatByte(long bytes)
	{
		double kb = bytes / 1024.0;

		if (kb < 1)
			return bytes + "bytes";
		else
		{
			double mb = kb / 1024.0;

			if (mb < 1)
				return StringUtil.formatDouble(kb) + "kb";
			else
				return StringUtil.formatDouble(mb) + "mb";
		}
	}

	public static void main(String args[])
	{
		System.out.println(getUsedMemoryString());
		@SuppressWarnings("unused")
		int n[] = new int[100000000];
		System.out.println(getUsedMemoryString());
		n = null;
		System.out.println(getUsedMemoryString());
		System.gc();
		System.out.println(getUsedMemoryString());
	}
}
