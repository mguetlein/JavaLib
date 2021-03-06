package org.mg.javalib.gui.binloc;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.mg.javalib.util.FileUtil;
import org.mg.javalib.util.OSUtil;

public class Binary
{
	private String command;
	private String envName;
	private String commandLocation;
	private String description;

	private List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();

	public Binary(String command, String envName, String description)
	{
		this.command = command;
		this.envName = envName;
		this.description = description;
	}

	public void addPropertyChangeListener(PropertyChangeListener l)
	{
		listeners.add(l);
	}

	private void fireEvent()
	{
		boolean f = isFound();
		for (PropertyChangeListener l : listeners)
			l.propertyChange(new PropertyChangeEvent(this, "propertyName", !f, f));
	}

	public boolean isFound()
	{
		return commandLocation != null && new File(commandLocation).exists();
	}

	public void setLocation(String location)
	{
		this.commandLocation = location;
		if (isFound())
			fireEvent();
	}

	public void setParent(String parent)
	{
		if (OSUtil.isWindows())
			this.commandLocation = parent + File.separator + command + ".exe";
		else
			this.commandLocation = parent + File.separator + command;
		if (isFound())
			fireEvent();
	}

	public String getSisterCommandLocation(String sisterCommand)
	{
		if (OSUtil.isWindows())
			return FileUtil.getParent(commandLocation) + File.separator + sisterCommand + ".exe";
		else
			return FileUtil.getParent(commandLocation) + File.separator + sisterCommand;
	}

	public String getEnvName()
	{
		return envName;
	}

	public String getCommand()
	{
		return command;
	}

	public String getLocation()
	{
		return commandLocation;
	}

	public String getOSCommand()
	{
		if (OSUtil.isWindows())
			return command + ".exe";
		else
			return command;
	}

	public String getDescription()
	{
		return description;
	}

}
