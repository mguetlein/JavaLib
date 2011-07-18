package gui.property;

import java.util.Properties;

import javax.swing.JComponent;

public class DoubleProperty extends AbstractProperty
{
	Double value;
	Double defaultValue;

	public DoubleProperty(String name, Double defaultValue)
	{
		super(name);
		this.defaultValue = defaultValue;
		this.value = defaultValue;
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
			value = Double.parseDouble(v);
	}

}
