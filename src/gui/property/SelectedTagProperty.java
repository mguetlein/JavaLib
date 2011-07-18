package gui.property;

import java.util.Properties;

import javax.swing.JComponent;

import weka.core.SelectedTag;

public class SelectedTagProperty extends AbstractProperty
{
	SelectedTag value;
	SelectedTag defaultValue;

	public SelectedTagProperty(String name, SelectedTag defaultValue)
	{
		super(name);
		this.defaultValue = defaultValue;
		this.value = defaultValue;
	}

	@Override
	public JComponent getPropertyCompound()
	{
		return new SelectedTagPropertyCompound(this);
	}

	@Override
	public SelectedTag getValue()
	{
		return value;
	}

	@Override
	public SelectedTag getDefaultValue()
	{
		return defaultValue;
	}

	@Override
	public void load(Properties javaProperties)
	{
		String v = loadVal(javaProperties);
		if (v != null)
			value = new SelectedTag(Integer.parseInt(v), value.getTags());
	}

	public static void main(String args[])
	{
		//		Tag[] tags = { new Tag(1, "First option"), new Tag(2, "Second option"), new Tag(3, "Third option"),
		//				new Tag(4, "Fourth option"), new Tag(5, "Fifth option"), };
		//		SelectedTag initial = new SelectedTag(1, tags);
		//		System.out.println(initial);
	}

}
