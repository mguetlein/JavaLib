package gui.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class DoublePropertyCompound extends JSpinner implements PropertyCompound
{
	DoubleProperty property;
	boolean update;

	public DoublePropertyCompound(DoubleProperty property)
	{
		super(new SpinnerNumberModel((double) property.getValue(), 0, Double.MAX_VALUE, 0.01));

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
				DoublePropertyCompound.this.property.setValue((Double) getValue());
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
				setValue(DoublePropertyCompound.this.property.getValue());
				update = false;
			}
		});
	}
}
