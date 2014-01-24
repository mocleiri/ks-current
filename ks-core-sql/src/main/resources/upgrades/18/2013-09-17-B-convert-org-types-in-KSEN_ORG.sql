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
update KSEN_ORG 
set ORG_TYPE = 
replace (
replace (
replace (
replace (
replace (
replace (
replace (
replace (
replace (
replace (
replace (
replace (
replace (
replace (
replace (
replace (
replace (
replace (
replace (
replace (
replace (
replace (
replace (
replace (
replace (
replace (
replace (
replace (
replace (
replace (
replace (
replace (ORG_TYPE, 'kuali.org.AccreditingBody', 'kuali.org.type.accrediting.body'),
'kuali.org.AdhocCommittee', 'kuali.org.type.adhoc.committee'),
'kuali.org.AdvisoryGroup', 'kuali.org.type.advisory.group'),
'kuali.org.Association', 'kuali.org.type.association'),
'kuali.org.Board', 'kuali.org.type.board'),
'kuali.org.COC', 'kuali.org.type.coc'),
'kuali.org.Center', 'kuali.org.type.center'),
'kuali.org.College', 'kuali.org.type.college'),
'kuali.org.Committee', 'kuali.org.type.committee'),
'kuali.org.CorporateEntity', 'kuali.org.type.corporate.entity'),
'kuali.org.Department', 'kuali.org.type.academic.department'),
'kuali.org.Division', 'kuali.org.type.division'),
'kuali.org.Office', 'kuali.org.type.office'),
'kuali.org.Program', 'kuali.org.type.program'),
'kuali.org.School', 'kuali.org.type.school'),
'kuali.org.Section', 'kuali.org.type.section'),
'kuali.org.Senate', 'kuali.org.type.senate'),
'kuali.org.SubjectCode', 'kuali.org.type.subject.code'),
'kuali.org.TestingService', 'kuali.org.type.testing.service'),
'kuali.org.WorkGroup', 'kuali.org.type.work.group'),
'kuali.org.type.campus', 'kuali.org.type.campus'),
'kuali.org.type.college', 'kuali.org.type.college'),
'kuali.org.type.curriculum', 'kuali.org.type.curriculum'),
'kuali.org.type.department', 'kuali.org.type.academic.department'),
'kuali.org.type.university', 'kuali.org.type.university'),
'kuali.org.AcademicAdministrativeSupport', 'kuali.org.type.academic.administrative.support'),
'kuali.org.AcademicInstructionalResearchSupport', 'kuali.org.type.academic.instructional.research.support'),
'kuali.org.AcademicOutreach', 'kuali.org.type.academic.outreach'),
'kuali.org.SpecialProgram ', 'kuali.org.type.special.program'),
'kuali.org.SubDepartment', 'kuali.org.type.sub.department'),
'kuali.org.UniversitySupport', 'kuali.org.type.university.support'),
'kuali.org.Institution', 'kuali.org.type.institution')
/

