package gui.property;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import util.SwingUtil;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

public class PropertyPanel extends JPanel
{
	Property properties[];
	PropertyComponent propertyComponents[];

	Properties javaProperties;
	String propertyFile;

	JButton defaultButton = new JButton("Restore defaults");

	public PropertyPanel(Property... properties)
	{
		this(properties, null, null);
	}

	public PropertyPanel(Property properties[], Properties javaProperties, String propertyFile)
	{
		this.properties = properties;
		this.javaProperties = javaProperties;
		this.propertyFile = propertyFile;
		buildLayout();
		defaultButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				for (Property property : PropertyPanel.this.properties)
					property.setValue(property.getDefaultValue());
			}
		});
	}

	public Property[] getProperties()
	{
		return properties;
	}

	public PropertyComponent getComponentForProperty(Property p)
	{
		for (int i = 0; i < properties.length; i++)
			if (properties[i] == p)
				return propertyComponents[i];
		return null;
	}

	public void addPropertyChangeListenerToProperties(PropertyChangeListener l)
	{
		for (Property p : properties)
			p.addPropertyChangeListener(l);
	}

	public void store()
	{
		if (javaProperties != null && properties != null && propertyFile != null)
			for (Property p : properties)
				p.store(javaProperties, propertyFile);
	}

	public void setEnabled(boolean b)
	{
		defaultButton.setEnabled(b);
		for (PropertyComponent p : propertyComponents)
			p.setEnabled(b);
	}

	class ColorIcon implements Icon
	{
		Color col;
		int size;

		public ColorIcon(Color col)
		{
			this.col = col;
			size = new JLabel("X").getPreferredSize().height;
		}

		@Override
		public int getIconHeight()
		{
			return size;
		}

		@Override
		public int getIconWidth()
		{
			return size;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y)
		{
			g.setColor(col);
			g.fillRect(0, 0, size, size);
		}
	}

	private void buildLayout()
	{
		DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("pref,5dlu,fill:pref:grow"));
		setLayout(new BorderLayout());

		if (properties != null)
		{
			propertyComponents = new PropertyComponent[properties.length];
			int i = 0;
			for (Property p : properties)
			{
				if (javaProperties != null)
					p.load(javaProperties);
				propertyComponents[i] = (PropertyComponent) p.getPropertyComponent();
				JLabel l = new JLabel(p.getDisplayName() + ":");
				if (p.getHighlightColor() != null)
					l.setIcon(new ColorIcon(p.getHighlightColor()));
				builder.append(l);
				builder.append((JComponent) propertyComponents[i++]);
			}
			if (properties.length > 0)
			{
				builder.nextLine();
				builder.append(ButtonBarFactory.buildRightAlignedBar(defaultButton), 3);
			}
		}
		add(builder.getPanel());
	}

	public static void main(String args[])
	{
		//		Explorer.main(null);

		Property[] props = new Property[8];

		props[0] = new StringProperty("Test-Property", "default");
		props[0].setValue("value");
		props[0].setHighlightColor(Color.CYAN);
		props[1] = new IntegerProperty("Test-Int-Property", 15);
		props[1].setValue(10);
		props[2] = new FileProperty("A file", null);
		props[3] = new DoubleProperty("Double prop", 0.5);
		props[4] = new DoubleProperty("Double prop small", 0.0001, 0.0, 1.0, 0.00001);
		props[5] = new ColorGradientProperty("Color gradient", new ColorGradient());
		props[6] = new ColorProperty("Color", Color.RED);
		props[7] = new SelectProperty("select", new String[] { "a", "b",
				"ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc" }, "a");

		//		props = ArrayUtil.concat(Property.class, props, WekaPropertyUtil.getProperties(new SimpleKMeans()));

		SwingUtil.showInDialog(new PropertyPanel(props));
	}
}
