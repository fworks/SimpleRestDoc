package com.github.fworks.simplerestdoc.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.github.fworks.simplerestdoc.entity.MethodInfo;
import com.github.fworks.simplerestdoc.entity.ParamInfo;
import com.google.gson.Gson;

public class Utilities {

	/**
	 * Get the request mapping path for the controller
	 * @param controllerClass
	 * @return
	 */
	public static String getRequestMappingPathValue(Class<?> controllerClass) {
		RequestMapping classannotation = controllerClass.getAnnotation(RequestMapping.class);
		if (classannotation != null) {
			String[] value = classannotation.value();
			for (String string : value) {
				return string;
			}
		}
		return null;
	}

	/**
	 * Get the method info for the method
	 * @param method
	 * @return
	 */
	public static MethodInfo getMethodInfoFromMethod(Method method) {
		RequestMapping annotation = method.getAnnotation(RequestMapping.class);
		if (annotation != null) {
			MethodInfo api = new MethodInfo();
			RequestMethod[] requestMethods = annotation.method();
			for (RequestMethod requestMethod : requestMethods) {
				api.setMethodName(requestMethod.name());
			}
			String[] value = annotation.value();
			for (String string : value) {
				api.setMethodPath(string);
			}
			Class<?> returnType = method.getReturnType();
			if (returnType.equals(ResponseEntity.class)) {
				try {
					Object fieldValue = getFieldValue(method.getClass().getDeclaredField("signature"), method);
					String rt = "undefined";
					if (fieldValue != null) {
						rt = fieldValue.toString().substring(fieldValue.toString().indexOf(returnType.getName().replace(".", "/"))).replace("/", ".");
						rt = rt.replace(ResponseEntity.class.getName(), "").replace(">;", ">").replace("Ljava.util.", "").replace("Ljava.lang.", "");
					}
					api.setMethodReturn(rt);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			} else {
				try {
					String rt = returnType.getName();
					api.setMethodReturn(rt);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			return api;
		}
		return null;
	}

	/**
	 * Get parameters of the method
	 * @param method
	 * @return
	 */
	public static List<ParamInfo> getMethodParamFromMethod(Method method) {
		Parameter[] parameters = method.getParameters();
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		List<ParamInfo> params = new ArrayList<>();
		// method.getParameterAnnotations()[0]
		int i = 0;
		for (Parameter parameter : parameters) {
			try {
				ParamInfo param = new ParamInfo();
				param.setParamName(parameter.getName());
				Annotation[] annotations = parameterAnnotations[i];
				if (annotations != null && annotations.length > 0) {
					String string = annotations[0].toString();
					Class<? extends Annotation> annotationType = annotations[0].annotationType();
					if (annotationType.getName().equals(PathVariable.class.getName())) {
						// if PathVariable
						String replaceAll = string.replaceAll("@" + annotationType.getName(), "");
						replaceAll = replaceAll.substring(1, replaceAll.length() - 1);
						String[] split = replaceAll.split(",");
						for (String string2 : split) {
							if (string2.startsWith("name=")) {
								String[] split2 = string2.split("=");
								if (split2.length > 1) {
									param.setParamName(split2[1]);
								}
							}
						}
					} else if (annotationType.getName().equals(RequestBody.class.getName())) {
						param.setParamName(null);
					} else {
						String annotationName = annotationType.getSimpleName();
						param.setParamName(annotationName);
					}
				}
				i++;
				params.add(param);
				if (!parameter.getType().equals(List.class)) {
					param.setParamType(parameter.getType().getSimpleName());
					if (parameter.getType().equals(String.class) || parameter.getType().equals(Integer.class) || parameter.getType().equals(Boolean.class) || parameter.getType().isPrimitive()) {

					} else {
						String createClassJsonSchema = createClassJsonSchema(parameter.getType());
						param.setJsonSchema(new Gson().fromJson(createClassJsonSchema, Object.class));
					}
				} else {
					param.setParamType(parameter.getParameterizedType().getTypeName());
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return params;
	}

	/**
	 * Create the jsonSchema for a class
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public static String createClassJsonSchema(Class<?> clazz) throws Exception {
		Field[] declaredFields = clazz.getDeclaredFields();
		StringBuffer jsonSB = new StringBuffer();
		for (Field field : declaredFields) {
			if (field.getType().equals(byte[].class)) {
				jsonSB.append("\"" + field.getName() + "\":{\"type\":\"byte[]\"},");
			} else if (field.getType().isPrimitive() || types.contains(field.getType())) {
				jsonSB.append("\"" + field.getName() + "\":{\"type\":\"" + field.getType().getSimpleName() + "\"},");
			} else if (field.getType().isPrimitive() || typesTyped.contains(field.getType())) {
				String string = getFieldValue(field.getClass().getDeclaredField("signature"), field).toString();
				if (field.getType().equals(List.class)) {
					// if list
					string = string.replace("Ljava/util/List<", "").replace(";>;", "");
					if (string.startsWith("L")) {
						string = string.replace("/", ".");
						string = string.substring(1);
						Class<?> forName = Class.forName(string);
						if (forName.isPrimitive() || types.contains(forName)) {
							jsonSB.append("\"" + field.getName() + "\":{\"type\":\"" + field.getType().getSimpleName() + "\",\"of\":\"" + forName.getSimpleName() + "\", \"value\":\"" + forName.getSimpleName() + "\"},");
						} else {
							jsonSB.append(
									"\"" + field.getName() + "\":{\"type\":\"" + field.getType().getSimpleName() + "\",\"of\":\"" + forName.getSimpleName() + "\", \"value\":" + createClassJsonSchema(forName) + "},");
						}
					}
				} else {
					string = string.replace("Ljava/util/Map<", "").replace(";>;", "");
					String replace = string.replace("/", ".").replace("Ljava.util.", "").replace("Ljava.lang.", "").replace(";", ",").replace(",>", ">");
					jsonSB.append("\"" + field.getName() + "\":{\"type\":\"" + field.getType().getSimpleName() + "\",\"of\":\"" + replace + "\"},");
				}
			} else if (field.getType().isEnum()) {
				jsonSB.append("\"" + field.getName() + "\":{\"type\":\"enum\",\"values\":" + new Gson().toJson(field.getType().getEnumConstants()) + "},");
			} else {
				if (!field.getType().equals(clazz)) {
					jsonSB.append("\"" + field.getName() + "\":" + createClassJsonSchema(field.getType()) + ",");
				} else {
					jsonSB.append("\"" + field.getName() + "\":" + "{}" + ",");
				}
			}
		}
		if (!jsonSB.toString().trim().isEmpty()) {
			String x = "{" + jsonSB.toString().substring(0, jsonSB.toString().length() - 1) + "}";
			return x;
		}
		return "";
	}

	private static final List<Class<?>> types = Arrays.asList(String.class, Integer.class, Boolean.class, Double.class, BigDecimal.class, Long.class);
	private static final List<Class<?>> typesTyped = Arrays.asList(Map.class, List.class);

	public static Object getFieldValue(Field declaredField, Object field) {
		try {
			declaredField.setAccessible(true);
			return declaredField.get(field);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
