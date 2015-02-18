package org.mg.javalib.gui.property;

import java.util.Properties;

import javax.swing.JComponent;

import weka.core.SelectedTag;

public class SelectedTagProperty extends AbstractProperty
{
	private SelectedTag value;
	private SelectedTag defaultValue;

	public SelectedTagProperty(String name, String uniqueName, SelectedTag value)
	{
		super(name, uniqueName);
		this.defaultValue = value;
		this.value = value;
	}

	@Override
	public JComponent getPropertyComponent()
	{
		return new SelectedTagPropertyComponent(this);
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
			setValue(new SelectedTag(Integer.parseInt(v), value.getTags()));
	}

	@Override
	public void setValue(Object value)
	{
		if (!this.value.equals(value))
		{
			this.value = (SelectedTag) value;
			valueChanged(this.value);
		}
	}

	public static void main(String args[])
	{
		//		Tag[] tags = { new Tag(1, "First option"), new Tag(2, "Second option"), new Tag(3, "Third option"),
		//				new Tag(4, "Fourth option"), new Tag(5, "Fifth option"), };
		//		SelectedTag initial = new SelectedTag(1, tags);
		//		System.out.println(initial);
	}

}
