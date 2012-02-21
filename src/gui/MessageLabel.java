package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

import util.ImageLoader;

public class MessageLabel extends JPanel
{
	private JLabel infoIcon = new JLabel();
	private JTextArea infoTextArea = new JTextArea();
	private LinkButton link = new LinkButton("");
	private String url;

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
				try
				{
					Desktop.getDesktop().browse(new URI(url));
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
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
			if (msg.getURL() != null)
			{
				link.setVisible(true);
				url = msg.getURL();
				link.setText(msg.getURLText() == null ? url : msg.getURLText());
			}
			else
				link.setVisible(false);
			switch (msg.getType())
			{
				case Info:
					infoIcon.setIcon(ImageLoader.INFO);
					break;
				case Slow:
					infoIcon.setIcon(ImageLoader.HOURGLASS);
					break;
				case Warning:
					infoIcon.setIcon(ImageLoader.WARNING);
					break;
				case Error:
					infoIcon.setIcon(ImageLoader.ERROR);
					break;
			}
			setVisible(true);
			setIgnoreRepaint(false);
			repaint();
			revalidate();
		}
	}
}
