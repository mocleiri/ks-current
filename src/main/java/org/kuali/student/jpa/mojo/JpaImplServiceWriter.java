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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import org.kuali.rice.core.api.criteria.CriteriaLookupService;
import org.kuali.rice.core.api.criteria.GenericQueryResults;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author nwright
 */
public class JpaImplServiceWriter extends JavaClassWriter {

    private static Logger log = LoggerFactory.getLogger(JpaImplServiceWriter.class);

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

    protected List<ServiceMethod> methods;

    /**
     * A flag that holds if the service is an R1 service.
     */
    private boolean isR1;

    //////////////////////////
    // Constructor
    //////////////////////////
    public JpaImplServiceWriter(ServiceContractModel model,
            String directory,
            String rootPackage,
            String servKey,
            List<ServiceMethod> methods,
            boolean isR1) {
        super(directory, calcPackage(servKey, rootPackage), calcClassName(servKey));
        this.model = model;
        this.finder = new ModelFinder(model);
        this.directory = directory;
        this.rootPackage = rootPackage;
        this.servKey = servKey;
        this.methods = methods;
        this.isR1 = isR1;
    }

    public JpaImplServiceWriter(ServiceContractModel model,
            String directory,
            String rootPackage,
            String servKey,
            List<ServiceMethod> methods,
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
    public static String calcClassName(String servKey) {
        return GetterSetterNameCalculator.calcInitUpper(fixServKey(servKey) + "ServiceJpaImpl");
    }

    public static String calcServiceInterfaceClassName(String servKey) {
        return GetterSetterNameCalculator.calcInitUpper(fixServKey(servKey) + "Service");
    }

    /**
     * Analyses the method and returns a MethodType enum that describes what
     * type of method this is.
     */
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
            if (splitName.contains(" By ")) {
                if (method.getReturnValue().getType().equals("StringList")) {
                    return MethodType.GET_IDS_BY_OTHER;
                }
                if (method.getReturnValue().getType().endsWith("InfoList")) {
                    return MethodType.GET_INFOS_BY_OTHER;
                }
                return MethodType.UNKNOWN;
            }
            if (splitName.contains(" For ")) {
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

    // got this from
    // http://stackoverflow.com/questions/2559759/how-do-i-convert-camelcase-into-human-readable-names-in-java
    private static String splitCamelCase(String s) {
        if (s == null) {
            return null;
        }
        return s.replaceAll(String.format("%s|%s|%s",
                "(?<=[A-Z])(?=[A-Z][a-z])", "(?<=[^A-Z])(?=[A-Z])",
                "(?<=[A-Za-z])(?=[^A-Za-z])"), " ");
    }

    /**
     * Write out the entire file
     */
    public void write() {
        indentPrintln("@Transactional(readOnly = true, noRollbackFor = {DoesNotExistException.class}, rollbackFor = {Throwable.class})");
        importsAdd(Transactional.class.getName());
        indentPrint("public class " + calcClassName(servKey));
        println(" implements " + calcServiceInterfaceClassName(servKey));
        Service serv = finder.findService(servKey);
        importsAdd(serv.getImplProject() + "." + serv.getName());
        openBrace();
        // put all the cache variables at the top
        indentPrintln("// dao variables ");
        jpaEntitiesWritten.clear();
        for (ServiceMethod method : methods) {
            MethodType methodType = calcMethodType(method);
            switch (methodType) {
                case CREATE:
                case GET_TYPE:
                case GET_BY_ID:
                    writeDaoVariable(method);
            }
        }

        for (ServiceMethod method : methods) {
            MethodType methodType = calcMethodType(method);
            println("");
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
            switch (methodType) {
                case CREATE:
                    indentPrintln("@Transactional");
                    break;
                case ADD:
                    indentPrintln("@Transactional");
                    break;
                case UPDATE:
                    indentPrintln("@Transactional");
                    break;
                case DELETE:
                    indentPrintln("@Transactional");
                    break;
                case REMOVE:
                    indentPrintln("@Transactional");
                    break;
                case GET_BY_ID:
                    indentPrintln("@Transactional(readOnly = true)");
                    break;
                case GET_BY_IDS:
                    indentPrintln("@Transactional(readOnly = true)");
                    break;
                case GET_IDS_BY_TYPE:
                    indentPrintln("@Transactional(readOnly = true)");
                    break;
                case GET_IDS_BY_OTHER:
                    indentPrintln("@Transactional(readOnly = true)");
                    break;
                case GET_INFOS_BY_OTHER:
                    indentPrintln("@Transactional(readOnly = true)");
                    break;
                case GET_TYPE:
                    indentPrintln("@Transactional(readOnly = true)");
                    break;
                case GET_TYPES:
                    indentPrintln("@Transactional(readOnly = true)");
                    break;
                case RICE_GET_BY_NAMESPACE_AND_NAME:
                    indentPrintln("@Transactional(readOnly = true)");
                    break;
                default:
            }
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
            indentPrintln("// " + methodType);
            switch (methodType) {
                case VALIDATE:
                    writeValidate(method);
                    break;
                case CREATE:
                    writeCreate(method);
                    break;
                case ADD:
                    writeAdd(method);
                    break;
                case UPDATE:
                    writeUpdate(method);
                    break;
                case DELETE:
                    writeDelete(method);
                    break;
                case REMOVE:
                    writeRemove(method);
                    break;
                case GET_BY_ID:
                    writeGetById(method);
                    break;
                case GET_BY_IDS:
                    writeGetByIds(method);
                    break;
                case GET_IDS_BY_TYPE:
                    writeGetIdsByType(method);
                    break;
                case GET_IDS_BY_OTHER:
                    writeGetIdsByOther(method);
                    break;
                case GET_INFOS_BY_OTHER:
                    writeGetInfosByOther(method);
                    break;
                case SEARCH_FOR_IDS:
                    writeSearchForIds(method);
                    break;
                case SEARCH_FOR_INFOS:
                    writeSearchForInfos(method);
                    break;
                case GET_TYPE:
                    writeGetType(method);
                    break;
                case GET_TYPES:
                    writeGetTypes(method);
                    break;
                case RICE_GET_BY_NAMESPACE_AND_NAME:
                    writeRiceGetByNamespaceAndName(method);
                    break;
                default:
                    writeThrowsNotImplemented(method);
            }
            closeBrace();
        }

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

    private void writeValidate(ServiceMethod method) {
        indentPrintln("return new ArrayList<ValidationResultInfo> ();");
        this.importsAdd(ArrayList.class.getName());
    }
    private final Set<String> jpaEntitiesWritten = new HashSet<String>();

    private void writeDaoVariable(ServiceMethod method) {
        String objectName = calcObjectName(method);
        String className = objectName + "Dao";
        String variableName = calcDaoVariableName(method);
        if (jpaEntitiesWritten.add(variableName)) {
            XmlType xmlType = finder.findXmlType(objectName + "Info");
            if (xmlType == null) {
                System.out.println ("Cannot write DAO because the object does not follow patterns.  ObjectName=" 
                        + objectName + " method=" + method);
                return;
            }
            new JpaEntityWriter(model, directory, rootPackage, servKey, methods, xmlType, isR1).write();
            new JpaDaoWriter(model, directory, rootPackage, servKey, methods, xmlType, isR1).write();
            println("");
            indentPrintln("private " + className + " " + variableName + ";");
            println("");
            indentPrintln("public void set" + className + "(" + className + " " + variableName + ") {");
            incrementIndent();
            indentPrintln("this." + variableName + " = " + variableName + ";");
            decrementIndent();
            indentPrintln("}");
            println("");
            indentPrintln("public " + className + " get" + className + "() {");
            incrementIndent();
            indentPrintln("return this." + variableName + ";");
            decrementIndent();
            indentPrintln("}");

            variableName = calcCriteriaLookupServiceVariableName(method);

            importsAdd(CriteriaLookupService.class.getName());
            className = "CriteriaLookupService";
            println("");
            indentPrintln("// Criteria Lookup for this object");
            indentPrintln("private " + className + " " + variableName + ";");
            println("");
            indentPrintln("public void set" + initUpper(variableName) + "(" + className + " " + variableName + ") {");
            incrementIndent();
            indentPrintln("this." + variableName + " = " + variableName + ";");
            decrementIndent();
            indentPrintln("}");
            println("");
            indentPrintln("public " + className + " get" + initUpper(variableName) + "() {");
            incrementIndent();
            indentPrintln("return this." + variableName + ";");
            decrementIndent();
            indentPrintln("}");

        }
    }

    private void writeCreate(ServiceMethod method) {

//        holdInfo.setPersonId(personId);
//        holdInfo.setHoldIssueId(issueId);
//        holdInfo.setTypeKey(holdTypeKey);
//
//        HoldIssueEntity holdIssueEntity = holdIssueDao.find(issueId);
//        if (holdIssueEntity == null) {
//            throw new InvalidParameterException(issueId);
//        }
//        AppliedHoldEntity entity = new AppliedHoldEntity(holdInfo);
//        entity.setHoldIssue(holdIssueEntity);
//        entity.setEntityCreated(context);
//        appliedHoldDao.persist(entity);
//        appliedHoldDao.getEm().flush();
//        return entity.toDto();
        String daoVariable = calcDaoVariableName(method);
        String entityClassName = calcEntityClassName(method);
        ServiceMethodParameter typeParam = this.findTypeParameter(method);
        ServiceMethodParameter infoParam = this.findInfoParameter(method);
        ServiceMethodParameter contextParam = this.findContextParameter(method);
        String objectName = calcObjectName(method);
        String infoName = objectName;
        if (!this.isRice()) {
            infoName = infoName + "Info";
        }
        String daoName = calcDaoVariableName(method);
        if (typeParam != null) {
            indentPrintln(infoParam.getName() + ".setTypeKey (" + typeParam.getName() + ");");
        }
        if (method.getParameters().size() > 3) {
            indentPrintln("//TODO: JPAIMPL overwrite the rest of the readonly fields that are specified on the create in the info object");
        }
        indentPrintln(entityClassName + " entity = new " + entityClassName + "(" + infoParam.getName() + ");");
        indentPrintln("entity.setEntityCreated(" + contextParam.getName() + ");");
        indentPrintln(daoVariable + ".persist(entity);");
        indentPrintln(daoVariable + ".getEm().flush();");
        indentPrintln("return entity.toDto();");
    }

    private void writeAdd(ServiceMethod method) {
        indentPrintln ("//TODO: JPAIMPL this needs to be implemented");
        indentPrintln ("throw new OperationFailedException (\"Not implemented\");");
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
        log.warn("Could not find the Id paramter for " + method.getService() + "." + method.getName() + " so returning the first one");
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
        throw new IllegalArgumentException(method.getName());
    }

    private void writeUpdate(ServiceMethod method) {
//        if (!holdId.equals(holdInfo.getId())) {
//            throw new InvalidParameterException(holdId + " does not match the id in the object " + holdInfo.getId());
//        }
//        AppliedHoldEntity entity = appliedHoldDao.find(holdId);
//        if (null == entity) {
//            throw new DoesNotExistException(holdId);
//        }
//        entity.fromDto(holdInfo);
//        entity.setEntityUpdated(context);
//        entity = appliedHoldDao.merge(entity);
//        appliedHoldDao.getEm().flush(); // need to flush to get the version indicator updated
//        return entity.toDto();
        ServiceMethodParameter idParam = this.findIdParameter(method);
        ServiceMethodParameter infoParam = this.findInfoParameter(method);
        ServiceMethodParameter contextParam = this.findContextParameter(method);
        String daoVariable = calcDaoVariableName(method);
        String entityClassName = calcEntityClassName(method);
        if (infoParam == null) {
            throw new NullPointerException(method.getName());
        }
        String objectName = calcObjectName(method);
        String infoName = objectName;
        if (!this.isRice()) {
            infoName = infoName + "Info";
        }
        if (idParam != null) {
            if (!this.isRice()) {
                indentPrintln("if (!" + idParam.getName() + ".equals (" + infoParam.getName() + ".getId())) {");
                indentPrintln("    throw new " + this.getInvalidParameterException() + " (\"The id parameter does not match the id on the info object\");");
                indentPrintln("}");
            }
        }
        indentPrintln(entityClassName + " entity = " + daoVariable + ".find(" + idParam.getName() + ");");
        indentPrintln("if (entity == null) {");
        incrementIndent();
        indentPrintln("throw new DoesNotExistException(" + idParam.getName() + ");");
        decrementIndent();
        indentPrintln("}");
        indentPrintln("entity.fromDto(" + infoParam.getName() + ");");
        indentPrintln("entity.setEntityUpdated(" + contextParam.getName() + ");");
        indentPrintln("entity = " + daoVariable + ".merge(entity);");
        indentPrintln("return entity.toDto();");
    }

    private void writeDelete(ServiceMethod method) {
//        AppliedHoldEntity entity = appliedHoldDao.find(holdId);
//        if (null == entity) {
//            throw new DoesNotExistException(holdId);
//        }
//        appliedHoldDao.remove(entity);
//        StatusInfo status = new StatusInfo();
//        status.setSuccess(Boolean.TRUE);
//        return status;
        ServiceMethodParameter idParam = this.findIdParameter(method);
        String daoVariable = calcDaoVariableName(method);
        String entityClassName = calcEntityClassName(method);
        indentPrintln(entityClassName + " entity = " + daoVariable + ".find(" + idParam.getName() + ");");
        indentPrintln("if (entity == null) {");
        incrementIndent();
        indentPrintln("throw new DoesNotExistException(" + idParam.getName() + ");");
        decrementIndent();
        indentPrintln("}");
        indentPrintln(daoVariable + ".remove(entity);");
        indentPrintln("StatusInfo status = new StatusInfo();");
        importsAdd("org.kuali.student.r2.common.dto.StatusInfo");
        indentPrintln("status.setSuccess(Boolean.TRUE);");
        indentPrintln("return status;");
    }

    private void writeRemove(ServiceMethod method) {
        indentPrintln ("//TODO: JPAIMPL this needs to be implemented");
        indentPrintln ("throw new OperationFailedException (\"Not implemented\");");
    }

    private void writeGetById(ServiceMethod method) {
//        AppliedHoldEntity entity = appliedHoldDao.find(holdId);
//        if (entity == null) {
//            throw new DoesNotExistException(holdId);
//        }
//        return entity.toDto();
        ServiceMethodParameter idParam = this.findIdParameter(method);
        String daoVariable = calcDaoVariableName(method);
        String entityClassName = calcEntityClassName(method);
        indentPrintln(entityClassName + " entity = " + daoVariable + ".find(" + idParam.getName() + ");");
        indentPrintln("if (entity == null) {");
        incrementIndent();
        indentPrintln("throw new DoesNotExistException(" + idParam.getName() + ");");
        decrementIndent();
        indentPrintln("}");
        indentPrintln("return entity.toDto();");
    }

    private void writeGetByIds(ServiceMethod method) {
//        List<HoldIssueEntity> holdIssues = holdIssueDao.findByIds(issueIds);
//        List<HoldIssueInfo> result = new ArrayList<HoldIssueInfo>(holdIssues.size());
//        for (HoldIssueEntity entity : holdIssues) {
//            if (entity == null) {
//                // if one of the entities from "findByIds" is returned as null, then one of the keys in the list was not found
//                throw new DoesNotExistException();
//            }
//            result.add(entity.toDto());
//        }
//        return result;
        String daoVariable = calcDaoVariableName(method);
        String entityClassName = calcEntityClassName(method);
        String objectName = this.calcObjectName(method);
        ServiceMethodParameter idListParam = this.findIdListParameter(method);
        String infoName = objectName;
        infoName = infoName + "Info";
        this.importsAdd(ArrayList.class.getName());
        indentPrintln("List<" + entityClassName + "> entities = " + daoVariable + ".findByIds(" + idListParam.getName() + ");");
        indentPrintln("List<" + infoName + "> list = new ArrayList<" + infoName + "> (entities.size());");
        indentPrintln("for (" + entityClassName + " entity : entities) {");
        incrementIndent();
        indentPrintln("if (entity == null) {");
        incrementIndent();
        indentPrintln("throw new DoesNotExistException();");
        decrementIndent();
        indentPrintln("}");
        indentPrintln("list.add(entity.toDto());");
        decrementIndent();
        indentPrintln("}");
        indentPrintln("return list;");
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

    private void writeGetIdsByOther(ServiceMethod method) {
//      return appliedHoldDao.getIdsByIssue(issueId);
        String objectName = this.calcObjectName(method);
        String infoName = objectName;
        if (!this.isRice()) {
            infoName = infoName + "Info";
        }
        String daoVariableName = calcDaoVariableName(method);
        String namedQuery = calcNamedQuery(method);
        indentPrint("return " + daoVariableName + "." + namedQuery + "(");
        String comma = "";
        for (ServiceMethodParameter param : method.getParameters()) {
            if (param.getType().equals("ContextInfo")) {
                continue;
            }
            print(comma);
            comma = ", ";
            print(param.getName());
        }
        println(");");
    }

    private ServiceMethodParameter findCriteriaParam(ServiceMethod method) {
        for (ServiceMethodParameter param : method.getParameters()) {
            if (param.getType().equals("QueryByCriteria")) {
                return param;
            }
        }
        return null;
    }

    private void writeSearchForIds(ServiceMethod method) {
//        List<String> results = new ArrayList<String>();
//        GenericQueryResults<AppliedHoldEntity> appliedHolds = criteriaLookupService.lookup(AppliedHoldEntity.class, criteria);
//        if (null != appliedHolds && appliedHolds.getResults().size() > 0) {
//            for (AppliedHoldEntity appliedHold : appliedHolds.getResults()) {
//                results.add(appliedHold.getId());
//            }
//        }
//        return results;
        String objectName = this.calcObjectName(method);
        String infoName = objectName;
        if (!this.isRice()) {
            infoName = infoName + "Info";
        }
        String criteriaLookupVariableName = calcCriteriaLookupServiceVariableName(method);
        String entityClassName = this.calcEntityClassName(method);
        ServiceMethodParameter criteriaParam = findCriteriaParam(method);
        importsAdd(CriteriaLookupService.class.getName());
        importsAdd(ArrayList.class.getName());
        importsAdd(List.class.getName());
        indentPrintln("List<String> results = new ArrayList<String>();");
        importsAdd (GenericQueryResults.class.getName ());
        indentPrintln("GenericQueryResults<" + entityClassName + "> entities");
        indentPrintln("    = " + criteriaLookupVariableName + ".lookup(" + entityClassName + ".class, " + criteriaParam.getName() + ");");
        indentPrintln("if (null != entities && !entities.getResults().isEmpty()) {");
        incrementIndent();
        indentPrintln("for (" + entityClassName + " entity : entities.getResults()) {");
        incrementIndent();
        indentPrintln("results.add(entity.getId());");
        decrementIndent();
        indentPrintln("}");
        decrementIndent();
        indentPrintln("}");
        indentPrintln("return results;");
    }

    private void writeSearchForInfos(ServiceMethod method) {
//        List<AppliedHoldInfo> results = new ArrayList<AppliedHoldInfo>();
//        GenericQueryResults<AppliedHoldEntity> appliedHolds = criteriaLookupService.lookup(AppliedHoldEntity.class, criteria);
//        if (null != appliedHolds && appliedHolds.getResults().size() > 0) {
//            for (AppliedHoldEntity appliedHold : appliedHolds.getResults()) {
//                results.add(appliedHold.toDto());
//            }
//        }
//        return results;
        String objectName = this.calcObjectName(method);
        String criteriaLookupVariableName = calcCriteriaLookupServiceVariableName(method);
        String entityClassName = this.calcEntityClassName(method);
        String infoName = this.calcInfoName(method);
        ServiceMethodParameter criteriaParam = findCriteriaParam(method);
        importsAdd(CriteriaLookupService.class.getName());
        importsAdd(ArrayList.class.getName());
        importsAdd(List.class.getName());
        indentPrintln("List<" + infoName + "> results = new ArrayList<" + infoName + ">();");
        importsAdd (GenericQueryResults.class.getName ());
        indentPrintln("GenericQueryResults<" + entityClassName + "> entities");
        indentPrintln("    = " + criteriaLookupVariableName + ".lookup(" + entityClassName + ".class, " + criteriaParam.getName() + ");");
        indentPrintln("if (null != entities && !entities.getResults().isEmpty()) {");
        incrementIndent();
        indentPrintln("for (" + entityClassName + " entity : entities.getResults()) {");
        incrementIndent();
        indentPrintln("results.add(entity.toDto());");
        decrementIndent();
        indentPrintln("}");
        decrementIndent();
        indentPrintln("}");
        indentPrintln("return results;");
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

    private void writeGetIdsByType(ServiceMethod method) {
        String objectName = this.calcObjectName(method);
        String infoName = this.calcInfoName(method);
        String daoVariable = calcDaoVariableName(method);
        ServiceMethodParameter typeParam = this.getTypeParameter(method);
        if (typeParam == null) {

        }
        indentPrintln("return " + daoVariable + ".getIdsByType(" + typeParam.getName() + ");");
    }

    private void writeRiceGetByNamespaceAndName(ServiceMethod method) {
        indentPrintln ("//TODO: JPAIMPL this needs to be implemented");
        indentPrintln ("throw new OperationFailedException (\"Not implemented\");");
    }

    private void writeGetInfosByOther(ServiceMethod method) {
//        List<AppliedHoldEntity> entities = this.appliedHoldDao.getByPerson(personId);
//        List<AppliedHoldInfo> result = new ArrayList<AppliedHoldInfo>(entities.size());
//        for (AppliedHoldEntity entity : entities) {
//            result.add(entity.toDto());
//        }
//        return result;
        String objectName = this.calcObjectName(method);
        String infoName = objectName;
        if (!this.isRice()) {
            infoName = infoName + "Info";
        }
        String daoVariableName = calcDaoVariableName(method);
        String namedQuery = calcNamedQuery(method);
        String entityClassName = calcEntityClassName(method);
        indentPrint("List<" + entityClassName + "> entities = " + daoVariableName + "." + namedQuery + "(");
        String comma = "";
        for (ServiceMethodParameter param : method.getParameters()) {
            if (param.getType().equals("ContextInfo")) {
                continue;
            }
            print(comma);
            comma = ", ";
            print(param.getName());
        }
        println(");");
        this.importsAdd(ArrayList.class.getName());
        indentPrintln("List<" + infoName + "> list = new ArrayList<" + infoName + "> (entities.size());");
        indentPrintln("for (" + entityClassName + " entity: entities) {");
        indentPrintln("    list.add (entity.toDto ());");
        indentPrintln("}");
        indentPrintln("return list;");
    }

    private void writeGetType(ServiceMethod method) {
        indentPrintln ("//TODO: JPAIMPL this needs to be implemented");
        indentPrintln ("throw new OperationFailedException (\"Not implemented\");");
    }

    private void writeGetTypes(ServiceMethod method) {
        indentPrintln ("//TODO: JPAIMPL this needs to be implemented");
        indentPrintln ("throw new OperationFailedException (\"Not implemented\");");
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
