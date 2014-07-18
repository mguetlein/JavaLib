package gui.property;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

import util.ObjectUtil;

public class SelectPropertyComponent extends JComboBox implements PropertyComponent
{
	SelectProperty property;
	boolean update;

	public SelectPropertyComponent(SelectProperty property)
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
						SelectPropertyComponent.this.property.setValue(getSelectedItem());
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

				boolean objectsChanged = false;
				Object o1[] = SelectPropertyComponent.this.property.getValues();
				if (o1.length != SelectPropertyComponent.this.getItemCount())
					objectsChanged = true;
				else
				{
					for (int i = 0; i < o1.length; i++)
						if (!ObjectUtil.equals(o1[i], SelectPropertyComponent.this.getItemAt(i)))
							objectsChanged = true;
				}
				if (objectsChanged)
				{
					SelectPropertyComponent.this.removeAllItems();
					for (int i = 0; i < o1.length; i++)
						SelectPropertyComponent.this.addItem(o1[i]);
				}
				setSelectedItem(SelectPropertyComponent.this.property.getValue());
				update = false;
			}
		});
	}
}