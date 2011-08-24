package gui.property;

import java.util.Properties;

import javax.swing.JComponent;

public class StringSelectProperty extends AbstractProperty
{
	String value;
	String defaultValue;
	String values[];

	public StringSelectProperty(String name, String[] values, String defaultValue)
	{
		super(name);
		this.defaultValue = defaultValue;
		this.value = defaultValue;
		this.values = values;
	}

	@Override
	public JComponent getPropertyCompound()
	{
		return new StringSelectPropertyCompound(this);
	}

	@Override
	public String getValue()
	{
		return value;
	}

	@Override
	public String getDefaultValue()
	{
		return defaultValue;
	}

	@Override
	public void load(Properties javaProperties)
	{
		String v = loadVal(javaProperties);
		if (v != null)
			value = v;
	}

}