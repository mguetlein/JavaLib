package gui.property;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

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
				setSelectedItem(SelectPropertyCompound.this.property.getValue());
				update = false;
			}
		});
	}
}