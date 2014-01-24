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


import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.student.common.test.util.AttributeTester;
import org.kuali.student.common.test.util.IdEntityTester;
import org.kuali.student.common.test.util.MetaTester;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.dto.RelationshipInfo;
import org.kuali.student.r2.common.dto.StatusInfo;
import org.kuali.student.r2.common.exceptions.AlreadyExistsException;
import org.kuali.student.r2.common.exceptions.DataValidationErrorException;
import org.kuali.student.r2.common.exceptions.DependentObjectsExistException;
import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.exceptions.PermissionDeniedException;
import org.kuali.student.r2.common.exceptions.ReadOnlyException;
import org.kuali.student.r2.common.exceptions.VersionMismatchException;
import org.kuali.student.r2.common.infc.IdNamelessEntity;
import org.kuali.student.r2.core.organization.dto.OrgHierarchyInfo;
import org.kuali.student.r2.core.organization.dto.OrgInfo;
import org.kuali.student.r2.core.organization.dto.OrgOrgRelationInfo;
import org.kuali.student.r2.core.organization.dto.OrgPersonRelationInfo;
import org.kuali.student.r2.core.organization.dto.OrgPositionRestrictionInfo;
import org.kuali.student.r2.core.organization.service.OrganizationService;
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
public abstract class TestOrganizationServiceImplConformanceBaseCrud {
	
	// ====================
	// SETUP
	// ====================
	
	@Resource(name = "organizationService")
	public OrganizationService testService;
	public OrganizationService getOrganizationService() { return testService; }
	public void setOrganizationService(OrganizationService service) { testService = service; }
	
	public ContextInfo contextInfo = null;
	public static String principalId = "123";
	
	@Before
	public void setUp()
	{
		principalId = "123";
		contextInfo = new ContextInfo();
		contextInfo.setPrincipalId(principalId);
	}

	
	// ====================
	// TESTING
	// ====================
	
	// ****************************************************
	//           OrgHierarchyInfo
	// ****************************************************
	@Test
	public void testCrudOrgHierarchy() 
		throws DataValidationErrorException,
			DoesNotExistException,
			InvalidParameterException,
			MissingParameterException,
			OperationFailedException,
			PermissionDeniedException,
			ReadOnlyException,
			VersionMismatchException,
			DependentObjectsExistException
	{
			// -------------------------------------
			// test create
			// -------------------------------------
			OrgHierarchyInfo expected = new OrgHierarchyInfo ();
			
			// METHOD TO SET DTO FIELDS HERE FOR TEST CREATE
			testCrudOrgHierarchy_setDTOFieldsForTestCreate (expected);
			
			new AttributeTester().add2ForCreate(expected.getAttributes());
			
			// code to create actual
			OrgHierarchyInfo actual = testService.createOrgHierarchy ( expected.getTypeKey(), expected, contextInfo);
			
			assertNotNull(actual.getId());
			new IdEntityTester().check(expected, actual);
			
			// METHOD TO TEST DTO FIELDS HERE FOR TEST CREATE
			testCrudOrgHierarchy_testDTOFieldsForTestCreateUpdate (expected, actual);
			
			new AttributeTester().check(expected.getAttributes(), actual.getAttributes());
			new MetaTester().checkAfterCreate(actual.getMeta());
			
			// -------------------------------------
			// test read
			// -------------------------------------
			expected = actual;
			actual = testService.getOrgHierarchy ( actual.getId(), contextInfo);
			assertEquals(expected.getId(), actual.getId());
			new IdEntityTester().check(expected, actual);
			
			// INSERT CODE FOR TESTING MORE DTO FIELDS HERE
			testCrudOrgHierarchy_testDTOFieldsForTestCreateUpdate (expected, actual);
			
			new AttributeTester().check(expected.getAttributes(), actual.getAttributes());
			new MetaTester().checkAfterGet(expected.getMeta(), actual.getMeta());
			
			// -------------------------------------
			// test update
			// -------------------------------------
			expected = actual;
			
			expected.setStateKey(expected.getState() + "_Updated");
			
			// METHOD TO INSERT CODE TO UPDATE DTO FIELDS HERE
			testCrudOrgHierarchy_setDTOFieldsForTestUpdate (expected);
			
			new AttributeTester().delete1Update1Add1ForUpdate(expected.getAttributes());
			// code to update
			actual = testService.updateOrgHierarchy ( expected.getId(), expected, contextInfo);
			
			assertEquals(expected.getId(), actual.getId());
			new IdEntityTester().check(expected, actual);
			
			// METHOD TO INSERT CODE FOR TESTING DTO FIELDS HERE
			testCrudOrgHierarchy_testDTOFieldsForTestCreateUpdate (expected, actual);
			
			new AttributeTester().check(expected.getAttributes(), actual.getAttributes());
			new MetaTester().checkAfterUpdate(expected.getMeta(), actual.getMeta());
			
			// -------------------------------------
			// test read after update
			// -------------------------------------
			
			expected = actual;
			// code to get actual
			actual = testService.getOrgHierarchy ( actual.getId(), contextInfo);
			
			assertEquals(expected.getId(), actual.getId());
			new IdEntityTester().check(expected, actual);
			
			// INSERT METHOD CODE FOR TESTING DTO FIELDS HERE
			testCrudOrgHierarchy_testDTOFieldsForTestReadAfterUpdate (expected, actual);
			
			new AttributeTester().check(expected.getAttributes(), actual.getAttributes());
			new MetaTester().checkAfterGet(expected.getMeta(), actual.getMeta());
			
			OrgHierarchyInfo alphaDTO = actual;
			
			// create a 2nd DTO
			OrgHierarchyInfo betaDTO = new OrgHierarchyInfo ();
			
			// METHOD TO INSERT CODE TO SET MORE DTO FIELDS HERE
			testCrudOrgHierarchy_setDTOFieldsForTestReadAfterUpdate (betaDTO);
			
			betaDTO.setTypeKey("typeKeyBeta");
			betaDTO.setStateKey("stateKeyBeta");
			betaDTO = testService.createOrgHierarchy ( betaDTO.getTypeKey(), betaDTO, contextInfo);
			
			// -------------------------------------
			// test bulk get with no ids supplied
			// -------------------------------------
			
			List<String> orgHierarchyIds = new ArrayList<String>();
			// code to get DTO by Ids
			List<OrgHierarchyInfo> records = testService.getOrgHierarchiesByIds(orgHierarchyIds, contextInfo);
			
			assertEquals(orgHierarchyIds.size(), records.size());
			assertEquals(0, orgHierarchyIds.size());
			
			// -------------------------------------
			// test bulk get
			// -------------------------------------
			orgHierarchyIds = new ArrayList<String>();
			orgHierarchyIds.add(alphaDTO.getId());
			orgHierarchyIds.add(betaDTO.getId());
			// code to get DTO by Ids
			records = testService.getOrgHierarchiesByIds(orgHierarchyIds, contextInfo);
			
			assertEquals(orgHierarchyIds.size(), records.size());
			for (OrgHierarchyInfo record : records)
			{
					if (!orgHierarchyIds.remove(record.getId()))
					{
							fail(record.getId());
					}
			}
			assertEquals(0, orgHierarchyIds.size());
			
			// -------------------------------------
			// test get by type
			// -------------------------------------
			// code to get by specific type "typeKey01" 
			orgHierarchyIds = testService.getOrgHierarchyIdsByType ("typeKey01", contextInfo);
			
			assertEquals(1, orgHierarchyIds.size());
			assertEquals(alphaDTO.getId(), orgHierarchyIds.get(0));
			
			// test get by other type
			// code to get by specific type "typeKeyBeta" 
			orgHierarchyIds = testService.getOrgHierarchyIdsByType ("typeKeyBeta", contextInfo);
			
			assertEquals(1, orgHierarchyIds.size());
			assertEquals(betaDTO.getId(), orgHierarchyIds.get(0));
			
			// -------------------------------------
			// test delete
			// -------------------------------------
			
			StatusInfo status = testService.deleteOrgHierarchy ( actual.getId(), contextInfo);
			
			assertNotNull(status);
			assertTrue(status.getIsSuccess());
			try
			{
					OrgHierarchyInfo record = testService.getOrgHierarchy ( actual.getId(), contextInfo);
					fail("Did not receive DoesNotExistException when attempting to get already-deleted entity");
			}
			catch (DoesNotExistException dnee)
			{
					// expected
			}
			
	}
	
