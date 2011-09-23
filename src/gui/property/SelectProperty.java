package gui.property;

import java.util.Properties;

import javax.swing.JComponent;

public class SelectProperty extends AbstractProperty
{
	private Object value;
	private Object defaultValue;
	private Object values[];

	public SelectProperty(String name, Object[] values, Object value, Object defaultValue)
	{
		super(name);
		this.defaultValue = defaultValue;
		this.value = value;
		this.values = values;
	}

	@Override
	public JComponent getPropertyCompound()
	{
		return new SelectPropertyCompound(this);
	}

	@Override
	public Object getValue()
	{
		return value;
	}

	public Object[] getValues()
	{
		return values;
	}

	@Override
	public Object getDefaultValue()
	{
		return defaultValue;
	}

	@Override
	public void load(Properties javaProperties)
	{
		String v = loadVal(javaProperties);
		if (v != null)
			setValue(v);
	}

	@Override
	public void setValue(Object value)
	{
		if (!this.value.toString().equals(value.toString()))
		{
			boolean match = false;
			for (Object v : values)
			{
				if (v.toString().equals(value.toString()))
				{
					this.value = v;
					valueChanged(this.value);
					match = true;
					break;
				}
			}
			if (!match)
				throw new Error("illegal select value: " + value.toString());
		}
	}
}