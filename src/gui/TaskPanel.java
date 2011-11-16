package gui;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.lowagie.text.Font;

public class TaskPanel extends JPanel
{
	public static boolean PRINT_VERBOSE_MESSAGES = false;

	Task t;

	private JButton killButton;
	private JProgressBar bar;
	private JLabel infoLabel;
	private JLabel verboseLabel;

	//	private MessagePanel detailPanel;
	//	private HideablePanel hidablePanel;

	TaskPanel(Task t)
	{
		this.t = t;
		buildLayout();

		t.addPropertyChangeListener(new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				String prop = evt.getPropertyName();
				String message = (String) evt.getNewValue();
				//				String details = (String) evt.getOldValue();

				//				if (prop.equals(Task.PROPERTY_INFO))
				//					detailPanel.addInfo(message, details);
				//				else if (prop.equals(Task.PROPERTY_WARNING))
				//					detailPanel.addWarning(message, details);
				//				else if (prop.equals(Task.PROPERTY_ERROR))
				//					detailPanel.addError(message, details);

				if (TaskPanel.this.t.progress())
				{
					bar.setValue(TaskPanel.this.t.getPercent());
					bar.setVisible(true);
				}

				if (prop.equals(Task.PROPERTY_VERBOSE))
				{
					verboseLabel.setText(message);
					if (PRINT_VERBOSE_MESSAGES)
						System.out.println(">> " + message);
				}
				else
				{
					if (prop.equals(Task.PROPERTY_WARNING) || prop.equals(Task.PROPERTY_ERROR))
						System.err.println("> " + message);
					else
						System.out.println("> " + message);

					if (!prop.equals(Task.PROPERTY_WARNING))
					{
						if (prop.equals(Task.PROPERTY_ERROR))
							message = "ERROR: " + message;
						infoLabel.setText(message);
						verboseLabel.setText(" ");
					}
				}
			}
		});
	}

	private void buildLayout()
	{
		killButton = new JButton("Abort");
		bar = new JProgressBar(0, 100);
		bar.setVisible(false);
		infoLabel = new JLabel(" ");
		infoLabel.setFont(infoLabel.getFont().deriveFont(Font.BOLD));
		verboseLabel = new JLabel(" ");

		//FormLayout f = new FormLayout("600px", "p,3dlu,p,3dlu,p,6dlu,fill:p:grow,6dlu,p");
		FormLayout f = new FormLayout("600px", "p,4dlu,p,6dlu,p,6dlu,p");
		CellConstraints cc = new CellConstraints();
		JPanel p = new JPanel(f);

		//		detailPanel = new MessagePanel();
		//		hidablePanel = new HideablePanel("History:", true);
		//		hidablePanel.setVisible(false);
		//		hidablePanel.addComponent(detailPanel);

		killButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				t.cancel();
			}
		});
		killButton.setVisible(false);

		//		dialog.addWindowListener(new WindowAdapter()
		//		{
		//			public void windowClosing(WindowEvent e)
		//			{
		//				firePropertyChanageEvent(PROPERTY_ABORT);
		//				//System.exit(1);
		//				dialog.setVisible(false);
		//			}
		//		});

		//JPanel buttonBar = ButtonBarFactory.buildLeftAlignedBar(hideButton, killButton);
		JPanel buttonBar = ButtonBarFactory.buildLeftAlignedBar(killButton);

		p.add(infoLabel, cc.xy(1, 1));
		p.add(verboseLabel, cc.xy(1, 3));
		p.add(bar, cc.xy(1, 5));
		//		p.add(hidablePanel, cc.xy(1, 7));
		//p.add(buttonBar, cc.xy(1, 9));
		p.add(buttonBar, cc.xy(1, 7));
		p.setBorder(new EmptyBorder(10, 10, 10, 10));

		setLayout(new BorderLayout());
		add(p);
	}

	public JDialog showDialog(Window owner, final String title)
	{
		//		hidablePanel.setVisible(true);
		killButton.setVisible(true);
		bar.setVisible(true);

		final JDialog d = new JDialog(owner, title);
		d.setModal(false);
		d.add(this);
		d.pack();
		d.setLocationRelativeTo(owner);

		if (!t.progress)
			bar.setVisible(false);

		t.addPropertyChangeListener(new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				String prop = evt.getPropertyName();
				//				String message = evt.getNewValue().toString();
				//				String details = evt.getOldValue().toString();

				if (t.getPercent() == 0)
					d.setTitle(title);
				else
					d.setTitle(t.getPercent() + "% - " + title);
				if (prop.equals(Task.PROPERTY_ERROR))
					d.setTitle("ERROR: " + d.getTitle());
			}
		});
		//		hidablePanel.addPropertyChangeListener("Hiding", new PropertyChangeListener()
		//		{
		//			@Override
		//			public void propertyChange(PropertyChangeEvent evt)
		//			{
		//				if (hidablePanel.isHidden())
		//					d.setSize(d.getWidth(), d.getHeight() - 100);
		//				else
		//					d.setSize(d.getWidth(), d.getHeight() + 100);
		//			}
		//		});
		killButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				d.setVisible(false);
			}
		});
		d.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				t.cancel();
			}
		});
		d.setVisible(true);
		return d;
	}

}
