package org.mg.javalib.util;

public class StringLineAdder
{
	StringBuffer b = new StringBuffer();

	@Override
	public String toString()
	{
		return b.toString();
	}

	public void add()
	{
		b.append("\n");
	}

	public void add(String s)
	{
		b.append(s);
		b.append("\n");
	}
}
