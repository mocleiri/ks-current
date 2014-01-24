/*
 * Copyright 2013 The Kuali Foundation
 *
 * Licensed under the the Educational Community License, Version 1.0
 * (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.student.r2.core.organization.service;

import org.kuali.student.common.test.mock.data.AbstractMockServicesAwareDataLoader;
import org.kuali.student.common.test.spring.log4j.KSLog4JConfigurer;
import org.kuali.student.common.test.util.AttributeTester;
import org.kuali.student.r2.common.dto.AttributeInfo;
import org.kuali.student.r2.common.dto.RichTextInfo;
import org.kuali.student.r2.common.dto.TimeAmountInfo;
import org.kuali.student.r2.common.exceptions.AlreadyExistsException;
import org.kuali.student.r2.common.exceptions.DataValidationErrorException;
import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.exceptions.PermissionDeniedException;
import org.kuali.student.r2.common.exceptions.ReadOnlyException;
import org.kuali.student.r2.core.constants.AtpServiceConstants;
import org.kuali.student.r2.core.constants.OrganizationServiceConstants;
import org.kuali.student.r2.core.organization.dto.OrgHierarchyInfo;
import org.kuali.student.r2.core.organization.dto.OrgInfo;
import org.kuali.student.r2.core.organization.dto.OrgOrgRelationInfo;
import org.kuali.student.r2.core.organization.dto.OrgPersonRelationInfo;
import org.kuali.student.r2.core.organization.dto.OrgPositionRestrictionInfo;
import org.kuali.student.r2.core.organization.service.impl.OrganizationServiceMockImpl;
import org.slf4j.Logger;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
/**
 * @author Kuali Student Team
 */
public class OrganizationServiceDataLoader extends AbstractMockServicesAwareDataLoader {


    private static final Logger LOGGER = KSLog4JConfigurer.getLogger(OrganizationServiceDataLoader.class);

    @Resource(name = "organizationService")
    private OrganizationService orgService = new OrganizationServiceMockImpl();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    public static final String ACTIVE_STATE = "kuali.org.active";

    public static final String ORG_RELATION_ACAD_PARENT_TYPE = "kuali.organization.relation.type.acad.parent";
    public static final String ORG_RELATION_ACAD_COLLABORATES_WITH_TYPE = "kuali.organization.relation.type.acad.collaborates";
    public static final String ORG_RELATION_FIN_PARENT_TYPE = "kuali.organization.relation.type.fin.parent";

    public static final String ORG_PERSON_RELATION_PRESIDENT_TYPE = "kuali.organization.personrelation.type.president";
    public static final String ORG_PERSON_RELATION_MEMBER_TYPE = "kuali.organization.personrelation.type.member";

    public OrganizationService getOrgService() {
        return orgService;
    }

    public void setOrgService(OrganizationService orgService) {
        this.orgService = orgService;
    }

    @Override
    protected void initializeData() throws Exception {
        createOrgs();
        createOrgPositionRestrictions();
        createOrgHierarchies();
        createOrgOrgRelations();
        createOrgPersonRelations();
    }

    private void createOrgs() throws ParseException, DoesNotExistException, PermissionDeniedException, OperationFailedException, InvalidParameterException, ReadOnlyException, MissingParameterException, DataValidationErrorException {
        for (int i = 1; i <= 20; i++) {
            OrgInfo orgInfo = new OrgInfo();
            orgInfo.setId(String.valueOf(i));
            RichTextInfo shortDescr = new RichTextInfo();
            shortDescr.setPlain("Description for new OrgInfo " + i);
            shortDescr.setFormatted("Description for new OrgInfo " + i);
            orgInfo.setShortDescr(shortDescr);
            RichTextInfo longDescr = new RichTextInfo();
            longDescr.setPlain("Loooooooooong description for new OrgInfo " + i);
            longDescr.setFormatted("Loooooooooong description for new OrgInfo " + i);
            orgInfo.setLongDescr(longDescr);
            orgInfo.setLongName("TestOrgLongName" + i);
            orgInfo.setShortName("TestOrgShortName" + i);
            orgInfo.setStateKey(ACTIVE_STATE);
            orgInfo.setTypeKey(OrganizationServiceConstants.ORGANIZATION_DEPARTMENT_TYPE_KEY);
            orgInfo.setEffectiveDate(dateFormat.parse("20090101"));
            orgInfo.setExpirationDate(dateFormat.parse("21001231"));
            orgInfo.setAttributes(new ArrayList<AttributeInfo>());
            new AttributeTester().add2ForCreate(orgInfo.getAttributes());

            LOGGER.debug("Creating org with ID {}", orgInfo.getId());

            orgService.createOrg(OrganizationServiceConstants.ORGANIZATION_DEPARTMENT_TYPE_KEY, orgInfo, context);
        }
    }

