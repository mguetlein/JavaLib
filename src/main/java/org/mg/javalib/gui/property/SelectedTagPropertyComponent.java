package org.mg.javalib.gui.property;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;

import weka.core.SelectedTag;
import weka.core.Tag;

public class SelectedTagPropertyComponent extends JComboBox implements PropertyComponent
{
	SelectedTagProperty property;
	boolean update;

	public SelectedTagPropertyComponent(SelectedTagProperty property)
	{
		super(property.getValue().getTags());
		setSelectedItem(property.getValue().getSelectedTag());

		setRenderer(new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
					boolean cellHasFocus)
			{
				return super.getListCellRendererComponent(list, ((Tag) value).getReadable(), index, isSelected,
						cellHasFocus);
			}
		});

		addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (getSelectedItem() != SelectedTagPropertyComponent.this.property.getValue().getSelectedTag())
				{
					if (update)
						return;
					update = true;
					SelectedTagPropertyComponent.this.property.setValue(new SelectedTag(getSelectedIndex(),
							SelectedTagPropertyComponent.this.property.getValue().getTags()));
					update = false;
				}
			}
		});

		property.addPropertyChangeListener(new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				if (update)
					return;
				update = true;
				setSelectedItem(SelectedTagPropertyComponent.this.property.getValue().getSelectedTag());
				update = false;
			}
		});

		this.property = property;

	}
}