package org.mg.javalib.util;

import java.util.Arrays;
import java.util.Comparator;

public class Version implements Comparable<Version>
{
	public static Comparator<Version> COMPARATOR = new Comparator<Version>()
	{
		@Override
		public int compare(Version v1, Version v2)
		{
			return v1.compareTo(v2);
		}
	};

	public int major;
	public int minor;
	public int patch;

	public Version(int major, int minor, int patch)
	{
		this.major = major;
		this.minor = minor;
		this.patch = patch;
	}

	public Version(int major, int minor)
	{
		this(major, minor, 0);
	}

	@Override
	public String toString()
	{
		return "v" + major + "." + minor + "." + patch;
	}

	public static Version fromString(String version)
	{
		if (!version.matches("v[0-9]+\\.[0-9]+\\.[0-9]+"))
			throw new IllegalArgumentException(version);
		int dots[] = StringUtil.indicesOf(version, ".");
		int major = Integer.parseInt(version.substring(1, dots[0]));
		int minor = Integer.parseInt(version.substring(dots[0] + 1, dots[1]));
		int patch = Integer.parseInt(version.substring(dots[1] + 1));
		return new Version(major, minor, patch);
	}

	public static Version fromMajorMinorString(String version)
	{
		if (!version.matches("v[0-9]+\\.[0-9]+"))
			throw new IllegalArgumentException(version);
		int dot = version.indexOf(".");
		int major = Integer.parseInt(version.substring(1, dot));
		int minor = Integer.parseInt(version.substring(dot + 1));
		return new Version(major, minor);
	}

	@Override
	public int compareTo(Version v)
	{
		if (major != v.major)
			return major - v.major;
		else if (minor != v.minor)
			return minor - v.minor;
		else
			return patch - v.patch;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Version))
			return false;
		Version v = (Version) obj;
		return major == v.major && minor == v.minor && patch == v.patch;
	}

	public static void main(String args[])
	{
		Integer ints[] = new Integer[] { 1, 2, 4, 3, 0 };
		Arrays.sort(ints);
		System.out.println(ArrayUtil.toString(ints));

		String s[] = { "v1.0.10", "v2.0.14", "v12.0.0", "v0.0.9", "v2.1.1", "v2.0.2" };
		Version v[] = new Version[s.length];
		for (int i = 0; i < v.length; i++)
			v[i] = Version.fromString(s[i]);
		Arrays.sort(v);
		System.out.println(ArrayUtil.toString(v));
	}
}
