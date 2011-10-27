package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import util.ImageLoader;
import util.SwingUtil;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

public class WizardDialog extends JFrame
{
	JLabel titleLabel;
	JTextArea descriptionTextArea;

	JButton next;
	JButton prev;
	JButton cancel;
	JButton finish;

	JPanel centerPanel;
	Vector<WizardPanel> panels;
	int status = 0;

	String title;
	Icon icon;
	Icon additionalIcon;

	DefaultListModel titleListModel;
	JList titleList;

	JLabel iconLabel;
	JLabel additionalIconLabel;

	public WizardDialog(JFrame owner, String title, Icon icon)
	{
		this(owner, title, icon, null);
	}

	public WizardDialog(JFrame owner, String title, Icon icon, Icon additionalIcon)
	{
		this.icon = icon;
		this.title = title;
		this.additionalIcon = additionalIcon;
		buildLayout();
		setLocationRelativeTo(owner);

		panels = new Vector<WizardPanel>();
	}

	private void buildLayout()
	{
		// north panel has title and description

		JPanel northPanel = new JPanel(new BorderLayout(0, 0));
		northPanel.setBackground(Color.WHITE);

		titleLabel = new JLabel();
		titleLabel.setFont(titleLabel.getFont().deriveFont(titleLabel.getFont().getSize() + 4f));
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
		northPanel.add(titleLabel, BorderLayout.NORTH);
		descriptionTextArea = new JTextArea();
		northPanel.add(descriptionTextArea, BorderLayout.SOUTH);
		descriptionTextArea.setEditable(false);
		descriptionTextArea.setOpaque(false);
		descriptionTextArea.setWrapStyleWord(true);
		descriptionTextArea.setLineWrap(true);
		northPanel.setBorder(new EmptyBorder(10, 10, 15, 10));

		// center panel contains the wizard panel
		centerPanel = new JPanel(new BorderLayout());
		//		centerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		centerPanel.setBorder(new CompoundBorder(new MatteBorder(1, 1, 1, 0, centerPanel.getBackground().darker()
				.darker()), new EmptyBorder(10, 10, 10, 5)));

		//		centerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		//		JScrollPane scroll = new JScrollPane(centerPanel);
		//		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		//		scroll.setBorder(new MatteBorder(1, 1, 1, 0, centerPanel.getBackground().darker().darker()));

		JPanel centerPanelContainer = new JPanel(new BorderLayout());
		centerPanelContainer.setOpaque(false);
		centerPanelContainer.add(centerPanel);

		// left panel contains icon and all titles

		DefaultFormBuilder leftPanelBuilder = new DefaultFormBuilder(new FormLayout("p"));
		leftPanelBuilder.setBackground(Color.WHITE);
		if (icon != null)
		{
			iconLabel = new JLabel(icon);
			leftPanelBuilder.append(iconLabel);
		}

		titleListModel = new DefaultListModel();
		titleList = new JList(titleListModel)
		{
			public Dimension getPreferredSize()
			{
				Dimension dim = super.getPreferredSize();
				return new Dimension(dim.width + 36, dim.height + 2);
			}
		};
		titleList.setBorder(null);
		titleList.setOpaque(false);
		titleList.setEnabled(false);
		DefaultListCellRenderer rend = new DefaultListCellRenderer()
		{
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
					boolean cellHasFocus)
			{
				value = (index + 1) + ". " + value;
				if (isSelected)
					value = "<html><u><b>" + value + "</b></u></html>";
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				setEnabled(true);
				if (!isSelected)
					setFont(getFont().deriveFont(Font.PLAIN));
				if (index == errorPanel())
					setIcon(ImageLoader.ERROR);
				else if (panels.get(index).hasWarning())
					setIcon(ImageLoader.WARNING);
				else
					setIcon(null);
				return this;
			}
		};
		rend.setHorizontalTextPosition(SwingConstants.LEFT);
		rend.setOpaque(false);
		rend.setForeground(Color.BLACK);
		titleList.setCellRenderer(rend);
		leftPanelBuilder.append(titleList);

		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.setBackground(Color.WHITE);
		leftPanel.add(leftPanelBuilder.getPanel(), BorderLayout.NORTH);
		additionalIconLabel = new JLabel(additionalIcon);
		leftPanel.add(additionalIconLabel, BorderLayout.SOUTH);
		leftPanel.setBorder(new CompoundBorder(new MatteBorder(0, 0, 0, 0, centerPanel.getBackground().darker()
				.darker()), new EmptyBorder(10, 10, 0, 10)));

		// button panel is south

		next = new JButton("Next");
		prev = new JButton("Previous");
		cancel = new JButton("Close");
		finish = new JButton(getFinishText());
		JPanel buttons = ButtonBarFactory.buildRightAlignedBar(cancel, prev, next, finish);
		buttons.setBackground(Color.WHITE);
		buttons.setBorder(new CompoundBorder(
				new MatteBorder(0, 0, 0, 0, centerPanel.getBackground().darker().darker()), new EmptyBorder(15, 10, 10,
						10)));

		centerPanelContainer.add(northPanel, BorderLayout.NORTH);
		JPanel p = new JPanel(new BorderLayout());
		p.add(leftPanel, BorderLayout.WEST);
		p.add(centerPanelContainer);
		p.add(buttons, BorderLayout.SOUTH);
		getContentPane().add(p);

		//		SwingUtil.setDebugBorder(centerPanelContainer, Color.RED);
		//		SwingUtil.setDebugBorder(centerPanel, Color.CYAN);

		addListeners();
	}

	public void addClickLinkToIcon(String link)
	{
		SwingUtil.addClickLink(iconLabel, link);
	}

	public void addClickLinkToAdditionalIcon(String link)
	{
		SwingUtil.addClickLink(additionalIconLabel, link);
	}

	private void addListeners()
	{
		titleList.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				Point p = new Point(e.getX(), e.getY());
				proceedTo(titleList.locationToIndex(p));
			}
		});

		cancel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				WizardDialog.this.setVisible(false);
			}
		});
		finish.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				for (int i = status; i < panels.size(); i++)
					panels.get(i).proceed();
				finish();
				WizardDialog.this.setVisible(false);
			}
		});
		next.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				panels.get(status).proceed();
				update(status + 1);
			}
		});
		prev.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				update(status - 1);
			}
		});
	}

	public void proceedTo(int index)
	{
		if (index < status)
		{
			while (index < status)
			{
				update(status - 1);
			}
		}
		else if (index > status)
		{
			while (index > status)
			{
				boolean canProceed = (status < panels.size() - 1 && panels.get(status).canProceed());
				if (!canProceed)
					break;
				else
				{
					panels.get(status).proceed();
					update(status + 1);
				}
			}
		}
	}

	protected String getFinishText()
	{
		return "Finish";
	}

	public void finish()
	{

	}

	public void update()
	{
		if (panels.size() > 0)
			update(status);
	}

	protected void update(int status)
	{
		setIgnoreRepaint(true);
		if (this.status != status || centerPanel.getComponents().length == 0)
		{
			this.status = status;
			centerPanel.removeAll();

			JPanel p = new JPanel(new BorderLayout());
			p.setBorder(new EmptyBorder(0, 0, 0, 20));
			p.add(panels.get(status));
			JScrollPane scroll = new JScrollPane(p);
			scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			//			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			scroll.setBorder(null);
			centerPanel.add(scroll);

			//centerPanel.add(panels.get(status));

			validate();
		}

		prev.setEnabled(status > 0);
		next.setEnabled(status < panels.size() - 1 && panels.get(status).canProceed());
		finish.setEnabled(errorPanel() == -1);
		//setTitle(title + " (" + (status + 1) + "/" + panels.size() + ")");
		setTitle(title);
		titleLabel.setText(panels.get(status).getTitle() + " (step " + (status + 1) + " of " + panels.size() + ")");
		descriptionTextArea.setText(panels.get(status).getDescription());

		titleList.setSelectedIndex(status);
		setIgnoreRepaint(false);
		repaint();
	}

	protected int errorPanel()
	{
		for (int i = status; i < panels.size(); i++)
			if (!panels.get(i).canProceed())
				return i;
		return -1;
	}

	public void addPanel(WizardPanel p)
	{
		titleListModel.addElement(p.getTitle());
		panels.add(p);
	}

	public void setVisible(boolean b)
	{
		if (b)
		{
			update();
		}
		super.setVisible(b);
	}

	public static void main(String args[])
	{
		WizardPanel p1 = new WizardPanel()
		{
			@Override
			public boolean canProceed()
			{
				return true;
			}

			@Override
			public String getTitle()
			{
				return "first panel";
			}

			@Override
			public void proceed()
			{
			}

			@Override
			public String getDescription()
			{
				return "this is some info text on the first panel";
			}

			@Override
			public boolean hasWarning()
			{
				return true;
			}
		};
		p1.add(new JLabel("test"));
		WizardPanel p2 = new WizardPanel()
		{
			@Override
			public boolean canProceed()
			{
				return true;
			}

			@Override
			public String getTitle()
			{
				return "second panel";
			}

			@Override
			public void proceed()
			{
			}

			@Override
			public String getDescription()
			{
				return "information on the second panel";
			}

			@Override
			public boolean hasWarning()
			{
				return false;
			}
		};
		p2.add(new JLabel("panel2"));

		WizardDialog w = new WizardDialog(null, "Test wizard", null);
		w.addPanel(p1);
		w.addPanel(p2);
		w.setSize(600, 400);
		w.setLocationRelativeTo(null);
		w.setVisible(true);
		SwingUtil.waitWhileVisible(w);
		System.exit(0);
	}

}
