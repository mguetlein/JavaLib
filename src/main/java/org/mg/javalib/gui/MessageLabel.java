package org.mg.javalib.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

import org.mg.javalib.util.ImageLoader;

public class MessageLabel extends JPanel
{
	private JLabel infoIcon = new JLabel();
	private JTextArea infoTextArea = new JTextArea();
	private LinkButton link = new LinkButton("");
	private Action action;

	public MessageLabel(Messages msgs)
	{
		this();
		setMessages(msgs);
	}

	public MessageLabel(Message msg)
	{
		this();
		setMessage(msg);
	}

	public MessageLabel()
	{
		infoTextArea.setFont(infoTextArea.getFont().deriveFont(Font.PLAIN));
		infoTextArea.setBorder(null);
		infoTextArea.setEditable(false);
		infoTextArea.setOpaque(false);
		infoTextArea.setWrapStyleWord(true);
		infoTextArea.setLineWrap(true);
		setLayout(new BorderLayout(5, 0));
		add(infoIcon, BorderLayout.WEST);
		add(infoTextArea);
		add(link, BorderLayout.SOUTH);
		link.setForegroundFont(infoTextArea.getFont().deriveFont(Font.PLAIN));
		link.setSelectedForegroundFont(infoTextArea.getFont().deriveFont(Font.PLAIN));
		link.setForegroundColor(Color.BLUE);
		link.setSelectedForegroundColor(Color.BLUE);
		link.setHorizontalAlignment(SwingConstants.RIGHT);
		link.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				action.actionPerformed(e);
			}
		});
		setOpaque(true);
		setBorder(new MatteBorder(1, 1, 1, 1, getBackground().darker().darker()));
		setMessages(null);
	}

	public void setMessageFont(Font f)
	{
		infoTextArea.setFont(f);
	}

	public Font getMessageFont()
	{
		return infoTextArea.getFont();
	}

	public void setMessages(Messages msgs)
	{
		Message msg = null;
		if (msgs != null)
			msg = msgs.getMostUrgentMessage();
		setMessage(msg);
	}

	public void setMessage(Message msg)
	{

		if (msg == null || msg.getString().length() == 0)
		{
			setVisible(false);
		}
		else
		{
			setIgnoreRepaint(true);
			infoTextArea.setText(msg.getString());
			if (msg.getAction() != null)
			{
				link.setVisible(true);
				action = msg.getAction();
				link.setText(action.getValue(Action.NAME).toString());
			}
			else
				link.setVisible(false);
			switch (msg.getType())
			{
				case Info:
					infoIcon.setIcon(ImageLoader.getImage(ImageLoader.Image.info));
					break;
				case Slow:
					infoIcon.setIcon(ImageLoader.getImage(ImageLoader.Image.hourglass));
					break;
				case Warning:
					infoIcon.setIcon(ImageLoader.getImage(ImageLoader.Image.warning));
					break;
				case Error:
					infoIcon.setIcon(ImageLoader.getImage(ImageLoader.Image.error));
					break;
			}
			setVisible(true);
			setIgnoreRepaint(false);
			repaint();
			revalidate();
		}
	}
}
