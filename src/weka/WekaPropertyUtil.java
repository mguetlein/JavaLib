package weka;

import gui.property.BooleanProperty;
import gui.property.DoubleProperty;
import gui.property.IntegerProperty;
import gui.property.Property;
import gui.property.SelectedTagProperty;
import gui.property.WekaProperty;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import weka.core.OptionHandler;
import weka.core.SelectedTag;

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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Property[] getProperties(Object wekaAlgorithm)
	{
		List<Property> props = new ArrayList<Property>();
		try
		{
			for (PropertyDescriptor propertyDescriptor : getDescriptors(wekaAlgorithm))
			{
				Class<?> type = propertyDescriptor.getPropertyType();
				String name = propertyDescriptor.getDisplayName();
				Method getter = propertyDescriptor.getReadMethod();
				if (type == boolean.class)
					props.add(new BooleanProperty(name, (Boolean) getter.invoke(wekaAlgorithm, (Object[]) null)));
				else if (type == int.class)
					props.add(new IntegerProperty(name, (Integer) getter.invoke(wekaAlgorithm, (Object[]) null)));
				else if (type == double.class)
					props.add(new DoubleProperty(name, (Double) getter.invoke(wekaAlgorithm, (Object[]) null)));
				else if (OptionHandler.class.isAssignableFrom(type))
					props.add(new WekaProperty(name, (OptionHandler) getter.invoke(wekaAlgorithm, (Object[]) null)));
				else if (type == SelectedTag.class)
					props.add(new SelectedTagProperty(name, (SelectedTag) getter.invoke(wekaAlgorithm, (Object[]) null)));
				else
					throw new Error("unknown type: " + type);
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
				Method setter = desc.getWriteMethod();
				setter.invoke(wekaAlgorithm, p.getValue());
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

	//	public static void main(String args[])
	//	{
	//		Class type = weka.core.DistanceFunction.class;
	//		System.out.println(type == DistanceFunction.class);
	//		System.out.println(OptionHandler.class.isAssignableFrom(type));
	//	}
}
