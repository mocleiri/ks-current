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
package org.kuali.student.jpa.mojo;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import javax.persistence.Query;
import org.kuali.student.contract.model.MessageStructure;

/**
 *
 * @author nwright
 */
public class JpaDaoWriter extends JavaClassWriter {

    private static final Logger log = LoggerFactory.getLogger(JpaDaoWriter.class);

    //////////////////////////////
    // Constants
    //////////////////////////////
    /**
     * The standard type of methods used in our Service contract.
     */
    protected static enum MethodType {

        VALIDATE,
        CREATE,
        CREATE_BULK,
        ADD,
        UPDATE,
        UPDATE_OTHER,
        DELETE,
        REMOVE,
        DELETE_OTHER,
        GET_CREATE,
        GET_BY_ID,
        GET_BY_IDS,
        RICE_GET_BY_NAMESPACE_AND_NAME,
        GET_IDS_BY_TYPE,
        GET_IDS_BY_OTHER,
        GET_INFOS_BY_OTHER,
        GET_TYPE,
        GET_TYPES,
        SEARCH_FOR_IDS,
        SEARCH_FOR_INFOS,
        UNKNOWN
    };

    //////////////////////////////
    // Data Variables
    //////////////////////////////
    protected ServiceContractModel model;
    protected ModelFinder finder;
    private String directory;
    private XmlType xmlType;
    /**
     * The package name is stored in the service object itself (the package spec
     * kept moving around so I assumed the actual service name was unique but
     * ran into a problem when we included rice because they have a StateService
     * meaning US states and we have a StateService meaning the state of the
     * object so I added logic to detect rice and prepend that "RICE." to it
     */
    private String rootPackage;

    /**
     * Name of the service being operated on. If it is a RICE service it is
     * prefixed with RICE. [11:32:18 AM] Norman Wright: short name... I think it
     * gets it by taking the java class SimpleName and stripping off the word
     * "Service" and I think making it lower case. [11:32:24 AM] Norman Wright:
     * so OrganizationService becomes organization
     */
    protected String servKey;
    /**
     * A flag that holds if the service is an R1 service.
     */
    private boolean isR1;
    private List<ServiceMethod> methods;

    //////////////////////////
    // Constructor
    //////////////////////////
    public JpaDaoWriter(ServiceContractModel model,
            String directory,
            String rootPackage,
            String servKey,
            List<ServiceMethod> methods,
            XmlType xmlType,
            boolean isR1) {
        super(directory, calcPackage(servKey, rootPackage), calcClassName(servKey, xmlType));
        this.model = model;
        this.finder = new ModelFinder(model);
        this.directory = directory;
        this.rootPackage = rootPackage;
        this.servKey = servKey;
        this.methods = methods;
        this.xmlType = xmlType;
        this.isR1 = isR1;
    }

    public JpaDaoWriter(ServiceContractModel model,
            String directory,
            String rootPackage,
            String servKey,
            List<ServiceMethod> methods,
            XmlType xmlType,
            boolean isR1,
            String packageName,
            String className) {
        super(directory, packageName, className);
        this.model = model;
        this.finder = new ModelFinder(model);
        this.directory = directory;
        this.rootPackage = rootPackage;
        this.servKey = servKey;
        this.methods = methods;
        this.xmlType = xmlType;
        this.isR1 = isR1;
    }

    /////////////////////////
    // Functional Methods
    /////////////////////////
    /**
     * Returns the jpa implementation package name.
     *
     * @param servKey
     * @param rootPackage
     * @return
     */
    public static String calcPackage(String servKey, String rootPackage) {
        String pack = rootPackage + ".";
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
        pack = pack + "service.impl.jpa." + servKey.toLowerCase();
        return pack;
    }

    /**
     * Checks if this is a RICE service.
     *
     * @return true if this is a RICE service.
     */
    private boolean isRice() {
        if (this.servKey.startsWith("RICE.")) {
            return true;
        }
        return false;
    }

    protected static String fixServKey(String servKey) {
        if (servKey.startsWith("RICE.")) {
            return servKey.substring("RICE.".length());
        }
        return servKey;
    }

