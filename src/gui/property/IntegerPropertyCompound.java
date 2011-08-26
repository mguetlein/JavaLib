package gui.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class IntegerPropertyCompound extends JSpinner implements PropertyCompound
{
	IntegerProperty property;
	boolean update;

	public IntegerPropertyCompound(IntegerProperty property)
	{
		super(new SpinnerNumberModel((int) property.getValue(), Integer.MIN_VALUE, Integer.MAX_VALUE, 1));

		((JSpinner.DefaultEditor) getEditor()).getTextField().setColumns(10);

		this.property = property;

		addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (update)
					return;
				update = true;
				IntegerPropertyCompound.this.property.setValue((Integer) getValue());
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
				setValue(IntegerPropertyCompound.this.property.getValue());
				update = false;
			}
		});
	}
}
