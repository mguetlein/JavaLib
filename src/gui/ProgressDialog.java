package gui;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.PrintStream;
import java.util.Random;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import util.MemoryUtil;
import util.StringUtil;
import util.SwingUtil;

import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class ProgressDialog implements Progressable
{
	private JDialog dialog;
	private JButton hideButton;
	private JButton killButton;
	private JProgressBar bar;
	private JLabel infoLabel;
	//	private JLabel timepastLabel;
	//	private JLabel timeleftLabel;

	private MessagePanel detailPanel;

	private String title;

	private double max;
	private long starttime;
	private long lastUpdate;

	private PrintStream out;
	private String prefix = "> ";
	private static long PRINT_BREAK = 3000;

	CurrentTimeEstimate currentProgress;

	public static ProgressDialog showProgress(Object windowOrPrintStream, String title, String prefix, double max,
			String info)
	{
		if (windowOrPrintStream instanceof Window)
			return showProgress((Window) windowOrPrintStream, title, prefix, max, info);
		else if (windowOrPrintStream instanceof PrintStream)
			return showProgress((PrintStream) windowOrPrintStream, title, prefix, max, info);
		else
			throw new Error("param must be JFrame or PrintStream");
	}

	public static ProgressDialog showProgress(Window owner, String title, double max, String info)
	{
		ProgressDialog d = new ProgressDialog(true, null, owner, title, null, max, info);
		return d;
	}

	public static ProgressDialog showProgress(PrintStream out, String title, String prefix, double max, String info)
	{

		ProgressDialog d = new ProgressDialog(false, out, null, title, prefix, max, info);
		return d;
	}

	public static ProgressDialog showProgress(Object windowOrPrintStream, String title, String prefix, double max)
	{
		if (windowOrPrintStream instanceof Window)
			return showProgress((Window) windowOrPrintStream, title, max, null);
		else if (windowOrPrintStream instanceof PrintStream)
			return showProgress((PrintStream) windowOrPrintStream, title, prefix, max, null);
		else
			throw new Error("param must be Window or PrintStream");
	}

	public static ProgressDialog showProgress(Window owner, String title, double max)
	{
		return showProgress(owner, title, max, null);
	}

	public static ProgressDialog showProgress(PrintStream out, String title, String prefix, double max)
	{
		return showProgress(out, title, prefix, max, null);
	}

	private ProgressDialog(boolean showDialog, PrintStream out, Window owner, String title, String prefix, double max,
			String info)
	{
		this.out = out;
		this.title = title;
		if (prefix != null)
			this.prefix = prefix;

		if (showDialog)
			buildLayout(owner);
		else
			out.println(prefix + "started: " + title);

		this.max = max;
		starttime = System.currentTimeMillis();
		lastUpdate = starttime;
		currentProgress = new CurrentTimeEstimate();
		update(0, info);

		if (showDialog)
			dialog.setVisible(true);
	}

	public void update(double value)
	{
		update(value, null);
	}

	@Override
	public void update(String info)
	{
		update((Double) null, info);
	}

	public void update(double value, String info)
	{
		update(value, info, false);
		if (info != null && info.trim().length() > 0)
			detailPanel.addInfo(info, null);
	}

	@Override
	public void warning(String message, String details)
	{
		update(null, message, false);
		detailPanel.addWarning(message, details);
	}

	@Override
	public void error(String message, Throwable error)
	{
		update(null, "ERROR: " + message, false);
		if (dialog != null)
			dialog.setTitle("ERROR - " + dialog.getTitle());

		String details = "";
		for (StackTraceElement e : error.getStackTrace())
			details += e.toString() + "\n";
		detailPanel.addError(message, details);
	}

	private void update(Double value, String info, boolean forcePrint)
	{
		if (value != null)
			currentProgress.update(value);
		long now = System.currentTimeMillis();

		if (dialog == null && !forcePrint && now - lastUpdate < PRINT_BREAK)
			return;
		lastUpdate = now;

		int percent = -1;
		long timePast = -1;
		long estimatedTimeLeft = 0;
		if (value != null)
		{
			percent = (int) Math.floor((value / max) * 100);
			timePast = now - starttime;

			if (timePast > 2000 && value < max && value > 0)
			{
				long estimatedTotalTime = (long) (timePast / (value / max));
				estimatedTimeLeft = estimatedTotalTime - timePast;

				long currentEstimate = currentProgress.timeEstimate(value);
				if (currentEstimate > 0)
					estimatedTimeLeft = (long) (estimatedTimeLeft * .5 + currentEstimate * .5);

				if (estimatedTimeLeft < 1000)
					estimatedTimeLeft = 0;
			}
			if (info == null)
				info = value + "/" + max;
		}
		if (dialog != null)
		{
			if (value != null)
			{
				dialog.setTitle(percent + "% - " + title);
				bar.setValue(percent);
			}
			infoLabel.setText(info);
			//			timepastLabel.setText("Vergangene Zeit: " + TimeFormatUtil.formatHumanReadable(timePast));
			//			if (estimatedTimeLeft == 0)
			//				timeleftLabel.setText("Verbleibende Zeit: ");
			//			else
			//				timeleftLabel.setText("Verbleibende Zeit: " + TimeFormatUtil.formatHumanReadable(estimatedTimeLeft));
		}
		else if (value != null)
		{
			System.gc();
			out.printf("%s%3d%s - %s / %s - %8s - %s\n", prefix, percent, "%", StringUtil.formatTime(timePast),
					StringUtil.formatTime(estimatedTimeLeft), MemoryUtil.getUsedMemoryString(), info);
			// StopWatchUtil.print(out);
		}

	}

	public void close()
	{
		close(max, null);
	}

	public void close(double value)
	{
		close(value, null);
	}

	public void close(double value, String info)
	{
		update(value, info, true);
		if (dialog == null)
			out.println(prefix + "finished: " + title);
		else
			dialog.setVisible(false);
	}

	private void buildLayout(Window owner)
	{
		dialog = new JDialog(owner);
		hideButton = new JButton("Im Hintergrund");
		killButton = new JButton("Abort");
		bar = new JProgressBar(0, 100);
		infoLabel = new JLabel("___________________________________________________________");
		//		timepastLabel = new JLabel("Vergangene Zeit: XX Monate und XX Wochen");
		//		timeleftLabel = new JLabel("Verbleibende Zeit: XX Monate und XX Wochen");

		FormLayout f = new FormLayout("fill:p:grow", "p,3dlu,p,6dlu,fill:p:grow,6dlu,p");
		CellConstraints cc = new CellConstraints();
		JPanel p = new JPanel(f);

		detailPanel = new MessagePanel();
		HideablePanel hide = new HideablePanel("Details:", true);
		hide.addPropertyChangeListener("Hiding", new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				dialog.setSize(dialog.getWidth(), dialog.getContentPane().getPreferredSize().height + 40);
			}
		});
		hide.addComponent(detailPanel);

		hideButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dialog.setVisible(false);
				out = System.out;
				dialog = null;
			}
		});

		killButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				firePropertyChanageEvent(PROPERTY_ABORT);
				//System.exit(1);
				dialog.setVisible(false);
			}
		});

		dialog.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				firePropertyChanageEvent(PROPERTY_ABORT);
				//System.exit(1);
				dialog.setVisible(false);
			}
		});

		//JPanel buttonBar = ButtonBarFactory.buildLeftAlignedBar(hideButton, killButton);
		JPanel buttonBar = ButtonBarFactory.buildLeftAlignedBar(killButton);

		p.add(infoLabel, cc.xy(1, 1));
		p.add(bar, cc.xy(1, 3));
		p.add(hide, cc.xy(1, 5));
		p.add(buttonBar, cc.xy(1, 7));
		p.setBorder(new EmptyBorder(10, 10, 10, 10));

		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(p);
		dialog.pack();
		dialog.setSize(Math.max(dialog.getSize().width, 600), dialog.getSize().height);
		dialog.setLocationRelativeTo(owner);
	}

	class CurrentTimeEstimate
	{
		private Vector<Long> times = new Vector<Long>();
		private Vector<Double> progress = new Vector<Double>();

		private long INTERVAL = 5000;
		private long NOTE = 1000;
		private long lastNote;

		private void update(double value)
		{
			long now = System.currentTimeMillis();
			if (now - lastNote < NOTE)
				return;
			lastNote = now;

			times.add(now);
			progress.add(value);
		}

		private long timeEstimate(double currentVal)
		{
			long now = System.currentTimeMillis();

			double deltaVal = -1;
			long deltaTime = -1;

			while (progress.size() > 0)
			{
				double tmpVal = progress.firstElement();
				long tmpTime = times.firstElement();

				deltaTime = now - tmpTime;
				deltaVal = currentVal - tmpVal;

				if (deltaTime < INTERVAL)
					break;

				progress.remove(0);
				times.remove(0);
			}

			// System.out.println(deltaVal + " done in last " + StringUtil.formatTime(deltaTime));
			// System.out.println((max - currentVal) + " left");

			if (deltaVal == 0)
				return -1;

			long res = (long) (((max - currentVal) * deltaTime) / deltaVal);

			assert (res > 0) : "now: " + now + " deltaTime: " + deltaTime + " deltaVal: " + deltaVal + " max: " + max
					+ " currentVal: " + currentVal;

			return res;
		}
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

				ProgressDialog d = ProgressDialog.showProgress((JFrame) null, "Test", max, "aktuell: " + 0);
				// ProgressDialog d = ProgressDialog.printProgress(System.out, "Test", max, "aktuell: " + 0);

				int gear = 5;
				System.out.println("starting gear: " + gear);

				while (true)
				{
					current += gear;
					d.update(current, "aktuell: " + current);

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
							System.out.println("change gears: " + gear + "");
							d.warning("change gears: " + gear + "", "now its goinf fatser or slower");
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

				d.close(current, "fertig");

				System.exit(0);
			}

		};
		Thread th = new Thread(r);
		th.start();
	}

	@Override
	public void waitForClose()
	{
		if (dialog != null)
			SwingUtil.waitWhileVisible(dialog);
	}

	Vector<PropertyChangeListener> listeners = new Vector<PropertyChangeListener>();

	@Override
	public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener)
	{
		listeners.add(propertyChangeListener);
	}

	public void firePropertyChanageEvent(String prop)
	{
		for (PropertyChangeListener p : listeners)
			p.propertyChange(new PropertyChangeEvent(this, prop, false, true));
	}

}
