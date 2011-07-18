package gui.property;

import java.util.Properties;

import javax.swing.JComponent;

public class IntegerProperty extends AbstractProperty
{
	Integer value;
	Integer defaultValue;

	public IntegerProperty(String name, Integer defaultValue)
	{
		super(name);
		this.defaultValue = defaultValue;
		this.value = defaultValue;
	}

	@Override
	public JComponent getPropertyCompound()
	{
		return new IntegerPropertyCompound(this);
	}

	@Override
	public Integer getValue()
	{
		return value;
	}

	@Override
	public Integer getDefaultValue()
	{
		return defaultValue;
	}

	@Override
	public void load(Properties javaProperties)
	{
		String v = loadVal(javaProperties);
		if (v != null)
			value = Integer.parseInt(v);
	}

}
