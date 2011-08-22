package util;

import java.awt.FontMetrics;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.StringTokenizer;

public class StringUtil
{
	public static String trimQuotes(String value)
	{
		if (value == null)
			return value;

		value = value.trim();
		if (value.startsWith("\"") && value.endsWith("\""))
			return value.substring(1, value.length() - 1);

		return value;
	}

	static String toCamelCase(String s)
	{
		String[] parts = s.split("_");
		String camelCaseString = "";
		for (String part : parts)
		{
			camelCaseString = camelCaseString + toProperCase(part);
		}
		return camelCaseString;
	}

	static String toProperCase(String s)
	{
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}

	public static int[] indicesOf(String s, String search)
	{
		List<Integer> indices = new ArrayList<Integer>();
		int index = s.indexOf(search);
		while (index != -1)
		{
			indices.add(index);
			index = s.indexOf(search, index + 1);
		}
		return ArrayUtil.toPrimitiveIntArray(indices);
	}

	public static int numOccurences(String s, String search)
	{
		return indicesOf(s, search).length;
	}

	public static int compareTo(String s1, String s2)
	{
		if (s1 == null || s2 == null)
			throw new NullPointerException();

		int start = getPrefix(s1, s2).length();
		int end = getSuffix(s1, s2).length();

		String sub1;
		String sub2;
		if (start == s1.length() || end == s1.length())
			sub1 = "";
		else
			sub1 = s1.substring(start, s1.length() - end);
		if (start == s2.length() || end == s2.length())
			sub2 = "";
		else
			sub2 = s2.substring(start, s2.length() - end);

		try
		{
			Integer i1 = Integer.parseInt(sub1);
			Integer i2 = Integer.parseInt(sub2);
			return i1.compareTo(i2);
		}
		catch (NumberFormatException e)
		{
			return s1.compareTo(s2);
		}
	}

	public static String getPrefix(String s1, String s2)
	{
		return getCommon(s1, s2, true);
	}

	public static String getSuffix(String s1, String s2)
	{
		return getCommon(s1, s2, false);
	}

	private static String getCommon(String s1, String s2, boolean forward)
	{
		if (s1 == null || s2 == null)
			return null;

		String shortString = s1;
		String longString = s2;
		if (s2.length() < s1.length())
		{
			shortString = s2;
			longString = s1;
		}
		String suffix = "";
		int begin = forward ? 0 : shortString.length() - 1;
		int end = forward ? shortString.length() : -1;
		int step = forward ? 1 : -1;
		int longOffset = forward ? 0 : longString.length() - shortString.length();
		for (int i = begin; i != end; i += step)
		{
			if (shortString.charAt(i) == longString.charAt(longOffset + i))
				suffix = forward ? (suffix + shortString.charAt(i)) : (shortString.charAt(i) + suffix);
			else
				break;
		}
		// remove leading/trailing digits to get the full number
		if (forward)
			suffix = removeTrailingDigits(suffix);
		else
			suffix = removeLeadingDigits(suffix);
		return suffix;
	}

	public static String removeLeadingDigits(String s)
	{
		int i;
		for (i = 0; i < s.length(); i++)
			if (!Character.isDigit(s.charAt(i)))
				break;
		return s.substring(i);
	}

	public static String removeTrailingDigits(String s)
	{
		int i;
		for (i = s.length() - 1; i >= 0; i--)
			if (!Character.isDigit(s.charAt(i)))
				break;
		return s.substring(0, i + 1);
	}

	public static String[] splitString(String s, String delim)
	{
		StringTokenizer tok = new StringTokenizer(s, delim);
		List<String> l = new ArrayList<String>();
		while (tok.hasMoreTokens())
			l.add(tok.nextToken().trim());
		String res[] = new String[l.size()];
		l.toArray(res);
		return res;
	}

	public static double[] splitStringToDoubles(String s, String delim)
	{
		String strings[] = splitString(s, delim);
		double d[] = new double[strings.length];
		for (int i = 0; i < d.length; i++)
			d[i] = Double.parseDouble(strings[i]);
		return d;
	}

	public static int[] splitStringToInt(String s, String delim)
	{
		String strings[] = splitString(s, delim);
		int d[] = new int[strings.length];
		for (int i = 0; i < d.length; i++)
			d[i] = Integer.parseInt(strings[i]);
		return d;
	}

	public static String formatDouble(double d)
	{
		return formatDouble(d, Locale.getDefault());
	}

	public static String formatDouble(double d, Locale l)
	{
		Locale.setDefault(l);
		DecimalFormat df = new DecimalFormat("#.##");
		return df.format(d);
	}

	public static String formatDouble(double d, int numDecimalPlaces)
	{
		return formatDouble(d, numDecimalPlaces, -1);
	}

