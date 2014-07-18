package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import util.SwingUtil;

public class LinkButton extends JLabel
{
	Color foregroundColor;
	Font foregroundFont;
	Color selectedForegroundColor;
	Font selectedForegroundFont;

	String text;
	boolean underline = true;

	private List<ActionListener> actionListeners = new ArrayList<ActionListener>();

	public LinkButton(String text)
	{
		super();

		setText(text);

		foregroundColor = Color.BLACK;
		foregroundFont = getFont().deriveFont(Font.PLAIN);
		setForeground(foregroundColor);
		setFont(foregroundFont);

		selectedForegroundColor = Color.BLACK;
		selectedForegroundFont = getFont().deriveFont(Font.BOLD);

		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (isEnabled())
					doAction();
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				enabledHovering(true);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				enabledHovering(false);
			}
		});
	}

	public void doAction()
	{
		for (ActionListener l : actionListeners)
			l.actionPerformed(new ActionEvent(LinkButton.this, -1, ""));
	}

	protected void enabledHovering(boolean hoverTrue)
	{
		if (hoverTrue)
		{
			if (isEnabled())
			{
				setForeground(selectedForegroundColor);
				setFont(selectedForegroundFont);
			}
		}
		else
		{
			setForeground(foregroundColor);
			setFont(foregroundFont);
		}
	}

	public void setText(String text)
	{
		this.text = text;
		super.setText(underline ? "<html><u>" + text + "</u></html>" : text);
	}

	public void setUnderline(boolean underline)
	{
		this.underline = underline;
		setText(text);
	}

	public void setForegroundColor(Color foregroundColor)
	{
		this.foregroundColor = foregroundColor;
		setForeground(foregroundColor);
	}

	public void setForegroundFont(Font foregroundFont)
	{
		this.foregroundFont = foregroundFont;
		setFont(foregroundFont);
	}

	public void setSelectedForegroundColor(Color selectedForegroundColor)
	{
		this.selectedForegroundColor = selectedForegroundColor;
	}

	public void setSelectedForegroundFont(Font selectedForegroundFont)
	{
		this.selectedForegroundFont = selectedForegroundFont;
	}

	public void addActionListener(ActionListener l)
	{
		actionListeners.add(l);
	}

	public static void main(String args[])
	{
		LinkButton l = new LinkButton("test-button");
		l.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("pressed");
			}
		});
		SwingUtil.showInDialog(l);
	}
}