    /**
     * Given the service key (name), returns a calculated class name for the jpa
     * impl.
     */
    public static String calcClassName(String servKey, XmlType xmlType) {
        String name = calcInfcName(xmlType) + "Dao";
        return name;
    }

    private String calcInfcName() {
        String name = calcInfcName(xmlType);
        String pkg = xmlType.getJavaPackage();
        if (pkg.endsWith(".dto")) {
            pkg = pkg.substring(0, pkg.length() - ".dto".length()) + ".infc";
        }
        importsAdd(pkg + "." + name);
        return name;
    }

    private String calcInfoName() {
        String name = initUpper(xmlType.getName());
        String pkg = xmlType.getJavaPackage();
        importsAdd(pkg + "." + name);
        return name;
    }

    public static String calcInfcName(XmlType xmlType) {
        String name = xmlType.getName();
        if (name.endsWith("Info")) {
            name = name.substring(0, name.length() - "Info".length());
        }
        name = GetterSetterNameCalculator.calcInitUpper(name);
        return name;
    }

    protected MethodType calcMethodType(ServiceMethod method) {
//        if (method.getName().equals("getInstructionalDaysForTerm")) {
//            System.out.println("debug here");
//        }
        if (this.isRice()) {
            if (method.getName().contains("ByNamespaceCodeAndName")) {
                return MethodType.RICE_GET_BY_NAMESPACE_AND_NAME;
            }
            if (method.getName().contains("ByNameAndNamespace")) {
                return MethodType.RICE_GET_BY_NAMESPACE_AND_NAME;
            }
            if (method.getName().startsWith("get")) {
                if (method.getParameters().size() == 1) {
                    if (!method.getReturnValue().getType().endsWith("List")) {
                        if (method.getParameters().get(0).getName().equals("id")) {
                            return MethodType.GET_BY_ID;
                        }

                    } else {
                        if (method.getParameters().get(0).getName().equals("ids")) {
                            return MethodType.GET_BY_IDS;
                        }
                    }
                }
            }
        }
        if (method.getName().startsWith("validate")) {
            return MethodType.VALIDATE;
        }
        if (method.getName().startsWith("create")) {
            if (method.getName().startsWith("createBatch")) {
                return MethodType.CREATE_BULK;
            }
            if (method.getName().startsWith("createSocRolloverResultItems")) {
                return MethodType.CREATE_BULK;
            }
            if (method.getName().contains("FromExisting")) {
                return MethodType.CREATE_BULK;
            }
            ServiceMethodParameter infoParam = this.findInfoParameter(method);
            if (infoParam == null) {
                return MethodType.CREATE_BULK;
            }
            if (method.getReturnValue().getType().endsWith("List")) {
                return MethodType.CREATE_BULK;
            }
            return MethodType.CREATE;
        }
        if (method.getName().startsWith("add")) {
            return MethodType.ADD;
        }
        if (method.getName().startsWith("update")) {
            if (this.findInfoParameter(method) != null) {
                return MethodType.UPDATE;
            }
            return MethodType.UPDATE_OTHER;
        }
        if (method.getName().startsWith("delete")) {
            if (method.getName().contains("By")) {
                if (!method.getName().startsWith("deleteBy")) {
                    return MethodType.DELETE_OTHER;
                }
            }
            if (method.getName().contains("For")) {
                if (!method.getName().startsWith("deleteFor")) {
                    return MethodType.DELETE_OTHER;
                }
            }
            return MethodType.DELETE;
        }
        if (method.getName().startsWith("remove")) {
            return MethodType.REMOVE;
        }

        if (method.getName().startsWith("getCreate")) {
            return MethodType.GET_CREATE;
        }

        if (method.getName().startsWith("get")) {
            if (method.getName().endsWith("ByIds")) {
                return MethodType.GET_BY_IDS;
            }
            if (method.getName().endsWith("ByKeys")) {
                return MethodType.GET_BY_IDS;
            }
            if (method.getName().endsWith("ByType")) {
                return MethodType.GET_IDS_BY_TYPE;
            }
            if (method.getReturnValue().getType().endsWith("TypeInfo")) {
                return MethodType.GET_TYPE;
            }
            if (method.getReturnValue().getType().endsWith("TypeInfoList")) {
                return MethodType.GET_TYPES;
            }
            if (method.getName().endsWith("ByType")) {
                return MethodType.GET_IDS_BY_TYPE;
            }
            String splitName = splitCamelCase(method.getName());
            if (splitName.contains("_By_")) {
                if (method.getReturnValue().getType().equals("StringList")) {
                    return MethodType.GET_IDS_BY_OTHER;
                }
                if (method.getReturnValue().getType().endsWith("InfoList")) {
                    return MethodType.GET_INFOS_BY_OTHER;
                }
                return MethodType.UNKNOWN;
            }
            if (splitName.contains("_For_")) {
                if (method.getReturnValue().getType().equals("StringList")) {
                    return MethodType.GET_IDS_BY_OTHER;
                }
                if (method.getReturnValue().getType().endsWith("InfoList")) {
                    return MethodType.GET_INFOS_BY_OTHER;
                }
                return MethodType.UNKNOWN;
            }
            if (method.getParameters().size() >= 1 && method.getParameters().size() <= 2) {
                if (!method.getReturnValue().getType().endsWith("List")) {
                    if (method.getParameters().get(0).getName().endsWith("Id")) {
                        return MethodType.GET_BY_ID;
                    }
                    if (method.getParameters().get(0).getName().endsWith("Key")) {
                        return MethodType.GET_BY_ID;
                    }
                }
            }
        }
        if (method.getName().startsWith("searchFor")) {
            if (method.getName().endsWith("Ids")) {
                return MethodType.SEARCH_FOR_IDS;
            }
            if (method.getName().endsWith("Keys")) {
                return MethodType.SEARCH_FOR_IDS;
            }
            return MethodType.SEARCH_FOR_INFOS;
        }

        return MethodType.UNKNOWN;
    }

