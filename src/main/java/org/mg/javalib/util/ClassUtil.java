package org.mg.javalib.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ClassUtil
{

	/**
	* Scans all classes accessible from the context class loader which belong to the given package and subpackages.
	*
	* @param packageName The base package
	* @return The classes
	* @throws ClassNotFoundException
	* @throws IOException
	*/
	@SuppressWarnings("unchecked")
	public static List<Class> getClasses(String packageName) //throws ClassNotFoundException, IOException
	{
		try
		{
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			assert classLoader != null;
			String path = packageName.replace('.', '/');
			Enumeration<URL> resources = classLoader.getResources(path);
			List<File> dirs = new ArrayList<File>();
			while (resources.hasMoreElements())
			{
				URL resource = resources.nextElement();
				String fileName = resource.getFile();
				String fileNameDecoded = URLDecoder.decode(fileName, "UTF-8");
				dirs.add(new File(fileNameDecoded));
			}
			ArrayList<Class> classes = new ArrayList<Class>();
			for (File directory : dirs)
			{
				classes.addAll(findClasses(directory, packageName));
			}
			return classes;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 *
	 * @param directory   The base directory
	 * @param packageName The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException
	{
		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists())
		{
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files)
		{
			String fileName = file.getName();
			if (file.isDirectory())
			{
				assert !fileName.contains(".");
				classes.addAll(findClasses(file, packageName + "." + fileName));
			}
			else if (fileName.endsWith(".class") && !fileName.contains("$"))
			{
				Class _class;
				try
				{
					_class = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6));
				}
				catch (ExceptionInInitializerError e)
				{
					// happen, for example, in classes, which depend on 
					// Spring to inject some beans, and which fail, 
					// if dependency is not fulfilled
					_class = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6), false,
							Thread.currentThread().getContextClassLoader());
				}
				classes.add(_class);
			}
		}
		return classes;
	}
	//
	//	
	//	/**
	//	 * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
	//	 *
	//	 * @param packageName The base package
	//	 * @return The classes
	//	 * @throws ClassNotFoundException
	//	 * @throws IOException
	//	 */
	//	@SuppressWarnings("rawtypes")
	//	public static Class<?>[] getClasses(String packageName)
	//	{
	//		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	//		assert classLoader != null;
	//		String path = packageName.replace('.', '/');
	//		Enumeration<URL> resources = null;
	//		try
	//		{
	//			resources = classLoader.getResources(path);
	//		}
	//		catch (IOException e)
	//		{
	//			e.printStackTrace();
	//		}
	//		List<File> dirs = new ArrayList<File>();
	//		while (resources.hasMoreElements())
	//		{
	//			URL resource = resources.nextElement();
	//			dirs.add(new File(resource.getFile()));
	//		}
	//		ArrayList<Class> classes = new ArrayList<Class>();
	//		for (File directory : dirs)
	//		{
	//			try
	//			{
	//				classes.addAll(findClasses(directory, packageName));
	//			}
	//			catch (ClassNotFoundException e)
	//			{
	//				e.printStackTrace();
	//			}
	//		}
	//		return classes.toArray(new Class[classes.size()]);
	//	}
	//
	//	/**
	//	 * Recursive method used to find all classes in a given directory and subdirs.
	//	 *
	//	 * @param directory   The base directory
	//	 * @param packageName The package name for classes found inside the base directory
	//	 * @return The classes
	//	 * @throws ClassNotFoundException
	//	 */
	//	private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException
	//	{
	//		List<Class<?>> classes = new ArrayList<Class<?>>();
	//		if (!directory.exists())
	//		{
	//			return classes;
	//		}
	//		File[] files = directory.listFiles();
	//		for (File file : files)
	//		{
	//			if (file.isDirectory())
	//			{
	//				assert !file.getName().contains(".");
	//				classes.addAll(findClasses(file, packageName + "." + file.getName()));
	//			}
	//			else if (file.getName().endsWith(".class"))
	//			{
	//				classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
	//			}
	//		}
	//		return classes;
	//	}

}
