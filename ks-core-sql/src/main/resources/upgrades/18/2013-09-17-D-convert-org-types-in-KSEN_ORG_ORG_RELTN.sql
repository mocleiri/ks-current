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
update KSEN_ORG_ORG_RELTN
set ORG_RELTN_TYPE = 
replace (
replace (ORG_RELTN_TYPE, 
'kuali.org.org.relation.type.subjectcode2org', 'kuali.org.org.relation.type.subjectcode2org'),
'kuali.org.org.relation.type.parent2curriculumchild', 'kuali.org.org.relation.type.parent2curriculumchild')
/

