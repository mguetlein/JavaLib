package gui.property;

import gui.DocumentAdapter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

public class StringPropertyCompound extends JTextField implements PropertyCompound
{
	StringProperty property;
	boolean update;

	public StringPropertyCompound(StringProperty property)
	{
		super(15);
		this.property = property;
		setText(property.getValue());
		getDocument().addDocumentListener(new DocumentAdapter()
		{
			@Override
			public void update(DocumentEvent e)
			{
				if (update)
					return;
				update = true;
				StringPropertyCompound.this.property.setValue(getText());
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
				setText(StringPropertyCompound.this.property.getValue());
				update = false;
			}
		});
	}
}
