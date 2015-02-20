package org.mg.javalib.gui.property;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class FilePropertyComponent extends JPanel implements PropertyComponent
{
	FileProperty property;
	JTextField textField;
	JButton button;
	JFileChooser fileChooser;

	public FilePropertyComponent(FileProperty property)
	{
		this.property = property;
		textField = new JTextField();
		textField.setColumns(15);
		textField.setEditable(false);
		textField.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				selectFile();
			}
		});
		updateTextField();

		button = new JButton("...");
		button.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				selectFile();
			}
		});

		setLayout(new BorderLayout(5, 5));
		add(textField);
		add(button, BorderLayout.EAST);

		property.addPropertyChangeListener(new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				updateTextField();
			}
		});
	}

	private void updateTextField()
	{
		textField.setText(FilePropertyComponent.this.property.getValue() == null ? ""
				: FilePropertyComponent.this.property.getValue().getAbsolutePath());
	}

	private void selectFile()
	{
		if (fileChooser == null)
		{
			fileChooser = new JFileChooser();
		}
		fileChooser.showOpenDialog(null);
		File f = fileChooser.getSelectedFile();
		if (f != null)
		{
			FilePropertyComponent.this.property.setValue(f);
			updateTextField();
		}
	}
}
