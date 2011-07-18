package gui;

import java.beans.PropertyChangeListener;

public class SubProgress implements Progressable
{
	Progressable p;
	double min;
	double delta;

	protected SubProgress(Progressable p, double min, double max)
	{
		this.p = p;
		this.min = min;
		this.delta = max - min;
	}

	public static SubProgress create(Progressable p, double min, double max)
	{
		if (p == null)
			return null;
		else
			return new SubProgress(p, min, max);
	}

	@Override
	public void update(double value)
	{
		update(value, null);
	}

	@Override
	public void update(double value, String info)
	{
		p.update(min + delta * value * 0.01, info);
	}

	public void close()
	{
		p.close();
	}

	@Override
	public void update(String info)
	{
		p.update(info);
	}

	@Override
	public void error(String message, Throwable e)
	{
		p.error(message, e);
	}

	@Override
	public void warning(String message, String details)
	{
		p.warning(message, details);
	}

	@Override
	public void waitForClose()
	{
		p.waitForClose();
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener)
	{
		p.addPropertyChangeListener(propertyChangeListener);
	}

}
