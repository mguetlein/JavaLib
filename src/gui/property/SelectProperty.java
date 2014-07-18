package gui.property;

import java.util.Properties;

import javax.swing.JComponent;

import util.ArrayUtil;

public class SelectProperty extends AbstractProperty
{
	private Object value;
	private Object defaultValue;
	private Object values[];

	public static String EMPTY = "nothing selected";

	public SelectProperty(String name, Object[] values, Object value)
	{
		this(name, name, values, value);
	}

	public SelectProperty(String name, String uniqueName, Object[] values, Object value)
	{
		super(name, uniqueName);
		if (value == null)
			value = EMPTY;
		this.defaultValue = value;
		this.value = value;
		if (values == null)
			values = new Object[0];
		this.values = values;
	}

	public void reset(Object[] values)
	{
		if (values == null)
			values = new Object[0];
		this.values = values;
		if (value != EMPTY && ArrayUtil.indexOf(values, value) == -1)
			value = EMPTY;
		if (value == EMPTY && values.length > 1)
		{
			this.value = values[0];
			this.defaultValue = values[0];
		}
		else
			this.defaultValue = value;
		valueChanged(this.value);
	}

	@Override
	public JComponent getPropertyComponent()
	{
		return new SelectPropertyComponent(this);
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
			if (values.length > 0)
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
					System.err.println("illegal select value: " + value.toString() + " " + ArrayUtil.toString(values));
			}
			else
				this.value = value;
		}
	}
}