package gui.property;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComboBox;

public class StringSelectPropertyCompound extends JComboBox implements PropertyCompound
{
	StringSelectProperty property;
	boolean update;

	public StringSelectPropertyCompound(StringSelectProperty property)
	{
		super(property.getValues());
		this.property = property;
		setSelectedItem(property.getValue());
		addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (update)
					return;
				update = true;
				StringSelectPropertyCompound.this.property.setValue((String) getSelectedItem());
				update = false;
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
				setSelectedItem(StringSelectPropertyCompound.this.property.getValue());
				update = false;
			}
		});
	}
}