    public OrgInfo generateOrg() throws ParseException {
        OrgInfo orgInfo = new OrgInfo();
        RichTextInfo shortDescr = new RichTextInfo();
        shortDescr.setPlain("Description for new OrgInfo ");
        shortDescr.setFormatted("Description for new OrgInfo ");
        orgInfo.setShortDescr(shortDescr);
        RichTextInfo longDescr = new RichTextInfo();
        longDescr.setPlain("Loooooooooong description for new OrgInfo ");
        longDescr.setFormatted("Loooooooooong description for new OrgInfo ");
        orgInfo.setLongDescr(longDescr);
        orgInfo.setLongName("TestOrgLongName");
        orgInfo.setShortName("TestOrgShortName");
        orgInfo.setStateKey(ACTIVE_STATE);
        orgInfo.setTypeKey(OrganizationServiceConstants.ORGANIZATION_DEPARTMENT_TYPE_KEY);
        orgInfo.setEffectiveDate(dateFormat.parse("20090101"));
        orgInfo.setExpirationDate(dateFormat.parse("21001231"));
        orgInfo.setAttributes(new ArrayList<AttributeInfo>());
        new AttributeTester().add2ForCreate(orgInfo.getAttributes());

        return orgInfo;
    }

    private void createOrgHierarchies() throws ParseException, DataValidationErrorException, PermissionDeniedException, OperationFailedException, InvalidParameterException, ReadOnlyException, MissingParameterException, DoesNotExistException {
        createOrgHierarchy('A', "1", OrganizationServiceConstants.ORGANIZATION_CAMPUS_TYPE_KEY,
                ORG_RELATION_ACAD_PARENT_TYPE, ORG_RELATION_ACAD_COLLABORATES_WITH_TYPE);
        createOrgHierarchy('B', "11", OrganizationServiceConstants.ORGANIZATION_CAMPUS_TYPE_KEY,
                ORG_RELATION_FIN_PARENT_TYPE);
    }

    private void createOrgHierarchy(char hierarchyId, String rootOrgId, String typeKey, String... orgOrgRelationTypes) throws ParseException, DoesNotExistException, PermissionDeniedException, OperationFailedException, InvalidParameterException, ReadOnlyException, MissingParameterException, DataValidationErrorException {
        OrgHierarchyInfo hierarchyInfo = new OrgHierarchyInfo();
        hierarchyInfo.setId(String.valueOf(hierarchyId));
        hierarchyInfo.setEffectiveDate(dateFormat.parse("20090101"));
        hierarchyInfo.setExpirationDate(dateFormat.parse("21001231"));
        RichTextInfo descr = new RichTextInfo();
        descr.setPlain("hierarchy " + hierarchyId);
        descr.setFormatted("Loooooooooong description for new OrgHierarchyInfo " + hierarchyId);
        hierarchyInfo.setDescr(descr);
        hierarchyInfo.setStateKey(ACTIVE_STATE);
        hierarchyInfo.setAttributes(new ArrayList<AttributeInfo>());
        new AttributeTester().add2ForCreate(hierarchyInfo.getAttributes());

        hierarchyInfo.setName("Hierarchy " + hierarchyId);
        hierarchyInfo.setTypeKey(typeKey);
        hierarchyInfo.setRootOrgId(rootOrgId);
        hierarchyInfo.setOrgOrgRelationTypes(new ArrayList<String>());
        for(String orgOrgRelationType : orgOrgRelationTypes) {
            hierarchyInfo.getOrgOrgRelationTypes().add(orgOrgRelationType);
        }

        LOGGER.debug("Creating org hierarchy with ID {}", hierarchyInfo.getId());

        orgService.createOrgHierarchy(typeKey, hierarchyInfo,  context);
    }

