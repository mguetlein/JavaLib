package task;

import io.Logger;

import java.util.ArrayList;
import java.util.List;

import task.TaskListener.TaskEvent;
import util.ObjectUtil;

public class TaskImpl implements Task
{
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

	private Logger logger;

	static class DetailMessage
	{
		String message;
		String detail;
	}

	public TaskImpl(String name)
	{
		this(name, 100);
	}

	public TaskImpl(String name, Logger logger)
	{
		this(name, 100, logger);
	}

	public TaskImpl(String name, int maxStatus)
	{
		this(name, maxStatus, null);
	}

	public TaskImpl(String name, int maxStatus, Logger logger)
	{
		this.name = name;
		this.maxStatus = maxStatus;
		this.logger = logger;
	}

	private void println(String msg, boolean debug)
	{
		if (logger != null)
			if (debug)
				logger.debug(msg);
			else
				logger.info(msg);
		else
			System.out.println(msg);
	}

	@Override
	public void update(String update)
	{
		println(name + "> " + update, false);
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
		println(name + "> " + update, false);
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
		if (!ObjectUtil.equals(this.verbose, verbose))
		{
			println(name + "> " + verbose, true);
			this.verbose = verbose;
			fire(TaskEvent.verbose);
		}
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
		logger.error(exception);
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
		println(name + "> CANCELLED", false);
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
		println(name + "> finished", false);
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

	Logger getLogger()
	{
		return logger;
	}

}
