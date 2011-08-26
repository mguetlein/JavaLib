package gui.property;

import java.util.Properties;

import javax.swing.JComponent;

public class StringSelectProperty extends AbstractProperty
{
	private String value;
	private String defaultValue;
	private String values[];

	public StringSelectProperty(String name, String[] values, String value, String defaultValue)
	{
		super(name);
		this.defaultValue = defaultValue;
		this.value = value;
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

	public String[] getValues()
	{
		return values;
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
			setValue(v);
	}

	@Override
	public void setValue(Object value)
	{
		if (!this.value.equals(value))
		{
			this.value = (String) value;
			valueChanged(this.value);
		}
	}

}