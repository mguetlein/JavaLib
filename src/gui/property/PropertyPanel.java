package gui.property;

import java.awt.BorderLayout;
import java.util.Properties;

import javax.swing.JPanel;

import util.SwingUtil;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class PropertyPanel extends JPanel
{
	Property properties[];

	Properties javaProperties;
	String propertyFile;

	public PropertyPanel(Property properties[])
	{
		this(properties, null, null);
	}

	public PropertyPanel(Property properties[], Properties javaProperties, String propertyFile)
	{
		this.properties = properties;
		this.javaProperties = javaProperties;
		this.propertyFile = propertyFile;

		buildLayout();
	}

	public Property[] getProperties()
	{
		return properties;
	}

	public void store()
	{
		if (javaProperties != null && properties != null)
			for (Property p : properties)
				p.store(javaProperties, propertyFile);
	}

	private void buildLayout()
	{
		DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("pref,5dlu,fill:pref:grow"));
		setLayout(new BorderLayout());

		if (properties != null)
			for (Property p : properties)
			{
				if (javaProperties != null)
					p.load(javaProperties);
				builder.append(p.getName() + ":");
				builder.append(p.getPropertyCompound());
			}

		add(builder.getPanel());
	}

	public static void main(String args[])
	{
		Property[] props = new Property[2];

		props[0] = new StringProperty("Test-Property", "default");
		props[1] = new IntegerProperty("Test-Int-Property", 15);

		SwingUtil.showInDialog(new PropertyPanel(props));
	}
}
