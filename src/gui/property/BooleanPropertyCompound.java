package gui.property;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;

public class BooleanPropertyCompound extends JCheckBox implements PropertyCompound
{
	BooleanProperty property;
	boolean update;

	public BooleanPropertyCompound(BooleanProperty property)
	{
		this.property = property;
		setSelected(property.getValue());
		addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (update)
					return;
				update = true;
				BooleanPropertyCompound.this.property.setValue(isSelected());
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
				setSelected(BooleanPropertyCompound.this.property.getValue());
				update = false;
			}
		});

	}
}
