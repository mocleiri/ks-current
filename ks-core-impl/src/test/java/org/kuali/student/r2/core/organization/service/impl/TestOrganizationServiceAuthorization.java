package org.kuali.student.r2.core.organization.service.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.PredicateFactory;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.student.r2.common.datadictionary.DataDictionaryValidator;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.exceptions.AlreadyExistsException;
import org.kuali.student.r2.common.exceptions.DataValidationErrorException;
import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.exceptions.PermissionDeniedException;
import org.kuali.student.r2.common.exceptions.ReadOnlyException;
import org.kuali.student.r2.common.exceptions.VersionMismatchException;
import org.kuali.student.r2.core.constants.OrganizationServiceConstants;
import org.kuali.student.r2.core.organization.dto.OrgHierarchyInfo;
import org.kuali.student.r2.core.organization.dto.OrgInfo;
import org.kuali.student.r2.core.organization.dto.OrgOrgRelationInfo;
import org.kuali.student.r2.core.organization.dto.OrgPersonRelationInfo;
import org.kuali.student.r2.core.organization.dto.OrgPositionRestrictionInfo;
import org.kuali.student.r2.core.organization.service.OrgPermissionDataLoader;
import org.kuali.student.r2.core.organization.service.OrganizationService;
import org.kuali.student.r2.core.organization.service.OrganizationServiceDataLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:org-test-with-mock-context.xml"})
public class TestOrganizationServiceAuthorization {

    @Resource(name = "organizationDataLoader")
    private OrganizationServiceDataLoader dataLoader;

    @Resource(name = "organizationPermissionLoader")
    private OrgPermissionDataLoader authDataLoader;

    @Resource(name = "orgAuthorizationService")
    private OrganizationService testService;

    private ContextInfo adminContext;
    private ContextInfo fredContext;
    private ContextInfo ednaContext;


    @Before
    public void setup() throws Exception {
        dataLoader.beforeTest();
        authDataLoader.beforeTest();

        adminContext = new ContextInfo();
        adminContext.setPrincipalId(OrgPermissionDataLoader.USER_ADMIN);

        fredContext = new ContextInfo();
        fredContext.setPrincipalId(OrgPermissionDataLoader.USER_FRED);

        ednaContext = new ContextInfo();
        ednaContext.setPrincipalId(OrgPermissionDataLoader.USER_EDNA);
    }

    @After
    public void tearDown() throws Exception {
        dataLoader.afterTest();
        authDataLoader.afterTest();
    }


