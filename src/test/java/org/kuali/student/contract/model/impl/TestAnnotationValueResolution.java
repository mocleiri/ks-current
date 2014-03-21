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
package org.kuali.student.contract.model.impl;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlEnum;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.kuali.student.contract.model.util.JavaClassAnnotationUtils;
import org.kuali.student.model.annotation.EnumWithAnnotationValue;
import org.kuali.student.model.annotation.EnumWithoutAnnotationValue;
import org.slf4j.Logger;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.DefaultDocletTagFactory;
import com.thoughtworks.qdox.model.JavaClass;

/**
 * 
 * tests how to resolve both types of @XmlEnum annotations.  Those with and without the value defined.
 * 
 * @author Kuali Student Team
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class TestAnnotationValueResolution {

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(TestAnnotationValueResolution.class);
	
	private static Map<String, JavaClass> parsedJavaClassesMap;
	
	/**
	 * 
	 */
	public TestAnnotationValueResolution() {
		
	}

	@BeforeClass
	public static void setup () {
		
        DefaultDocletTagFactory dtf = new DefaultDocletTagFactory();
        JavaDocBuilder builder = new JavaDocBuilder(dtf);
        
        builder.addSourceTree(new File("src/test/java/org/kuali/student/model/annotation"));
        
       parsedJavaClassesMap = new LinkedHashMap<String, JavaClass>();
        
        for (JavaClass javaClass : builder.getClasses()) {
            
        	parsedJavaClassesMap.put(javaClass.getName(), javaClass);
            
        }
        
	}
	
	
	
	@Test
	public void checkExistence() {
		
		JavaClass withValue = parsedJavaClassesMap.get(EnumWithAnnotationValue.class.getSimpleName());
		
		Assert.assertNotNull(withValue);
		
		JavaClass withOutValue = parsedJavaClassesMap.get(EnumWithAnnotationValue.class.getSimpleName());
		
		Assert.assertNotNull(withOutValue);
		
	}
	
	@Test
	public void extractAnnotationValue() {
		
		JavaClass withValue = parsedJavaClassesMap.get(EnumWithAnnotationValue.class.getSimpleName());
		
		Assert.assertTrue(JavaClassAnnotationUtils.doesAnnotationExist(XmlEnum.class.getSimpleName(), withValue));
		
		Class<?>withValueClass = JavaClassAnnotationUtils.extractXmlEnumValue(withValue);
		
		Assert.assertEquals(Integer.class, withValueClass);
		
		JavaClass withoutValue = parsedJavaClassesMap.get(EnumWithoutAnnotationValue.class.getSimpleName());
		
		Assert.assertTrue(JavaClassAnnotationUtils.doesAnnotationExist(XmlEnum.class.getSimpleName(), withValue));
		
		Class<?>withoutValueClass = JavaClassAnnotationUtils.extractXmlEnumValue(withoutValue);
		
		Assert.assertEquals(String.class, withoutValueClass);
			
        
		
	}
}
