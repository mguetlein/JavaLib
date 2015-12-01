package org.mg.javalib.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;

public class FileHashMap<K, V> implements Serializable
{
	private static final long serialVersionUID = 1L;

	HashMap<K, V> map;
	String file;
	int storeEach;

	public FileHashMap(String file, int storeEach)
	{
		this.file = file;
		this.storeEach = storeEach;
		if (new File(file).exists())
			map = getFromFile(file);
		else
			map = new HashMap<>();
	}

	public V put(K k, V v)
	{
		V vOld = map.put(k, v);
		if (map.size() % storeEach == 0)
			storeToFile(map, file);
		return vOld;
	}

	public static <K, V> void storeToFile(HashMap<K, V> map, String file)
	{
		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(map);
			oos.close();
			System.err.println("stored map (#" + map.size() + ") in file");
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <K, V> HashMap<K, V> getFromFile(String file)
	{
		ObjectInputStream ois = null;
		try
		{
			FileInputStream fin = new FileInputStream(file);
			ois = new ObjectInputStream(fin);
			HashMap<K, V> map = (HashMap<K, V>) ois.readObject();
			System.err.println("read map (#" + map.size() + ") from file");
			return map;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			IOUtils.closeQuietly(ois);
		}
	}

	public boolean containsKey(K k)
	{
		return map.containsKey(k);
	}

	public V get(K k)
	{
		return map.get(k);
	}
}
