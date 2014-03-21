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

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.*;
import com.thoughtworks.qdox.model.Type;
import com.thoughtworks.qdox.model.annotation.AnnotationValue;
import org.kuali.student.contract.model.*;
import org.kuali.student.contract.model.util.JavaClassAnnotationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlEnum;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;

/**
 *
 * @author nwright
 */
public class ServiceContractModelQDoxLoader implements
        ServiceContractModel {
	
	private static final Logger log = LoggerFactory.getLogger(ServiceContractModelQDoxLoader.class);

    private static final String LOCALE_KEY_LIST = "LocaleKeyList";
    private static final String MESSAGE_GROUP_KEY_LIST = "MessageGroupKeyList";
    private static final JavaClass STRING_JAVA_CLASS = new JavaClass(
            "java.lang.String");
    private List<String> sourceDirectories = null;
    private List<Service> services = null;
    private List<ServiceMethod> serviceMethods = null;
    private Map<String, XmlType> xmlTypeMap = null;
    private List<MessageStructure> messageStructures;
    private boolean validateKualiStudent;

    public ServiceContractModelQDoxLoader(List<String> sourceDirectories) {
        this (sourceDirectories, true);
    }

    public ServiceContractModelQDoxLoader(List<String> sourceDirectories, boolean validateKualiStudent) {
        this.sourceDirectories = sourceDirectories;
        this.setValidateKualiStudent(validateKualiStudent);
    }

    public boolean isValidateKualiStudent() {
        return validateKualiStudent;
    }

    public void setValidateKualiStudent(boolean validateKualiStudent) {
        this.validateKualiStudent = validateKualiStudent;
    }

    @Override
    public List<ServiceMethod> getServiceMethods() {
        if (this.serviceMethods == null) {
            this.parse();
        }
        return this.serviceMethods;
    }

    @Override
    public List<String> getSourceNames() {
        List<String> list = new ArrayList<String>(this.sourceDirectories.size());
        for (String javaFile : this.sourceDirectories) {
            list.add(javaFile);
        }
        return list;
    }

    @Override
    public List<Service> getServices() {
        if (services == null) {
            this.parse();
        }
        return services;
    }

    @Override
    public List<XmlType> getXmlTypes() {
        if (xmlTypeMap == null) {
            this.parse();
        }
        return new ArrayList<XmlType>(xmlTypeMap.values());
    }

    @Override
    public List<MessageStructure> getMessageStructures() {
        if (messageStructures == null) {
            this.parse();
        }
        return this.messageStructures;
    }

    private void checkIfExists(String sourceDirectory) {
        File file = new File(sourceDirectory);
        if (!file.isDirectory()) {
            throw new IllegalArgumentException(sourceDirectory + " is not a directory on disk");
        }
    }

    @SuppressWarnings("unchecked")
	private void parse() {
//  System.out.println ("ServiceContractModelQDoxLoader: Starting parse");
        services = new ArrayList<Service>();
        serviceMethods = new ArrayList<ServiceMethod>();
        xmlTypeMap = new LinkedHashMap<String, XmlType>();
        messageStructures = new ArrayList<MessageStructure>();
        
        DefaultDocletTagFactory dtf = new DefaultDocletTagFactory();
        
		JavaDocBuilder builder = new JavaDocBuilder(dtf);
        
        for (String sourceDirectory : sourceDirectories) {
            checkIfExists(sourceDirectory);
            builder.addSourceTree(new File(sourceDirectory));
        }
        Set<JavaClass> mergedClasses = new LinkedHashSet<JavaClass>();
        
        for (JavaClass javaClass : builder.getClasses()) {
            
            if (!javaClass.getPackageName().contains("r1"))
                mergedClasses.add(javaClass);
            else
                log.warn("excluding r1 class: " + javaClass.getFullyQualifiedName());
            
        }
        
        List<JavaClass>sortedClasses = new ArrayList<JavaClass>(mergedClasses);
        
        Collections.sort(sortedClasses);
        
        for (JavaClass javaClass : sortedClasses) {
            if (!this.isServiceToProcess(javaClass)) {
                continue;
            }
//   System.out.println ("processing service=" + javaClass.getName ());
            Service service = new Service();
            services.add(service);
            service.setKey(calcServiceKey (javaClass));
            service.setName(javaClass.getName());
            service.setComments(this.calcComment(javaClass));
            service.setUrl(this.calcServiceUrl(javaClass));
            service.setVersion(this.calcVersion(javaClass));
            service.setStatus("???");
            service.setIncludedServices(calcIncludedServices(javaClass));
            service.setImplProject(javaClass.getPackageName());

//   for (DocletTag tag : javaClass.getTags ())
//   {
//    System.out.println ("ServiceContractModelQDoxLoader: Class: "
//                        + javaClass.getName () + " has tag=" + dump (
//      tag));
//   }
            
            JavaMethod[]  methods = getServiceMethods (javaClass);  
            for (JavaMethod javaMethod : methods) {

                ServiceMethod serviceMethod = new ServiceMethod();
                serviceMethods.add(serviceMethod);
                serviceMethod.setService(service.getKey());
                serviceMethod.setName(javaMethod.getName());
                serviceMethod.setDescription(calcMissing(javaMethod.getComment()));
                serviceMethod.setParameters(new ArrayList<ServiceMethodParameter>());
                serviceMethod.setImplNotes(calcImplementationNotes(javaMethod));
                serviceMethod.setDeprecated(isDeprecated(javaMethod));
//    for (DocletTag tag : javaMethod.getTags ())
//    {
//     System.out.println ("ServiceContractModelQDoxLoader: Method: "
//                         + service.getName () + "."
//                         + javaMethod.getName ()
//                         + " has tag=" + dump (tag));
//    }
                // parameters
                for (JavaParameter parameter : javaMethod.getParameters()) {
                    ServiceMethodParameter param = new ServiceMethodParameter();
                    serviceMethod.getParameters().add(param);
                    param.setName(parameter.getName());
                    param.setType(calcType(parameter.getType()));
                    param.setDescription(calcMissing(
                            calcParameterDescription(javaMethod,
                            param.getName())));
                    try {
						addXmlTypeAndMessageStructure(calcRealJavaClass(parameter.getType()),
						        serviceMethod.getService());
					} catch (Exception e) {
						String message= "failed to parameter message structure: " + serviceMethod.getService() + " : " + parameter.getType();
						
						log.error (message + " : " + e.getMessage());
						log.debug(message, e);
					}
                }
                // errors
                serviceMethod.setErrors(new ArrayList<ServiceMethodError>());
                for (Type exception : javaMethod.getExceptions()) {
                    ServiceMethodError error = new ServiceMethodError();
                    error.setType(this.calcType(exception.getJavaClass()));
                    error.setDescription(calcMissing(
                            calcExceptionDescription(javaMethod,
                            error.getType())));
                    error.setPackageName(exception.getJavaClass().getPackageName());
                    error.setClassName(exception.getJavaClass().getName());
                    serviceMethod.getErrors().add(error);
                }
                // return values
                ServiceMethodReturnValue rv = new ServiceMethodReturnValue();
                serviceMethod.setReturnValue(rv);
                Type returnType = null;
                try {
                    returnType = javaMethod.getReturnType();
                } catch (NullPointerException ex) {
                    log.error("Nullpointer getting return type: " + javaMethod.getCallSignature());
                    returnType = null;
                }

                rv.setType(calcType(returnType));
                rv.setDescription(calcMissing(this.calcReturnDescription(javaMethod)));
                if (returnType != null) {
                    try {
                    	
                    	
						addXmlTypeAndMessageStructure(calcRealJavaClass(returnType),
						        serviceMethod.getService());
					} catch (Exception e) {
						String message = "failed to parse return type message structure: " + serviceMethod.getService() + " : " + returnType;
						
						log.error (message + " : " + e.getMessage());
						log.debug(message, e);
					}
                }
            }
        }
    }

    
    private String calcServiceKey(JavaClass javaClass) {
        String key = javaClass.getName();
        if (key.endsWith ("Service")) {
         key = key.substring(0, key.length() - "Service".length());
        }
        if (javaClass.getPackageName().startsWith("org.kuali.rice.")) {
            log.warn("WARNING " + " changing servkey for the RICE services to include prefix "
                    + " to disambiguate with Kuali Student StateService See Jira KSENROLL-2892");
            key = "RICE." + key;
        }
        return key;
    }
    
    private JavaMethod[] getServiceMethods(JavaClass javaClass) {
		
    	Set<JavaMethod>methods = new LinkedHashSet<JavaMethod>();
		
		/*
		 * As inheritence is useful from a technical level but not as much from a business level
		 * This lets the union of the methods from all of the interfaces be listed in the contract.
		 */
		JavaClass[] interfaces = javaClass.getImplementedInterfaces();
		
		for (JavaClass intfc : interfaces) {
			
			if (!isAService(intfc)) {
				// only add the methods if this is not a service
				// e.g. extends ServiceBusinessLogic
				for (JavaMethod javaMethod : intfc.getMethods()) {
				
					methods.add(javaMethod);
				}
			}
			
		}
		
		// add the main service methods last incase we override any from the parent interfaces.
		// note the heirarchy is only guaranteed relative to the target service class (if there are two levels or more of 
		// heirarchy there is no guarantee the method ordering will be correct).
		for (JavaMethod javaMethod : javaClass.getMethods()) {
			
			methods.add(javaMethod);
		}
		
		return methods.toArray(new JavaMethod[] {});
	}

	private boolean isServiceToProcess(JavaClass javaClass) {
//  System.out.println ("looking if javaClass is a service to process=" + javaClass.getName () + "=" + javaClass.getPackageName ());
    	
        if (!javaClass.getName().endsWith("Service")) {
            return false;
        }
        if (javaClass.getPackageName().contains(".old.")) {

            return false;
        }
        if (javaClass.getPackageName().endsWith(".old")) {
            return false;
        }
        for (Annotation annotation : javaClass.getAnnotations()) {
//   System.out.println ("looking for webservice tag=" + annotation.getType ().getJavaClass ().getName ());
            if (annotation.getType().getJavaClass().getName().equals("WebService")) {
//    System.out.println ("Processing web service=" + javaClass.getPackageName ()
//                        + "." + javaClass.getName ());
                return true;
            }
        }
        // This includes RICE's business object services even though they are not web services
        // because often they are the only real service they have exposed and it helps to document
        // them to see what the data really is like
        if (javaClass.getName().endsWith("BoService")) {
            return true;
        }
        // This includes KSA's internal services even though they are not web services
        if (javaClass.getPackageName().startsWith("com.sigmasys.kuali.ksa.service")) {
            return true;
        }
        if (javaClass.getPackageName().startsWith("org.kuali.mobility")) {
            return true;
        }
//  System.out.println ("skipping service because it is not a web service="
//                      + javaClass.getPackageName () + "." + javaClass.getName ());
        return false;
    }

    private List<String> calcIncludedServices(JavaClass javaClass) {
        List<String> includedServices = new ArrayList<String>();
        for (JavaClass interfaceClass : javaClass.getImplementedInterfaces()) {
            if (isAService(interfaceClass)) {
//            System.out.println("ServiceContractModelQDoxLoader:" + javaClass.getName()
//                    + " implements " + interfaceClass.getName());
                includedServices.add(interfaceClass.getName());
            }
        }
        return includedServices;
    }

    private boolean isAService(JavaClass interfaceClass) {
        if (interfaceClass.getName().endsWith("Service")) {
            return true;
        }
        return false;
    }

    private String calcParameterDescription(JavaMethod method,
            String parameterName) {
        for (DocletTag tag : method.getTags()) {
            if (tag.getName().equals("param")) {
                if (tag.getValue().startsWith(parameterName + " ")) {
                    return tag.getValue().substring(parameterName.length() + 1);
                }
            }
        }
        return null;
    }

    private String calcExceptionDescription(JavaMethod serviceMethod,
            String exceptionType) {
        for (DocletTag tag : serviceMethod.getTags()) {
            if (tag.getName().equals("throws")) {
                if (tag.getValue().startsWith(exceptionType + " ")) {
                    return tag.getValue().substring(exceptionType.length() + 1);
                }
            }
        }
        return null;
    }

    private String calcReturnDescription(JavaMethod serviceMethod) {
        for (DocletTag tag : serviceMethod.getTags()) {
            if (tag.getName().equals("return")) {
                return tag.getValue();
            }
        }
        return null;
    }

    private String calcServiceUrl(JavaClass serviceClass) {
        for (DocletTag tag : serviceClass.getTags()) {
            if (tag.getName().equals("See")) {
                return tag.getValue();
            }
        }
        return null;
    }

    private void addXmlTypeAndMessageStructure(JavaClass messageStructureJavaClass,
            String serviceKey) {
        String name = calcType(messageStructureJavaClass);
        if (name.endsWith("CallbackService")) {
            return;
        }
        XmlType xmlType = xmlTypeMap.get(name);
        if (xmlType == null) {
            xmlType = new XmlType();
            xmlTypeMap.put(name, xmlType);
            xmlType.setName(name);
            xmlType.setDesc(this.calcMessageStructureDesc(messageStructureJavaClass));
            xmlType.setDeprecated(isDeprecated (messageStructureJavaClass));
            xmlType.setService(serviceKey);
            xmlType.setVersion("IGNORE -- SAME AS SERVICE");
            xmlType.setPrimitive(calcPrimitive(messageStructureJavaClass));
            xmlType.setJavaPackage(calcJavaPackage(messageStructureJavaClass));
            if (xmlType.getPrimitive().equals(XmlType.COMPLEX)) {
                addMessageStructure(messageStructureJavaClass, serviceKey);
            }

        } else {
            addServiceToList(xmlType, serviceKey);
        }
    }

    private boolean isDeprecated(JavaClass javaClass) {
        for (Annotation annotation : javaClass.getAnnotations()) {
            if (annotation.getType().getJavaClass().getName().equals(
                    "Deprecated")) {
                return true;
            }
        }
        return false;
    }

    private String calcJavaPackage(JavaClass javaClass) {
        String packageName = javaClass.getPackageName();
        return packageName;
    }

    private String calcMessageStructureDesc(JavaClass javaClass) {
        {
            String desc = javaClass.getComment();
            if (desc != null) {
                if (!desc.isEmpty()) {
                    return desc;
                }
            }
            JavaClass infcClass = this.getMatchingInfc(javaClass);
            if (infcClass == null) {
                return null;
            }
            return infcClass.getComment();
        }
    }

    private JavaClass getMatchingInfc(JavaClass javaClass) {
        // ks uses this pattern
        String nameInfc = javaClass.getName();
        if (nameInfc.endsWith("Info")) {
            nameInfc = nameInfc.substring(0, nameInfc.length() - "Info".length())
                    + "Infc";
        }
        String nameWithOutInfo = javaClass.getName();
        // rice uses this pattern
        if (nameWithOutInfo.endsWith("Info")) {
            nameWithOutInfo = nameWithOutInfo.substring(0, nameWithOutInfo.length()
                    - "Info".length());
        }
        for (JavaClass infc : javaClass.getImplementedInterfaces()) {
            if (infc.getName().equals(nameInfc)) {
//                System.out.println("found matching interface " + infc.getName());
                return infc;
            }
            if (infc.getName().equals(nameWithOutInfo)) {
                return infc;
            }
        }
        return null;
    }

    private String calcPrimitive(JavaClass javaClass) {
        if (this.isComplex(javaClass)) {
            return XmlType.COMPLEX;
        }
        return "Primitive";
    }

    private String initLower(String str) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return str;
        }
        if (str.length() == 1) {
            return str.toLowerCase();
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    private String initUpper(String str) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return str;
        }
        if (str.length() == 1) {
            return str.toUpperCase();
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private Set<String> getShortNames(JavaClass messageStructureJavaClass) {
        Set<String> fields = getFieldsUsingPropOrder(messageStructureJavaClass);
        if (fields != null) {
            return fields;
        }
        fields = new LinkedHashSet<String>();
        for (JavaMethod method : messageStructureJavaClass.getMethods(true)) {
            if (isSetterMethodToProcess(method, messageStructureJavaClass.getName())) {
                String shortName = this.calcShortNameFromSetter(method);
                fields.add(shortName);
                continue;
            }
            if (isGetterMethodToProcess(method, messageStructureJavaClass.getName())) {
                String shortName = this.calcShortNameFromGetter(method);
                fields.add(shortName);
                continue;
            }
        }
        return fields;
    }

    private Set<String> getFieldsUsingPropOrder(
            JavaClass messageStructureJavaClass) {
        for (Annotation annotation : messageStructureJavaClass.getAnnotations()) {
            if (annotation.getType().getJavaClass().getName().equals("XmlType")) {
                AnnotationValue propOrderParam = annotation.getProperty("propOrder");
                if (propOrderParam == null) {
                    continue;
                }
                Object propOrderValue = propOrderParam.getParameterValue();
                if (!(propOrderValue instanceof List)) {
                    continue;
                }
                Set<String> fields = new LinkedHashSet<String>();
                for (Object value : (List<?>) propOrderValue) {
                    if (value instanceof String) {
                        String shortName = (String) value;
                        shortName = this.stripQuotes(shortName);
                        if (shortName.contains(".Elements.")) {
                            String newShortName = getShortNameFromElements(shortName, messageStructureJavaClass);
                            if (newShortName == null) {
                                continue;
                            }
                            shortName = newShortName;
                        } else if (shortName.startsWith("CoreConstants.CommonElements.")) {
                            String newShortName = getCoreConstants(shortName);
                            if (newShortName == null) {
                                continue;
                            }
                            shortName = newShortName;
                        }
                        if (shortName.equals("_futureElements")) {
                            continue;
                        }
                        shortName = this.initUpper(shortName);
                        fields.add(shortName);
                    }
                }
                return fields;
            }
        }
        return null;
    }

    private String getShortNameFromElements(String shortName, JavaClass messageStructureJavaClass) {
        JavaClass elementsJavaClass = messageStructureJavaClass.getNestedClassByName("Elements");
        if (elementsJavaClass == null) {
            return null;
        }
        String fieldName = shortName.substring(shortName.indexOf(".Elements.") + ".Elements.".length());
        JavaField field = elementsJavaClass.getFieldByName(fieldName);
        String initExpr = field.getInitializationExpression();
        return stripQuotes(initExpr);
    }

    private String getCoreConstants(String shortName) {
        if (shortName.endsWith("VERSION_NUMBER")) {
            return "versionNumber";
        }
        if (shortName.endsWith("OBJECT_ID")) {
            return "objectId";
        }
        if (shortName.endsWith("ACTIVE")) {
            return "active";
        }
        if (shortName.endsWith("ACTIVE_FROM_DATE")) {
            return "activeFromDate";
        }
        if (shortName.endsWith("ACTIVE_TO_DATE")) {
            return "activeToDate";
        }
        if (shortName.endsWith("ATTRIBUTES")) {
            return "attributes";
        }
        if (shortName.endsWith("FUTURE_ELEMENTS")) {
            return "_futureElements";
        }
        throw new RuntimeException("Unknown shortName " + shortName);
    }

    private void addMessageStructure(JavaClass messageStructureJavaClass,
            String serviceKey) {
        Set<JavaClass> subObjectsToAdd = new LinkedHashSet<JavaClass>();
        for (String shortName : this.getShortNames(messageStructureJavaClass)) {
            JavaMethod setterMethod = findSetterMethod(messageStructureJavaClass,
                    shortName);
            JavaMethod getterMethod = findGetterMethod(messageStructureJavaClass,
                    shortName);
            if (getterMethod == null) {
                if (this.validateKualiStudent) {
                    throw new IllegalArgumentException("shortName has no corresponding getter method: "
                            + messageStructureJavaClass.getFullyQualifiedName()
                            + "." + shortName);
                }
            }
            JavaField beanField = this.findField(messageStructureJavaClass,
                    shortName, setterMethod);
            if (beanField == null) {
                String accessorType = getAccessorType(getterMethod);
                if ("XmlAccessType.FIELD".equals(accessorType)) {
                    throw new IllegalArgumentException("Setter method has no corresponding bean field: "
                            + messageStructureJavaClass.getName()
                            + "." + getterMethod.getName());
                }
            }
            // overide the shortName if the bean field has an XmlAttribute name=xxx
            // this catches the key=id switch
            if (beanField != null) {
                for (Annotation annotation : beanField.getAnnotations()) {
                    if (annotation.getType().getJavaClass().getName().equals("XmlAttribute")) {
                        Object nameValue = annotation.getNamedParameter("name");
                        if (nameValue != null) {
                            shortName = stripQuotes(nameValue.toString());
                        }
                    }
                }
            }
            shortName = initLower(shortName);
            MessageStructure ms = new MessageStructure();
            messageStructures.add(ms);
            ms.setXmlObject(messageStructureJavaClass.getName());
            ms.setShortName(shortName);
            ms.setId(ms.getXmlObject() + "." + ms.getShortName());
            ms.setName(calcMissing(calcName(messageStructureJavaClass, getterMethod, setterMethod,
                    beanField, shortName)));
            ms.setType(calcType(messageStructureJavaClass, getterMethod, setterMethod, beanField, shortName));
            if (ms.getType().equals("Object")) {
                log.warn(ms.getId()
                        + " has Object as it's type ==> Changing to String");
                ms.setType("String");
            } else if (ms.getType().equals("ObjectList")) {
                log.warn(ms.getId()
                        + " has a list of Objects as it's type ==> Changing to List of String");
                ms.setType("StringList");
            }
            ms.setXmlAttribute(this.calcXmlAttribute(beanField));
            ms.setRequired(calcRequired(getterMethod, setterMethod, beanField));
            ms.setReadOnly(calcReadOnly(getterMethod, setterMethod, beanField));
            ms.setCardinality(this.calcCardinality(messageStructureJavaClass, getterMethod, setterMethod, beanField, shortName));
            ms.setDescription(calcMissing(calcDescription(messageStructureJavaClass, getterMethod, setterMethod,
                    beanField, shortName)));
            ms.setImplNotes(calcImplementationNotes(getterMethod, setterMethod, beanField));
            ms.setDeprecated(isDeprecated(getterMethod));
            ms.setStatus("???");
            ms.setLookup(calcLookup (messageStructureJavaClass, getterMethod, setterMethod,
                    beanField, serviceKey, ms.getXmlObject(), shortName, ms.getType()));
            ms.setPrimaryKey(calcPrimaryKey (messageStructureJavaClass, getterMethod, setterMethod,
                    beanField, serviceKey, ms.getXmlObject(), shortName, ms.getType()));
            ms.setOverriden(this.calcOverridden(messageStructureJavaClass, getterMethod));
            JavaClass subObjToAdd = this.calcRealJavaClassOfGetterReturn(getterMethod);
            if (subObjToAdd != null) {
//                if (!subObjToAdd.isEnum()) {
                if (!subObjToAdd.getName().equals("Object")) {
                    if (!subObjToAdd.getName().equals("LocaleKeyList")) {
                        if (!subObjToAdd.getName().equals("MessageGroupKeyList")) {
                            subObjectsToAdd.add(subObjToAdd);
                        }
                    }
                }
//                }
            }
        }
        // now add all it's complex sub-objects if they haven't already been added
        for (JavaClass subObjectToAdd : subObjectsToAdd) {
            XmlType xmlType = xmlTypeMap.get(calcType(subObjectToAdd));
            if (xmlType == null) {
                try {
					addXmlTypeAndMessageStructure(subObjectToAdd, serviceKey);
				} catch (Exception e) {
					String message = "failed to parse subobject structure: " + subObjectToAdd + " : " + serviceKey;
					// log into message
					log.error (message + " : " + e.getMessage());
					// log into debug log
					log.debug(message, e);
				}
            } else {
                addServiceToList(xmlType, serviceKey);
            }
        }
        return;
    }

    private String stripList (String type) {
        if (type == null) {
            return null;
        }
        if (type.endsWith("List")) {
            return type.substring(0, type.length() - "List".length());
        }
        return type;
    }
    
    
    private boolean calcPrimaryKey (JavaClass mainClass, JavaMethod getterMethod,
            JavaMethod setterMethod, JavaField beanField, String serviceKey, String xmlObject, String shortName, String type) {
        if (!type.equalsIgnoreCase ("String")) {
            return false;
        }
        if (shortName.equalsIgnoreCase ("Id")) {
            return true;
        }
        if (shortName.equalsIgnoreCase ("Key")) {
            return true;
        }
        // Special fix up for principal
//        log.warn(serviceKey + ":" + xmlObject + "." + shortName);
        if (xmlObject.equalsIgnoreCase("Principal")) {
            if (shortName.equalsIgnoreCase("principalId")) {
                return true;
            }
        }
        return false;
    }
    

    private Lookup calcLookup (JavaClass mainClass, JavaMethod getterMethod,
            JavaMethod setterMethod, JavaField beanField, String serviceKey, String xmlObject, String shortName, String type) {
        type = this.stripList(type);
        // all keys and Ids are represented as strings
        if (!type.equalsIgnoreCase ("String")) {
            return null;
        }
        Lookup lookup = LOOKUP_MAPPINGS.get(shortName);
        if (lookup == null) {
            if (this.endsWithIdOrKey(shortName)) {
                log.warn (serviceKey + ":" + xmlObject + "." + shortName + " ends with id or key but has no lookup defined");
            } 
        }
        return lookup;
    }
    
    private boolean endsWithIdOrKey (String shortName) {
        if (shortName.equals ("Id")) {
            return false;
        }
        if (shortName.equals ("Key")) {
            return false;
        }
        if (shortName.endsWith ("Id")) {
            return true;
        }
        if (shortName.endsWith ("Ids")) {
            return true;
        }
        if (shortName.endsWith ("Key")) {
            return true;
        }
        if (shortName.endsWith ("Keys")) {
            return true;
        }
        return false;
    }
    
    private static Map<String, Lookup> LOOKUP_MAPPINGS;
    
    {
        // global ones where the name matches the object 
        LOOKUP_MAPPINGS = new LinkedHashMap<String, Lookup> ();
        LOOKUP_MAPPINGS.put("typeKey", new Lookup ("Type", "TypeInfo"));
        LOOKUP_MAPPINGS.put("stateKey", new Lookup ("State", "StateInfo"));
        LOOKUP_MAPPINGS.put("lifecycleKey", new Lookup ("State", "LifecycleInfo"));
        LOOKUP_MAPPINGS.put("orgId", new Lookup ("Organization", "OrgInfo"));
        LOOKUP_MAPPINGS.put("orgIds", new Lookup ("Organization", "OrgInfo"));
        LOOKUP_MAPPINGS.put("organizationId", new Lookup ("Organization", "OrgInfo"));
        LOOKUP_MAPPINGS.put("organizationIds", new Lookup ("Organization", "OrgInfo"));
        LOOKUP_MAPPINGS.put("atpId", new Lookup ("Atp", "AtpInfo"));
        LOOKUP_MAPPINGS.put("atpIds", new Lookup ("Atp", "AtpInfo"));
        LOOKUP_MAPPINGS.put("termId", new Lookup ("AcademicCalendar", "TermInfo"));
        LOOKUP_MAPPINGS.put("termIds", new Lookup ("AcademicCalendar", "TermInfo"));
        LOOKUP_MAPPINGS.put("luiId", new Lookup ("Lui", "LuiInfo"));
        LOOKUP_MAPPINGS.put("luiIds", new Lookup ("Lui", "LuiInfo"));
        LOOKUP_MAPPINGS.put("cluId", new Lookup ("Clu", "CluInfo"));
        LOOKUP_MAPPINGS.put("cluIds", new Lookup ("Clu", "CluInfo"));
        LOOKUP_MAPPINGS.put("credentialProgramId", new Lookup ("Program", "CredentialProgramInfo"));
        LOOKUP_MAPPINGS.put("credentialProgramIds", new Lookup ("Program", "CredentialProgramInfo"));
        LOOKUP_MAPPINGS.put("credentialProgramId", new Lookup ("Program", "CredentialProgramInfo"));
        LOOKUP_MAPPINGS.put("credentialProgramIds", new Lookup ("Program", "CredentialProgramInfo"));
        LOOKUP_MAPPINGS.put("coreProgramId", new Lookup ("Program", "CoreProgramInfo"));
        LOOKUP_MAPPINGS.put("coreProgramIds", new Lookup ("Program", "CoreProgramInfo"));
        LOOKUP_MAPPINGS.put("resultScaleKey", new Lookup ("LRC", "ResultScaleInfo"));
        LOOKUP_MAPPINGS.put("resultScaleKeys", new Lookup ("LRC", "ResultScaleInfo"));
        LOOKUP_MAPPINGS.put("resultValuesGroupKey", new Lookup ("LRC", "ResultValuesGroupInfo"));
        LOOKUP_MAPPINGS.put("resultValuesGroupKeys", new Lookup ("LRC", "ResultValuesGroupInfo"));
        LOOKUP_MAPPINGS.put("resultValueKey", new Lookup ("LRC", "ResultValueInfo"));
        LOOKUP_MAPPINGS.put("resultValueKeys", new Lookup ("LRC", "ResultValueInfo"));
        LOOKUP_MAPPINGS.put("scheduleId", new Lookup ("Schedule", "ScheduleInfo"));
        LOOKUP_MAPPINGS.put("scheduleIds", new Lookup ("Schedule", "ScheduleInfo"));
        LOOKUP_MAPPINGS.put("courseId", new Lookup ("Course", "CourseInfo"));
        LOOKUP_MAPPINGS.put("courseIds", new Lookup ("Course", "CourseInfo"));
        LOOKUP_MAPPINGS.put("formatId", new Lookup ("Course", "FormatInfo"));
        LOOKUP_MAPPINGS.put("formatIds", new Lookup ("Course", "FormatInfo"));
        LOOKUP_MAPPINGS.put("activityId", new Lookup ("Course", "ActivityInfo"));
        LOOKUP_MAPPINGS.put("activityIds", new Lookup ("Course", "ActivityInfo"));
        LOOKUP_MAPPINGS.put("courseOfferingId", new Lookup ("CourseOffering", "CourseOfferingInfo"));
        LOOKUP_MAPPINGS.put("courseOfferingIds", new Lookup ("CourseOffering", "CourseOfferingInfo"));
        LOOKUP_MAPPINGS.put("formatOfferingId", new Lookup ("CourseOffering", "FormatOfferingInfo"));
        LOOKUP_MAPPINGS.put("formatOfferingIds", new Lookup ("CourseOffering", "FormatOfferingInfo"));
        LOOKUP_MAPPINGS.put("activityOfferingId", new Lookup ("CourseOffering", "ActivityOfferingInfo"));
        LOOKUP_MAPPINGS.put("activityOfferingIds", new Lookup ("CourseOffering", "ActivityOfferingInfo"));
        LOOKUP_MAPPINGS.put("socRolloverResultId", new Lookup ("CourseOfferingSet", "SorRolloverResultInfo"));
        LOOKUP_MAPPINGS.put("socRolloverResultIds", new Lookup ("CourseOfferingSet", "SorRolloverResultInfo"));
        LOOKUP_MAPPINGS.put("socRolloverResultItemId", new Lookup ("CourseOfferingSet", "SorRolloverResultItemInfo"));
        LOOKUP_MAPPINGS.put("socRolloverResultItemIds", new Lookup ("CourseOfferingSet", "SorRolloverResultItemInfo"));
        LOOKUP_MAPPINGS.put("buildingId", new Lookup ("Room", "BuildingInfo"));
        LOOKUP_MAPPINGS.put("buildingIds", new Lookup ("Room", "BuildingInfo"));
        LOOKUP_MAPPINGS.put("roomId", new Lookup ("Room", "RoomInfo"));
        LOOKUP_MAPPINGS.put("roomIds", new Lookup ("Room", "RoomInfo"));
        LOOKUP_MAPPINGS.put("populationId", new Lookup ("Population", "PopulationInfo"));
        LOOKUP_MAPPINGS.put("populationIds", new Lookup ("Population", "PopulationInfo"));
        
        
        // COMMON RICE IDENTITY SERVICE
        LOOKUP_MAPPINGS.put("principalId", new Lookup ("rice.kim.Identity", "Principal"));
        LOOKUP_MAPPINGS.put("principalIds", new Lookup ("rice.kim.Identity", "Principal"));
        // TODO: fix these Ids that currently maps to principal instead of the entity id
        LOOKUP_MAPPINGS.put("personId", new Lookup ("rice.kim.Identity", "Principal"));
        LOOKUP_MAPPINGS.put("personIds", new Lookup ("rice.kim.Identity", "Principal"));
        LOOKUP_MAPPINGS.put("instructorId", new Lookup ("rice.kim.Identity", "Principal"));
        LOOKUP_MAPPINGS.put("instructorIds", new Lookup ("rice.kim.Identity", "Principal"));
        LOOKUP_MAPPINGS.put("studentId", new Lookup ("rice.kim.Identity", "Principal"));
        LOOKUP_MAPPINGS.put("studentIds", new Lookup ("rice.kim.Identity", "Principal"));
        
        // Common objects 
        // TimeAmount
        LOOKUP_MAPPINGS.put("atpDurationTypeKey", new Lookup ("Type", "TypeInfo")); 
        LOOKUP_MAPPINGS.put("currencyTypeKey", new Lookup ("Type", "TypeInfo"));        
        // Context
        LOOKUP_MAPPINGS.put("authenticatedPrincipalId", new Lookup ("rice.kim.Identity", "Principal"));
        // meta
        LOOKUP_MAPPINGS.put("createId", new Lookup ("rice.kim.Identity", "Principal"));
        LOOKUP_MAPPINGS.put("updateId", new Lookup ("rice.kim.Identity", "Principal"));
        LOOKUP_MAPPINGS.put("agendaId", new Lookup ("rice.krms.Agenda", "Agenda"));
        LOOKUP_MAPPINGS.put("agendaIds", new Lookup ("rice.krms.Agenda", "Agenda"));
        LOOKUP_MAPPINGS.put("", new Lookup ("", ""));
        LOOKUP_MAPPINGS.put("", new Lookup ("", ""));
        LOOKUP_MAPPINGS.put("", new Lookup ("", ""));
        LOOKUP_MAPPINGS.put("", new Lookup ("", ""));
        LOOKUP_MAPPINGS.put("", new Lookup ("", ""));
        LOOKUP_MAPPINGS.put("", new Lookup ("", ""));
        LOOKUP_MAPPINGS.put("", new Lookup ("", ""));
        
        
        // TODO: replace or augment this special list of ones with annotations in the contract itself
        // program service
        LOOKUP_MAPPINGS.put("unitsContentOwner", new Lookup ("Organization", "OrgInfo"));
        LOOKUP_MAPPINGS.put("unitsDeployment", new Lookup ("Organization", "OrgInfo"));
        LOOKUP_MAPPINGS.put("unitsStudentOversight", new Lookup ("Organization", "OrgInfo"));
        LOOKUP_MAPPINGS.put("unitsFinancialResources", new Lookup ("Organization", "OrgInfo"));
        LOOKUP_MAPPINGS.put("unitsFinancialControl", new Lookup ("Organization", "OrgInfo"));
        LOOKUP_MAPPINGS.put("divisionsContentOwner", new Lookup ("Organization", "OrgInfo"));
        LOOKUP_MAPPINGS.put("divisionsDeployment", new Lookup ("Organization", "OrgInfo"));
        LOOKUP_MAPPINGS.put("divisionsStudentOversight", new Lookup ("Organization", "OrgInfo"));
        LOOKUP_MAPPINGS.put("divisionsFinancialResources", new Lookup ("Organization", "OrgInfo"));
        LOOKUP_MAPPINGS.put("divisionsFinancialControl", new Lookup ("Organization", "OrgInfo"));
        LOOKUP_MAPPINGS.put("startTerm", new Lookup ("AcademicCalendar", "TermInfo"));
        LOOKUP_MAPPINGS.put("endTerm", new Lookup ("AcademicCalendar", "TermInfo"));
        LOOKUP_MAPPINGS.put("endProgramEntryTerm", new Lookup ("AcademicCalendar", "TermInfo"));
        LOOKUP_MAPPINGS.put("resultOptions", new Lookup ("LRC", "ResultValuesGroupInfo"));
        LOOKUP_MAPPINGS.put("programRequirements", new Lookup ("Program", "ProgramRequirementInfo"));
        // share by program and course
        LOOKUP_MAPPINGS.put("parentRelType", new Lookup ("Type", "TypeInfo"));
        LOOKUP_MAPPINGS.put("parentLoRelationid", new Lookup ("LearningObjective", "LoInfo"));
        
        // type service
        LOOKUP_MAPPINGS.put("ownerTypeKey", new Lookup ("Type", "TypeInfo"));
        LOOKUP_MAPPINGS.put("relatedTypeKey", new Lookup ("Type", "TypeInfo"));
        
        // State service (there are no special purpose ones) 
        
        // LRC service (there are no special purpose ones)
        
        // atp
        LOOKUP_MAPPINGS.put("adminOrgId", new Lookup ("Organization", "OrgInfo"));
        LOOKUP_MAPPINGS.put("relatedAtpId", new Lookup ("Atp", "AtpInfo"));
        LOOKUP_MAPPINGS.put("relativeAnchorMilestoneId", new Lookup ("Atp", "MilestoneInfo"));
        
        // Lui
        LOOKUP_MAPPINGS.put("relatedLuiTypes", new Lookup ("Type", "TypeInfo"));
        LOOKUP_MAPPINGS.put("relatedLuiId", new Lookup ("Lui", "LuiInfo"));
        
        // Course Offering
        LOOKUP_MAPPINGS.put("", new Lookup ("", ""));
        LOOKUP_MAPPINGS.put("", new Lookup ("", ""));
        
        // TODO: finish the services
        LOOKUP_MAPPINGS.put("unitsDeploymentOrgIds", new Lookup ("Organization", "OrgInfo"));
        LOOKUP_MAPPINGS.put("unitsContentOwnerOrgIds", new Lookup ("Organization", "OrgInfo"));
        LOOKUP_MAPPINGS.put("unitsContentOwnerId", new Lookup ("Organization", "OrgInfo"));
        LOOKUP_MAPPINGS.put("jointOfferingIds", new Lookup ("CourseOffering", "CourseOfferingInfo"));
        LOOKUP_MAPPINGS.put("gradingOptionId", new Lookup ("LRC", "ResultValuesGroupInfo"));
        LOOKUP_MAPPINGS.put("creditOptionId", new Lookup ("LRC", "ResultValuesGroupInfo"));
        LOOKUP_MAPPINGS.put("waitlistLevelTypeKey", new Lookup ("Type", "TypeInfo"));
        LOOKUP_MAPPINGS.put("waitlistTypeKey", new Lookup ("Type", "TypeInfo"));
        LOOKUP_MAPPINGS.put("activityOfferingTypeKeys", new Lookup ("Type", "TypeInfo"));
        LOOKUP_MAPPINGS.put("gradeRosterLevelTypeKey", new Lookup ("Type", "TypeInfo"));
        LOOKUP_MAPPINGS.put("finalExamLevelTypeKey", new Lookup ("Type", "TypeInfo"));
        LOOKUP_MAPPINGS.put("schedulingStateKey", new Lookup ("State", "StateInfo"));
        LOOKUP_MAPPINGS.put("gradingOptionKeys", new Lookup ("LRC", "ResultValuesGroupInfo"));
        LOOKUP_MAPPINGS.put("creditOptionId", new Lookup ("LRC", "ResultValuesGroupInfo"));
        LOOKUP_MAPPINGS.put("gradingOptionKeys", new Lookup ("", ""));
        LOOKUP_MAPPINGS.put("", new Lookup ("", ""));
        LOOKUP_MAPPINGS.put("", new Lookup ("", ""));
        LOOKUP_MAPPINGS.put("", new Lookup ("", ""));
        LOOKUP_MAPPINGS.put("", new Lookup ("", ""));
        LOOKUP_MAPPINGS.put("", new Lookup ("", ""));
        LOOKUP_MAPPINGS.put("", new Lookup ("", ""));
        LOOKUP_MAPPINGS.put("", new Lookup ("", ""));
        LOOKUP_MAPPINGS.put("", new Lookup ("", ""));
        LOOKUP_MAPPINGS.put("", new Lookup ("", ""));
        LOOKUP_MAPPINGS.put("", new Lookup ("", ""));
        LOOKUP_MAPPINGS.put("", new Lookup ("", ""));
        
    }
    
    private boolean calcOverridden(JavaClass mainClass, JavaMethod getterMethod) {
        if (getterMethod == null) {
            return false;
        }
        JavaMethod infcGetter = null;
        if (getterMethod.getParentClass().isInterface()) {
            infcGetter = getterMethod;
        }
        if (infcGetter == null) {
            infcGetter = findInterfaceMethod(mainClass, getterMethod, false);
        }
        if (infcGetter == null) {
            return false;
        }
        Annotation annotation = this.getAnnotation(infcGetter, null, null, "Override");
        if (annotation != null) {
            return true;
        }
        return false;
    }

    private String calcComment(JavaClass javaClass) {
        return this.calcComment(javaClass.getComment());
    }

    private String calcComment(String comment) {
        return this.parseCommentVersion(comment)[0];
    }

    private String calcVersion(JavaClass javaClass) {
        DocletTag tag = javaClass.getTagByName("version", true);
        if (tag != null) {
            return tag.getValue();
        }
        return this.calcVersion(javaClass.getComment());
    }

    private String calcVersion(String comment) {
        return this.parseCommentVersion(comment)[1];
    }

    private String[] parseCommentVersion(String commentVersion) {
        String[] parsed = new String[2];
        if (commentVersion == null) {
            return parsed;
        }
        commentVersion = commentVersion.trim();
        int i = commentVersion.toLowerCase().indexOf("\nversion:");
        if (i == -1) {
            parsed[0] = commentVersion;
            return parsed;
        }
        parsed[1] = commentVersion.substring(i + "\nversion:".length()).trim();
        parsed[0] = commentVersion.substring(0, i).trim();

        return parsed;
    }

    private Annotation getAnnotation(JavaMethod getterMethod,
            JavaMethod setterMethod, JavaField beanField, String type) {
        if (beanField != null) {

            for (Annotation annotation : beanField.getAnnotations()) {
                if (annotation.getType().getJavaClass().getName().equals(type)) {
                    return annotation;
                }
            }
        }
        if (getterMethod != null) {

            for (Annotation annotation : getterMethod.getAnnotations()) {
                if (annotation.getType().getJavaClass().getName().equals(type)) {
                    return annotation;
                }
            }
        }
        if (setterMethod != null) {

            for (Annotation annotation : setterMethod.getAnnotations()) {
                if (annotation.getType().getJavaClass().getName().equals(type)) {
                    return annotation;
                }
            }
        }
        return null;
    }

    private String calcRequired(JavaMethod getterMethod,
            JavaMethod setterMethod, JavaField beanField) {
        Annotation annotation = this.getAnnotation(getterMethod, setterMethod, beanField, "XmlElement");
        if (annotation == null) {
            annotation = this.getAnnotation(getterMethod, setterMethod, beanField, "XmlAttribute");
        }
        if (annotation != null) {
            Object required = annotation.getNamedParameter("required");
            if (required != null) {
                if (required.toString().equalsIgnoreCase("true")) {
                    return "Required";
                }
            }
        }
        if (getterMethod != null) {
            DocletTag tag = getterMethod.getTagByName("required", true);
            if (tag != null) {
                if (tag.getValue() == null) {
                    return "Required";
                }
                String required = "Required " + tag.getValue();
                return required.trim();
            }
        }
        return null;
    }

    private String calcReadOnly(JavaMethod getterMethod,
            JavaMethod setterMethod, JavaField beanField) {
        if (getterMethod != null) {
            DocletTag tag = getterMethod.getTagByName("readOnly", true);
            if (tag != null) {
                if (tag.getValue() == null) {
                    return "Read only";
                }
                String readOnly = "Read only " + tag.getValue();
                return readOnly.trim();
            }
        }
        return null;
    }

    private String calcImplementationNotes(JavaMethod serviceMethod) {
        StringBuilder bldr = new StringBuilder();
        String newLine = "";
        for (DocletTag tag : serviceMethod.getTagsByName("impl", true)) {
            bldr.append(newLine);
            newLine = "\n";
            String value = tag.getValue();
            bldr.append(value);
        }
        if (hasOverride(serviceMethod)) {
            boolean matchJustOnName = true;
            JavaMethod overriddenMethod = findInterfaceMethod(serviceMethod.getParentClass(), serviceMethod, matchJustOnName);
            if (overriddenMethod == null) {
                // do it again so we can debug
                findInterfaceMethod(serviceMethod.getParentClass(), serviceMethod, true);
                throw new NullPointerException("could not find overridden method or method that has @Override annotation " + serviceMethod.getCallSignature());
            }
            bldr.append(newLine);
            newLine = "\n";
            bldr.append("Overridden method should be implemented in helper: ");
            bldr.append(overriddenMethod.getParentClass().getName());
        }
        if (bldr.length() == 0) {
            return null;
        }
        return bldr.toString();
    }

    private boolean hasOverride(JavaMethod serviceMethod) {
        for (Annotation annotation : serviceMethod.getAnnotations()) {
            if (annotation.getType().getJavaClass().getName().equals(
                    "Override")) {
                return true;
            }
        }
        return false;
    }

    private boolean isDeprecated(JavaMethod serviceMethod) {
        for (Annotation annotation : serviceMethod.getAnnotations()) {
            if (annotation.getType().getJavaClass().getName().equals(
                    "Deprecated")) {
                return true;
            }
        }
        return false;
    }

    private String calcImplementationNotes(JavaMethod getterMethod,
            JavaMethod setterMethod, JavaField beanField) {
        if (getterMethod != null) {
            DocletTag tag = getterMethod.getTagByName("impl", true);
            if (tag != null) {
                return tag.getValue();
            }
        }
        return null;
    }

    private String calcNameFromShortName(String shortName) {
        StringBuilder bldr = new StringBuilder(shortName.length() + 3);
        char c = shortName.charAt(0);
        bldr.append(Character.toUpperCase(c));
        boolean lastWasUpper = true;
        for (int i = 1; i < shortName.length(); i++) {
            c = shortName.charAt(i);
            if (Character.isUpperCase(c)) {
                if (!lastWasUpper) {
                    bldr.append(" ");
                }
            } else {
                lastWasUpper = false;
            }
            bldr.append(c);
        }
        return bldr.toString();
    }

    private String calcName(JavaClass mainClass, JavaMethod getterMethod,
            JavaMethod setterMethod, JavaField beanField, String shortName) {
        String name = this.calcNameFromTag(getterMethod, setterMethod, beanField);
        if (name != null) {
            return name;
        }
        name = this.calcNameFromNameEmbeddedInDescription(mainClass, getterMethod, setterMethod, beanField, shortName);
        if (name != null) {
            return name;
        }
        return this.calcNameFromShortName(shortName);
    }

    private String calcNameFromTag(JavaMethod getterMethod,
            JavaMethod setterMethod, JavaField beanField) {
        if (getterMethod != null) {
            DocletTag tag = getterMethod.getTagByName("name", true);
            if (tag != null) {
                return tag.getValue();
            }
        }
        return null;
    }

    private String calcNameFromNameEmbeddedInDescription(JavaClass mainClass, JavaMethod getterMethod,
            JavaMethod setterMethod, JavaField beanField, String shortName) {
        String nameDesc = this.calcMethodComment(mainClass, getterMethod, setterMethod,
                beanField, shortName);
        String[] parsed = parseNameDesc(nameDesc);
        return parsed[0];
    }

    private String[] parseNameDesc(String nameDesc) {
        String[] parsed = new String[2];
        if (nameDesc == null) {
            return parsed;
        }
        nameDesc = nameDesc.trim();
        if (!nameDesc.startsWith("Name:")) {
            parsed[1] = nameDesc;
            return parsed;
        }
        nameDesc = nameDesc.substring("Name:".length()).trim();
        int i = nameDesc.indexOf("\n");
        if (i == -1) {
            parsed[0] = nameDesc.trim();
            return parsed;
        }
        parsed[0] = nameDesc.substring(0, i).trim();
        parsed[1] = nameDesc.substring(i).trim();
        return parsed;
    }

    private String calcDescription(JavaClass mainClass, JavaMethod getterMethod,
            JavaMethod setterMethod, JavaField beanField, String shortName) {
        String nameDesc = this.calcMethodComment(mainClass, getterMethod, setterMethod,
                beanField, shortName);
        String[] parsed = parseNameDesc(nameDesc);
        return parsed[1];
    }
 
    private String calcMethodComment(JavaClass mainClass, JavaMethod getterMethod,
            JavaMethod setterMethod,
            JavaField beanField,
            String shortName) {
        String desc = null;
        if (getterMethod != null) {
            desc = getterMethod.getComment();
            if (isCommentNotEmpty(desc)) {
                return desc;
            }
//            System.out.println("Searching for superclass to find description for field " + shortName);
            JavaClass jc = mainClass;
            while (true) {
                JavaClass superJc = jc.getSuperJavaClass();
                if (superJc == null) {
                    break;
                }
                JavaMethod parentGetterMethod = this.findGetterMethod(superJc, shortName);
                if (parentGetterMethod != null) {
                    desc = parentGetterMethod.getComment();
                    if (isCommentNotEmpty(desc)) {
                        return desc;
                    }
                }
                jc = superJc;
            }
        }
        if (setterMethod != null) {
            desc = setterMethod.getComment();
            if (isCommentNotEmpty(desc)) {
                return desc;
            }
        }
        if (beanField != null) {
            desc = beanField.getComment();
            if (isCommentNotEmpty(desc)) {
                return desc;
            }
        }
        desc = calcMethodCommentRecursively(mainClass, getterMethod);
        if (isCommentNotEmpty(desc)) {
            return desc;
        }
        desc = calcMethodCommentRecursively(mainClass, setterMethod);
        if (isCommentNotEmpty(desc)) {
            return desc;
        }
        return null;
    }

    private String calcMethodCommentRecursively(JavaClass mainClass, JavaMethod method) {
        if (method == null) {
            return null;
        }
        String desc = method.getComment();
        if (isCommentNotEmpty(desc)) {
            return desc;
        }
        JavaMethod infcMethod = findInterfaceMethod(mainClass, method, false);
        if (infcMethod != null) {
            desc = infcMethod.getComment();
            if (isCommentNotEmpty(desc)) {
                return desc;
            }
        }
        JavaMethod superMethod = findSuperMethod(method);
        if (superMethod != null) {
            desc = superMethod.getComment();
            if (isCommentNotEmpty(desc)) {
                return desc;
            }
        }
        return null;
    }

    private JavaMethod findSuperMethod(JavaMethod method) {
//        System.out.println("Searching for super method for "
//                + method.getParentClass().getName() + "."
//                + method.getCallSignature());
        for (JavaMethod superMethod : method.getParentClass().getMethods(true)) {
            if (method.equals(superMethod)) {
                continue;
            }
            if (method.getCallSignature().equals(superMethod.getCallSignature())) {
                return superMethod;
            }
        }
        return null;
    }

    private JavaMethod findInterfaceMethod(JavaClass mainClass, JavaMethod method, boolean matchJustOnName) {
        String callSig = method.getCallSignature();
        if (matchJustOnName) {
            callSig = method.getName();
        }
        JavaClass classToSearch = mainClass;
//        log ("Searching mainClass " + classToSearch.getName() + " for " + callSig, callSig);
        while (true) {
            for (JavaClass infcClass : classToSearch.getImplementedInterfaces()) {
                JavaMethod meth = this.findMethodOnInterfaceRecursively(infcClass, callSig, matchJustOnName);
                if (meth != null) {
//                    recursionCntr = 0;
                    return meth;
                }
            }
            JavaClass superClass = classToSearch.getSuperJavaClass();
            if (superClass == null) {
//                recursionCntr = 0;                
//                log ("Did not find " + callSig + " on " + mainClass, callSig); 
                return null;
            }
            classToSearch = superClass;
//            log ("Searching superClass " + classToSearch.getName() + " for " + callSig, callSig);                
        }
    }

//    private void log (String message, String callSig) {
//        if (callSig.equalsIgnoreCase("getTypeKey()")) {
//            for (int i = 0; i < this.recursionCntr; i++) {
//                System.out.print (" ");
//            }
//            System.out.println (message);
//        }
//    }
//    private int recursionCntr = 0;
    private JavaMethod findMethodOnInterfaceRecursively(JavaClass infcClass, String callSig, boolean matchJustOnName) {
//        recursionCntr++;
//        log ("Searching interface " + infcClass.getName() + " for " + callSig, callSig);
        for (JavaMethod infcMethod : infcClass.getMethods()) {
            if (callSig.equals(infcMethod.getCallSignature())) {
//                log (callSig + " found on " + infcClass.getName() + "!!!!!!!!!!!!!!!!", callSig); 
//                recursionCntr--;
                return infcMethod;
            }
            if (matchJustOnName) {
                if (callSig.equals(infcMethod.getName())) {
                    return infcMethod;
                }
            }
        }
        for (JavaClass subInfc : infcClass.getImplementedInterfaces()) {
//            log ("Searching  sub-interface " + subInfc.getName() + " for " + callSig, callSig);
            JavaMethod infcMethod = findMethodOnInterfaceRecursively(subInfc, callSig, matchJustOnName);
            if (infcMethod != null) {
//                recursionCntr--;
                return infcMethod;
            }
        }
//        log (callSig + " not found on " + infcClass.getName(), callSig);
//        this.recursionCntr--;
        return null;
    }

    private boolean isCommentNotEmpty(String desc) {
        if (desc == null) {
            return false;
        }
        if (desc.trim().isEmpty()) {
            return false;
        }
        if (desc.contains("@inheritDoc")) {
            return false;
        }
        return true;
    }

    private String getAccessorType(JavaMethod method) {
        String accessorType = getAccessorType(method.getAnnotations());
        if (accessorType != null) {
            return accessorType;
        }
        accessorType = getAccessorType(method.getParentClass().getAnnotations());
        return accessorType;
    }

    private String getAccessorType(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation.getType().getJavaClass().getName().equals(
                    "XmlAccessorType")) {
//    System.out.println ("Looking for XmlAccessorType annotation = "
//                        + annotation.getParameterValue ());
                return annotation.getParameterValue().toString();
            }
        }
        return null;
    }

    private String stripQuotes(String str) {
        if (str.startsWith("\"")) {
            str = str.substring(1);
        }
        if (str.endsWith("\"")) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    private String calcMissing(String str) {
        if (str == null) {
            return "???";
        }
        if (str.trim().isEmpty()) {
            return "???";
        }
        return str;
    }

    private void addServiceToList(XmlType xmlType, String serviceKey) {
        if (!xmlType.getService().contains(serviceKey)) {
            xmlType.setService(xmlType.getService() + ", " + serviceKey);
        }
    }

    private String calcXmlAttribute(JavaField beanField) {
        if (beanField == null) {
            // TODO: worry about checking for this annotation on the method for non-field based AccessorTypes
            return "No";
        }
        for (Annotation annotation : beanField.getAnnotations()) {
            if (annotation.getType().getJavaClass().getName().equals("XmlAttribute")) {
                return "Yes";
            }
        }
        return "No";
    }

    private JavaField findField(JavaClass javaClass, String shortName,
            JavaMethod setterMethod) {
        JavaField field = findField(javaClass, shortName);
        if (field != null) {
            return field;
        }
        if (setterMethod != null) {
            String paramName = setterMethod.getParameters()[0].getName();
            if (paramName.equalsIgnoreCase(shortName)) {
                return null;
            }
            return findField(javaClass, paramName);
        }
        return null;
    }

    private JavaField findField(JavaClass javaClass, String name) {
        if (name == null) {
            return null;
        }
        for (JavaField field : javaClass.getFields()) {
            if (field.getName().equalsIgnoreCase(name)) {
                return field;
            }
            // TODO: check for shortNames that already start with is so we don't check for isIsEnrollable
            if (field.getName().equals("is" + name)) {
                return field;
            }
        }
        JavaClass superClass = javaClass.getSuperJavaClass();
        if (superClass == null) {
            return null;
        }
        return findField(superClass, name);
    }

    private JavaMethod findGetterMethod(JavaClass msClass, String shortName) {
        for (JavaMethod method : msClass.getMethods(true)) {
            String methodName = method.getName();
            if (methodName.equalsIgnoreCase("get" + shortName)) {
                return method;
            }
            if (methodName.toLowerCase().startsWith("is")) {
                if (methodName.equalsIgnoreCase("is" + shortName)) {
                    return method;
                }
                // shortName already has "is" in it
                if (methodName.equalsIgnoreCase(shortName)) {
                    return method;
                }
            }
            // TODO: followup on KimEntityResidencyInfo.getInState
            if (methodName.equalsIgnoreCase("getInState") && shortName.equalsIgnoreCase(
                    "InStateFlag")) {
                return method;
            }
        }
        return null;
    }

    private JavaMethod findSetterMethod(JavaClass msClass, String shortName) {
        for (JavaMethod method : msClass.getMethods(true)) {
            if (method.getName().equals("set" + shortName)) {
                return method;
            }
            // TODO: check for shortNames that already start with is so we don't check for isIsEnrollable
            if (method.getName().equals("setIs" + shortName)) {
                return method;
            }
            // TODO: followup on KimEntityResidencyInfo.getInState
            if (method.getName().equals("setInStateFlag") && shortName.equals(
                    "InState")) {
                return method;
            }
        }
        return null;
    }
    private static final String[] SETTER_METHODS_TO_SKIP = {
        // Somebody put "convenience" methods on the validation result info
        "ValidationResultInfo.setWarning",
        "ValidationResultInfo.setError",
        // not on original wiki but still defined as a method but not backed by a field so not in wsdl
        "CredentialProgramInfo.setDiplomaTitle",
        // synonym for the official of setCredentialType
        "CredentialProgramInfo.setType",
        // not on original wiki but still defined as a method but not backed by a field so not in wsdl
        "CredentialProgramInfo.setHegisCode",
        "CredentialProgramInfo.setCip2000Code",
        "CredentialProgramInfo.setCip2010Code",
        "CredentialProgramInfo.setSelectiveEnrollmentCode",
        "CoreProgramInfo.setDiplomaTitle",
        // synonym for the official of setCredentialType
        //  "CoreProgramInfo.setType",
        // not on original wiki but still defined as a method but not backed by a field so not in wsdl
        "CoreProgramInfo.setHegisCode",
        "CoreProgramInfo.setCip2000Code",
        "CoreProgramInfo.setCip2010Code",
        "CoreProgramInfo.setSelectiveEnrollmentCode",
        "WhenConstraint.setValue"
    };
    private static final String[] GETTER_METHODS_TO_SKIP = {
        // Somebody put "convenience" methods on the validation result info
        "ValidationResultInfo.getWarning",
        "ValidationResultInfo.getError",
        // not on original wiki but still defined as a method but not backed by a field so not in wsdl
        "CredentialProgramInfo.getDiplomaTitle",
        // synonym for the official of setCredentialType
        "CredentialProgramInfo.getType",
        // not on original wiki but still defined as a method but not backed by a field so not in wsdl
        "CredentialProgramInfo.getHegisCode",
        "CredentialProgramInfo.getCip2000Code",
        "CredentialProgramInfo.getCip2010Code",
        "CredentialProgramInfo.getSelectiveEnrollmentCode",
        "CoreProgramInfo.getDiplomaTitle",
        // synonym for the official of setCredentialType
        //  "CoreProgramInfo.setType",
        // not on original wiki but still defined as a method but not backed by a field so not in wsdl
        "CoreProgramInfo.getHegisCode",
        "CoreProgramInfo.getCip2000Code",
        "CoreProgramInfo.getCip2010Code",
        "CoreProgramInfo.getSelectiveEnrollmentCode",
        "WhenConstraint.getValue"
    };

    private boolean isSetterMethodToProcess(JavaMethod method, String className) {
        if (!method.getName().startsWith("set")) {
            return false;
        }
        if (method.getParameters().length != 1) {
            return false;
        }
        if (method.isPrivate()) {
            return false;
        }
        if (method.isProtected()) {
            return false;
        }
        if (method.isStatic()) {
            return false;
        }
        if (method.getParentClass().getPackageName().startsWith("java")) {
            return false;
        }
        String fullName = className + "." + method.getName();
        for (String skip : SETTER_METHODS_TO_SKIP) {
            if (skip.equals(fullName)) {
                return false;
            }
        }
//  if (method.getParentClass ().isInterface ())
//  {
//   return false;
//  }
        for (Annotation annotation : method.getAnnotations()) {
            if (annotation.getType().getJavaClass().getName().equals("XmlTransient")) {
                return false;
            }
        }
        return true;
    }

    private boolean isGetterMethodToProcess(JavaMethod method, String className) {
        if (!method.getName().startsWith("get")) {
            if (!method.getName().startsWith("is")) {
                return false;
            }
        }
        if (method.getParameters().length != 0) {
            return false;
        }
        if (method.isPrivate()) {
            return false;
        }
        if (method.isProtected()) {
            return false;
        }
        if (method.isStatic()) {
            return false;
        }
        if (method.getParentClass().getPackageName().startsWith("java")) {
            return false;
        }
        String fullName = className + "." + method.getName();
        for (String skip : GETTER_METHODS_TO_SKIP) {
            if (skip.equals(fullName)) {
                return false;
            }
        }
//  if (method.getParentClass ().isInterface ())
//  {
//   return false;
//  }
        for (Annotation annotation : method.getAnnotations()) {
            if (annotation.getType().getJavaClass().getName().equals("XmlTransient")) {
                return false;
            }
        }
        return true;
    }

    private String calcShortNameFromSetter(JavaMethod method) {
        return method.getName().substring(3);
    }

    private String calcShortNameFromGetter(JavaMethod method) {
        if (method.getName().startsWith("get")) {
            return method.getName().substring(3);
        }
        if (method.getName().startsWith("is")) {
            return method.getName().substring(2);
        }
        throw new IllegalArgumentException(method.getName()
                + " does not start with is or get");
    }

    private String calcCardinality(JavaClass mainClass, JavaMethod getterMethod,
            JavaMethod setterMethod, JavaField beanField, String shortName) {
        if (isReturnACollection(mainClass, getterMethod, setterMethod, beanField, shortName)) {
            return "Many";
        }
        return "One";
    }

    private boolean isReturnACollection(JavaClass mainClass, JavaMethod getterMethod,
            JavaMethod setterMethod, JavaField beanField, String shortName) {
        if (getterMethod != null) {
            return isCollection(getterMethod.getReturnType());
        }
        if (beanField != null) {
            return isCollection(beanField.getType());
        }
        // TODO: check setterMethod
        return false;
    }

    private boolean isCollection(Type type) {
        JavaClass javaClass = type.getJavaClass();
        return this.isCollection(javaClass);
    }

    private boolean isCollection(JavaClass javaClass) {
        if (javaClass.getName().equals("LocalKeyList")) {
            return true;
        }
        if (javaClass.getName().equals("MessageGroupKeyList")) {
            return true;
        }
        if (javaClass.getName().equals(List.class.getSimpleName())) {
            return true;
        }
        if (javaClass.getName().equals(ArrayList.class.getSimpleName())) {
            return true;
        }
        if (javaClass.getName().equals(Collection.class.getSimpleName())) {
            return true;
        }
        if (javaClass.getName().equals(Set.class.getSimpleName())) {
            return true;
        }
        return false;
    }

    private String calcType(JavaClass mainClass, JavaMethod getterMethod,
            JavaMethod setterMethod, JavaField beanField, String shortName) {
        if (getterMethod != null) {
            return calcTypeOfGetterMethodReturn(getterMethod);
        }
        if (beanField != null) {
            Type type = beanField.getType();
            return calcType(type);
        }
        // TODO: calc type based on the setterMethod
        return null;
    }

    private String calcTypeOfGetterMethodReturn(JavaMethod getterMethod) {
        Type type = getterMethod.getReturnType();
        return calcType(type);
    }

    private String calcType(Type type) {
        if (type == null) {
            return "void";
        }
        if (isCollection(type.getJavaClass())) {
            return calcType(calcRealJavaClass(type)) + "List";
        }
        return calcType(calcRealJavaClass(type));
    }

    private Annotation findJavaAnnotation(String name, JavaClass clazz) {
    	
    	Annotation[] annotations = clazz.getAnnotations();
    	
    	for (Annotation annotation : annotations) {
	        
    		if (annotation.getType().getJavaClass().getName().equals(name)) {
    			return annotation;
    		}
        }
    	return null;
    }
    private String calcType(JavaClass javaClass) {
    	
        if (javaClass.isEnum()) {
        	
			if (!JavaClassAnnotationUtils.doesAnnotationExist(
			        XmlEnum.class.getSimpleName(), javaClass)) {
				// a rice or other dependency without the @XmlEnum annotation
				// present
				if (javaClass.getName().equals("WriteAccess")) {
					// rice CommonLookupParam
					return "String";
				} else if (javaClass.getName().equals("Widget")) {
					// rice CommonLookupParam
					return "String";
				} else if (javaClass.getName().equals("Usage")) {
					// rice
					return "String";
				} else {
					// this allows the types to be manually specified 
					// using the full package.classname format.
					return javaClass.getFullyQualifiedName();
				}

			}
    		
    		Class<?>annotationSpecifiedType = JavaClassAnnotationUtils.extractXmlEnumValue(javaClass);
    		
        	return annotationSpecifiedType.getSimpleName();
           
        }
        // this is messed up instead of list of strings it is an object with a list of strings
        if (javaClass.getName().equals(LOCALE_KEY_LIST)) {
            return "StringList";
        }
        if (javaClass.getName().equals(MESSAGE_GROUP_KEY_LIST)) {
            return "StringList";
        }
        // TODO: figure out why rice stuff translates like this junk?
        if (javaClass.getName().equals("java$util$Map")) {
            return "Map<String, String>";
        }
        if (javaClass.getName().equals("Map")) {
            // TODO: make sure it is in fact a String,String map
            return "Map<String, String>";
        }
        return javaClass.getName();
    }

    private JavaClass calcRealJavaClassOfGetterReturn(JavaMethod getterMethod) {
        if (getterMethod == null) {
            return null;
        }
        Type type = getterMethod.getReturnType();
        return this.calcRealJavaClass(type);
    }

    private JavaClass calcRealJavaClass(Type type) {
        if (type == null) {
            return null;
        }
        JavaClass javaClass = type.getJavaClass();
        if (javaClass.getName().equals(LOCALE_KEY_LIST)) {
            return STRING_JAVA_CLASS;
        }
        if (javaClass.getName().equals(MESSAGE_GROUP_KEY_LIST)) {
            return STRING_JAVA_CLASS;
        }
        if (!this.isCollection(javaClass)) {
            return javaClass;
        }

//  for (Type t : type.getActualTypeArguments ())
//  {
//   System.out.println ("ServiceContractModelQDoxLoader: type arguments = "
//                       + t.toString ());
//  }

        Type[]collectionTypeArguments = type.getActualTypeArguments();
        
        if (collectionTypeArguments == null)
        	return new JavaClass (Object.class.getName());
        else
        	return collectionTypeArguments[0].getJavaClass();
    }

    private boolean isComplex(JavaClass javaClass) {
        if (javaClass.getName().equals ("void")) {
            return false;
        }
        if (javaClass.isEnum()) {
            return false;
        }
        if (javaClass.getName().equals(String.class.getSimpleName())) {
            return false;
        }
        if (javaClass.getName().equals(Integer.class.getSimpleName())) {
            return false;
        }
        if (javaClass.getName().equals(Date.class.getSimpleName())) {
            return false;
        }
        if (javaClass.getName().equals(Long.class.getSimpleName())) {
            return false;
        }
        if (javaClass.getName().equals(Boolean.class.getSimpleName())) {
            return false;
        }
        if (javaClass.getName().equals(Double.class.getSimpleName())) {
            return false;
        }
        if (javaClass.getName().equals(Float.class.getSimpleName())) {
            return false;
        }
        if (javaClass.getName().equals(int.class.getSimpleName())) {
            return false;
        }
        if (javaClass.getName().equals(long.class.getSimpleName())) {
            return false;
        }
        if (javaClass.getName().equals(boolean.class.getSimpleName())) {
            return false;
        }
        if (javaClass.getName().equals(double.class.getSimpleName())) {
            return false;
        }
        if (javaClass.getName().equals(float.class.getSimpleName())) {
            return false;
        }
        if (javaClass.getName().equals(Map.class.getSimpleName())) {
            return false;
        }

        if (javaClass.getName().equals(String.class.getName())) {
            return false;
        }
        if (javaClass.getName().equals(Integer.class.getName())) {
            return false;
        }
        if (javaClass.getName().equals(Date.class.getName())) {
            return false;
        }
        if (javaClass.getName().equals(Long.class.getName())) {
            return false;
        }
        if (javaClass.getName().equals(Boolean.class.getName())) {
            return false;
        }
        if (javaClass.getName().equals(Double.class.getName())) {
            return false;
        }
        if (javaClass.getName().equals(Float.class.getName())) {
            return false;
        }
        if (javaClass.getName().equals(int.class.getName())) {
            return false;
        }
        if (javaClass.getName().equals(long.class.getName())) {
            return false;
        }
        if (javaClass.getName().equals(boolean.class.getName())) {
            return false;
        }
        if (javaClass.getName().equals(double.class.getName())) {
            return false;
        }
        if (javaClass.getName().equals(float.class.getName())) {
            return false;
        }
        if (javaClass.getName().equals(BigDecimal.class.getSimpleName())) {
            return false;
        }
        if (javaClass.getName().equals(Map.class.getName())) {
            return false;
        }
        if (javaClass.getName().equals(LOCALE_KEY_LIST)) {
            return false;
        }
        if (javaClass.getName().equals(MESSAGE_GROUP_KEY_LIST)) {
            return false;
        }
        if (javaClass.getName().equals("java$util$Map")) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ServiceContractModelQDoxLoader{" +
                "sourceDirectories=" + sourceDirectories +
                ", services=" + services +
                ", serviceMethods=" + serviceMethods +
                ", xmlTypeMap=" + xmlTypeMap +
                ", messageStructures=" + messageStructures +
                ", validateKualiStudent=" + validateKualiStudent +
                '}';
    }
}
