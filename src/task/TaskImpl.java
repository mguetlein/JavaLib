package task;

import java.util.ArrayList;
import java.util.List;

import task.TaskListener.TaskEvent;

public class TaskImpl implements Task
{
	public static boolean PRINT_VERBOSE = false;

	private String name;
	private double status = 0;
	private double maxStatus;
	private List<DetailMessage> warnings = new ArrayList<DetailMessage>();
	private DetailMessage error;
	private String update;
	private String verbose;
	private boolean cancelled = false;
	private boolean finished = false;
	private List<TaskListener> listeners = new ArrayList<TaskListener>();

	static class DetailMessage
	{
		String message;
		String detail;
	}

	public TaskImpl(String name)
	{
		this(name, 100);
	}

	public TaskImpl(String name, int maxStatus)
	{
		this.name = name;
		this.maxStatus = maxStatus;
	}

	@Override
	public void update(String update)
	{
		System.out.println(name + "> " + update);
		this.update = update;
		fire(TaskEvent.update);
	}

	@Override
	public void update(double status)
	{
		this.status = status;
		fire(TaskEvent.update);
	}

	@Override
	public void update(double status, String update)
	{
		System.out.println(name + "> " + update);
		this.status = status;
		this.update = update;
		fire(TaskEvent.update);
	}

	String getUpdateMessage()
	{
		return update;
	}

	@Override
	public void verbose(String verbose)
	{
		if (PRINT_VERBOSE)
			System.out.println(name + "> " + verbose);
		this.verbose = verbose;
		fire(TaskEvent.verbose);
	}

	String getVerboseMessage()
	{
		return verbose;
	}

	int getPercent()
	{
		return (int) Math.floor((status / maxStatus) * 100);
	}

	@Override
	public void warning(String warningMessage, Throwable exception)
	{
		warning(warningMessage, exception.getMessage());
	}

	@Override
	public void warning(String warningMessage, String details)
	{
		DetailMessage warn = new DetailMessage();
		warn.message = warningMessage;
		warn.detail = details;
		warnings.add(warn);
		fire(TaskEvent.warning);
	}

	boolean hasWarnings()
	{
		return warnings.size() > 0;
	}

	@Override
	public void failed(String errorMessage, Throwable exception)
	{
		failed(errorMessage, exception.getClass().getSimpleName() + ": " + exception.getMessage());
	}

	@Override
	public void failed(String errorMessage, String details)
	{
		if (!isRunning())
			return;
		error = new DetailMessage();
		error.message = errorMessage;
		error.detail = details;
		fire(TaskEvent.failed);
	}

	boolean isFailed()
	{
		return error != null;
	}

	@Override
	public void cancel()
	{
		if (!isRunning())
			return;
		System.out.println(name + "> CANCELLED");
		cancelled = true;
		fire(TaskEvent.cancelled);
	}

	boolean isCancelled()
	{
		return cancelled;
	}

	@Override
	public void finish()
	{
		if (!isRunning())
			return;
		System.out.println(name + "> finished");
		finished = true;
		fire(TaskEvent.finished);
	}

	boolean isFinished()
	{
		return finished;
	}

	private void fire(TaskEvent event)
	{
		for (TaskListener l : listeners)
			l.update(event);
	}

	void addListener(TaskListener taskListener)
	{
		listeners.add(taskListener);
	}

	String getName()
	{
		return name;
	}

	DetailMessage getError()
	{
		return error;
	}

	List<DetailMessage> getWarnings()
	{
		return warnings;
	}

	@Override
	public boolean isRunning()
	{
		return !isFailed() && !isCancelled() && !isFinished();
	}

}
