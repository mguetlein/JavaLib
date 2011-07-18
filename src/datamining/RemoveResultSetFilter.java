package datamining;

import java.util.ArrayList;
import java.util.List;

public class RemoveResultSetFilter implements ResultSetFilter
{
	List<String> properties;
	List<Object[]> values;
	List<Boolean> removeMatches;

	public RemoveResultSetFilter()
	{
		properties = new ArrayList<String>();
		values = new ArrayList<Object[]>();
		removeMatches = new ArrayList<Boolean>();
	}

	public void addFilterValue(String property, Object value)
	{
		addFilterValue(property, new Object[] { value }, true);
	}

	public void addFilterValue(String property, Object value, boolean removeMatches)
	{
		addFilterValue(property, new Object[] { value }, removeMatches);
	}

	public void addFilterValue(String property, Object value[])
	{
		addFilterValue(property, value, true);
	}

	public void addFilterValue(String property, Object value[], boolean removeMatches)
	{
		properties.add(property);
		values.add(value);
		this.removeMatches.add(removeMatches);
	}

	@Override
	public boolean accept(Result result)
	{
		for (int i = 0; i < properties.size(); i++)
		{
			boolean match = false;
			for (int j = 0; j < values.get(i).length; j++)
				if (matches(values.get(i)[j], result.getValue(properties.get(i))))
				{
					match = true;
					break;
				}
			if ((removeMatches.get(i) && match) || (!removeMatches.get(i) && !match))
				return false;
		}
		return true;
	}

	protected boolean matches(Object filterValue, Object resultValue)
	{
		return filterValue.equals(resultValue);
	}
}
