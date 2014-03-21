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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.kuali.student.contract.model.MessageStructure;
import org.kuali.student.contract.model.Service;
import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.ServiceMethod;
import org.kuali.student.contract.model.ServiceMethodParameter;
import org.kuali.student.contract.model.XmlType;
import org.kuali.student.contract.model.util.HtmlContractMessageStructureWriter;
import org.kuali.student.contract.model.util.ModelFinder;
import org.kuali.student.contract.model.validation.ServiceContractModelValidator;
import org.kuali.student.validation.decorator.mojo.ValidationDecoratorWriterForOneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nwright
 */
@Ignore
public class R1R2ServiceContractComparisonTest {
    
    private static Logger log = LoggerFactory.getLogger(R1R2ServiceContractComparisonTest.class);
    

    public R1R2ServiceContractComparisonTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {

        log.info("This section was created by programmatically comparing the message structures.");
        log.info("Run on: " + new Date());
        log.info("See [R1R2ServiceContractComparisonTest.java|https://test.kuali.org/svn/student/tools/maven-kscontractdoc-plugin/trunk/src/test/java/org/kuali/student/contract/model/impl/R1R2ServiceContractComparisonTest.java]");
        log.info("");
        log.info("*TABLE OF CONTENTS*");
        log.info("{toc}");
        log.info("");
        log.info("h1. Loading models of the contracts from the source code");
        log.info("h2. Log from loading model for R1");
        getModel1();
        log.info("h2. Log from loading model for R2");
        getModel2();
        getFinder1();
        getFinder2();
        loadKnownObjectRenames();
        loadKnownUnconvertedObjects();
        loadKnownFieldRenames();
        loadKnownFieldIssues();
        loadKnownMethodRenames();
        loadKnownMethodIssues ();
    }

    @After
    public void tearDown() {
    }
    private static final String RESOURCES_DIRECTORY = "src/test/resources";
    private static final String TEST_SOURCE_DIRECTORY =
            "src/test/java/org/kuali/student/contract/model/test/source";
    private static final String ENROLL_PROJECT_SRC_MAIN = "C:/svn/ks-1.3-services/ks-enroll/ks-enroll-api/src/main";
    private static final String ENROLL_PROJECT_JAVA_DIRECTORY = ENROLL_PROJECT_SRC_MAIN + "/java";
    private static final String RICE_CORE_API_DIRECTORY = "C:/svn/rice/trunk/core/api/src/main/java";
    private static final String RICE_KIM_API_DIRECTORY = "C:/svn/rice/trunk/kim/kim-api/src/main/java";
    private static final String RICE_LOCATION_API_DIRECTORY = "C:/svn/rice/trunk/location/api/src/main/java";
    private static final String RICE_KEW_API_DIRECTORY = "C:/svn/rice/trunk/kew/api/src/main/java";
    private static final String RICE_KEN_API_DIRECTORY = "C:/svn/rice/trunk/ken/api/src/main/java";
    private static final String RICE_KSB_API_DIRECTORY = "C:/svn/rice/trunk/ksb/api/src/main/java";
    private static final String RICE_KRMS_API_DIRECTORY = "C:/svn/rice/trunk/krms/api/src/main/java";
    private static final String R1_PROJECT_DIRECTORY = "C:/svn/student/";
    private static final String CORE_API_DIRECTORY = R1_PROJECT_DIRECTORY + "ks-core/ks-core-api/src/main/java";
    private static final String COMMON_API_DIRECTORY = R1_PROJECT_DIRECTORY + "ks-common/ks-common-api/src/main/java";
    private static final String LUM_API_DIRECTORY = R1_PROJECT_DIRECTORY + "ks-lum/ks-lum-api/src/main/java";
    private static ServiceContractModel model1 = null;
    private static ServiceContractModel model2 = null;

    /**
     * Test of getServiceMethods method, of class ServiceContractModelQDoxLoader.
     */
    @Test
    public void testCompareModels() {
        log.info("");
        log.info("h1. Message Structure Comparison");
        compareTypes();
        log.info("");
        log.info("h1. Service Method Comparison");
        compareMethods();
    }

    private ServiceContractModel getModel1() {
        if (model1 != null) {
            return model1;
        }
        List<String> srcDirs = new ArrayList();
        log.info("User directory=" + System.getProperty("user.dir"));
        log.info("Current directory=" + new File(".").getAbsolutePath());
        srcDirs.add(COMMON_API_DIRECTORY);
        srcDirs.add(CORE_API_DIRECTORY);
        srcDirs.add(LUM_API_DIRECTORY);
        log.info ("Reading as input:");
        for (String directory : srcDirs) {
            log.info ("* " + directory);
        }
        log.info ("");
        boolean validateKualiStudent = false;
        ServiceContractModel instance = new ServiceContractModelQDoxLoader(srcDirs, validateKualiStudent);

        instance = new ServiceContractModelCache(instance);
        validate(instance);
        model1 = instance;
        return instance;
    }

