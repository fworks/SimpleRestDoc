package com.github.fworks.simplerestdoc;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.web.bind.annotation.RestController;

import com.github.fworks.simplerestdoc.entity.ControllerInfo;
import com.github.fworks.simplerestdoc.entity.MethodInfo;
import com.github.fworks.simplerestdoc.util.Utilities;
import com.google.gson.GsonBuilder;

public class RestDoc {

	Reflections reflections;

	public RestDoc() {
		// TODO Auto-generated constructor stub
		reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forClassLoader()).addScanners(new TypeAnnotationsScanner()/* , new MethodParameterNamesScanner() */));
		List<Class<?>> restControllers = new ArrayList<Class<?>>(reflections.getTypesAnnotatedWith(RestController.class));
		Collections.sort(restControllers, new Comparator<Class<?>>() {
			@Override
			public int compare(Class<?> o1, Class<?> o2) {
				return o1.getSimpleName().compareTo(o2.getSimpleName());
			}
		});
		List<ControllerInfo> controllerInfos = new ArrayList<>();
		for (Class<?> class1 : restControllers) {
			controllerInfos.add(this.createDoc(class1));
			// break;
		}
		System.out.println(new GsonBuilder().disableHtmlEscaping().create().toJson(controllerInfos));
	}

	private ControllerInfo createDoc(Class<?> controllerClass) {
		ControllerInfo docInfo = new ControllerInfo();
		docInfo.setControllerClassName(controllerClass.getSimpleName());
		docInfo.setControllerPath(Utilities.getRequestMappingPathValue(controllerClass));
		Method[] methods = controllerClass.getMethods();
		for (Method method : methods) {
			MethodInfo methodInfo = Utilities.getMethodInfoFromMethod(method);
			if (methodInfo != null) {
				docInfo.getControllerMethods().add(methodInfo);
				methodInfo.getMethodParams().addAll(Utilities.getMethodParamFromMethod(method));
			}
		}
		return docInfo;
	}
}
