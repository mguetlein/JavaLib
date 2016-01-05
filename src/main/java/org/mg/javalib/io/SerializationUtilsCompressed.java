package org.mg.javalib.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.SerializationException;

public class SerializationUtilsCompressed
{
	public static void serialize(Serializable s, File f) throws SerializationException,
			FileNotFoundException
	{
		FileOutputStream o = new FileOutputStream(f);
		try
		{
			serialize(s, o);
		}
		finally
		{
			IOUtils.closeQuietly(o);
		}
	}

	public static void serialize(Serializable s, OutputStream o) throws SerializationException
	{
		try
		{
			ZipOutputStream zipout = new ZipOutputStream(o);
			zipout.putNextEntry(new ZipEntry("entry"));
			ObjectOutputStream out = new ObjectOutputStream(zipout);
			out.writeObject(s);
			out.flush();
			zipout.closeEntry();
			out.close();
		}
		catch (IOException e)
		{
			throw new SerializationException(e);
		}
	}

	public static <T> T deserialize(File f) throws SerializationException, FileNotFoundException
	{
		FileInputStream i = new FileInputStream(f);
		try
		{
			return deserialize(i);
		}
		finally
		{
			IOUtils.closeQuietly(i);
		}
	}

	public static <T> T deserialize(InputStream i) throws SerializationException
	{
		try
		{
			ZipInputStream zipin = new ZipInputStream(i);
			zipin.getNextEntry();
			ObjectInputStream in = new ObjectInputStream(zipin);
			@SuppressWarnings("unchecked")
			T obj = (T) in.readObject();
			in.close();
			return obj;
		}
		catch (Exception e)
		{
			throw new SerializationException(e);
		}
	}

	public static void demo() throws IOException, ClassNotFoundException
	{
		File tmp = new File("/tmp/out2.zip");
		//		String 

		//		//int array[] = ArrayUtil.indexArray(1000000);
		String array = RandomStringUtils.randomAlphabetic(10000000);
		System.out.println(array.substring(0, 100));

		//
		serialize(array, new FileOutputStream(tmp));
		array = (String) deserialize(tmp);
		System.out.println(array.substring(0, 100));

		//		System.out.println("kb uncompressed: " + (tmp.length() / 1024));
		//		//		serialize(array, new FileOutputStream(tmp));
		//		//		System.out.println("kb uncompressed: " + (tmp.length() / 1024));
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException
	{
		demo();
	}
}
