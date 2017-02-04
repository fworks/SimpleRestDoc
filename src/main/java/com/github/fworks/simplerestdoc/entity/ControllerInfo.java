package com.github.fworks.simplerestdoc.entity;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class ControllerInfo {

	private String controllerClassName;
	private String controllerPath;
	private List<MethodInfo> controllerMethods = new ArrayList<MethodInfo>();

	public String getControllerClassName() {
		return controllerClassName;
	}

	public void setControllerClassName(String controllerClassName) {
		this.controllerClassName = controllerClassName;
	}

	public String getControllerPath() {
		return controllerPath;
	}

	public void setControllerPath(String controllerPath) {
		this.controllerPath = controllerPath;
	}

	public List<MethodInfo> getControllerMethods() {
		return controllerMethods;
	}

	public void setControllerMethods(List<MethodInfo> controllerMethods) {
		this.controllerMethods = controllerMethods;
	}

	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}