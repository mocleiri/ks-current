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

-- INSERT NEW ROLES
--   See https://docs.google.com/a/kuali.org/spreadsheet/ccc?key=0AuoZASu-9lUAdHNDSDIxVU4zVENoLTVnc1JTbEZEZUE#gid=4
--
insert into KRIM_ROLE_T (ROLE_ID, OBJ_ID, VER_NBR, ROLE_NM, NMSPC_CD, DESC_TXT, KIM_TYP_ID, ACTV_IND, LAST_UPDT_DT) values (KRIM_ROLE_ID_S.NEXTVAL,  SYS_GUID(), 1, 'KS Org Maintenance', 'KS-SYS', 'This role grants a user the ability to maintain organizations', '1', 'Y', TO_DATE ('9/1/2013', 'MM/DD/YYYY') )
/
insert into KRIM_ROLE_T (ROLE_ID, OBJ_ID, VER_NBR, ROLE_NM, NMSPC_CD, DESC_TXT, KIM_TYP_ID, ACTV_IND, LAST_UPDT_DT) values (KRIM_ROLE_ID_S.NEXTVAL,  SYS_GUID(), 1, 'KS Org View', 'KS-SYS', 'A student grants a user the ability to view an organizations', '1', 'Y', TO_DATE ('9/1/2013', 'MM/DD/YYYY') )
/

-- INSERT NEW PERMISSION TEMPLATE
--   See https://docs.google.com/a/kuali.org/spreadsheet/ccc?key=0AuoZASu-9lUAdHNDSDIxVU4zVENoLTVnc1JTbEZEZUE#gid=2
--
insert into KRIM_PERM_TMPL_T (PERM_TMPL_ID, OBJ_ID, VER_NBR, NMSPC_CD, NM, DESC_TXT, KIM_TYP_ID, ACTV_IND) values (KRIM_PERM_TMPL_ID_S.NEXTVAL, SYS_GUID(), 1, 'KS-SYS', 'Can Invoke Service Method', 'Can Invoke a Method in a service contract', '1', 'Y')
/

-- INSERT NEW PERMISSIONS
--   See https://docs.google.com/a/kuali.org/spreadsheet/ccc?key=0AuoZASu-9lUAdHNDSDIxVU4zVENoLTVnc1JTbEZEZUE#gid=1
--
insert into KRIM_PERM_T (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND) values (KRIM_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT perm_tmpl_id FROM krim_perm_tmpl_t where nm = 'Can Invoke Service Method' and nmspc_cd = 'KS-SYS'), 'KS-SYS', 'Can Invoke Service Method Read Organization Data', 'Allows user to Can Invoke Service Method Read Organization Data', 'Y')
/
insert into KRIM_PERM_T (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND) values (KRIM_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT perm_tmpl_id FROM krim_perm_tmpl_t where nm = 'Can Invoke Service Method' and nmspc_cd = 'KS-SYS'), 'KS-SYS', 'Can Invoke Service Method Update Organization Data', 'Allows user to Can Invoke Service Method Update Organization Data', 'Y')
/
insert into KRIM_PERM_T (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND) values (KRIM_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT perm_tmpl_id FROM krim_perm_tmpl_t where nm = 'Can Invoke Service Method' and nmspc_cd = 'KS-SYS'), 'KS-SYS', 'Can Invoke Service Method Delete Organization Data', 'Allows user to Can Invoke Service Method Delete Organization Data', 'Y')
/
insert into KRIM_PERM_T (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND) values (KRIM_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT perm_tmpl_id FROM krim_perm_tmpl_t where nm = 'Can Invoke Service Method' and nmspc_cd = 'KS-SYS'), 'KS-SYS', 'Can Invoke Service Method Read Organization Hierarchy Data', 'Allows user to Can Invoke Service Method Read Organization Hierarchy Data', 'Y')
/
insert into KRIM_PERM_T (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND) values (KRIM_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT perm_tmpl_id FROM krim_perm_tmpl_t where nm = 'Can Invoke Service Method' and nmspc_cd = 'KS-SYS'), 'KS-SYS', 'Can Invoke Service Method Update Organization Hierarchy Data', 'Allows user to Can Invoke Service Method Update Organization Hierarchy Data', 'Y')
/
insert into KRIM_PERM_T (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND) values (KRIM_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT perm_tmpl_id FROM krim_perm_tmpl_t where nm = 'Can Invoke Service Method' and nmspc_cd = 'KS-SYS'), 'KS-SYS', 'Can Invoke Service Method Delete Organization Hierarchy Data', 'Allows user to Can Invoke Service Method Delete Organization Hierarchy Data', 'Y')
/
insert into KRIM_PERM_T (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND) values (KRIM_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT perm_tmpl_id FROM krim_perm_tmpl_t where nm = 'Can Invoke Service Method' and nmspc_cd = 'KS-SYS'), 'KS-SYS', 'Can Invoke Service Method Read Organization Person Relation Data', 'Allows user to Can Invoke Service Method Read Organization Person Relation Data', 'Y')
/
insert into KRIM_PERM_T (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND) values (KRIM_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT perm_tmpl_id FROM krim_perm_tmpl_t where nm = 'Can Invoke Service Method' and nmspc_cd = 'KS-SYS'), 'KS-SYS', 'Can Invoke Service Method Update Organization Person Relation Data', 'Allows user to Can Invoke Service Method Update Organization Person Relation Data', 'Y')
/
insert into KRIM_PERM_T (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND) values (KRIM_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT perm_tmpl_id FROM krim_perm_tmpl_t where nm = 'Can Invoke Service Method' and nmspc_cd = 'KS-SYS'), 'KS-SYS', 'Can Invoke Service Method Delete Organization Person Relation Data', 'Allows user to Can Invoke Service Method Delete Organization Person Relation Data', 'Y')
/

