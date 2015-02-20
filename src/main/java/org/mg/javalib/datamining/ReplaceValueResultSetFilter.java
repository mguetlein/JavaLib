package org.mg.javalib.datamining;

import java.util.HashMap;

public class ReplaceValueResultSetFilter implements ResultSetFilter
{
	HashMap<String, HashMap<Object, Object>> replace;

	public ReplaceValueResultSetFilter()
	{
		replace = new HashMap<String, HashMap<Object, Object>>();
	}

	public void replace(String property, Object searchValue, Object replaceValue)
	{
		if (replace.containsKey(property))
			replace.get(property).put(searchValue, replaceValue);
		else
		{
			HashMap<Object, Object> map = new HashMap<Object, Object>();
			map.put(searchValue, replaceValue);
			replace.put(property, map);
		}
	}

	@Override
	public boolean accept(Result result)
	{
		for (String property : replace.keySet())
		{
			for (Object searchValue : replace.get(property).keySet())
			{
				if (result.getValue(property).equals(searchValue))
					result.setValue(property, replace.get(property).get(searchValue));
			}
		}
		return true;
	}

}
