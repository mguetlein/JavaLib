package gui.property;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

public class BooleanPropertyCompound extends JCheckBox implements PropertyCompound
{
	BooleanProperty property;

	public BooleanPropertyCompound(BooleanProperty property)
	{
		this.property = property;
		setSelected(property.getValue());
		addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				BooleanPropertyCompound.this.property.value = isSelected();
			}
		});

	}
}
