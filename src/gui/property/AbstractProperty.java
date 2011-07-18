package gui.property;

import java.io.FileOutputStream;
import java.util.Properties;

public abstract class AbstractProperty implements Property
{
	String name;

	public AbstractProperty(String name)
	{
		this.name = name;
	}

	@Override
	public String getName()
	{
		return name;
	}

	protected String loadVal(Properties javaProperties)
	{
		return (String) javaProperties.get("property-" + getName());
	}

	protected String valueToString()
	{
		return getValue().toString();
	}

	public void store(Properties javaProperties, String propertyFilename)
	{
		//System.err.println("persisting property: " + "property-" + getName() + " -> " + valueToString());
		javaProperties.put("property-" + getName(), valueToString());
		try
		{
			FileOutputStream out = new FileOutputStream(propertyFilename);
			javaProperties.store(out, "---No Comment---");
			out.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
