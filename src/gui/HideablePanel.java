package gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;

import util.ImageLoader;
import util.SwingUtil;

public class HideablePanel extends JPanel
{
	boolean hide;
	LinkButton button;

	public HideablePanel(String title, boolean hide)
	{
		this.hide = hide;
		buildLayout(title);
	}

	public void setHorizontalAlignement(int alignement)
	{
		button.setHorizontalAlignment(alignement);
	}

	private void buildLayout(String title)
	{
		setLayout(new BorderLayout(0, 6));
		button = new LinkButton(title);
		button.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				setHide(!hide);
				firePropertyChange("Hiding", !hide, hide);
			}
		});
		//button.setUnderline(false);
		button.setForegroundFont(button.getFont().deriveFont(Font.BOLD));
		update();
	}

	public void addComponent(JComponent comp)
	{
		setIgnoreRepaint(true);
		removeAll();
		add(button, BorderLayout.NORTH);
		add(comp);
		comp.setVisible(!hide);
		setIgnoreRepaint(false);
		validate();
		repaint();
	}

	private void update()
	{
		button.setIcon(hide ? ImageLoader.RIGHT : ImageLoader.DOWN);
		if (getComponentCount() > 0)
			getComponent(1).setVisible(!hide);
	}

	public void setHide(boolean hide)
	{
		if (this.hide != hide)
		{
			this.hide = hide;
			update();
		}
	}

	public static void main(String args[])
	{
		HideablePanel p = new HideablePanel("test-panel", true);
		JPanel pp = new JPanel();
		pp.add(new JList(new String[] { "ene", "mene", "miste" }));
		p.addComponent(pp);
		SwingUtil.showInDialog(p);
	}
}