	/*
		A method to set the fields for a OrgHierarchy in a 'test create' section prior to calling the 'create' operation.
	*/
	public abstract void testCrudOrgHierarchy_setDTOFieldsForTestCreate(OrgHierarchyInfo expected);
	
	/*
		A method to test the fields for a OrgHierarchy. This is called after:
		- creating a DTO, where actual is the DTO returned by the create operation, and expected is the dto passed in to the create operation
		- reading a DTO after creating it, and actual is the read DTO, and expected is the dto that was created
		- updating a DTO, where actual is DTO returned by the update operation, and expected is the dto that was passed in to the update operation
	*/
	public abstract void testCrudOrgHierarchy_testDTOFieldsForTestCreateUpdate(OrgHierarchyInfo expected, OrgHierarchyInfo actual);
	
	/*
		A method to set the fields for a OrgHierarchy in a 'test update' section prior to calling the 'update' operation.
	*/
	public abstract void testCrudOrgHierarchy_setDTOFieldsForTestUpdate(OrgHierarchyInfo expected);
	
	/*
		A method to test the fields for a OrgHierarchy after an update operation, followed by a read operation,
		where actual is the DTO returned by the read operation, and expected is the dto returned by the update operation.
	*/
	public abstract void testCrudOrgHierarchy_testDTOFieldsForTestReadAfterUpdate(OrgHierarchyInfo expected, OrgHierarchyInfo actual);
	