-- Role Permissions Bindings
-- See https://docs.google.com/a/kuali.org/spreadsheet/ccc?key=0AuoZASu-9lUAdHNDSDIxVU4zVENoLTVnc1JTbEZEZUE#gid=8
-- 
insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) values (KRIM_ROLE_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT ROLE_ID FROM KRIM_ROLE_T where ROLE_NM = 'KS Org Maintenance' and nmspc_cd = 'KS-SYS'), (SELECT perm_id from krim_perm_t where nm = 'Can Invoke Service Method Read Organization Data' and nmspc_cd = 'KS-SYS'), 'Y')
/
insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) values (KRIM_ROLE_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT ROLE_ID FROM KRIM_ROLE_T where ROLE_NM = 'KS Org Maintenance' and nmspc_cd = 'KS-SYS'), (SELECT perm_id from krim_perm_t where nm = 'Can Invoke Service Method Update Organization Data' and nmspc_cd = 'KS-SYS'), 'Y')
/
insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) values (KRIM_ROLE_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT ROLE_ID FROM KRIM_ROLE_T where ROLE_NM = 'KS Org Maintenance' and nmspc_cd = 'KS-SYS'), (SELECT perm_id from krim_perm_t where nm = 'Can Invoke Service Method Delete Organization Data' and nmspc_cd = 'KS-SYS'), 'Y')
/
insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) values (KRIM_ROLE_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT ROLE_ID FROM KRIM_ROLE_T where ROLE_NM = 'KS Org Maintenance' and nmspc_cd = 'KS-SYS'), (SELECT perm_id from krim_perm_t where nm = 'Can Invoke Service Method Read Organization Hierarchy Data' and nmspc_cd = 'KS-SYS'), 'Y')
/
insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) values (KRIM_ROLE_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT ROLE_ID FROM KRIM_ROLE_T where ROLE_NM = 'KS Org Maintenance' and nmspc_cd = 'KS-SYS'), (SELECT perm_id from krim_perm_t where nm = 'Can Invoke Service Method Update Organization Hierarchy Data' and nmspc_cd = 'KS-SYS'), 'Y')
/
insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) values (KRIM_ROLE_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT ROLE_ID FROM KRIM_ROLE_T where ROLE_NM = 'KS Org Maintenance' and nmspc_cd = 'KS-SYS'), (SELECT perm_id from krim_perm_t where nm = 'Can Invoke Service Method Delete Organization Hierarchy Data' and nmspc_cd = 'KS-SYS'), 'Y')
/
insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) values (KRIM_ROLE_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT ROLE_ID FROM KRIM_ROLE_T where ROLE_NM = 'KS Org Maintenance' and nmspc_cd = 'KS-SYS'), (SELECT perm_id from krim_perm_t where nm = 'Can Invoke Service Method Read Organization Person Relation Data' and nmspc_cd = 'KS-SYS'), 'Y')
/
insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) values (KRIM_ROLE_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT ROLE_ID FROM KRIM_ROLE_T where ROLE_NM = 'KS Org Maintenance' and nmspc_cd = 'KS-SYS'), (SELECT perm_id from krim_perm_t where nm = 'Can Invoke Service Method Update Organization Person Relation Data' and nmspc_cd = 'KS-SYS'), 'Y')
/
insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) values (KRIM_ROLE_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT ROLE_ID FROM KRIM_ROLE_T where ROLE_NM = 'KS Org Maintenance' and nmspc_cd = 'KS-SYS'), (SELECT perm_id from krim_perm_t where nm = 'Can Invoke Service Method Delete Organization Person Relation Data' and nmspc_cd = 'KS-SYS'), 'Y')
/
insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) values (KRIM_ROLE_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT ROLE_ID FROM KRIM_ROLE_T where ROLE_NM = 'KS Org View' and nmspc_cd = 'KS-SYS'), (SELECT perm_id from krim_perm_t where nm = 'Can Invoke Service Method Read Organization Data' and nmspc_cd = 'KS-SYS'), 'Y')
/
insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) values (KRIM_ROLE_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT ROLE_ID FROM KRIM_ROLE_T where ROLE_NM = 'KS Org View' and nmspc_cd = 'KS-SYS'), (SELECT perm_id from krim_perm_t where nm = 'Can Invoke Service Method Read Organization Hierarchy Data' and nmspc_cd = 'KS-SYS'), 'Y')
/
insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) values (KRIM_ROLE_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT ROLE_ID FROM KRIM_ROLE_T where ROLE_NM = 'KS Org View' and nmspc_cd = 'KS-SYS'), (SELECT perm_id from krim_perm_t where nm = 'Can Invoke Service Method Read Organization Person Relation Data' and nmspc_cd = 'KS-SYS'), 'Y')
/
insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) values (KRIM_ROLE_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT ROLE_ID FROM KRIM_ROLE_T where ROLE_NM = 'Kuali Student CM Admin' and nmspc_cd = 'KS-SYS'), (SELECT perm_id from krim_perm_t where nm = 'Can Invoke Service Method Read Organization Data' and nmspc_cd = 'KS-SYS'), 'Y')
/
insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) values (KRIM_ROLE_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT ROLE_ID FROM KRIM_ROLE_T where ROLE_NM = 'Kuali Student CM Admin' and nmspc_cd = 'KS-SYS'), (SELECT perm_id from krim_perm_t where nm = 'Can Invoke Service Method Read Organization Hierarchy Data' and nmspc_cd = 'KS-SYS'), 'Y')
/
insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) values (KRIM_ROLE_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT ROLE_ID FROM KRIM_ROLE_T where ROLE_NM = 'Kuali Student CM Admin' and nmspc_cd = 'KS-SYS'), (SELECT perm_id from krim_perm_t where nm = 'Can Invoke Service Method Read Organization Person Relation Data' and nmspc_cd = 'KS-SYS'), 'Y')
/
insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) values (KRIM_ROLE_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT ROLE_ID FROM KRIM_ROLE_T where ROLE_NM = 'Kuali Student CM User' and nmspc_cd = 'KS-SYS'), (SELECT perm_id from krim_perm_t where nm = 'Can Invoke Service Method Read Organization Data' and nmspc_cd = 'KS-SYS'), 'Y')
/
insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) values (KRIM_ROLE_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT ROLE_ID FROM KRIM_ROLE_T where ROLE_NM = 'Kuali Student CM User' and nmspc_cd = 'KS-SYS'), (SELECT perm_id from krim_perm_t where nm = 'Can Invoke Service Method Read Organization Hierarchy Data' and nmspc_cd = 'KS-SYS'), 'Y')
/
insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) values (KRIM_ROLE_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT ROLE_ID FROM KRIM_ROLE_T where ROLE_NM = 'Kuali Student CM User' and nmspc_cd = 'KS-SYS'), (SELECT perm_id from krim_perm_t where nm = 'Can Invoke Service Method Read Organization Person Relation Data' and nmspc_cd = 'KS-SYS'), 'Y')
/

