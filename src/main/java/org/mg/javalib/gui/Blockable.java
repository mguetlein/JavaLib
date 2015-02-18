package org.mg.javalib.gui;

public interface Blockable
{
	public void block(String blocker);

	public boolean isBlocked();

	public void unblock(String blocker);

	public static final String BLOCKED = "blocked";
	public static final String UN_BLOCKED = "unblocked";
}
