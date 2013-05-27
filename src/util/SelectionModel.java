package util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class SelectionModel
{
	HashSet<Integer> selected = new HashSet<Integer>();
	List<PropertyChangeListener> listeners;
	boolean multiSelectionAllowed = false;
	boolean suppressEvents = false;
	boolean exclusiveSelection = false;

	public SelectionModel()
	{
		listeners = new ArrayList<PropertyChangeListener>();
	}

	public SelectionModel(boolean multiSelectionAllowed)
	{
		this();
		this.multiSelectionAllowed = multiSelectionAllowed;
	}

	public void addListener(PropertyChangeListener l)
	{
		listeners.add(l);
	}

	public int getNumSelected()
	{
		return selected.size();
	}

	public int getSelected()
	{
		if (getNumSelected() == 0)
			return -1;
		else
			return getSelectedIndices()[0];
	}

	public int[] getSelectedIndices()
	{
		return ArrayUtil.toPrimitiveIntArray(selected);
	}

	public void clearSelection()
	{
		int[] oldVal = getSelectedIndices();
		selected.clear();
		if (!suppressEvents && oldVal.length > 0)
			for (PropertyChangeListener l : listeners)
				l.propertyChange(new PropertyChangeEvent(this, "", oldVal, getSelectedIndices()));
	}

	public void setSelected(int index)
	{
		setSelected(index, true);
	}

	public void setSelected(int index, boolean exclusiveSelection)
	{
		setSelected(index, exclusiveSelection, true);
	}

	public void setSelected(int index, boolean exclusiveSelection, boolean selected)
	{
		if (exclusiveSelection)
		{
			if (isSelected(index) == selected && getNumSelected() == 1)
				return;
		}
		else
		{
			if (isSelected(index) == selected)
				return;
		}
		setSelectedInverted(index, exclusiveSelection);
	}

	public void setSelectedInverted(int index)
	{
		setSelectedInverted(index, false);
	}

	private void setSelectedInverted(int index, boolean exclusiveSelection)
	{
		int[] oldVal = getSelectedIndices();
		if (isSelected(index))
			selected.remove(index);
		else
		{
			if (!multiSelectionAllowed || exclusiveSelection)
				selected.clear();
			if (index < 0)
				throw new IllegalStateException();
			selected.add(index);
		}
		this.exclusiveSelection = exclusiveSelection;
		if (!suppressEvents)
			for (PropertyChangeListener l : listeners)
				l.propertyChange(new PropertyChangeEvent(this, "", oldVal, getSelectedIndices()));
	}

	public boolean isSelected(int index)
	{
		return selected.contains(index);
	}

	public void addRemoveSelectedIndices(int[] indices, boolean[] selected)
	{
		setSelectedIndices(indices, selected, true);
	}

	public void addSelectedIndices(int[] indices)
	{
		boolean b[] = new boolean[indices.length];
		Arrays.fill(b, true);
		setSelectedIndices(indices, b, false);
	}

	public void setSelectedIndices(int[] indices)
	{
		boolean b[] = new boolean[indices.length];
		Arrays.fill(b, true);
		setSelectedIndices(indices, b, true);
	}

	public void setSelectedIndices(int[] indices, boolean exclusiveSelection)
	{
		boolean b[] = new boolean[indices.length];
		Arrays.fill(b, true);
		setSelectedIndices(indices, b, exclusiveSelection);
	}

	private void setSelectedIndices(int[] indices, boolean selected[], boolean exclusiveSelection)
	{
		int[] oldSelection = getSelectedIndices();
		suppressEvents = true;
		if (exclusiveSelection)
			clearSelection();
		for (int j = 0; j < indices.length; j++)
			setSelected(indices[j], false, selected[j]);
		suppressEvents = false;
		int[] newSelection = getSelectedIndices();
		this.exclusiveSelection = exclusiveSelection;
		if (!suppressEvents && !Arrays.equals(oldSelection, newSelection))
			for (PropertyChangeListener l : listeners)
				l.propertyChange(new PropertyChangeEvent(this, "", oldSelection, newSelection));
	}

	public boolean isExclusiveSelection()
	{
		return exclusiveSelection;
	}

}
