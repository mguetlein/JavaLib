package gui.property;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;

import weka.gui.GenericObjectEditor;

public class WekaPropertyCompound extends JPanel implements PropertyCompound
{
	WekaProperty property;
	GenericObjectEditor editor;

	public WekaPropertyCompound(WekaProperty property)
	{
		this.property = property;
		editor = new GenericObjectEditor();
		editor.setClassType(property.getValue().getClass());
		editor.setValue(property.getValue());

		setLayout(new BorderLayout());
		add(editor.getCustomPanel());

		addPropertyChangeListener(new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{

			}
		});

	}
}