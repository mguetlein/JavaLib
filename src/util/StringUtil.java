package util;

import java.awt.FontMetrics;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64;

import util.FileUtil.UnexpectedNumColsException;

import com.csvreader.CsvReader;

public class StringUtil
{
	public static String trimQuotes(String value)
	{
		if (value == null)
			return value;

		value = value.trim();
		if (value.startsWith("\"") && value.endsWith("\""))
			return value.substring(1, value.length() - 1);
		else if (value.startsWith("\'") && value.endsWith("\'"))
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

	public static int compare(String s1, String s2)
	{
		if (s1 == null)
			if (s2 == null)
				return 0;
			else
				return -1;
		else if (s2 == null)
			return 1;
		else
			return s1.compareTo(s2);
	}

	public static int compareFilenames(String s1, String s2)
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

	public static List<String> split(String input)
	{
		return split(input, ',');
	}

	public static List<String> split(String input, char sep)
	{
		try
		{
			return split(input, false, -1, sep);
		}
		catch (UnexpectedNumColsException e)
		{
			throw new Error("should never happen");
		}
	}

	public static List<String> split(String input, boolean skipEmptyFields)
	{
		try
		{
			return split(input, skipEmptyFields, -1, ',');
		}
		catch (UnexpectedNumColsException e)
		{
			throw new Error("should never happen");
		}
	}

	public static List<String> split(String input, boolean skipEmptyFields, int expectedNumCols, char sep)
			throws UnexpectedNumColsException
	{
		List<String> result = new ArrayList<String>();
		if (input != null && input.trim().length() > 0)
		{
			CsvReader csv = new CsvReader(new StringReader(input), sep);
			csv.setSkipEmptyRecords(skipEmptyFields);
			csv.setTrimWhitespace(true);
			try
			{
				csv.readRecord();
				for (int i = 0; i < csv.getColumnCount(); i++)
				{
					String v = csv.get(i);
					if (v.length() == 0 && !skipEmptyFields)
						result.add(null);
					else
						result.add(v);
				}
			}
			catch (IOException e)
			{
				//should not throw io exception, as parsed from local string
				e.printStackTrace();
			}
		}
		if (expectedNumCols != -1)
		{
			if (result.size() == expectedNumCols + 1 && result.get(result.size() - 1) == null)
				result.remove(result.size() - 1);
			if (result.size() != expectedNumCols)
				throw new UnexpectedNumColsException("csv string has not the expected length: " + result.size()
						+ " != " + expectedNumCols);
		}
		return result;
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
		Arrays.fill(dec, '#');
		String decString = new String(dec);
		if (dec.length > 0)
			decString = "." + decString;
		DecimalFormat df = new DecimalFormat("#" + decString);
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

	public static String compress(String str)
	{
		try
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			GZIPOutputStream gzip = new GZIPOutputStream(out);
			gzip.write(str.getBytes());
			gzip.close();
			return new String(Base64.encodeBase64URLSafe(out.toByteArray()));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static String deCompress(String str)
	{
		try
		{
			byte[] bytes = Base64.decodeBase64(str);
			GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(bytes));
			BufferedReader b = new BufferedReader(new InputStreamReader(gzip));
			StringBuffer res = new StringBuffer();
			String l = null;
			while ((l = b.readLine()) != null)
			{
				res.append(l);
				res.append("\n");
			}
			return res.toString();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static String concat(String... s)
	{
		StringBuffer sb = new StringBuffer();
		for (String string : s)
			sb.append(string);
		return sb.toString();
	}

	public static String concatNaive(String... s)
	{
		String res = "";
		for (String string : s)
			res += string;
		return res;
	}

	public static void main(String[] args)
	{
		List<String> l = new ArrayList<String>();
		for (int i = 0; i < 40; i++)
			l.add(randomString());
		String s[] = ArrayUtil.toArray(l);

		for (boolean b : new boolean[] { false, true })
		{
			for (int i = 0; i < 50000; i++)
			{
				StopWatchUtil.start(b ? "naive" : "buffer");
				if (b)
					concatNaive(s);
				else
					concat(s);
				StopWatchUtil.stop(b ? "naive" : "buffer");
			}

		}
		StopWatchUtil.print();
		System.exit(0);

		System.out.println(ListUtil.toString(StringUtil.split(",1,\"2','3\",3,4,,")));
		System.exit(1);

		String string = "#---No Comment---\n"
				+ "#Mon Jan 09 15:39:24 CET 2012\n"
				+ "property-Smarts\\ matching\\ software\\ for\\ smarts\\ files=OpenBabel\n"
				+ "property-The\\ number\\ of\\ dimensions\\ to\\ use\\ in\\ reduction\\ method\\ (initial_dims)=17\n"
				+ "Align\\ Compounds-method=Maximum Common Subgraph (MCS) Aligner\n"
				+ "Embed\\ into\\ 3D\\ Space-simple-selected=false\n"
				+ "features-cdk=\"ALogP\",\"Acidic Group Count\",\"Aromatic Atoms Count\",\"Aromatic Bonds Count\",\"Element Count\",\"Basic Group Count\",\"Bond Count\",\"Largest Chain\",\"Largest Pi Chain\",\"Longest Aliphatic Chain\",\"Mannhold LogP\",\"Rotatable Bonds Count\",\"Lipinski's Rule of Five\",\"Molecular Weight\",\"XLogP\",\n"
				+ "property-Maximum\\ number\\ of\\ iterations\\ (max_iter)=1000\n"
				+ "dataset-current-dir=/home/martin/data\n"
				+ "Embed\\ into\\ 3D\\ Space-method=PCA 3D Embedder (R)\n"
				+ "bin-path-Rscript=/usr/games/Rscript\n"
				+ "property-Skip\\ fragments\\ that\\ match\\ all\\ compounds=true\n"
				+ "property-Optimal\\ number\\ of\\ neighbors\\ (perplexity)=7\n"
				+ "property-Maximum\\ number\\ of\\ iterations\\ (itmax)=150\n"
				+ "property-forcefield=mm2\n"
				+ "Create\\ 3D\\ Structures-simple-selected=false\n"
				+ "Align\\ Compounds-simple-selected=false\n"
				+ "bin-path-babel=/usr/bin/babel\n"
				+ "Cluster\\ Dataset-simple-selected=true\n"
				+ "property-Minimum\\ frequency=5\n"
				+ "Create\\ 3D\\ Structures-method=No 3D Structure Generation (use original structures)\n"
				+ "dataset-recently-used=null\\#/home/martin/data/bbp2.sdf\\#bbp2.sdf,http\\://www.cheminformatics.org/datasets/funar-timofei/funar-timofei.3d.sdf\\#/home/martin/.ches-mapper/http%3A%2F%2Fwww.cheminformatics.org%2Fdatasets%2Ffunar-timofei%2Ffunar-timofei.3d.sdf\\#http\\://www.cheminformatics.org/datasets/funar-timofei/funar-timofei.3d.sdf,http\\://opentox.informatik.uni-freiburg.de/ches-mapper/data/caco2.sdf\\#/home/martin/.ches-mapper/http%3A%2F%2Fopentox.informatik.uni-freiburg.de%2Fches-mapper%2Fdata%2Fcaco2.sdf\\#http\\://opentox.informatik.uni-freiburg.de/ches-mapper/data/caco2.sdf,http\\://opentox.informatik.uni-freiburg.de/ches-mapper/data/NCTRER_v4b_232_15Feb2008.ob3d.sdf\\#/home/martin/.ches-mapper/http%3A%2F%2Fopentox.informatik.uni-freiburg.de%2Fches-mapper%2Fdata%2FNCTRER_v4b_232_15Feb2008.ob3d.sdf\\#http\\://opentox.informatik.uni-freiburg.de/ches-mapper/data/NCTRER_v4b_232_15Feb2008.ob3d.sdf,\n"
				+ "property-maxNumClusters=10\n" + "Embed\\ into\\ 3D\\ Space-simple-yes=true\n"
				+ "property-minNumClusters=2\n" + "features-integrated=\n" + "features-fragments=\n"
				+ "Cluster\\ Dataset-simple-yes=true";

		String comp = compress(string);
		System.out.println(comp.length());
		System.out.println();
		System.out.println(comp);
		System.out.println();
		System.out.println(string.length());
		System.out.println();
		System.out.println(deCompress(comp));

		//		System.out.println(toCamelCase("ene_mene_miste"));
		//		System.out.println(toProperCase("EneMeneMiste"));

		//		Random r = new Random();
		//		HashMap<String, String> md5keys = new HashMap<String, String>();
		//		while (true)
		//		{
		//			if (md5keys.size() % 1000 == 0)
		//				System.out.println(md5keys.size());
		//			String s = StringUtil.randomString(0, 1000, r);
		//			String md5 = StringUtil.getMD5(s);
		//			if (md5keys.containsKey(md5) && !s.equals(md5keys.get(md5)))
		//				throw new Error("same key at " + md5keys.size() + "key: " + md5 + "\n'" + s + "'\n'" + md5keys.get(md5)
		//						+ "'");
		//			md5keys.put(md5, s);
		//		}

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

	public static String wordWrap(String base, int regex)
	{
		//Prepare variables
		String rsm = base;
		boolean gotspace = false;
		boolean gotfeed = false;

		//Jump to characters to add line feeds
		int pos = regex;
		while (pos < rsm.length())
		{
			//Progressivly go backwards until next space
			int bf = pos - regex; //What is the stop point
			gotspace = false;
			gotfeed = false;

			//Find space just before to avoid cutting words
			for (int ap = pos; ap > bf; ap--)
			{
				//Is it a space?
				if (String.valueOf(rsm.charAt(ap)).equals(" ") == true && gotspace == false)
				{
					//Insert line feed and compute position variable
					gotspace = true;
					pos = ap; //Go to position
				}
				//If it is a line feed, go to it
				else if (String.valueOf(rsm.charAt(ap)).equals("\n") == true && gotfeed == false)
				{
					pos = ap; //Go to position
					gotfeed = true;
				}
			}
			//Got no feed? Append a line feed to the appropriate place
			if (gotfeed == false)
			{
				if (gotspace == false)
				{
					rsm = new StringBuffer(rsm).insert(pos, "\n").toString();
				}
				else
				{
					rsm = new StringBuffer(rsm).insert(pos + 1, "\n").toString();
				}
			}
			//Increment position by regex and restart loop
			pos += (regex + 1);
		}
		//Return th result
		return (rsm);
	}

	public static String getMD5(String data)
	{
		try
		{
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.reset();
			m.update(data.getBytes());
			BigInteger bigInt = new BigInteger(1, m.digest());
			String hashtext = bigInt.toString(16);
			while (hashtext.length() < 32)
			{
				hashtext = "0" + hashtext;
			}
			return hashtext;
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static String encodeFilename(String s)
	{
		try
		{
			return URLEncoder.encode(s, "UTF-8");
		}
		catch (java.io.UnsupportedEncodingException e)
		{
			throw new RuntimeException("UTF-8 is an unknown encoding!?");
		}
	}

}
