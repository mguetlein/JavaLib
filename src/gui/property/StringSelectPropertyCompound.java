package gui.property;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

public class StringSelectPropertyCompound extends JComboBox implements PropertyCompound
{
	StringSelectProperty property;

	public StringSelectPropertyCompound(StringSelectProperty property)
	{
		super(property.values);
		this.property = property;
		setSelectedItem(property.getValue());
		addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				StringSelectPropertyCompound.this.property.value = (String) getSelectedItem();
			}
		});
	}
}