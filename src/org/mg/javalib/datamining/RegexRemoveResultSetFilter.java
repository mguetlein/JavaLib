package org.mg.javalib.datamining;

public class RegexRemoveResultSetFilter extends RemoveResultSetFilter
{
	@Override
	protected boolean matches(Object filterValue, Object resultValue)
	{
		return resultValue.toString().matches(filterValue.toString());
	}
}
