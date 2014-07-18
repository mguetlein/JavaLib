package gui.property;

import java.util.Properties;

import javax.swing.JComponent;

public class DoubleProperty extends AbstractProperty
{
	private Double value;
	private Double defaultValue;

	private Double minValue;
	private Double maxValue;
	private Double stepWidth;

	public DoubleProperty(String name, Double value)
	{
		this(name, name, value);
	}

	public DoubleProperty(String name, Double value, Double minValue, Double maxValue, Double stepWidth)
	{
		this(name, name, value, minValue, maxValue, stepWidth);
	}

	public DoubleProperty(String name, String uniqueName, Double value)
	{
		this(name, uniqueName, value, 0.0, Double.MAX_VALUE, 0.01);
	}

	public DoubleProperty(String name, String uniqueName, Double value, Double minValue, Double maxValue,
			Double stepWidth)
	{
		super(name, uniqueName);
		this.defaultValue = value;
		this.value = value;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.stepWidth = stepWidth;
	}

	@Override
	public JComponent getPropertyComponent()
	{
		return new DoublePropertyComponent(this);
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

	public Double getMinValue()
	{
		return minValue;
	}

	public Double getMaxValue()
	{
		return maxValue;
	}

	public Double getStepWidth()
	{
		return stepWidth;
	}

}
