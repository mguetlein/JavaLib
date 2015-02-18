package org.mg.javalib.gui;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class MouseOverList extends JList
{
	public MouseOverList()
	{
		super();
		init();
	}

	public MouseOverList(Object[] elements)
	{
		super(elements);
		init();
	}

	public MouseOverList(ListModel m)
	{
		super(m);
		init();
	}

	boolean clearOnExit = true;
	int lastSelectedIndex = -1;

	public void setClearOnExit(boolean b)
	{
		clearOnExit = b;
	}

	/**
	 * != -1
	 * 
	 * @return
	 */
	public int getLastSelectedIndex()
	{
		return lastSelectedIndex;
	}

	public void init()
	{
		addListSelectionListener(new ListSelectionListener()
		{

			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				int index = MouseOverList.this.getSelectedIndex();
				if (index >= 0)
					lastSelectedIndex = index;
			}
		});

		addMouseMotionListener(new MouseAdapter()
		{
			public void mouseMoved(MouseEvent me)
			{
				Point p = new Point(me.getX(), me.getY());
				MouseOverList.this.setSelectedIndex(locationToIndex(p));
			}
		});

		addMouseListener(new MouseAdapter()
		{
			public void mouseExited(MouseEvent e)
			{
				if (clearOnExit)
					MouseOverList.this.clearSelection();
			}
		});
		setFocusable(false);
	}

	public static void main(String args[])
	{

		MouseOverList list = new MouseOverList(new String[] { "a", "bbbbbb", "cc" });

		JDialog d = new JDialog();
		JPanel p = new JPanel(new BorderLayout());
		p.add(new JLabel("Mouse over List:"), BorderLayout.NORTH);
		p.add(list);
		d.add(p);
		d.pack();
		d.setLocationRelativeTo(null);
		d.setVisible(true);
	}

}
