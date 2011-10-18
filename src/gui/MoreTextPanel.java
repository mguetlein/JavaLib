package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import util.SwingUtil;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

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

		setLayout(new BorderLayout());
		add(editorPane, BorderLayout.CENTER);
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
		d.addParagraph("which is extremely helpful. http://www.google.de/  Turns out that the second argument to the String replaceAll method “may” have some issues extremely helpful. http://www.google.de/  Turns out that the second argument to the String extremely helpful. http://www.google.de/  Turns out that the second argument to the String extremely helpful. http://www.google.de/  Turns out that the second argument to the String with dollar signs and backslashes which you only find out about if you dig into the Matcher class that backs the replaceAll method or if you’re lucky and you read about the whole thing on a site devoted to regular expressions. In short:\n"
				+ "    In the replacement text, a dollar sign not follo http://www.asdf.com/ wed by a digit caus extremely helpful. http://www.google.de/  Turns out that the second argument to the Stringes an IllegalArgumentException to be thrown. If there are less than 9 backreferences, a dollar sign followed by a digit greater than the number of backreferences throws an IndexOutOfBoundsException. So be careful if the replacement string is a user-specified string. To insert a dollar sign as literal text, use \\$ in the replacement text. When coding the replacement text as a literal string in your source code, remember that the backslash itself must be escaped too: “\\\\$”. ");
		d.setDialogTitle("Dialog Title");
		SwingUtil.setDebugBorder(d);

		d.setPreferredWith(300);

		JPanel pp = new JPanel(new FormLayout("fill:p:grow", "p,fill:100:grow"));
		CellConstraints cc = new CellConstraints();
		pp.add(d, cc.xy(1, 2));
		JLabel lab = new JLabel("more stuff");
		SwingUtil.setDebugBorder(lab, Color.GREEN);
		lab.setPreferredSize(new Dimension(100, 300));
		pp.add(lab, cc.xy(1, 1));

		//		JPanel pp = new JPanel(new BorderLayout());
		//		pp.add(d, BorderLayout.NORTH);

		SwingUtil.showInDialog(pp, new Dimension(400, 800));

		//		SwingUtil.showInDialog(d, new Dimension(400, 300));
		System.exit(0);
	}
}
