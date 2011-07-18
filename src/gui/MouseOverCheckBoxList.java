package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.UIManager;

import util.SelectionModel;

public class MouseOverCheckBoxList extends JList
{
	// Vector<Integer> checkSelection = new Vector<Integer>();

	SelectionModel checkSelection = new SelectionModel(true);

	// Vector<CheckboxSelectionListener> listeners = new Vector<MouseOverCheckBoxList.CheckboxSelectionListener>();

	// boolean suppressEvents = false;

	public MouseOverCheckBoxList()
	{
		super();
		init();
	}

	public MouseOverCheckBoxList(String[] strings)
	{
		super(strings);
		init();
	}

	public MouseOverCheckBoxList(ListModel model)
	{
		super(model);
		init();
	}

	public SelectionModel getCheckBoxSelection()
	{
		return checkSelection;
	}

	public Object getCheckboxSelectedValue()
	{
		int i = checkSelection.getSelected();
		if (i == -1)
			return null;
		else
			return getModel().getElementAt(i);
	}

	public Object[] getCheckboxSelectedValues()
	{
		int i[] = checkSelection.getSelectedIndices();
		if (i.length == 0)
			return new Object[0];
		else
		{
			Object o[] = new Object[i.length];
			for (int j = 0; j < o.length; j++)
				o[j] = getModel().getElementAt(i[j]);
			return o;
		}
	}

	public void setCheckboxSelectedValue(Object elem)
	{
		int i = -1;
		for (int j = 0; j < getModel().getSize(); j++)
		{
			if (getModel().getElementAt(j).equals(elem))
			{
				i = j;
				break;
			}
		}
		checkSelection.setSelected(i);
	}

	// public void setCheckboxSelectionInverted(int index)
	// {
	// int[] oldSelection = getCheckboxSelection();
	// if (checkSelection.contains(index))
	// checkSelection.removeElement(index);
	// else
	// checkSelection.add(index);
	// MouseOverCheckBoxList.this.repaint();
	//
	// if (!suppressEvents)
	// for (CheckboxSelectionListener l : listeners)
	// l.selectionChanged(getCheckboxSelection(), oldSelection);
	// }
	//
	// public void setCheckboxSelection(int index, boolean selected)
	// {
	// if (selected && checkSelection.contains(index))
	// return;
	// if (!selected && !checkSelection.contains(index))
	// return;
	// setCheckboxSelectionInverted(index);
	// }
	//
	// public void setCheckboxSelectionIndices(int index[], boolean selected)
	// {
	// int[] oldSelection = getCheckboxSelection();
	// setIgnoreRepaint(true);
	// suppressEvents = true;
	// for (int i : index)
	// setCheckboxSelection(i, selected);
	// suppressEvents = false;
	// setIgnoreRepaint(false);
	// repaint();
	// int[] newSelection = getCheckboxSelection();
	// if (!Arrays.equals(oldSelection, newSelection))
	// for (CheckboxSelectionListener l : listeners)
	// l.selectionChanged(getCheckboxSelection(), oldSelection);
	// }
	//
	// public void setCheckboxSelectionAll(boolean selected)
	// {
	// int[] oldSelection = getCheckboxSelection();
	// if (selected && oldSelection.length == getModel().getSize())
	// return;
	// if (!selected && oldSelection.length == 0)
	// return;
	// setIgnoreRepaint(true);
	// suppressEvents = true;
	// for (int i = 0; i < getModel().getSize(); i++)
	// setCheckboxSelection(i, selected);
	// suppressEvents = false;
	// setIgnoreRepaint(false);
	// repaint();
	// for (CheckboxSelectionListener l : listeners)
	// l.selectionChanged(getCheckboxSelection(), oldSelection);
	// }
	//
	// public boolean isAllSelected()
	// {
	// return checkSelection.size() == getModel().getSize();
	// }
	//
	// public boolean isCheckBoxSelected(int index)
	// {
	// return checkSelection.contains(index);
	// }
	//
	// public int[] getCheckboxSelection()
	// {
	// return ArrayUtil.toPrimitiveIntArray(checkSelection);
	// }

	boolean clearOnExit = true;

	public void setClearOnExit(boolean b)
	{
		clearOnExit = b;
	}

	public void init()
	{
		setCellRenderer(new CheckboxCellRenderer());

		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseExited(MouseEvent me)
			{
				if (clearOnExit)
					MouseOverCheckBoxList.this.clearSelection();
			}

			public void mouseClicked(MouseEvent me)
			{
				Point p = new Point(me.getX(), me.getY());
				int clickedIndex = locationToIndex(p);
				int selectedIndex = MouseOverCheckBoxList.this.getSelectedIndex();
				if (clickedIndex == selectedIndex)
					checkSelection.setSelectedInverted(clickedIndex);
			}
		});

		addMouseMotionListener(new MouseAdapter()
		{
			public void mouseMoved(MouseEvent me)
			{
				Point p = new Point(me.getX(), me.getY());
				MouseOverCheckBoxList.this.setSelectedIndex(locationToIndex(p));
			}
		});

		checkSelection.addListener(new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				repaint();
			}
		});
	}

	// public void addCheckboxSelectionListener(CheckboxSelectionListener l)
	// {
	// listeners.add(l);
	// }

	static interface CheckboxSelectionListener
	{
		public void selectionChanged(int[] newSelection, int[] oldSelection);
	}

	static class CheckboxCellRenderer extends JCheckBox implements ListCellRenderer
	{
		Color selectionBackground = UIManager.getColor("List.selectionBackground");
		Color selectionForeground = UIManager.getColor("List.selectionForeground");
		Color background = UIManager.getColor("List.background");
		Color forground = UIManager.getColor("List.foreground");

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus)
		{
			setText(value.toString());
			if (isSelected)
			{
				setForeground(selectionForeground);
				setBackground(selectionBackground);
			}
			else
			{
				setForeground(forground);
				setBackground(background);
			}
			setSelected(((MouseOverCheckBoxList) list).getCheckBoxSelection().isSelected(index));
			return this;
		}
	}

	public static void main(String args[])
	{

		MouseOverCheckBoxList list = new MouseOverCheckBoxList(new String[] { "a", "bbbbbb", "cc" });

		JDialog d = new JDialog();
		JPanel p = new JPanel(new BorderLayout());
		p.add(new JLabel("Mouse over Checkbox List:"), BorderLayout.NORTH);
		p.add(list);
		d.add(p);
		d.pack();
		d.setLocationRelativeTo(null);
		d.setVisible(true);
	}

}
