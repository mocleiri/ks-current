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
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.apache.commons.lang.StringUtils;
import org.kuali.student.contract.model.MessageStructure;

/**
 *
 * @author nwright
 */
public class JpaEntityWriter extends JavaClassWriter {

    private static Logger log = LoggerFactory.getLogger(JpaEntityWriter.class);

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
    public JpaEntityWriter(ServiceContractModel model,
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

    public JpaEntityWriter(ServiceContractModel model,
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
        String name = calcInfcName(xmlType) + "Entity";
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

    /**
     * Analyses the method and returns a MethodType enum that describes what
     * type of method this is.
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
            if (method.getParameters().size() >= 1 && method.getParameters().size() <= 2) {
                if (!method.getReturnValue().getType().endsWith("List")) {
//                    if (method.getParameters().get(0).getName().endsWith("Id")) {
                    return MethodType.GET_BY_ID;
//                    }
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
        tableName = applyAbbreviations(tableName.toUpperCase());
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
        String name = ms.getColumnName();
        if (name != null) {
            return name;
        }
        if (ms.getShortName().equalsIgnoreCase("TypeKey")) {
            String prefix = calcTableName(xmlType);
            if (prefix.startsWith("KSEN_")) {
                prefix = prefix.substring("KSEN_".length());
            }
            // already applied
//            prefix = applyAbbreviations (prefix.toUpperCase());
            return prefix + "_TYPE";
        }
        if (ms.getShortName().equalsIgnoreCase("StateKey")) {
            String prefix = calcTableName(xmlType);
            if (prefix.startsWith("KSEN_")) {
                prefix = prefix.substring("KSEN_".length());
            }
            // already applied
//            prefix = applyAbbreviations (prefix.toUpperCase());
            return prefix + "_STATE";
        }
        name = splitCamelCase(ms.getShortName());
        name = applyAbbreviations(name.toUpperCase());
        return name;
    }

    private static final Map<String, String> abbreviations = new LinkedHashMap<String, String>();

    // known abbreviations see https://wiki.kuali.org/display/STUDENT/Database+Abbreviations+for+R2
    static {
        abbreviations.put("ABBREVIATION", "ABBREV");
        abbreviations.put("ACCOUNT", "ACCT");
        abbreviations.put("ACCOUNTING", "ACCT");
        abbreviations.put("ACCREDITING", "ACCRED");
        abbreviations.put("ACCREDIT", "ACCRED");
        abbreviations.put("ACCREDITATION", "ACCRED");
        abbreviations.put("ACTION", "ACTN");
        abbreviations.put("AFFILIATION", "AFFIL");
        abbreviations.put("AMOUNT", "AMT");
        abbreviations.put("APPLIED", "APPLD");
        abbreviations.put("APPLICATION", "APP");
        abbreviations.put("ALLOWABLE", "ALOW");
        abbreviations.put("ALTERNATE", "ALT");
        abbreviations.put("APPOINTMENT", "APPT");
        abbreviations.put("ASSOCIATED", "ASSO");
        abbreviations.put("ATTRIBUTE", "ATTR");
        abbreviations.put("CAMPUS", "CAMP");
        abbreviations.put("CANONICAL LEARNING UNIT", "CLU");
        abbreviations.put("CATEGORY", "CAT");
        abbreviations.put("CODE", "CD");
        abbreviations.put("COMPLETE", "COMP");
        abbreviations.put("COMPONENT", "CMP");
        abbreviations.put("CONTEXT", "CTX");
        abbreviations.put("CONTINUE", "CONT");
        abbreviations.put("COUNT", "CNT");
        abbreviations.put("CREDIT", "CR");
        abbreviations.put("CRITERIA", "CRIT");
        abbreviations.put("DATE", "DT");
        abbreviations.put("DEADLINE", "DEDLN");
        abbreviations.put("DEFAULT", "DEF");
        abbreviations.put("DEFINABLE", "DEFNBL");
        abbreviations.put("DEFINITION", "DEFN");
        abbreviations.put("DESCRIPTION", "DESCR");
        abbreviations.put("DISABLED", "DISBLD");
        abbreviations.put("DATE OVERRIDE", "DO");
        abbreviations.put("DOCUMENT", "DOC");
        abbreviations.put("DURATION", "DUR");
        abbreviations.put("DIVISION", "DIV");
        abbreviations.put("EFFECTIVE", "EFF");
        abbreviations.put("ENROLL", "ENRL");
        abbreviations.put("ENROLLED", "ENRL");
        abbreviations.put("ENROLLMENT", "ENRL");
        abbreviations.put("ENROLLABLE", "ENRL");
        abbreviations.put("ESTIMATE", "EST");
        abbreviations.put("EXEMPTION", "EXEMPT");
        abbreviations.put("EXPENDITURE", "EXPEND");
        abbreviations.put("EXPIRATION", "EXP");
        abbreviations.put("EXPIRED", "EXP");
        abbreviations.put("FLEXIBLE", "FLEX");
        abbreviations.put("FREQUENCY", "FREQ");
        abbreviations.put("GROUP", "GRP");
        abbreviations.put("HAZARD", "HAZR");
        abbreviations.put("HAZARDOUS", "HAZR");
        abbreviations.put("HEADER", "HDR");
        abbreviations.put("HIERARCHY", "HIRCHY");
        abbreviations.put("IDENTIFIER", "ID");
        abbreviations.put("INACTIVE", "INACV");
        abbreviations.put("INDEPENDENT", "INDPT");
        abbreviations.put("INDICATOR", "IND");
        abbreviations.put("INSTRUCTOR", "INSTR");
        abbreviations.put("INSTRUCTION", "INSTRN");
        abbreviations.put("INSTRUCTIONAL", "INSTRN");
        abbreviations.put("INTENSITY", "INTSTY");
        abbreviations.put("INTERVAL", "INTVL");
        abbreviations.put("LEARNING OBJECTIVE", "LO");
        abbreviations.put("LEARNING RESULT", "LR");
        abbreviations.put("LEARNING UNIT", "LU");
        abbreviations.put("LOCATION", "LOC");
        abbreviations.put("LONG", "LNG");
        abbreviations.put("LEVEL", "LVL");
        abbreviations.put("LIFECYCLE", "LIFCYC");
        abbreviations.put("MARKETING", "MKTG");
        abbreviations.put("MAXIMUM", "MAX");
        abbreviations.put("MEMBER", "MBR");
        abbreviations.put("MESSAGE", "MSG");
        abbreviations.put("MILESTONE", "MSTONE");
        abbreviations.put("MILLISECONDS", "MS");
        abbreviations.put("MINIMUM", "MIN");
        abbreviations.put("NAMESPACE", "NMSPC");
        abbreviations.put("NEXT", "NEXT");
        abbreviations.put("NUMBER", "NUM");
        abbreviations.put("OFFICIAL", "OFFIC");
        abbreviations.put("ORG ORG RELATION TYPE", "OORT");
        abbreviations.put("OPTION", "OPT");
        abbreviations.put("ORGANIZATION", "ORG");
        abbreviations.put("PARAMETER", "PARM");
        abbreviations.put("PARAMETER VALUE", "PV");
        abbreviations.put("PERCENT", "PERCT");
        abbreviations.put("PERIOD", "PRD");
        abbreviations.put("PERSON", "PERS");
        abbreviations.put("POSITION", "POS");
        abbreviations.put("PRIMARY", "PRI");
        abbreviations.put("PROCESS", "PROC");
        abbreviations.put("PUBLISH", "PUBL");
        abbreviations.put("PUBLISHING", "PUBL");
        abbreviations.put("QUANTITY", "QTY");
        abbreviations.put("RECOGNITIION", "RECOG");
        abbreviations.put("RECORD", "REC");
        abbreviations.put("REFERENCE", "REF");
        abbreviations.put("REGISTRATION", "REG");
        abbreviations.put("RELATION", "RELN");
        abbreviations.put("RELATIONSHIP", "RELN");
        abbreviations.put("ROLLOVER RESULT", "ROR");
        abbreviations.put("REQUEST", "RQST");
        abbreviations.put("REQUIRED", "REQ");
        abbreviations.put("REPONSIBLE", "RESP");
        abbreviations.put("RESOURCE", "RSRC");
        abbreviations.put("RESTRICTION", "RESTR");
        abbreviations.put("RESULT VALUE GROUP", "RVG");
        abbreviations.put("REVIEW", "REVIEW");
        abbreviations.put("RICH TEXT", "RT");
        abbreviations.put("SCHEDULE", "SCHED");
        abbreviations.put("SHORT", "SHRT");
        abbreviations.put("SLOT RULE", "SR");
        abbreviations.put("STANDARD", "STD");
        abbreviations.put("STATE", "ST");
        abbreviations.put("STATEMENT", "STMNT");
        abbreviations.put("STUDENT", "STU");
        abbreviations.put("STUDY", "STDY");
        abbreviations.put("SUBJECT", "SUBJ");
        abbreviations.put("SUFFIX", "SUFX");
        abbreviations.put("TEMPLATE", "TMPLT");
        abbreviations.put("TIME", "TM");
        abbreviations.put("TITLE", "TTL");
        abbreviations.put("TOTAL", "TOT");
        abbreviations.put("TRANSACTION", "TRANS");
        abbreviations.put("TYPE", "TYP");
        abbreviations.put("VARIATION", "VARTN");
        abbreviations.put("VERSION NUMBER", "VER_NBR");
        abbreviations.put("", "");
    }

    private String applyAbbreviations(String name) {
        // THIS DOESN'T HANDLE THE MULTI-WORD ABBREVIATIONS!
        // good enough for now
        String[] parts = name.split("_");
        for (int i = 0; i < parts.length; i++) {
            String abbrev = abbreviations.get(parts[i]);
            if (abbrev != null) {
                System.out.println("Replacing abbreviation " + parts[i] + " with " + abbrev);
                parts[i] = abbrev;
            }
        }
        name = StringUtils.join(parts, '_');
        return name;
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
        indentPrintln("@Entity");
        importsAdd(Entity.class.getName());
        String tableName = calcTableName(xmlType);
        if (xmlType.getTableName() == null) {
            indentPrintln("//TODO: JPAIMPL double check table name because it was calculated based on heuristics and may not match the actual table name");
        }
        indentPrintln("@Table(name = \"" + tableName + "\")");
        importsAdd(Table.class.getName());
//@NamedQueries({
//    @NamedQuery(name = "HoldIssueEntity.getIdsByType",
//    query = "select id from HoldIssueEntity where holdIssueType = :type"),
//    @NamedQuery(name = "HoldIssueEntity.getByOrganization",
//    query = "select ISSUE from HoldIssueEntity ISSUE where ISSUE.organizationId = :organizationId")
//})
        String className = calcClassName(servKey, xmlType);
        String attributeEntity = JpaAttributeEntityWriter.calcClassName(servKey, xmlType);
        indentPrintln("@NamedQueries({");
        String comma = "";
        for (ServiceMethod method : methods) {
            if (!isMethodForThisType(xmlType, method)) {
                continue;
            }
            MethodType methodType = calcMethodType(method);
            switch (methodType) {
                case GET_IDS_BY_TYPE:
                    indentPrint(comma);
                    comma = ",";
                    indentPrintln("@NamedQuery(name = \"" + className + ".getIdsByType\",");
                    indentPrintln("    query = \"select id from " + className + " where typeKey = :type\")");
                    break;
                case GET_IDS_BY_OTHER:
                    indentPrintln("//TODO: JPAIMPL double check this JPQL queries to make sure they match the intention of the service method");
                    indentPrint(comma);
                    comma = ",";
                    String namedQuery = calcNamedQuery(method);
                    indentPrintln("@NamedQuery(name = \"" + className + "." + namedQuery + "\",");

                    indentPrint("    query = \"select id from " + className);
                    String and = " where ";
                    for (ServiceMethodParameter param : method.getParameters()) {
                        if (param.getType().equals("ContextInfo")) {
                            continue;
                        }
                        print(and);
                        and = " and ";
                        String paramName = initLower(param.getName());
                        print(paramName + " = :" + paramName);
                    }
                    println("\")");
                    break;
                case GET_INFOS_BY_OTHER:
                    indentPrintln("//TODO: JPAIMPL double check this JPQL queries to make sure they match the intention of the service method");
                    indentPrint(comma);
                    comma = ",";
                    namedQuery = calcNamedQuery(method);
                    indentPrintln("@NamedQuery(name = \"" + className + "." + namedQuery + "\",");

                    indentPrint("    query = \"select " + className + " from " + className);
                    and = " where ";
                    for (ServiceMethodParameter param : method.getParameters()) {
                        if (param.getType().equals("ContextInfo")) {
                            continue;
                        }
                        print(and);
                        and = " and ";
                        String paramName = initLower(param.getName());
                        print(paramName + " = :" + paramName);
                    }
                    println("\")");
                    break;
            }
        }
        indentPrintln("})");
        importsAdd(NamedQueries.class.getName());
        importsAdd(NamedQuery.class.getName());
        indentPrint("public class " + calcClassName(servKey, xmlType));
        println(" extends MetaEntity");
        importsAdd("org.kuali.student.r2.common.entity.MetaEntity");

        boolean hasAttributes = hasAttributes();
        String attributeEntityName = JpaAttributeEntityWriter.calcClassName(servKey, xmlType);
        if (hasAttributes) {
            indentPrintln("    implements AttributeOwner<" + attributeEntityName + ">");
            importsAdd("org.kuali.student.r2.common.entity.AttributeOwner");
        }
        openBrace();

        for (MessageStructure ms : finder.findMessageStructures(xmlType.getName())) {
            if (ms.getShortName().equalsIgnoreCase("id")) {
                continue;
            }
            if (ms.getShortName().equalsIgnoreCase("meta")) {
                continue;
            }
            if (ms.getShortName().equalsIgnoreCase("key")) {
                continue;
            }
            if (ms.getShortName().equalsIgnoreCase("descr")) {
                importsAdd(Column.class.getName());
                importsAdd("org.kuali.student.r1.common.entity.KSEntityConstants");
                indentPrintln("@Column(name = \"DESCR_PLAIN\", length = KSEntityConstants.EXTRA_LONG_TEXT_LENGTH, nullable = false)");
                indentPrintln("private String descrPlain;");
                indentPrintln("@Column(name = \"DESCR_FORMATTED\", length = KSEntityConstants.EXTRA_LONG_TEXT_LENGTH)");
                indentPrintln("private String descrFormatted;");
                continue;
            }
            if (ms.getShortName().equalsIgnoreCase("attributes")) {
                importsAdd(OneToMany.class.getName());
                importsAdd(CascadeType.class.getName());
                importsAdd(FetchType.class.getName());
                importsAdd(Set.class.getName());
                importsAdd(HashSet.class.getName());
                indentPrintln("@OneToMany(cascade = CascadeType.ALL, mappedBy = \"owner\", fetch = FetchType.EAGER, orphanRemoval = true)");
                indentPrintln("private final Set<" + attributeEntity + "> attributes = new HashSet<" + attributeEntity + ">();");
                JpaAttributeEntityWriter attrEntityWriter
                        = new JpaAttributeEntityWriter(model, directory, rootPackage, servKey, methods, xmlType, isR1, className);
                attrEntityWriter.write();
                continue;
            }
            //    @Column(name = "NAME")
            //    private String name;
            importsAdd(Column.class.getName());
            String columnName = calcColumnName(ms);
            if (ms.getType().equalsIgnoreCase("Date")) {
                importsAdd(Temporal.class.getName());
                importsAdd(TemporalType.class.getName());
                indentPrintln("@Temporal(TemporalType.TIMESTAMP)");
            }
            if (shouldAddJpaImplTodo(ms)) {
                indentPrintln("//TODO: JPAIMPL double check column name it was calculated based on heuristics and may not match actual table column");
            }
            indentPrintln("@Column(name= \"" + columnName + "\", nullable=" + this.isNullable(ms) + ")");
            String javaClass = this.calcType(ms.getType(), this.stripList(ms.getType()));
            indentPrintln("private " + javaClass + " " + initLower(ms.getShortName()) + ";");
        }

        println("");
//    public HoldIssueEntity() {
//    }
//
//    public HoldIssueEntity(HoldIssue dto) {
//        super(dto);
//        this.setId(dto.getId());
//        this.setHoldIssueType(dto.getTypeKey());
//        this.fromDto(dto);
//    }
        String infcName = this.calcInfcName();
        println("");
        indentPrintln("public " + className + "() {");
        indentPrintln("}");
        println("");
        indentPrintln("public " + className + "(" + infcName + " dto) {");
        incrementIndent();
        indentPrintln("super(dto);");
        String keyOrId = this.keyOrId();
        indentPrintln("this.setId(dto.get" + keyOrId + "());");
        indentPrintln("this.setTypeKey(dto.getTypeKey());");
        indentPrintln("// TODO: JPAIMPL insert other fields that should only be specified on create");
        indentPrintln("this.fromDto(dto);");
        decrementIndent();
        indentPrintln("}");

//        public void fromDto(HoldIssue dto) {
//        super.fromDTO(dto);
//        
//        setName(dto.getName());
//        setHoldIssueState(dto.getStateKey());
//        setOrganizationId(dto.getOrganizationId());
//        if (dto.getDescr() != null) {
//            this.setDescrFormatted(dto.getDescr().getFormatted());
//            this.setDescrPlain(dto.getDescr().getPlain());
//        } else {
//            this.setDescrFormatted(null);
//            this.setDescrPlain(null);
//        }
//
//        // dynamic attributes
//        this.getAttributes().clear();
//        for (Attribute att : dto.getAttributes()) {
//            HoldIssueAttributeEntity attEntity = new HoldIssueAttributeEntity(att, this);
//            this.getAttributes().add(attEntity);
//        }
//    }
        println("");
        indentPrintln("public void fromDto(" + infcName + " dto) {");
        incrementIndent();
        indentPrintln("super.fromDTO(dto);");
        indentPrintln("// TODO: JPAIMPL move all fields that are only supposed to be only specified on create up to the constructor section (like type is)");
        for (MessageStructure ms : finder.findMessageStructures(xmlType.getName())) {
            if (ms.getShortName().equalsIgnoreCase("id")) {
                continue;
            }
            if (ms.getShortName().equalsIgnoreCase("meta")) {
                continue;
            }
            if (ms.getShortName().equalsIgnoreCase("key")) {
                continue;
            }
            if (ms.getShortName().equalsIgnoreCase("typeKey")) {
                continue;
            }
            if (ms.getShortName().equalsIgnoreCase("typeKey")) {
                continue;
            }
            if (ms.getShortName().equalsIgnoreCase("descr")) {
                println("");
                indentPrintln("if (dto.getDescr() != null) {");
                indentPrintln("    this.setDescrFormatted(dto.getDescr().getFormatted());");
                indentPrintln("    this.setDescrPlain(dto.getDescr().getPlain());");
                indentPrintln("} else {");
                indentPrintln("     this.setDescrFormatted(null);");
                indentPrintln("     this.setDescrPlain(null);");
                indentPrintln("}");
                println("");
                continue;
            }
            if (ms.getShortName().equalsIgnoreCase("attributes")) {
                importsAdd("org.kuali.student.r2.common.infc.Attribute");
                println("");
                indentPrintln("// dynamic attributes");
                indentPrintln("this.getAttributes().clear();");
                indentPrintln("for (Attribute att : dto.getAttributes()) {");
                indentPrintln("    " + attributeEntityName + " attEntity = new " + attributeEntityName + "(att, this);");
                indentPrintln("    this.getAttributes().add(attEntity);");
                indentPrintln("}");
                continue;
            }
            // regular fields
            String upperName = initUpper(ms.getShortName());
            indentPrintln("set" + upperName + "(dto.get" + upperName + "());");
        }
        decrementIndent();
        indentPrintln("}");

//       public AppliedHoldInfo toDto() {
//        AppliedHoldInfo info = new AppliedHoldInfo();
//        info.setId(getId());
//        info.setName(name);
//        info.setEffectiveDate(effectiveDate);
//        info.setReleasedDate(releasedDate);
//        info.setPersonId(personId);
//        info.setTypeKey(holdType);
//        info.setStateKey(holdState);
//        if (holdIssue != null) {
//            info.setHoldIssueId(holdIssue.getId());
//        }
//        info.setDescr(new RichTextHelper().toRichTextInfo(getDescrPlain(), getDescrFormatted()));
//
//        info.setMeta(super.toDTO());
//        for (AppliedHoldAttributeEntity att : getAttributes()) {
//            AttributeInfo attInfo = att.toDto();
//            info.getAttributes().add(attInfo);
//        }
//        return info;
//    }
        String infoName = this.calcInfoName();
        println("");
        indentPrintln("public " + infoName + " toDto() {");
        incrementIndent();
        indentPrintln(infoName + " info = new " + infoName + "();");
        for (MessageStructure ms : finder.findMessageStructures(xmlType.getName())) {
            if (ms.getShortName().equalsIgnoreCase("id")) {
                indentPrintln("info.setId(getId());");
                continue;
            }
            if (ms.getShortName().equalsIgnoreCase("meta")) {
                indentPrintln("info.setMeta(super.toDTO());");
                continue;
            }
            if (ms.getShortName().equalsIgnoreCase("key")) {
                indentPrintln("info.setKey(getId());");
                continue;
            }
            if (ms.getShortName().equalsIgnoreCase("descr")) {
                importsAdd("org.kuali.student.r2.common.util.RichTextHelper");
                indentPrintln("info.setDescr(new RichTextHelper().toRichTextInfo(getDescrPlain(), getDescrFormatted()));");
                continue;
            }
            if (ms.getShortName().equalsIgnoreCase("attributes")) {
                importsAdd("org.kuali.student.r2.common.dto.AttributeInfo");
                println("");
                indentPrintln("// dynamic attributes");
                indentPrintln("for (" + attributeEntityName + " att : getAttributes()) {");
                indentPrintln("    AttributeInfo attInfo = att.toDto();");
                indentPrintln("    info.getAttributes().add(attInfo);");
                indentPrintln("}");
                continue;
            }
            // regular fields
            String upperName = initUpper(ms.getShortName());
            indentPrintln("info.set" + upperName + "(this.get" + upperName + "());");
        }
        indentPrintln("return info;");
        decrementIndent();
        indentPrintln("}");

        // write getters and setters
        for (MessageStructure ms : finder.findMessageStructures(xmlType.getName())) {
            if (ms.getShortName().equalsIgnoreCase("id")) {
                continue;
            }
            if (ms.getShortName().equalsIgnoreCase("meta")) {
                continue;
            }
            if (ms.getShortName().equalsIgnoreCase("key")) {
                continue;
            }
            if (ms.getShortName().equalsIgnoreCase("descr")) {
                println("");
                indentPrintln("public String getDescrPlain() {");
                indentPrintln("    return this.descrPlain;");
                indentPrintln("}");
                println("");
                indentPrintln("public void setDescrPlain(String descrPlain) {");
                indentPrintln("    this.descrPlain = descrPlain;");
                indentPrintln("}");
                println("");
                indentPrintln("public String getDescrFormatted() {");
                indentPrintln("    return this.descrFormatted;");
                indentPrintln("}");
                println("");
                indentPrintln("public void setDescrFormatted(String descrFormatted) {");
                indentPrintln("    this.descrFormatted = descrFormatted;");
                indentPrintln("}");
                continue;
            }
            if (ms.getShortName().equalsIgnoreCase("attributes")) {
                println("");
                indentPrintln("@Override");
                indentPrintln("public void setAttributes(Set<" + attributeEntityName + "> attributes) {");
                incrementIndent();
                indentPrintln("this.attributes.clear();");
                indentPrintln("if (attributes != null) {");
                indentPrintln("    this.attributes.addAll(attributes);");
                indentPrintln("}");
                decrementIndent();
                indentPrintln("}");
                println("");
                indentPrintln("@Override");
                indentPrintln("public Set<" + attributeEntityName + "> getAttributes() {");
                indentPrintln("    return attributes;");
                indentPrintln("}");
                println("");
                continue;
            }
            importsAdd(Column.class.getName());
            String variableName = initLower(ms.getShortName());
            String javaClass = this.calcType(ms.getType(), this.stripList(ms.getType()));
            String upperName = initUpper(ms.getShortName());
            println("");
            indentPrintln("public " + javaClass + " get" + upperName + "() {");
            indentPrintln("    return this." + variableName + ";");
            indentPrintln("}");
            println("");
            indentPrintln("public void set" + upperName + "(" + javaClass + " " + variableName + ") {");
            indentPrintln("    this." + variableName + " = " + variableName + ";");
            indentPrintln("}");
        }

        println("");
        closeBrace();

        this.writeJavaClassAndImportsOutToFile();
        this.getOut().close();
    }

    private boolean shouldAddJpaImplTodo(MessageStructure ms) {
        if (ms.getColumnName() != null) {
            return false;
        }
        if (ms.getShortName().equalsIgnoreCase("name")) {
            return false;
        }
        if (ms.getShortName().equalsIgnoreCase("descr")) {
            return false;
        }
        if (ms.getShortName().equalsIgnoreCase("effectiveDate")) {
            return false;
        }
        if (ms.getShortName().equalsIgnoreCase("expirationDate")) {
            return false;
        }
        return true;
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
        return null;
    }

    private String calcNamedQuery(ServiceMethod method) {
        String objectName = calcObjectName(method);
        if (objectName == null) {
            return null;
        }
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
