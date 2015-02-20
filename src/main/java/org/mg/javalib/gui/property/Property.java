package org.mg.javalib.gui.property;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.util.Properties;

import javax.swing.JComponent;

public interface Property
{
	public String getName();

	public Object getDefaultValue();

	public Object getValue();

	public void setValue(Object value);

	public JComponent getPropertyComponent();

	public void load(Properties javaProperties);

	public void loadOrResetToDefault(Properties javaProperties);

	public void put(Properties javaProperties);

	public void store(Properties javaProperties, String propertyFilename);

	public void addPropertyChangeListener(PropertyChangeListener l);

	public void setDisplayName(String string);

	public String getDisplayName();

	public String getUniqueName();

	public void setHighlightColor(Color col);

	public Color getHighlightColor();

}
