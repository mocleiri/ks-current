/**
 * Copyright 2004-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kuali.student.remote.impl.mojo;

import java.util.ArrayList;
import java.util.List;
import org.kuali.student.contract.model.Service;

import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.ServiceMethod;
import org.kuali.student.contract.model.ServiceMethodError;
import org.kuali.student.contract.model.ServiceMethodParameter;
import org.kuali.student.contract.model.XmlType;
import org.kuali.student.contract.model.util.ModelFinder;
import org.kuali.student.contract.writer.JavaClassWriter;
import org.kuali.student.contract.writer.service.GetterSetterNameCalculator;
import org.kuali.student.contract.writer.service.MessageStructureTypeCalculator;
import org.kuali.student.contract.writer.service.ServiceExceptionWriter;

/**
 *
 * @author nwright
 */
public class RemoteImplServiceWriter extends JavaClassWriter {

    private ServiceContractModel model;
    private ModelFinder finder;
    private String directory;
    private String rootPackage;
    private String servKey;
    private List<ServiceMethod> methods;

    public RemoteImplServiceWriter(ServiceContractModel model,
            String directory,
            String rootPackage,
            String servKey,
            List<ServiceMethod> methods) {
        super(directory + "/main/java", calcPackage(servKey, rootPackage), calcClassName(servKey));
        this.model = model;
        this.finder = new ModelFinder(model);
        this.directory = directory;
        this.rootPackage = rootPackage;
        this.servKey = servKey;
        this.methods = methods;
    }

    public static String calcPackage(String servKey, String rootPackage) {
//        String pack = rootPackage + "." + servKey.toLowerCase() + ".";
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
//        return pack;
        return rootPackage;
    }

    public static String calcClassName(String servKey) {
        String name = GetterSetterNameCalculator.calcInitUpper(servKey + "ServiceRemoteImpl");
        if (name.startsWith("RICE.")) {
            name = name.substring("RICE.".length());
        }
        return name;
    }

    public static String calcDecoratorClassName(String servKey) {
        String name = GetterSetterNameCalculator.calcInitUpper(servKey + "ServiceDecorator");
        if (name.startsWith("RICE.")) {
            name = name.substring("RICE.".length());
        }
        return name;
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
        indentPrint("public class " + calcClassName(servKey));
        println(" extends " + calcDecoratorClassName(servKey));
        // TODO: figure out how to add import for the decorator
        openBrace();
        writeBiolerPlate();
        indentPrintln("//");
        indentPrintln("// Have to override and check each method that returns a list for null");
        indentPrintln("// This is because SOAP/JAXB renders and empty list as null ");
        indentPrintln("// but our contract standard says we ALWAYS return an empty list");
        indentPrintln("//");
        for (ServiceMethod method : methods) {
            if (!method.getReturnValue().getType().endsWith("List")) {
                continue;
            }

            indentPrintln("");
//            indentPrintln("/**");
//            indentPrintWrappedComment(method.getDescription());
//            indentPrintln("* ");
//            for (ServiceMethodParameter param : method.getParameters()) {
//                indentPrintWrappedComment("@param " + param.getName() + " - "
//                        + param.getType() + " - "
//                        + param.getDescription());
//            }
//            indentPrintWrappedComment("@return " + method.getReturnValue().
//                    getDescription());
//            indentPrintln("*/");
            indentPrintln("@Override");
            String type = method.getReturnValue().getType();
            String realType = stripList(type);
            indentPrint("public " + calcType(type, realType) + " " + method.getName()
                    + "(");
            // now do parameters
            String comma = "";
            for (ServiceMethodParameter param : method.getParameters()) {
                type = param.getType();
                realType = stripList(type);
                print(comma);
                print(calcType(type, realType));
                print(" ");
                print(param.getName());
                comma = ", ";
            }
            println(")");
            // now do exceptions
            comma = "throws ";
            incrementIndent();
            for (ServiceMethodError error : method.getErrors()) {
                indentPrint(comma);
                String exceptionClassName = calcExceptionClassName(error);
                String exceptionPackageName = this.calcExceptionPackageName(error);
                println(exceptionClassName);
                this.importsAdd(exceptionPackageName + "." + exceptionClassName);
                comma = "      ,";
            }
            decrementIndent();
            openBrace();
            type = method.getReturnValue().getType();
            realType = stripList(type);
            XmlType retValXmlType = finder.findXmlType(realType);
            importsAdd(retValXmlType.getJavaPackage() + "." + retValXmlType.getName());
            indentPrint("List<" + retValXmlType.getName() + "> list = this.getNextDecorator ()." + method.getName() + "(");
            comma = "";
            for (ServiceMethodParameter param : method.getParameters()) {
                type = param.getType();
                realType = stripList(type);
                print(comma);
                print(param.getName());
                comma = ", ";
            }
            println(");");
            indentPrintln("if (list == null)");
            openBrace();
            importsAdd(ArrayList.class.getName());
            indentPrintln("return new ArrayList<" + retValXmlType.getName() + "> ();");
            closeBrace();
            indentPrintln("return list;");
            closeBrace();
        }
        closeBrace();

        this.writeJavaClassAndImportsOutToFile();
        this.getOut().close();
    }

    private String calcType(String type, String realType) {
        XmlType t = finder.findXmlType(this.stripList(type));
        return MessageStructureTypeCalculator.calculate(this, model, type, realType,
                t.getJavaPackage());
    }

    private String stripList(String str) {
        return GetterSetterNameCalculator.stripList(str);
    }

