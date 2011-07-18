package gui.property;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class IntegerPropertyCompound extends JSpinner implements PropertyCompound
{
	IntegerProperty property;

	public IntegerPropertyCompound(IntegerProperty property)
	{
		super(new SpinnerNumberModel((int) property.value, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));

		((JSpinner.DefaultEditor) getEditor()).getTextField().setColumns(10);

		this.property = property;

		addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				IntegerPropertyCompound.this.property.value = (Integer) getValue();
			}
		});
	}
}
