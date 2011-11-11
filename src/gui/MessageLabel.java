package gui;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.MatteBorder;

import util.ImageLoader;

public class MessageLabel extends JPanel
{
	private JLabel infoIcon;
	private JTextArea infoTextArea;

	public MessageLabel()
	{
		infoIcon = new JLabel();
		infoTextArea = new JTextArea();
		infoTextArea.setFont(infoTextArea.getFont().deriveFont(Font.PLAIN));
		infoTextArea.setBorder(null);
		infoTextArea.setEditable(false);
		infoTextArea.setOpaque(false);
		infoTextArea.setWrapStyleWord(true);
		infoTextArea.setLineWrap(true);
		setLayout(new BorderLayout(5, 0));
		add(infoIcon, BorderLayout.WEST);
		add(infoTextArea);

		setOpaque(true);
		setBorder(new MatteBorder(1, 1, 1, 1, getBackground().darker().darker()));

		setMessages(null);
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
