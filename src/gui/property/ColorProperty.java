package gui.property;

import java.awt.Color;
import java.util.Properties;

import javax.swing.JComponent;

import util.ColorUtil;

public class ColorProperty extends AbstractProperty
{
	private Color value;
	private Color defaultValue;

	public ColorProperty(String name, Color value)
	{
		this(name, name, value);
	}

	public ColorProperty(String name, String uniqueName, Color value)
	{
		this(name, uniqueName, value, value);
	}

	public ColorProperty(String name, String uniqueName, Color value, Color defaultValue)
	{
		super(name, uniqueName);
		this.defaultValue = defaultValue;
		this.value = value;
	}

	@Override
	public JComponent getPropertyComponent()
	{
		return new ColorPropertyComponent(this);
	}

	@Override
	public Color getValue()
	{
		return value;
	}

	@Override
	public Color getDefaultValue()
	{
		return defaultValue;
	}

	@Override
	public void load(Properties javaProperties)
	{
		String v = loadVal(javaProperties);
		if (v != null)
			setValue(ColorUtil.parseColor(v));
	}

	@Override
	public void setValue(Object value)
	{
		if (!this.value.equals(value))
		{
			this.value = (Color) value;
			valueChanged(this.value);
		}
	}
}
