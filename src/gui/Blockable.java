package gui;

public interface Blockable
{
	public void block(String blocker);

	public boolean isBlocked();

	public void unblock(String blocker);
}
