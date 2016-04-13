package org.mg.javalib.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class ResourceBundleOwner
{
	private String resourceBundleName;

	private ResourceBundle bundle;

	public ResourceBundleOwner(String resourceBundleName)
	{
		this.resourceBundleName = resourceBundleName;
	}

	private ResourceBundle bundle()
	{
		if (bundle == null)
			bundle = ResourceBundle.getBundle(resourceBundleName);
		//		ResourceBundle.clearCache();
		//		bundle = ResourceBundle.getBundle(resourceBundleName);
		return bundle;
	}

	public String text(String key)
	{
		return bundle().getString(key);
	}

	public String text(String key, Object... params)
	{
		return MessageFormat.format(bundle().getString(key), params);
	}
}
