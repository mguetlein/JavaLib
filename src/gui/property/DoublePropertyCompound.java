package gui.property;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class DoublePropertyCompound extends JSpinner implements PropertyCompound
{
	DoubleProperty property;

	public DoublePropertyCompound(DoubleProperty property)
	{
		super(new SpinnerNumberModel((double) property.value, 0, 1, 0.01));

		((JSpinner.DefaultEditor) getEditor()).getTextField().setColumns(10);

		this.property = property;

		addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				DoublePropertyCompound.this.property.value = (Double) getValue();
			}
		});
	}
}