    private ServiceContractModel getModel2() {
        if (model2 != null) {
            return model2;
        }
        List<String> srcDirs = new ArrayList();
        log.info("User directory=" + System.getProperty("user.dir"));
        log.info("Current directory=" + new File(".").getAbsolutePath());
        srcDirs.add(ENROLL_PROJECT_JAVA_DIRECTORY);
        log.info ("Reading as input:");
        for (String directory : srcDirs) {
            log.info ("* " + directory);
        }
        log.info ("");     
        boolean validateKualiStudent = true;
        ServiceContractModel instance = new ServiceContractModelQDoxLoader(srcDirs, validateKualiStudent);

        instance = new ServiceContractModelCache(instance);
        validate(instance);
        model2 = instance;
        return instance;
    }

    private String dump(ServiceMethod method) {
        StringBuilder bldr = new StringBuilder();
        bldr.append(method.getName());
        String comma = "";
        bldr.append("(");
        for (ServiceMethodParameter param : method.getParameters()) {
            bldr.append(comma);
            comma = ", ";
            bldr.append(param.getType());
            bldr.append(" ");
            bldr.append(param.getName());
        }
        bldr.append(")");
        return bldr.toString();
    }

    private void validate(ServiceContractModel model) {
        Collection<String> errors =
                new ServiceContractModelValidator(model).validate();
        if (errors.size() > 0) {
            StringBuilder buf = new StringBuilder();
            buf.append(errors.size()).append(" errors found while validating the data.");
            int cnt = 0;
            for (String msg : errors) {
                cnt++;
                buf.append("\n");
                buf.append("*error*").append(cnt).append(":").append(msg);
            }

            fail(buf.toString());
        }
    }
    private ModelFinder finder1 = null;

    private ModelFinder getFinder1() {
        if (finder1 == null) {
            finder1 = new ModelFinder(getModel1());
        }
        return finder1;
    }
    private ModelFinder finder2 = null;

    private ModelFinder getFinder2() {
        if (finder2 == null) {
            finder2 = new ModelFinder(getModel2());
        }
        return finder2;
    }

    private void compareTypes() {
        for (Service service : model1.getServices()) {
            log.info("");
            log.info("h2. " + service.getName() + " Structures");
            for (XmlType type : finder1.findAllComplexTypesInService(service.getKey())) {
                findCompareType(type);
            }
        }
    }

    private String calcService(XmlType xmlType) {
        StringBuilder bldr = new StringBuilder();
        String comma = "";
        for (String serviceKey : HtmlContractMessageStructureWriter.calcUsageByService(model1, xmlType)) {
            bldr.append(comma);
            comma = ", ";
            bldr.append(serviceKey);
        }
        return bldr.toString();
    }

    private String calcFieldNames(XmlType xmlType) {
        StringBuilder bldr = new StringBuilder();
        String comma = "";
        for (MessageStructure ms : finder2.findMessageStructures(xmlType.getName())) {
            bldr.append(comma);
            comma = ", ";
            bldr.append(ms.getShortName());
        }
        return bldr.toString();
    }
    private Map<String, String> knownUnconvertedObjects = null;