	/*
		A method to set the fields for a OrgHierarchy in the 'test read after update' section.
		This dto is another (second) dto object being created for other tests.
	*/
	public abstract void testCrudOrgHierarchy_setDTOFieldsForTestReadAfterUpdate(OrgHierarchyInfo expected);
	
	
	// ****************************************************
	//           OrgInfo
	// ****************************************************
	@Test
	public void testCrudOrg() 
		throws DataValidationErrorException,
			DoesNotExistException,
			InvalidParameterException,
			MissingParameterException,
			OperationFailedException,
			PermissionDeniedException,
			ReadOnlyException,
			VersionMismatchException,
			DependentObjectsExistException
	{
			// -------------------------------------
			// test create
			// -------------------------------------
			OrgInfo expected = new OrgInfo ();
			
			// METHOD TO SET DTO FIELDS HERE FOR TEST CREATE
			testCrudOrg_setDTOFieldsForTestCreate (expected);
			
			new AttributeTester().add2ForCreate(expected.getAttributes());
			
			// code to create actual
			OrgInfo actual = testService.createOrg ( expected.getTypeKey(), expected, contextInfo);
			
			assertNotNull(actual.getId());
			check(expected, actual);
			
			// METHOD TO TEST DTO FIELDS HERE FOR TEST CREATE
			testCrudOrg_testDTOFieldsForTestCreateUpdate (expected, actual);
			
			new AttributeTester().check(expected.getAttributes(), actual.getAttributes());
			new MetaTester().checkAfterCreate(actual.getMeta());
			
			// -------------------------------------
			// test read
			// -------------------------------------
			expected = actual;
			actual = testService.getOrg ( actual.getId(), contextInfo);
			assertEquals(expected.getId(), actual.getId());
			check(expected, actual);
			
			// INSERT CODE FOR TESTING MORE DTO FIELDS HERE
			testCrudOrg_testDTOFieldsForTestCreateUpdate (expected, actual);
			
			new AttributeTester().check(expected.getAttributes(), actual.getAttributes());
			new MetaTester().checkAfterGet(expected.getMeta(), actual.getMeta());
			
			// -------------------------------------
			// test update
			// -------------------------------------
			expected = actual;
			
			expected.setStateKey(expected.getState() + "_Updated");
			
			// METHOD TO INSERT CODE TO UPDATE DTO FIELDS HERE
			testCrudOrg_setDTOFieldsForTestUpdate (expected);
			
			new AttributeTester().delete1Update1Add1ForUpdate(expected.getAttributes());
			// code to update
			actual = testService.updateOrg ( expected.getId(), expected, contextInfo);
			
			assertEquals(expected.getId(), actual.getId());
			check(expected, actual);
			
			// METHOD TO INSERT CODE FOR TESTING DTO FIELDS HERE
			testCrudOrg_testDTOFieldsForTestCreateUpdate (expected, actual);
			
			new AttributeTester().check(expected.getAttributes(), actual.getAttributes());
			new MetaTester().checkAfterUpdate(expected.getMeta(), actual.getMeta());
			
			// -------------------------------------
			// test read after update
			// -------------------------------------
			
			expected = actual;
			// code to get actual
			actual = testService.getOrg ( actual.getId(), contextInfo);
			
			assertEquals(expected.getId(), actual.getId());
			check(expected, actual);
			
			// INSERT METHOD CODE FOR TESTING DTO FIELDS HERE
			testCrudOrg_testDTOFieldsForTestReadAfterUpdate (expected, actual);
			
			new AttributeTester().check(expected.getAttributes(), actual.getAttributes());
			new MetaTester().checkAfterGet(expected.getMeta(), actual.getMeta());
			
			OrgInfo alphaDTO = actual;
			
			// create a 2nd DTO
			OrgInfo betaDTO = new OrgInfo ();
			
			// METHOD TO INSERT CODE TO SET MORE DTO FIELDS HERE
			testCrudOrg_setDTOFieldsForTestReadAfterUpdate (betaDTO);
			
			betaDTO.setTypeKey("typeKeyBeta");
			betaDTO.setStateKey("stateKeyBeta");
			betaDTO = testService.createOrg ( betaDTO.getTypeKey(), betaDTO, contextInfo);
			
			// -------------------------------------
			// test bulk get with no ids supplied
			// -------------------------------------
			
			List<String> orgIds = new ArrayList<String>();
			// code to get DTO by Ids
			List<OrgInfo> records = testService.getOrgsByIds ( orgIds, contextInfo);
			
			assertEquals(orgIds.size(), records.size());
			assertEquals(0, orgIds.size());
			
			// -------------------------------------
			// test bulk get
			// -------------------------------------
			orgIds = new ArrayList<String>();
			orgIds.add(alphaDTO.getId());
			orgIds.add(betaDTO.getId());
			// code to get DTO by Ids
			records = testService.getOrgsByIds ( orgIds, contextInfo);
			
			assertEquals(orgIds.size(), records.size());
			for (OrgInfo record : records)
			{
					if (!orgIds.remove(record.getId()))
					{
							fail(record.getId());
					}
			}
			assertEquals(0, orgIds.size());
			
			// -------------------------------------
			// test get by type
			// -------------------------------------
			// code to get by specific type "typeKey01" 
			orgIds = testService.getOrgIdsByType ("typeKey01", contextInfo);
			
			assertEquals(1, orgIds.size());
			assertEquals(alphaDTO.getId(), orgIds.get(0));
			
			// test get by other type
			// code to get by specific type "typeKeyBeta" 
			orgIds = testService.getOrgIdsByType ("typeKeyBeta", contextInfo);
			
			assertEquals(1, orgIds.size());
			assertEquals(betaDTO.getId(), orgIds.get(0));
			
			// -------------------------------------
			// test delete
			// -------------------------------------
			
			StatusInfo status = testService.deleteOrg ( actual.getId(), contextInfo);
			
			assertNotNull(status);
			assertTrue(status.getIsSuccess());
			try
			{
					OrgInfo record = testService.getOrg ( actual.getId(), contextInfo);
					fail("Did not receive DoesNotExistException when attempting to get already-deleted entity");
			}
			catch (DoesNotExistException dnee)
			{
					// expected
			}
			
	}
	
	/*
		A method to set the fields for a Org in a 'test create' section prior to calling the 'create' operation.
	*/
	public abstract void testCrudOrg_setDTOFieldsForTestCreate(OrgInfo expected);
	
	/*
		A method to test the fields for a Org. This is called after:
		- creating a DTO, where actual is the DTO returned by the create operation, and expected is the dto passed in to the create operation
		- reading a DTO after creating it, and actual is the read DTO, and expected is the dto that was created
		- updating a DTO, where actual is DTO returned by the update operation, and expected is the dto that was passed in to the update operation
	*/
	public abstract void testCrudOrg_testDTOFieldsForTestCreateUpdate(OrgInfo expected, OrgInfo actual);
	
	/*
		A method to set the fields for a Org in a 'test update' section prior to calling the 'update' operation.
	*/
	public abstract void testCrudOrg_setDTOFieldsForTestUpdate(OrgInfo expected);
	
	/*
		A method to test the fields for a Org after an update operation, followed by a read operation,
		where actual is the DTO returned by the read operation, and expected is the dto returned by the update operation.
	*/
	public abstract void testCrudOrg_testDTOFieldsForTestReadAfterUpdate(OrgInfo expected, OrgInfo actual);
	
