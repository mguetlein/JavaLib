package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import util.ImageLoader;
import util.StringUtil;
import util.SwingUtil;

public class MessagePanel extends JPanel
{
	static enum Type
	{
		INFO, ERROR, WARNING;
	}

	class Message
	{
		Type type;
		String message;
		String details;
		boolean isSelected = false;

		public Message(Type type, String message, String details)
		{
			this.type = type;
			this.message = message;
			this.details = details;
		}

		String toString = null;

		public String toString()
		{
			if (showMessageDetails)
			{
				if (toString == null)
				{
					if (details == null || details.length() == 0)
						toString = "<html><b>" + message + "</b></html>";
					else
						toString = "<html><b>" + message + "</b><br>"
								+ StringUtil.wordWrap(details, 80).replaceAll("\n", "<br>") + "</html>";
				}
				return toString;
			}
			else
				return "<html><b>" + message + "</b></html>";
		}
	}

	JList list;
	DefaultListModel model;
	JCheckBox showDetails;
	boolean showMessageDetails = false;

	public MessagePanel()
	{
		model = new DefaultListModel();
		list = new JList(model);
		//		{
		//			public Dimension getPreferredScrollableViewportSize()
		//			{
		//				Dimension dim = super.getPreferredScrollableViewportSize();
		//				if (dim.getHeight() > 600)
		//					return new Dimension(dim.width, 600);
		//				else
		//					return dim;
		//			}
		//		};
		list.setFont(list.getFont().deriveFont(Font.PLAIN));
		list.setVisibleRowCount(10);

		list.setCellRenderer(new DefaultListCellRenderer()
		{
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
					boolean cellHasFocus)
			{
				Message message = ((Message) value);
				super.getListCellRendererComponent(list, message, index, isSelected, cellHasFocus);
				switch (message.type)
				{
					case WARNING:
						setIcon(ImageLoader.WARNING);
						break;
					case ERROR:
						setIcon(ImageLoader.ERROR);
						break;
					case INFO:
						setIcon(ImageLoader.INFO);
						break;
					default:
						throw new Error("not handled type");
				}
				return this;
			}
		});

		JScrollPane scroll = new JScrollPane(list);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		setLayout(new BorderLayout(10, 10));
		add(scroll);
		showDetails = new JCheckBox("Show details");
		showDetails.setSelected(showMessageDetails);
		showDetails.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				showMessageDetails = showDetails.isSelected();
				list.setIgnoreRepaint(true);
				model.removeAllElements();
				for (Message m : messages)
					model.addElement(m);
				list.setIgnoreRepaint(false);
				list.repaint();
			}
		});
		add(showDetails, BorderLayout.SOUTH);
	}

	public void addWarning(String message, String details)
	{
		addMessage(new Message(Type.WARNING, message, details));
	}

	public void addError(String message, String details)
	{
		addMessage(new Message(Type.ERROR, message, details));
	}

	public void addInfo(String message, String details)
	{
		addMessage(new Message(Type.INFO, message, details));
	}

	List<Message> messages = new ArrayList<Message>();

	public void addMessage(Message message)
	{
		messages.add(message);
		model.addElement(message);
	}

	public static void main(String args[])
	{
		MessagePanel p = new MessagePanel();
		p.addWarning("warning because of asdfasdf", null);
		p.addWarning("warning because of bla", "more test alöskfj aösldkfj alök jsdfklsj lfjk sd");
		p.addError("some error", "more test alöskfj aösldkfj alök jsdfklsj lfjk sd");
		p.addInfo(
				"info message",
				"more test alöskfj\n \n \n \n \n \n \n \n aösldkfj alök jsdfklsj lfjk sd more test alöskfj aösldkfj alök jsdfklsj lfjk sdmore test alöskfj aösldkfj alök jsdfklsj lfjk sd");
		//		p.expandToLast();
		//p.tree.expandPath(new TreePath(p.root));
		SwingUtil.showInDialog(p);//, new Dimension(400, 600));
	}

}