    private void loadKnownUnconvertedObjects() {
        Map<String, String> missings = new HashMap<String, String>();
        missings.put("ObjectStructureDefinition", "Old R1 dictionary not converted");
        missings.put("FieldDefinition", "Old R1 dictionary not converted");
        missings.put("ValidCharsConstraint", "Old R1 dictionary not converted");
        missings.put("RequiredConstraint", "Old R1 dictionary not converted");
        missings.put("CaseConstraint", "Old R1 dictionary not converted");
        missings.put("WhenConstraint", "Old R1 dictionary not converted");
        missings.put("Constraint", "Old R1 dictionary not converted");
        missings.put("MustOccurConstraint", "Old R1 dictionary not converted");
        missings.put("LookupConstraint", "Old R1 dictionary not converted");
        missings.put("CommonLookupParam", "Old R1 dictionary not converted");
        missings.put("CommonLookup", "Old R1 dictionary not converted");
        missings.put("DateRangeInfo", "DateRange was merged in with Milestone");
        missings.put("CredentialInfo", "LRC was revamped and Class II like objects were dropped");
        missings.put("CreditInfo", "LRC was revamped and Class II like objects were dropped");
        missings.put("ScaleInfo", "Changed to be ResultScaleInfo");
        missings.put("GradeInfo", "LRC was revamped and Class II like objects were dropped");
        missings.put("ResultComponentInfo", "Changed to be ResultValuesGroupInfo");
        missings.put("QueryParamInfo", "Is really a type object that holds typing info information about a parameter model as TypeInfo and use type-type relation to connnect it to search criteria");
        missings.put("FieldDescriptor", "Old pre-R1 dictionary structure that were attached to search param types were dropped -- ui dictionary provided that info");
        missings.put("SearchSelector", "Old pre-R1 dictionary structure that were attached to search param types were dropped -- ui dictionary provided that info");
        missings.put("ObjectStructure", "Old pre-R1 dictionary structure that were attached to search param types were dropped -- ui dictionary provided that info");
        missings.put("Type", "Old pre-R1 dictionary structure that were attached to search param types were dropped -- ui dictionary provided that info");
        missings.put("State", "Old pre-R1 dictionary structure that were attached to search param types were dropped -- ui dictionary provided that info");
        missings.put("Field", "Old pre-R1 dictionary structure that were attached to search param types were dropped -- ui dictionary provided that info");
        missings.put("ConstraintDescriptor", "Old pre-R1 dictionary structure that were attached to search param types were dropped -- ui dictionary provided that info");
        missings.put("ConstraintSelector", "Old pre-R1 dictionary structure that were attached to search param types were dropped -- ui dictionary provided that info");
        missings.put("RequireConstraint", "Old pre-R1 dictionary structure that were attached to search param types were dropped -- ui dictionary provided that info");
        missings.put("TypeStateCaseConstraint", "Old pre-R1 dictionary structure that were attached to search param types were dropped -- ui dictionary provided that info");
        missings.put("TypeStateWhenConstraint", "Old pre-R1 dictionary structure that were attached to search param types were dropped -- ui dictionary provided that info");
        missings.put("OccursConstraint", " Old pre-R1 dictionary structure that were attached to search param types were dropped -- ui dictionary provided that info");
        missings.put("ResultColumnInfo", " is really a type that describes the type of result that comes back, store as a TypeInfo object and use type-type relation to connect to result");
//        missings.put("SearchCriteriaTypeInfo", "The search criteria is really a type stucture that should be modeled as as TypeInfo and type-type relationship to connect it to a search type");
        missings.put("java.lang.String", "");
        missings.put("Map<String, String>", "");
        missings.put("LuiInfo", "Lui was pulled out and put in it's own service.  The LuiInfo object was not used in R1 and was radically redesigned in R2");

        knownUnconvertedObjects = missings;
        return;
    }
    private Map<String, String> knownObjectRenames = null;

    private void loadKnownObjectRenames() {
        Map<String, String> renames = new HashMap<String, String>();
        renames.put("Message", "MessageInfo");
        renames.put("SearchRequest", "SearchRequestInfo");
        renames.put("SearchResult", "SearchResultInfo");
        renames.put("SearchParam", "SearchParamInfo");
        renames.put("SearchResultRow", "SearchResultRowInfo");
        renames.put("SearchResultCell", "SearchResultCellInfo");
        renames.put("Message", "MessageInfo");
        knownObjectRenames = renames;
        return;
    }
    private Map<String, String> knownFieldRenames = null;

    private void loadKnownFieldRenames() {
        Map<String, String> renames = new HashMap<String, String>();
        renames.put("id", "key"); // not all the time but when it happens want to catch if id not found
        renames.put("desc", "descr");
        renames.put("state", "stateKey");
        renames.put("type", "typeKey");
        renames.put("metaInfo", "meta");
        renames.put("desc", "descr");
        renames.put("startTerm", "startTermId");
        renames.put("endTerm", "endTermId");
        renames.put("longDesc", "longDescr");
        renames.put("shortDesc", "shortDescr");
        renames.put("objectTypeURI", "refObjectUri");
        // TODO: this works but really should make these specific to the object they are connected with
        renames.put("detailDesc", "descr");
        renames.put("milestoneDate", "startDate");
        renames.put("success", "isSuccess");
        renames.put("relationType", "relationTypeKey");
        renames.put("unitType", "unitTypeKey");
        renames.put("enrollable", "isEnrollable");
        renames.put("hazardousForDisabledStudents", "isHazardousForDisabledStudents");
        renames.put("versionInfo", "version");
        renames.put("primary", "isPrimary");
        renames.put("activityType", "typeKey");
        renames.put("loRepository", "loRepositoryKey");
        renames.put("queryParamValueList", "queryParamValues");
        renames.put("credentialProgramType", "typeKey");
        knownFieldRenames = renames;
        return;
    }
    private Map<String, String> knownFieldIssues = null;

