package gui.property;

import gui.DocumentAdapter;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

public class StringPropertyCompound extends JTextField implements PropertyCompound
{
	StringProperty property;

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
				StringPropertyCompound.this.property.value = getText();
			}
		});
	}
}
