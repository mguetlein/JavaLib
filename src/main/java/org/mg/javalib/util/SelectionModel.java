package org.mg.javalib.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import javax.swing.SwingUtilities;

public class SelectionModel
{
	LinkedHashSet<Integer> selected = new LinkedHashSet<Integer>();
	Integer lastSelected = null;
	List<PropertyChangeListener> listeners;
	boolean multiSelectionAllowed = false;
	boolean suppressEvents = false;
	boolean exclusiveSelection = false;
	boolean isAWTModel = true;

	public SelectionModel()
	{
		listeners = new ArrayList<PropertyChangeListener>();
	}

	public SelectionModel(boolean multiSelectionAllowed)
	{
		this();
		this.multiSelectionAllowed = multiSelectionAllowed;
	}

	public void addListenerFirst(PropertyChangeListener l)
	{
		listeners.add(0, l);
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
			return lastSelected;
	}

	public int[] getSelectedIndices()
	{
		return ArrayUtil.toPrimitiveIntArray(selected);
	}

	public void clearSelection()
	{
		if (isAWTModel && !SwingUtilities.isEventDispatchThread())
			throw new IllegalStateException("this is a awt model, set selection only in awt dispatch thread");
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
		if (isAWTModel && !SwingUtilities.isEventDispatchThread())
			throw new IllegalStateException("this is a awt model, set selection only in awt dispatch thread");
		int[] oldVal = getSelectedIndices();
		if (isSelected(index))
		{
			selected.remove(index);
			for (Integer i : selected)
				lastSelected = i; //lazy way to set lastSelected to the last elem in the list
		}
		else
		{
			if (!multiSelectionAllowed || exclusiveSelection)
				selected.clear();
			if (index < 0)
				throw new IllegalStateException();
			selected.add(index);
			lastSelected = index;
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

	public void setSuppressEvents(boolean suppressEvents)
	{
		this.suppressEvents = suppressEvents;
	}

	public void setAWTModel(boolean isAWTModel)
	{
		this.isAWTModel = isAWTModel;
	}

	public void setSelectedIndices(int[] indices, boolean exclusiveSelection)
	{
		boolean b[] = new boolean[indices.length];
		Arrays.fill(b, true);
		setSelectedIndices(indices, b, exclusiveSelection);
	}

	private void setSelectedIndices(int[] indices, boolean selected[], boolean exclusiveSelection)
	{
		if (isAWTModel && !SwingUtilities.isEventDispatchThread())
			throw new IllegalStateException("this is a awt model, set selection only in awt dispatch thread");
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