    private void loadKnownFieldIssues() {
        Map<String, String> issues = new HashMap<String, String>();
        issues.put("AtpInfo.key", "Switched from key to Id");
        issues.put("MilestoneInfo.key", "Switched from key to Id");
        issues.put("AtpInfo.id", ""); // suppress the extra field message from the r2 side
        issues.put("MilestoneInfo.id", ""); // ditto
        issues.put("MilestoneInfo.atpId", "Is not in R2 because a Milestone can be connected to more than one ATP so it is managed through a relationship");
        issues.put("Message.locale", "the type was changed from String to LocaleInfo to hold the different parts of the locale info");
        issues.put("SearchRequest.params", "");
        issues.put("SearchResult.rows", "");
        issues.put("SearchResultRow.cells", "");
        issues.put("ValidationResultInfo.errorLevel", "");
        issues.put("ValidationResultInfo.level", "");
        issues.put("ValidationResultInfo.ok", "");
        issues.put("ValidationResultInfo.warn", "");
        issues.put("ValidationResultInfo.error", "");
        issues.put("DocumentInfo.documentBinaryInfo", "renamd to just documentBinary (removing the trailing Info from the field name)");
        issues.put("OrgHierarchyInfo.key", "Switched from key to Id");
        issues.put("SearchResultTypeInfo.resultColumns", "ResultColumns is really anotther type to describe the column, Use type-type relation to hold that info");
        issues.put("ReqCompFieldTypeInfo.fieldDescriptor", "was dropped because it was an Old Pre-R1 dictionary and was not used -- UI dictionary provides that info instead");
        issues.put("LuTypeInfo.instructionalFormat", "Instructional format is a TypeInfo object and should be modeled as such using the type-type relation to connect it to a learning unit type");
        issues.put("LuTypeInfo.deliveryMethod", "Delivery method is a TypeInfo object and should be modeled as such using type-type relation to connect it to a learning unit type");
        issues.put("SearchCriteriaTypeInfo.queryParams", "Query Params is a TypeInfo that describes the parameter, model as type and type-type relation");
        issues.put("OrgOrgRelationTypeInfo.orgHierarchyKey", "This was removed because a particular relation type can participate in more than one hierarchies!");
        issues.put("SearchParam.value", "Renamed to values which is List<String>, in R1 the setValue method was overloaded to take a string or List, Kept in R2 but marked as deprecated");
        issues.put("", "");
        issues.put("", "");
        issues.put("", "");
        issues.put("", "");
        issues.put("", "");
        issues.put("", "");
        issues.put("", "");
        issues.put("", "");
        issues.put("", "");
        issues.put("", "");

        knownFieldIssues = issues;
        return;
    }

    private XmlType findType(XmlType r1) {
        XmlType r2 = finder2.findXmlType(r1.getName());
        if (r2 == null) {
            String renamedName = this.knownObjectRenames.get(r1.getName());
            if (renamedName != null) {
                r2 = finder2.findXmlType(renamedName);
                if (r2 == null) {
                    log.info("# (-) " + r1.getName() + ": was not found even after being renamed to " + renamedName);
                    return null;
                }
                log.info("# (/) " + r1.getName() + ": was renamed to " + renamedName);
                return r2;
            }
        }
        if (r2 == null) {
            if (r1.getName().endsWith("TypeInfo")) {
                r2 = finder2.findXmlType("TypeInfo");
            }
        }
        return r2;
    }

    private void findCompareType(XmlType r1) {
        if (r1.getName().endsWith("List")) {
            return;
        }
        if (this.knownUnconvertedObjects.containsKey(r1.getName())) {
            String message = this.knownUnconvertedObjects.get(r1.getName());
            if (message.isEmpty()) {
                return;
            }
            log.info("# (/) " + r1.getName() + ":" + message);
            return;
        }
        XmlType r2 = findType(r1);
        if (r2 == null) {
            log.info("# " + r1.getName() + ": has no corresponding object in r2");
            return;
        }
        Set<MessageStructure> usedInR2 = new HashSet<MessageStructure>();
        for (MessageStructure ms : finder1.findMessageStructures(r1.getName())) {
            MessageStructure used = findCompareMessageStructure(ms, r2);
            if (used != null) {
                usedInR2.add(used);
            }
        }
        // Don't report extra fields on type info
        if (!r2.getName().equals("TypeInfo")) {
            for (MessageStructure ms : finder2.findMessageStructures(r2.getName())) {
                if (usedInR2.contains(ms)) {
                    continue;
                }
                String issue = this.knownFieldIssues.get(ms.getXmlObject() + "." + ms.getShortName());
                if (issue != null) {
                    if (!issue.isEmpty()) {
                        log.info("# (*g) " + ms.getXmlObject() + "." + ms.getShortName() + ": " + issue);
                    }
                    continue;
                }
                log.info("# (+) " + ms.getXmlObject() + "." + ms.getShortName() + " - new field added in R2");
            }
        }
    }

