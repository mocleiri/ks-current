/*
 * Copyright 2013 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Lic+ense is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.student.r2.core.organization.service.impl;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.PredicateFactory;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.student.common.test.util.IdEntityTester;
import org.kuali.student.common.test.util.KeyEntityTester;
import org.kuali.student.common.test.util.RichTextTester;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.dto.IdEntityInfo;
import org.kuali.student.r2.common.dto.StatusInfo;
import org.kuali.student.r2.common.dto.TimeAmountInfo;
import org.kuali.student.r2.common.dto.TypeStateEntityInfo;
import org.kuali.student.r2.common.exceptions.DataValidationErrorException;
import org.kuali.student.r2.common.exceptions.DependentObjectsExistException;
import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.exceptions.PermissionDeniedException;
import org.kuali.student.r2.common.exceptions.ReadOnlyException;
import org.kuali.student.r2.common.exceptions.VersionMismatchException;
import org.kuali.student.r2.common.util.RichTextHelper;
import org.kuali.student.r2.core.constants.AtpServiceConstants;
import org.kuali.student.r2.core.organization.dto.OrgCodeInfo;
import org.kuali.student.r2.core.organization.dto.OrgHierarchyInfo;
import org.kuali.student.r2.core.organization.dto.OrgInfo;
import org.kuali.student.r2.core.organization.dto.OrgOrgRelationInfo;
import org.kuali.student.r2.core.organization.dto.OrgPersonRelationInfo;
import org.kuali.student.r2.core.organization.dto.OrgPositionRestrictionInfo;
import org.kuali.student.r2.core.organization.dto.OrgTreeViewInfo;
import org.kuali.student.r2.core.organization.infc.OrgHierarchy;
import org.kuali.student.r2.core.organization.service.OrganizationService;
import org.kuali.student.r2.core.organization.service.OrganizationServiceDataLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@Transactional
public abstract class TestOrganizationServiceImplConformanceExtendedCrud extends TestOrganizationServiceImplConformanceBaseCrud
{

    @Resource(name = "organizationDataLoader")
    protected OrganizationServiceDataLoader dataLoader;


    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    @Before
    public void setUpExtended() throws Exception {
        dataLoader.beforeTest();
    }

    @After
    public void tearDownExtended() throws Exception {
        dataLoader.afterTest();
    }

	// ========================================
	// DTO FIELD SPECIFIC METHODS
	// ========================================
	
	// ****************************************************
	//           OrgHierarchyInfo
	// ****************************************************
	
	/*
		A method to set the fields for a OrgHierarchy in a 'test create' section prior to calling the 'create' operation.
	*/
	public void testCrudOrgHierarchy_setDTOFieldsForTestCreate(OrgHierarchyInfo expected) {
		expected.setTypeKey("typeKey01");
		expected.setStateKey("stateKey01");
		expected.setName("name01");
        expected.setDescr(RichTextHelper.buildRichTextInfo("plain orgHierarchy descr", "formatted orgHierarchy descr"));
		expected.setRootOrgId("1");
        List<String> oorTypes = new ArrayList<String>();
        oorTypes.add(OrganizationServiceDataLoader.ORG_RELATION_ACAD_COLLABORATES_WITH_TYPE);
        oorTypes.add(OrganizationServiceDataLoader.ORG_RELATION_ACAD_PARENT_TYPE);
        expected.setOrgOrgRelationTypes(oorTypes);
        try {
            expected.setEffectiveDate(dateFormat.parse("20090101"));
            expected.setExpirationDate(dateFormat.parse("21001231"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
	}
	
	/*
		A method to test the fields for a OrgHierarchy. This is called after:
		- creating a DTO, where actual is the DTO returned by the create operation, and expected is the dto passed in to the create operation
		- reading a DTO after creating it, and actual is the read DTO, and expected is the dto that was created
		- updating a DTO, where actual is DTO returned by the update operation, and expected is the dto that was passed in to the update operation
	*/
	public void testCrudOrgHierarchy_testDTOFieldsForTestCreateUpdate(OrgHierarchyInfo expected, OrgHierarchyInfo actual) 
	{
		assertEquals (expected.getTypeKey(), actual.getTypeKey());
		assertEquals (expected.getStateKey(), actual.getStateKey());
		assertEquals (expected.getName(), actual.getName());
        new RichTextTester().check(expected.getDescr(), actual.getDescr());
		assertEquals(expected.getRootOrgId(), actual.getRootOrgId());
        assertEquals(expected.getOrgOrgRelationTypes().size(), actual.getOrgOrgRelationTypes().size());
        for(String oorType : expected.getOrgOrgRelationTypes()) {
            assertTrue(actual.getOrgOrgRelationTypes().contains(oorType));
        }
        assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
        assertEquals(expected.getExpirationDate(), actual.getExpirationDate());
	}
	
	/*
		A method to set the fields for a OrgHierarchy in a 'test update' section prior to calling the 'update' operation.
	*/
	public void testCrudOrgHierarchy_setDTOFieldsForTestUpdate(OrgHierarchyInfo expected) 
	{
		expected.setStateKey("stateKey_Updated");
		expected.setName("name_Updated");
        expected.setDescr(RichTextHelper.buildRichTextInfo("plain orgHierarchy updated descr", "formatted orgHierarchy updated descr"));
		expected.setRootOrgId("2");
        List<String> oorTypes = new ArrayList<String>();
        oorTypes.add(OrganizationServiceDataLoader.ORG_RELATION_FIN_PARENT_TYPE);
        expected.setOrgOrgRelationTypes(oorTypes);
        try {
            expected.setEffectiveDate(dateFormat.parse("20100101"));
            expected.setExpirationDate(dateFormat.parse("22011231"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
	}
	
	/*
		A method to test the fields for a OrgHierarchy after an update operation, followed by a read operation,
		where actual is the DTO returned by the read operation, and expected is the dto returned by the update operation.
	*/
	public void testCrudOrgHierarchy_testDTOFieldsForTestReadAfterUpdate(OrgHierarchyInfo expected, OrgHierarchyInfo actual) 
	{
		assertEquals (expected.getId(), actual.getId());
        assertEquals (expected.getTypeKey(), actual.getTypeKey());
        assertEquals (expected.getStateKey(), actual.getStateKey());
        assertEquals (expected.getName(), actual.getName());
        new RichTextTester().check(expected.getDescr(), actual.getDescr());
        assertEquals (expected.getRootOrgId(), actual.getRootOrgId());
        assertEquals(expected.getOrgOrgRelationTypes().size(), actual.getOrgOrgRelationTypes().size());
        for(String oorType : expected.getOrgOrgRelationTypes()) {
            assertTrue(actual.getOrgOrgRelationTypes().contains(oorType));
        }
        assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
        assertEquals(expected.getExpirationDate(), actual.getExpirationDate());
	}
	
	/*
		A method to set the fields for a OrgHierarchy in the 'test read after update' section.
		This dto is another (second) dto object being created for other tests.
	*/
	public void testCrudOrgHierarchy_setDTOFieldsForTestReadAfterUpdate(OrgHierarchyInfo expected) 
	{
        expected.setName("name_Updated");
        expected.setDescr(RichTextHelper.buildRichTextInfo("plain OH updated descr", "formatted OH updated descr"));
        expected.setRootOrgId("2");
        List<String> oorTypes = new ArrayList<String>();
        oorTypes.add(OrganizationServiceDataLoader.ORG_RELATION_FIN_PARENT_TYPE);
        expected.setOrgOrgRelationTypes(oorTypes);
        try {
            expected.setEffectiveDate(dateFormat.parse("20100101"));
            expected.setExpirationDate(dateFormat.parse("22011231"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
	}
	
	
	// ****************************************************
	//           OrgInfo
	// ****************************************************
	
	/*
		A method to set the fields for a Org in a 'test create' section prior to calling the 'create' operation.
	*/
	public void testCrudOrg_setDTOFieldsForTestCreate(OrgInfo expected) 
	{
		expected.setTypeKey("typeKey01");
		expected.setStateKey("stateKey01");
		expected.setLongName("longName01");
        expected.setLongDescr(RichTextHelper.buildRichTextInfo("plain LONG O descr", "formatted LONG O descr"));
		expected.setShortName("shortName01");
		expected.setSortName("sortName01");
        expected.setShortDescr(RichTextHelper.buildRichTextInfo("plain SHORT O descr", "formatted SHORT O descr"));
		List<OrgCodeInfo> codes = new ArrayList<OrgCodeInfo>();
        OrgCodeInfo code = new OrgCodeInfo();
        code.setValue("code1");
        code.setTypeKey("key1");
        code.setStateKey("state1");
        codes.add(code);
        code = new OrgCodeInfo();
        code.setValue("code2");
        code.setTypeKey("key2");
        code.setStateKey("state2");
        codes.add(code);
		expected.setOrgCodes(codes);

        try {
            expected.setEffectiveDate(dateFormat.parse("20090101"));
            expected.setExpirationDate(dateFormat.parse("21001231"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
	}
	
	/*
		A method to test the fields for a Org. This is called after:
		- creating a DTO, where actual is the DTO returned by the create operation, and expected is the dto passed in to the create operation
		- reading a DTO after creating it, and actual is the read DTO, and expected is the dto that was created
		- updating a DTO, where actual is DTO returned by the update operation, and expected is the dto that was passed in to the update operation
	*/
	public void testCrudOrg_testDTOFieldsForTestCreateUpdate(OrgInfo expected, OrgInfo actual) 
	{
		assertEquals (expected.getTypeKey(), actual.getTypeKey());
		assertEquals (expected.getStateKey(), actual.getStateKey());
		assertEquals (expected.getLongName(), actual.getLongName());
        new RichTextTester().check(expected.getLongDescr(), actual.getLongDescr());
		assertEquals (expected.getShortName(), actual.getShortName());
		assertEquals (expected.getSortName(), actual.getSortName());
        new RichTextTester().check(expected.getShortDescr(), actual.getShortDescr());

        assertEquals(expected.getOrgCodes().size(), actual.getOrgCodes().size());
        Map<String, OrgCodeInfo> map = generateOrgCodeInfoMap(actual.getOrgCodes());
        for(OrgCodeInfo info : expected.getOrgCodes()) {
            OrgCodeInfo info2 = map.get(info.getValue());
            assertNotNull(info2);
            assertEquals(info.getTypeKey(), info2.getTypeKey());
            assertEquals(info.getStateKey(), info2.getStateKey());
            assertEquals(info.getValue(), info2.getValue());
        }
        assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
        assertEquals(expected.getExpirationDate(), actual.getExpirationDate());
	}

    private Map<String, OrgCodeInfo> generateOrgCodeInfoMap(List<OrgCodeInfo> list) {
        Map<String, OrgCodeInfo> map = new HashMap<String, OrgCodeInfo>();
        for(OrgCodeInfo info : list) {
            map.put(info.getValue(), info);
        }
        return map;
    }
	
	/*
		A method to set the fields for a Org in a 'test update' section prior to calling the 'update' operation.
	*/
	public void testCrudOrg_setDTOFieldsForTestUpdate(OrgInfo expected) 
	{
		expected.setStateKey("stateKey_Updated");
		expected.setLongName("longName_Updated");
        expected.setLongDescr(RichTextHelper.buildRichTextInfo("plain LONG O updated descr", "formatted LONG O updated descr"));
		expected.setShortName("shortName_Updated");
		expected.setSortName("sortName_Updated");
        expected.setShortDescr(RichTextHelper.buildRichTextInfo("plain SHORT O updated descr", "formatted SHORT O updated descr"));
        List<OrgCodeInfo> codes = new ArrayList<OrgCodeInfo>();
        OrgCodeInfo code = new OrgCodeInfo();
        code.setId("updated1");
        code.setValue("updatedCode1");
        code.setStateKey("updatedState1");
        code.setTypeKey("updatedKey1");
        codes.add(code);
        code = new OrgCodeInfo();
        code.setId("updated2");
        code.setValue("updatedCode2");
        code.setStateKey("updatedState2");
        code.setTypeKey("updatedKey2");
        codes.add(code);
        expected.setOrgCodes(codes);

        try {
            expected.setEffectiveDate(dateFormat.parse("20100101"));
            expected.setExpirationDate(dateFormat.parse("22001231"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
	}
	
	/*
		A method to test the fields for a Org after an update operation, followed by a read operation,
		where actual is the DTO returned by the read operation, and expected is the dto returned by the update operation.
	*/
	public void testCrudOrg_testDTOFieldsForTestReadAfterUpdate(OrgInfo expected, OrgInfo actual) 
	{
		assertEquals (expected.getId(), actual.getId());
		assertEquals (expected.getTypeKey(), actual.getTypeKey());
		assertEquals (expected.getStateKey(), actual.getStateKey());
		assertEquals (expected.getLongName(), actual.getLongName());
        new RichTextTester().check(expected.getLongDescr(), actual.getLongDescr());
        assertEquals (expected.getShortName(), actual.getShortName());
        assertEquals (expected.getSortName(), actual.getSortName());
        new RichTextTester().check(expected.getShortDescr(), actual.getShortDescr());

        assertEquals(expected.getOrgCodes().size(), actual.getOrgCodes().size());
        Map<String, OrgCodeInfo> map = generateOrgCodeInfoMap(actual.getOrgCodes());
        for(OrgCodeInfo info : expected.getOrgCodes()) {
            OrgCodeInfo info2 = map.get(info.getValue());
            assertNotNull(info2);
            assertEquals(info.getTypeKey(), info2.getTypeKey());
            assertEquals(info.getStateKey(), info2.getStateKey());
            assertEquals(info.getValue(), info2.getValue());
        }
        assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
        assertEquals(expected.getExpirationDate(), actual.getExpirationDate());
	}
	
	/*
		A method to set the fields for a Org in the 'test read after update' section.
		This dto is another (second) dto object being created for other tests.
	*/
	public void testCrudOrg_setDTOFieldsForTestReadAfterUpdate(OrgInfo expected) 
	{
		expected.setLongName("longName_Updated");
        expected.setLongDescr(RichTextHelper.buildRichTextInfo("plain LONG O updated descr", "formatted LONG O updated descr"));
        expected.setShortName("shortName_Updated");
        expected.setSortName("sortName_Updated");
        expected.setShortDescr(RichTextHelper.buildRichTextInfo("plain SHORT O updated descr", "formatted SHORT O updated descr"));
        List<OrgCodeInfo> codes = new ArrayList<OrgCodeInfo>();
        OrgCodeInfo code = new OrgCodeInfo();
        code.setValue("updatedCode1");
        code.setTypeKey("updatedType1");
        code.setStateKey("updatedState1");
        codes.add(code);
        code = new OrgCodeInfo();
        code.setValue("updatedCode2");
        code.setTypeKey("updatedType2");
        code.setStateKey("updatedState2");
        codes.add(code);
        expected.setOrgCodes(codes);

        try {
            expected.setEffectiveDate(dateFormat.parse("20100101"));
            expected.setExpirationDate(dateFormat.parse("22001231"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
	}
	
	
	// ****************************************************
	//           OrgOrgRelationInfo
	// ****************************************************
	
	/*
		A method to set the fields for a OrgOrgRelation in a 'test create' section prior to calling the 'create' operation.
	*/
	public void testCrudOrgOrgRelation_setDTOFieldsForTestCreate(OrgOrgRelationInfo expected) 
	{
		expected.setTypeKey("typeKey01");
		expected.setStateKey("stateKey01");
		expected.setOrgId("1");
		expected.setRelatedOrgId("2");

        try {
            expected.setEffectiveDate(dateFormat.parse("20090101"));
            expected.setExpirationDate(dateFormat.parse("21001231"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
	}
	
	/*
		A method to test the fields for a OrgOrgRelation. This is called after:
		- creating a DTO, where actual is the DTO returned by the create operation, and expected is the dto passed in to the create operation
		- reading a DTO after creating it, and actual is the read DTO, and expected is the dto that was created
		- updating a DTO, where actual is DTO returned by the update operation, and expected is the dto that was passed in to the update operation
	*/
	public void testCrudOrgOrgRelation_testDTOFieldsForTestCreateUpdate(OrgOrgRelationInfo expected, OrgOrgRelationInfo actual) 
	{
		assertEquals (expected.getTypeKey(), actual.getTypeKey());
		assertEquals (expected.getStateKey(), actual.getStateKey());
		assertEquals (expected.getOrgId(), actual.getOrgId());
		assertEquals (expected.getRelatedOrgId(), actual.getRelatedOrgId());
        assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
        assertEquals(expected.getExpirationDate(), actual.getExpirationDate());
	}
	
	/*
		A method to set the fields for a OrgOrgRelation in a 'test update' section prior to calling the 'update' operation.
	*/
	public void testCrudOrgOrgRelation_setDTOFieldsForTestUpdate(OrgOrgRelationInfo expected) 
	{
		expected.setStateKey("stateKey_Updated");
        try {
            expected.setEffectiveDate(dateFormat.parse("20100101"));
            expected.setExpirationDate(dateFormat.parse("22001231"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
	}
	
	/*
		A method to test the fields for a OrgOrgRelation after an update operation, followed by a read operation,
		where actual is the DTO returned by the read operation, and expected is the dto returned by the update operation.
	*/
	public void testCrudOrgOrgRelation_testDTOFieldsForTestReadAfterUpdate(OrgOrgRelationInfo expected, OrgOrgRelationInfo actual) 
	{
		assertEquals (expected.getId(), actual.getId());
		assertEquals (expected.getTypeKey(), actual.getTypeKey());
		assertEquals (expected.getStateKey(), actual.getStateKey());
		assertEquals (expected.getOrgId(), actual.getOrgId());
		assertEquals (expected.getRelatedOrgId(), actual.getRelatedOrgId());
        assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
        assertEquals(expected.getExpirationDate(), actual.getExpirationDate());
	}
	
	/*
		A method to set the fields for a OrgOrgRelation in the 'test read after update' section.
		This dto is another (second) dto object being created for other tests.
	*/
	public void testCrudOrgOrgRelation_setDTOFieldsForTestReadAfterUpdate(OrgOrgRelationInfo expected) 
	{
		expected.setOrgId("5");
		expected.setRelatedOrgId("6");
        try {
            expected.setEffectiveDate(dateFormat.parse("20110101"));
            expected.setExpirationDate(dateFormat.parse("22011231"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
	}
	
	
	// ****************************************************
	//           OrgPersonRelationInfo
	// ****************************************************
	
	/*
		A method to set the fields for a OrgPersonRelation in a 'test create' section prior to calling the 'create' operation.
	*/
	public void testCrudOrgPersonRelation_setDTOFieldsForTestCreate(OrgPersonRelationInfo expected) 
	{
		expected.setTypeKey("typeKey01");
		expected.setStateKey("stateKey01");
		expected.setOrgId("1");
		expected.setPersonId("personId01");

        try {
            expected.setEffectiveDate(dateFormat.parse("20090101"));
            expected.setExpirationDate(dateFormat.parse("21001231"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
	}
	
	/*
		A method to test the fields for a OrgPersonRelation. This is called after:
		- creating a DTO, where actual is the DTO returned by the create operation, and expected is the dto passed in to the create operation
		- reading a DTO after creating it, and actual is the read DTO, and expected is the dto that was created
		- updating a DTO, where actual is DTO returned by the update operation, and expected is the dto that was passed in to the update operation
	*/
	public void testCrudOrgPersonRelation_testDTOFieldsForTestCreateUpdate(OrgPersonRelationInfo expected, OrgPersonRelationInfo actual) 
	{
		assertEquals (expected.getTypeKey(), actual.getTypeKey());
		assertEquals (expected.getStateKey(), actual.getStateKey());
		assertEquals (expected.getOrgId(), actual.getOrgId());
		assertEquals (expected.getPersonId(), actual.getPersonId());
        assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
        assertEquals(expected.getExpirationDate(), actual.getExpirationDate());
	}
	
	/*
		A method to set the fields for a OrgPersonRelation in a 'test update' section prior to calling the 'update' operation.
	*/
	public void testCrudOrgPersonRelation_setDTOFieldsForTestUpdate(OrgPersonRelationInfo expected) 
	{
		expected.setStateKey("stateKey_Updated");

        try {
            expected.setEffectiveDate(dateFormat.parse("20110101"));
            expected.setExpirationDate(dateFormat.parse("21211231"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
	}
	
	/*
		A method to test the fields for a OrgPersonRelation after an update operation, followed by a read operation,
		where actual is the DTO returned by the read operation, and expected is the dto returned by the update operation.
	*/
	public void testCrudOrgPersonRelation_testDTOFieldsForTestReadAfterUpdate(OrgPersonRelationInfo expected, OrgPersonRelationInfo actual) 
	{
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getTypeKey(), actual.getTypeKey());
		assertEquals(expected.getStateKey(), actual.getStateKey());
		assertEquals(expected.getOrgId(), actual.getOrgId());
		assertEquals(expected.getPersonId(), actual.getPersonId());
        assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
        assertEquals(expected.getExpirationDate(), actual.getExpirationDate());
	}
	
	/*
		A method to set the fields for a OrgPersonRelation in the 'test read after update' section.
		This dto is another (second) dto object being created for other tests.
	*/
	public void testCrudOrgPersonRelation_setDTOFieldsForTestReadAfterUpdate(OrgPersonRelationInfo expected) 
	{
		expected.setOrgId("3");
		expected.setPersonId("personId_Updated");

        try {
            expected.setEffectiveDate(dateFormat.parse("20110101"));
            expected.setExpirationDate(dateFormat.parse("21211231"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
	}
	
	
	// ****************************************************
	//           OrgPositionRestrictionInfo
	// ****************************************************
	
	/*
		A method to set the fields for a OrgPositionRestriction in a 'test create' section prior to calling the 'create' operation.
	*/
	public void testCrudOrgPositionRestriction_setDTOFieldsForTestCreate(OrgPositionRestrictionInfo expected) 
	{
		expected.setOrgId("7");
		expected.setOrgPersonRelationTypeKey("orgPersonRelationTypeKey01");
		expected.setTitle("title01");
        expected.setDescr(RichTextHelper.buildRichTextInfo("plain opr descr", "formatted opr descr"));
        TimeAmountInfo info = new TimeAmountInfo();
        info.setAtpDurationTypeKey(AtpServiceConstants.DURATION_MONTH_TYPE_KEY);
        info.setTimeQuantity(5);
        expected.setStdDuration(info);
        expected.setMaxNumRelations(1000);
        expected.setMinNumRelations(30);
	}
	
	/*
		A method to test the fields for a OrgPositionRestriction. This is called after:
		- creating a DTO, where actual is the DTO returned by the create operation, and expected is the dto passed in to the create operation
		- reading a DTO after creating it, and actual is the read DTO, and expected is the dto that was created
		- updating a DTO, where actual is DTO returned by the update operation, and expected is the dto that was passed in to the update operation
	*/
	public void testCrudOrgPositionRestriction_testDTOFieldsForTestCreateUpdate(OrgPositionRestrictionInfo expected, OrgPositionRestrictionInfo actual) 
	{
		assertEquals (expected.getOrgId(), actual.getOrgId());
		assertEquals (expected.getOrgPersonRelationTypeKey(), actual.getOrgPersonRelationTypeKey());
		assertEquals (expected.getTitle(), actual.getTitle());
        new RichTextTester().check(expected.getDescr(), actual.getDescr());
        assertTrue((expected.getStdDuration() != null && actual.getStdDuration() != null) || (expected.getStdDuration() == null && actual.getStdDuration() == null));
        if(expected.getStdDuration() != null) {
            assertEquals(expected.getStdDuration().getAtpDurationTypeKey(), actual.getStdDuration().getAtpDurationTypeKey());
            assertEquals(actual.getStdDuration().getTimeQuantity(), actual.getStdDuration().getTimeQuantity());
        }
        assertEquals(expected.getMaxNumRelations(), actual.getMaxNumRelations());
        assertEquals(expected.getMinNumRelations(), actual.getMinNumRelations());
	}
	
	/*
		A method to set the fields for a OrgPositionRestriction in a 'test update' section prior to calling the 'update' operation.
	*/
	public void testCrudOrgPositionRestriction_setDTOFieldsForTestUpdate(OrgPositionRestrictionInfo expected) 
	{
		expected.setTitle("title_Updated");
        expected.setDescr(RichTextHelper.buildRichTextInfo("plain opr updated descr", "formatted opr updated descr"));
        TimeAmountInfo info = new TimeAmountInfo();
        info.setAtpDurationTypeKey(AtpServiceConstants.DURATION_WEEK_TYPE_KEY);
        info.setTimeQuantity(5);
        expected.setStdDuration(info);
        expected.setMaxNumRelations(10);
        expected.setMinNumRelations(2);
	}
	
	/*
		A method to test the fields for a OrgPositionRestriction after an update operation, followed by a read operation,
		where actual is the DTO returned by the read operation, and expected is the dto returned by the update operation.
	*/
	public void testCrudOrgPositionRestriction_testDTOFieldsForTestReadAfterUpdate(OrgPositionRestrictionInfo expected, OrgPositionRestrictionInfo actual) 
	{
		assertEquals (expected.getId(), actual.getId());
		assertEquals (expected.getOrgId(), actual.getOrgId());
		assertEquals (expected.getOrgPersonRelationTypeKey(), actual.getOrgPersonRelationTypeKey());
		assertEquals (expected.getTitle(), actual.getTitle());
        new RichTextTester().check(expected.getDescr(), actual.getDescr());
        assertTrue((expected.getStdDuration() != null && actual.getStdDuration() != null) || (expected.getStdDuration() == null && actual.getStdDuration() == null));
        if(expected.getStdDuration() != null) {
            assertEquals(expected.getStdDuration().getAtpDurationTypeKey(), actual.getStdDuration().getAtpDurationTypeKey());
            assertEquals(actual.getStdDuration().getTimeQuantity(), actual.getStdDuration().getTimeQuantity());
        }
        assertEquals(expected.getMaxNumRelations(), actual.getMaxNumRelations());
        assertEquals(expected.getMinNumRelations(), actual.getMinNumRelations());
	}
	
	/*
		A method to set the fields for a OrgPositionRestriction in the 'test read after update' section.
		This dto is another (second) dto object being created for other tests.
	*/
	public void testCrudOrgPositionRestriction_setDTOFieldsForTestReadAfterUpdate(OrgPositionRestrictionInfo expected) 
	{
		expected.setOrgId("8");
		expected.setOrgPersonRelationTypeKey("orgPersonRelationTypeKey_Updated");
		expected.setTitle("title_Updated");
        expected.setDescr(RichTextHelper.buildRichTextInfo("plain opr updated descr", "formatted opr updated descr"));
        TimeAmountInfo info = new TimeAmountInfo();
        info.setAtpDurationTypeKey(AtpServiceConstants.DURATION_WEEK_TYPE_KEY);
        info.setTimeQuantity(5);
        expected.setStdDuration(info);
        expected.setMaxNumRelations(10);
        expected.setMinNumRelations(2);
	}
	
	
	// ========================================
	// SERVICE OPS NOT TESTED IN BASE TEST CLASS
	// ========================================
	
	/* Method Name: getOrgHierarchies */
	@Test
	public void test_getOrgHierarchies() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{
        List<OrgHierarchyInfo> orgHierarchyInfos = testService.getOrgHierarchies(contextInfo);
        assertEquals(2,orgHierarchyInfos.size());
        Map<String, OrgHierarchyInfo> map = generateHierarchyMap(orgHierarchyInfos);
        assertNotNull(map.get("A"));
        assertNotNull(map.get("B"));
	}

    private Map<String, OrgHierarchyInfo> generateHierarchyMap(List<OrgHierarchyInfo> list) {
        Map<String, OrgHierarchyInfo> map = new HashMap<String, OrgHierarchyInfo>();
        for(OrgHierarchyInfo info : list) {
            map.put(info.getId(), info);
        }
        return map;
    }
	
	/* Method Name: searchForOrgHierarchyIds */
	@Test
	public void test_searchForOrgHierarchyIds() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{
        //NOT NEEDED
	}
	
	/* Method Name: searchForOrgHierarchies */
	@Test
	public void test_searchForOrgHierarchies() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{
        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        Predicate[] predicates = {PredicateFactory.like("name", "Hierarchy%")};
        builder.setPredicates(predicates);
        QueryByCriteria criteria = builder.build();

        List<String> ids = testService.searchForOrgHierarchyIds(criteria, contextInfo);
        assertEquals(2, ids.size());
        assertTrue(ids.contains("A"));
        assertTrue(ids.contains("B"));

        List<OrgHierarchyInfo> orgHierarchies = testService.searchForOrgHierarchies(criteria, contextInfo);
        assertEquals(2, ids.size());
        for(OrgHierarchy orgHierarchy : orgHierarchies) {
            assertTrue(ids.remove(orgHierarchy.getId()));
        }

        predicates[0] = PredicateFactory.equal("id", "someRandomFakeId");
        builder.setPredicates(predicates);
        criteria = builder.build();

        ids = testService.searchForOrgHierarchyIds(criteria, contextInfo);
        assertEquals(0, ids.size());
        orgHierarchies = testService.searchForOrgHierarchies(criteria, contextInfo);
        assertEquals(0, orgHierarchies.size());

        predicates[0] = PredicateFactory.and(PredicateFactory.equal("id", "A"),
                PredicateFactory.equal("stateKey", OrganizationServiceDataLoader.ACTIVE_STATE),
                PredicateFactory.like("descr.formatted", "%desc%"));
        builder.setPredicates(predicates);
        criteria = builder.build();

        ids = testService.searchForOrgHierarchyIds(criteria, contextInfo);
        assertEquals(1, ids.size());
        assertEquals("A", ids.get(0));
        orgHierarchies = testService.searchForOrgHierarchies(criteria, contextInfo);
        assertEquals(1, orgHierarchies.size());
        assertEquals("A", orgHierarchies.get(0).getId());
	}
	
	/* Method Name: validateOrgHierarchy */
	@Test
	public void test_validateOrgHierarchy() 
	throws 	DoesNotExistException	,InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{
	}
	
	/* Method Name: searchForOrgIds */
	@Test
	public void test_searchForOrgIds() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{
        //NOT NEEDED
	}
	
	/* Method Name: searchForOrgs */
	@Test
	public void test_searchForOrgs() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{
        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        Predicate[] predicates = {PredicateFactory.like("shortName", "TestOrgShortName%")};
        builder.setPredicates(predicates);
        QueryByCriteria criteria = builder.build();

        List<String> ids = testService.searchForOrgIds(criteria, contextInfo);
        assertEquals(20, ids.size());
        for (int i = 1; i <= 20; i++) {
            assertTrue(ids.contains(String.valueOf(i)));
        }

        List<OrgInfo> orgs = testService.searchForOrgs(criteria, contextInfo);
        assertEquals(20, ids.size());
        for (OrgInfo org : orgs) {
            assertTrue(ids.remove(org.getId()));
        }

        predicates[0] = PredicateFactory.equal("id", "someRandomFakeId");
        builder.setPredicates(predicates);
        criteria = builder.build();

        ids = testService.searchForOrgIds(criteria, contextInfo);
        assertEquals(0, ids.size());
        orgs = testService.searchForOrgs(criteria, contextInfo);
        assertEquals(0, orgs.size());

        predicates[0] = PredicateFactory.and(PredicateFactory.equal("id", "1"),
                PredicateFactory.equal("stateKey", OrganizationServiceDataLoader.ACTIVE_STATE),
                PredicateFactory.like("longDescr.formatted", "Lo%"));
        builder.setPredicates(predicates);
        criteria = builder.build();

        ids = testService.searchForOrgIds(criteria, contextInfo);
        assertEquals(1, ids.size());
        assertEquals("1", ids.get(0));
        orgs = testService.searchForOrgs(criteria, contextInfo);
        assertEquals(1, orgs.size());
        assertEquals("1", orgs.get(0).getId());
	}
	
	/* Method Name: validateOrg */
	@Test
	public void test_validateOrg() 
	throws 	DoesNotExistException	,InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{
	}
	
	/* Method Name: hasOrgOrgRelation */
	@Test
	public void test_hasOrgOrgRelation() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{
        //Not implemented because it has been deprecated
	}
	
	/* Method Name: getOrgOrgRelationsByOrg */
	@Test
	public void test_getOrgOrgRelationsByOrg() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{
        //org with no relations
        List<OrgOrgRelationInfo> orgRelations = testService.getOrgOrgRelationsByOrg("17", contextInfo);
        assertTrue(orgRelations.isEmpty());

        //org with one relation
        orgRelations = testService.getOrgOrgRelationsByOrg("1", contextInfo);
        assertEquals(1, orgRelations.size());
        OrgOrgRelationInfo info = getOrgOrgRelationInfoByRelatedOrgIdAndType("2", OrganizationServiceDataLoader.ORG_RELATION_ACAD_PARENT_TYPE, orgRelations);
        validateOrgOrgRelation(info, "1", "2", OrganizationServiceDataLoader.ORG_RELATION_ACAD_PARENT_TYPE);

        //org with multiple relations of different types.
        orgRelations = testService.getOrgOrgRelationsByOrg("4", contextInfo);
        assertEquals(6, orgRelations.size());
        info = getOrgOrgRelationInfoByRelatedOrgIdAndType("12", OrganizationServiceDataLoader.ORG_RELATION_ACAD_COLLABORATES_WITH_TYPE, orgRelations);
        validateOrgOrgRelation(info, "4", "12", OrganizationServiceDataLoader.ORG_RELATION_ACAD_COLLABORATES_WITH_TYPE);
        info = getOrgOrgRelationInfoByRelatedOrgIdAndType("7", OrganizationServiceDataLoader.ORG_RELATION_FIN_PARENT_TYPE, orgRelations);
        validateOrgOrgRelation(info, "4", "7", OrganizationServiceDataLoader.ORG_RELATION_FIN_PARENT_TYPE);
        info = getOrgOrgRelationInfoByRelatedOrgIdAndType("10", OrganizationServiceDataLoader.ORG_RELATION_FIN_PARENT_TYPE, orgRelations);
        validateOrgOrgRelation(info, "4", "10", OrganizationServiceDataLoader.ORG_RELATION_FIN_PARENT_TYPE);
        info = getOrgOrgRelationInfoByRelatedOrgIdAndType("10", OrganizationServiceDataLoader.ORG_RELATION_ACAD_COLLABORATES_WITH_TYPE, orgRelations);
        validateOrgOrgRelation(info, "4", "10", OrganizationServiceDataLoader.ORG_RELATION_ACAD_COLLABORATES_WITH_TYPE);
    }
	
	/* Method Name: getOrgOrgRelationsByOrgs */
	@Test
	public void test_getOrgOrgRelationsByOrgs() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{
        //no relations
        List<OrgOrgRelationInfo> orgRelations = testService.getOrgOrgRelationsByOrgs("14", "15", contextInfo);
        assertTrue(orgRelations.isEmpty());

        orgRelations = testService.getOrgOrgRelationsByOrgs("1", "3", contextInfo);
        assertTrue(orgRelations.isEmpty());

        //org with one relation
        orgRelations = testService.getOrgOrgRelationsByOrgs("1", "2", contextInfo);
        assertEquals(1, orgRelations.size());
        OrgOrgRelationInfo info = getOrgOrgRelationInfoByRelatedOrgIdAndType("2", OrganizationServiceDataLoader.ORG_RELATION_ACAD_PARENT_TYPE, orgRelations);
        validateOrgOrgRelation(info, "1", "2", OrganizationServiceDataLoader.ORG_RELATION_ACAD_PARENT_TYPE);

        //org with multiple relations of different types.
        orgRelations = testService.getOrgOrgRelationsByOrgs("4", "10", contextInfo);
        assertEquals(2, orgRelations.size());
        info = getOrgOrgRelationInfoByRelatedOrgIdAndType("10", OrganizationServiceDataLoader.ORG_RELATION_FIN_PARENT_TYPE, orgRelations);
        validateOrgOrgRelation(info, "4", "10", OrganizationServiceDataLoader.ORG_RELATION_FIN_PARENT_TYPE);
        info = getOrgOrgRelationInfoByRelatedOrgIdAndType("10", OrganizationServiceDataLoader.ORG_RELATION_ACAD_COLLABORATES_WITH_TYPE, orgRelations);
        validateOrgOrgRelation(info, "4", "10", OrganizationServiceDataLoader.ORG_RELATION_ACAD_COLLABORATES_WITH_TYPE);
	}
	
	/* Method Name: getOrgOrgRelationsByTypeAndOrg */
	@Test
	public void test_getOrgOrgRelationsByTypeAndOrg() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{
        //no relations
        List<OrgOrgRelationInfo> orgRelations = testService.getOrgOrgRelationsByTypeAndOrg("14",
                OrganizationServiceDataLoader.ORG_RELATION_ACAD_PARENT_TYPE, contextInfo);
        assertTrue(orgRelations.isEmpty());

        orgRelations = testService.getOrgOrgRelationsByTypeAndOrg("1", OrganizationServiceDataLoader.ORG_RELATION_FIN_PARENT_TYPE, contextInfo);
        assertTrue(orgRelations.isEmpty());

        //org with one relation
        orgRelations = testService.getOrgOrgRelationsByTypeAndOrg("1", OrganizationServiceDataLoader.ORG_RELATION_ACAD_PARENT_TYPE, contextInfo);
        assertEquals(1, orgRelations.size());
        OrgOrgRelationInfo info = getOrgOrgRelationInfoByRelatedOrgIdAndType("2", OrganizationServiceDataLoader.ORG_RELATION_ACAD_PARENT_TYPE, orgRelations);
        validateOrgOrgRelation(info, "1", "2", OrganizationServiceDataLoader.ORG_RELATION_ACAD_PARENT_TYPE);

        //org with multiple relations of different types.
        orgRelations = testService.getOrgOrgRelationsByTypeAndOrg("4", OrganizationServiceDataLoader.ORG_RELATION_FIN_PARENT_TYPE, contextInfo);
        assertEquals(2, orgRelations.size());
        info = getOrgOrgRelationInfoByRelatedOrgIdAndType("7", OrganizationServiceDataLoader.ORG_RELATION_FIN_PARENT_TYPE, orgRelations);
        validateOrgOrgRelation(info, "4", "7", OrganizationServiceDataLoader.ORG_RELATION_FIN_PARENT_TYPE);
        info = getOrgOrgRelationInfoByRelatedOrgIdAndType("10", OrganizationServiceDataLoader.ORG_RELATION_FIN_PARENT_TYPE, orgRelations);
        validateOrgOrgRelation(info, "4", "10", OrganizationServiceDataLoader.ORG_RELATION_FIN_PARENT_TYPE);
	}
	
	/* Method Name: getOrgOrgRelationsByTypeAndRelatedOrg */
	@Test
	public void test_getOrgOrgRelationsByTypeAndRelatedOrg() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{
        //no relations
        List<OrgOrgRelationInfo> orgRelations = testService.getOrgOrgRelationsByTypeAndRelatedOrg("14",
                OrganizationServiceDataLoader.ORG_RELATION_ACAD_PARENT_TYPE, contextInfo);
        assertTrue(orgRelations.isEmpty());

        //one relation
        orgRelations = testService.getOrgOrgRelationsByTypeAndRelatedOrg("10", OrganizationServiceDataLoader.ORG_RELATION_FIN_PARENT_TYPE, contextInfo);
        assertEquals(1, orgRelations.size());
        validateOrgOrgRelation(orgRelations.get(0), "4", "10", OrganizationServiceDataLoader.ORG_RELATION_FIN_PARENT_TYPE);

        //multiple relations
        orgRelations = testService.getOrgOrgRelationsByTypeAndRelatedOrg("16", OrganizationServiceDataLoader.ORG_RELATION_FIN_PARENT_TYPE, contextInfo);
        assertEquals(2, orgRelations.size());
        OrgOrgRelationInfo info = getOrgOrgRelationInfoByOrgIdAndType("14", OrganizationServiceDataLoader.ORG_RELATION_FIN_PARENT_TYPE, orgRelations);
        validateOrgOrgRelation(info, "14", "16", OrganizationServiceDataLoader.ORG_RELATION_FIN_PARENT_TYPE);
        info = getOrgOrgRelationInfoByOrgIdAndType("15", OrganizationServiceDataLoader.ORG_RELATION_FIN_PARENT_TYPE, orgRelations);
        validateOrgOrgRelation(info, "15", "16", OrganizationServiceDataLoader.ORG_RELATION_FIN_PARENT_TYPE);
	}
	
	/* Method Name: searchForOrgOrgRelationIds */
	@Test
	public void test_searchForOrgOrgRelationIds() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{
        //NOT NEEDED
	}

	/* Method Name: searchForOrgOrgRelations */
	@Test
	public void test_searchForOrgOrgRelations() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{
        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        Predicate[] predicates = {PredicateFactory.equal("orgId", "13")};
        builder.setPredicates(predicates);
        QueryByCriteria criteria = builder.build();

        List<String> ids = testService.searchForOrgOrgRelationIds(criteria, contextInfo);
        assertEquals(3, ids.size());

        List<OrgOrgRelationInfo> orgOrgRelations = testService.searchForOrgOrgRelations(criteria, contextInfo);
        assertEquals(3, orgOrgRelations.size());
        for(OrgOrgRelationInfo orgRelation : orgOrgRelations) {
            assertTrue(ids.remove(orgRelation.getId()));
        }

        predicates[0] = PredicateFactory.equal("orgId", "someRandomFakeId");
        builder.setPredicates(predicates);
        criteria = builder.build();

        ids = testService.searchForOrgOrgRelationIds(criteria, contextInfo);
        assertEquals(0, ids.size());
        orgOrgRelations = testService.searchForOrgOrgRelations(criteria, contextInfo);
        assertEquals(0, orgOrgRelations.size());

        predicates[0] = PredicateFactory.and(PredicateFactory.equal("orgId", "2"),
                PredicateFactory.equal("relatedOrgId", "3"));
        builder.setPredicates(predicates);
        criteria = builder.build();

        ids = testService.searchForOrgOrgRelationIds(criteria, contextInfo);
        assertEquals(1, ids.size());
        orgOrgRelations = testService.searchForOrgOrgRelations(criteria, contextInfo);
        assertEquals(1, orgOrgRelations.size());
        assertEquals(ids.get(0), orgOrgRelations.get(0).getId());
	}
	
	/* Method Name: validateOrgOrgRelation */
	@Test
	public void test_validateOrgOrgRelation() 
	throws 	DoesNotExistException	,InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{
	}
	
	/* Method Name: hasOrgPersonRelation */
	@Test
	public void test_hasOrgPersonRelation() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{
	}
	
	/* Method Name: getOrgPersonRelationsByOrg */
	@Test
	public void test_getOrgPersonRelationsByOrg() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{
        //only one person id
        List<OrgPersonRelationInfo> relations = testService.getOrgPersonRelationsByOrg("1", contextInfo);
        assertEquals(2, relations.size());
        OrgPersonRelationInfo relation = getOrgPersonRelationInfoByPersonAndType(relations, "1", OrganizationServiceDataLoader.ORG_PERSON_RELATION_PRESIDENT_TYPE);
        validateOrgPersonRelation(relation, "1", "1", OrganizationServiceDataLoader.ORG_PERSON_RELATION_PRESIDENT_TYPE);
        relation = getOrgPersonRelationInfoByPersonAndType(relations, "1", OrganizationServiceDataLoader.ORG_PERSON_RELATION_MEMBER_TYPE);
        validateOrgPersonRelation(relation, "1", "1", OrganizationServiceDataLoader.ORG_PERSON_RELATION_MEMBER_TYPE);


        //lots of people one duplicate
        relations = testService.getOrgPersonRelationsByOrg("20", contextInfo);
        assertEquals(21, relations.size());
        relation = getOrgPersonRelationInfoByPersonAndType(relations, "1", OrganizationServiceDataLoader.ORG_PERSON_RELATION_PRESIDENT_TYPE);
        validateOrgPersonRelation(relation, "20", "1", OrganizationServiceDataLoader.ORG_PERSON_RELATION_PRESIDENT_TYPE);
        for (int i = 1; i <= 20; i++) {
            relation = getOrgPersonRelationInfoByPersonAndType(relations, String.valueOf(i), OrganizationServiceDataLoader.ORG_PERSON_RELATION_MEMBER_TYPE);
            validateOrgPersonRelation(relation, "20", String.valueOf(i), OrganizationServiceDataLoader.ORG_PERSON_RELATION_MEMBER_TYPE);
        }

        //no relations
        relations = testService.getOrgPersonRelationsByOrg("50", contextInfo);
        assertTrue(relations.isEmpty());
	}
	
	/* Method Name: getOrgPersonRelationsByTypeAndOrg */
	@Test
	public void test_getOrgPersonRelationsByTypeAndOrg() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{
	}
	
	/* Method Name: getOrgPersonRelationsByPerson */
	@Test
	public void test_getOrgPersonRelationsByPerson() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{
	}
	
	/* Method Name: getOrgPersonRelationsByTypeAndPerson */
	@Test
	public void test_getOrgPersonRelationsByTypeAndPerson() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{
        //one relation of this type in all orgs.
        List<OrgPersonRelationInfo> relations = testService.getOrgPersonRelationsByTypeAndPerson(OrganizationServiceDataLoader.ORG_PERSON_RELATION_PRESIDENT_TYPE, "1", contextInfo);
        assertEquals(20, relations.size());
        for (int i = 1; i <= 20; i++) {
            OrgPersonRelationInfo relation = getOrgPersonRelationInfoByOrg(relations, String.valueOf(i));
            validateOrgPersonRelation(relation, String.valueOf(i), "1", OrganizationServiceDataLoader.ORG_PERSON_RELATION_PRESIDENT_TYPE);
        }

        //no relations
        relations = testService.getOrgPersonRelationsByTypeAndPerson(OrganizationServiceDataLoader.ORG_PERSON_RELATION_PRESIDENT_TYPE, "50", contextInfo);
        assertTrue(relations.isEmpty());
	}
	
	/* Method Name: getOrgPersonRelationsByOrgAndPerson */
	@Test
	public void test_getOrgPersonRelationsByOrgAndPerson() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{
        //person with two relations to an org
        List<OrgPersonRelationInfo> relations = testService.getOrgPersonRelationsByOrgAndPerson("10", "1", contextInfo);
        assertEquals(2, relations.size());
        OrgPersonRelationInfo relation = getOrgPersonRelationInfoByPersonAndType(relations, "1", OrganizationServiceDataLoader.ORG_PERSON_RELATION_PRESIDENT_TYPE);
        validateOrgPersonRelation(relation, "10", "1", OrganizationServiceDataLoader.ORG_PERSON_RELATION_PRESIDENT_TYPE);
        relation = getOrgPersonRelationInfoByPersonAndType(relations, "1", OrganizationServiceDataLoader.ORG_PERSON_RELATION_MEMBER_TYPE);
        validateOrgPersonRelation(relation, "10", "1", OrganizationServiceDataLoader.ORG_PERSON_RELATION_MEMBER_TYPE);


        //person with only one relation to an org
        relations = testService.getOrgPersonRelationsByOrgAndPerson("2", "2", contextInfo);
        assertEquals(1, relations.size());
        relation = getOrgPersonRelationInfoByPersonAndType(relations, "2", OrganizationServiceDataLoader.ORG_PERSON_RELATION_MEMBER_TYPE);
        validateOrgPersonRelation(relation, "2", "2", OrganizationServiceDataLoader.ORG_PERSON_RELATION_MEMBER_TYPE);

        //no relations
        relations = testService.getOrgPersonRelationsByOrgAndPerson("50", "2", contextInfo);
        assertTrue(relations.isEmpty());
	}
	
	/* Method Name: getOrgPersonRelationsByTypeAndOrgAndPerson */
	@Test
	public void test_getOrgPersonRelationsByTypeAndOrgAndPerson() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{
	}
	
	/* Method Name: searchForOrgPersonRelationIds */
	@Test
	public void test_searchForOrgPersonRelationIds() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{
        //Not NEEDED
	}
	
	/* Method Name: searchForOrgPersonRelations */
	@Test
	public void test_searchForOrgPersonRelations() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{
        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        Predicate[] predicates = {PredicateFactory.and(PredicateFactory.equal("personId", "1"), PredicateFactory.like("orgId", "2%"), PredicateFactory.equal("typeKey", OrganizationServiceDataLoader.ORG_PERSON_RELATION_PRESIDENT_TYPE))};
        builder.setPredicates(predicates);
        QueryByCriteria criteria = builder.build();

        List<String> ids = testService.searchForOrgPersonRelationIds(criteria, contextInfo);
        assertEquals(2, ids.size());

        List<OrgPersonRelationInfo> orgPeople = testService.searchForOrgPersonRelations(criteria, contextInfo);
        assertEquals(2, orgPeople.size());
        for(OrgPersonRelationInfo orgPerson : orgPeople) {
            assertTrue(ids.remove(orgPerson.getId()));
        }

        predicates[0] = PredicateFactory.equal("id", "someRandomFakeId");
        builder.setPredicates(predicates);
        criteria = builder.build();

        ids = testService.searchForOrgPersonRelationIds(criteria, contextInfo);
        assertEquals(0, ids.size());
        orgPeople = testService.searchForOrgPersonRelations(criteria, contextInfo);
        assertEquals(0, orgPeople.size());

        predicates[0] = PredicateFactory.and(PredicateFactory.equal("orgId", "1"),
                PredicateFactory.equal("stateKey", OrganizationServiceDataLoader.ACTIVE_STATE),
                PredicateFactory.equal("typeKey", OrganizationServiceDataLoader.ORG_PERSON_RELATION_PRESIDENT_TYPE));
        builder.setPredicates(predicates);
        criteria = builder.build();

        ids = testService.searchForOrgPersonRelationIds(criteria, contextInfo);
        assertEquals(1, ids.size());
        orgPeople = testService.searchForOrgPersonRelations(criteria, contextInfo);
        assertEquals(1, orgPeople.size());
        assertEquals("1", orgPeople.get(0).getOrgId());
	}
	
	/* Method Name: validateOrgPersonRelation */
	@Test
	public void test_validateOrgPersonRelation() 
	throws 	DoesNotExistException	,InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{
	}
	
	/* Method Name: getOrgPositionRestrictionIdsByOrg */
	@Test
	public void test_getOrgPositionRestrictionIdsByOrg() 
	throws 	DoesNotExistException	,InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{        //no restrictions
        List<String> restrictions = testService.getOrgPositionRestrictionIdsByOrg("50", contextInfo);
        assertTrue(restrictions.isEmpty());

        //multiple restrictions
        restrictions = testService.getOrgPositionRestrictionIdsByOrg("20", contextInfo);
        assertEquals(2, restrictions.size());
        assertTrue(restrictions.contains("20"));
        assertTrue(restrictions.contains("40"));
	}

    /*Existing test that was pulled in*/
    @Test
    public void testGetOrgPositionRestrictionIdsByType() throws PermissionDeniedException, MissingParameterException, InvalidParameterException, OperationFailedException, DoesNotExistException {
        //multiple restrictions
        List<String> restrictions = testService.getOrgPositionRestrictionIdsByType(OrganizationServiceDataLoader.ORG_PERSON_RELATION_PRESIDENT_TYPE, contextInfo);
        assertEquals(20, restrictions.size());

        for (int i = 1; i <= 20; i++) {
            assertTrue(restrictions.contains(String.valueOf(i)));
        }

        restrictions = testService.getOrgPositionRestrictionIdsByType(OrganizationServiceDataLoader.ORG_PERSON_RELATION_MEMBER_TYPE, contextInfo);
        assertEquals(20, restrictions.size());

        for (int i = 21; i <= 40; i++) {
            assertTrue(restrictions.contains(String.valueOf(i)));
        }
    }

	/* Method Name: searchForOrgPositionRestrictionIds */
	@Test
	public void test_searchForOrgPositionRestrictionIds()
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{
        //NOT NEEDED
	}
	
	/* Method Name: searchForOrgPositionRestrictions */
	@Test
	public void test_searchForOrgPositionRestrictions() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{
        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        Predicate[] predicates = {PredicateFactory.like("descr.formatted", "%President%")};
        builder.setPredicates(predicates);
        QueryByCriteria criteria = builder.build();

        List<String> ids = testService.searchForOrgPositionRestrictionIds(criteria, contextInfo);
        assertEquals(20, ids.size());
        for(int i = 1; i <= 20; i++) {
            assertTrue(ids.contains(String.valueOf(i)));
        }

        List<OrgPositionRestrictionInfo> orgPositionRestrictions = testService.searchForOrgPositionRestrictions(criteria, contextInfo);
        assertEquals(20, orgPositionRestrictions.size());
        for(OrgPositionRestrictionInfo restriction : orgPositionRestrictions) {
            assertTrue(ids.remove(restriction.getId()));
        }

        predicates[0] = PredicateFactory.equal("id", "someRandomFakeId");
        builder.setPredicates(predicates);
        criteria = builder.build();

        ids = testService.searchForOrgPositionRestrictionIds(criteria, contextInfo);
        assertEquals(0, ids.size());
        orgPositionRestrictions = testService.searchForOrgPositionRestrictions(criteria, contextInfo);
        assertEquals(0, orgPositionRestrictions.size());

        predicates[0] = PredicateFactory.and(PredicateFactory.equal("title", "Mr. President"),
                PredicateFactory.equal("orgId", "1"));
        builder.setPredicates(predicates);
        criteria = builder.build();

        ids = testService.searchForOrgPositionRestrictionIds(criteria, contextInfo);
        assertEquals(1, ids.size());
        assertEquals("1", ids.get(0));
        orgPositionRestrictions = testService.searchForOrgPositionRestrictions(criteria, contextInfo);
        assertEquals(1, orgPositionRestrictions.size());
        assertEquals("1", orgPositionRestrictions.get(0).getId());
	}
	
	/* Method Name: validateOrgPositionRestriction */
	@Test
	public void test_validateOrgPositionRestriction() 
	throws 	DoesNotExistException	,InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{
	}
	
	/* Method Name: isDescendant */
	@Test
	public void test_isDescendant() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{
        assertTrue(testService.isDescendant("1", "2", "A", contextInfo));
        assertTrue(testService.isDescendant("1", "10", "A", contextInfo));
        assertFalse(testService.isDescendant("3", "4", "A", contextInfo));
        assertFalse(testService.isDescendant("3", "2", "A", contextInfo));

        assertFalse(testService.isDescendant("1", "2", "B", contextInfo));
        assertTrue(testService.isDescendant("4", "10", "B", contextInfo));
        assertFalse(testService.isDescendant("11", "8", "B", contextInfo));
        assertFalse(testService.isDescendant("11", "3", "B", contextInfo));

        assertFalse(testService.isDescendant("1", "20", "A", contextInfo));

        assertTrue(testService.isDescendant("4", "10", "B", contextInfo));

        assertTrue(testService.isDescendant("13", "13", "B", contextInfo));
        assertFalse(testService.isDescendant("11", "11", "B", contextInfo));
	}
	
	/* Method Name: getAllDescendants */
	@Test
	public void test_getAllDescendants() 
	throws 	DoesNotExistException	,InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{
        //Not implemented because it has been deprecated
	}
	
	/* Method Name: getAllAncestors */
	@Test
	public void test_getAllAncestors() 
	throws 	DoesNotExistException	,InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{
        //Not implemented because it has been deprecated
	}
	
	/* Method Name: getOrgTree */
	@Test
	public void test_getOrgTree() 
	throws 	DoesNotExistException	,InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	{
        //pull the entire tree from the root
        OrgTreeViewInfo treeFromOrgOne = testService.getOrgTree("1", "A", 0, contextInfo);
        validateTree(treeFromOrgOne, "1", 0, 1);
        OrgTreeViewInfo treeFromOrgTwo = treeFromOrgOne.getChildren().get(0);
        validateTree(treeFromOrgTwo, "2", 1, 2);
        assertTrue(treeFromOrgTwo.getParents().get(0) == treeFromOrgOne);
        OrgTreeViewInfo treeFromOrgThree = getOrgTreeViewInfoById("3", treeFromOrgTwo.getChildren());
        validateTree(treeFromOrgThree, "3", 1, 3);
        OrgTreeViewInfo treeFromOrgFive = getOrgTreeViewInfoById("5", treeFromOrgThree.getChildren());
        validateTree(treeFromOrgFive, "5", 1, 0);
        OrgTreeViewInfo treeFromOrgSix = getOrgTreeViewInfoById("6", treeFromOrgThree.getChildren());
        validateTree(treeFromOrgSix, "6", 1, 0);
        OrgTreeViewInfo treeFromOrgSeven = getOrgTreeViewInfoById("7", treeFromOrgThree.getChildren());
        validateTree(treeFromOrgSeven, "7", 1, 3);
        OrgTreeViewInfo treeFromOrgEight = getOrgTreeViewInfoById("8", treeFromOrgSeven.getChildren());
        validateTree(treeFromOrgEight, "8", 2, 1);
        OrgTreeViewInfo treeFromOrgNine = getOrgTreeViewInfoById("9", treeFromOrgSeven.getChildren());
        validateTree(treeFromOrgNine, "9", 2, 1);
        treeFromOrgNine = getOrgTreeViewInfoById("9", treeFromOrgEight.getChildren());
        validateTree(treeFromOrgNine, "9", 2, 1);
        OrgTreeViewInfo treeFromOrgTen = getOrgTreeViewInfoById("10", treeFromOrgSeven.getChildren());
        validateTree(treeFromOrgTen, "10", 2, 0);
        OrgTreeViewInfo treeFromOrgFour = getOrgTreeViewInfoById("4", treeFromOrgTwo.getChildren());
        validateTree(treeFromOrgFour, "4", 1, 2);
        OrgTreeViewInfo treeFromOrgTwelve = getOrgTreeViewInfoById("12", treeFromOrgFour.getChildren());
        validateTree(treeFromOrgTwelve, "12", 1, 0);

        //Test pulling with an org that is not in the tree.
        treeFromOrgTwo = testService.getOrgTree("2", "B", 0, contextInfo);
        validateTree(treeFromOrgTwo, "2", 0, 0);

        //Test pulling one level of parents.
        treeFromOrgFour = testService.getOrgTree("4", "B", -1, contextInfo);
        validateTree(treeFromOrgFour, "4", 1, 0);
        OrgTreeViewInfo treeFromOrgEleven = treeFromOrgFour.getParents().get(0);
        validateTree(treeFromOrgEleven, "11", 0, 1);

        //Test pulling one level of children.
        treeFromOrgFour = testService.getOrgTree("4", "B", 1, contextInfo);
        validateTree(treeFromOrgFour, "4", 0, 2);
        treeFromOrgSeven = getOrgTreeViewInfoById("7", treeFromOrgFour.getChildren());
        validateTree(treeFromOrgSeven, "7", 1, 0);
        treeFromOrgTen = getOrgTreeViewInfoById("10", treeFromOrgFour.getChildren());
        validateTree(treeFromOrgTen, "10", 1, 0);

        //pull entire tree from the perspective of 9.
        treeFromOrgNine = testService.getOrgTree("9", "A", 0, contextInfo);
        validateTree(treeFromOrgNine, "9", 2, 1);
        treeFromOrgEight = getOrgTreeViewInfoById("8", treeFromOrgNine.getChildren());
        validateTree(treeFromOrgEight, "8", 1, 1);
        treeFromOrgSeven = getOrgTreeViewInfoById("7", treeFromOrgNine.getParents());
        validateTree(treeFromOrgSeven, "7", 1, 1);
        treeFromOrgThree = getOrgTreeViewInfoById("3", treeFromOrgSeven.getParents());
        validateTree(treeFromOrgThree, "3", 1, 1);
        treeFromOrgTwo = getOrgTreeViewInfoById("2", treeFromOrgThree.getParents());
        validateTree(treeFromOrgTwo, "2", 1, 1);
        treeFromOrgOne = getOrgTreeViewInfoById("1", treeFromOrgTwo.getParents());
        validateTree(treeFromOrgOne, "1", 0, 1);

        //pull a node that is its own parent.
        OrgTreeViewInfo treeFromOrgThirteen = testService.getOrgTree("13", "B", 0, contextInfo);
        validateTree(treeFromOrgThirteen, "13", 1, 3);
	}

    private void validateTree(OrgTreeViewInfo orgTreeViewInfo, String orgId, int parentCount, int childCount) {
        assertNotNull(orgTreeViewInfo);
        assertEquals(orgId, orgTreeViewInfo.getOrg().getId());
        assertEquals(parentCount, orgTreeViewInfo.getParents().size());
        assertEquals(childCount, orgTreeViewInfo.getChildren().size());
    }

    private OrgTreeViewInfo getOrgTreeViewInfoById(String orgId, List<OrgTreeViewInfo> orgTrees) {
        for (OrgTreeViewInfo currentTree : orgTrees) {
            if (currentTree.getOrg().getId().equals(orgId)) {
                return currentTree;
            }
        }
        return null;
    }

    private void validateOrgOrgRelation(OrgOrgRelationInfo info, String orgId, String relatedOrgId, String type) {
        assertNotNull(info);
        assertEquals(orgId, info.getOrgId());
        assertEquals(relatedOrgId, info.getRelatedOrgId());
        assertEquals(type, info.getTypeKey());
    }

    private OrgOrgRelationInfo getOrgOrgRelationInfoByRelatedOrgIdAndType(String id, String type, List<OrgOrgRelationInfo> relations) {
        for (OrgOrgRelationInfo info : relations) {
            if (info.getRelatedOrgId().equals(id) && info.getTypeKey().equals(type)) {
                return info;
            }
        }
        return null;
    }

    private OrgOrgRelationInfo getOrgOrgRelationInfoByOrgIdAndType(String id, String type, List<OrgOrgRelationInfo> relations) {
        for (OrgOrgRelationInfo info : relations) {
            if (info.getOrgId().equals(id) && info.getTypeKey().equals(type)) {
                return info;
            }
        }
        return null;
    }

    private OrgPersonRelationInfo getOrgPersonRelationInfoByPersonAndType(List<OrgPersonRelationInfo> relations, String personId, String type) {
        for (OrgPersonRelationInfo info : relations) {
            if (info.getPersonId().equals(personId) && info.getTypeKey().equals(type)) {
                return info;
            }
        }
        return null;
    }

    private OrgPersonRelationInfo getOrgPersonRelationInfoByOrg(List<OrgPersonRelationInfo> relations, String id) {
        for (OrgPersonRelationInfo info : relations) {
            if (info.getOrgId().equals(id)) {
                return info;
            }
        }
        return null;
    }

    private void validateOrgPersonRelation(OrgPersonRelationInfo info, String orgId, String personId, String type) {
        assertNotNull(info);
        assertEquals(orgId, info.getOrgId());
        assertEquals(personId, info.getPersonId());
        assertEquals(type, info.getTypeKey());
    }
}


