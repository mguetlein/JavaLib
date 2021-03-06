package org.mg.javalib.task;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.mg.javalib.gui.MessagePanel;
import org.mg.javalib.io.Logger;
import org.mg.javalib.task.TaskImpl.DetailMessage;
import org.mg.javalib.util.ScreenUtil;
import org.mg.javalib.util.SwingUtil;
import org.mg.javalib.util.ThreadUtil;

import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class TaskDialog
{
	TaskImpl task;

	JDialog dialog;
	private JButton cancelButton;
	private JButton showLogButton;
	private JLabel infoLabel;
	private JLabel verboseLabel;

	private boolean showWarningDialog = true;
	Window warningDialogOwner;

	String warningDialogMsg = "The following issues have occured during the mapping process:";

	public TaskDialog(Task task, int screen)
	{
		this(task, null, screen);
	}

	public TaskDialog(Task task, Window owner)
	{
		this(task, owner, -1);
	}

	private TaskDialog(final Task task, final Window owner, final int screen)
	{
		this.task = (TaskImpl) task;
		this.warningDialogOwner = owner;
		SwingUtil.invokeAndWait(new Runnable()
		{
			@Override
			public void run()
			{
				buildDialog(owner, screen);
				addListeners();
				if (task.isRunning())//still not entirely safe, should synchronize this
					dialog.setVisible(true);
			}
		});
	}

	public void setShowWarningDialog(boolean showWarningDialog)
	{
		this.showWarningDialog = showWarningDialog;
	}

	public void setWarningDialogOwner(Window owner)
	{
		this.warningDialogOwner = owner;
	}

	private void buildDialog(Window owner, int screen)
	{
		cancelButton = new JButton("Abort");
		showLogButton = new JButton("Show log");
		infoLabel = new JLabel(task.getUpdateMessage() == null ? " " : task.getUpdateMessage());
		infoLabel.setFont(infoLabel.getFont().deriveFont(Font.BOLD));
		verboseLabel = new JLabel(task.getVerboseMessage() == null ? " " : task.getVerboseMessage())
		{
			@Override
			public void setText(String text)
			{
				if (text.length() == 0)
					super.setText(" ");
				else
					super.setText(text);
			}
		};
		FormLayout f = new FormLayout("600px", "p,4dlu,p,6dlu,p");
		CellConstraints cc = new CellConstraints();
		JPanel p = new JPanel(f);
		cancelButton.setVisible(true);
		showLogButton.setVisible(false);
		JPanel buttonBar = ButtonBarFactory.buildLeftAlignedBar(cancelButton, showLogButton);
		p.add(infoLabel, cc.xy(1, 1));
		p.add(verboseLabel, cc.xy(1, 3));
		p.add(buttonBar, cc.xy(1, 5));
		p.setBorder(new EmptyBorder(10, 10, 10, 10));
		dialog = new JDialog(owner);
		updateTitle();
		dialog.setModal(false);
		dialog.setLayout(new BorderLayout());
		dialog.add(p);
		pack();
		if (owner != null && owner.isVisible())
			dialog.setLocationRelativeTo(owner);
		else if (screen != -1)
			ScreenUtil.centerOnScreen(dialog, screen);
		else
			ScreenUtil.centerOnScreen(dialog, ScreenUtil.getLargestScreen());
	}

	private void updateTitle()
	{
		String error = "";
		if (task.isFailed())
			error = "ERROR - ";
		String percent = "";
		if (task.getPercent() > 0)
			percent = task.getPercent() + "% - ";
		dialog.setTitle(error + percent + task.getName());
	}

	public boolean doWarningsExist()
	{
		return task.getWarnings().size() > 0;
	}

	public void showWarningDialog()
	{
		List<DetailMessage> warnings = task.getWarnings();
		MessagePanel p = new MessagePanel();
		for (int i = 0; i < warnings.size(); i++)
			p.addWarning(warnings.get(i).message, warnings.get(i).detail);
		final JPanel pp = new JPanel(new BorderLayout(10, 10));
		pp.add(new JLabel(warningDialogMsg), BorderLayout.NORTH);
		pp.add(p);
		Thread th = new Thread(new Runnable()
		{
			public void run()
			{
				SwingUtil.showInDialog(pp, task.getName(), new Dimension(600, 300), null, (JFrame) warningDialogOwner);
			}
		});
		th.start();
	}

	private void pack()
	{
		dialog.pack();
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				dialog.pack();//text field hight adjustment 'bug'		
			}
		});
	}

	private void addListeners()
	{
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (task.isRunning())
					task.cancel();
				else
					dialog.dispose();
			}
		});
		showLogButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (((TaskImpl) task).getLogger() != null)
					((TaskImpl) task).getLogger().showDialog(dialog);
			}
		});
		task.addListener(new TaskListener()
		{
			long stamp = -1;

			@Override
			public void update(final TaskEvent event)
			{
				stamp = System.currentTimeMillis();
				final long fStamp = stamp;

				switch (event)
				{
					case update:
						updateTitle();
						infoLabel.setText(task.getUpdateMessage());
						verboseLabel.setText("");
						break;
					case debug_verbose:
						final String msg = task.getVerboseMessage();
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								if (fStamp == stamp)
									verboseLabel.setText(msg);
							}
						});
						break;
					case cancelled:
						SwingUtil.invokeAndWait(new Runnable()
						{
							@Override
							public void run()
							{
								dialog.dispose();
							}
						});
						break;
					case failed:
						updateTitle();
						cancelButton.setText("Close");
						showLogButton.setVisible(true);
						infoLabel.setText(task.getError().message);
						verboseLabel.setText(task.getError().detail);
						pack();
						SwingUtil.waitWhileVisible(dialog);
						SwingUtil.invokeAndWait(new Runnable()
						{
							@Override
							public void run()
							{
								dialog.dispose();
							}
						});
						break;
					case finished:
						SwingUtil.invokeAndWait(new Runnable()
						{
							@Override
							public void run()
							{
								dialog.dispose();
							}
						});
						if (task.hasWarnings() && showWarningDialog)
							showWarningDialog();
						break;
					case warning:
						break;
				}
			}
		});
		dialog.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				if (task.isRunning())
					task.cancel();
			}
		});
	}

	public Task getTask()
	{
		return task;
	}

	public static void main(String args[])
	{
		final JButton b = new JButton("start task");
		b.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Thread th = new Thread(new Runnable()
				{
					public void run()
					{
						TaskImpl t = new TaskImpl("test", new Logger(null, true));
						new TaskDialog(t, (Window) b.getTopLevelAncestor());
						t.update("bla");
						ThreadUtil.sleep(500);
						t.update("bla2");
						ThreadUtil.sleep(500);
						t.update("bla3");
						t.failed("some error", "no details");
						t.finish();
					}
				});
				th.start();
			}
		});
		SwingUtil.showInDialog(b);
		System.exit(0);
	}

	public void setCancelButtonText(String string)
	{
		cancelButton.setText(string);
	}

}
