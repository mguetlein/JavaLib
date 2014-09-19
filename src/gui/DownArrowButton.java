package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import util.ImageLoader;
import util.ImageLoader.Image;
import util.SwingUtil;

public class DownArrowButton extends JPanel
{
	JButton button;
	JButton down;
	JPopupMenu popup;

	public DownArrowButton(String text)
	{
		button = new JButton(text);
		down = new JButton(ImageLoader.getImage(Image.down14))
		{
			@Override
			public Dimension getPreferredSize()
			{
				return new Dimension(super.getPreferredSize().width, button.getPreferredSize().height);
			}
		};
		down.setMargin(new Insets(0, 1, 0, 1)); // top/bottom insets dont matter because height is used from main button
		down.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (down.getModel().isPressed())
				{
					popup.show(down, 0, down.getHeight());
				}
			}
		});
		setLayout(new BorderLayout(0, 0));
		popup = new JPopupMenu();
		popup.add(new JMenuItem("yet to be added"));
		add(button);
		add(down, BorderLayout.EAST);
	}

	public void addActionListener(ActionListener a)
	{
		button.addActionListener(a);
	}

	public void setPopupMenu(JPopupMenu m)
	{
		this.popup = m;
	}

	public static void main(String[] args)
	{
		DownArrowButton d = new DownArrowButton("Load");
		d.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				System.err.println("loading");
			}
		});
		JPopupMenu m = new JPopupMenu();
		m.add(new JMenuItem("<html>ene<br><span style=\"font-size:75%\">this is more info on ene</span></html>"));
		m.add(new JMenuItem("mene"));
		m.add(new JMenuItem("miste"));
		d.setPopupMenu(m);
		SwingUtil.showInDialog(d);
		System.exit(0);
	}

}