    @Test
    public void testReadOrg() throws MissingParameterException, InvalidParameterException, OperationFailedException, PermissionDeniedException, DoesNotExistException {
        try {
            testService.getOrgIdsByType(OrganizationServiceConstants.ORGANIZATION_DEPARTMENT_TYPE_KEY, ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }
        testService.getOrgIdsByType(OrganizationServiceConstants.ORGANIZATION_DEPARTMENT_TYPE_KEY, fredContext);
        List<String> ids = testService.getOrgIdsByType(OrganizationServiceConstants.ORGANIZATION_DEPARTMENT_TYPE_KEY, adminContext);


        try {
            testService.getOrg(ids.get(0), ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }
        testService.getOrg(ids.get(0), fredContext);
        OrgInfo info = testService.getOrg(ids.get(0), adminContext);

        try {
            testService.getOrgsByIds(ids, ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }
        testService.getOrgsByIds(ids, fredContext);
        List<OrgInfo> infos = testService.getOrgsByIds(ids, adminContext);


        try {
            testService.validateOrg(DataDictionaryValidator.ValidationType.FULL_VALIDATION.toString(), info.getTypeKey(), info, ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        try {
            testService.validateOrg(DataDictionaryValidator.ValidationType.FULL_VALIDATION.toString(), info.getTypeKey(), info, fredContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }
        testService.validateOrg(DataDictionaryValidator.ValidationType.FULL_VALIDATION.toString(), info.getTypeKey(), info, adminContext);

        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        Predicate[] predicates = {PredicateFactory.like("shortName", "TestOrgShortName%")};
        builder.setPredicates(predicates);
        QueryByCriteria criteria = builder.build();

        try {
            testService.searchForOrgIds(criteria, ednaContext);
        } catch (PermissionDeniedException e) {
        }
        testService.searchForOrgIds(criteria, fredContext);
        ids = testService.searchForOrgIds(criteria, adminContext);
    }

    @Test
    public void testCreateAndUpdateOrg() throws ParseException, DoesNotExistException, ReadOnlyException, OperationFailedException, InvalidParameterException, MissingParameterException, DataValidationErrorException, PermissionDeniedException, VersionMismatchException {
        OrgInfo info = dataLoader.generateOrg();

        try {
            testService.createOrg(info.getTypeKey(), info, ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        try {
            testService.createOrg(info.getTypeKey(), info, fredContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        info = testService.createOrg(info.getTypeKey(), info, adminContext);

        info.setShortName("The short name that is not really all that short when you take into account that it has been set to the contents of this sentence.");

        try {
            testService.updateOrg(info.getId(), info, ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        try {
            testService.updateOrg(info.getId(), info, fredContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        info = testService.updateOrg(info.getId(), info, adminContext);
    }

    @Test
    public void testDeleteOrg() throws MissingParameterException, InvalidParameterException, OperationFailedException, PermissionDeniedException, DoesNotExistException {
        List<String> ids = testService.getOrgIdsByType(OrganizationServiceConstants.ORGANIZATION_DEPARTMENT_TYPE_KEY, adminContext);
        String id = ids.get(0);

        try {
            testService.deleteOrg(id, ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        try {
            testService.deleteOrg(id, fredContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        testService.deleteOrg(id, adminContext);
    }

    @Test
    public void testReadOrgHierarchy() throws MissingParameterException, InvalidParameterException, OperationFailedException, PermissionDeniedException, DoesNotExistException {
        try {
            testService.getOrgHierarchyIdsByType(OrganizationServiceConstants.ORGANIZATION_CAMPUS_TYPE_KEY, ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }
        testService.getOrgHierarchyIdsByType(OrganizationServiceConstants.ORGANIZATION_CAMPUS_TYPE_KEY, fredContext);
        List<String> ids = testService.getOrgHierarchyIdsByType(OrganizationServiceConstants.ORGANIZATION_CAMPUS_TYPE_KEY, adminContext);

        try {
            testService.getOrgHierarchy(ids.get(0), ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }
        testService.getOrgHierarchy(ids.get(0), fredContext);
        OrgHierarchyInfo info = testService.getOrgHierarchy(ids.get(0), adminContext);

        try {
            testService.getOrgHierarchiesByIds(ids, ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }
        testService.getOrgHierarchiesByIds(ids, fredContext);
        List<OrgHierarchyInfo> infos = testService.getOrgHierarchiesByIds(ids, adminContext);

        try {
            testService.getOrgHierarchies(ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        testService.getOrgHierarchies(fredContext);
        infos = testService.getOrgHierarchies(adminContext);

        try {
            testService.validateOrgHierarchy(DataDictionaryValidator.ValidationType.FULL_VALIDATION.toString(), info.getTypeKey(), info, ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        try {
            testService.validateOrgHierarchy(DataDictionaryValidator.ValidationType.FULL_VALIDATION.toString(), info.getTypeKey(), info, fredContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }
        testService.validateOrgHierarchy(DataDictionaryValidator.ValidationType.FULL_VALIDATION.toString(), info.getTypeKey(), info, adminContext);

        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        Predicate[] predicates = {PredicateFactory.like("name", "Hierarchy%")};
        builder.setPredicates(predicates);
        QueryByCriteria criteria = builder.build();

        try {
            testService.searchForOrgHierarchyIds(criteria, ednaContext);
        } catch (PermissionDeniedException e) {
        }
        testService.searchForOrgHierarchyIds(criteria, fredContext);
        ids = testService.searchForOrgHierarchyIds(criteria, adminContext);

        try {
            testService.searchForOrgHierarchies(criteria, ednaContext);
        } catch (PermissionDeniedException e) {
        }
        testService.searchForOrgHierarchies(criteria, fredContext);
        infos = testService.searchForOrgHierarchies(criteria, adminContext);
    }

    @Test
    public void testCreateAndUpdateOrgHierarchy() throws ParseException, DoesNotExistException, ReadOnlyException, OperationFailedException, InvalidParameterException, MissingParameterException, DataValidationErrorException, PermissionDeniedException, VersionMismatchException {
        OrgHierarchyInfo info = dataLoader.generateOrgHierarchy();

        try {
            testService.createOrgHierarchy(info.getTypeKey(), info, ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        try {
            testService.createOrgHierarchy(info.getTypeKey(), info, fredContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        info = testService.createOrgHierarchy(info.getTypeKey(), info, adminContext);

        info.setName("The updated Name!");

        try {
            testService.updateOrgHierarchy(info.getId(), info, ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        try {
            testService.updateOrgHierarchy(info.getId(), info, fredContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        info = testService.updateOrgHierarchy(info.getId(), info, adminContext);
    }

    @Test
    public void testDeleteOrgHierarchy() throws MissingParameterException, InvalidParameterException, OperationFailedException, PermissionDeniedException, DoesNotExistException {
        List<String> ids = testService.getOrgHierarchyIdsByType(OrganizationServiceConstants.ORGANIZATION_CAMPUS_TYPE_KEY, adminContext);
        String id = ids.get(0);

        try {
            testService.deleteOrgHierarchy(id, ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        try {
            testService.deleteOrgHierarchy(id, fredContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        testService.deleteOrgHierarchy(id, adminContext);
    }

    @Test
    public void testReadOrgOrgRelation() throws MissingParameterException, InvalidParameterException, OperationFailedException, PermissionDeniedException, DoesNotExistException {
        try {
            testService.getOrgOrgRelationIdsByType(OrganizationServiceDataLoader.ORG_RELATION_ACAD_PARENT_TYPE, ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }
        testService.getOrgOrgRelationIdsByType(OrganizationServiceDataLoader.ORG_RELATION_ACAD_PARENT_TYPE, fredContext);
        List<String> ids = testService.getOrgOrgRelationIdsByType(OrganizationServiceDataLoader.ORG_RELATION_ACAD_PARENT_TYPE, adminContext);

        try {
            testService.getOrgOrgRelation(ids.get(0), ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }
        testService.getOrgOrgRelation(ids.get(0), fredContext);
        OrgOrgRelationInfo info = testService.getOrgOrgRelation(ids.get(0), adminContext);

        try {
            testService.getOrgOrgRelationsByIds(ids, ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }
        testService.getOrgOrgRelationsByIds(ids, fredContext);
        List<OrgOrgRelationInfo> infos = testService.getOrgOrgRelationsByIds(ids, adminContext);

        try {
            testService.getOrgOrgRelationsByOrg(infos.get(0).getOrgId(), ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        testService.getOrgOrgRelationsByOrg(info.getOrgId(), fredContext);
        infos = testService.getOrgOrgRelationsByOrg(info.getOrgId(), adminContext);

        try {
            testService.getOrgOrgRelationsByTypeAndOrg(info.getOrgId(), info.getTypeKey(), ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        testService.getOrgOrgRelationsByTypeAndOrg(info.getOrgId(), info.getTypeKey(), fredContext);
        infos = testService.getOrgOrgRelationsByTypeAndOrg(info.getOrgId(), info.getTypeKey(), adminContext);

        try {
            testService.getOrgOrgRelationsByTypeAndRelatedOrg(info.getRelatedOrgId(), info.getTypeKey(), ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        testService.getOrgOrgRelationsByTypeAndRelatedOrg(info.getRelatedOrgId(), info.getTypeKey(), fredContext);
        infos = testService.getOrgOrgRelationsByTypeAndRelatedOrg(info.getRelatedOrgId(), info.getTypeKey(), adminContext);

        try {
            testService.validateOrgOrgRelation(DataDictionaryValidator.ValidationType.FULL_VALIDATION.toString(), info.getOrgId(), info.getRelatedOrgId(), info.getTypeKey(), info, ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        try {
            testService.validateOrgOrgRelation(DataDictionaryValidator.ValidationType.FULL_VALIDATION.toString(), info.getOrgId(), info.getRelatedOrgId(), info.getTypeKey(), info, fredContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }
        testService.validateOrgOrgRelation(DataDictionaryValidator.ValidationType.FULL_VALIDATION.toString(), info.getOrgId(), info.getRelatedOrgId(), info.getTypeKey(), info, adminContext);

        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        Predicate[] predicates = {PredicateFactory.equal("orgId", info.getOrgId())};
        builder.setPredicates(predicates);
        QueryByCriteria criteria = builder.build();

        try {
            testService.searchForOrgOrgRelationIds(criteria, ednaContext);
        } catch (PermissionDeniedException e) {
        }
        testService.searchForOrgOrgRelationIds(criteria, fredContext);
        ids = testService.searchForOrgOrgRelationIds(criteria, adminContext);

        try {
            testService.searchForOrgOrgRelations(criteria, ednaContext);
        } catch (PermissionDeniedException e) {
        }
        testService.searchForOrgOrgRelations(criteria, fredContext);
        infos = testService.searchForOrgOrgRelations(criteria, adminContext);
    }

    @Test
    public void testCreateAndUpdateOrgOrgRelation() throws ReadOnlyException, DataValidationErrorException, InvalidParameterException, OperationFailedException, MissingParameterException, DoesNotExistException, PermissionDeniedException, VersionMismatchException, ParseException {
        OrgOrgRelationInfo info = dataLoader.generateOrgOrgRelationInfo();

        try {
            testService.createOrgOrgRelation(info.getOrgId(), info.getRelatedOrgId(), info.getTypeKey(), info, ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        try {
            testService.createOrgOrgRelation(info.getOrgId(), info.getRelatedOrgId(), info.getTypeKey(), info, fredContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        info = testService.createOrgOrgRelation(info.getOrgId(), info.getRelatedOrgId(), info.getTypeKey(), info, adminContext);

        info.setStateKey(OrganizationServiceDataLoader.ACTIVE_STATE);

        try {
            testService.updateOrgOrgRelation(info.getId(), info, ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        try {
            testService.updateOrgOrgRelation(info.getId(), info, fredContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        info = testService.updateOrgOrgRelation(info.getId(), info, adminContext);
    }

    @Test
    public void testDeleteOrgOrgRelation() throws MissingParameterException, InvalidParameterException, OperationFailedException, PermissionDeniedException, DoesNotExistException {
        List<String> ids = testService.getOrgOrgRelationIdsByType(OrganizationServiceDataLoader.ORG_RELATION_ACAD_PARENT_TYPE, adminContext);
        String id = ids.get(0);

        try {
            testService.deleteOrgOrgRelation(id, ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        try {
            testService.deleteOrgOrgRelation(id, fredContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        testService.deleteOrgOrgRelation(id, adminContext);
    }

    @Test
    public void testReadOrgPersonRelation() throws MissingParameterException, InvalidParameterException, OperationFailedException, PermissionDeniedException, DoesNotExistException {
        try {
            testService.getOrgPersonRelationIdsByType(OrganizationServiceDataLoader.ORG_PERSON_RELATION_MEMBER_TYPE, ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }
        testService.getOrgPersonRelationIdsByType(OrganizationServiceDataLoader.ORG_PERSON_RELATION_MEMBER_TYPE, fredContext);
        List<String> ids = testService.getOrgPersonRelationIdsByType(OrganizationServiceDataLoader.ORG_PERSON_RELATION_MEMBER_TYPE, adminContext);

        try {
            testService.getOrgPersonRelation(ids.get(0), ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }
        testService.getOrgPersonRelation(ids.get(0), fredContext);
        OrgPersonRelationInfo info = testService.getOrgPersonRelation(ids.get(0), adminContext);

        try {
            testService.getOrgPersonRelationsByIds(ids, ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }
        testService.getOrgPersonRelationsByIds(ids, fredContext);
        List<OrgPersonRelationInfo> infos = testService.getOrgPersonRelationsByIds(ids, adminContext);

        try {
            testService.getOrgPersonRelationsByOrg(info.getOrgId(), ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        testService.getOrgPersonRelationsByOrg(info.getOrgId(), fredContext);
        infos = testService.getOrgPersonRelationsByOrg(info.getOrgId(), adminContext);

        try {
            testService.getOrgPersonRelationsByTypeAndPerson(info.getTypeKey(), info.getPersonId(), ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        testService.getOrgPersonRelationsByTypeAndPerson(info.getTypeKey(), info.getPersonId(), fredContext);
        infos = testService.getOrgPersonRelationsByTypeAndPerson(info.getTypeKey(), info.getPersonId(), adminContext);

        try {
            testService.getOrgPersonRelationsByOrgAndPerson(info.getOrgId(), info.getPersonId(), ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        testService.getOrgPersonRelationsByOrgAndPerson(info.getOrgId(), info.getPersonId(), fredContext);
        infos = testService.getOrgPersonRelationsByOrgAndPerson(info.getOrgId(), info.getPersonId(), adminContext);

        try {
            testService.getOrgPersonRelationsByPerson(info.getPersonId(), ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        testService.getOrgPersonRelationsByPerson(info.getPersonId(), fredContext);
        infos = testService.getOrgPersonRelationsByPerson(info.getPersonId(), adminContext);

        try {
            testService.getOrgPersonRelationsByTypeAndOrg(info.getTypeKey(), info.getOrgId(), ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        testService.getOrgPersonRelationsByTypeAndOrg(info.getTypeKey(), info.getOrgId(), fredContext);
        infos = testService.getOrgPersonRelationsByTypeAndOrg(info.getTypeKey(), info.getOrgId(), adminContext);

        try {
            testService.getOrgPersonRelationsByTypeAndOrgAndPerson(info.getTypeKey(), info.getOrgId(), info.getPersonId(), ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        testService.getOrgPersonRelationsByTypeAndOrgAndPerson(info.getTypeKey(), info.getOrgId(), info.getPersonId(), fredContext);
        infos = testService.getOrgPersonRelationsByTypeAndOrgAndPerson(info.getTypeKey(), info.getOrgId(), info.getPersonId(), adminContext);

        try {
            testService.validateOrgPersonRelation(DataDictionaryValidator.ValidationType.FULL_VALIDATION.toString(), info.getOrgId(), info.getPersonId(), info.getTypeKey(), info, ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        try {
            testService.validateOrgPersonRelation(DataDictionaryValidator.ValidationType.FULL_VALIDATION.toString(), info.getOrgId(), info.getPersonId(), info.getTypeKey(), info, fredContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }
        testService.validateOrgPersonRelation(DataDictionaryValidator.ValidationType.FULL_VALIDATION.toString(), info.getOrgId(), info.getPersonId(), info.getTypeKey(), info, adminContext);

        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        Predicate[] predicates = {PredicateFactory.like("orgId", "1")};
        builder.setPredicates(predicates);
        QueryByCriteria criteria = builder.build();

        try {
            testService.searchForOrgPersonRelations(criteria, ednaContext);
        } catch (PermissionDeniedException e) {
        }
        testService.searchForOrgPersonRelations(criteria, fredContext);
        infos = testService.searchForOrgPersonRelations(criteria, adminContext);

        try {
            testService.searchForOrgPersonRelationIds(criteria, ednaContext);
        } catch (PermissionDeniedException e) {
        }
        testService.searchForOrgPersonRelationIds(criteria, fredContext);
        ids = testService.searchForOrgPersonRelationIds(criteria, adminContext);
    }

    @Test
    public void testCreateAndUpdateOrgPersonRelation() throws ParseException, ReadOnlyException, DataValidationErrorException, InvalidParameterException, OperationFailedException, MissingParameterException, DoesNotExistException, PermissionDeniedException, VersionMismatchException {
        OrgPersonRelationInfo info = dataLoader.generateOrgPersonRelation();

        try {
            testService.createOrgPersonRelation(info.getOrgId(), info.getPersonId(), info.getTypeKey(), info, ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        try {
            testService.createOrgPersonRelation(info.getOrgId(), info.getPersonId(), info.getTypeKey(), info, fredContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        info = testService.createOrgPersonRelation(info.getOrgId(), info.getPersonId(), info.getTypeKey(), info, adminContext);

        info.setStateKey(OrganizationServiceDataLoader.ACTIVE_STATE);

        try {
            testService.updateOrgPersonRelation(info.getId(), info, ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        try {
            testService.updateOrgPersonRelation(info.getId(), info, fredContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        info = testService.updateOrgPersonRelation(info.getId(), info, adminContext);
    }

    @Test
    public void testDeleteOrgPersonRelation() throws MissingParameterException, InvalidParameterException, OperationFailedException, PermissionDeniedException, DoesNotExistException {
        List<String> ids = testService.getOrgPersonRelationIdsByType(OrganizationServiceDataLoader.ORG_PERSON_RELATION_MEMBER_TYPE, adminContext);
        String id = ids.get(0);

        try {
            testService.deleteOrgPersonRelation(id, ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        try {
            testService.deleteOrgPersonRelation(id, fredContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        testService.deleteOrgPersonRelation(id, adminContext);
    }

    @Test
    public void testReadOrgPositionRestriction() throws MissingParameterException, InvalidParameterException, OperationFailedException, PermissionDeniedException, DoesNotExistException {
        try {
            testService.getOrgPositionRestrictionIdsByType(OrganizationServiceDataLoader.ORG_PERSON_RELATION_PRESIDENT_TYPE, ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }
        testService.getOrgPositionRestrictionIdsByType(OrganizationServiceDataLoader.ORG_PERSON_RELATION_PRESIDENT_TYPE, fredContext);
        List<String> ids = testService.getOrgPositionRestrictionIdsByType(OrganizationServiceDataLoader.ORG_PERSON_RELATION_PRESIDENT_TYPE, adminContext);

        try {
            testService.getOrgPositionRestriction(ids.get(0), ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }
        testService.getOrgPositionRestriction(ids.get(0), fredContext);
        OrgPositionRestrictionInfo info = testService.getOrgPositionRestriction(ids.get(0), adminContext);

        try {
            testService.getOrgPositionRestrictionsByIds(ids, ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }
        testService.getOrgPositionRestrictionsByIds(ids, fredContext);
        List<OrgPositionRestrictionInfo> infos = testService.getOrgPositionRestrictionsByIds(ids, adminContext);

        try {
            testService.getOrgPositionRestrictionIdsByOrg(info.getOrgId(), ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        testService.getOrgPositionRestrictionIdsByOrg(info.getOrgId(), fredContext);
        ids = testService.getOrgPositionRestrictionIdsByOrg(info.getOrgId(), adminContext);

        try {
            testService.validateOrgPositionRestriction(DataDictionaryValidator.ValidationType.FULL_VALIDATION.toString(), info.getOrgId(), info.getOrgPersonRelationTypeKey(), info, ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        try {
            testService.validateOrgPositionRestriction(DataDictionaryValidator.ValidationType.FULL_VALIDATION.toString(), info.getOrgId(), info.getOrgPersonRelationTypeKey(), info, fredContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }
        testService.validateOrgPositionRestriction(DataDictionaryValidator.ValidationType.FULL_VALIDATION.toString(), info.getOrgId(), info.getOrgPersonRelationTypeKey(), info, adminContext);

        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        Predicate[] predicates = {PredicateFactory.like("orgId", "1")};
        builder.setPredicates(predicates);
        QueryByCriteria criteria = builder.build();

        try {
            testService.searchForOrgPositionRestrictionIds(criteria, ednaContext);
        } catch (PermissionDeniedException e) {
        }
        testService.searchForOrgPositionRestrictionIds(criteria, fredContext);
        ids = testService.searchForOrgPositionRestrictionIds(criteria, adminContext);

        try {
            testService.searchForOrgPositionRestrictions(criteria, ednaContext);
        } catch (PermissionDeniedException e) {
        }
        testService.searchForOrgPositionRestrictions(criteria, fredContext);
        infos = testService.searchForOrgPositionRestrictions(criteria, adminContext);
    }

    @Test
    public void testCreateAndUpdateOrgPositionRestriction() throws DoesNotExistException, ReadOnlyException, OperationFailedException, InvalidParameterException, AlreadyExistsException, MissingParameterException, DataValidationErrorException, PermissionDeniedException, VersionMismatchException {
        OrgPositionRestrictionInfo info = dataLoader.generateOrgPositionRestriction();

        try {
            testService.createOrgPositionRestriction(info.getOrgId(), info.getOrgPersonRelationTypeKey(), info, ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        try {
            testService.createOrgPositionRestriction(info.getOrgId(), info.getOrgPersonRelationTypeKey(), info, fredContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        info = testService.createOrgPositionRestriction(info.getOrgId(), info.getOrgPersonRelationTypeKey(), info, adminContext);

        info.setMaxNumRelations(Integer.MAX_VALUE);

        try {
            testService.updateOrgPositionRestriction(info.getId(), info, ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        try {
            testService.updateOrgPositionRestriction(info.getId(), info, fredContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        info = testService.updateOrgPositionRestriction(info.getId(), info, adminContext);
    }

    @Test
    public void testDeleteOrgPositionRestriction() throws MissingParameterException, InvalidParameterException, OperationFailedException, PermissionDeniedException, DoesNotExistException {
        List<String> ids = testService.getOrgPositionRestrictionIdsByType(OrganizationServiceDataLoader.ORG_PERSON_RELATION_PRESIDENT_TYPE, adminContext);
        String id = ids.get(0);

        try {
            testService.deleteOrgPositionRestriction(id, ednaContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        try {
            testService.deleteOrgPositionRestriction(id, fredContext);
            fail("PermissionDeniedException should have been thrown!");
        } catch (PermissionDeniedException e) {
        }

        testService.deleteOrgPositionRestriction(id, adminContext);
    }
}