    private MessageStructure findCompareMessageStructure(MessageStructure r1, XmlType xmlType2) {
        MessageStructure r2 = findMessageStructure(r1, xmlType2);
        String issue = this.knownFieldIssues.get(r1.getXmlObject() + "." + r1.getShortName());
        if (issue != null) {
            if (!issue.isEmpty()) {
                log.info("# (*g) " + r1.getXmlObject() + "." + r1.getShortName() + ": " + issue);
            }
            return r2;
        }
        if (r2 == null) {
            if (xmlType2.getName().equals("TypeInfo")) {
                if (r1.getShortName().endsWith("Type")
                        || r1.getShortName().endsWith("TypeInfo")
                        || r1.getShortName().endsWith("Types")
                        || r1.getShortName().endsWith("TypeInfos")) {
                    log.info("# (*g) " + r1.getXmlObject() + "." + r1.getShortName() + " was a type stored on a type: use type-type relationship instead");
                    return null;
                }
                log.info("# (!) " + r1.getXmlObject() + "." + r1.getShortName() + " was extra data on type, store in dynamic attribute if actually used");
                return null;
            }
            log.info("# (-) " + r1.getXmlObject() + "." + r1.getShortName() + " not found in r2: renamed to one of these? " + calcFieldNames(xmlType2));
            return null;
        }
        compareType(r1, r2);
        return r2;
    }

    private void compareType(MessageStructure r1, MessageStructure r2) {
        if (r1.getType().equalsIgnoreCase(r2.getType())) {
            return;
        }
        if (r1.getShortName().equals("attributes")) {
            if (r1.getType().equals("Map<String, String>")) {
                if (r2.getType().equals("AttributeInfoList")) {
                    return;
                }
            }
        }
        if (r1.getShortName().equals("desc") || r1.getShortName().equals("descr")) {
            if (r1.getType().equals("String")) {
                if (r2.getType().equals("RichTextInfo")) {
                    log.info("# (*g) " + r1.getXmlObject() + "." + r1.getShortName() + ": description type were changed to RichText, use plain version");
                    return;
                }
            }
        }
        log.info("# (!) " + r1.getXmlObject() + "." + r1.getShortName() + ": the type was changed from " + r1.getType() + " to " + r2.getType());
    }

    private MessageStructure findMessageStructure(MessageStructure r1, XmlType xmlType2) {
        MessageStructure r2 = finder2.findMessageStructure(xmlType2.getName(), r1.getShortName());
        if (r2 == null) {
            String renamed = this.knownFieldRenames.get(r1.getShortName());
            if (renamed != null) {
                r2 = finder2.findMessageStructure(xmlType2.getName(), renamed);
                if (r2 == null) {
                    log.info("# (-) " + r1.getXmlObject() + "." + r1.getShortName()
                            + " was renamed to " + xmlType2.getName() + "." + renamed
                            + " BUT IT STILL DIDN'T EXIST IN R2");
                    return null;
                }
                log.info("# (*g) " + r1.getXmlObject() + "." + r1.getShortName()
                        + " was renamed to " + xmlType2.getName() + "." + renamed);
            }
        }
        return r2;
    }

    private void compareMethods() {
        for (Service service : model1.getServices()) {
            log.info("");
            log.info("h2. " + service.getName() + " Methods");
            List<ServiceMethod> methodsInService = finder1.findServiceMethods(service.getKey());
            for (ServiceMethod method : methodsInService) {
                findCompareMethod(method);
            }
        }
    }

    private void findCompareMethod(ServiceMethod method1) {
        String issue = knownMethodIssues.get (method1.getService() + "Service." + method1.getName());
        if (issue != null) {
            if (!issue.isEmpty()) {
                log.info("# (*g) " + method1.getService() + "Service." + method1.getName()
                        + ": " + issue);
            }
            return;
        }
        ServiceMethod method2 = findMethod(method1);
        if (method2 == null) {
//            String possibleMethods = calcPossibleMethods(method1);
            if (isTypeMethod(method1)) {
                log.info("# (*g) " + method1.getService() + "Service." + method1.getName()
                        + " was dropped because it is a type, use TypeService instead");
                return;
            }
            String possibleMethods = this.calcPossibleMethods(method1);
            if (possibleMethods.isEmpty()) {
                log.info("# (-) " + method1.getService() + "Service." + method1.getName()
                        + " could not be found in R2");
            } else {
                log.info("# (!) " + method1.getService() + "Service." + method1.getName()
                        + " might have been renamed to one of these: "
                        + possibleMethods);
            }
            return;
        }
        if (!method1.getName().equals(method2.getName())) {
            log.info("# (*g) " + method1.getService() + "Service." + method1.getName()
                    + " was renamed to " + method2.getService() + "Service." + method2.getName());
        }
    }

