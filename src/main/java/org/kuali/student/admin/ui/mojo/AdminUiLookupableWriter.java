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
package org.kuali.student.admin.ui.mojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.PredicateFactory;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.krad.web.form.LookupForm;
import org.kuali.student.contract.model.Service;

import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.ServiceMethod;
import org.kuali.student.contract.model.ServiceMethodParameter;
import org.kuali.student.contract.model.XmlType;
import org.kuali.student.contract.model.util.ModelFinder;
import org.kuali.student.contract.writer.JavaClassWriter;
import org.kuali.student.contract.writer.service.GetterSetterNameCalculator;

/**
 *
 * @author nwright
 */
public class AdminUiLookupableWriter extends JavaClassWriter {

    private ServiceContractModel model;
    private ModelFinder finder;
    private String directory;
    private String rootPackage;
    private String servKey;
    private Service service;
    private XmlType xmlType;
    private List<ServiceMethod> methods;

    public AdminUiLookupableWriter(ServiceContractModel model,
            String directory,
            String rootPackage,
            String servKey,
            XmlType xmlType,
            List<ServiceMethod> methods) {
        super(directory + "/" + "java", calcPackage(servKey, rootPackage, xmlType), calcClassName(servKey, xmlType));
        this.model = model;
        this.finder = new ModelFinder(model);
        this.directory = directory;
        this.rootPackage = rootPackage;
        this.servKey = servKey;
        service = finder.findService(servKey);
        this.xmlType = xmlType;
        this.methods = methods;
    }

    public static String calcPackage(String servKey, String rootPackage, XmlType xmlType) {
        return AdminUiInquirableWriter.calcPackage(servKey, rootPackage, xmlType);
    }

    public static String calcClassName(String servKey, String xmlTypeName) {
        return GetterSetterNameCalculator.calcInitUpper(xmlTypeName) + "AdminLookupableImpl";
    }

    public static String calcClassName(String servKey, XmlType xmlType) {
        return calcClassName(servKey, xmlType.getName());
    }

    public static String calcDecoratorClassName(String servKey) {
        return GetterSetterNameCalculator.calcInitUpper(servKey + "ServiceDecorator");
    }

    private static enum MethodType {

        VALIDATE, CREATE, UPDATE
    };

    private MethodType calcMethodType(ServiceMethod method) {
        if (method.getName().startsWith("validate")) {
            return MethodType.VALIDATE;
        }
        if (method.getName().startsWith("create")) {
            return MethodType.CREATE;
        }
        if (method.getName().startsWith("update")) {
            return MethodType.UPDATE;
        }
        return null;
    }

    /**
     * Write out the entire file
     *
     * @param out
     */
    public void write() {
        indentPrint("public class " + calcClassName(servKey, xmlType));
        println(" extends LookupableImpl");
        importsAdd("org.kuali.rice.krad.lookup.LookupableImpl");
        openBrace();
        writeLogic();
        closeBrace();

        this.writeJavaClassAndImportsOutToFile();
        this.getOut().close();
    }

