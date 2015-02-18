package org.mg.javalib.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class MouseOverCheckBoxListComponent extends JPanel
{
	JScrollPane scrollPane;
	MouseOverCheckBoxList list;
	JCheckBox selectAllCheckbox;
	boolean blockSelf = false;

	public JScrollPane getScrollPane()
	{
		return scrollPane;
	}

	public MouseOverCheckBoxListComponent(MouseOverCheckBoxList list)
	{
		this.list = list;

		buildLayout();
		installListeners();
	}

	private void installListeners()
	{
		selectAllCheckbox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (blockSelf)
					return;
				blockSelf = true;
				if (selectAllCheckbox.isSelected())
				{
					int[] indices = new int[list.getModel().getSize()];
					for (int i = 0; i < indices.length; i++)
						indices[i] = i;
					list.getCheckBoxSelection().setSelectedIndices(indices);
				}
				else
					list.getCheckBoxSelection().clearSelection();
				blockSelf = false;
			}
		});

		list.getCheckBoxSelection().addListener(new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				if (blockSelf)
					return;
				blockSelf = true;
				selectAllCheckbox.setSelected(list.getCheckBoxSelection().getNumSelected() == list.getModel().getSize());
				blockSelf = false;
			}
		});
	}

	public void buildLayout()
	{
		setLayout(new BorderLayout());
		selectAllCheckbox = new JCheckBox("Select all");
		selectAllCheckbox.setSelected(list.getModel().getSize() > 0
				&& list.getCheckBoxSelection().getNumSelected() == list.getModel().getSize());
		add(selectAllCheckbox, BorderLayout.NORTH);
		scrollPane = new JScrollPane(list);
		add(scrollPane);
	}

	public static void main(String args[])
	{

		MouseOverCheckBoxList list = new MouseOverCheckBoxList(new String[] { "a", "bbbbbb", "cc" });

		JDialog d = new JDialog();
		JPanel p = new JPanel(new BorderLayout());
		p.add(new JLabel("Mouse over Checkbox List:"), BorderLayout.NORTH);
		p.add(new MouseOverCheckBoxListComponent(list));
		d.add(p);
		d.pack();
		d.setLocationRelativeTo(null);
		d.setVisible(true);
	}

	public JCheckBox getSelectAllCheckBox()
	{
		return selectAllCheckbox;
	}

}
