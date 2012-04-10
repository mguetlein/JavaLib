package task;

interface TaskListener
{
	enum TaskEvent
	{
		update, verbose, warning, failed, cancelled, finished;
	}

	void update(TaskEvent event);
}
