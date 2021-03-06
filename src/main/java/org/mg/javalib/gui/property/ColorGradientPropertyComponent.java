package org.mg.javalib.gui.property;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JPanel;

import org.mg.javalib.util.ColorUtil;
import org.mg.javalib.util.SwingUtil;

import com.jgoodies.forms.factories.ButtonBarFactory;

public class ColorGradientPropertyComponent extends JPanel implements PropertyComponent
{
	ColorGradientProperty property;

	JButton high;
	JButton med;
	JButton low;
	JButton reverse;

	public ColorGradientPropertyComponent(ColorGradientProperty property)
	{
		super();
		this.property = property;

		property.addPropertyChangeListener(new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				update();
			}
		});

		high = new JButton("high");
		med = new JButton("medium");
		low = new JButton("low");
		update();
		ActionListener l = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Color c = JColorChooser.showDialog(ColorGradientPropertyComponent.this.getTopLevelAncestor(),
						"Select Color", ((JButton) e.getSource()).getBackground());
				if (c != null && !((JButton) e.getSource()).getBackground().equals(c))
				{
					((JButton) e.getSource()).setBackground(c);
					setValue(new ColorGradient(high.getBackground(), med.getBackground(), low.getBackground()));
				}
			}
		};
		high.addActionListener(l);
		med.addActionListener(l);
		low.addActionListener(l);

		reverse = new JButton("<html>&#8596;</html>");
		reverse.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				ColorGradientPropertyComponent.this.property.reverse();
			}
		});

		setLayout(new BorderLayout(10, 10));
		add(ButtonBarFactory.buildCenteredBar(low, med, high));
		add(reverse, BorderLayout.EAST);
	}

	private void update()
	{
		high.setBackground(property.getValue().getHigh());
		high.setForeground(ColorUtil.getForegroundColor(high.getBackground()));
		med.setBackground(property.getValue().getMed());
		med.setForeground(ColorUtil.getForegroundColor(med.getBackground()));
		low.setBackground(property.getValue().getLow());
		low.setForeground(ColorUtil.getForegroundColor(low.getBackground()));
	}

	public void setValue(ColorGradient val)
	{
		ColorGradient old = property.getValue();
		if (!old.equals(val))
		{
			property.setValue(val);
			firePropertyChange("prop changed", old, val);
		}
	}

	public static void main(String[] args)
	{
		SwingUtil
				.showInDialog(new ColorGradientPropertyComponent(new ColorGradientProperty("test", new ColorGradient())));
	}

}
