package gui.property;

import java.util.Properties;

import javax.swing.JComponent;

public class BooleanProperty extends AbstractProperty
{
	Boolean value;
	Boolean defaultValue;

	public BooleanProperty(String name, Boolean defaultValue)
	{
		super(name);
		this.defaultValue = defaultValue;
		this.value = defaultValue;
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
			value = Boolean.parseBoolean(v);
	}

}
