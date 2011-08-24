package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

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
		moreDescriptionButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(DescriptionPanel.this.getTopLevelAncestor(), text, title,
						JOptionPane.INFORMATION_MESSAGE);
			}
		});

		setLayout(new BorderLayout());
		add(propertyDescriptionTextArea);
		add(moreDescriptionButton, BorderLayout.SOUTH);
	}

	public void setText(String title, String text)
	{
		this.title = title;
		this.text = text;

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