    private ServiceMethod findMethod(ServiceMethod method1) {
        ServiceMethod method2 = findMethod2(method1.getService(), method1.getName());
        if (method2 == null) {
            String methodRename = knownMethodRenames.get(method1.getService() + "Service." + method1.getName());
            if (methodRename != null) {
                method2 = findMethod2(method1.getService(), methodRename);
                if (method2 == null) {
                    log.info("# (x) " + method1.getService() + "Service." + method1.getName() 
                            + " could not be found even after being renamed to " + methodRename);
                    return null;
                }
            }
        }
        return method2;
    }
    private Map<String, String> knownMethodRenames = null;

    private void loadKnownMethodRenames() {
        Map<String, String> renames = new HashMap<String, String>();
        renames.put("AtpService.getAtpsByAtpType", "getAtpIdsByType");
        renames.put("AtpService.getMilestonesByAtp", "getMilestonesForAtp");
        renames.put("AtpService.addMilestone", "addMilestoneToAtp");
        renames.put("AtpService.removeMilestone", "removeMilestoneFromAtp");
        renames.put("MessageService.getMessageGroups", "getMessageGroupKeys");
        renames.put("CommentService.getComments", "getCommentsByReferenceAndType");
        renames.put("CommentService.getTags", "getTagsByReferenceAndType"); 
        renames.put("CommentService.addTag", "createTag");
        renames.put("CommentService.addComment", "createComment");
        renames.put("CommentService.removeComment", "deleteComment");
        renames.put("CommentService.removeTag", "deleteTag");
        renames.put("CommentService.removeComments", "deleteCommentsByReference");
        renames.put("CommentService.removeTags", "deleteTagsByReference");
        renames.put("DocumentService.getDocumentsByIdList", "getDocumentsByIds");
        renames.put("DocumentService.getCategoriesByDocument", "getDocumentCategoriesByDocumentId");
        renames.put("DocumentService.getRefDocRelationsByDoc", "getRefDocRelationsByDocument");
        renames.put("EnumerationManagementService.removeEnumeratedValue", "deleteEnumeratedValue");
        renames.put("OrganizationService.getOrganization", "getOrg");
        renames.put("OrganizationService.getOrganizationsByIdList", "getOrgsByIds");
        renames.put("OrganizationService.getOrgOrgRelationsByIdList", "getOrgOrgRelationsByIds");
        renames.put("OrganizationService.getOrgPersonRelationsByIdList", "getOrgPersonRelationsByIds");
        renames.put("OrganizationService.getPersonIdsForOrgByRelationType", "");
        renames.put("OrganizationService.getAllOrgPersonRelationsByPerson", "getOrgPersonRelationsByPerson");
        renames.put("OrganizationService.getAllOrgPersonRelationsByOrg", "getOrgPersonRelationsByOrg");
        renames.put("OrganizationService.createOrganization", "createOrg");
        renames.put("OrganizationService.updateOrganization", "updateOrg");
        renames.put("OrganizationService.deleteOrganization", "deleteOrg");
        renames.put("OrganizationService.validateOrganization", "validateOrg");
        renames.put("OrganizationService.removeOrgOrgRelation", "deleteOrgOrgRelation");
        renames.put("OrganizationService.removeOrgPersonRelation", "deleteOrgPersonRelation");
        renames.put("OrganizationService.addPositionRestrictionToOrg", "createOrgPositionRestriction");
        renames.put("OrganizationService.updatePositionRestrictionForOrg", "updateOrgPositionRestriction");
        renames.put("OrganizationService.removePositionRestrictionFromOrg", "deleteOrgPositionRestriction");
        renames.put("StatementService.getStatementsUsingReqComponent", "getStatementsByReqComponent");
        renames.put("StatementService.getStatementsUsingStatement", "getStatementsForStatement");
        renames.put("CourseService.getCourseFormats", "getCourseFormatsByCourse");
        renames.put("CourseService.getCourseActivities", "getCourseActivitiesByCourseFormat");
        renames.put("CourseService.getCourseLos", "getCourseLearningObjectivesByCourse");
        renames.put("LearningObjectiveService.getLoCategories", "getLoCategoriesByLoRepository");
        renames.put("LearningObjectiveService.getLoByIdList", "getLosByIds");
        renames.put("LearningObjectiveService.getLosByRepository", "getLosByLoRepository");
        renames.put("LearningObjectiveService.getLoCategoriesForLo", "getLoCategoriesByLo");
        renames.put("LrcService.getResultComponent", "getResultValuesGroup");
        renames.put("LuService.getClusByIdList", "getClusByIds");
        renames.put("LuService.getAllowedLuLuRelationTypesByCluId", "getAllowedCluCluRelationTypesByClu");
        renames.put("LuService.getClusByRelation", "getClusByRelatedCluAndRelationType");
        renames.put("LuService.getCluIdsByRelation", "getCluIdsByRelatedCluAndRelationType");
        renames.put("LuService.getRelatedClusByCluId", "getRelatedClusByCluAndRelationType");
        renames.put("LuService.getRelatedCluIdsByCluId", "getRelatedCluIdsByCluAndRelationType");
        renames.put("LuService.getCluPublicationsByCluId", "getCluPublicationsByClu");
        renames.put("LuService.getResourceRequirementsForCluId", "getResourceRequirementsForClu");
        renames.put("LuService.getCluSetInfo", "getCluSet");
        renames.put("LuService.getCluSetInfoByIdList", "getCluSetsByIds");
        renames.put("LuService.getLuisByIdList", "getLuisByIds");
        renames.put("ProgramService.getMajorIdsByCredentialProgramType", "getMajorDisciplineIdsByCredentialProgramType");
        renames.put("ProgramService.getVariationsByMajorDisciplineId", "getProgramVariationsByMajorDiscipline");
        renames.put("ProgramService.getHonorsByCredentialProgramType", "getHonorProgramIdsByCredentialProgramType");
        renames.put("ProposalService.getProposalsByIdList", "getProposalsByIds");
        renames.put("", "");
        renames.put("", "");
        knownMethodRenames = renames;
        return;
    }

