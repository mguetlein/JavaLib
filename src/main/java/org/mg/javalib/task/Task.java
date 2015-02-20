package org.mg.javalib.task;

public interface Task
{
	public void update(double status);

	public void update(double status, String update);

	public void update(String update);

	/*
	 * will be logged
	 */
	public void debug(String verbose);

	/*
	 * will be not logged
	 */
	public void verbose(String verbose);

	public void warning(String warningMessage, String details);

	public void warning(String warningMessage, Throwable exception);

	public void failed(String errorMessage, String details);

	public void failed(String errorMessage, Throwable exception);

	public boolean isRunning();

	public void cancel();

	public void finish();
}
