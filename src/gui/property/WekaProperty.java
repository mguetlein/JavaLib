package gui.property;

import java.util.Properties;

import javax.swing.JComponent;

import weka.core.OptionHandler;
import weka.core.Utils;

public class WekaProperty extends AbstractProperty
{
	private OptionHandler value;
	private OptionHandler defaultValue;

	public WekaProperty(String name, String uniqueName, OptionHandler value, OptionHandler defaultValue)
	{
		super(name, uniqueName);
		this.defaultValue = defaultValue;
		this.value = value;
	}

	@Override
	public JComponent getPropertyCompound()
	{
		return new WekaPropertyCompound(this);
	}

	@Override
	public OptionHandler getValue()
	{
		return value;
	}

	@Override
	public OptionHandler getDefaultValue()
	{
		return defaultValue;
	}

	protected String valueToString()
	{
		String str = value.getClass().getName();
		str += " " + Utils.joinOptions(value.getOptions());
		return str;
	}

	@Override
	public void load(Properties javaProperties)
	{
		try
		{
			String v = loadVal(javaProperties);
			if (v != null)
			{
				int i = v.indexOf(" ");
				String alg = v.substring(0, i);
				String options = v.substring(i + 1);

				OptionHandler c = (OptionHandler) Class.forName(alg).newInstance();
				c.setOptions(Utils.splitOptions(options));
				setValue(c);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void setValue(Object value)
	{
		if (!this.value.equals(value))
		{
			this.value = (OptionHandler) value;
			valueChanged(this.value);
		}
	}
}
