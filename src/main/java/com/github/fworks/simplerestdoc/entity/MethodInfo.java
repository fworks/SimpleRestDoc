package com.github.fworks.simplerestdoc.entity;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class MethodInfo {

	private String methodPath;
	private String methodName;
	private List<ParamInfo> methodParams = new ArrayList<>();;
	private String methodReturn;

	public String getMethodPath() {
		return methodPath;
	}

	public void setMethodPath(String methodPath) {
		this.methodPath = methodPath;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public List<ParamInfo> getMethodParams() {
		return methodParams;
	}

	public void setMethodParams(List<ParamInfo> methodParams) {
		this.methodParams = methodParams;
	}

	public String getMethodReturn() {
		return methodReturn;
	}

	public void setMethodReturn(String methodReturn) {
		this.methodReturn = methodReturn;
	}

	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}