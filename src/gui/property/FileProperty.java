package gui.property;

import java.io.File;
import java.util.Properties;

import javax.swing.JComponent;

import util.ObjectUtil;

public class FileProperty extends AbstractProperty
{
	private File value;
	private File defaultValue;

	public FileProperty(String name, File value)
	{
		this(name, name, value);
	}

	public FileProperty(String name, String uniqueName, File value)
	{
		super(name, uniqueName);
		this.defaultValue = value;
		this.value = value;
	}

	@Override
	public JComponent getPropertyCompound()
	{
		return new FilePropertyCompound(this);
	}

	@Override
	public File getValue()
	{
		return value;
	}

	@Override
	public File getDefaultValue()
	{
		return defaultValue;
	}

	@Override
	public void load(Properties javaProperties)
	{
		String v = loadVal(javaProperties);
		if (v != null)
			setValue(new File(v));
	}

	@Override
	public void setValue(Object value)
	{
		if (!ObjectUtil.equals(this.value, value))
		{
			this.value = (File) value;
			valueChanged(this.value);
		}
	}

}
