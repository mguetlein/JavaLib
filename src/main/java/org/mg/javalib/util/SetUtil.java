package org.mg.javalib.util;

import java.util.Set;

public class SetUtil
{
	public static <T> boolean isSubSet(Set<T> superSet, Set<T> subSet)
	{
		for (T t : subSet)
			if (!superSet.contains(t))
				return false;
		return true;
	}
}
