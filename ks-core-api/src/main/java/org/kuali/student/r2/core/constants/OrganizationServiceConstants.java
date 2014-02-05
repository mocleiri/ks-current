/**
 * Copyright 2011 The Kuali Foundation 
 *
 * Licensed under the * Educational Community License, Version 2.0
 * (the "License"); you may * not use this file except in compliance
 * with the License. You may * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.kuali.student.r2.core.constants;

import org.kuali.student.r2.common.constants.CommonServiceConstants;
import org.kuali.student.r2.common.util.RichTextHelper;
import org.kuali.student.r2.common.util.date.DateFormatters;
import org.kuali.student.r2.core.class1.type.dto.TypeInfo;
import org.kuali.student.r2.core.organization.dto.OrgInfo;
import org.kuali.student.r2.core.organization.dto.OrgCodeInfo;
import org.kuali.student.r2.core.organization.dto.OrgHierarchyInfo;
import org.kuali.student.r2.core.organization.dto.OrgOrgRelationInfo;
import org.kuali.student.r2.core.organization.dto.OrgPersonRelationInfo;
import org.kuali.student.r2.core.organization.dto.OrgPositionRestrictionInfo;

/**
 * This class holds the constants used by the organization service
 *
 * @author tom
 */
public class OrganizationServiceConstants {

    /**
     * Reference Object URIs
     */
    public static final String NAMESPACE = CommonServiceConstants.REF_OBJECT_URI_GLOBAL_PREFIX + "organization";
    public static final String REF_OBJECT_URI_DEFAULT = NAMESPACE + "COMMON/DEFAULT";
    public static final String REF_OBJECT_URI_ORG = NAMESPACE + "/" + OrgInfo.class.getSimpleName();
    public static final String REF_OBJECT_URI_ORG_CODE = NAMESPACE + "/" + OrgCodeInfo.class.getSimpleName();
    public static final String REF_OBJECT_URI_ORG_HIERARCHY = NAMESPACE + "/" + OrgHierarchyInfo.class.getSimpleName();
    public static final String REF_OBJECT_URI_ORG_ORG_RELATION = NAMESPACE + "/" + OrgOrgRelationInfo.class.getSimpleName();
    public static final String REF_OBJECT_URI_ORG_PERSON_RELATION = NAMESPACE + "/" + OrgPersonRelationInfo.class.getSimpleName();
    public static final String REF_OBJECT_URI_ORG_POSITION_RESTRICTION = NAMESPACE + "/" + OrgPositionRestrictionInfo.class.getSimpleName();
    public static final String SERVICE_NAME_LOCAL_PART = "OrganizationService";

    /**
     * Organization types (https://wiki.kuali.org/display/STUDENT/Organization+Types#)
     * KSENROLL-3877., KSENROLL-4436
     */
    public static final String ORGANIZATION_COLLEGE_TYPE_KEY = "kuali.org.type.college";
    public static final String ORGANIZATION_DEPARTMENT_TYPE_KEY = "kuali.org.type.academic.department";
    public static final String ORGANIZATION_DIVISION_TYPE_KEY = "kuali.org.type.division";
    public static final String ORGANIZATION_SENATE_TYPE_KEY = "kuali.org.type.senate";
    public static final String ORGANIZATION_PROGRAM_TYPE_KEY = "kuali.org.type.program";
    public static final String ORGANIZATION_SCHOOL_TYPE_KEY = "kuali.org.type.school";
    public static final String ORGANIZATION_CENTER_TYPE_KEY = "kuali.org.type.center";
    public static final String ORGANIZATION_TESTING_SERVICE_TYPE_KEY = "kuali.org.type.testing.service";
    public static final String ORGANIZATION_COMMITTEE_TYPE_KEY = "kuali.org.type.committee";
    public static final String ORGANIZATION_ADVISORY_GROUP_TYPE_KEY = "kuali.org.type.advisory.group";
    public static final String ORGANIZATION_ACCREDITING_BODY_TYPE_KEY = "kuali.org.type.accrediting.body";
    public static final String ORGANIZATION_CAMPUS_TYPE_KEY = "kuali.org.Campus";
    public static final String ORGANIZATION_ACADEMIC_INSTRUCTIONAL_RESEARCH_SUPPORT_TYPE_KEY = "kuali.org.type.academic.instructional.research.support";
    public static final String ORGANIZATION_ACADEMIC_ADMINISTRATIVE_SUPPORT_TYPE_KEY = "kuali.org.type.academic.administrative.support";
    public static final String ORGANIZATION_ACADEMIC_OUTREACH_TYPE_KEY = "kuali.org.type.academic.outreach";
    public static final String ORGANIZATION_UNIVERSITY_SUPPORT_TYPE_KEY = "kuali.org.type.university.support";
    public static final String ORGANIZATION_SUB_DEPARTMENT_TYPE_KEY = "kuali.org.type.sub.department";
    public static final String ORGANIZATION_INSTITUTION_TYPE_KEY = "kuali.org.type.institution";
    public static final String ORGANIZATION_SUBJECT_CODE_TYPE_KEY = "kuali.org.type.subject.code"; // KSENROLL-5112

