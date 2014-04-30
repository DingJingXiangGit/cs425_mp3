package uiuc.nosql.model.task;

import uiuc.nosql.model.Request;
import uiuc.nosql.model.Response;

public abstract class Task {
	private int taskId;
	private Request request;
	
	public abstract boolean consume(Response response);

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
}
