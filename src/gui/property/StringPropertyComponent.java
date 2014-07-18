package gui.property;

import gui.DocumentAdapter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

public class StringPropertyComponent extends JTextField implements PropertyComponent
{
	StringProperty property;
	boolean update;

	public StringPropertyComponent(StringProperty property)
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
				StringPropertyComponent.this.property.setValue(getText());
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
				setText(StringPropertyComponent.this.property.getValue());
				update = false;
			}
		});
	}
}
