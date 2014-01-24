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
INSERT INTO KSEN_TYPE 
(TYPE_KEY, OBJ_ID, NAME, DESCR_PLAIN, DESCR_FORMATTED, EFF_DT, EXPIR_DT, 
REF_OBJECT_URI, SERVICE_URI, VER_NBR, CREATETIME, CREATEID, UPDATETIME, UPDATEID) 
VALUES
 ('kuali.proposal.type.curriculum.management.user.authorization.add.request', 
sys_guid(), 'Add New Curriculum Management User',
'A proposal of this type records the information needed to add a new departmental user and set them up both in the org-person relation service and in the required KIM Roles table.',
'A proposal of this type records the information needed to add a new departmental user and set them up both in the org-person relation service and in the required KIM Roles table.', 
null, null, 
'http://student.kuali.org/wsdl/proposal/ProposalInfo',
'http://student.kuali.org/wsdl/proposal/ProposalService', 
0, to_date('2012-03-01', 'YYYY-MM-DD'), 'SYSTEMLOADER', null,null)
/
INSERT INTO KSEN_TYPE (TYPE_KEY, OBJ_ID, NAME, DESCR_PLAIN, DESCR_FORMATTED, EFF_DT, EXPIR_DT, 
REF_OBJECT_URI, SERVICE_URI, VER_NBR, CREATETIME, CREATEID, UPDATETIME, UPDATEID) 
VALUES 
('kuali.proposal.type.curriculum.management.user.authorization.terminate.request', 
sys_guid(), 
'Terminate Curriculum Management User',
'A proposal of this type records the information needed to terminate a departmental user and remove their rights as of a specified date in both the org-person relation service and in the required KIM Roles table.',
'A proposal of this type records the information needed to terminate a departmental user and remove their rights as of a specified date in both the org-person relation service and in the required KIM Roles table.', 
null, null, 
'http://student.kuali.org/wsdl/proposal/ProposalInfo',
'http://student.kuali.org/wsdl/proposal/ProposalService', 
0, to_date('2012-03-01', 'YYYY-MM-DD'), 'SYSTEMLOADER', null,null)
/
--
-- also need to put the types into the proposal type table since this is an old R1 imple
--
INSERT INTO KSPR_PROPOSAL_TYPE 
(TYPE_KEY, OBJ_ID, NAME, TYPE_DESC, EFF_DT, EXPIR_DT, VER_NBR) 
VALUES
 ('kuali.proposal.type.curriculum.management.user.authorization.add.request', 
sys_guid(), 'Add New Curriculum Management User',
'A proposal of this type records the information needed to add a new departmental user and set them up both in the org-person relation service and in the required KIM Roles table.',
to_date('2012-03-01', 'YYYY-MM-DD'), null, 0)
/
INSERT INTO KSPR_PROPOSAL_TYPE 
(TYPE_KEY, OBJ_ID, NAME, TYPE_DESC, EFF_DT, EXPIR_DT, VER_NBR) 
VALUES
 ('kuali.proposal.type.curriculum.management.user.authorization.terminate.request', 
sys_guid(), 'Terminate Curriculum Management User',
'A proposal of this type records the information needed to terminate a departmental user and remove their rights as of a specified date in both the org-person relation service and in the required KIM Roles table.',
to_date('2012-03-01', 'YYYY-MM-DD'), null, 0)
/
--
-- lifecycle states
--
INSERT INTO KSEN_STATE_LIFECYCLE (ID, NAME, REF_OBJECT_URI, CREATEID, CREATETIME, DESCR_FORMATTED, DESCR_PLAIN, OBJ_ID, UPDATEID, UPDATETIME, VER_NBR)
 VALUES ('kuali.proposal.lifecycle', 'General Lifecycle of a Proposal', 'http://student.kuali.org/wsdl/proposal/ProposalInfo', 'SYSTEMLOADER', TO_DATE('1/1/2012', 'MM/DD/YYYY'), 'General lifecycle used to manage proposals', 'General lifecycle used to manage proposals', NVL('', SYS_GUID()), '', TO_DATE('', 'MM/DD/YYYY'), 0)
