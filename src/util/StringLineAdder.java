package util;

public class StringLineAdder
{
	StringBuffer b = new StringBuffer();

	@Override
	public String toString()
	{
		return b.toString();
	}

	public void add(String s)
	{
		b.append(s);
		b.append("\n");
	}
}