	/*
		A method to set the fields for a Org in the 'test read after update' section.
		This dto is another (second) dto object being created for other tests.
	*/
	public abstract void testCrudOrg_setDTOFieldsForTestReadAfterUpdate(OrgInfo expected);
	
	
	// ****************************************************
	//           OrgOrgRelationInfo
	// ****************************************************
	@Test
	public void testCrudOrgOrgRelation() 
		throws DataValidationErrorException,
			DoesNotExistException,
			InvalidParameterException,
			MissingParameterException,
			OperationFailedException,
			PermissionDeniedException,
			ReadOnlyException,
			VersionMismatchException,
			DependentObjectsExistException
	{
			// -------------------------------------
			// test create
			// -------------------------------------
			OrgOrgRelationInfo expected = new OrgOrgRelationInfo ();
			
			// METHOD TO SET DTO FIELDS HERE FOR TEST CREATE
			testCrudOrgOrgRelation_setDTOFieldsForTestCreate (expected);
			
			new AttributeTester().add2ForCreate(expected.getAttributes());
			
			// code to create actual
			OrgOrgRelationInfo actual = testService.createOrgOrgRelation (expected.getOrgId(), expected.getRelatedOrgId(), expected.getTypeKey(), expected, contextInfo);
			
			assertNotNull(actual.getId());
			check(expected, actual);
			
			// METHOD TO TEST DTO FIELDS HERE FOR TEST CREATE
			testCrudOrgOrgRelation_testDTOFieldsForTestCreateUpdate (expected, actual);
			
			new AttributeTester().check(expected.getAttributes(), actual.getAttributes());
			new MetaTester().checkAfterCreate(actual.getMeta());
			
			// -------------------------------------
			// test read
			// -------------------------------------
			expected = actual;
			actual = testService.getOrgOrgRelation ( actual.getId(), contextInfo);
			assertEquals(expected.getId(), actual.getId());
			check(expected, actual);
			
			// INSERT CODE FOR TESTING MORE DTO FIELDS HERE
			testCrudOrgOrgRelation_testDTOFieldsForTestCreateUpdate (expected, actual);
			
			new AttributeTester().check(expected.getAttributes(), actual.getAttributes());
			new MetaTester().checkAfterGet(expected.getMeta(), actual.getMeta());
			
			// -------------------------------------
			// test update
			// -------------------------------------
			expected = actual;
			
			expected.setStateKey(expected.getState() + "_Updated");
			
			// METHOD TO INSERT CODE TO UPDATE DTO FIELDS HERE
			testCrudOrgOrgRelation_setDTOFieldsForTestUpdate (expected);
			
			new AttributeTester().delete1Update1Add1ForUpdate(expected.getAttributes());
			// code to update
			actual = testService.updateOrgOrgRelation ( expected.getId(), expected, contextInfo);
			
			assertEquals(expected.getId(), actual.getId());
			check(expected, actual);
			
			// METHOD TO INSERT CODE FOR TESTING DTO FIELDS HERE
			testCrudOrgOrgRelation_testDTOFieldsForTestCreateUpdate (expected, actual);
			
			new AttributeTester().check(expected.getAttributes(), actual.getAttributes());
			new MetaTester().checkAfterUpdate(expected.getMeta(), actual.getMeta());
			
			// -------------------------------------
			// test read after update
			// -------------------------------------
			
			expected = actual;
			// code to get actual
			actual = testService.getOrgOrgRelation ( actual.getId(), contextInfo);
			
			assertEquals(expected.getId(), actual.getId());
			check(expected, actual);
			
			// INSERT METHOD CODE FOR TESTING DTO FIELDS HERE
			testCrudOrgOrgRelation_testDTOFieldsForTestReadAfterUpdate (expected, actual);
			
			new AttributeTester().check(expected.getAttributes(), actual.getAttributes());
			new MetaTester().checkAfterGet(expected.getMeta(), actual.getMeta());
			
			OrgOrgRelationInfo alphaDTO = actual;
			
			// create a 2nd DTO
			OrgOrgRelationInfo betaDTO = new OrgOrgRelationInfo ();
			
			// METHOD TO INSERT CODE TO SET MORE DTO FIELDS HERE
			testCrudOrgOrgRelation_setDTOFieldsForTestReadAfterUpdate (betaDTO);
			
			betaDTO.setTypeKey("typeKeyBeta");
			betaDTO.setStateKey("stateKeyBeta");
			betaDTO = testService.createOrgOrgRelation (betaDTO.getOrgId(), betaDTO.getRelatedOrgId(), betaDTO.getTypeKey(), betaDTO, contextInfo);
			
			// -------------------------------------
			// test bulk get with no ids supplied
			// -------------------------------------
			
			List<String> orgOrgRelationIds = new ArrayList<String>();
			// code to get DTO by Ids
			List<OrgOrgRelationInfo> records = testService.getOrgOrgRelationsByIds ( orgOrgRelationIds, contextInfo);
			
			assertEquals(orgOrgRelationIds.size(), records.size());
			assertEquals(0, orgOrgRelationIds.size());
			
			// -------------------------------------
			// test bulk get
			// -------------------------------------
			orgOrgRelationIds = new ArrayList<String>();
			orgOrgRelationIds.add(alphaDTO.getId());
			orgOrgRelationIds.add(betaDTO.getId());
			// code to get DTO by Ids
			records = testService.getOrgOrgRelationsByIds ( orgOrgRelationIds, contextInfo);
			
			assertEquals(orgOrgRelationIds.size(), records.size());
			for (OrgOrgRelationInfo record : records)
			{
					if (!orgOrgRelationIds.remove(record.getId()))
					{
							fail(record.getId());
					}
			}
			assertEquals(0, orgOrgRelationIds.size());
			
			// -------------------------------------
			// test get by type
			// -------------------------------------
			// code to get by specific type "typeKey01" 
			orgOrgRelationIds = testService.getOrgOrgRelationIdsByType ("typeKey01", contextInfo);
			
			assertEquals(1, orgOrgRelationIds.size());
			assertEquals(alphaDTO.getId(), orgOrgRelationIds.get(0));
			
			// test get by other type
			// code to get by specific type "typeKeyBeta" 
			orgOrgRelationIds = testService.getOrgOrgRelationIdsByType ("typeKeyBeta", contextInfo);
			
			assertEquals(1, orgOrgRelationIds.size());
			assertEquals(betaDTO.getId(), orgOrgRelationIds.get(0));
			
			// -------------------------------------
			// test delete
			// -------------------------------------
			
			StatusInfo status = testService.deleteOrgOrgRelation ( actual.getId(), contextInfo);
			
			assertNotNull(status);
			assertTrue(status.getIsSuccess());
			try
			{
					OrgOrgRelationInfo record = testService.getOrgOrgRelation ( actual.getId(), contextInfo);
					fail("Did not receive DoesNotExistException when attempting to get already-deleted entity");
			}
			catch (DoesNotExistException dnee)
			{
					// expected
			}
			
	}
	
	/*
		A method to set the fields for a OrgOrgRelation in a 'test create' section prior to calling the 'create' operation.
	*/
	public abstract void testCrudOrgOrgRelation_setDTOFieldsForTestCreate(OrgOrgRelationInfo expected);
	
	/*
		A method to test the fields for a OrgOrgRelation. This is called after:
		- creating a DTO, where actual is the DTO returned by the create operation, and expected is the dto passed in to the create operation
		- reading a DTO after creating it, and actual is the read DTO, and expected is the dto that was created
		- updating a DTO, where actual is DTO returned by the update operation, and expected is the dto that was passed in to the update operation
	*/
	public abstract void testCrudOrgOrgRelation_testDTOFieldsForTestCreateUpdate(OrgOrgRelationInfo expected, OrgOrgRelationInfo actual);
	
