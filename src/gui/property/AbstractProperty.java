package gui.property;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

public abstract class AbstractProperty implements Property
{
	String name;
	private String uniqueName;
	protected String displayName;
	private Color highlightColor;

	List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();

	private static HashSet<String> uniqueSaveNames = new HashSet<String>();

	public AbstractProperty(String name)
	{
		this(name, name);
	}

	public AbstractProperty(String name, String uniqueName)
	{
		this.name = name;
		this.displayName = name;
		this.uniqueName = uniqueName;

		if (this.uniqueName != null)
		{
			if (uniqueSaveNames.contains(uniqueName))
				throw new Error(uniqueName + " not unique!!");
			uniqueSaveNames.add(uniqueName);
		}
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String getUniqueName()
	{
		return uniqueName;
	}

	@Override
	public String getDisplayName()
	{
		return displayName;
	}

	@Override
	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	protected String loadVal(Properties javaProperties)
	{
		return (String) javaProperties.get("property-" + uniqueName);
	}

	protected String valueToString()
	{
		return getValue().toString();
	}

	@Override
	public void loadOrResetToDefault(Properties javaProperties)
	{
		String v = loadVal(javaProperties);
		if (v == null)
			setValue(getDefaultValue());
		else
			load(javaProperties);
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

	public void put(Properties javaProperties)
	{
		javaProperties.put("property-" + uniqueName, valueToString());
	}

	public void store(Properties javaProperties, String propertyFilename)
	{
		//System.err.println("persisting property: " + "property-" + getName() + " -> " + valueToString());
		javaProperties.put("property-" + uniqueName, valueToString());
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

	@Override
	public void setHighlightColor(Color col)
	{
		this.highlightColor = col;
	}

	@Override
	public Color getHighlightColor()
	{
		return highlightColor;
	}
}