    /**
     * lifecycles
     */
    public static final String GENERAL_ORGANIZATION_LIFECYCLE = "kuali.org.lifecycle";
    public static final String GENERAL_ORG_ORG_RELATION_LIFECCYCLE = "kuali.org.org.relation.lifecycle";
    public static final String GENERAL_ORG_PERSON_RELATION_LIFECCYCLE = "kuali.org.person.relation.lifecycle";
    public static final String GENERAL_ORG_HIERARCHY_LIFECCYCLE = "kuali.org.hierarchy.lifecycle";
    
    /**
     * States
     */
    public static final String KUALI_ORG_STATE_ACTIVE = "kuali.org.state.active";
    public static final String KUALI_ORG_STATE_INACTIVE = "kuali.org.state.inactive";
    public static final String KUALI_ORG_ORG_RELATION_STATE_ACTIVE = "kuali.org.org.relation.state.active";
    public static final String KUALI_ORG_ORG_RELATION_STATE_INACTIVE = "kuali.org.org.relation.state.inactive";
    public static final String KUALI_ORG_PERSON_RELATION_STATE_ACTIVE = "kuali.org.person.relation.state.active";
    public static final String KUALI_ORG_PERSON_RELATION_STATE_INACTIVE = "kuali.org.person.relation.state.inactive";
    public static final String KUALI_ORG_HIERARCHY_STATE_ACTIVE = "kuali.org.hierarchy.state.active";
    public static final String KUALI_ORG_HIERARCHY_STATE_INACTIVE = "kuali.org.hierarchy.state.inactive";
    
    
    /**
     * Org-Org types (https://wiki.kuali.org/display/STUDENT/Organization+Service+Types+and+States)
     * KSENROLL-5112
     */
    public static final String ORG_ORG_SUBJECT_CODE_TO_ORG_TYPE_KEY = "kuali.org.org.relation.type.subjectcode2org";

    /*Search constants*/
    public static final TypeInfo ORGANIZATION_SEARCH_ORG_TYPE_SEARCH_TYPE;
    public static final TypeInfo ORGANIZATION_SEARCH_ORG_PERSON_RELATION_TYPE_SEARCH_TYPE;

    public static final String ORGANIZATION_SEARCH_ORG_TYPE_SEARCH_KEY = "org.search.orgTypes";
    public static final String ORGANIZATION_SEARCH_ORG_PERSON_RELATION_TYPE_SEARCH_KEY = "org.search.orgPersonRelationTypes";

    public static final class OrganizationSearchParameters {
        public static final String ORG_OPTIONAL_ID = "org.queryParam.orgOptionalId";
    }

    public static final class OrganizationSearchResultColumns {
        public static final String ORG_PERSON_RELATION_TYPE_ID = "org.resultColumn.orgPersonRelationType";
        public static final String ORG_PERSON_RELATION_TYPE_NAME = "org.resultColumn.orgPersonRelationTypeName";
        public static final String ORG_TYPE_ID = "org.resultColumn.orgType";
        public static final String ORG_TYPE_NAME = "org.resultColumn.orgTypeName";
    }

    static {
        TypeInfo info = new TypeInfo();
        info.setKey(ORGANIZATION_SEARCH_ORG_TYPE_SEARCH_KEY);
        info.setName("Org Type Search");
        info.setDescr(new RichTextHelper().fromPlain("Return search results for Org Types"));

        try {
            info.setEffectiveDate(DateFormatters.MONTH_DAY_YEAR_DATE_FORMATTER.parse("01/01/2012"));
        } catch ( IllegalArgumentException ex) {
            throw new RuntimeException("failed to parse date", ex);
        }

        ORGANIZATION_SEARCH_ORG_TYPE_SEARCH_TYPE = info;

        info = new TypeInfo();
        info.setKey(ORGANIZATION_SEARCH_ORG_PERSON_RELATION_TYPE_SEARCH_KEY);
        info.setName("Org Person Relation Type Search");
        info.setDescr(new RichTextHelper().fromPlain("Return search results for Org Person Relation Types"));

        try {
            info.setEffectiveDate(DateFormatters.MONTH_DAY_YEAR_DATE_FORMATTER.parse("01/01/2012"));
        } catch ( IllegalArgumentException ex) {
            throw new RuntimeException("failed to parse date", ex);
        }
        ORGANIZATION_SEARCH_ORG_PERSON_RELATION_TYPE_SEARCH_TYPE = info;
    }
}
