package org.mg.javalib.gui;

import java.util.ArrayList;
import java.util.List;

public class BlockableImpl implements Blockable
{
	List<String> block = new ArrayList<String>();
	private static boolean DEBUG = false;

	@Override
	public synchronized void block(String blocker)
	{
		if (block.contains(blocker))
			throw new Error("already blocking for: " + blocker);
		block.add(blocker);
		if (DEBUG)
			System.out.println("BLOCK (" + block.size() + ") '" + blocker + "' ------------------");
	}

	@Override
	public boolean isBlocked()
	{
		return block.size() > 0;
	}

	@Override
	public synchronized void unblock(String blocker)
	{
		if (!block.contains(blocker))
			throw new Error("use block first for " + blocker);
		block.remove(blocker);
		if (block.size() == 0 && DEBUG)
			System.out.println("---------------- UNBLOCK");

	}

}
