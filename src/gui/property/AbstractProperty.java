package gui.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public abstract class AbstractProperty implements Property
{
	String name;

	List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();

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

	@Override
	public void addPropertyChangeListener(PropertyChangeListener l)
	{
		listeners.add(l);
	}

	protected void valueChanged(Object newVal)
	{
		for (PropertyChangeListener l : listeners)
			l.propertyChange(new PropertyChangeEvent(this, "valueChanged", null, newVal));
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
