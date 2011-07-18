package gui;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class NumberTextField extends JTextField
{
	private NumberFormat integerFormatter;

	public NumberTextField(int columns)
	{
		super(columns);
		integerFormatter = NumberFormat.getNumberInstance(Locale.US);
		integerFormatter.setParseIntegerOnly(true);
	}

	public NumberTextField(int columns, int value)
	{
		super(columns);
		integerFormatter = NumberFormat.getNumberInstance(Locale.US);
		integerFormatter.setParseIntegerOnly(true);
		setValue(value);
	}

	public int getValue()
	{
		int retVal = 0;
		try
		{
			retVal = integerFormatter.parse(getText()).intValue();
		}
		catch (ParseException e)
		{
			//			System.err.println("no int value: " + e.getMessage());
		}
		return retVal;
	}

	public void setValue(int value)
	{
		setText(integerFormatter.format(value));
	}

	protected Document createDefaultModel()
	{
		return new WholeNumberDocument();
	}

	protected class WholeNumberDocument extends PlainDocument
	{

		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException
		{
			char[] source = str.toCharArray();
			char[] result = new char[source.length];
			int j = 0;

			for (int i = 0; i < result.length; i++)
			{
				if (Character.isDigit(source[i]))
					result[j++] = source[i];
				else
				{
					//					System.err.println("insertString: " + source[i]);
				}
			}
			super.insertString(offs, new String(result, 0, j), a);
		}
	}
}
