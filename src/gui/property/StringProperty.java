package gui.property;

import java.util.Properties;

import javax.swing.JComponent;

public class StringProperty extends AbstractProperty
{
	private String defaultValue;
	private String value;

	public StringProperty(String name, String value)
	{
		super(name);
		this.defaultValue = value;
		this.value = value;
	}

	@Override
	public JComponent getPropertyComponent()
	{
		return new StringPropertyComponent(this);
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