    public OrgHierarchyInfo generateOrgHierarchy() throws ParseException {
        OrgHierarchyInfo hierarchyInfo = new OrgHierarchyInfo();
        hierarchyInfo.setEffectiveDate(dateFormat.parse("20090101"));
        hierarchyInfo.setExpirationDate(dateFormat.parse("21001231"));
        RichTextInfo descr = new RichTextInfo();
        descr.setPlain("hierarchy plain");
        descr.setFormatted("Loooooooooong description for new OrgHierarchyInfo");
        hierarchyInfo.setDescr(descr);
        hierarchyInfo.setStateKey(ACTIVE_STATE);
        hierarchyInfo.setAttributes(new ArrayList<AttributeInfo>());
        new AttributeTester().add2ForCreate(hierarchyInfo.getAttributes());

        hierarchyInfo.setName("Hierarchy");
        hierarchyInfo.setTypeKey(OrganizationServiceConstants.ORGANIZATION_CAMPUS_TYPE_KEY);
        hierarchyInfo.setRootOrgId("1");

        return hierarchyInfo;
    }

    private void createOrgOrgRelations() throws DoesNotExistException, PermissionDeniedException, ParseException, OperationFailedException, InvalidParameterException, ReadOnlyException, MissingParameterException, DataValidationErrorException {
        createOrgOrgRelation("1", "2", ORG_RELATION_ACAD_PARENT_TYPE);
        createOrgOrgRelation("2", "3", ORG_RELATION_ACAD_PARENT_TYPE);
        createOrgOrgRelation("2", "4", ORG_RELATION_ACAD_PARENT_TYPE);
        createOrgOrgRelation("3", "5", ORG_RELATION_ACAD_PARENT_TYPE);
        createOrgOrgRelation("3", "6", ORG_RELATION_ACAD_PARENT_TYPE);
        createOrgOrgRelation("3", "7", ORG_RELATION_ACAD_PARENT_TYPE);
        createOrgOrgRelation("7", "8", ORG_RELATION_ACAD_PARENT_TYPE);
        createOrgOrgRelation("7", "9", ORG_RELATION_ACAD_PARENT_TYPE);
        createOrgOrgRelation("7", "10", ORG_RELATION_ACAD_PARENT_TYPE);

        createOrgOrgRelation("4", "10", ORG_RELATION_ACAD_COLLABORATES_WITH_TYPE);
        createOrgOrgRelation("4", "12", ORG_RELATION_ACAD_COLLABORATES_WITH_TYPE);
        createOrgOrgRelation("8", "9", ORG_RELATION_ACAD_COLLABORATES_WITH_TYPE);
        createOrgOrgRelation("9", "8", ORG_RELATION_ACAD_COLLABORATES_WITH_TYPE);


        createOrgOrgRelation("11", "4", ORG_RELATION_FIN_PARENT_TYPE);
        createOrgOrgRelation("4", "7", ORG_RELATION_FIN_PARENT_TYPE);
        createOrgOrgRelation("4", "10", ORG_RELATION_FIN_PARENT_TYPE);

        createOrgOrgRelation("13", "13", ORG_RELATION_FIN_PARENT_TYPE);
        createOrgOrgRelation("13", "14", ORG_RELATION_FIN_PARENT_TYPE);
        createOrgOrgRelation("13", "15", ORG_RELATION_FIN_PARENT_TYPE);
        createOrgOrgRelation("14", "16", ORG_RELATION_FIN_PARENT_TYPE);
        createOrgOrgRelation("15", "16", ORG_RELATION_FIN_PARENT_TYPE);
    }