	/*
		A method to set the fields for a OrgOrgRelation in a 'test update' section prior to calling the 'update' operation.
	*/
	public abstract void testCrudOrgOrgRelation_setDTOFieldsForTestUpdate(OrgOrgRelationInfo expected);
	
	/*
		A method to test the fields for a OrgOrgRelation after an update operation, followed by a read operation,
		where actual is the DTO returned by the read operation, and expected is the dto returned by the update operation.
	*/
	public abstract void testCrudOrgOrgRelation_testDTOFieldsForTestReadAfterUpdate(OrgOrgRelationInfo expected, OrgOrgRelationInfo actual);
	
	/*
		A method to set the fields for a OrgOrgRelation in the 'test read after update' section.
		This dto is another (second) dto object being created for other tests.
	*/
	public abstract void testCrudOrgOrgRelation_setDTOFieldsForTestReadAfterUpdate(OrgOrgRelationInfo expected);
	
	
	// ****************************************************
	//           OrgPersonRelationInfo
	// ****************************************************
	@Test
	public void testCrudOrgPersonRelation() 
		throws DataValidationErrorException,
			DoesNotExistException,
			InvalidParameterException,
			MissingParameterException,
			OperationFailedException,
			PermissionDeniedException,
			ReadOnlyException,
			VersionMismatchException,
			DependentObjectsExistException
	{
			// -------------------------------------
			// test create
			// -------------------------------------
			OrgPersonRelationInfo expected = new OrgPersonRelationInfo ();
			
			// METHOD TO SET DTO FIELDS HERE FOR TEST CREATE
			testCrudOrgPersonRelation_setDTOFieldsForTestCreate (expected);
			
			new AttributeTester().add2ForCreate(expected.getAttributes());
			
			// code to create actual
			OrgPersonRelationInfo actual = testService.createOrgPersonRelation (expected.getOrgId(), expected.getPersonId(), expected.getTypeKey(), expected, contextInfo);
			
			assertNotNull(actual.getId());
			check(expected, actual);
			
			// METHOD TO TEST DTO FIELDS HERE FOR TEST CREATE
			testCrudOrgPersonRelation_testDTOFieldsForTestCreateUpdate (expected, actual);
			
			new AttributeTester().check(expected.getAttributes(), actual.getAttributes());
			new MetaTester().checkAfterCreate(actual.getMeta());
			
			// -------------------------------------
			// test read
			// -------------------------------------
			expected = actual;
			actual = testService.getOrgPersonRelation ( actual.getId(), contextInfo);
			assertEquals(expected.getId(), actual.getId());
			check(expected, actual);
			
			// INSERT CODE FOR TESTING MORE DTO FIELDS HERE
			testCrudOrgPersonRelation_testDTOFieldsForTestCreateUpdate (expected, actual);
			
			new AttributeTester().check(expected.getAttributes(), actual.getAttributes());
			new MetaTester().checkAfterGet(expected.getMeta(), actual.getMeta());
			
			// -------------------------------------
			// test update
			// -------------------------------------
			expected = actual;
			
			expected.setStateKey(expected.getState() + "_Updated");
			
			// METHOD TO INSERT CODE TO UPDATE DTO FIELDS HERE
			testCrudOrgPersonRelation_setDTOFieldsForTestUpdate (expected);
			
			new AttributeTester().delete1Update1Add1ForUpdate(expected.getAttributes());
			// code to update
			actual = testService.updateOrgPersonRelation (expected.getId(), expected, contextInfo);
			
			assertEquals(expected.getId(), actual.getId());
			check(expected, actual);
			
			// METHOD TO INSERT CODE FOR TESTING DTO FIELDS HERE
			testCrudOrgPersonRelation_testDTOFieldsForTestCreateUpdate (expected, actual);
			
			new AttributeTester().check(expected.getAttributes(), actual.getAttributes());
			new MetaTester().checkAfterUpdate(expected.getMeta(), actual.getMeta());
			
			// -------------------------------------
			// test read after update
			// -------------------------------------
			
			expected = actual;
			// code to get actual
			actual = testService.getOrgPersonRelation ( actual.getId(), contextInfo);
			
			assertEquals(expected.getId(), actual.getId());
			check(expected, actual);
			
			// INSERT METHOD CODE FOR TESTING DTO FIELDS HERE
			testCrudOrgPersonRelation_testDTOFieldsForTestReadAfterUpdate (expected, actual);
			
			new AttributeTester().check(expected.getAttributes(), actual.getAttributes());
			new MetaTester().checkAfterGet(expected.getMeta(), actual.getMeta());
			
			OrgPersonRelationInfo alphaDTO = actual;
			
			// create a 2nd DTO
			OrgPersonRelationInfo betaDTO = new OrgPersonRelationInfo ();
			
			// METHOD TO INSERT CODE TO SET MORE DTO FIELDS HERE
			testCrudOrgPersonRelation_setDTOFieldsForTestReadAfterUpdate (betaDTO);
			
			betaDTO.setTypeKey("typeKeyBeta");
			betaDTO.setStateKey("stateKeyBeta");
			betaDTO = testService.createOrgPersonRelation (betaDTO.getOrgId(), betaDTO.getPersonId(), betaDTO.getTypeKey(), betaDTO, contextInfo);
			
			// -------------------------------------
			// test bulk get with no ids supplied
			// -------------------------------------
			
			List<String> orgPersonRelationIds = new ArrayList<String>();
			// code to get DTO by Ids
			List<OrgPersonRelationInfo> records = testService.getOrgPersonRelationsByIds ( orgPersonRelationIds, contextInfo);
			
			assertEquals(orgPersonRelationIds.size(), records.size());
			assertEquals(0, orgPersonRelationIds.size());
			
			// -------------------------------------
			// test bulk get
			// -------------------------------------
			orgPersonRelationIds = new ArrayList<String>();
			orgPersonRelationIds.add(alphaDTO.getId());
			orgPersonRelationIds.add(betaDTO.getId());
			// code to get DTO by Ids
			records = testService.getOrgPersonRelationsByIds ( orgPersonRelationIds, contextInfo);
			
			assertEquals(orgPersonRelationIds.size(), records.size());
			for (OrgPersonRelationInfo record : records)
			{
					if (!orgPersonRelationIds.remove(record.getId()))
					{
							fail(record.getId());
					}
			}
			assertEquals(0, orgPersonRelationIds.size());
			
			// -------------------------------------
			// test get by type
			// -------------------------------------
			// code to get by specific type "typeKey01" 
			orgPersonRelationIds = testService.getOrgPersonRelationIdsByType ("typeKey01", contextInfo);
			
			assertEquals(1, orgPersonRelationIds.size());
			assertEquals(alphaDTO.getId(), orgPersonRelationIds.get(0));
			
			// test get by other type
			// code to get by specific type "typeKeyBeta" 
			orgPersonRelationIds = testService.getOrgPersonRelationIdsByType ("typeKeyBeta", contextInfo);
			
			assertEquals(1, orgPersonRelationIds.size());
			assertEquals(betaDTO.getId(), orgPersonRelationIds.get(0));
			
			// -------------------------------------
			// test delete
			// -------------------------------------
			
			StatusInfo status = testService.deleteOrgPersonRelation ( actual.getId(), contextInfo);
			
			assertNotNull(status);
			assertTrue(status.getIsSuccess());
			try
			{
					OrgPersonRelationInfo record = testService.getOrgPersonRelation ( actual.getId(), contextInfo);
					fail("Did not receive DoesNotExistException when attempting to get already-deleted entity");
			}
			catch (DoesNotExistException dnee)
			{
					// expected
			}
			
	}
	
