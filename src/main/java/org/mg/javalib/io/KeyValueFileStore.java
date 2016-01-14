package org.mg.javalib.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.channels.FileLock;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;

public class KeyValueFileStore<K, V extends Serializable>
{
	String dir;
	Set<String> files;
	boolean md5Enabled = false;
	boolean compress = false;
	String tmpDir = null;
	boolean permanent = false;

	public KeyValueFileStore(String dir, boolean md5Enabled, boolean compress, String tmpDir,
			boolean permanent)
	{
		//		if (!new File(dir).isDirectory())
		//			throw new IllegalArgumentException("directory not found: " + dir);
		this.dir = dir;
		this.compress = compress;
		this.tmpDir = tmpDir;
		this.permanent = permanent;
		if (permanent)
			files = new HashSet<>();
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
		if (permanent && files.contains(name))
			return true;
		// dynamic update
		if (new File(dir, name).exists())
		{
			if (permanent)
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
		RandomAccessFile raf = null;
		FileLock lock = null;
		try
		{
			String name = filename(k);
			String directory = dir;
			if (tmpDir != null)
				directory = tmpDir;

			File destFile = new File(directory, name);
			destFile.getParentFile().mkdirs();
			raf = new RandomAccessFile(new File(directory, name), "rw");
			lock = raf.getChannel().tryLock();
			if (lock == null)
				throw new RuntimeException("already locked " + destFile);
			o = new FileOutputStream(destFile);
			if (compress)
				SerializationUtilsCompressed.serialize(v, o);
			else
				SerializationUtils.serialize(v, o);

			if (tmpDir != null)
			{
				File realDestFile = new File(dir, name);
				realDestFile.getParentFile().mkdirs();
				FileUtils.moveFile(destFile, realDestFile);
			}
			if (permanent)
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
			if (lock != null)
				try
				{
					lock.release();
				}
				catch (IOException e)
				{
					throw new RuntimeException(e);
				}
			if (raf != null)
				IOUtils.closeQuietly(raf);
		}
	}

	public void clear()
	{
		if (permanent)
			throw new IllegalArgumentException();
		for (String f : files)
			new File(dir, f).delete();
	}

	public void clear(K k)
	{
		if (permanent)
			throw new IllegalArgumentException();
		if (!contains(k))
			throw new IllegalArgumentException();
		String name = filename(k);
		new File(dir, name).delete();
	}

}