    private void createOrgOrgRelation(String orgId, String relatedOrgId, String relationType) throws ParseException, PermissionDeniedException, DataValidationErrorException, InvalidParameterException, ReadOnlyException, OperationFailedException, MissingParameterException, DoesNotExistException {
        OrgOrgRelationInfo info = new OrgOrgRelationInfo();
        info.setOrgId(orgId);
        info.setRelatedOrgId(relatedOrgId);
        info.setTypeKey(relationType);
        info.setStateKey(ACTIVE_STATE);

        info.setEffectiveDate(dateFormat.parse("20090101"));
        info.setExpirationDate(dateFormat.parse("21001231"));

        info.setAttributes(new ArrayList<AttributeInfo>());
        new AttributeTester().add2ForCreate(info.getAttributes());

        LOGGER.debug("creating OrgOrgRelation between org {} and related org {} of type " + info.getTypeKey(), info.getOrgId(), info.getRelatedOrgId());

        orgService.createOrgOrgRelation(orgId, relatedOrgId, relationType, info, context);
    }

    public OrgOrgRelationInfo generateOrgOrgRelationInfo() throws ParseException {
        OrgOrgRelationInfo info = new OrgOrgRelationInfo();
        info.setOrgId("2");
        info.setRelatedOrgId("3");
        info.setTypeKey(ORG_RELATION_ACAD_PARENT_TYPE);

        info.setEffectiveDate(dateFormat.parse("20090101"));
        info.setExpirationDate(dateFormat.parse("21001231"));

        info.setAttributes(new ArrayList<AttributeInfo>());
        new AttributeTester().add2ForCreate(info.getAttributes());

        return info;
    }

    private void createOrgPersonRelations() throws ParseException, PermissionDeniedException, DataValidationErrorException, InvalidParameterException, ReadOnlyException, OperationFailedException, MissingParameterException, DoesNotExistException {
        for(int i = 1; i <= 20; i++) {
            OrgPersonRelationInfo info = new OrgPersonRelationInfo();
            info.setOrgId(String.valueOf(i));
            info.setPersonId(String.valueOf(1));
            info.setEffectiveDate(dateFormat.parse("20090101"));
            info.setExpirationDate(dateFormat.parse("21001231"));

            info.setAttributes(new ArrayList<AttributeInfo>());
            new AttributeTester().add2ForCreate(info.getAttributes());

            info.setStateKey(ACTIVE_STATE);
            info.setTypeKey(ORG_PERSON_RELATION_PRESIDENT_TYPE);

            orgService.createOrgPersonRelation(String.valueOf(i), "1", ORG_PERSON_RELATION_PRESIDENT_TYPE, info, context);

            for(int j = 1; j <= i; j++) {
                info = new OrgPersonRelationInfo();
                info.setOrgId(String.valueOf(i));
                info.setPersonId(String.valueOf(j));
                info.setEffectiveDate(dateFormat.parse("20090101"));
                info.setExpirationDate(dateFormat.parse("21001231"));

                info.setAttributes(new ArrayList<AttributeInfo>());
                new AttributeTester().add2ForCreate(info.getAttributes());

                info.setStateKey(ACTIVE_STATE);
                info.setTypeKey(ORG_PERSON_RELATION_MEMBER_TYPE);

                orgService.createOrgPersonRelation(String.valueOf(i), String.valueOf(j), ORG_PERSON_RELATION_MEMBER_TYPE, info, context);
            }
        }
    }

