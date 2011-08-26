package gui.property;

import java.util.Properties;

import javax.swing.JComponent;

public class DoubleProperty extends AbstractProperty
{
	private Double value;
	private Double defaultValue;

	public DoubleProperty(String name, Double value, Double defaultValue)
	{
		super(name);
		this.defaultValue = defaultValue;
		this.value = value;
	}

	@Override
	public JComponent getPropertyCompound()
	{
		return new DoublePropertyCompound(this);
	}

	@Override
	public Double getValue()
	{
		return value;
	}

	@Override
	public Double getDefaultValue()
	{
		return defaultValue;
	}

	@Override
	public void load(Properties javaProperties)
	{
		String v = loadVal(javaProperties);
		if (v != null)
			setValue(Double.parseDouble(v));
	}

	@Override
	public void setValue(Object value)
	{
		if (!this.value.equals(value))
		{
			this.value = (Double) value;
			valueChanged(this.value);
		}
	}

}
