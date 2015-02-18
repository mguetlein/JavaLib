package org.mg.javalib.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.mg.javalib.gui.property.BooleanProperty;
import org.mg.javalib.gui.property.IntegerProperty;
import org.mg.javalib.gui.property.Property;
import org.mg.javalib.gui.property.PropertyPanel;
import org.mg.javalib.util.SwingUtil;

import com.jgoodies.forms.factories.ButtonBarFactory;

public class ResolutionPanel extends JPanel
{
	IntegerProperty width;
	IntegerProperty height;
	BooleanProperty aspectRatio = new BooleanProperty("Aspect ratio", null, true);

	public ResolutionPanel(int w, int h)
	{
		width = new IntegerProperty("Width", null, w);
		height = new IntegerProperty("Height", null, h);
		setLayout(new BorderLayout());
		add(new PropertyPanel(new Property[] { width, height, aspectRatio }));
		final double ratio = width.getValue() / (double) height.getValue();
		PropertyChangeListener l = new PropertyChangeListener()
		{
			boolean selfUpdate = false;

			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				if (selfUpdate)
					return;
				selfUpdate = true;
				if (aspectRatio.getValue().booleanValue())
				{
					if (evt.getSource() == width)
						height.setValue((int) Math.round(width.getValue() / ratio));
					else
						width.setValue((int) Math.round(height.getValue() * ratio));
				}
				selfUpdate = false;
			}
		};
		width.addPropertyChangeListener(l);
		height.addPropertyChangeListener(l);
	}

	public static Dimension getResuloution(JFrame owner, String title, int w, int h)
	{
		ResolutionPanel pr = new ResolutionPanel(w, h);
		final JDialog d = new JDialog(owner, title);
		if (owner != null)
			d.setModal(true);
		final JButton ok = new JButton("Ok");
		JButton cancel = new JButton("Cancel");
		final StringBuffer okPressed = new StringBuffer("");
		ActionListener al = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (e.getSource() == ok)
					okPressed.append("true");
				d.setVisible(false);
			}
		};
		ok.addActionListener(al);
		cancel.addActionListener(al);
		JPanel p = new JPanel(new BorderLayout(10, 10));
		p.add(pr, BorderLayout.NORTH);
		p.add(ButtonBarFactory.buildOKCancelBar(ok, cancel), BorderLayout.SOUTH);
		p.setBorder(new EmptyBorder(10, 10, 10, 10));
		d.getContentPane().add(p);
		d.pack();
		d.setLocationRelativeTo(owner);
		d.setVisible(true);
		if (!d.isModal())
			SwingUtil.waitWhileVisible(d);
		if (okPressed.toString().equals("true"))
			return new Dimension(pr.width.getValue(), pr.height.getValue());
		else
			return null;
	}

	public static void main(String args[])
	{
		System.out.println(ResolutionPanel.getResuloution(null, "Select resolution", 1024, 768));
		System.exit(0);
	}

}
