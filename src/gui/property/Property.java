package gui.property;

import java.beans.PropertyChangeListener;
import java.util.Properties;

import javax.swing.JComponent;

public interface Property
{
	public String getName();

	public Object getDefaultValue();

	public Object getValue();

	public void setValue(Object value);

	public JComponent getPropertyCompound();

	public void load(Properties javaProperties);

	public void store(Properties javaProperties, String propertyFilename);

	public void addPropertyChangeListener(PropertyChangeListener l);

}
