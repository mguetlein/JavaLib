package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import util.SwingUtil;

public class Task
{
	private double max;

	private List<String> warnings = new ArrayList<String>();
	private List<String> warningDetails = new ArrayList<String>();

	int percent;

	private boolean cancelled = false;

	public static String PROPERTY_INFO = "info";
	public static String PROPERTY_VERBOSE = "verbose";
	public static String PROPERTY_WARNING = "warning";
	public static String PROPERTY_ERROR = "error";
	public static String PROPERTY_CANCELLED = "cancel";

	private TaskPanel panel;
	private JDialog dialog;

	public Task(double max)
	{
		this(max, null);
	}

	public Task(double max, String info)
	{
		this.max = max;
		update(0, info);
	}

	public void verbose(String info)
	{
		firePropertyChanageEvent(PROPERTY_VERBOSE, info, null);
	}

	public void update(String info)
	{
		firePropertyChanageEvent(PROPERTY_INFO, info, null);
	}

	public void update(double value, String info)
	{
		update(value);
		firePropertyChanageEvent(PROPERTY_INFO, info, null);
	}

	private static String toString(Throwable error)
	{
		String details = error.getMessage() + "\n";
		for (StackTraceElement e : error.getStackTrace())
			details += e.toString() + "\n";
		return details;
	}

	public void warning(String message, Throwable error)
	{
		warning(message, toString(error));
	}

	public void warning(String message, String details)
	{
		firePropertyChanageEvent(PROPERTY_WARNING, message, details);
		warnings.add(message);
		warningDetails.add(details);
	}

	public void error(String message, Throwable error)
	{
		firePropertyChanageEvent(PROPERTY_ERROR, message, toString(error));
	}

	public int getPercent()
	{
		return percent;
	}

	boolean progress = false;

	public boolean progress()
	{
		return progress;
	}

	public void update(double value)
	{
		if (value > 0)
			progress = true;
		percent = (int) Math.floor((value / max) * 100);
	}

	Vector<PropertyChangeListener> listeners = new Vector<PropertyChangeListener>();

	public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener)
	{
		listeners.add(propertyChangeListener);
	}

	public void firePropertyChanageEvent(String prop, String value, String details)
	{
		for (PropertyChangeListener p : listeners)
			p.propertyChange(new PropertyChangeEvent(this, prop, details, value));
	}

	public boolean containsWarnings()
	{
		return warnings.size() > 0;
	}

	public void showWarningDialog(final JFrame owner, final String title, String message)
	{
		MessagePanel p = new MessagePanel();
		for (int i = 0; i < warnings.size(); i++)
			p.addWarning(warnings.get(i), warningDetails.get(i));

		final JPanel pp = new JPanel(new BorderLayout(10, 10));
		pp.add(new JLabel(message), BorderLayout.NORTH);
		pp.add(p);

		Thread th = new Thread(new Runnable()
		{
			public void run()
			{
				SwingUtil.showInDialog(pp, title, new Dimension(600, 400), null, owner);
			}
		});
		th.start();
	}

	public boolean isCancelled()
	{
		return cancelled;
	}

	public void cancel()
	{
		if (!cancelled)
		{
			System.err.println("Cancelling task!");
			cancelled = true;
			firePropertyChanageEvent(PROPERTY_CANCELLED, "cancelling", null);
		}
	}

	public void showDialog(Window owner, String title)
	{
		showDialog(owner, title, 0);
	}

	public void showDialog(Window owner, String title, int screen)
	{
		dialog = getPanel().showDialog(owner, title, screen);
	}

	public JDialog getDialog()
	{
		return dialog;
	}

	public TaskPanel getPanel()
	{
		if (panel == null)
			panel = new TaskPanel(this);
		return panel;
	}

	public static void main(String args[])
	{
		Runnable r = new Runnable()
		{
			int max = 10000;
			int current = 0;
			Random rand = new Random();

			public void run()
			{
				Task t = new Task(max);
				TaskPanel p = new TaskPanel(t);
				p.showDialog(null, "Test task");

				// ProgressDialog d = ProgressDialog.printProgress(System.out, "Test", max, "aktuell: " + 0);

				int gear = 5;
				System.out.println("starting gear: " + gear);

				while (!t.isCancelled())
				{
					current += gear;
					t.update(current, "aktuell: " + current);

					try
					{

						// if (current == 5000)
						// {
						// gear = 10;
						// System.out.println("change gears: " + gear + "\n");
						// }

						if (rand.nextDouble() > 0.99)
						{
							gear = rand.nextInt(10);
							//							System.out.println("change gears: " + gear + "");
							t.warning("change gears: " + gear
									+ ", now its going fatser or slower, this is a very long message just for testing",
									"");
							Thread.sleep(3000);
						}

						Thread.sleep((int) 20);// * rand.nextDouble()));
					}
					catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (current >= max)
						break;
				}
				try
				{
					Thread.sleep(3000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}

				//d.close(current, "fertig");

				System.exit(0);
			}

		};
		Thread th = new Thread(r);
		th.start();
	}
}
