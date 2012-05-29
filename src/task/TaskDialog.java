package task;

import gui.MessagePanel;
import io.Logger;

import java.awt.BorderLayout;
import java.awt.Dimension;
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

import task.TaskImpl.DetailMessage;
import util.ScreenUtil;
import util.SwingUtil;
import util.ThreadUtil;

import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.lowagie.text.Font;

public class TaskDialog
{
	TaskImpl task;

	JDialog dialog;
	private JButton cancelButton;
	private JButton showLogButton;
	private JLabel infoLabel;
	private JLabel verboseLabel;
	Window warningDialogOwner;

	public TaskDialog(Task task, Window owner)
	{
		this.task = (TaskImpl) task;
		this.warningDialogOwner = owner;
		buildDialog(owner);
		addListeners();
		dialog.setVisible(true);
	}

	public void setWarningDialogOwner(Window owner)
	{
		this.warningDialogOwner = owner;
	}

	private void buildDialog(Window owner)
	{
		cancelButton = new JButton("Abort");
		showLogButton = new JButton("Show log");
		infoLabel = new JLabel(task.getUpdateMessage() == null ? " " : task.getUpdateMessage());
		infoLabel.setFont(infoLabel.getFont().deriveFont(Font.BOLD));
		verboseLabel = new JLabel(task.getVerboseMessage() == null ? " " : task.getVerboseMessage());
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

	private void showWarningDialog()
	{
		List<DetailMessage> warnings = task.getWarnings();
		MessagePanel p = new MessagePanel();
		for (int i = 0; i < warnings.size(); i++)
			p.addWarning(warnings.get(i).message, warnings.get(i).detail);
		final JPanel pp = new JPanel(new BorderLayout(10, 10));
		pp.add(new JLabel("The following non-critical errors have occured:"), BorderLayout.NORTH);
		pp.add(p);
		Thread th = new Thread(new Runnable()
		{
			public void run()
			{
				SwingUtil.showInDialog(pp, task.getName(), new Dimension(600, 400), null, (JFrame) warningDialogOwner);
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
			@Override
			public void update(TaskEvent event)
			{
				switch (event)
				{
					case update:
						updateTitle();
						infoLabel.setText(task.getUpdateMessage());
						break;
					case verbose:
						verboseLabel.setText(task.getVerboseMessage());
						break;
					case cancelled:
						dialog.dispose();
						break;
					case failed:
						updateTitle();
						cancelButton.setText("Close");
						showLogButton.setVisible(true);
						infoLabel.setText(task.getError().message);
						verboseLabel.setText(task.getError().detail);
						pack();
						SwingUtil.waitWhileVisible(dialog);
						dialog.dispose();
						break;
					case finished:
						dialog.dispose();
						if (task.hasWarnings())
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
}
