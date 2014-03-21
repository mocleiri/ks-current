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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.student.contract.model.MessageStructure;
import org.kuali.student.contract.model.Service;

import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.ServiceMethod;
import org.kuali.student.contract.model.XmlType;
import org.kuali.student.contract.model.util.ModelFinder;
import org.kuali.student.contract.writer.JavaClassWriter;
import org.kuali.student.contract.writer.service.GetterSetterNameCalculator;

/**
 *
 * @author nwright
 */
public class AdminUiInquirableWriter extends JavaClassWriter {

    private ServiceContractModel model;
    private ModelFinder finder;
    private String directory;
    private String rootPackage;
    private String servKey;
    private Service service;
    private XmlType xmlType;
    private List<ServiceMethod> methods;

    public AdminUiInquirableWriter(ServiceContractModel model,
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

    private static String calcModulePartOfPackage(XmlType xmlType) {
        if (xmlType.getJavaPackage().contains(".enrollment.")) {
            return "enrollment";
        }
        if (xmlType.getJavaPackage().contains(".core.")) {
            return "core";
        }
        if (xmlType.getJavaPackage().contains(".lum.")) {
            return "lum";
        }
        return null;
    }

    public static String calcPackage(String servKey, String rootPackage, XmlType xmlType) {
        String module = calcModulePartOfPackage(xmlType);
        if (module == null) {
            throw new IllegalArgumentException("unknown module " + xmlType.getJavaPackage());
        }
        String pack = "org.kuali.student." + module  + "." + rootPackage + "." + servKey.toLowerCase();
//  StringBuffer buf = new StringBuffer (service.getVersion ().length ());
//  for (int i = 0; i < service.getVersion ().length (); i ++)
//  {
//   char c = service.getVersion ().charAt (i);
//   c = Character.toLowerCase (c);
//   if (Character.isLetter (c))
//   {
//    buf.append (c);
//    continue;
//   }
//   if (Character.isDigit (c))
//   {
//    buf.append (c);
//   }
//  }
//  pack = pack + buf.toString ();
//        pack = pack + "service.decorators";
        return pack;
//        return rootPackage;
    }

    public static String calcClassName(String servKey, String xmlTypeName) {
        return GetterSetterNameCalculator.calcInitUpper(xmlTypeName) + "AdminInquirableImpl";
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
        println(" extends InquirableImpl");
        importsAdd("org.kuali.rice.krad.inquiry.InquirableImpl");
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
        MessageStructure pk = this.getPrimaryKey(xmlType.getName());
        indentPrintln("private final static String PRIMARY_KEY = \"" + pk.getShortName() + "\";");
        indentPrintln ("private static final long serialVersionUID = 1L;");

        println("");
        indentPrintln("@Override");
        importsAdd(xmlType.getJavaPackage() + "." + infoClass);
        importsAdd(List.class.getName());
        importsAdd(Map.class.getName());
//        importsAdd(LookupForm.class.getName());
//        importsAdd(QueryByCriteria.class.getName());
//        importsAdd(Predicate.class.getName());
//        importsAdd(PredicateFactory.class.getName());
        importsAdd(GlobalResourceLoader.class.getName());
        XmlType contextInfo = finder.findXmlType("contextInfo");
        importsAdd(contextInfo.getJavaPackage() + "." + contextInfo.getName());
        importsAdd("org.kuali.student.common.util.ContextBuilder");
//        importsAdd(PredicateFactory.class.getName());
        importsAdd(ArrayList.class.getName());
        importsAdd(QName.class.getName());
//        importsAdd (PredicateFactory.class.getName());
        indentPrintln("public " + infoClass + " retrieveDataObject(Map<String, String> parameters)");
        openBrace();
        indentPrintln("String key = parameters.get(PRIMARY_KEY);");
        indentPrintln("try");
        openBrace();
        String getMethod = calcGetMethod();
        if (getMethod == null) {
            indentPrintln("// WARNING: Missing get method please add it to the service contract: " + servKey + "." + xmlType.getName());
            getMethod = "get" + GetterSetterNameCalculator.calcInitUpper(xmlType.getName());
        }
        indentPrintln("" + infoClass + " info = this.get" + serviceClass + "()." + getMethod + "(key, getContextInfo());");
        indentPrintln("return info;");
        closeBrace();
        indentPrintln("catch (Exception ex) {");
        indentPrintln("    throw new RuntimeException(ex);");
        indentPrintln("}");
        closeBrace();
        writeServiceGetterAndSetter(this, serviceClass, serviceVar, xmlType);
    }

    private MessageStructure getPrimaryKey(String xmlTypeName) {
        for (MessageStructure ms : finder.findMessageStructures(xmlTypeName)) {
            if (ms.isPrimaryKey()) {
                return ms;
            }
        }
        return null;
    }

    public static void writeServiceGetterAndSetter(JavaClassWriter out, String serviceClass, String serviceVar, XmlType xmlType) {

        out.println("");
        out.indentPrintln("public void set" + serviceClass + "(" + serviceClass + " " + serviceVar + ")");
        out.openBrace();
        out.indentPrintln("    this." + serviceVar + " = " + serviceVar + ";");
        out.closeBrace();
        out.println("");
        out.indentPrintln("public " + serviceClass + " get" + serviceClass + "()");
        out.openBrace();
        out.indentPrintln("if (" + serviceVar + " == null)");
        out.openBrace();
        String serviceConstants = calcServiceContantsName(serviceClass);
        out.importsAdd(calcServiceConstantsPackage(xmlType) + "." + serviceConstants);
        out.indentPrintln("QName qname = new QName(" + serviceConstants + ".NAMESPACE," + serviceConstants + ".SERVICE_NAME_LOCAL_PART);");
        out.indentPrintln(serviceVar + " = (" + serviceClass + ") GlobalResourceLoader.getService(qname);");
        out.closeBrace();
        out.indentPrintln("return this." + serviceVar + ";");
        out.closeBrace();
        out.println("");
        out.indentPrintln("private ContextInfo getContextInfo() {");
        out.indentPrintln("    return ContextBuilder.loadContextInfo();");
        out.indentPrintln("}");
    }

    public static String calcServiceContantsName(String serviceClass) {
        if (serviceClass.equals("LRCService")) {
            return "LrcServiceConstants";
        }
        return serviceClass + "Constants";
    }
    private static Map<String, String> SERVICE_CLASS_PACKAGE;

    {
        SERVICE_CLASS_PACKAGE = new HashMap<String, String>();
        SERVICE_CLASS_PACKAGE.put("organization", "org.kuali.student.r2.core.constants");
        SERVICE_CLASS_PACKAGE.put("lum", "org.kuali.student.r2.lum.util.constants");
        SERVICE_CLASS_PACKAGE.put("lum", "org.kuali.student.r2.lum.util.constants");
        SERVICE_CLASS_PACKAGE.put("lum", "org.kuali.student.r2.lum.util.constants");
        SERVICE_CLASS_PACKAGE.put("core", "org.kuali.student.r2.core.constants");
        SERVICE_CLASS_PACKAGE.put("enrollment", "org.kuali.student.r2.common.util.constants");
    }

    public static String calcServiceConstantsPackage(XmlType xmlType) {
        String pkg = SERVICE_CLASS_PACKAGE.get(xmlType.getService().toLowerCase());
        if (pkg != null) {
            return pkg;
        }
        String module = calcModulePartOfPackage(xmlType);
        if (module != null) {
            return SERVICE_CLASS_PACKAGE.get(module);
        }
        return "org.kuali.student.r2.common.util.constants";
    }

    private String calcGetMethod() {
        ServiceMethod method = this.findGetMethod();
        if (method != null) {
            return method.getName();
        }
        return null;
    }

    private ServiceMethod findGetMethod() {
        String infoClass = GetterSetterNameCalculator.calcInitUpper(xmlType.getName());
        for (ServiceMethod method : methods) {
            if (method.getName().startsWith("get")) {
                if (method.getReturnValue().getType().equalsIgnoreCase(infoClass)) {
                    if (method.getParameters().size() == 2) {
                        if (method.getParameters().get(0).getType().equalsIgnoreCase("String")) {
                            return method;
                        }
                    }
                }
            }
        }
        return null;
    }
}
