package gui.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class DoublePropertyCompound extends JSpinner implements PropertyCompound
{
	DoubleProperty property;
	boolean update;

	/**
	 * fix for small values: 
	 * http://stackoverflow.com/questions/8634160/odd-spinnernumbermodel-behavior-in-java
	 */
	@Override
	protected JComponent createEditor(SpinnerModel model)
	{
		if ((Double) ((SpinnerNumberModel) model).getStepSize() < 0.001)
			return new NumberEditor(this, "0.00000");
		else
			return super.createEditor(model);
	}

	public DoublePropertyCompound(DoubleProperty property)
	{
		super(new SpinnerNumberModel((double) property.getValue(), (double) property.getMinValue(),
				(double) property.getMaxValue(), (double) property.getStepWidth()));

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
