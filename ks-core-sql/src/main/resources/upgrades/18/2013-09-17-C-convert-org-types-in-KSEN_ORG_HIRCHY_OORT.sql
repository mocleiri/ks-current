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
update KSEN_ORG_HIRCHY_OORT 
set ORG_ORG_RELTN_TYPE_ID  = 
replace (
replace (ORG_ORG_RELTN_TYPE_ID, 
'kuali.org.org.relation.type.subjectcode2org', 'kuali.org.org.relation.type.subjectcode2org'),
'kuali.org.Parent2CurriculumChild', 'kuali.org.org.relation.type.parent2curriculumchild')
/