    private void writeLogic() {

        String initUpper = GetterSetterNameCalculator.calcInitUpper(servKey);
        String initLower = GetterSetterNameCalculator.calcInitLower(servKey);
        String infoClass = GetterSetterNameCalculator.calcInitUpper(xmlType.getName());
        String serviceClass = GetterSetterNameCalculator.calcInitUpper(service.getName());
        String serviceVar = GetterSetterNameCalculator.calcInitLower(service.getName());
        importsAdd(service.getImplProject() + "." + service.getName());
        importsAdd("org.apache.log4j.Logger");
        indentPrintln("private static final Logger LOG = Logger.getLogger(" + calcClassName(servKey, xmlType) + ".class);");
        indentPrintln("private transient " + serviceClass + " " + serviceVar + ";");
        indentPrintln ("private static final long serialVersionUID = 1L;");

        println("");
        indentPrintln("@Override");
        importsAdd(xmlType.getJavaPackage() + "." + infoClass);
        importsAdd(List.class.getName());
        importsAdd(Map.class.getName());
        importsAdd(LookupForm.class.getName());
        importsAdd(QueryByCriteria.class.getName());
        importsAdd(Predicate.class.getName());
        importsAdd(PredicateFactory.class.getName());
        importsAdd(GlobalResourceLoader.class.getName());
        XmlType contextInfo = finder.findXmlType("contextInfo");
        importsAdd(contextInfo.getJavaPackage() + "." + contextInfo.getName());
        importsAdd("org.kuali.student.common.util.ContextBuilder");
        importsAdd(PredicateFactory.class.getName());
        importsAdd(ArrayList.class.getName());
        importsAdd(QName.class.getName());
//        importsAdd (PredicateFactory.class.getName());
        indentPrintln("protected List<" + infoClass + "> getSearchResults(LookupForm lookupForm, Map<String, String> fieldValues, boolean unbounded)");
        openBrace();
        indentPrintln("QueryByCriteria.Builder qBuilder = QueryByCriteria.Builder.create();");
        indentPrintln("List<Predicate> pList = new ArrayList<Predicate>();");
        indentPrintln("for (String fieldName : fieldValues.keySet())");
        openBrace();
        indentPrintln("String value = fieldValues.get(fieldName);");
        indentPrintln("if (value != null && !value.isEmpty())");
        openBrace();
        indentPrintln("if (fieldName.equals(\"maxResultsToReturn\"))");
        openBrace();
        indentPrintln("qBuilder.setMaxResults (Integer.parseInt(value));");
        indentPrintln("continue;");
        closeBrace();
        indentPrintln("pList.add(PredicateFactory.equal(fieldName, value));");
        closeBrace();
        closeBrace();
        indentPrintln("if (!pList.isEmpty())");
        openBrace();
        indentPrintln("qBuilder.setPredicates(PredicateFactory.and(pList.toArray(new Predicate[pList.size()])));");
        closeBrace();
        indentPrintln("try");
        openBrace();
        String searchMethodName = calcSearchMethodName();
        if (searchMethodName == null) {
            indentPrintln("// WARNING: Missing searchMethod please add it to the service contract: " + servKey + "." + xmlType.getName());
            searchMethodName = "searchFor" + GetterSetterNameCalculator.calcInitUpper(xmlType.getName()) + "s";
        }
        indentPrintln("List<" + infoClass + "> list = this.get" + serviceClass + "()." + searchMethodName + "(qBuilder.build(), getContextInfo());");
        indentPrintln("return list;");
        closeBrace();
        indentPrintln("catch (Exception ex) {");
        indentPrintln("    throw new RuntimeException(ex);");
        indentPrintln("}");
        closeBrace();

        AdminUiInquirableWriter.writeServiceGetterAndSetter(this, serviceClass, serviceVar, xmlType);
    }

    private String calcSearchMethodName() {
        ServiceMethod method = this.findSearchMethod();
        if (method != null) {
            return method.getName();
        }
        return null;
    }

    private ServiceMethod findSearchMethod() {
        return findSearchMethod(xmlType, methods);
    }

    public static ServiceMethod findSearchMethod(XmlType xmlType, List<ServiceMethod> methods) {
        for (ServiceMethod method : methods) {
            if (hasProperReturnTypeForSearchMethod(xmlType, method)) {
                if (hasProperParameterForSearchMethod(xmlType, method)) {
                    return method;
                }
            }
        }
        return null;
    }

    private static boolean hasProperReturnTypeForSearchMethod(XmlType xmlType, ServiceMethod method) {
        String returnValueTypeLower = method.getReturnValue().getType().toLowerCase();
        if (returnValueTypeLower.endsWith("List".toLowerCase())) {
            if (returnValueTypeLower.startsWith(xmlType.getName().toLowerCase())) {
                return true;
            }
        }
        if (returnValueTypeLower.endsWith("QueryResults".toLowerCase())) {
            if (returnValueTypeLower.startsWith(xmlType.getName().toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasProperParameterForSearchMethod(XmlType xmlType, ServiceMethod method) {
        for (ServiceMethodParameter parameter : method.getParameters()) {
            if (parameter.getType().equals("QueryByCriteria")) {
                return true;
            }
        }
        return false;
    }
}