/
INSERT INTO KSEN_STATE (ID, NAME, LIFECYCLE_KEY, DESCR_PLAIN, DESCR_FORMATTED, CREATEID, CREATETIME, EFF_DT, EXPIR_DT, OBJ_ID, UPDATEID, UPDATETIME, VER_NBR)
 VALUES ('kuali.proposal.state.draft', 'Draft', 'kuali.proposal.lifecycle', 'Proposal is in draft mode. I.e. the proposer is still working on it and it has not yet beeen submittted', 'Proposal is in draft. I.e. the proposer is still working on it and it has not yet beeen submittted', 'SYSTEMLOADER', TO_DATE('1/1/2013', 'MM/DD/YYYY'), TO_DATE('', 'MM/DD/YYYY'), TO_DATE('', 'MM/DD/YYYY'), NVL('', SYS_GUID()), '', TO_DATE('', 'MM/DD/YYYY'), 0)
/
INSERT INTO KSEN_STATE (ID, NAME, LIFECYCLE_KEY, DESCR_PLAIN, DESCR_FORMATTED, CREATEID, CREATETIME, EFF_DT, EXPIR_DT, OBJ_ID, UPDATEID, UPDATETIME, VER_NBR)
 VALUES ('kuali.proposal.state.submitted', 'Submitted', 'kuali.proposal.lifecycle', 'Proposal has been submitted for approval.  Typically this means that the proposal is being routed through workflow and has not yet been acted upon.', 'Proposal has been submitted for approval.  Typically this means that the proposal is being routed through workflow and has not yet been acted upon.', 'SYSTEMLOADER', TO_DATE('1/1/2013', 'MM/DD/YYYY'), TO_DATE('', 'MM/DD/YYYY'), TO_DATE('', 'MM/DD/YYYY'), NVL('', SYS_GUID()), '', TO_DATE('', 'MM/DD/YYYY'), 0)
/
INSERT INTO KSEN_STATE (ID, NAME, LIFECYCLE_KEY, DESCR_PLAIN, DESCR_FORMATTED, CREATEID, CREATETIME, EFF_DT, EXPIR_DT, OBJ_ID, UPDATEID, UPDATETIME, VER_NBR)
 VALUES ('kuali.proposal.state.approved', 'Approved', 'kuali.proposal.lifecycle', 'Proposal has been approved.  Typically this means the proposal has completed workflow and the final approval has been made.', 'Proposal has been approved.  Typically this means the proposal has completed workflow and the final approval has been made.', 'SYSTEMLOADER', TO_DATE('1/1/2013', 'MM/DD/YYYY'), TO_DATE('', 'MM/DD/YYYY'), TO_DATE('', 'MM/DD/YYYY'), NVL('', SYS_GUID()), '', TO_DATE('', 'MM/DD/YYYY'), 0)
/
INSERT INTO KSEN_STATE (ID, NAME, LIFECYCLE_KEY, DESCR_PLAIN, DESCR_FORMATTED, CREATEID, CREATETIME, EFF_DT, EXPIR_DT, OBJ_ID, UPDATEID, UPDATETIME, VER_NBR)
 VALUES ('kuali.proposal.state.not.approved', 'Not Approved', 'kuali.proposal.lifecycle', 'Proposal has not been approved.  Typically this means the proposal has completed workflow and the final decision was to not approve the proposal.', 'Proposal has not been approved.  Typically this means the proposal has completed workflow and the final decision was to not approve the proposal.', 'SYSTEMLOADER', TO_DATE('1/1/2013', 'MM/DD/YYYY'), TO_DATE('', 'MM/DD/YYYY'), TO_DATE('', 'MM/DD/YYYY'), NVL('', SYS_GUID()), '', TO_DATE('', 'MM/DD/YYYY'), 0)
/
-- added 12/15/2013 by aniruddha to cover new case
INSERT INTO KSEN_STATE (ID, NAME, LIFECYCLE_KEY, DESCR_PLAIN,
DESCR_FORMATTED, CREATEID, CREATETIME, EFF_DT, EXPIR_DT, OBJ_ID,
UPDATEID, UPDATETIME, VER_NBR)
 VALUES ('kuali.proposal.state.clarification', 'Clarification',
'kuali.proposal.lifecycle', 'Proposal sent back for clarification.
Typically this means the proposal has sent back for clarification and
final approval has not been made.', 'Proposal sent back for
clarification.  Typically this means the proposal has sent back for
clarification and  final approval has not been made.', 'SYSTEMLOADER',
TO_DATE('1/1/2013', 'MM/DD/YYYY'), TO_DATE('', 'MM/DD/YYYY'),
TO_DATE('', 'MM/DD/YYYY'), NVL('', SYS_GUID()), '', TO_DATE('',
'MM/DD/YYYY'), 0)
/