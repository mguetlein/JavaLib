package org.mg.javalib.gui.property;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.SwingUtilities;

import org.mg.javalib.gui.BoundsPopupMenuListener;
import org.mg.javalib.util.ObjectUtil;

public class SelectPropertyComponent extends JComboBox implements PropertyComponent
{
	SelectProperty property;
	boolean update;

	@SuppressWarnings("unchecked")
	public SelectPropertyComponent(SelectProperty property)
	{
		super(property.getValues());
		this.property = property;
		setSelectedItem(property.getValue());
		addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						if (update)
							return;
						update = true;
						SelectPropertyComponent.this.property.setValue(getSelectedItem());
						update = false;
					}
				});
			}
		});

		setRenderer(new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index,
					boolean isSelected, boolean cellHasFocus)
			{
				value = SelectPropertyComponent.this.property.getRenderValue(value);
				return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
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

				boolean objectsChanged = false;
				Object o1[] = SelectPropertyComponent.this.property.getValues();
				if (o1.length != SelectPropertyComponent.this.getItemCount())
					objectsChanged = true;
				else
				{
					for (int i = 0; i < o1.length; i++)
						if (!ObjectUtil.equals(o1[i], SelectPropertyComponent.this.getItemAt(i)))
							objectsChanged = true;
				}
				if (objectsChanged)
				{
					SelectPropertyComponent.this.removeAllItems();
					for (int i = 0; i < o1.length; i++)
						SelectPropertyComponent.this.addItem(o1[i]);
				}
				setSelectedItem(SelectPropertyComponent.this.property.getValue());
				update = false;
			}
		});

		BoundsPopupMenuListener listener = new BoundsPopupMenuListener(true, false);
		this.addPopupMenuListener(listener);
	}

	@Override
	public Dimension getPreferredSize()
	{
		Dimension d = super.getPreferredSize();
		return new Dimension(Math.min(200, d.width), d.height);
	}
}
