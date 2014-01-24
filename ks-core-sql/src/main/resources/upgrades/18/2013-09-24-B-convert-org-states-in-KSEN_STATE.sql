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
INSERT INTO KSEN_STATE (ID, NAME, LIFECYCLE_KEY, DESCR_PLAIN, DESCR_FORMATTED, CREATEID, CREATETIME, EFF_DT, EXPIR_DT, OBJ_ID, UPDATEID, UPDATETIME, VER_NBR)
 VALUES ('kuali.org.state.active', 'Active', 'kuali.org.lifecycle', 'Organization is active', 'Organization is active', 'SYSTEMLOADER', TO_DATE('1/1/2013', 'MM/DD/YYYY'), TO_DATE('', 'MM/DD/YYYY'), TO_DATE('', 'MM/DD/YYYY'), NVL('', SYS_GUID()), '', TO_DATE('', 'MM/DD/YYYY'), 0)
/

INSERT INTO KSEN_STATE (ID, NAME, LIFECYCLE_KEY, DESCR_PLAIN, DESCR_FORMATTED, CREATEID, CREATETIME, EFF_DT, EXPIR_DT, OBJ_ID, UPDATEID, UPDATETIME, VER_NBR)
 VALUES ('kuali.org.state.inactive', 'Inactive', 'kuali.org.lifecycle', 'Organization is inactive', 'Organization is inactive', 'SYSTEMLOADER', TO_DATE('1/1/2013', 'MM/DD/YYYY'), TO_DATE('', 'MM/DD/YYYY'), TO_DATE('', 'MM/DD/YYYY'), NVL('', SYS_GUID()), '', TO_DATE('', 'MM/DD/YYYY'), 0)
/

INSERT INTO KSEN_STATE (ID, NAME, LIFECYCLE_KEY, DESCR_PLAIN, DESCR_FORMATTED, CREATEID, CREATETIME, EFF_DT, EXPIR_DT, OBJ_ID, UPDATEID, UPDATETIME, VER_NBR)
 VALUES ('kuali.org.org.relation.state.active', 'Active', 'kuali.org.org.relation.lifecycle', 'Org-Org relation is active', 'Org-Org relation is active', 'SYSTEMLOADER', TO_DATE('1/1/2013', 'MM/DD/YYYY'), TO_DATE('', 'MM/DD/YYYY'), TO_DATE('', 'MM/DD/YYYY'), NVL('', SYS_GUID()), '', TO_DATE('', 'MM/DD/YYYY'), 0)
/

INSERT INTO KSEN_STATE (ID, NAME, LIFECYCLE_KEY, DESCR_PLAIN, DESCR_FORMATTED, CREATEID, CREATETIME, EFF_DT, EXPIR_DT, OBJ_ID, UPDATEID, UPDATETIME, VER_NBR)
 VALUES ('kuali.org.org.relation.state.inactive', 'Inactive', 'kuali.org.org.relation.lifecycle', 'Org-Org relation is no longer active', 'Org-Org relation is no longer active', 'SYSTEMLOADER', TO_DATE('1/1/2013', 'MM/DD/YYYY'), TO_DATE('', 'MM/DD/YYYY'), TO_DATE('', 'MM/DD/YYYY'), NVL('', SYS_GUID()), '', TO_DATE('', 'MM/DD/YYYY'), 0)
/

INSERT INTO KSEN_STATE (ID, NAME, LIFECYCLE_KEY, DESCR_PLAIN, DESCR_FORMATTED, CREATEID, CREATETIME, EFF_DT, EXPIR_DT, OBJ_ID, UPDATEID, UPDATETIME, VER_NBR)
 VALUES ('kuali.org.person.relation.state.active', 'Active', 'kuali.org.person.relation.lifecycle', 'Org-Person relation is active', 'Org-Person relation is active', 'SYSTEMLOADER', TO_DATE('1/1/2013', 'MM/DD/YYYY'), TO_DATE('', 'MM/DD/YYYY'), TO_DATE('', 'MM/DD/YYYY'), NVL('', SYS_GUID()), '', TO_DATE('', 'MM/DD/YYYY'), 0)
/

INSERT INTO KSEN_STATE (ID, NAME, LIFECYCLE_KEY, DESCR_PLAIN, DESCR_FORMATTED, CREATEID, CREATETIME, EFF_DT, EXPIR_DT, OBJ_ID, UPDATEID, UPDATETIME, VER_NBR)
 VALUES ('kuali.org.person.relation.state.inactive', 'Inactive', 'kuali.org.person.relation.lifecycle', 'Org-Person relation is no longer active, i.e. the person is no longer in that position', 'Org-Person relation is no longer active, i.e. the person is no longer in that position', 'SYSTEMLOADER', TO_DATE('1/1/2013', 'MM/DD/YYYY'), TO_DATE('', 'MM/DD/YYYY'), TO_DATE('', 'MM/DD/YYYY'), NVL('', SYS_GUID()), '', TO_DATE('', 'MM/DD/YYYY'), 0)
/

INSERT INTO KSEN_STATE (ID, NAME, LIFECYCLE_KEY, DESCR_PLAIN, DESCR_FORMATTED, CREATEID, CREATETIME, EFF_DT, EXPIR_DT, OBJ_ID, UPDATEID, UPDATETIME, VER_NBR)
 VALUES ('kuali.org.hierarchy.state.active', 'Active', 'kuali.org.person.relation.lifecycle', 'Org-Person relation is active', 'Org-Person relation is active', 'SYSTEMLOADER', TO_DATE('1/1/2013', 'MM/DD/YYYY'), TO_DATE('', 'MM/DD/YYYY'), TO_DATE('', 'MM/DD/YYYY'), NVL('', SYS_GUID()), '', TO_DATE('', 'MM/DD/YYYY'), 0)
/

INSERT INTO KSEN_STATE (ID, NAME, LIFECYCLE_KEY, DESCR_PLAIN, DESCR_FORMATTED, CREATEID, CREATETIME, EFF_DT, EXPIR_DT, OBJ_ID, UPDATEID, UPDATETIME, VER_NBR)
 VALUES ('kuali.org.hierarchy.state.inactive', 'Inactive', 'kuali.org.person.relation.lifecycle', 'Org-Person relation is no longer active, i.e. the person is no longer in that position', 'Org-Person relation is no longer active, i.e. the person is no longer in that position', 'SYSTEMLOADER', TO_DATE('1/1/2013', 'MM/DD/YYYY'), TO_DATE('', 'MM/DD/YYYY'), TO_DATE('', 'MM/DD/YYYY'), NVL('', SYS_GUID()), '', TO_DATE('', 'MM/DD/YYYY'), 0)
/

