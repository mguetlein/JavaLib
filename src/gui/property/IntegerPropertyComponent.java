package gui.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class IntegerPropertyComponent extends JSpinner implements PropertyComponent
{
	IntegerProperty property;
	boolean update;

	public IntegerPropertyComponent(IntegerProperty property)
	{
		super(new SpinnerNumberModel((int) property.getValue(), (int) property.getMinValue(),
				(int) property.getMaxValue(), 1));
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
				IntegerPropertyComponent.this.property.setValue((Integer) getValue());
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
				setValue(IntegerPropertyComponent.this.property.getValue());
				update = false;
			}
		});
	}
}
