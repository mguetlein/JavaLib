package gui.property;

import java.util.Properties;

import javax.swing.JComponent;

public class ColorGradientProperty extends AbstractProperty
{
	private ColorGradient value;
	private ColorGradient defaultValue;

	public ColorGradientProperty(String name, ColorGradient value)
	{
		this(name, name, value);
	}

	public ColorGradientProperty(String name, String uniqueName, ColorGradient value)
	{
		super(name, uniqueName);
		this.defaultValue = value;
		this.value = value;
	}

	@Override
	public JComponent getPropertyCompound()
	{
		return new ColorGradientPropertyCompound(this);
	}

	@Override
	public ColorGradient getValue()
	{
		return value;
	}

	@Override
	public ColorGradient getDefaultValue()
	{
		return defaultValue;
	}

	@Override
	public void load(Properties javaProperties)
	{
		String v = loadVal(javaProperties);
		if (v != null)
			setValue(ColorGradient.parseColorGradient(v));
	}

	@Override
	public void setValue(Object value)
	{
		if (!this.value.equals(value))
		{
			this.value = (ColorGradient) value;
			valueChanged(this.value);
		}
	}

	public void reverse()
	{
		setValue(value.reverse());
	}
}