	/*
		A method to set the fields for a OrgPersonRelation in a 'test create' section prior to calling the 'create' operation.
	*/
	public abstract void testCrudOrgPersonRelation_setDTOFieldsForTestCreate(OrgPersonRelationInfo expected);
	
	/*
		A method to test the fields for a OrgPersonRelation. This is called after:
		- creating a DTO, where actual is the DTO returned by the create operation, and expected is the dto passed in to the create operation
		- reading a DTO after creating it, and actual is the read DTO, and expected is the dto that was created
		- updating a DTO, where actual is DTO returned by the update operation, and expected is the dto that was passed in to the update operation
	*/
	public abstract void testCrudOrgPersonRelation_testDTOFieldsForTestCreateUpdate(OrgPersonRelationInfo expected, OrgPersonRelationInfo actual);
	
	/*
		A method to set the fields for a OrgPersonRelation in a 'test update' section prior to calling the 'update' operation.
	*/
	public abstract void testCrudOrgPersonRelation_setDTOFieldsForTestUpdate(OrgPersonRelationInfo expected);
	
	/*
		A method to test the fields for a OrgPersonRelation after an update operation, followed by a read operation,
		where actual is the DTO returned by the read operation, and expected is the dto returned by the update operation.
	*/
	public abstract void testCrudOrgPersonRelation_testDTOFieldsForTestReadAfterUpdate(OrgPersonRelationInfo expected, OrgPersonRelationInfo actual);
	
