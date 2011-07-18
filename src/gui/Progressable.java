package gui;

import java.beans.PropertyChangeListener;

public interface Progressable
{
	public void update(double value);

	public void update(String info);

	public void update(double value, String info);

	public void error(String message, Throwable e);

	public void warning(String message, String details);

	public void close();

	public void waitForClose();

	public static String PROPERTY_ABORT = "abort";

	public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener);
}
