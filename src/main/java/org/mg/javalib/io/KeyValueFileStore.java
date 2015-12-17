package org.mg.javalib.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.SerializationUtils;
import org.mg.javalib.util.ArrayUtil;

public class KeyValueFileStore<K, V extends Serializable>
{
	String dir;
	Set<String> files;

	public KeyValueFileStore(String dir)
	{
		if (!new File(dir).isDirectory())
			throw new IllegalArgumentException("directory not found: " + dir);
		this.dir = dir;
		files = new HashSet<>(ArrayUtil.toList(new File(dir).list()));
	}

	private String filename(K k)
	{
		return Integer.toString(k.hashCode());
	}

	public synchronized boolean contains(K k)
	{
		return files.contains(filename(k));
	}

	public synchronized V get(K k)
	{
		try
		{
			return SerializationUtils.deserialize(new FileInputStream(new File(dir, filename(k))));
		}
		catch (FileNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	public synchronized void store(K k, V v)
	{
		try
		{
			if (files.contains(k))
				throw new IllegalArgumentException();
			SerializationUtils.serialize(v, new FileOutputStream(new File(dir, filename(k))));
			files.add(filename(k));
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public void clear()
	{
		for (String f : files)
			new File(dir, f).delete();
		files.clear();
	}

	public void clear(K k)
	{
		if (!files.contains(filename(k)))
			throw new IllegalArgumentException();
		new File(dir, filename(k)).delete();
		files.remove(filename(k));
	}

}