    private String calcExceptionClassName(ServiceMethodError error) {
        if (error.getClassName() == null) {
            return ServiceExceptionWriter.calcClassName(error.getType());
        }
        return error.getClassName();
    }

    private String calcExceptionPackageName(ServiceMethodError error) {
        if (error.getClassName() == null) {
            return ServiceExceptionWriter.calcPackage(rootPackage);
        }
        return error.getPackageName();
    }

    private void writeBiolerPlate() {

        String initUpperServKey = GetterSetterNameCalculator.calcInitUpper(servKey);
        if (initUpperServKey.startsWith("RICE.")) {
            initUpperServKey = initUpperServKey.substring("RICE.".length());
        }

        // TODO: figure out how to add import for the service constants
        importsAdd("java.net.MalformedURLException");
        importsAdd("java.net.URL");
        importsAdd("javax.xml.namespace.QName");
        importsAdd("javax.xml.ws.Service");
        Service service = finder.findService(servKey);
        importsAdd(service.getImplProject() + "." + service.getName());

        indentPrintln("private String hostUrl = null;");
        indentPrintln("private String servicesUrlFragment = \"/services/\";");
        indentPrintln("private String serviceNameLocalPart = " + initUpperServKey + "ServiceConstants.SERVICE_NAME_LOCAL_PART;");
        indentPrintln("private String namespace = " + initUpperServKey + "ServiceConstants.NAMESPACE;");
        indentPrintln("private URL wsdlUrl = null;");
        println("");
        indentPrintln("public String getHostUrl() {");
        indentPrintln("    // check if explicitly configured, manually for testing or via spring");
        indentPrintln("    if (this.hostUrl != null) {");
        indentPrintln("        return hostUrl;");
        indentPrintln("    }");
        indentPrintln("    // check for a specific url for this service (not all services may be hosted from the same place)");
        indentPrintln("    Properties config = ConfigContext.getCurrentContextConfig().getProperties();");
        indentPrintln("    this.hostUrl = config.getProperty(\"remote.service.host.url." + servKey.toLowerCase() + "\");");
        indentPrintln("    if (this.hostUrl != null) {");
        indentPrintln("        return this.hostUrl;");
        indentPrintln("    }");
        indentPrintln("    // check for the default for all remote services");
        indentPrintln("    this.hostUrl = config.getProperty(\"remote.service.host.url\");");
        indentPrintln("    if (this.hostUrl != null) {");
        indentPrintln("        return this.hostUrl;");
        indentPrintln("    }");
        indentPrintln("    throw new IllegalArgumentException(\"No host url configured\");");
        indentPrintln("}");
        println("");
        indentPrintln("public void setHostUrl(String hostUrl) {");
        indentPrintln("    this.hostUrl = hostUrl;");
        indentPrintln("}");
        println("");
        indentPrintln("public URL getWsdlUrl() {");
        indentPrintln("    if (this.wsdlUrl == null) {");
        indentPrintln("        String urlString = this.getHostUrl() + this.getServicesUrlFragment() + this.getServiceNameLocalPart() + \"?wsdl\";");
        indentPrintln("        try {");
        indentPrintln("            this.wsdlUrl = new URL(urlString);");
        indentPrintln("        } catch (MalformedURLException ex) {");
        indentPrintln("            throw new IllegalArgumentException(urlString, ex);");
        indentPrintln("        }");
        indentPrintln("    }");
        indentPrintln("    return wsdlUrl;");
        indentPrintln("}");
        println("");
        indentPrintln("public void setWsdlUrl(URL wsdlUrl) {");
        indentPrintln("    this.wsdlUrl = wsdlUrl;");
        indentPrintln("}");
        println("");
        indentPrintln("public String getServiceNameLocalPart() {");
        indentPrintln("    return serviceNameLocalPart;");
        indentPrintln("}");
        println("");
        indentPrintln("public void setServiceNameLocalPart(String serviceNameLocalPart) {");
        indentPrintln("    this.serviceNameLocalPart = serviceNameLocalPart;");
        indentPrintln("}");
        println("");
        indentPrintln("public String getServicesUrlFragment() {");
        indentPrintln("    return servicesUrlFragment;");
        indentPrintln("}");
        println("");
        indentPrintln("public void setServicesUrlFragment(String servicesUrlFragment) {");
        indentPrintln("    this.servicesUrlFragment = servicesUrlFragment;");
        indentPrintln("}");
        println("");
        indentPrintln("public String getNamespace() {");
        indentPrintln("    return namespace;");
        indentPrintln("}");
        println("");
        indentPrintln("public void setNamespace(String namespace) {");
        indentPrintln("    this.namespace = namespace;");
        indentPrintln("}");
        println("");
        indentPrintln("public void init() {");
        indentPrintln("    QName qname = new QName(this.getNamespace(), this.getServiceNameLocalPart());");
        indentPrintln("    Service factory = Service.create(this.getWsdlUrl(), qname);");
        indentPrintln("    " + initUpperServKey + "Service port = factory.getPort(" + initUpperServKey + "Service.class);");
        indentPrintln("    this.setNextDecorator(port);");
        indentPrintln("}");
        println("");
        indentPrintln("private boolean hasBeenIntialized = false;");
        println("");
        indentPrintln("@Override");
        indentPrintln("public " + initUpperServKey + "Service getNextDecorator() throws OperationFailedException {");
        indentPrintln("    if (!this.hasBeenIntialized) {");
        indentPrintln("        this.hasBeenIntialized = true;");
        indentPrintln("        this.init();");
        indentPrintln("    }");
        indentPrintln("    return super.getNextDecorator();");
        indentPrintln("}");
        println("");

    }
}