    public OrgPersonRelationInfo generateOrgPersonRelation() throws ParseException {
        OrgPersonRelationInfo info = new OrgPersonRelationInfo();
        info.setPersonId("12345679");
        info.setOrgId("1");
        info.setEffectiveDate(dateFormat.parse("20090101"));
        info.setExpirationDate(dateFormat.parse("21001231"));

        info.setAttributes(new ArrayList<AttributeInfo>());
        new AttributeTester().add2ForCreate(info.getAttributes());

        info.setStateKey(ACTIVE_STATE);
        info.setTypeKey(ORG_PERSON_RELATION_MEMBER_TYPE);

        return info;
    }

    private void createOrgPositionRestrictions() throws DoesNotExistException, PermissionDeniedException, OperationFailedException, InvalidParameterException, AlreadyExistsException, ReadOnlyException, MissingParameterException, DataValidationErrorException {
        for(int i = 1; i <= 20; i++) {
            OrgPositionRestrictionInfo info = new OrgPositionRestrictionInfo();
            info.setOrgId(String.valueOf(i));
            info.setId(String.valueOf(i));
            info.setOrgPersonRelationTypeKey(ORG_PERSON_RELATION_PRESIDENT_TYPE);
            info.setMinNumRelations(1);
            info.setMaxNumRelations(1);
            RichTextInfo descr = new RichTextInfo();
            descr.setPlain("Position for President " + i);
            descr.setFormatted("Position for President (formatted) " + i);
            info.setDescr(descr);
            info.setTitle("Mr. President");
            TimeAmountInfo timeInfo = new TimeAmountInfo();
            timeInfo.setTimeQuantity(i);
            timeInfo.setAtpDurationTypeKey(AtpServiceConstants.DURATION_YEAR_TYPE_KEY);
            info.setStdDuration(timeInfo);
            info.setAttributes(new ArrayList<AttributeInfo>());
            new AttributeTester().add2ForCreate(info.getAttributes());
            orgService.createOrgPositionRestriction(String.valueOf(i), ORG_PERSON_RELATION_PRESIDENT_TYPE, info, context);

            info = new OrgPositionRestrictionInfo();
            info.setOrgId(String.valueOf(i));
            info.setId(String.valueOf(i + 20));
            info.setOrgPersonRelationTypeKey(ORG_PERSON_RELATION_MEMBER_TYPE);
            info.setMinNumRelations(0);
            info.setMaxNumRelations(i);
            descr = new RichTextInfo();
            descr.setPlain("Position for Member " + i);
            descr.setFormatted("Position for Member (formatted) " + i);
            info.setDescr(descr);
            info.setTitle("Member");
            timeInfo = new TimeAmountInfo();
            timeInfo.setTimeQuantity(i);
            timeInfo.setAtpDurationTypeKey(AtpServiceConstants.DURATION_YEAR_TYPE_KEY);
            info.setStdDuration(timeInfo);
            info.setAttributes(new ArrayList<AttributeInfo>());
            new AttributeTester().add2ForCreate(info.getAttributes());
            orgService.createOrgPositionRestriction(String.valueOf(i), ORG_PERSON_RELATION_MEMBER_TYPE, info, context);
        }
    }

    public OrgPositionRestrictionInfo generateOrgPositionRestriction() {
        OrgPositionRestrictionInfo info = new OrgPositionRestrictionInfo();
        info.setOrgId("5");
        info.setOrgPersonRelationTypeKey(ORG_PERSON_RELATION_PRESIDENT_TYPE);
        info.setMinNumRelations(1);
        info.setMaxNumRelations(1);
        RichTextInfo descr = new RichTextInfo();
        descr.setPlain("Position for President");
        descr.setFormatted("Position for President (formatted)");
        info.setDescr(descr);
        info.setTitle("Mr. President");
        TimeAmountInfo timeInfo = new TimeAmountInfo();
        timeInfo.setTimeQuantity(1);
        timeInfo.setAtpDurationTypeKey(AtpServiceConstants.DURATION_YEAR_TYPE_KEY);
        info.setStdDuration(timeInfo);
        info.setAttributes(new ArrayList<AttributeInfo>());
        new AttributeTester().add2ForCreate(info.getAttributes());

        return info;
    }
}
