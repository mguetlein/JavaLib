package org.mg.javalib.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class ResourceBundleOwner
{
	private String resourceBundleName;

	private boolean reload;

	private ResourceBundle bundle;

	public ResourceBundleOwner(String resourceBundleName)
	{
		this(resourceBundleName, false);
	}

	public ResourceBundleOwner(String resourceBundleName, boolean reload)
	{
		this.resourceBundleName = resourceBundleName;
		this.reload = reload;
	}

	private ResourceBundle bundle()
	{
		if (reload)
		{
			ResourceBundle.clearCache();
			return ResourceBundle.getBundle(resourceBundleName);
		}
		else
		{
			if (bundle == null)
				bundle = ResourceBundle.getBundle(resourceBundleName);
			return bundle;
		}
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
