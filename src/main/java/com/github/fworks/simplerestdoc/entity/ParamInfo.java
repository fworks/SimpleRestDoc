package com.github.fworks.simplerestdoc.entity;

import com.google.gson.Gson;

public class ParamInfo {
	private String paramName;
	private String paramType;
	private String paramValue;
	private Object jsonSchema;

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamType() {
		return paramType;
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public Object getJsonSchema() {
		return jsonSchema;
	}

	public void setJsonSchema(Object jsonSchema) {
		this.jsonSchema = jsonSchema;
	}

	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}