    private String calcTableName(XmlType xmlType) {
        String tableName = xmlType.getTableName();
        if (tableName != null) {
            return tableName;
        }
        return calcTableName(xmlType.getName());
    }

    private String calcTableName(String name) {
        if (name.endsWith("Info")) {
            name = name.substring(0, name.length() - "Info".length());
        }
        String tableName = "KSEN_" + splitCamelCase(name).toUpperCase();
        return tableName;
    }

    // got this from
    // http://stackoverflow.com/questions/2559759/how-do-i-convert-camelcase-into-human-readable-names-in-java
    private static String splitCamelCase(String s) {
        if (s == null) {
            return null;
        }
        return s.replaceAll(String.format("%s|%s|%s",
                "(?<=[A-Z])(?=[A-Z][a-z])", "(?<=[^A-Z])(?=[A-Z])",
                "(?<=[A-Za-z])(?=[^A-Za-z])"), "_");
    }

    private String calcColumnName(MessageStructure ms) {
        if (ms.getShortName().equalsIgnoreCase("TypeKey")) {
            String prefix = calcTableName(xmlType);
            if (prefix.startsWith("KSEN_")) {
                prefix = prefix.substring("KSEN_".length());
            }
            return prefix + "_TYPE";
        }
        if (ms.getShortName().equalsIgnoreCase("StateKey")) {
            String prefix = calcTableName(xmlType);
            if (prefix.startsWith("KSEN_")) {
                prefix = prefix.substring("KSEN_".length());
            }
            return prefix + "_STATE";
        }
        String name = splitCamelCase(ms.getShortName());
        // TODO: apply known abbreviations see https://wiki.kuali.org/display/STUDENT/Database+Abbreviations+for+R2
        return name.toUpperCase();
    }

    private boolean isNullable(MessageStructure ms) {
        if (ms.getRequired() == null) {
            return true;
        }
        if (ms.getRequired().equalsIgnoreCase("Required")) {
            return false;
        }
        // figure out what to do if it is qualified like
        // "required on update"
        return true;
    }

    private String keyOrId() {

        if (finder.findMessageStructure(xmlType.getName(), "id") != null) {
            return "Id";
        }
        if (finder.findMessageStructure(xmlType.getName(), "key") != null) {
            return "Key";
        }
        return "Id";
    }

    private boolean hasAttributes() {
        for (MessageStructure ms : finder.findMessageStructures(xmlType.getName())) {
            if (ms.getShortName().equals("attributes")) {
                return true;
            }
        }
        return false;
    }

