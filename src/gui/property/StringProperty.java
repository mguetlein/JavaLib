package gui.property;

import java.util.Properties;

import javax.swing.JComponent;

public class StringProperty extends AbstractProperty
{
	String defaultValue;
	String value;

	public StringProperty(String name, String defaultValue)
	{
		super(name);
		this.defaultValue = defaultValue;
		this.value = defaultValue;
	}

	@Override
	public JComponent getPropertyCompound()
	{
		return new StringPropertyCompound(this);
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
		String v = (String) loadVal(javaProperties);
		if (v != null)
			value = v;
	}

}
