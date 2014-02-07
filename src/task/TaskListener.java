package task;

interface TaskListener
{
	enum TaskEvent
	{
		update, debug_verbose, warning, failed, cancelled, finished;
	}

	void update(TaskEvent event);
}