      private Map<String, String> knownMethodIssues = null;

    private void loadKnownMethodIssues() {
        Map<String, String> issues = new HashMap<String, String>();
        issues.put("AtpService.validateDateRange", "Dropped because DateRange objects were merged in with milestones");
        issues.put("AtpService.getDateRange", "Dropped because DateRange objects were merged in with milestones");
        issues.put("AtpService.getDateRangesByAtp", "Dropped because DateRange objects were merged in with milestones");
        issues.put("AtpService.getDateRangesByDate", "Dropped because DateRange objects were merged in with milestones");
        issues.put("AtpService.addDateRange", "Dropped because DateRange objects were merged in with milestones");
        issues.put("AtpService.updateDateRange", "Dropped because DateRange objects were merged in with milestones");
        issues.put("AtpService.removeDateRange", "Dropped because DateRange objects were merged in with milestones");
        issues.put("DictionaryService.getObjectTypes", "Dictionary service was completely revamped to match KRAD, old one is still around use that for R1 stuff");
        issues.put("DictionaryService.getObjectStructure", "Dictionary service was completely revamped to match KRAD, old one is still around use that for R1 stuff");
        issues.put("CommentService.getCommentsByType", "Renamed and changed to just get Ids, so use getCommentIdsByType then call getCommentsByIds");
        issues.put("CommentService.getTagsByType", "Renamed and changed to just get Ids, so use getTagIdsByType then call getTagsByIds");
        issues.put("DocumentService.getRefObjectTypes", "(!) has been dropped from the contract, the document service should store any uri");
        issues.put("DocumentService.getRefObjectSubTypes", "(!) has been dropped from the contract, the document service should store any uri and sub-object URI");
        issues.put("OrganizationService.getOrgOrgRelationsByRelatedOrg", " (!) the two methods for tranversing by one side of the relationship or other has replaced by a single method that finds relationships no matter which side it is on (?) Need to possibly rethink this it imposes a big change on both the implementation and on the the application. ");        
        issues.put("OrganizationService.getPersonIdsForOrgByRelationType", "Was removed, instead use getOrgPersonRelationsByTypeAndPerson and loop through the relationships to get the list of personIds that you want.  The issue was the old method did not take into account relationships that are old/inactive so using it would lead to errors that would only appear once transitions occured in the people being related to the org.");
        issues.put("OrganizationService.getOrgPersonRelationsByPerson", "Renamd to getOrgPersonRelationsByOrgAndPerson, because the R1 was badly named, it said just by person but the parameters required an Org as well!");
        issues.put("OrganizationService.getPositionRestrictionsByOrg", "use getOrgPositionRestrictionIdsByOrg then call getOrgPositionRestrictionsByIds to get the objects");
        issues.put("LearningObjectiveService.getAllowedLoLoRelationTypesForLoType", "is a type method, use Type Service instead");
        issues.put("LrcService.getCredential", "Is a Class 2 concept and as dropped from the Class 1 service");
        issues.put("LrcService.getCredentialsByKeyList", "Is a Class 2 concept and as dropped from the Class 1 service");
        issues.put("LrcService.getCredentialKeysByCredentialType", "Is a Class 2 concept and as dropped from the Class 1 service");
        issues.put("LrcService.getCredit", "Is a Class 2 concept and as dropped from the Class 1 service");
        issues.put("LrcService.getCreditsByKeyList", "Is a Class 2 concept and as dropped from the Class 1 service");
        issues.put("LrcService.getCreditKeysByCreditType", "Is a Class 2 concept and as dropped from the Class 1 service");
        issues.put("LrcService.getGrade", "Is a Class 2 concept and as dropped from the Class 1 service");
        issues.put("LrcService.getGradesByKeyList", "Is a Class 2 concept and as dropped from the Class 1 service");
        issues.put("LrcService.getGradeKeysByGradeType", "Is a Class 2 concept and as dropped from the Class 1 service");
        issues.put("LrcService.getGradesByScale", "Is a Class 2 concept and as dropped from the Class 1 service");
        issues.put("LrcService.translateGrade", "(-) is not being supported at this time, translations will be added later");
        issues.put("LrcService.compareGrades", "(-) is not being supported at this time, comparisons will be added later");
        issues.put("LrcService.getResultComponentIdsByResultComponentType", "roughly maps to getResultValuesGroupIdsByType but they are different objects and the types have changed as well");
        issues.put("LrcService.getResultComponentIdsByResult", "roughly maps to getResultValuesGroupsByResultValue but doesn't take the extra type parameter");
        issues.put("LrcService.createResultComponent", "rougly maps to createResultValuesGroup");
        issues.put("LrcService.updateResultComponent", "rougly maps to updateResultValuesGroup");
        issues.put("LrcService.deleteResultComponent", "rougly maps to deleteResultValuesGroup");
        issues.put("LrcService.getScale", "roughly maps to getResultScale");
        issues.put("LuService.getAllowedLuLuRelationTypesByLuiId", "is a type method, use TypeService instead");
        issues.put("", "");
        issues.put("", "");
        issues.put("", "");
        issues.put("", "");
        knownMethodIssues = issues;
        return;
    }
    
    
    private ServiceMethod findMethod2(String serviceKey, String methodName) {
        ServiceMethod method2 = finder2.findServiceMethod(serviceKey, methodName);
        if (method2 == null) {
            if (serviceKey.equals("Lu")) {
                method2 = finder2.findServiceMethod("Clu", methodName);
                if (method2 == null) {
                    method2 = finder2.findServiceMethod("Lui", methodName);
                }
            }
        }
        return method2;
    }

