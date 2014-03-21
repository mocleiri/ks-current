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
package org.kuali.student.datadictionary.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Stack;
import org.kuali.rice.core.api.uif.DataType;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.CollectionDefinition;
import org.kuali.rice.krad.datadictionary.ComplexAttributeDefinition;
import org.kuali.rice.krad.datadictionary.DataDictionaryDefinitionBase;
import org.kuali.rice.krad.datadictionary.DataObjectEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bean2DictionaryConverter {

	private static final Logger log = LoggerFactory.getLogger(Bean2DictionaryConverter.class);
	
    private Class<?> clazz;
    private Stack<DataDictionaryDefinitionBase> parentFields;
    private Stack<Class<?>> parentClasses;

    public Bean2DictionaryConverter(Class<?> clazz, Stack<DataDictionaryDefinitionBase> parentFields, Stack<Class<?>> parentClasses) {
        this.clazz = clazz;
        this.parentFields = parentFields;
        this.parentClasses = parentClasses;
    }

    public DataObjectEntry convert() {
        DataObjectEntry ode = new DataObjectEntry();
        ode.setDataObjectClass(clazz);
        addFields("", ode);
        return ode;
    }

    public void addFields(String debuggingContext, DataObjectEntry ode) {
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(clazz);
        } catch (IntrospectionException ex) {
            throw new RuntimeException(ex);
        }
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            if (Class.class.equals(pd.getPropertyType())) {
                continue;
            }
            if ("_futureElements".equals(pd.getName())) {
                continue;
            }
            if ("attributes".equals(pd.getName())) {
                continue;
            }
            String name = calcName(pd.getName());
            Class<?> actualClass = calcActualClass(clazz, pd);
            DataType dt = calcDataType(debuggingContext + "." + clazz.getSimpleName() + "." + name, actualClass);
            DataDictionaryDefinitionBase dddef = calcDataDictionaryDefinition(pd, dt);
            if (dddef instanceof AttributeDefinition) {
                AttributeDefinition ad = (AttributeDefinition) dddef;
                ode.getAttributes().add(ad);
            } else if (dddef instanceof ComplexAttributeDefinition) {
                ComplexAttributeDefinition cad = (ComplexAttributeDefinition) dddef;
                ode.getComplexAttributes().add(cad);
                if (!parentClasses.contains(clazz)) {
                    parentFields.push(dddef);
                    parentClasses.push(clazz);
                    Bean2DictionaryConverter subConverter = new Bean2DictionaryConverter(actualClass, parentFields, parentClasses);
                    subConverter.addFields(debuggingContext + "." + clazz.getSimpleName() + name, ode);
                    parentFields.pop();
                    parentClasses.pop();
                }
            } else if (dddef instanceof CollectionDefinition) {
                CollectionDefinition cd = (CollectionDefinition) dddef;
                ode.getCollections().add(cd);
                // TODO: handle collections of primitives
                // DataType == null means it is a complex
//                TODO: add back in this logic once they fix the jira about collectoin definition not working right                
//                if (dt == null) {
//                    if (!parentClasses.contains(clazz)) {
//                        parentFields.push(dddef);
//                        parentClasses.push(clazz);
//                        Bean2DictionaryConverter subConverter = new Bean2DictionaryConverter(actualClass, parentFields, parentClasses);
//                        subConverter.addFields(debuggingContext + "." + clazz.getSimpleName() + name, ode);
//                        parentFields.pop();
//                        parentClasses.pop();
//                    }
//                }
            }
            if (dddef instanceof ComplexAttributeDefinition || dddef instanceof CollectionDefinition) {
            }
        }
    }

    private DataDictionaryDefinitionBase calcDataDictionaryDefinition(PropertyDescriptor pd, DataType dataType) {
        Class<?> pt = pd.getPropertyType();
        if (List.class.equals(pt)) {
            if (dataType != null) {
                log.warn("Can't handle lists of primitives just yet: " + calcName(pd.getName()));
            }
            CollectionDefinition cd = new CollectionDefinition();
            cd.setName(calcName(pd.getName()));
//            cd.setDataObjectClass(pt);
            return cd;
        }
        if (dataType != null) {
            AttributeDefinition ad = new AttributeDefinition();
            ad.setName(calcName(pd.getName()));
            ad.setDataType(dataType);
            return ad;
        }
        ComplexAttributeDefinition cad = new ComplexAttributeDefinition();
        cad.setName(calcName(pd.getName()));
//        cad.setDataObjectEntry(pt);
        return cad;
    }

    private String calcName(String leafName) {
        StringBuilder bldr = new StringBuilder();
        if (!parentFields.isEmpty()) {
            DataDictionaryDefinitionBase parent = parentFields.peek();
            if (parent instanceof ComplexAttributeDefinition) {
                ComplexAttributeDefinition cad = (ComplexAttributeDefinition) parent;
                bldr.append(cad.getName());
                bldr.append(".");
            } else if (parent instanceof CollectionDefinition) {
                CollectionDefinition cad = (CollectionDefinition) parent;
                bldr.append(cad.getName());
                bldr.append(".");
            }
        }
        bldr.append(initLower(leafName));
        return bldr.toString();
    }

    private String initLower(String name) {
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    public static Class<?> calcActualClass(Class<?> clazz, PropertyDescriptor pd) {
        Class<?> pt = null;
        // there is a bug in the BeanInfo impl see workaround below
        // pd.getPropertyType gets the interface not the info object
        // for example: 
        // info has...
        // @Override
        // ExpenditureInfo getExpenditure ();
        // 
        // and interface has
        // Expenditure getExpenditure ();
        // then pd.getPropertyType () returns Expenditure not ExpenditureInfo
        // so...
        // we use the work around if it gets an interface
        pt = pd.getPropertyType();
        if (pt.isInterface()) {
            if (pd.getReadMethod() != null) {
                pt = workAround(clazz, pd.getReadMethod().getName());
            }
//            else {
//                throw new NullPointerException (clazz.getName() + "." + pd.getName() + " has no corresponding read method");
//            }
        }
        
        if (List.class.equals(pt)) {
            pt = ComplexSubstructuresHelper.getActualClassFromList(clazz, pd.getName());
        }
        return pt;
    }

    private static Class<?> workAround(Class<?> currentTargetClass, String methodName) {
        Method method = findMethodImplFirst(currentTargetClass, methodName);
        return method.getReturnType();
    }

    /**
     * Got this code from:
     * http://raulraja.com/2009/09/12/java-beans-introspector-odd-behavio/
     * 
     * workaround for introspector odd behavior with javabeans that implement interfaces with comaptible return types
     * but instrospection is unable to find the right accessors
     *
     * @param currentTargetClass the class being evaluated
     * @param methodName		 the method name we are looking for
     * @param argTypes		   the arg types for the method name
     * @return a method if found
     */
    private static Method findMethodImplFirst(Class<?> currentTargetClass, String methodName, Class<?>... argTypes) {
        Method method = null;
        if (currentTargetClass != null && methodName != null) {
            try {
                method = currentTargetClass.getMethod(methodName, argTypes);
            } catch (Throwable t) {
                // nothing we can do but continue
            }
            //Is the method in one of our parent classes
            if (method == null) {
                Class<?> superclass = currentTargetClass.getSuperclass();
                if (!superclass.equals(Object.class)) {
                    method = findMethodImplFirst(superclass, methodName, argTypes);
                }
            }
        }
        return method;
    }

    public static DataType calcDataType(String context, Class<?> pt) {
        if (int.class.equals(pt) || Integer.class.equals(pt)) {
            return DataType.INTEGER;
        } else if (long.class.equals(pt) || Long.class.equals(pt)) {
            return DataType.LONG;
        } else if (double.class.equals(pt) || Double.class.equals(pt)) {
            return DataType.DOUBLE;
        } else if (float.class.equals(pt) || Float.class.equals(pt)) {
            return DataType.FLOAT;
        } else if (boolean.class.equals(pt) || Boolean.class.equals(pt)) {
            return DataType.BOOLEAN;
        } else if (Date.class.equals(pt)) {
            return DataType.DATE;
        } else if (String.class.equals(pt)) {
            return DataType.STRING;
        } else if (List.class.equals(pt)) {
            throw new RuntimeException("Found list can't have a list of lists, List<List<?>> in " + context);
        } else if (Enum.class.isAssignableFrom(pt)) {
            return DataType.STRING;
        } else if (Object.class.equals(pt)) {
            return DataType.STRING;
        } else if (pt.getName().startsWith("org.kuali.student.") || pt.getName().startsWith("org.kuali.rice.")) {
            return null;
        } else {
            throw new RuntimeException("Found unknown/unhandled type of object in bean " + pt.getName() + " in " + context);
        }
    }
}
