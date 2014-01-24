--
-- Copyright 2005-2012 The Kuali Foundation
--
-- Licensed under the Educational Community License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
-- http://www.opensource.org/licenses/ecl2.php
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--
INSERT INTO KSEN_STATE_LIFECYCLE (ID, NAME, REF_OBJECT_URI, CREATEID, CREATETIME, DESCR_FORMATTED, DESCR_PLAIN, OBJ_ID, UPDATEID, UPDATETIME, VER_NBR)
 VALUES ('kuali.org.lifecycle', 'General Organization Lifecycle', 'http://student.kuali.org/wsdl/organization/OrgInfo', 'SYSTEMLOADER', TO_DATE('1/1/2012', 'MM/DD/YYYY'), 'General lifecycle used to manage the creation of organizations', 'General lifecycle used to manage the creation of organizations', NVL('', SYS_GUID()), '', TO_DATE('', 'MM/DD/YYYY'), 0)
/

INSERT INTO KSEN_STATE_LIFECYCLE (ID, NAME, REF_OBJECT_URI, CREATEID, CREATETIME, DESCR_FORMATTED, DESCR_PLAIN, OBJ_ID, UPDATEID, UPDATETIME, VER_NBR)
 VALUES ('kuali.org.org.relation.lifecycle', 'General Org-Org Relation Lifeccycle', 'http://student.kuali.org/wsdl/organization/OrgOrgRelationInfo', 'SYSTEMLOADER', TO_DATE('1/1/2012', 'MM/DD/YYYY'), 'General lifecycle used to manage relationships between organizations', 'General lifecycle used to manage relationships between organizations', NVL('', SYS_GUID()), '', TO_DATE('', 'MM/DD/YYYY'), 0)
/

INSERT INTO KSEN_STATE_LIFECYCLE (ID, NAME, REF_OBJECT_URI, CREATEID, CREATETIME, DESCR_FORMATTED, DESCR_PLAIN, OBJ_ID, UPDATEID, UPDATETIME, VER_NBR)
 VALUES ('kuali.org.person.relation.lifecycle', 'General Org-Person Relation Lifeccycle', 'http://student.kuali.org/wsdl/organization/OrgOrgRelationInfo', 'SYSTEMLOADER', TO_DATE('1/1/2012', 'MM/DD/YYYY'), 'General lifecycle used to manage the attachment of people to organizations', 'General lifecycle used to manage the attachment of people to organizations', NVL('', SYS_GUID()), '', TO_DATE('', 'MM/DD/YYYY'), 0)
/

INSERT INTO KSEN_STATE_LIFECYCLE (ID, NAME, REF_OBJECT_URI, CREATEID, CREATETIME, DESCR_FORMATTED, DESCR_PLAIN, OBJ_ID, UPDATEID, UPDATETIME, VER_NBR)
 VALUES ('kuali.org.hierarchy.lifecycle', 'General Org-Hierarchy Lifeccycle', 'http://student.kuali.org/wsdl/organization/OrgHierarchyInfo', 'SYSTEMLOADER', TO_DATE('1/1/2012', 'MM/DD/YYYY'), 'General lifecycle used to manage organizational hierarchies', 'General lifecycle used to manage organizational hierarchies', NVL('', SYS_GUID()), '', TO_DATE('', 'MM/DD/YYYY'), 0)
/