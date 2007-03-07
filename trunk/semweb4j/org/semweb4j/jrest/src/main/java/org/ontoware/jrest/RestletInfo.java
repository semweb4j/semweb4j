package org.ontoware.jrest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.ontoware.jrest.annotation.AnnotationUtils;
import org.ontoware.jrest.annotation.DefaultValue;
import org.ontoware.jrest.annotation.RestAddressByOption;
import org.ontoware.jrest.annotation.RestAddressByParameter;
import org.ontoware.jrest.annotation.RestAddressByPath;
import org.ontoware.jrest.annotation.RestContent;

/**
 * 
 * EXPERIMENTAL
 * 
 * gahers data about a restlet for purposes of self-documentation generating
 * web-forms and command line utils)
 * 
 * @author voelkel
 * 
 */
public class RestletInfo {

	/**
	 * 
	 * @param restlet -
	 *            consider only the 'get','put','post','delete' methods
	 * @return the document
	 */
	public static Document getDocumentation(Object restlet) {
		Document d = DocumentHelper.createDocument();
		Element _resource = d.addElement("resource");

		if (restlet == null) {
			_resource.setText("null");
			return d;
		}
		
		Class c = restlet.getClass();
		for (Method method : c.getMethods()) {
			// TODO: ignore case ok?
			if (method.getName().equalsIgnoreCase(RestServlet.GET)
					|| method.getName().equalsIgnoreCase(RestServlet.PUT)
					|| method.getName().equalsIgnoreCase(RestServlet.POST)
					|| method.getName().equalsIgnoreCase(RestServlet.DELETE)) {
				// TODO add to doc
				Element _method = _resource.addElement("" + method.getName());
				for (int i = 0; i < method.getParameterTypes().length; i++) {
					Element _parameter = _method.addElement("parameter");

					Class<?> javaParameterType = method.getParameterTypes()[i];
					_parameter.addAttribute("type", javaParameterType.getSimpleName());

					Map<Class<?>, Annotation> type2annotation = AnnotationUtils.asMap(method
							.getParameterAnnotations()[i]);
					RestAddressByParameter restAddressByParameter = (RestAddressByParameter) type2annotation
							.get(RestAddressByParameter.class);
					RestAddressByPath restAddressByPath = (RestAddressByPath) type2annotation
							.get(RestAddressByPath.class);
					RestAddressByOption restAddressByOption = (RestAddressByOption) type2annotation
					.get(RestAddressByOption.class);
					RestContent restContent = (RestContent) type2annotation.get(RestContent.class);

					DefaultValue defaultValue = (DefaultValue) type2annotation
							.get(DefaultValue.class);
					if (defaultValue != null) {
						_parameter.addAttribute("default", defaultValue.value());
					}

					if (restAddressByParameter != null) {
						String restParameterName = restAddressByParameter.value();
						_parameter.addAttribute("name", restParameterName);
					} else if (restAddressByPath != null) {
						_parameter.addAttribute("name", "resource-path");
					} else if (restContent != null) {
						_parameter.addAttribute("name", "resource-content");
					}
					
					if (restAddressByOption != null) {
						for(String option : restAddressByOption.value()) {
							_parameter.addElement("option").setText(option);
						}
					}

				}
			}
		}
		return d;
	}

}
