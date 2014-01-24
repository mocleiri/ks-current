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
update KSEN_ORG_PERS_RELTN
set ORG_PERS_RELTN_TYPE = 
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
replace (ORG_PERS_RELTN_TYPE, 'kuali.org.PersonRelation.AdminAssistant', 'kuali.org.person.relation.type.admin.assistant'),
'kuali.org.PersonRelation.AdminMember', 'kuali.org.person.relation.type.admin.member'),
'kuali.org.PersonRelation.AdministrativeOfficer', 'kuali.org.person.relation.type.administrative.officer'),
'kuali.org.PersonRelation.AssocDean', 'kuali.org.person.relation.type.assoc.dean'),
'kuali.org.PersonRelation.AssocProvost', 'kuali.org.person.relation.type.assoc.provost'),
'kuali.org.PersonRelation.AssocRegistrar', 'kuali.org.person.relation.type.assoc.registrar'),
'kuali.org.PersonRelation.Chair', 'kuali.org.person.relation.type.chair'),
'kuali.org.PersonRelation.Chancellor', 'kuali.org.person.relation.type.chancellor'),
'kuali.org.PersonRelation.Co-Chair', 'kuali.org.person.relation.type.co-chair'),
'kuali.org.PersonRelation.Coordinator', 'kuali.org.person.relation.type.coordinator'),
'kuali.org.PersonRelation.CurriculumManager', 'kuali.org.person.relation.type.curriculum.manager'),
'kuali.org.PersonRelation.Dean', 'kuali.org.person.relation.type.dean'),
'kuali.org.PersonRelation.Director', 'kuali.org.person.relation.type.director'),
'kuali.org.PersonRelation.EVPP', 'kuali.org.person.relation.type.evpp'),
'kuali.org.PersonRelation.Ex-Officio', 'kuali.org.person.relation.type.ex-officio'),
'kuali.org.PersonRelation.ExecutiveOfficer', 'kuali.org.person.relation.type.executive.officer'),
'kuali.org.PersonRelation.Head', 'kuali.org.person.relation.type.head'),
'kuali.org.PersonRelation.Member', 'kuali.org.person.relation.type.member'),
'kuali.org.PersonRelation.Officer', 'kuali.org.person.relation.type.officer'),
'kuali.org.PersonRelation.President', 'kuali.org.person.relation.type.president'),
'kuali.org.PersonRelation.Professor', 'kuali.org.person.relation.type.professor'),
'kuali.org.PersonRelation.Registrar', 'kuali.org.person.relation.type.registrar'),
'kuali.org.PersonRelation.Rep', 'kuali.org.person.relation.type.rep'),
'kuali.org.PersonRelation.Secretary', 'kuali.org.person.relation.type.secretary'),
'kuali.org.PersonRelation.Treasurer', 'kuali.org.person.relation.type.treasurer'),
'kuali.org.PersonRelation.ViceChancellor', 'kuali.org.person.relation.type.vice.chancellor'),
'kuali.org.PersonRelation.VicePresident', 'kuali.org.person.relation.type.vice.president')
/