	public static String formatDouble(double d, int numDecimalPlaces, int completeStringSize)
	{
		char[] dec = new char[numDecimalPlaces];
		Arrays.fill(dec, '0');
		DecimalFormat df = new DecimalFormat("0." + new String(dec));
		if (completeStringSize != -1)
			return StringUtil.concatWhitespace(df.format(d), completeStringSize, false);
		else
			return df.format(d);
	}

	public static int computeLineCount(FontMetrics f, String s, int width)
	{
		if (!s.contains("\n"))
		{
			int sWidth = f.stringWidth(s.trim());
			if (sWidth < width)
				return 1;
			else
				return ((sWidth / width) + 1);
		}
		else
		{
			String ss = s.replace("\n\n", "\n \n");
			StringTokenizer t = new StringTokenizer(ss, "\n");

			int lineCount = 0;
			while (t.hasMoreTokens())
				lineCount += computeLineCount(f, t.nextToken(), width);
			return lineCount;
		}
	}

	public static String formatTime(long time)
	{
		int seconds = (int) (time / 1000);
		int days = seconds / 86400;
		int leftover = seconds % 86400;
		int hrs = leftover / 3600;
		leftover %= 3600;
		int mins = leftover / 60;
		int secs = leftover % 60;

		StringBuffer b = new StringBuffer();
		Formatter f = new Formatter(b);
		f.format("%02d:%02d:%02d:%02d", days, hrs, mins, secs);
		return b.toString();
	}

	public static String getTimeStamp(long starttime)
	{
		Date now = new Date();
		return "now: " + new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(now) + ", run-time: "
				+ StringUtil.formatTime(now.getTime() - starttime);

	}

	public static String whitespace(int length)
	{
		return charString(' ', length);
	}

	public static String charString(char c, int length)
	{
		char[] empty = new char[length];
		Arrays.fill(empty, c);
		return new String(empty);
	}

	public static String concatWhitespace(String string, int length)
	{
		return concatChar(string, length, ' ');
	}

	public static String concatChar(String string, int length, char c)
	{
		return concatChar(string, length, c, true);
	}

	public static String concatWhitespace(String string, int length, boolean alignLeft)
	{
		return concatChar(string, length, ' ', alignLeft);
	}

	public static String concatChar(String string, int length, char c, boolean alignLeft)
	{
		String s = string + "";
		if (s.length() >= length)
			return s;
		else
		{
			if (alignLeft)
				return s + charString(c, length - string.length());
			else
				return charString(c, length - string.length()) + s;
		}
	}

	public static String randomString()
	{
		return randomString(1, 1000, new Random());
	}

	public static String randomString(int minLength, int maxLength, Random r)
	{
		return randomString(minLength, maxLength, r, true);
	}

	public static String randomString(int minLength, int maxLength, Random r, boolean emptySpaceAllowed)
	{
		String chars = "abcdefghijklmnopqrstuvwxyz";
		if (emptySpaceAllowed)
			chars = "         " + chars;
		String s = "";
		double p = 1 / (double) (maxLength - minLength);
		while (s.length() < maxLength
				&& (s.length() < minLength || r.nextDouble() < (1 - ((s.length() - minLength) * p))))
		{
			// System.out.println(s.length() + " " + (1 - ((s.length() - minLength) * p)));
			s += chars.charAt(r.nextInt(chars.length()));
		}
		return s;
	}

	public static boolean contains(String string, String searchString)
	{
		return string.matches(".*" + searchString + ".*");
	}

	public static boolean containsOne(String string, String[] searchStrings)
	{
		for (String a : searchStrings)
			if (string.matches(".*" + a + ".*"))
				return true;
		return false;
	}

	public static void main(String[] args)
	{
		System.out.println(toCamelCase("ene_mene_miste"));
		System.out.println(toProperCase("EneMeneMiste"));

		//		System.out.println(ArrayUtil.toString(indicesOf("c-c-c-c", "-")));
		//		System.out.println(ArrayUtil.toString(indicesOf("c-c-c-c", "x")));

		// System.out.println(getSuffix("a1", "a11"));

		// System.out.println(getSuffix("abac12.sdf", "abac53.sdf"));
		// System.out.println(getPrefix("abac12.sdf", "abac53.sdf"));
		//
		// System.out.println(compareTo("abac12.sdf", "abac53.sdf"));

		// System.out.println(computeLineCount(new JTable().getFontMetrics(new JTable().getFont()), "1\n\n \n2", 30));

		// long startTime = new Date().getTime();
		// try
		// {
		// Thread.sleep(3000);
		// }
		// catch (InterruptedException e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// System.out.println(getTimeStamp(startTime));

		// for (int i = 0; i < 1000; i++)
		// {
		// String s = randomString();
		// System.out.println(s.length() + " " + s);
		// }
	}

	public static boolean isInteger(String integer)
	{
		try
		{
			Integer.parseInt(integer);
			return true;
		}
		catch (NumberFormatException e)
		{
			return false;
		}
	}
}
