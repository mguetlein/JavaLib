package gui.property;

import java.util.Properties;

import javax.swing.JComponent;

public class IntegerProperty extends AbstractProperty
{
	private Integer value;
	private Integer defaultValue;

	public IntegerProperty(String name, Integer value, Integer defaultValue)
	{
		super(name);
		this.defaultValue = defaultValue;
		this.value = value;
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
			setValue(Integer.parseInt(v));
	}

	@Override
	public void setValue(Object value)
	{
		if (!this.value.equals(value))
		{
			this.value = (Integer) value;
			valueChanged(this.value);
		}
	}

}
