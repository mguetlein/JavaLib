package org.mg.javalib.util;

import java.io.File;
import java.util.HashMap;

public class FileStore
{
	File baseDir;

	public FileStore(String baseDirPath)
	{
		baseDir = new File(baseDirPath);
		if (!baseDir.exists() || !baseDir.isDirectory())
			throw new Error("please create directory '" + baseDirPath + "' first");
	}

	public boolean exists(String hash, String fileExtension)
	{
		return get(hash, fileExtension).exists();
	}

	public File get(String hash, String fileExtension)
	{
		return new File(baseDir.getAbsolutePath() + "/" + hash + fileExtension);
	}

	// public boolean exists(HashMap<String, Object> properties, String fileExtension)
	// {
	// return get(properties, fileExtension).exists();
	// }
	//
	// public File get(HashMap<String, Object> properties, String fileExtension)
	// {
	// return new File(baseDir.getAbsolutePath() + "/"
	// + propsToFilename(properties, fileExtension));
	// }
	//
	// @SuppressWarnings("unchecked")
	// private static String propsToFilename(HashMap<String, Object> properties, String fileExtension)
	// {
	// List<String> keys = new ArrayList<String>(properties.keySet());
	// Collections.sort(keys, null);
	// String s = "";
	// for (String k : keys)
	// {
	// if (properties.get(k) instanceof HashMap<?, ?>)
	// s += k + "#" + propsToFilename((HashMap<String, Object>) properties.get(k), "")
	// + "#_";
	// else
	// s += k + "-" + properties.get(k) + "_";
	// }
	// s = s.substring(0, s.length() - 1);
	// s += fileExtension;
	// return s;
	// }

	public static void main(String args[])
	{
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("bla", 1);
		map.put("Osterhase", "asjklö");
		map.put("123", 1 / 3.0);
		HashMap<String, Object> map2 = new HashMap<String, Object>();
		map2.put("some", "info");
		map2.put("more", "info");
		map.put("infos", map2);

		// System.out.println(propsToFilename(map, ".test"));
	}
}
