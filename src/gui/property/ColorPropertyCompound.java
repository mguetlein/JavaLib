package gui.property;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;

public class ColorPropertyCompound extends JButton implements PropertyCompound
{
	ColorProperty property;
	boolean update;

	public ColorPropertyCompound(ColorProperty property)
	{
		this.property = property;
		setText("          ");
		setBackground(property.getValue());
		addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (update)
					return;
				update = true;
				Color c = JColorChooser.showDialog(ColorPropertyCompound.this.getTopLevelAncestor(), "Select Color",
						getBackground());
				if (c != null)
				{
					setBackground(c);
					ColorPropertyCompound.this.property.setValue(c);
				}
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
				setBackground(ColorPropertyCompound.this.property.getValue());
				update = false;
			}
		});

	}
}