	/*
		A method to set the fields for a OrgPersonRelation in the 'test read after update' section.
		This dto is another (second) dto object being created for other tests.
	*/
	public abstract void testCrudOrgPersonRelation_setDTOFieldsForTestReadAfterUpdate(OrgPersonRelationInfo expected);
	
	
	// ****************************************************
	//           OrgPositionRestrictionInfo
	// ****************************************************
	@Test
	public void testCrudOrgPositionRestriction()
            throws DataValidationErrorException,
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException,
            ReadOnlyException,
            VersionMismatchException,
            DependentObjectsExistException,
            AlreadyExistsException {
			// -------------------------------------
			// test create
			// -------------------------------------
			OrgPositionRestrictionInfo expected = new OrgPositionRestrictionInfo ();
			
			// METHOD TO SET DTO FIELDS HERE FOR TEST CREATE
			testCrudOrgPositionRestriction_setDTOFieldsForTestCreate (expected);
			
			new AttributeTester().add2ForCreate(expected.getAttributes());
			
			// code to create actual
			OrgPositionRestrictionInfo actual = testService.createOrgPositionRestriction (expected.getOrgId(), expected.getOrgPersonRelationTypeKey(), expected, contextInfo);
			
			assertNotNull(actual.getId());
			
			// METHOD TO TEST DTO FIELDS HERE FOR TEST CREATE
			testCrudOrgPositionRestriction_testDTOFieldsForTestCreateUpdate(expected, actual);
			
			new AttributeTester().check(expected.getAttributes(), actual.getAttributes());
			new MetaTester().checkAfterCreate(actual.getMeta());
			
			// -------------------------------------
			// test read
			// -------------------------------------
			expected = actual;
			actual = testService.getOrgPositionRestriction ( actual.getId(), contextInfo);
			assertEquals(expected.getId(), actual.getId());
			
			// INSERT CODE FOR TESTING MORE DTO FIELDS HERE
			testCrudOrgPositionRestriction_testDTOFieldsForTestCreateUpdate (expected, actual);
			
			new AttributeTester().check(expected.getAttributes(), actual.getAttributes());
			new MetaTester().checkAfterGet(expected.getMeta(), actual.getMeta());
			
			// -------------------------------------
			// test update
			// -------------------------------------
			expected = actual;
			
			
			// METHOD TO INSERT CODE TO UPDATE DTO FIELDS HERE
			testCrudOrgPositionRestriction_setDTOFieldsForTestUpdate (expected);
			
			new AttributeTester().delete1Update1Add1ForUpdate(expected.getAttributes());
			// code to update
			actual = testService.updateOrgPositionRestriction ( expected.getId(), expected, contextInfo);
			
			assertEquals(expected.getId(), actual.getId());
			
			// METHOD TO INSERT CODE FOR TESTING DTO FIELDS HERE
			testCrudOrgPositionRestriction_testDTOFieldsForTestCreateUpdate (expected, actual);
			
			new AttributeTester().check(expected.getAttributes(), actual.getAttributes());
			new MetaTester().checkAfterUpdate(expected.getMeta(), actual.getMeta());
			
			// -------------------------------------
			// test read after update
			// -------------------------------------
			
			expected = actual;
			// code to get actual
			actual = testService.getOrgPositionRestriction ( actual.getId(), contextInfo);
			
			assertEquals(expected.getId(), actual.getId());
			
			// INSERT METHOD CODE FOR TESTING DTO FIELDS HERE
			testCrudOrgPositionRestriction_testDTOFieldsForTestReadAfterUpdate (expected, actual);
			
			new AttributeTester().check(expected.getAttributes(), actual.getAttributes());
			new MetaTester().checkAfterGet(expected.getMeta(), actual.getMeta());
			
			OrgPositionRestrictionInfo alphaDTO = actual;
			
			// create a 2nd DTO
			OrgPositionRestrictionInfo betaDTO = new OrgPositionRestrictionInfo ();
			
			// METHOD TO INSERT CODE TO SET MORE DTO FIELDS HERE
			testCrudOrgPositionRestriction_setDTOFieldsForTestReadAfterUpdate (betaDTO);
			
			betaDTO = testService.createOrgPositionRestriction (betaDTO.getOrgId(), betaDTO.getOrgPersonRelationTypeKey(), betaDTO, contextInfo);
			
			// -------------------------------------
			// test bulk get with no ids supplied
			// -------------------------------------
			
			List<String> orgPositionRestrictionIds = new ArrayList<String>();
			// code to get DTO by Ids
			List<OrgPositionRestrictionInfo> records = testService.getOrgPositionRestrictionsByIds ( orgPositionRestrictionIds, contextInfo);
			
			assertEquals(orgPositionRestrictionIds.size(), records.size());
			assertEquals(0, orgPositionRestrictionIds.size());
			
			// -------------------------------------
			// test bulk get
			// -------------------------------------
			orgPositionRestrictionIds = new ArrayList<String>();
			orgPositionRestrictionIds.add(alphaDTO.getId());
			orgPositionRestrictionIds.add(betaDTO.getId());
			// code to get DTO by Ids
			records = testService.getOrgPositionRestrictionsByIds ( orgPositionRestrictionIds, contextInfo);
			
			assertEquals(orgPositionRestrictionIds.size(), records.size());
			for (OrgPositionRestrictionInfo record : records)
			{
					if (!orgPositionRestrictionIds.remove(record.getId()))
					{
							fail(record.getId());
					}
			}
			assertEquals(0, orgPositionRestrictionIds.size());
			
			// -------------------------------------
			// test get by type
			// -------------------------------------
			// code to get by specific type "typeKey01" 
			orgPositionRestrictionIds = testService.getOrgPositionRestrictionIdsByType ("orgPersonRelationTypeKey01", contextInfo);
			
			assertEquals(1, orgPositionRestrictionIds.size());
			assertEquals(alphaDTO.getId(), orgPositionRestrictionIds.get(0));
			
			// -------------------------------------
			// test delete
			// -------------------------------------
			
			StatusInfo status = testService.deleteOrgPositionRestriction ( actual.getId(), contextInfo);
			
			assertNotNull(status);
			assertTrue(status.getIsSuccess());
			try
			{
					OrgPositionRestrictionInfo record = testService.getOrgPositionRestriction ( actual.getId(), contextInfo);
					fail("Did not receive DoesNotExistException when attempting to get already-deleted entity");
			}
			catch (DoesNotExistException dnee)
			{
					// expected
			}
			
	}
	
	/*
		A method to set the fields for a OrgPositionRestriction in a 'test create' section prior to calling the 'create' operation.
	*/
	public abstract void testCrudOrgPositionRestriction_setDTOFieldsForTestCreate(OrgPositionRestrictionInfo expected);
	
	/*
		A method to test the fields for a OrgPositionRestriction. This is called after:
		- creating a DTO, where actual is the DTO returned by the create operation, and expected is the dto passed in to the create operation
		- reading a DTO after creating it, and actual is the read DTO, and expected is the dto that was created
		- updating a DTO, where actual is DTO returned by the update operation, and expected is the dto that was passed in to the update operation
	*/
	public abstract void testCrudOrgPositionRestriction_testDTOFieldsForTestCreateUpdate(OrgPositionRestrictionInfo expected, OrgPositionRestrictionInfo actual);
	
	/*
		A method to set the fields for a OrgPositionRestriction in a 'test update' section prior to calling the 'update' operation.
	*/
	public abstract void testCrudOrgPositionRestriction_setDTOFieldsForTestUpdate(OrgPositionRestrictionInfo expected);
	
	/*
		A method to test the fields for a OrgPositionRestriction after an update operation, followed by a read operation,
		where actual is the DTO returned by the read operation, and expected is the dto returned by the update operation.
	*/
	public abstract void testCrudOrgPositionRestriction_testDTOFieldsForTestReadAfterUpdate(OrgPositionRestrictionInfo expected, OrgPositionRestrictionInfo actual);
	
	/*
		A method to set the fields for a OrgPositionRestriction in the 'test read after update' section.
		This dto is another (second) dto object being created for other tests.
	*/
	public abstract void testCrudOrgPositionRestriction_setDTOFieldsForTestReadAfterUpdate(OrgPositionRestrictionInfo expected);
	
	
	// ========================================
	// SERVICE OPS TESTED IN BASE TEST CLASS
	// ========================================
	
	/*
		The following methods are tested as part of CRUD operations for this service's DTOs:
			getSearchTypes
			getSearchType
			getOrgHierarchy
			getOrgHierarchiesByIds
			getOrgHierarchyIdsByType
			createOrgHierarchy
			updateOrgHierarchy
			deleteOrgHierarchy
			getOrgTypes
			getOrg
			getOrgsByIds
			getOrgIdsByType
			createOrg
			updateOrg
			deleteOrg
			getOrgOrgRelationTypes
			getOrgOrgRelationTypesForOrgType
			getOrgOrgRelationTypesForOrgHierarchy
			getOrgOrgRelation
			getOrgOrgRelationsByIds
			getOrgOrgRelationIdsByType
			createOrgOrgRelation
			updateOrgOrgRelation
			deleteOrgOrgRelation
			getOrgPersonRelationTypes
			getOrgPersonRelationTypesForOrgType
			getOrgPersonRelation
			getOrgPersonRelationsByIds
			getOrgPersonRelationIdsByType
			createOrgPersonRelation
			updateOrgPersonRelation
			deleteOrgPersonRelation
			getOrgPositionRestriction
			getOrgPositionRestrictionsByIds
			getOrgPositionRestrictionIdsByType
			createOrgPositionRestriction
			updateOrgPositionRestriction
			deleteOrgPositionRestriction
	*/
	
