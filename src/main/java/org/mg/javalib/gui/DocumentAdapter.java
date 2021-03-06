package org.mg.javalib.gui;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public abstract class DocumentAdapter implements DocumentListener
{

	@Override
	public void changedUpdate(DocumentEvent e)
	{
		update(e);
	}

	@Override
	public void insertUpdate(DocumentEvent e)
	{
		update(e);
	}

	@Override
	public void removeUpdate(DocumentEvent e)
	{
		update(e);
	}

	public abstract void update(DocumentEvent e);
}
