package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import util.StringUtil;

public class DescriptionPanel extends JPanel
{
	JTextArea propertyDescriptionTextArea;
	LinkButton moreDescriptionButton;

	int maxLength;

	String title;
	String text;

	public DescriptionPanel()
	{
		this(400);
	}

	public DescriptionPanel(int maxLength)
	{
		this.maxLength = maxLength;

		propertyDescriptionTextArea = new JTextArea();
		propertyDescriptionTextArea.setBorder(null);
		propertyDescriptionTextArea.setEditable(false);
		propertyDescriptionTextArea.setOpaque(false);
		propertyDescriptionTextArea.setWrapStyleWord(true);
		propertyDescriptionTextArea.setLineWrap(true);

		moreDescriptionButton = new LinkButton("complete description...");
		moreDescriptionButton.setForegroundFont(moreDescriptionButton.getFont().deriveFont(Font.PLAIN));
		moreDescriptionButton.setSelectedForegroundFont(moreDescriptionButton.getFont().deriveFont(Font.PLAIN));
		moreDescriptionButton.setSelectedForegroundColor(Color.BLUE);
		moreDescriptionButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(DescriptionPanel.this.getTopLevelAncestor(),
						StringUtil.wordWrap(text, 80), title, JOptionPane.INFORMATION_MESSAGE);
			}
		});

		setLayout(new BorderLayout(10, 10));
		add(propertyDescriptionTextArea);
		add(moreDescriptionButton, BorderLayout.SOUTH);
	}

	public void setText(String title, String text)
	{
		setText(title, text, maxLength);
	}

	public void setText(String title, String text, int maxLength)
	{
		this.title = title;
		this.text = text;
		this.maxLength = maxLength;

		if (text == null)
			text = "";
		if (text.length() > maxLength)
		{
			propertyDescriptionTextArea.setText(text.substring(0, maxLength / 2) + "...");
			moreDescriptionButton.setVisible(true);
		}
		else
		{
			propertyDescriptionTextArea.setText(text);
			moreDescriptionButton.setVisible(false);
		}

	}

}
