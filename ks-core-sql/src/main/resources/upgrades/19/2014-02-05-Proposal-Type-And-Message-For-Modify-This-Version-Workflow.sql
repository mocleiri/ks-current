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
 ('kuali.proposal.type.course.modify.current.version', 
sys_guid(), 'Modify Course Edit Current Version type',
'A proposal of this type modifies the current version but via workflow.',
'A proposal of this type modifies the current version but via workflow.', 
null, null, 
'http://student.kuali.org/wsdl/proposal/ProposalInfo',
'http://student.kuali.org/wsdl/proposal/ProposalService', 
0, to_date('2012-03-01', 'YYYY-MM-DD'), 'SYSTEMLOADER', null,null)
/
--
-- also need to put the types into the proposal type table since this is an old R1 imple
--
INSERT INTO KSPR_PROPOSAL_TYPE
(EFF_DT,NAME,OBJ_ID,TYPE_DESC,TYPE_KEY,VER_NBR)
  VALUES (TO_DATE( '20000101000000', 'YYYYMMDDHH24MISS' ),
'Modify Course Edit Current Version',
'F0B674A3A1F4571FE040007F010163D4',
'A modify course current version proposal type',
'kuali.proposal.type.course.modify.current.version', 
0)
/
INSERT INTO KSMG_MESSAGE (GRP_NAME,ID,LOCALE,MSG_ID,MSG_VALUE,VER_NBR)
  VALUES
('course','f0790d0c-6937-7008-e040-007f0101056d',
'en',
'modifyCourseCurrentVersion','Modify current version with workflow',1)
/