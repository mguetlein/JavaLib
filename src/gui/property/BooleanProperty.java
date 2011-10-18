package gui.property;

import java.util.Properties;

import javax.swing.JComponent;

public class BooleanProperty extends AbstractProperty
{
	private Boolean value;
	private Boolean defaultValue;

	public BooleanProperty(String name, Boolean value)
	{
		this(name, name, value);
	}

	public BooleanProperty(String name, String uniqueName, Boolean value)
	{
		super(name, uniqueName);
		this.defaultValue = value;
		this.value = value;
	}

	@Override
	public JComponent getPropertyCompound()
	{
		return new BooleanPropertyCompound(this);
	}

	@Override
	public Boolean getValue()
	{
		return value;
	}

	@Override
	public Boolean getDefaultValue()
	{
		return defaultValue;
	}

	@Override
	public void load(Properties javaProperties)
	{
		String v = loadVal(javaProperties);
		if (v != null)
			setValue(Boolean.parseBoolean(v));
	}

	@Override
	public void setValue(Object value)
	{
		if (!this.value.equals(value))
		{
			this.value = (Boolean) value;
			valueChanged(this.value);
		}
	}
}