    private boolean isMethodForThisType(XmlType xmlType, ServiceMethod method) {
        String objectName = calcObjectName(method);
        if (objectName == null) {
            return false;
        }
        if (xmlType.getName().equalsIgnoreCase(objectName + "Info")) {
            return true;
        }
        return false;
    }

    /**
     * Write out the entire file
     */
    public void write() {
        String className = calcClassName(servKey, xmlType);
        String entityName = JpaEntityWriter.calcClassName(servKey, xmlType);
        indentPrint("public class " + className);
        println(" extends GenericEntityDao<" + entityName + "> {");
        incrementIndent();
        importsAdd("org.kuali.student.r2.common.dao.GenericEntityDao");
        for (ServiceMethod method : methods) {
            if (!isMethodForThisType(xmlType, method)) {
                continue;
            }
            MethodType methodType = calcMethodType(method);
            String returnClass = null;
            switch (methodType) {
                case GET_IDS_BY_TYPE:
                    importsAdd(List.class.getName());
                    println("");
                    indentPrintln("public List<String> getIdsByType(String type) {");
                    incrementIndent();
                    importsAdd(Query.class.getName());
                    indentPrintln("Query query = em.createNamedQuery(\"" + entityName + ".getIdsByType\");");
                    indentPrintln("query.setParameter(\"type\", type);");
                    indentPrintln("return query.getResultList();");
                    decrementIndent();
                    indentPrintln("}");
                    break;
                case GET_IDS_BY_OTHER:
                case GET_INFOS_BY_OTHER:
                    importsAdd(List.class.getName());
                    importsAdd(Query.class.getName());
                    if (methodType.equals(MethodType.GET_IDS_BY_OTHER)) {
                        returnClass = "String";
                    } else {
                        returnClass = entityName;
                    }
                    println("");
                    String namedQuery = calcNamedQuery(method);
                    indentPrint("public List<" + returnClass + "> " + namedQuery + "(");
                    String comma = "";
                    for (ServiceMethodParameter param : method.getParameters()) {
                        if (param.getType().equals("ContextInfo")) {
                            continue;
                        }
                        print(comma);
                        comma = ", ";
                        String paramName = initLower(param.getName());
                        String javaClass = this.calcType(param.getType(), this.stripList(param.getType()));
                        print(javaClass + " " + paramName);
                    }
                    println(") {");
                    incrementIndent();
                    indentPrintln("Query query = em.createNamedQuery(\"" + entityName + "." + namedQuery + "\");");
                    for (ServiceMethodParameter param : method.getParameters()) {
                        if (param.getType().equals("ContextInfo")) {
                            continue;
                        }
                        String paramName = initLower(param.getName());
                        indentPrintln("query.setParameter(\"" + paramName + "\", " + paramName + ");");
                    }
                    indentPrintln("return query.getResultList();");
                    decrementIndent();
                    indentPrintln("}");
                    break;
            }
        }

        println("");
        closeBrace();

        this.writeJavaClassAndImportsOutToFile();
        this.getOut().close();
    }

    private String getInvalidParameterException() {
        if (this.isRice()) {
            return "RiceIllegalArgumentException";
        }
        return "InvalidParameterException";
    }

    private String getOperationFailedException() {
        if (this.isRice()) {
            return "RiceIllegalArgumentException";
        }
        return "OperationFailedException";
    }

    private String getDoesNotExistException() {
        if (this.isRice()) {
            return "RiceIllegalArgumentException";
        }
        return "DoesNotExistException";
    }

    private String getVersionMismatchException() {
        if (this.isRice()) {
            return "RiceIllegalStateException";
        }
        return "VersionMismatchException";
    }

    private void writeThrowsNotImplemented(ServiceMethod method) {
        indentPrintln("throw new " + this.getOperationFailedException() + " (\"" + method.getName() + " has not been implemented\");");
    }

