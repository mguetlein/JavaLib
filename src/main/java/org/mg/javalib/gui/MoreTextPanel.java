package org.mg.javalib.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JOptionPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import org.mg.javalib.util.SwingUtil;

public class MoreTextPanel extends TextPanel
{
	LinkButton moreDescriptionButton;
	String dialogTitle;

	@Override
	protected void buildLayout()
	{
		moreDescriptionButton = new LinkButton("complete description...");
		moreDescriptionButton.setForegroundFont(moreDescriptionButton.getFont().deriveFont(Font.PLAIN));
		moreDescriptionButton.setSelectedForegroundFont(moreDescriptionButton.getFont().deriveFont(Font.PLAIN));
		moreDescriptionButton.setSelectedForegroundColor(Color.BLUE);
		moreDescriptionButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				TextPanel p = MoreTextPanel.this.cloneTextPanel();
				p.setPreferredWith(400);
				JOptionPane.showMessageDialog(MoreTextPanel.this.getTopLevelAncestor(), p, dialogTitle,
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		moreDescriptionButton.setBorder(new CompoundBorder(new CompoundBorder(new MatteBorder(1, 0, 0, 0, editorPane
				.getBackground().darker()), new MatteBorder(1, 0, 0, 0, editorPane.getBackground().brighter())),
				new EmptyBorder(2, 0, 0, 0)));

		setLayout(new BorderLayout(0, 0));
		add(editorPane, BorderLayout.CENTER);
		editorPane.setBorder(null);
		add(moreDescriptionButton, BorderLayout.SOUTH);

		addComponentListener(new ComponentAdapter()
		{

			@Override
			public void componentShown(ComponentEvent e)
			{
				moreDescriptionButton.setVisible(editorPane.getPreferredSize().getHeight() > editorPane.getSize()
						.getHeight());
			}

			@Override
			public void componentResized(ComponentEvent e)
			{
				moreDescriptionButton.setVisible(editorPane.getPreferredSize().getHeight() > editorPane.getSize()
						.getHeight());
			}
		});
	}

	public void setDialogTitle(String s)
	{
		dialogTitle = s;
	}

	public static void main(String args[])
	{
		MoreTextPanel d = new MoreTextPanel();
		d.addParagraph("Lorem ipsum dolor sit amet, http://google.de consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet cl");
		d.setDialogTitle("Dialog Title");
		d.setPreferredWith(300);
		SwingUtil.showInDialog(d, new Dimension(400, 300));
		System.exit(0);
	}

}
