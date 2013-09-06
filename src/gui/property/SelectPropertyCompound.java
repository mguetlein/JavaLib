package gui.property;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

import util.ObjectUtil;

public class SelectPropertyCompound extends JComboBox implements PropertyCompound
{
	SelectProperty property;
	boolean update;

	public SelectPropertyCompound(SelectProperty property)
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
						SelectPropertyCompound.this.property.setValue(getSelectedItem());
						update = false;
					}
				});
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
				Object o1[] = SelectPropertyCompound.this.property.getValues();
				if (o1.length != SelectPropertyCompound.this.getItemCount())
					objectsChanged = true;
				else
				{
					for (int i = 0; i < o1.length; i++)
						if (!ObjectUtil.equals(o1[i], SelectPropertyCompound.this.getItemAt(i)))
							objectsChanged = true;
				}
				if (objectsChanged)
				{
					SelectPropertyCompound.this.removeAllItems();
					for (int i = 0; i < o1.length; i++)
						SelectPropertyCompound.this.addItem(o1[i]);
				}
				setSelectedItem(SelectPropertyCompound.this.property.getValue());
				update = false;
			}
		});
	}
}