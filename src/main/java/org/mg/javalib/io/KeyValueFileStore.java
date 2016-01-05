package org.mg.javalib.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import org.mg.javalib.util.ArrayUtil;

public class KeyValueFileStore<K, V extends Serializable>
{
	String dir;
	Set<String> files;
	boolean md5Enabled = false;
	boolean compress = false;

	public KeyValueFileStore(String dir, boolean md5Enabled, boolean compress)
	{
		if (!new File(dir).isDirectory())
			throw new IllegalArgumentException("directory not found: " + dir);
		this.dir = dir;
		this.compress = compress;
		files = new HashSet<>(ArrayUtil.toList(new File(dir).list()));
	}

	private String filename(K k)
	{
		if (md5Enabled)
			return DigestUtils.md5Hex(k.toString());
		else
			return k.toString();
	}

	public synchronized boolean contains(K k)
	{
		String name = filename(k);
		if (files.contains(name))
			return true;
		// dynamic update
		if (new File(dir, name).exists())
		{
			files.add(name);
			return true;
		}
		return false;
	}

	public synchronized V get(K k)
	{
		FileInputStream i = null;
		try
		{
			i = new FileInputStream(new File(dir, filename(k)));
			if (compress)
				return SerializationUtilsCompressed.deserialize(i);
			else
				return SerializationUtils.deserialize(i);
		}
		catch (SerializationException ex)
		{
			System.err.println(filename(k));
			throw ex;
		}
		catch (FileNotFoundException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			if (i != null)
				IOUtils.closeQuietly(i);
		}
	}

	public synchronized void store(K k, V v)
	{
		FileOutputStream o = null;
		try
		{
			String name = filename(k);
			new File(dir, name).getParentFile().mkdirs();
			o = new FileOutputStream(new File(dir, name));
			if (compress)
				SerializationUtilsCompressed.serialize(v, o);
			else
				SerializationUtils.serialize(v, o);
			files.add(name);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			if (o != null)
				IOUtils.closeQuietly(o);
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
		if (!contains(k))
			throw new IllegalArgumentException();
		String name = filename(k);
		new File(dir, name).delete();
		files.remove(name);
	}

}
