package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import util.ImageLoader;
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

		public String toString()
		{
			return message;
		}
	}

	JList list;
	DefaultListModel model;

	public MessagePanel()
	{
		model = new DefaultListModel();
		list = new JList(model);

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

				if (message.isSelected && message.details != null)
				{
					JPanel p = new JPanel(new BorderLayout());
					p.setOpaque(false);
					p.add(this);
					JTextArea a = new JTextArea(message.details);
					a.setEditable(false);
					p.add(a, BorderLayout.SOUTH);
					p.setBorder(new EmptyBorder(0, 0, 5, 0));
					return p;
				}
				else
				{
					return this;
				}
			}
		});

		// dirtiest hack ever!!!
		list.addListSelectionListener(new ListSelectionListener()
		{
			boolean hacking = false;
			Message oldModel = null;

			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				if (hacking)
					return;
				list.setIgnoreRepaint(true);
				hacking = true;

				if (oldModel != null)
					oldModel.isSelected = false;

				int index = list.getSelectedIndex();
				Message o = (Message) model.remove(index);
				o.isSelected = true;
				model.insertElementAt(o, index);
				list.setSelectedIndex(index);

				list.setIgnoreRepaint(false);
				list.invalidate();
				list.repaint();

				oldModel = o;

				hacking = false;

			}
		});

		JScrollPane scroll = new JScrollPane(list);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		list.setVisibleRowCount(5);
		setLayout(new BorderLayout());
		add(scroll);
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

	public void addMessage(Message message)
	{
		model.addElement(message);
	}

	public static void main(String args[])
	{
		MessagePanel p = new MessagePanel();
		p.addWarning("warning because of bla", "more test alöskfj aösldkfj alök jsdfklsj lfjk sd");
		p.addError("some error", "more test alöskfj aösldkfj alök jsdfklsj lfjk sd");
		p.addInfo(
				"info message",
				"more test alöskfj aösldkfj alök jsdfklsj lfjk sd more test alöskfj aösldkfj alök jsdfklsj lfjk sdmore test alöskfj aösldkfj alök jsdfklsj lfjk sd");
		//		p.expandToLast();
		//p.tree.expandPath(new TreePath(p.root));
		SwingUtil.showInDialog(p, new Dimension(400, 600));
	}

}
