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
package org.kuali.student.mock.mojo;

import org.kuali.student.contract.model.Service;
import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.ServiceMethod;
import org.kuali.student.contract.model.ServiceMethodError;
import org.kuali.student.contract.model.ServiceMethodParameter;
import org.kuali.student.contract.model.XmlType;
import org.kuali.student.contract.model.impl.ServiceContractModelPescXsdLoader;
import org.kuali.student.contract.model.util.ModelFinder;
import org.kuali.student.contract.writer.JavaClassWriter;
import org.kuali.student.contract.writer.service.GetterSetterNameCalculator;
import org.kuali.student.contract.writer.service.MessageStructureTypeCalculator;
import org.kuali.student.contract.writer.service.ServiceExceptionWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 *
 * @author nwright
 */
public class MockImplServiceWriter extends JavaClassWriter {
    
    private static Logger log = LoggerFactory.getLogger(MockImplServiceWriter.class);

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
        UNKNOWN
    };

    //////////////////////////////
    // Data Variables
    //////////////////////////////

    protected ServiceContractModel model;
    protected ModelFinder finder;
    private String directory;
    /**
     * The package name is stored in the service object itself (the package spec kept
     * moving around so I assumed the actual service name was unique but ran into a problem
     * when we included rice because they have a StateService  meaning US states and we have
     * a StateService meaning the state of the object so I added logic to detect rice and
     * prepend that "RICE." to it
     */
    private String rootPackage;

    /**
     * Name of the service being operated on.
     * If it is a RICE service it is prefixed with RICE.
     * [11:32:18 AM] Norman Wright: short name... I think it gets it by taking the java class SimpleName and stripping off the word "Service" and I think making it lower case.
     * [11:32:24 AM] Norman Wright: so OrganizationService becomes organization
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

    public MockImplServiceWriter(ServiceContractModel model,
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

    public MockImplServiceWriter(ServiceContractModel model,
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
     * Returns the mock implementation package name.
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
        pack = pack + "service.impl.mock";
        return pack;
    }

    /**
     * Checks if this is a RICE service.
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
     * Given the service key (name), returns a calculated class name for the mock impl.
     */
    public static String calcClassName(String servKey) {
        return GetterSetterNameCalculator.calcInitUpper(fixServKey(servKey) + "ServiceMockImpl");
    }

    public static String calcServiceInterfaceClassName(String servKey) {
        return GetterSetterNameCalculator.calcInitUpper(fixServKey(servKey) + "Service");
    }

    /**
     * Analyses the method and returns a MethodType enum that describes what type of method this is.
     */
    protected MethodType calcMethodType(ServiceMethod method) {
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
            if (this.findInfoParameter(method) != null) {
                return MethodType.CREATE;
            }
            return MethodType.CREATE_BULK;
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
            if (method.getParameters().size() >= 1 && method.getParameters().size() <= 2) {
                if (!method.getReturnValue().getType().endsWith("List")) {
                    if (method.getParameters().get(0).getName().endsWith("Id")) {
                        return MethodType.GET_BY_ID;
                    }
                }
            }
            if (method.getName().contains("By")) {
                if (method.getReturnValue().getType().equals("StringList")) {
                    return MethodType.GET_IDS_BY_OTHER;
                }
                if (method.getReturnValue().getType().endsWith("InfoList")) {
                    return MethodType.GET_INFOS_BY_OTHER;
                }
            }
        }

        return MethodType.UNKNOWN;
    }

    /**
     * Write out the entire file
     */
    public void write() {
        indentPrint("public class " + calcClassName(servKey));
        println(" implements MockService, " + calcServiceInterfaceClassName(servKey));
        importsAdd ("org.kuali.student.common.mock.MockService");
        Service serv = finder.findService(servKey);
        importsAdd(serv.getImplProject() + "." + serv.getName());
        openBrace();
        // put all the cache variables at the top
        indentPrintln("// cache variable ");
        indentPrintln("// The LinkedHashMap is just so the values come back in a predictable order");
        cacheVariablesWritten.clear();
        for (ServiceMethod method : methods) {
            MethodType methodType = calcMethodType(method);
            switch (methodType) {
                case CREATE:
                case ADD:
                case GET_TYPE:
                case GET_BY_ID:
                    writeCacheVariable(method);
            }
        }
        println ("");
        indentPrintln ("@Override");
        indentPrintln ("public void clear()");
        openBrace ();
        cacheVariablesWritten.clear();
         for (ServiceMethod method : methods) {
            MethodType methodType = calcMethodType(method);
            switch (methodType) {
                case CREATE:
                case ADD:
                case GET_TYPE:
                case GET_BY_ID:
                    writeCacheVariableClear(method);
            }
        }
        closeBrace ();
        println ("");
        
        for (ServiceMethod method : methods) {
            MethodType methodType = calcMethodType(method);
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

        writeBoilerPlate();
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

    private void writeBoilerPlate() {
        if (this.isRice()) {
            return;
        }
        indentPrintln("");
        indentPrintln("private StatusInfo newStatus() {");
        indentPrintln("     StatusInfo status = new StatusInfo();");
        indentPrintln("     status.setSuccess(Boolean.TRUE);");
        indentPrintln("     return status;");
        indentPrintln("}");
        if (isR1) {
            this.writeBoilerPlateR1();
        } else {
            this.writeBoilerPlateR2();
        }
    }

    private void writeBoilerPlateR1() {
        importsAdd("org.kuali.student.common.dto.MetaInfo");
        indentPrintln("");

        indentPrintln("private MetaInfo newMeta() {");
        indentPrintln("     MetaInfo meta = new MetaInfo();");
        indentPrintln("     meta.setCreateId(\"MOCKUSER\");");
        importsAdd(Date.class.getName());
        indentPrintln("     meta.setCreateTime(new Date());");
        indentPrintln("     meta.setUpdateId(\"MOCKUSER\");");
        indentPrintln("     meta.setUpdateTime(meta.getCreateTime());");
        indentPrintln("     meta.setVersionInd(\"0\");");
        indentPrintln("     return meta;");
        indentPrintln("}");
        indentPrintln("");
        indentPrintln("private MetaInfo updateMeta(MetaInfo meta) {");
        indentPrintln("     meta.setUpdateId(\"MOCKUSER\");");
        indentPrintln("     meta.setUpdateTime(new Date());");
        indentPrintln("     meta.setVersionInd((Integer.parseInt(meta.getVersionInd()) + 1) + \"\");");
        indentPrintln("     return meta;");
        indentPrintln("}");
        indentPrintln("");

    }

    private void writeBoilerPlateR2() {
        importsAdd("org.kuali.student.r2.common.dto.MetaInfo");
        indentPrintln("");

        indentPrintln("private MetaInfo newMeta(ContextInfo context) {");
        indentPrintln("     MetaInfo meta = new MetaInfo();");
        indentPrintln("     meta.setCreateId(context.getPrincipalId());");
        importsAdd(Date.class.getName());
        indentPrintln("     meta.setCreateTime(new Date());");
        indentPrintln("     meta.setUpdateId(context.getPrincipalId());");
        indentPrintln("     meta.setUpdateTime(meta.getCreateTime());");
        indentPrintln("     meta.setVersionInd(\"0\");");
        indentPrintln("     return meta;");
        indentPrintln("}");
        indentPrintln("");
        indentPrintln("private MetaInfo updateMeta(MetaInfo old, ContextInfo context) {");
        indentPrintln("     MetaInfo meta = new MetaInfo(old);");
        indentPrintln("     meta.setUpdateId(context.getPrincipalId());");
        indentPrintln("     meta.setUpdateTime(new Date());");
        indentPrintln("     meta.setVersionInd((Integer.parseInt(meta.getVersionInd()) + 1) + \"\");");
        indentPrintln("     return meta;");
        indentPrintln("}");
        indentPrintln("");

    }

    private void writeValidate(ServiceMethod method) {
        indentPrintln("return new ArrayList<ValidationResultInfo> ();");
        this.importsAdd(ArrayList.class.getName());
    }
    private Set<String> cacheVariablesWritten = new HashSet<String>();

    private void writeCacheVariable(ServiceMethod method) {
        String objectName = calcObjectName(method);
        if (!this.isRice()) {
            objectName = objectName + "Info";
        }
        String mapName = calcMapName(method);
        if (cacheVariablesWritten.add(mapName)) {
            indentPrintln("private Map<String, " + objectName + "> " + mapName + " = new LinkedHashMap<String, " + objectName + ">();");
            importsAdd(Map.class.getName());
            importsAdd(LinkedHashMap.class.getName());
        }
    }

    private void writeCacheVariableClear(ServiceMethod method) {
        String objectName = calcObjectName(method);
        if (!this.isRice()) {
            objectName = objectName + "Info";
        }
        String mapName = calcMapName(method);
        if (cacheVariablesWritten.add(mapName)) {
            indentPrintln("this." + mapName + ".clear ();");
        }
    }

    private void writeCreate(ServiceMethod method) {
        ServiceMethodParameter typeParam = this.findTypeParameter(method);
        ServiceMethodParameter infoParam = this.findInfoParameter(method);
        ServiceMethodParameter contextParam = this.findContextParameter(method);
        String objectName = calcObjectName(method);
        String infoName = objectName;
        if (!this.isRice()) {
            infoName = infoName + "Info";
        }
        String mapName = calcMapName(method);

        if (this.isRice()) {
            indentPrintln(infoName + " orig = this.get" + infoName + "ByNamespaceCodeAndName(" + infoParam.getName() + ".getNamespaceCode(), " + infoParam.getName() + ".getName());");
            indentPrintln("if (orig != null)");
            openBrace();
            indentPrintln("throw new RiceIllegalArgumentException (" + infoParam.getName() + ".getNamespaceCode() + \".\" + " + infoParam.getName() + ".getName());");
            closeBrace();
        }
        if (typeParam != null) {
            indentPrintln("if (!" + typeParam.getName() + ".equals (" + infoParam.getName() + ".getTypeKey())) {");
            indentPrintln("    throw new " + this.getInvalidParameterException() + " (\"The type parameter does not match the type on the info object\");");
            indentPrintln("}");
        }
        if (method.getParameters().size() > 3) {
            indentPrintln("// TODO: check the rest of the readonly fields that are specified on the create to make sure they match the info object");
        }
        if (this.isR1) {
            indentPrintln("// don't have deep copy in R1 contracts so just use the object");
            indentPrintln(infoName + " copy = " + infoParam.getName() + ";");
        } else if (this.isRice()) {
            indentPrintln(infoName + ".Builder copy = " + infoName + ".Builder.create (" + infoParam.getName() + ");");
        } else {
            indentPrintln(infoName + " copy = new " + infoName + "(" + infoParam.getName() + ");");
        }
        indentPrintln("if (copy.getId() == null) {");
        // indentPrintln("   copy.setId(" + mapName + ".size() + \"\");");
        importsAdd("org.kuali.student.common.util.UUIDHelper");
        indentPrintln("   copy.setId(UUIDHelper.genStringUUID());");
        indentPrintln("}");
        if (contextParam != null) {
            indentPrintln("copy.setMeta(newMeta(" + contextParam.getName() + "));");
        }
        if (isR1) {
            indentPrintln(mapName + ".put(copy.getId(), copy);");
            indentPrintln("// don't have deep copy in R1 contracts so just use the object");
            indentPrintln("return copy;");
        } else if (this.isRice()) {
            indentPrintln (infoParam.getName() + " = copy.build ();");
            indentPrintln(mapName + ".put(" + infoParam.getName() + ".getId(), " + infoParam.getName() + ");");
            indentPrintln("return " + infoParam.getName() + ";");
        } else {
            indentPrintln(mapName + ".put(copy.getId(), copy);");
            indentPrintln("return new " + infoName + "(copy);");
        }
    }

    private void writeAdd(ServiceMethod method) {
        ServiceMethodParameter typeParam = this.findTypeParameter(method);
        ServiceMethodParameter infoParam = this.findInfoParameter(method);
        ServiceMethodParameter contextParam = this.findContextParameter(method);
        String objectName = calcObjectName(method);
        String infoName = objectName;
        if (!this.isRice()) {
            infoName = infoName + "Info";
        }
        String mapName = calcMapName(method);

        if (typeParam != null) {
            indentPrintln("if (!" + typeParam.getName() + ".equals (" + infoParam.getName() + ".getTypeKey())) {");
            indentPrintln("    throw new " + this.getInvalidParameterException() + " (\"The type parameter does not match the type on the info object\");");
            indentPrintln("}");
        }
        if (method.getParameters().size() > 3) {
            indentPrintln("// TODO: check the rest of the readonly fields that are specified on the create to make sure they match the info object");
        }
        if (isR1) {
            indentPrintln("// don't have deep copy in R1 contracts so just use the object");
            indentPrintln(infoName + " copy = " + infoParam.getName() + ";");
        } else if (this.isRice()) {
            indentPrintln(infoName + ".Builder copy = " + infoName + ".Builder.create (" + infoParam.getName() + ");");
        } else {
            indentPrintln(infoName + " copy = new " + infoName + "(" + infoParam.getName() + ");");
        }
        indentPrintln("if (copy.getId() == null) {");
        // indentPrintln("   copy.setId(" + mapName + ".size() + \"\");");
        importsAdd("org.kuali.student.common.util.UUIDHelper");
        indentPrintln("   copy.setId(UUIDHelper.genStringUUID());");
        indentPrintln("}");
        if (contextParam != null) {
            indentPrintln("copy.setMeta(newMeta(" + contextParam.getName() + "));");
        }
        if (isR1) {
            indentPrintln(mapName + ".put(copy.getId(), copy);");
            indentPrintln("// don't have deep copy in R1 contracts so just use the object");
            indentPrintln("return copy;");
        } else if (this.isRice()) {
            indentPrintln (infoParam.getName() + " = copy.build ();");
            indentPrintln(mapName + ".put(" + infoParam.getName() + ".getId(), " + infoParam.getName() + ");");
            indentPrintln("return " + infoParam.getName() + ";");
        } else {
            indentPrintln(mapName + ".put(copy.getId(), copy);");
            indentPrintln("return new " + infoName + "(copy);");
        }
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

    private String calcMapName(ServiceMethod method) {
        String mapName = this.calcObjectName(method);
        mapName = this.initLower(mapName) + "Map";
        return mapName;
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
                if (method.getName().contains("IdsFor")) {
                    return method.getName().substring("get".length(),
                            method.getName().indexOf("IdsFor"));
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
        ServiceMethodParameter idParam = this.findIdParameter(method);
        ServiceMethodParameter infoParam = this.findInfoParameter(method);
        ServiceMethodParameter contextParam = this.findContextParameter(method);
        if (infoParam == null) {
            throw new NullPointerException(method.getName());
        }
        String objectName = calcObjectName(method);
        String infoName = objectName;
        if (!this.isRice()) {
            infoName = infoName + "Info";
        }
        String mapName = calcMapName(method);
        if (idParam != null) {
            if (!this.isRice()) {
                indentPrintln("if (!" + idParam.getName() + ".equals (" + infoParam.getName() + ".getId())) {");
                indentPrintln("    throw new " + this.getInvalidParameterException() + " (\"The id parameter does not match the id on the info object\");");
                indentPrintln("}");
            }
        }
        if (isR1) {
            indentPrintln("// don't have deep copy in R1 contracts so just use the object");
            indentPrintln(infoName + " copy = " + infoParam.getName() + ";");
        } else if (this.isRice()) {
            indentPrintln(infoName + ".Builder copy = " + infoName + ".Builder.create (" + infoParam.getName() + ");");
        } else {
            indentPrintln(infoName + " copy = new " + infoName + "(" + infoParam.getName() + ");");
        }
        if (contextParam != null) {
            indentPrintln(infoName + " old = this.get" + objectName + "(" + infoParam.getName() + ".getId(), " + contextParam.getName() + ");");
        } else {
            indentPrintln(infoName + " old = this.get" + objectName + "(" + infoParam.getName() + ".getId());");
        }
        if (isR1) {
            indentPrintln("if (!old.getMetaInfo().getVersionInd().equals(copy.getMetaInfo().getVersionInd())) {");
            indentPrintln("    throw new " + this.getVersionMismatchException() + "(old.getMetaInfo().getVersionInd());");
            indentPrintln("}");
            if (contextParam != null) {
                indentPrintln("copy.setMeta(updateMeta(copy.getMetaInfo()));");
            }
        } else if (this.isRice()) {
            indentPrintln("if (!old.getVersionNumber().equals(copy.getVersionNumber())) {");
            indentPrintln("    throw new " + this.getVersionMismatchException() + "(\"\" + old.getVersionNumber());");
            indentPrintln("}");
            indentPrintln("copy.setVersionNumber(copy.getVersionNumber() + 1);");
        } else {
            indentPrintln("if (!old.getMeta().getVersionInd().equals(copy.getMeta().getVersionInd())) {");
            indentPrintln("    throw new " + this.getVersionMismatchException() + "(old.getMeta().getVersionInd());");
            indentPrintln("}");
            if (contextParam != null) {
                indentPrintln("copy.setMeta(updateMeta(copy.getMeta(), contextInfo));");
            }
        }
        if (isR1) {
            indentPrintln("this." + mapName + " .put(" + infoParam.getName() + ".getId(), copy);");
            indentPrintln("// don't have deep copy in R1 contracts so just use the object");
            indentPrintln("return copy;");
        } else if (this.isRice()) {
            indentPrintln (infoParam.getName() + " = copy.build ();");
            indentPrintln("this." + mapName + " .put(" + infoParam.getName() + ".getId(), " + infoParam.getName() + ");");
            indentPrintln("return " + infoParam.getName() + ";");
        } else {
            indentPrintln("this." + mapName + " .put(" + infoParam.getName() + ".getId(), copy);");
            indentPrintln("return new " + infoName + "(copy);");
        }

    }

    private void writeDelete(ServiceMethod method) {
        ServiceMethodParameter idParam = this.findIdParameter(method);
        String mapName = calcMapName(method);
        indentPrintln("if (this." + mapName + ".remove(" + idParam.getName() + ") == null) {");
        indentPrintln("   throw new " + this.getOperationFailedException() + "(" + idParam.getName() + ");");
        indentPrintln("}");
        indentPrintln("return newStatus();");
    }

    private void writeRemove(ServiceMethod method) {
        ServiceMethodParameter idParam = this.findIdParameter(method);
        String mapName = calcMapName(method);
        indentPrintln("if (this." + mapName + ".remove(" + idParam.getName() + ") == null) {");
        indentPrintln("   throw new " + this.getOperationFailedException() + "(" + idParam.getName() + ");");
        indentPrintln("}");
        indentPrintln("return newStatus();");
    }

    private void writeGetById(ServiceMethod method) {
        ServiceMethodParameter idParam = this.findIdParameter(method);
        String mapName = calcMapName(method);
        indentPrintln("if (!this." + mapName + ".containsKey(" + idParam.getName() + ")) {");
        indentPrintln("   throw new " + this.getDoesNotExistException() + "(" + idParam.getName() + ");");
        indentPrintln("}");
        String objectName = calcObjectName(method);
        String infoName = objectName;
        if (!this.isRice()) {
            infoName = infoName + "Info";
        }
        if (isR1) {
            indentPrintln("// r1 objects do not have deep cody");
            indentPrintln("return this." + mapName + ".get (" + idParam.getName() + ");");
        } else if (this.isRice()) {
            indentPrintln("return this." + mapName + ".get (" + idParam.getName() + ");");
        } else {
            indentPrintln("return new " + infoName + "(this." + mapName + ".get (" + idParam.getName() + "));");
        }

    }

    private void writeGetByIds(ServiceMethod method) {
        String objectName = this.calcObjectName(method);
        String infoName = objectName;
        if (!this.isRice()) {
            infoName = infoName + "Info";
        }
        ServiceMethodParameter idListParam = this.findIdListParameter(method);
        ServiceMethodParameter contextParam = this.findContextParameter(method);
        this.importsAdd(ArrayList.class.getName());
        indentPrintln("List<" + infoName + "> list = new ArrayList<" + infoName + "> ();");

        indentPrintln("for (String id: " + idListParam.getName() + ") {");
        if (this.isRice()) {
            indentPrintln("    list.add (this.get" + objectName + "(id));");
        } else {
            indentPrintln("    list.add (this.get" + objectName + "(id, " + contextParam.getName() + "));");
        }
        indentPrintln("}");
        indentPrintln("return list;");
    }

    private void writeGetIdsByOther(ServiceMethod method) {
        String objectName = this.calcObjectName(method);
        String infoName = objectName;
        if (!this.isRice()) {
            infoName = infoName + "Info";
        }
        String mapName = calcMapName(method);
        this.importsAdd(ArrayList.class.getName());
        indentPrintln("List<String> list = new ArrayList<String> ();");
        indentPrintln("for (" + infoName + " info: " + mapName + ".values ()) {");
        for (ServiceMethodParameter parameter : method.getParameters()) {
            if (parameter.getType().equals("ContextInfo")) {
                continue;
            }
            incrementIndent();
            indentPrintln("if (" + parameter.getName() + ".equals(info.get" + initUpper(parameter.getName()) + "())) {");
        }
        indentPrintln("    list.add (info.getId ());");
        for (ServiceMethodParameter parameter : method.getParameters()) {
            if (parameter.getType().equals("ContextInfo")) {
                continue;
            }
            indentPrintln("}");
            decrementIndent();
        }
        indentPrintln("}");
        indentPrintln("return list;");
    }

    private void writeGetIdsByType(ServiceMethod method) {
        String objectName = this.calcObjectName(method);
        String infoName = objectName;
        if (!this.isRice()) {
            infoName = infoName + "Info";
        }
        String mapName = calcMapName(method);
        this.importsAdd(ArrayList.class.getName());
        indentPrintln("List<String> list = new ArrayList<String> ();");
        indentPrintln("for (" + infoName + " info: " + mapName + ".values ()) {");
        for (ServiceMethodParameter parameter : method.getParameters()) {
            if (parameter.getType().equals("ContextInfo")) {
                continue;
            }
            incrementIndent();
            indentPrintln("if (" + parameter.getName() + ".equals(info.getTypeKey())) {");
        }
        indentPrintln("    list.add (info.getId ());");
        for (ServiceMethodParameter parameter : method.getParameters()) {
            if (parameter.getType().equals("ContextInfo")) {
                continue;
            }
            indentPrintln("}");
            decrementIndent();
        }
        indentPrintln("}");
        indentPrintln("return list;");
    }

    private void writeRiceGetByNamespaceAndName(ServiceMethod method) {
        String objectName = this.calcObjectName(method);
        String infoName = objectName;
        if (!this.isRice()) {
            infoName = infoName + "Info";
        }
        String mapName = calcMapName(method);
        indentPrintln("for (" + infoName + " info: " + mapName + ".values ()) {");
        for (ServiceMethodParameter parameter : method.getParameters()) {
            incrementIndent();
            indentPrintln("if (" + parameter.getName() + ".equals(info.get" + initUpper(parameter.getName()) + "())) {");
        }
        indentPrintln("    return " + infoName + ".Builder.create (info).build ();");
        for (ServiceMethodParameter parameter : method.getParameters()) {
            indentPrintln("}");
            decrementIndent();
        }
        indentPrintln("}");
        indentPrintln("throw new RiceIllegalArgumentException ();");
    }

    private void writeGetInfosByOther(ServiceMethod method) {
        String objectName = this.calcObjectName(method);
        String infoName = objectName;
        if (!this.isRice()) {
            infoName = infoName + "Info";
        }
        String mapName = calcMapName(method);
        this.importsAdd(ArrayList.class.getName());
        indentPrintln("List<" + infoName + "> list = new ArrayList<" + infoName + "> ();");
        indentPrintln("for (" + infoName + " info: " + mapName + ".values ()) {");
        for (ServiceMethodParameter parameter : method.getParameters()) {
            if (parameter.getType().equals("ContextInfo")) {
                continue;
            }
            incrementIndent();
            indentPrintln("if (" + parameter.getName() + ".equals(info.get" + initUpper(parameter.getName()) + "())) {");
        }
        indentPrintln("    list.add (new " + infoName + "(info));");
        for (ServiceMethodParameter parameter : method.getParameters()) {
            if (parameter.getType().equals("ContextInfo")) {
                continue;
            }
            indentPrintln("}");
            decrementIndent();
        }
        indentPrintln("}");
        indentPrintln("return list;");
    }

    private void writeGetType(ServiceMethod method) {
        ServiceMethodParameter idParam = this.findIdParameter(method);
        String mapName = calcMapName(method);
        indentPrintln("if (!this." + mapName + ".containsKey(" + idParam.getName() + ")) {");
        indentPrintln("   throw new " + this.getOperationFailedException() + "(" + idParam.getName() + ");");
        indentPrintln("}");
        indentPrintln("return this." + mapName + ".get (" + idParam.getName() + ");");
    }

    private void writeGetTypes(ServiceMethod method) {
        String mapName = calcMapName(method);
        String objectName = this.calcObjectName(method);
        String infoName = objectName;
        if (!this.isRice()) {
            infoName = infoName + "Info";
        }
        this.importsAdd(ArrayList.class.getName());
        indentPrintln("return new ArrayList<" + infoName + ">(" + mapName + ".values ());");
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
