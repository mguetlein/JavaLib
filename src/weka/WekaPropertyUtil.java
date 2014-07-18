package weka;

import gui.property.BooleanProperty;
import gui.property.DoubleProperty;
import gui.property.FileProperty;
import gui.property.IntegerProperty;
import gui.property.Property;
import gui.property.SelectedTagProperty;
import gui.property.WekaProperty;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import util.ArrayUtil;
import weka.clusterers.HierarchicalClusterer;
import weka.core.OptionHandler;
import weka.core.SelectedTag;
import weka.gui.GenericObjectEditor;

public class WekaPropertyUtil
{

	static HashMap<Class<?>, List<PropertyDescriptor>> map = new HashMap<Class<?>, List<PropertyDescriptor>>();

	private static List<PropertyDescriptor> getDescriptors(Object wekaAlgorithm)
	{
		if (!map.containsKey(wekaAlgorithm.getClass()))
		{
			try
			{
				List<PropertyDescriptor> desc = new ArrayList<PropertyDescriptor>();
				BeanInfo bi = Introspector.getBeanInfo(wekaAlgorithm.getClass());
				for (PropertyDescriptor propertyDescriptor : bi.getPropertyDescriptors())
				{
					if (propertyDescriptor.isHidden() || propertyDescriptor.isExpert())
						continue;
					String name = propertyDescriptor.getDisplayName();
					if (name.equals("options"))
						continue;
					Method getter = propertyDescriptor.getReadMethod();
					Method setter = propertyDescriptor.getWriteMethod();
					if (getter == null || setter == null)
						continue;

					//					System.out.println("Property of " + wekaAlgorithm.getClass() + ":"
					//							+ propertyDescriptor.getShortDescription());
					desc.add(propertyDescriptor);
				}
				map.put(wekaAlgorithm.getClass(), desc);
			}
			catch (IntrospectionException e)
			{
				e.printStackTrace();
			}
		}
		return map.get(wekaAlgorithm.getClass());
	}

	//	public static HashMap<String, Object> defaults = new HashMap<String, Object>();

	//	private static Object getDefault(Object wekaAlgorithm, Method getter)
	//	{
	//		String key = wekaAlgorithm.getClass().getName() + "_" + getter.getName();
	//		if (!defaults.containsKey(key))
	//		{
	//			try
	//			{
	//				defaults.put(key, getter.invoke(wekaAlgorithm, (Object[]) null));
	//			}
	//			catch (IllegalArgumentException e)
	//			{
	//				e.printStackTrace();
	//			}
	//			catch (IllegalAccessException e)
	//			{
	//				e.printStackTrace();
	//			}
	//			catch (InvocationTargetException e)
	//			{
	//				e.printStackTrace();
	//			}
	//		}
	//		return defaults.get(key);
	//	}

	public static Property[] getProperties(Object wekaAlgorithm)
	{
		return getProperties(wekaAlgorithm, new String[0]);
	}

	public static interface DefaultChanger
	{
		public String getName();

		public Object getAlternateDefaultValue();
	}

	public static Property[] getProperties(Object wekaAlgorithm, String[] skipProperties)
	{
		return getProperties(wekaAlgorithm, skipProperties, null);
	}

	public static Property[] getProperties(Object wekaAlgorithm, String[] skipProperties, DefaultChanger changer)
	{
		List<Property> props = new ArrayList<Property>();
		try
		{
			for (PropertyDescriptor propertyDescriptor : getDescriptors(wekaAlgorithm))
			{
				Class<?> type = propertyDescriptor.getPropertyType();
				String name = propertyDescriptor.getDisplayName();
				String uniqueName = wekaAlgorithm.getClass().getSimpleName() + "_" + name;

				if (ArrayUtil.indexOf(skipProperties, name) != -1)
					continue;

				Object defaultValue = null;
				if (changer != null && name.equals(changer.getName()))
					defaultValue = changer.getAlternateDefaultValue();

				Method getter = propertyDescriptor.getReadMethod();
				if (type == boolean.class)
					props.add(new BooleanProperty(name, uniqueName, (Boolean) (defaultValue != null ? defaultValue
							: getter.invoke(wekaAlgorithm, (Object[]) null))));
				else if (type == int.class)
					props.add(new IntegerProperty(name, uniqueName, (Integer) (defaultValue != null ? defaultValue
							: getter.invoke(wekaAlgorithm, (Object[]) null))));
				else if (type == double.class)
					props.add(new DoubleProperty(name, uniqueName, (Double) (defaultValue != null ? defaultValue
							: getter.invoke(wekaAlgorithm, (Object[]) null))));
				else if (OptionHandler.class.isAssignableFrom(type))
					props.add(new WekaProperty(name, uniqueName, (OptionHandler) (defaultValue != null ? defaultValue
							: getter.invoke(wekaAlgorithm, (Object[]) null))));
				else if (type == SelectedTag.class)
					props.add(new SelectedTagProperty(name, uniqueName,
							(SelectedTag) (defaultValue != null ? defaultValue : getter.invoke(wekaAlgorithm,
									(Object[]) null))));
				else if (type == File.class)
					props.add(new FileProperty(name, uniqueName, (File) (defaultValue != null ? defaultValue : getter
							.invoke(wekaAlgorithm, (Object[]) null))));
				else
				{
					throw new Error("unknown type in algorithm '" + wekaAlgorithm.getClass().getName() + "' : " + type);
				}
			}
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}
		Property p[] = new Property[props.size()];
		return props.toArray(p);
	}

	public static void setProperties(Object wekaAlgorithm, Property[] properties)
	{
		setProperties(wekaAlgorithm, properties, false);
	}

	public static void setProperties(Object wekaAlgorithm, Property[] properties, boolean setDefaultProps)
	{
		try
		{
			for (Property p : properties)
			{
				PropertyDescriptor desc = null;
				for (PropertyDescriptor propertyDescriptor : getDescriptors(wekaAlgorithm))
					if (p.getName().equals(propertyDescriptor.getDisplayName()))
					{
						desc = propertyDescriptor;
						break;
					}
				if (desc == null)
					throw new Error("no write method for property: " + p.getName() + " (alg: "
							+ wekaAlgorithm.getClass().getName() + ")");
				Method setter = desc.getWriteMethod();
				setter.invoke(wekaAlgorithm, setDefaultProps ? p.getDefaultValue() : p.getValue());
			}
		}
		catch (IllegalArgumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String args[])
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				new GenericObjectEditor();
			}
		}).start();

		for (Property p : WekaPropertyUtil.getProperties(new HierarchicalClusterer()))
		{
			System.out.println(p);
			if (p instanceof WekaProperty)
			{
				p.getPropertyComponent();
			}
		}
	}

	//	public static void main(String args[])
	//	{
	//		Class type = weka.core.DistanceFunction.class;
	//		System.out.println(type == DistanceFunction.class);
	//		System.out.println(OptionHandler.class.isAssignableFrom(type));
	//	}

	public static void initWekaStuff()
	{
		new GenericObjectEditor();
	}
}