-- Role Member Bindings
-- See https://docs.google.com/a/kuali.org/spreadsheet/ccc?key=0AuoZASu-9lUAdHNDSDIxVU4zVENoLTVnc1JTbEZEZUE#gid=5
-- 
insert into KRIM_ROLE_MBR_T (ROLE_MBR_ID, VER_NBR, OBJ_ID, ROLE_ID, MBR_ID, MBR_TYP_CD, ACTV_FRM_DT, ACTV_TO_DT, LAST_UPDT_DT) values (KRIM_ROLE_MBR_ID_S.NEXTVAL, 1, SYS_GUID(), (SELECT ROLE_ID FROM KRIM_ROLE_T where ROLE_NM = 'KS Org Maintenance' and nmspc_cd = 'KS-SYS'), 'admin', 'P', '', '', TO_DATE('10/24/2012', 'MM/DD/YYYY'))
/
insert into KRIM_ROLE_MBR_T (ROLE_MBR_ID, VER_NBR, OBJ_ID, ROLE_ID, MBR_ID, MBR_TYP_CD, ACTV_FRM_DT, ACTV_TO_DT, LAST_UPDT_DT) values (KRIM_ROLE_MBR_ID_S.NEXTVAL, 1, SYS_GUID(), (SELECT ROLE_ID FROM KRIM_ROLE_T where ROLE_NM = 'KS Org View' and nmspc_cd = 'KS-SYS'), 'fred', 'P', '', '', TO_DATE('10/24/2012', 'MM/DD/YYYY'))
/