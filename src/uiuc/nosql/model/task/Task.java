package uiuc.nosql.model.task;

import uiuc.nosql.controller.TaskManager;
import uiuc.nosql.model.Action;
import uiuc.nosql.model.Level;
import uiuc.nosql.model.Request;
import uiuc.nosql.model.Response;

public abstract class Task {
	protected int taskId;
	protected Level level;
	protected Action action;
	protected Request request;
	protected TaskManager taskManager;
	
	public abstract void consume(Response response);

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}
	
	public Level getLevel() {
		return level;
	}
	
	public void setLevel(Level level) {
		this.level = level;
	}
	
	public Action getAction() {
		return action;
	}
	
	public void setAction(Action action) {
		this.action = action;
	}

	public void setManager(TaskManager taskManager) {
		this.taskManager = taskManager;
	}

	public abstract boolean isResultReady();
	public abstract void printResult();
}
