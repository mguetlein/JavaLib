package gui.property;

import java.util.Properties;

import javax.swing.JComponent;

public class IntegerProperty extends AbstractProperty
{
	private Integer value;
	private Integer defaultValue;

	private Integer minValue = Integer.MIN_VALUE;
	private Integer maxValue = Integer.MAX_VALUE;

	public IntegerProperty(String name, Integer value)
	{
		this(name, name, value);
	}

	public IntegerProperty(String name, String uniqueName, Integer value)
	{
		super(name, uniqueName);
		this.defaultValue = value;
		this.value = value;
	}

	public IntegerProperty(String name, String uniqueName, Integer value, Integer minValue, Integer maxValue)
	{
		super(name, uniqueName);
		this.defaultValue = value;
		this.value = value;
		this.minValue = minValue;
		this.maxValue = maxValue;
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
			if ((Integer) value < minValue || (Integer) value > maxValue)
				throw new IllegalArgumentException();
			this.value = (Integer) value;
			valueChanged(this.value);
		}
	}

	public Integer getMinValue()
	{
		return minValue;
	}

	public Integer getMaxValue()
	{
		return maxValue;
	}

}