    private String calcMethods(ServiceMethod method1) {
        StringBuilder bldr = new StringBuilder();
        String comma = "";
        for (ServiceMethod method2 : finder2.findServiceMethods(method1.getService())) {
            bldr.append(comma);
            comma = ", ";
            bldr.append(method2.getName());
        }
        return bldr.toString();
    }

    private String calcPossibleMethods(ServiceMethod method1) {
        StringBuilder bldr = new StringBuilder();
        String comma = "";
        for (ServiceMethod method2 : findPossibleMethods(method1)) {
            bldr.append(comma);
            comma = ", ";
            bldr.append(method2.getName());
        }
        return bldr.toString();
    }

    private List<ServiceMethod> findPossibleMethods(ServiceMethod method1) {
        List<ServiceMethod> methods = new ArrayList<ServiceMethod>();
        List<ServiceMethod> wideNet = null;
        if (method1.getService().equals("Lu")) {
            wideNet = finder2.findServiceMethods("Clu");
            wideNet.addAll(finder2.findServiceMethods("Lui"));
        } else {
            wideNet = finder2.findServiceMethods(method1.getService());
        }
        for (ServiceMethod method2 : wideNet) {
            if (isPossibleMatch(method1, method2)) {
                methods.add(method2);
            }
        }
        return methods;
    }

    private boolean isPossibleMatch(ServiceMethod method1, ServiceMethod method2) {
         if (method1.getName().contains(method2.getName())) {
            return true;
        }
        if (method2.getName().contains(method1.getName())) {
            return true;
        }
        if (method1.getName().startsWith("get") && method2.getName().startsWith("get")) {
            return true;
        }
        if (method1.getName().startsWith("add") && method2.getName().startsWith("create")) {
            return true;
        }
        if (method1.getName().startsWith("create") && method2.getName().startsWith("create")) {
            return true;
        }
        if (method1.getName().startsWith("update") && method2.getName().startsWith("update")) {
            return true;
        }
        if (method1.getName().startsWith("delete") && method2.getName().startsWith("delete")) {
            return true;
        }
        if (method1.getName().startsWith("remove") && method2.getName().startsWith("delete")) {
            return true;
        }
        if (method1.getName().startsWith("validate") && method2.getName().startsWith("validate")) {
            return true;
        }
        return false;
    }

    private boolean isTypeMethod(ServiceMethod method1) {
        if (method1.getReturnValue().getType().endsWith("TypeInfo")) {
            return true;
        }
        if (method1.getReturnValue().getType().endsWith("TypeInfoList")) {
            return true;
        }
        return false;
    }
}
