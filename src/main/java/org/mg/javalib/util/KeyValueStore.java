package org.mg.javalib.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

public abstract class KeyValueStore<T, K>
{
	String path;
	int lastStoredAt = 0;
	public static boolean suppressCaching = true;
	HashMap<T, K> products = new HashMap<T, K>();

	@SuppressWarnings("unchecked")
	public KeyValueStore(String path)
	{
		this.path = path;
		if (!suppressCaching)
			try
			{
				FileInputStream fin = new FileInputStream(path);
				ObjectInputStream ois = new ObjectInputStream(fin);
				products = (HashMap<T, K>) ois.readObject();
				ois.close();
				lastStoredAt = products.size();
				System.out.println("init cache with " + lastStoredAt + " signatures");
			}
			catch (Exception e)
			{
				System.err.println("could not read cached products");
			}

		if (products == null)
			products = new HashMap<T, K>();
	}

	public void store()
	{
		if (!suppressCaching)
			try
			{
				File tmp = File.createTempFile("storing", "products");
				System.out.println("storing products " + products.size());
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tmp));
				oos.writeObject(products);
				oos.close();
				FileUtils.deleteQuietly(new File(path));
				FileUtils.moveFile(tmp, new File(path));
				lastStoredAt = products.size();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
	}

	public K getValue(T key)
	{
		if (!products.containsKey(key))
		{
			products.put(key, getValue(key));
			if (products.size() - lastStoredAt >= 200)
				store();
		}
		return products.get(key);
	}

	public abstract K computeValue(T key);
}