    protected String initLower(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    private String calcCriteriaLookupServiceVariableName(ServiceMethod method) {
        String objectName = calcObjectName(method);
        String variableName = initLower(objectName) + "CriteriaLookupService";
        return variableName;
    }

    private ServiceMethodParameter findIdParameter(ServiceMethod method) {
        String idFieldName = calcObjectName(method) + "Id";
        for (ServiceMethodParameter parameter : method.getParameters()) {
            if (parameter.getType().equals("String")) {
                if (parameter.getName().equals(idFieldName)) {
                    return parameter;
                }
            }
        }

        // if only one parameter and it is a string then grab that
        if (method.getParameters().size() == 1) {
            for (ServiceMethodParameter parameter : method.getParameters()) {
                if (parameter.getType().equals("String")) {
                    return parameter;
                }
            }
        }
        // can't find name exactly 
        for (ServiceMethodParameter parameter : method.getParameters()) {
            if (parameter.getType().equals("String")) {
                if (parameter.getName().endsWith("Id")) {
                    return parameter;
                }
            }
        }
        // can't find name exactly try key 
        for (ServiceMethodParameter parameter : method.getParameters()) {
            if (parameter.getType().equals("String")) {
                if (!parameter.getName().endsWith("TypeKey")) {
                    if (parameter.getName().endsWith("Key")) {
                        return parameter;
                    }
                }
            }
        }
        log.warn("Could not find the Id paramter for {}.{} so returning the first one", method.getService(), method.getName());
        return method.getParameters().get(0);
    }

    private ServiceMethodParameter findContextParameter(ServiceMethod method) {
        for (ServiceMethodParameter parameter : method.getParameters()) {
            if (parameter.getType().equals("ContextInfo")) {
                return parameter;
            }
        }
        return null;
    }

    private ServiceMethodParameter findInfoParameter(ServiceMethod method) {
        String objectName = calcObjectName(method);
        if (!this.isRice()) {
            objectName = objectName + "Info";
        }
        for (ServiceMethodParameter parameter : method.getParameters()) {
            if (parameter.getType().equals(objectName)) {
                return parameter;
            }
        }
        if (method.getParameters().size() >= 1) {
            return method.getParameters().get(0);
        }
        return null;
    }

    private ServiceMethodParameter findTypeParameter(ServiceMethod method) {
        for (ServiceMethodParameter parameter : method.getParameters()) {
            if (parameter.getType().equals("String")) {
                if (parameter.getName().endsWith("TypeKey")) {
                    return parameter;
                }
                if (parameter.getName().endsWith("Type")) {
                    return parameter;
                }
            }
        }
        return null;
    }

    private String calcDaoVariableName(ServiceMethod method) {
        String daoVariableName = this.calcObjectName(method);
        daoVariableName = this.initLower(daoVariableName) + "Dao";
        return daoVariableName;
    }

    private String calcEntityClassName(ServiceMethod method) {
        String objectName = this.calcObjectName(method);
        objectName = objectName + "Entity";
        return objectName;
    }

    protected String calcObjectName(ServiceMethod method) {
        if (method.getName().startsWith("create")) {
            return method.getName().substring("create".length());
        }
        if (method.getName().startsWith("update")) {
            return method.getName().substring("update".length());
        }
        if (method.getName().startsWith("validate")) {
            return method.getName().substring("validate".length());
        }
        if (method.getName().startsWith("delete")) {
            return method.getName().substring("delete".length());
        }
        if (method.getName().startsWith("get")) {
            if (method.getReturnValue().getType().equals("StringList")) {
                if (method.getName().contains("IdsBy")) {
                    return method.getName().substring("get".length(),
                            method.getName().indexOf("IdsBy"));
                }
                if (method.getName().contains("KeysBy")) {
                    return method.getName().substring("get".length(),
                            method.getName().indexOf("KeysBy"));
                }
                if (method.getName().contains("IdsFor")) {
                    return method.getName().substring("get".length(),
                            method.getName().indexOf("IdsFor"));
                }
                if (method.getName().contains("With")) {
                    return method.getName().substring("get".length(),
                            method.getName().indexOf("With"));
                }
                if (method.getName().contains("By")) {
                    return method.getName().substring("get".length(),
                            method.getName().indexOf("By"));
                }
                return method.getName().substring("get".length());
            }
            String name = method.getReturnValue().getType();
            if (name.endsWith("List")) {
                name = name.substring(0, name.length() - "List".length());
            }
            if (name.endsWith("Info")) {
                name = name.substring(0, name.length() - "Info".length());
            }
            return name;
        }

        if (method.getName().startsWith("searchFor")) {
            if (method.getReturnValue().getType().equals("StringList")) {
                if (method.getName().endsWith("Ids")) {
                    return method.getName().substring("searchFor".length(),
                            method.getName().indexOf("Ids"));
                }
                if (method.getName().endsWith("Keys")) {
                    return method.getName().substring("get".length(),
                            method.getName().indexOf("Keys"));
                }
                return method.getName().substring("searchFor".length());
            }
            String name = method.getReturnValue().getType();
            if (name.endsWith("List")) {
                name = name.substring(0, name.length() - "List".length());
            }
            if (name.endsWith("Info")) {
                name = name.substring(0, name.length() - "Info".length());
            }
            return name;
        }
        if (method.getName().startsWith("add")) {
            return method.getName().substring("add".length());
        }
        if (method.getName().startsWith("remove")) {
            return method.getName().substring("remove".length());
        }
        String returnType = this.stripList(method.getReturnValue().getType());
        XmlType type = this.finder.findXmlType(returnType);
        if (type.getPrimitive().equals(XmlType.COMPLEX)) {
            return returnType;
        }
        return null;
    }

    private String calcNamedQuery(ServiceMethod method) {
        String objectName = calcObjectName(method);
        String name = method.getName();
        if (name.startsWith("get")) {
            name = name.substring("get".length());
        }
        if (name.startsWith(objectName)) {
            name = name.substring(objectName.length());
        }
//        if (name.startsWith("Ids")) {
//            name = name.substring("Ids".length());
//        }
//        if (name.isEmpty()) {
//            throw new RuntimeException (method.getName());
//        }
        // remove plural
        if (!method.getReturnValue().getType().equals("StringList")) {
            if (name.startsWith("s")) {
                name = name.substring("s".length());
            }
        }
        // add back the get
        name = "get" + name;
        return name;
    }

    private ServiceMethodParameter findCriteriaParam(ServiceMethod method) {
        for (ServiceMethodParameter param : method.getParameters()) {
            if (param.getType().equals("QueryByCriteria")) {
                return param;
            }
        }
        return null;
    }

    private ServiceMethodParameter getTypeParameter(ServiceMethod method) {
        ServiceMethodParameter fallbackParam = null;
        for (ServiceMethodParameter parameter : method.getParameters()) {
            if (parameter.getName().endsWith("TypeKey")) {
                return parameter;
            }
            if (parameter.getType().equals("String")) {
                if (parameter.getName().toLowerCase().contains("type")) {
                    fallbackParam = parameter;
                }
            }
        }
        return fallbackParam;
    }

    private String calcInfoName(ServiceMethod method) {
        String objectName = this.calcObjectName(method);
        String infoName = objectName;
        if (!this.isRice()) {
            infoName = infoName + "Info";
        }
        return infoName;
    }

    private String initUpper(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private ServiceMethodParameter findIdListParameter(ServiceMethod method) {
        String idFieldName = calcObjectName(method) + "Ids";
        if (this.isRice()) {
            idFieldName = "ids";
        }
        for (ServiceMethodParameter parameter : method.getParameters()) {
            if (parameter.getType().equals("StringList")) {
                if (parameter.getName().equals(idFieldName)) {
                    return parameter;
                }
            }
        }
        // can't find name exactly 
        for (ServiceMethodParameter parameter : method.getParameters()) {
            if (parameter.getType().equals("StringList")) {
                if (parameter.getName().endsWith("Ids")) {
                    return parameter;
                }
            }
        }
        // can't find name exactly try key 
        for (ServiceMethodParameter parameter : method.getParameters()) {
            if (parameter.getType().equals("StringList")) {
                if (parameter.getName().endsWith("Keys")) {
                    return parameter;
                }
            }
        }
        return null;
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

    private String calcType(String type, String realType) {
        XmlType t = finder.findXmlType(this.stripList(type));
        String retType = MessageStructureTypeCalculator.calculate(this, model, type, realType,
                t.getJavaPackage());
        if (this.isRice()) {
            if (retType.equals("Boolean")) {
                retType = "boolean";
            }
            if (retType.equals("Void")) {
                retType = "void";
            }
        }
        return retType;
    }
}
