package gui.property;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import util.ArrayUtil;
import util.SwingUtil;
import weka.WekaPropertyUtil;
import weka.clusterers.SimpleKMeans;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

public class PropertyPanel extends JPanel
{
	Property properties[];
	PropertyCompound propertyCompounds[];

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
		for (PropertyCompound p : propertyCompounds)
			p.setEnabled(b);
	}

	private void buildLayout()
	{
		DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("pref,5dlu,fill:pref:grow"));
		setLayout(new BorderLayout());

		if (properties != null)
		{
			propertyCompounds = new PropertyCompound[properties.length];
			int i = 0;
			for (Property p : properties)
			{
				if (javaProperties != null)
					p.load(javaProperties);
				propertyCompounds[i] = (PropertyCompound) p.getPropertyCompound();
				builder.append(p.getDisplayName() + ":");
				builder.append((JComponent) propertyCompounds[i++]);
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

		Property[] props = new Property[6];

		props[0] = new StringProperty("Test-Property", "default");
		props[0].setValue("value");
		props[1] = new IntegerProperty("Test-Int-Property", 15);
		props[1].setValue(10);
		props[2] = new FileProperty("A file", null);
		props[3] = new DoubleProperty("Double prop", 0.5);
		props[4] = new DoubleProperty("Double prop small", 0.0001, 0.0, 1.0, 0.00001);
		props[5] = new ColorGradientProperty("Color gradient", new ColorGradient());

		props = ArrayUtil.concat(Property.class, props, WekaPropertyUtil.getProperties(new SimpleKMeans()));

		SwingUtil.showInDialog(new PropertyPanel(props));
	}
}
