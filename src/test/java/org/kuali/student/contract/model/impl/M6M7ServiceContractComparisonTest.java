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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
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

/**
 *
 * @author nwright
 */
@Ignore
public class M6M7ServiceContractComparisonTest {

    public M6M7ServiceContractComparisonTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    private ByteArrayOutputStream baos;
    private PrintStream out;

    @Before
    public void setUp() {
        baos = new ByteArrayOutputStream();
        out = new PrintStream(baos);

        out.println("This section was created by programmatically comparing the message structures.");
        out.println("Run on: " + new Date());
        out.println("See [M6M7ServiceContractComparisonTest.java|https://test.kuali.org/svn/student/tools/maven-kscontractdoc-plugin/trunk/src/test/java/org/kuali/student/contract/model/impl/M6M7ServiceContractComparisonTest.java]");
        out.println("");
        out.println("Legend:");
        out.println("* (-) Removed or dropped from contract");
        out.println("* (+) Added to contract");
        out.println("* (/) Renamed in contract");
        out.println("* (*y) Change to contract");
        out.println("* (!) Deprecated");
        out.println("");
        out.println("*TABLE OF CONTENTS*");
        out.println("{toc}");
        out.println("");
        out.println("h1. Loading models of the contracts from the source code");
        out.println("h2. Log from loading model for M6");
        getModelM6();
        out.println("h2. Log from loading model for M7");
        getModelM7();
        getFinderM6();
        getFinderM7();
        loadKnownObjectRenames();
        loadKnownUnconvertedObjects();
        loadKnownFieldRenames();
        loadKnownFieldIssues();
        loadKnownMethodRenames();
        loadKnownMethodIssues();
    }

    @After
    public void tearDown() {
        if (baos != null) {
            System.out.append(baos.toString());
        }
    }
    private static final String RESOURCES_DIRECTORY = "src/test/resources";
    private static final String TEST_SOURCE_DIRECTORY =
            "src/test/java/org/kuali/student/contract/model/test/source";
    private static final String M6_PROJECT_API_DIRECTORY = "D:/svn/ks/trunk/ks-api";
    private static final String M6_COMMON_API_DIRECTORY = M6_PROJECT_API_DIRECTORY + "/ks-common-api/src/main/java";
    private static final String M6_CORE_API_DIRECTORY = M6_PROJECT_API_DIRECTORY + "/ks-core-api/src/main/java";
    private static final String M6_LUM_API_DIRECTORY = M6_PROJECT_API_DIRECTORY + "/ks-lum-api/src/main/java";
    private static final String M6_ENROLL_API_DIRECTORY = M6_PROJECT_API_DIRECTORY + "/ks-enroll-api/src/main/java";
    private static final String M7_PROJECT_API_DIRECTORY = "D:/svn/ks/services/ks-api";
    private static final String M7_COMMON_API_DIRECTORY = M7_PROJECT_API_DIRECTORY + "/ks-common-api/src/main/java";
    private static final String M7_CORE_API_DIRECTORY = M7_PROJECT_API_DIRECTORY + "/ks-core-api/src/main/java";
    private static final String M7_LUM_API_DIRECTORY = M7_PROJECT_API_DIRECTORY + "/ks-lum-api/src/main/java";
    private static final String M7_ENROLL_API_DIRECTORY = M7_PROJECT_API_DIRECTORY + "/ks-enroll-api/src/main/java";
    private static ServiceContractModel modelM6 = null;
    private static ServiceContractModel modelM7 = null;
    private Map<String, String> knownMethodRenames = null;
    private Map<String, String> knownFieldRenames = null;
    private Map<String, String> knownMethodIssues = null;
    private Map<String, String> knownUnconvertedObjects = null;

    /**
     * Test of getServiceMethods method, of class
     * ServiceContractModelQDoxLoader.
     */
    @Test
    public void testCompareModels() {
        out.println("");
        out.println("h1. Message Structure Comparison");
        compareTypes();
        out.println("");
        out.println("h1. Service Method Comparison");
        compareMethods();
    }

    private ServiceContractModel getModelM6() {
        if (modelM6 != null) {
            return modelM6;
        }
        List<String> srcDirs = new ArrayList();
        out.println("User directory=" + System.getProperty("user.dir"));
        out.println("Current directory=" + new File(".").getAbsolutePath());
        srcDirs.add(M6_COMMON_API_DIRECTORY);
        srcDirs.add(M6_CORE_API_DIRECTORY);
        srcDirs.add(M6_LUM_API_DIRECTORY);
        srcDirs.add(M6_ENROLL_API_DIRECTORY);
        out.println("Reading as input:");
        for (String directory : srcDirs) {
            out.println("* " + directory);
        }
        out.println("");
        boolean validateKualiStudent = false;
        ServiceContractModel instance = new ServiceContractModelQDoxLoader(srcDirs, validateKualiStudent);

        instance = new ServiceContractModelCache(instance);
        validate(instance);
        modelM6 = instance;
        return instance;
    }

