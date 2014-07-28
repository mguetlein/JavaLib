package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import util.ArrayUtil;

public class CheckBoxSelectPanel extends JPanel
{
	MouseOverCheckBoxList list;
	DefaultListModel listModel;
	MouseOverCheckBoxListComponent listPanel;

	public CheckBoxSelectPanel(String description, Object[] values, boolean selection[])
	{
		buildLayout(description);

		for (Object o : values)
			listModel.addElement(o);

		List<Integer> selected = new ArrayList<Integer>();
		for (int i = 0; i < selection.length; i++)
			if (selection[i])
				selected.add(i);
		list.getCheckBoxSelection().setSelectedIndices(ArrayUtil.toPrimitiveIntArray(selected));

		for (int i = 0; i < selection.length; i++)
		{
			if (selection[i])
			{
				list.ensureIndexIsVisible(i);
				break;
			}
		}

		//System.out.println("selected: " + ArrayUtil.toString(list.getCheckBoxSelection().getSelectedIndices()));
	}

	private void buildLayout(String description)
	{
		JTextArea d = null;
		if (description != null)
		{
			d = new JTextArea(description);
			d.setEditable(false);
			d.setOpaque(false);
			d.setBorder(null);
			d.setWrapStyleWord(true);
			d.setLineWrap(true);
			//text-area hack to prevent pref-size to be sth like 0,2500 
			d.setPreferredSize(null);
			d.setSize(new Dimension(200, Integer.MAX_VALUE));
		}

		listModel = new DefaultListModel();
		list = new MouseOverCheckBoxList(listModel);
		list.setClearOnExit(false);
		listPanel = new MouseOverCheckBoxListComponent(list);

		setLayout(new BorderLayout(0, 10));
		if (description != null)
			add(d, BorderLayout.NORTH);
		add(listPanel);
	}

	public Object[] getSelectedValues()
	{
		return list.getCheckboxSelectedValues();
	}

	public int[] getSelectedIndices()
	{
		return list.getCheckBoxSelection().getSelectedIndices();
	}

	public void addListener(PropertyChangeListener l)
	{
		list.getCheckBoxSelection().addListener(l);
	}
}
