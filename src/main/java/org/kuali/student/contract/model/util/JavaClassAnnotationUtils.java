/**
 * Copyright 2004-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.student.contract.model.util;

import javax.xml.bind.annotation.XmlEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.qdox.model.Annotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.annotation.AnnotationValue;

/**
 * 
 * Utility methods for extracting annotation details from a QDox JavaClass.
 * 
 * @author Kuali Student Team
 */
public final class JavaClassAnnotationUtils {

	private static final Logger log = LoggerFactory.getLogger(JavaClassAnnotationUtils.class);
	/**
	 * 
	 */
	private JavaClassAnnotationUtils() {
		// prevent subclassing and instances
	}

	public static boolean doesAnnotationExist(String simpleAnnotationName, JavaClass javaClass) {
		
		Annotation[] as = javaClass.getAnnotations();
		
		for (Annotation annotation : as) {
			if (annotation.getType().getJavaClass().getName().equals(simpleAnnotationName))
				return true;
		}
		// no match
		return false;
		
	}
	/**
	 * Extract the value of the XmlEnum annotation from the class provided.
	 * 
	 * returns null if the annotation does not exist on the class.
	 * 
	 * returns String.class if no value is specified which is the default specified by the annotation.
	 * 
	 * @param javaClass
	 * @return the class value stored in the XmlEnum annotation on the class specified.
	 */
	public static Class<?>extractXmlEnumValue (JavaClass javaClass) {
		
		Annotation[] as = javaClass.getAnnotations();
		
		for (Annotation annotation : as) {
			if (annotation.getType().getJavaClass().getName().equals(XmlEnum.class.getSimpleName())) {
				
				AnnotationValue value = annotation.getProperty("value");
				
				if (value == null) {
					// this is what the XmlEnum annotation defaults to
					// ideally I would get this from the XmlEnum but I can't figure out how
					// so I will just hard code it.
					return String.class;
				}
				
					
				String clazz =  (String) value.getParameterValue();
				
					String className = clazz.replaceFirst("\\.class", "");
					
					try {
	                    return ClassLoader.getSystemClassLoader().loadClass(className);
                    } catch (ClassNotFoundException e) {
                    	log.error("No class found for name: " + className);
                    	
                    	return null;
                    }
				
			}
		}
		
		// no match
		return null;
	}
}