    private ServiceContractModel getModelM7() {
        if (modelM7 != null) {
            return modelM7;
        }
        List<String> srcDirs = new ArrayList();
        out.println("User directory=" + System.getProperty("user.dir"));
        out.println("Current directory=" + new File(".").getAbsolutePath());
        srcDirs.add(M7_COMMON_API_DIRECTORY);
        srcDirs.add(M7_CORE_API_DIRECTORY);
        srcDirs.add(M7_LUM_API_DIRECTORY);
        srcDirs.add(M7_ENROLL_API_DIRECTORY);
        out.println("Reading as input:");
        for (String directory : srcDirs) {
            out.println("* " + directory);
        }
        out.println("");
        boolean validateKualiStudent = true;
        ServiceContractModel instance = new ServiceContractModelQDoxLoader(srcDirs, validateKualiStudent);

        instance = new ServiceContractModelCache(instance);
        validate(instance);
        modelM7 = instance;
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
    private ModelFinder finderM6 = null;

    private ModelFinder getFinderM6() {
        if (finderM6 == null) {
            finderM6 = new ModelFinder(getModelM6());
        }
        return finderM6;
    }
    private ModelFinder finderM7 = null;

    private ModelFinder getFinderM7() {
        if (finderM7 == null) {
            finderM7 = new ModelFinder(getModelM7());
        }
        return finderM7;
    }

    private void compareTypes() {
        for (Service service : modelM6.getServices()) {
            Set<String> found = new LinkedHashSet<String>();
            out.println("");
            out.println("h2. " + service.getName() + " Structures");
            for (XmlType xmlTypeM6 : finderM6.findAllComplexTypesInService(service.getKey())) {
                XmlType xmlTypeM7 = findCompareType(xmlTypeM6);
                if (xmlTypeM7 != null) {
                    found.add(xmlTypeM7.getName());
                }
            }
            for (XmlType xmlTypeM7 : finderM7.findAllComplexTypesInService(service.getKey())) {
                if (!found.contains(xmlTypeM7.getName())) {
                    out.println("# (+) " + xmlTypeM7.getName() + ": was added in M7");
                }
            }
        }
    }

    private String calcService(XmlType xmlType) {
        StringBuilder bldr = new StringBuilder();
        String comma = "";
        for (String serviceKey : HtmlContractMessageStructureWriter.calcUsageByService(modelM6, xmlType)) {
            bldr.append(comma);
            comma = ", ";
            bldr.append(serviceKey);
        }
        return bldr.toString();
    }

    private String calcFieldNames(XmlType xmlType) {
        StringBuilder bldr = new StringBuilder();
        String comma = "";
        for (MessageStructure ms : finderM7.findMessageStructures(xmlType.getName())) {
            bldr.append(comma);
            comma = ", ";
            bldr.append(ms.getShortName());
        }
        return bldr.toString();
    }

    private void loadKnownUnconvertedObjects() {
        Map<String, String> missings = new HashMap<String, String>();
//        missings.put("ObjectStructureDefinition", "Old M6 dictionary not converted");
//        missings.put("FieldDefinition", "Old M6 dictionary not converted");
//        missings.put("ValidCharsConstraint", "Old M6 dictionary not converted");
//        missings.put("RequiredConstraint", "Old M6 dictionary not converted");
//        missings.put("CaseConstraint", "Old M6 dictionary not converted");
//        missings.put("WhenConstraint", "Old M6 dictionary not converted");
//        missings.put("Constraint", "Old M6 dictionary not converted");
//        missings.put("MustOccurConstraint", "Old M6 dictionary not converted");
//        missings.put("LookupConstraint", "Old M6 dictionary not converted");
//        missings.put("CommonLookupParam", "Old M6 dictionary not converted");
//        missings.put("CommonLookup", "Old M6 dictionary not converted");
//        missings.put("DateRangeInfo", "DateRange was merged in with Milestone");
//        missings.put("CredentialInfo", "LRC was revamped and Class II like objects were dropped");
//        missings.put("CreditInfo", "LRC was revamped and Class II like objects were dropped");
//        missings.put("ScaleInfo", "Changed to be ResultScaleInfo");
//        missings.put("GradeInfo", "LRC was revamped and Class II like objects were dropped");
//        missings.put("ResultComponentInfo", "Changed to be ResultValuesGroupInfo");
//        missings.put("QueryParamInfo", "Is really a type object that holds typing info information about a parameter model as TypeInfo and use type-type relation to connnect it to search criteria");
//        missings.put("FieldDescriptor", "Old pre-M6 dictionary structure that were attached to search param types were dropped -- ui dictionary provided that info");
//        missings.put("SearchSelector", "Old pre-M6 dictionary structure that were attached to search param types were dropped -- ui dictionary provided that info");
//        missings.put("ObjectStructure", "Old pre-M6 dictionary structure that were attached to search param types were dropped -- ui dictionary provided that info");
//        missings.put("Type", "Old pre-M6 dictionary structure that were attached to search param types were dropped -- ui dictionary provided that info");
//        missings.put("State", "Old pre-M6 dictionary structure that were attached to search param types were dropped -- ui dictionary provided that info");
//        missings.put("Field", "Old pre-M6 dictionary structure that were attached to search param types were dropped -- ui dictionary provided that info");
//        missings.put("ConstraintDescriptor", "Old pre-M6 dictionary structure that were attached to search param types were dropped -- ui dictionary provided that info");
//        missings.put("ConstraintSelector", "Old pre-M6 dictionary structure that were attached to search param types were dropped -- ui dictionary provided that info");
//        missings.put("RequireConstraint", "Old pre-M6 dictionary structure that were attached to search param types were dropped -- ui dictionary provided that info");
//        missings.put("TypeStateCaseConstraint", "Old pre-M6 dictionary structure that were attached to search param types were dropped -- ui dictionary provided that info");
//        missings.put("TypeStateWhenConstraint", "Old pre-M6 dictionary structure that were attached to search param types were dropped -- ui dictionary provided that info");
//        missings.put("OccursConstraint", " Old pre-M6 dictionary structure that were attached to search param types were dropped -- ui dictionary provided that info");
//        missings.put("ResultColumnInfo", " is really a type that describes the type of result that comes back, store as a TypeInfo object and use type-type relation to connect to result");
//        missings.put("SearchCriteriaTypeInfo", "The search criteria is really a type stucture that should be modeled as as TypeInfo and type-type relationship to connect it to a search type");
//        missings.put("java.lang.String", "");
//        missings.put("Map<String, String>", "");
//        missings.put("LuiInfo", "Lui was pulled out and put in it's own service.  The LuiInfo object was not used in M6 and was radically redesigned in M7");

        knownUnconvertedObjects = missings;
        return;
    }
    private Map<String, String> knownObjectRenames = null;

    private void loadKnownObjectRenames() {
        Map<String, String> renames = new HashMap<String, String>();
//        renames.put("Message", "MessageInfo");
//        renames.put("SearchRequest", "SearchRequestInfo");
//        renames.put("SearchResult", "SearchResultInfo");
//        renames.put("SearchParam", "SearchParamInfo");
//        renames.put("SearchResultRow", "SearchResultRowInfo");
//        renames.put("SearchResultCell", "SearchResultCellInfo");
//        renames.put("Message", "MessageInfo");
        knownObjectRenames = renames;
        return;
    }

    private void loadKnownFieldRenames() {
        Map<String, String> renames = new HashMap<String, String>();
        renames.put("OrgCodeInfo.key", "OrgCodeInfo.typeKey"); // not all the time but when it happens want to catch if id not found
//        renames.put("desc", "descr");
//        renames.put("state", "stateKey");
//        renames.put("type", "typeKey");
//        renames.put("metaInfo", "meta");
//        renames.put("desc", "descr");
//        renames.put("startTerm", "startTermId");
//        renames.put("endTerm", "endTermId");
//        renames.put("longDesc", "longDescr");
//        renames.put("shortDesc", "shortDescr");
//        renames.put("objectTypeURI", "refObjectUri");
//        // TODO: this works but really should make these specific to the object they are connected with
//        renames.put("detailDesc", "descr");
//        renames.put("milestoneDate", "startDate");
//        renames.put("success", "isSuccess");
//        renames.put("relationType", "relationTypeKey");
//        renames.put("unitType", "unitTypeKey");
//        renames.put("enrollable", "isEnrollable");
//        renames.put("hazardousForDisabledStudents", "isHazardousForDisabledStudents");
//        renames.put("versionInfo", "version");
//        renames.put("primary", "isPrimary");
//        renames.put("activityType", "typeKey");
//        renames.put("loRepository", "loRepositoryKey");
//        renames.put("queryParamValueList", "queryParamValues");
//        renames.put("credentialProgramType", "typeKey");
        knownFieldRenames = renames;
        return;
    }
    private Map<String, String> knownFieldIssues = null;

    private void loadKnownFieldIssues() {
        Map<String, String> issues = new HashMap<String, String>();
//        issues.put("AtpInfo.key", "Switched from key to Id");
//        issues.put("MilestoneInfo.key", "Switched from key to Id");
//        issues.put("AtpInfo.id", ""); // suppress the extra field message from the m7 side
//        issues.put("MilestoneInfo.id", ""); // ditto
//        issues.put("MilestoneInfo.atpId", "Is not in M7 because a Milestone can be connected to more than one ATP so it is managed through a relationship");
//        issues.put("Message.locale", "the type was changed from String to LocaleInfo to hold the different parts of the locale info");
//        issues.put("SearchRequest.params", "");
//        issues.put("SearchResult.rows", "");
//        issues.put("SearchResultRow.cells", "");
//        issues.put("ValidationResultInfo.errorLevel", "");
//        issues.put("ValidationResultInfo.level", "");
//        issues.put("ValidationResultInfo.ok", "");
//        issues.put("ValidationResultInfo.warn", "");
//        issues.put("ValidationResultInfo.error", "");
//        issues.put("DocumentInfo.documentBinaryInfo", "renamd to just documentBinary (removing the trailing Info from the field name)");
//        issues.put("OrgHierarchyInfo.key", "Switched from key to Id");
//        issues.put("SearchResultTypeInfo.resultColumns", "ResultColumns is really anotther type to describe the column, Use type-type relation to hold that info");
//        issues.put("ReqCompFieldTypeInfo.fieldDescriptor", "was dropped because it was an Old Pre-M6 dictionary and was not used -- UI dictionary provides that info instead");
//        issues.put("LuTypeInfo.instructionalFormat", "Instructional format is a TypeInfo object and should be modeled as such using the type-type relation to connect it to a learning unit type");
//        issues.put("LuTypeInfo.deliveryMethod", "Delivery method is a TypeInfo object and should be modeled as such using type-type relation to connect it to a learning unit type");
//        issues.put("SearchCriteriaTypeInfo.queryParams", "Query Params is a TypeInfo that describes the parameter, model as type and type-type relation");
//        issues.put("OrgOrgRelationTypeInfo.orgHierarchyKey", "This was removed because a particular relation type can participate in more than one hierarchies!");
//        issues.put("SearchParam.value", "Renamed to values which is List<String>, in M6 the setValue method was overloaded to take a string or List, Kept in M7 but marked as deprecated");
//        issues.put("", "");
//        issues.put("", "");
//        issues.put("", "");
//        issues.put("", "");
//        issues.put("", "");
//        issues.put("", "");
//        issues.put("", "");
//        issues.put("", "");
//        issues.put("", "");
//        issues.put("", "");

        knownFieldIssues = issues;
        return;
    }

    private void loadKnownMethodRenames() {
        Map<String, String> renames = new HashMap<String, String>();
//        renames.put("AtpService.getAtpsByAtpType", "getAtpIdsByType");
//        renames.put("AtpService.getMilestonesByAtp", "getMilestonesForAtp");
//        renames.put("AtpService.addMilestone", "addMilestoneToAtp");
//        renames.put("AtpService.removeMilestone", "removeMilestoneFromAtp");
//        renames.put("MessageService.getMessageGroups", "getMessageGroupKeys");
//        renames.put("CommentService.getComments", "getCommentsByReferenceAndType");
//        renames.put("CommentService.getTags", "getTagsByReferenceAndType"); 
//        renames.put("CommentService.addTag", "createTag");
//        renames.put("CommentService.addComment", "createComment");
//        renames.put("CommentService.removeComment", "deleteComment");
//        renames.put("CommentService.removeTag", "deleteTag");
//        renames.put("CommentService.removeComments", "deleteCommentsByReference");
//        renames.put("CommentService.removeTags", "deleteTagsByReference");
//        renames.put("DocumentService.getDocumentsByIdList", "getDocumentsByIds");
//        renames.put("DocumentService.getCategoriesByDocument", "getDocumentCategoriesByDocumentId");
//        renames.put("DocumentService.getRefDocRelationsByDoc", "getRefDocRelationsByDocument");
//        renames.put("EnumerationManagementService.removeEnumeratedValue", "deleteEnumeratedValue");
//        renames.put("OrganizationService.getOrganization", "getOrg");
//        renames.put("OrganizationService.getOrganizationsByIdList", "getOrgsByIds");
//        renames.put("OrganizationService.getOrgOrgRelationsByIdList", "getOrgOrgRelationsByIds");
//        renames.put("OrganizationService.getOrgPersonRelationsByIdList", "getOrgPersonRelationsByIds");
//        renames.put("OrganizationService.getPersonIdsForOrgByRelationType", "");
//        renames.put("OrganizationService.getAllOrgPersonRelationsByPerson", "getOrgPersonRelationsByPerson");
//        renames.put("OrganizationService.getAllOrgPersonRelationsByOrg", "getOrgPersonRelationsByOrg");
//        renames.put("OrganizationService.createOrganization", "createOrg");
//        renames.put("OrganizationService.updateOrganization", "updateOrg");
//        renames.put("OrganizationService.deleteOrganization", "deleteOrg");
//        renames.put("OrganizationService.validateOrganization", "validateOrg");
//        renames.put("OrganizationService.removeOrgOrgRelation", "deleteOrgOrgRelation");
//        renames.put("OrganizationService.removeOrgPersonRelation", "deleteOrgPersonRelation");
//        renames.put("OrganizationService.addPositionRestrictionToOrg", "createOrgPositionRestriction");
//        renames.put("OrganizationService.updatePositionRestrictionForOrg", "updateOrgPositionRestriction");
//        renames.put("OrganizationService.removePositionRestrictionFromOrg", "deleteOrgPositionRestriction");
//        renames.put("StatementService.getStatementsUsingReqComponent", "getStatementsByReqComponent");
//        renames.put("StatementService.getStatementsUsingStatement", "getStatementsForStatement");
//        renames.put("CourseService.getCourseFormats", "getCourseFormatsByCourse");
//        renames.put("CourseService.getCourseActivities", "getCourseActivitiesByCourseFormat");
//        renames.put("CourseService.getCourseLos", "getCourseLearningObjectivesByCourse");
//        renames.put("LearningObjectiveService.getLoCategories", "getLoCategoriesByLoRepository");
//        renames.put("LearningObjectiveService.getLoByIdList", "getLosByIds");
//        renames.put("LearningObjectiveService.getLosByRepository", "getLosByLoRepository");
//        renames.put("LearningObjectiveService.getLoCategoriesForLo", "getLoCategoriesByLo");
//        renames.put("LrcService.getResultComponent", "getResultValuesGroup");
//        renames.put("LuService.getClusByIdList", "getClusByIds");
//        renames.put("LuService.getAllowedLuLuRelationTypesByCluId", "getAllowedCluCluRelationTypesByClu");
//        renames.put("LuService.getClusByRelation", "getClusByRelatedCluAndRelationType");
//        renames.put("LuService.getCluIdsByRelation", "getCluIdsByRelatedCluAndRelationType");
//        renames.put("LuService.getRelatedClusByCluId", "getRelatedClusByCluAndRelationType");
//        renames.put("LuService.getRelatedCluIdsByCluId", "getRelatedCluIdsByCluAndRelationType");
//        renames.put("LuService.getCluPublicationsByCluId", "getCluPublicationsByClu");
//        renames.put("LuService.getResourceRequirementsForCluId", "getResourceRequirementsForClu");
//        renames.put("LuService.getCluSetInfo", "getCluSet");
//        renames.put("LuService.getCluSetInfoByIdList", "getCluSetsByIds");
//        renames.put("LuService.getLuisByIdList", "getLuisByIds");
//        renames.put("ProgramService.getMajorIdsByCredentialProgramType", "getMajorDisciplineIdsByCredentialProgramType");
//        renames.put("ProgramService.getVariationsByMajorDisciplineId", "getProgramVariationsByMajorDiscipline");
//        renames.put("ProgramService.getHonorsByCredentialProgramType", "getHonorProgramIdsByCredentialProgramType");
//        renames.put("ProposalService.getProposalsByIdList", "getProposalsByIds");
//        renames.put("", "");
//        renames.put("", "");
        knownMethodRenames = renames;
        return;
    }

    private void loadKnownMethodIssues() {
        Map<String, String> issues = new HashMap<String, String>();
//        issues.put("AtpService.validateDateRange", "Dropped because DateRange objects were merged in with milestones");
//        issues.put("AtpService.getDateRange", "Dropped because DateRange objects were merged in with milestones");
//        issues.put("AtpService.getDateRangesByAtp", "Dropped because DateRange objects were merged in with milestones");
//        issues.put("AtpService.getDateRangesByDate", "Dropped because DateRange objects were merged in with milestones");
//        issues.put("AtpService.addDateRange", "Dropped because DateRange objects were merged in with milestones");
//        issues.put("AtpService.updateDateRange", "Dropped because DateRange objects were merged in with milestones");
//        issues.put("AtpService.removeDateRange", "Dropped because DateRange objects were merged in with milestones");
//        issues.put("DictionaryService.getObjectTypes", "Dictionary service was completely revamped to match KRAD, old one is still around use that for M6 stuff");
//        issues.put("DictionaryService.getObjectStructure", "Dictionary service was completely revamped to match KRAD, old one is still around use that for M6 stuff");
//        issues.put("CommentService.getCommentsByType", "Renamed and changed to just get Ids, so use getCommentIdsByType then call getCommentsByIds");
//        issues.put("CommentService.getTagsByType", "Renamed and changed to just get Ids, so use getTagIdsByType then call getTagsByIds");
//        issues.put("DocumentService.getRefObjectTypes", "(!) has been dropped from the contract, the document service should store any uri");
//        issues.put("DocumentService.getRefObjectSubTypes", "(!) has been dropped from the contract, the document service should store any uri and sub-object URI");
//        issues.put("OrganizationService.getOrgOrgRelationsByRelatedOrg", " (!) the two methods for tranversing by one side of the relationship or other has replaced by a single method that finds relationships no matter which side it is on (?) Need to possibly rethink this it imposes a big change on both the implementation and on the the application. ");        
//        issues.put("OrganizationService.getPersonIdsForOrgByRelationType", "Was removed, instead use getOrgPersonRelationsByTypeAndPerson and loop through the relationships to get the list of personIds that you want.  The issue was the old method did not take into account relationships that are old/inactive so using it would lead to errors that would only appear once transitions occured in the people being related to the org.");
//        issues.put("OrganizationService.getOrgPersonRelationsByPerson", "Renamd to getOrgPersonRelationsByOrgAndPerson, because the M6 was badly named, it said just by person but the parameters required an Org as well!");
//        issues.put("OrganizationService.getPositionRestrictionsByOrg", "use getOrgPositionRestrictionIdsByOrg then call getOrgPositionRestrictionsByIds to get the objects");
//        issues.put("LearningObjectiveService.getAllowedLoLoRelationTypesForLoType", "is a type method, use Type Service instead");
//        issues.put("LrcService.getCredential", "Is a Class 2 concept and as dropped from the Class 1 service");
//        issues.put("LrcService.getCredentialsByKeyList", "Is a Class 2 concept and as dropped from the Class 1 service");
//        issues.put("LrcService.getCredentialKeysByCredentialType", "Is a Class 2 concept and as dropped from the Class 1 service");
//        issues.put("LrcService.getCredit", "Is a Class 2 concept and as dropped from the Class 1 service");
//        issues.put("LrcService.getCreditsByKeyList", "Is a Class 2 concept and as dropped from the Class 1 service");
//        issues.put("LrcService.getCreditKeysByCreditType", "Is a Class 2 concept and as dropped from the Class 1 service");
//        issues.put("LrcService.getGrade", "Is a Class 2 concept and as dropped from the Class 1 service");
//        issues.put("LrcService.getGradesByKeyList", "Is a Class 2 concept and as dropped from the Class 1 service");
//        issues.put("LrcService.getGradeKeysByGradeType", "Is a Class 2 concept and as dropped from the Class 1 service");
//        issues.put("LrcService.getGradesByScale", "Is a Class 2 concept and as dropped from the Class 1 service");
//        issues.put("LrcService.translateGrade", "(-) is not being supported at this time, translations will be added later");
//        issues.put("LrcService.compareGrades", "(-) is not being supported at this time, comparisons will be added later");
//        issues.put("LrcService.getResultComponentIdsByResultComponentType", "roughly maps to getResultValuesGroupIdsByType but they are different objects and the types have changed as well");
//        issues.put("LrcService.getResultComponentIdsByResult", "roughly maps to getResultValuesGroupsByResultValue but doesn't take the extra type parameter");
//        issues.put("LrcService.createResultComponent", "rougly maps to createResultValuesGroup");
//        issues.put("LrcService.updateResultComponent", "rougly maps to updateResultValuesGroup");
//        issues.put("LrcService.deleteResultComponent", "rougly maps to deleteResultValuesGroup");
//        issues.put("LrcService.getScale", "roughly maps to getResultScale");
//        issues.put("LuService.getAllowedLuLuRelationTypesByLuiId", "is a type method, use TypeService instead");
//        issues.put("", "");
//        issues.put("", "");
//        issues.put("", "");
//        issues.put("", "");
        knownMethodIssues = issues;
        return;
    }

    private XmlType findType(XmlType m6) {
        XmlType m7 = finderM7.findXmlType(m6.getName());
        if (m7 == null) {
            String renamedName = this.knownObjectRenames.get(m6.getName());
            if (renamedName != null) {
                m7 = finderM7.findXmlType(renamedName);
                if (m7 == null) {
                    out.println("# (-) " + m6.getName() + ": was not found even after being renamed to " + renamedName);
                    return null;
                }
                out.println("# (/) " + m6.getName() + ": was renamed to " + renamedName);
                return m7;
            }
        }
//        if (m7 == null) {
//            if (m6.getName().endsWith("TypeInfo")) {
//                m7 = finderM7.findXmlType("TypeInfo");
//            }
//        }
        return m7;
    }

    private XmlType findCompareType(XmlType m6) {
        if (m6.getName().endsWith("List")) {
            return null;
        }
        if (this.knownUnconvertedObjects.containsKey(m6.getName())) {
            String message = this.knownUnconvertedObjects.get(m6.getName());
            if (message.isEmpty()) {
                return null;
            }
            out.println("# (-) " + m6.getName() + ":" + message);
            return null;
        }
        XmlType m7 = findType(m6);
        if (m7 == null) {
            out.println("# (-) " + m6.getName() + ": has no corresponding object in M7");
            return m7;
        }
        Set<MessageStructure> usedInM7 = new HashSet<MessageStructure>();
        for (MessageStructure ms : finderM6.findMessageStructures(m6.getName())) {
            MessageStructure used = findCompareMessageStructure(ms, m7);
            if (used != null) {
                usedInM7.add(used);
            }
        }
        return m7;
    }

    private MessageStructure findCompareMessageStructure(MessageStructure m6, XmlType xmlTypeM7) {
        MessageStructure m7 = findMessageStructure(m6, xmlTypeM7);
        String issue = this.knownFieldIssues.get(m6.getXmlObject() + "." + m6.getShortName());
        if (issue != null) {
            if (!issue.isEmpty()) {
                out.println("# (*y) " + m6.getXmlObject() + "." + m6.getShortName() + ": " + issue);
            }
            return m7;
        }
        if (m7 == null) {
            out.println("# (-) " + m6.getXmlObject() + "." + m6.getShortName() + " not found in M7: perhaps it was renamed to one of these? " + calcFieldNames(xmlTypeM7));
            return null;
        }
        compareType(m6, m7);
        compareDeprecation(m6, m7);
        return m7;
    }

    private void compareType(MessageStructure m6, MessageStructure m7) {
        if (m6.getType().equalsIgnoreCase(m7.getType())) {
            return;
        }
        out.println("# (*y) " + m6.getXmlObject() + "." + m6.getShortName() + ": the type was changed from " + m6.getType() + " to " + m7.getType());
    }

    private void compareDeprecation(MessageStructure m6, MessageStructure m7) {
        if (m6.isDeprecated() && m7.isDeprecated()) {
            return;
        }
        if (!m6.isDeprecated() && !m7.isDeprecated()) {
            return;
        }
        if (!m6.isDeprecated() && m7.isDeprecated()) {
            out.println("# (!) " + m6.getXmlObject() + "." + m6.getShortName() + " has been deprecated and may be removed in future releases");
            return;
        }

        if (m6.isDeprecated() && !m7.isDeprecated()) {
            out.println("# (!) " + m6.getXmlObject() + "." + m6.getShortName() + " had been dedprecated but is no longer marked as such");
            return;
        }
        throw new RuntimeException("should never reach here");
    }

    private MessageStructure findMessageStructure(MessageStructure m6, XmlType xmlTypeM7) {
        MessageStructure m7 = finderM7.findMessageStructure(xmlTypeM7.getName(), m6.getShortName());
        if (m7 == null) {
            String renamed = this.knownFieldRenames.get(m6.getShortName());
            if (renamed != null) {
                m7 = finderM7.findMessageStructure(xmlTypeM7.getName(), renamed);
                if (m7 == null) {
                    out.println("# (-) " + m6.getXmlObject() + "." + m6.getShortName()
                            + " was renamed to " + xmlTypeM7.getName() + "." + renamed
                            + " BUT IT STILL DIDN'T EXIST IN M7");
                    return null;
                }
                out.println("# (*y) " + m6.getXmlObject() + "." + m6.getShortName()
                        + " was renamed to " + xmlTypeM7.getName() + "." + renamed);
            }
        }
        return m7;
    }

    private void compareMethods() {
        for (Service service : modelM6.getServices()) {
            Set<String> found = new LinkedHashSet<String>();
            out.println("");
            out.println("h2. " + service.getName() + " Methods");
            List<ServiceMethod> methodsInService = finderM6.findServiceMethods(service.getKey());
            for (ServiceMethod method : methodsInService) {
                ServiceMethod methodM7 = findCompareMethod(method);
                if (methodM7 != null) {
                    found.add(methodM7.getName());
                }
            }
            for (ServiceMethod methodM7 : finderM7.findServiceMethods(service.getKey())) {
                if (!found.contains(methodM7.getName())) {
                    out.println("# (+) " + methodM7.getName() + ": was added in M7");
                }
            }
        }
    }

    private ServiceMethod findCompareMethod(ServiceMethod methodM6) {
        String issue = knownMethodIssues.get(methodM6.getService() + "Service." + methodM6.getName());
        if (issue != null) {
            if (!issue.isEmpty()) {
                out.println("# (*y) " + methodM6.getService() + "Service." + methodM6.getName()
                        + ": " + issue);
            }
            return null;
        }
        ServiceMethod methodM7 = findMethod(methodM6);
        if (methodM7 == null) {
            String possibleMethods = this.calcPossibleMethods(methodM6);
            if (possibleMethods.isEmpty()) {
                out.println("# (-) " + methodM6.getService() + "Service." + methodM6.getName()
                        + " could not be found in M7");
            } else {
                out.println("# (-) " + methodM6.getService() + "Service." + methodM6.getName()
                        + " does not exist in M7.  It might have been renamed to one of these: "
                        + possibleMethods);
            }
            return null;
        }
        if (!methodM6.getName().equals(methodM7.getName())) {
            out.println("# (*y) " + methodM6.getService() + "Service." + methodM6.getName()
                    + " was renamed to " + methodM7.getService() + "Service." + methodM7.getName());
        }
        if (!doAllParametersMatch(methodM6, methodM7)) {
           out.print("# (*y) " + methodM6.getService() + "Service." + methodM6.getName()
                    + " parameters changed from (");
           String comma = "";
           for (ServiceMethodParameter param : methodM6.getParameters()) {
               out.print (comma);
               comma = ", ";
               out.print (param.getType() + " " + param.getName());
           } 
           out.print (") to (");
           comma = "";
           for (ServiceMethodParameter param : methodM7.getParameters()) {
               out.print (comma);
               comma = ", ";
               out.print (param.getType() +  " " + param.getName());
           }
           out.println (")");
        }
        return methodM7;
    }

    private boolean doAllParametersMatch(ServiceMethod methodM6, ServiceMethod methodM7) {
        if (methodM6.getParameters().size() != methodM7.getParameters().size()) {
            return false;
        }
        for (int i = 0; i < methodM6.getParameters().size(); i++) {
            ServiceMethodParameter paramM6 = methodM6.getParameters().get(i);
            ServiceMethodParameter paramM7 = methodM7.getParameters().get(i);
            if (!doParametersMatch(paramM6, paramM7)) {
                return false;
            }
        }
        return true;
    }

    private boolean doParametersMatch(ServiceMethodParameter paramM6, ServiceMethodParameter paramM7) {
        if (!paramM6.getName().equals(paramM7.getName())) {
            return false;
        }
        if (!paramM6.getType().equals(paramM7.getType())) {
            return false;
        }
        return true;
    }

    private ServiceMethod findMethod(ServiceMethod method1) {
        ServiceMethod method2 = findMethod2(method1.getService(), method1.getName());
        if (method2 == null) {
            String methodRename = knownMethodRenames.get(method1.getService() + "Service." + method1.getName());
            if (methodRename != null) {
                method2 = findMethod2(method1.getService(), methodRename);
                if (method2 == null) {
                    out.println("# (-) " + method1.getService() + "Service." + method1.getName()
                            + " could not be found even after being renamed to " + methodRename);
                    return null;
                }
            }
        }
        return method2;
    }

    private ServiceMethod findMethod2(String serviceKey, String methodName) {
        ServiceMethod method2 = finderM7.findServiceMethod(serviceKey, methodName);
        if (method2 == null) {
            if (serviceKey.equals("Lu")) {
                method2 = finderM7.findServiceMethod("Clu", methodName);
                if (method2 == null) {
                    method2 = finderM7.findServiceMethod("Lui", methodName);
                }
            }
        }
        return method2;
    }

    private String calcMethods(ServiceMethod method1) {
        StringBuilder bldr = new StringBuilder();
        String comma = "";
        for (ServiceMethod method2 : finderM7.findServiceMethods(method1.getService())) {
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
//        if (method1.getService().equals("Lu")) {
//            wideNet = finderM7.findServiceMethods("Clu");
//            wideNet.addAll(finderM7.findServiceMethods("Lui"));
//        } else {
        wideNet = finderM7.findServiceMethods(method1.getService());
//        }
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
}