	// ========================================
	// SERVICE OPS NOT TESTED IN BASE TEST CLASS
	// ========================================
	
	/* Method Name: getOrgHierarchies */
	@Test
	public abstract void test_getOrgHierarchies() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;
	
	/* Method Name: searchForOrgHierarchyIds */
	@Test
	public abstract void test_searchForOrgHierarchyIds() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;
	
	/* Method Name: searchForOrgHierarchies */
	@Test
	public abstract void test_searchForOrgHierarchies() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;
	
	/* Method Name: validateOrgHierarchy */
	@Test
	public abstract void test_validateOrgHierarchy() 
	throws 	DoesNotExistException	,InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;
	
	/* Method Name: searchForOrgIds */
	@Test
	public abstract void test_searchForOrgIds() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;
	
	/* Method Name: searchForOrgs */
	@Test
	public abstract void test_searchForOrgs() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;
	
	/* Method Name: validateOrg */
	@Test
	public abstract void test_validateOrg() 
	throws 	DoesNotExistException	,InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;
	
	/* Method Name: hasOrgOrgRelation */
	@Test
	public abstract void test_hasOrgOrgRelation() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;
	
	/* Method Name: getOrgOrgRelationsByOrg */
	@Test
	public abstract void test_getOrgOrgRelationsByOrg() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;
	
	/* Method Name: getOrgOrgRelationsByOrgs */
	@Test
	public abstract void test_getOrgOrgRelationsByOrgs() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;
	
	/* Method Name: getOrgOrgRelationsByTypeAndOrg */
	@Test
	public abstract void test_getOrgOrgRelationsByTypeAndOrg() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;
	
	/* Method Name: getOrgOrgRelationsByTypeAndRelatedOrg */
	@Test
	public abstract void test_getOrgOrgRelationsByTypeAndRelatedOrg() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;
	
	/* Method Name: searchForOrgOrgRelationIds */
	@Test
	public abstract void test_searchForOrgOrgRelationIds() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;
	
	/* Method Name: searchForOrgOrgRelations */
	@Test
	public abstract void test_searchForOrgOrgRelations() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;
	
	/* Method Name: validateOrgOrgRelation */
	@Test
	public abstract void test_validateOrgOrgRelation() 
	throws 	DoesNotExistException	,InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;
	
	/* Method Name: hasOrgPersonRelation */
	@Test
	public abstract void test_hasOrgPersonRelation() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;
	
	/* Method Name: getOrgPersonRelationsByOrg */
	@Test
	public abstract void test_getOrgPersonRelationsByOrg() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;
	
	/* Method Name: getOrgPersonRelationsByTypeAndOrg */
	@Test
	public abstract void test_getOrgPersonRelationsByTypeAndOrg() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;
	
	/* Method Name: getOrgPersonRelationsByPerson */
	@Test
	public abstract void test_getOrgPersonRelationsByPerson() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;
	
	/* Method Name: getOrgPersonRelationsByTypeAndPerson */
	@Test
	public abstract void test_getOrgPersonRelationsByTypeAndPerson() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;
	
	/* Method Name: getOrgPersonRelationsByOrgAndPerson */
	@Test
	public abstract void test_getOrgPersonRelationsByOrgAndPerson() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;
	
	/* Method Name: getOrgPersonRelationsByTypeAndOrgAndPerson */
	@Test
	public abstract void test_getOrgPersonRelationsByTypeAndOrgAndPerson() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;
	
	/* Method Name: searchForOrgPersonRelationIds */
	@Test
	public abstract void test_searchForOrgPersonRelationIds() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;
	
	/* Method Name: searchForOrgPersonRelations */
	@Test
	public abstract void test_searchForOrgPersonRelations() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;
	
	/* Method Name: validateOrgPersonRelation */
	@Test
	public abstract void test_validateOrgPersonRelation() 
	throws 	DoesNotExistException	,InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;
	
	/* Method Name: getOrgPositionRestrictionIdsByOrg */
	@Test
	public abstract void test_getOrgPositionRestrictionIdsByOrg() 
	throws 	DoesNotExistException	,InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;
	
	/* Method Name: searchForOrgPositionRestrictionIds */
	@Test
	public abstract void test_searchForOrgPositionRestrictionIds() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;
	
	/* Method Name: searchForOrgPositionRestrictions */
	@Test
	public abstract void test_searchForOrgPositionRestrictions() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;
	
	/* Method Name: validateOrgPositionRestriction */
	@Test
	public abstract void test_validateOrgPositionRestriction() 
	throws 	DoesNotExistException	,InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;
	
	/* Method Name: isDescendant */
	@Test
	public abstract void test_isDescendant() 
	throws 	InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;
	
	/* Method Name: getAllDescendants */
	@Test
	public abstract void test_getAllDescendants() 
	throws 	DoesNotExistException	,InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;
	
	/* Method Name: getAllAncestors */
	@Test
	public abstract void test_getAllAncestors() 
	throws 	DoesNotExistException	,InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;
	
	/* Method Name: getOrgTree */
	@Test
	public abstract void test_getOrgTree() 
	throws 	DoesNotExistException	,InvalidParameterException	,MissingParameterException	,OperationFailedException	,PermissionDeniedException	;

    public void check(IdNamelessEntity expected, IdNamelessEntity actual) {
        assertEquals(expected.getTypeKey(), actual.getTypeKey());
        assertEquals(expected.getStateKey(), actual.getStateKey());
    }

    public void check(RelationshipInfo expected, RelationshipInfo actual) {
        assertEquals(expected.getTypeKey(), actual.getTypeKey());
        assertEquals(expected.getStateKey(), actual.getStateKey());
        assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
        assertEquals(expected.getExpirationDate(), actual.getExpirationDate());
    }
}


