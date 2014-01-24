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

-- INSERT NEW PERMISSIONS
--   See https://docs.google.com/a/kuali.org/spreadsheet/ccc?key=0AuoZASu-9lUAdHNDSDIxVU4zVENoLTVnc1JTbEZEZUE#gid=1
--
insert into KRIM_PERM_T (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND) values (KRIM_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT perm_tmpl_id FROM krim_perm_tmpl_t where nm = 'Can Invoke Service Method' and nmspc_cd = 'KS-SYS'), 'KS-SYS', 'Can Invoke Service Method Read Organization Position Restriction Data', 'Allows user to Can Invoke Service Method Read Organization Position Restriction Data', 'Y')
/
insert into KRIM_PERM_T (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND) values (KRIM_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT perm_tmpl_id FROM krim_perm_tmpl_t where nm = 'Can Invoke Service Method' and nmspc_cd = 'KS-SYS'), 'KS-SYS', 'Can Invoke Service Method Update Organization Position Restriction Data', 'Allows user to Can Invoke Service Method Update Organization Position Restriction Data', 'Y')
/
insert into KRIM_PERM_T (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND) values (KRIM_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT perm_tmpl_id FROM krim_perm_tmpl_t where nm = 'Can Invoke Service Method' and nmspc_cd = 'KS-SYS'), 'KS-SYS', 'Can Invoke Service Method Delete Organization Position Restriction Data', 'Allows user to Can Invoke Service Method Delete Organization Position Restriction Data', 'Y')
/

-- Role Permissions Bindings
-- See https://docs.google.com/a/kuali.org/spreadsheet/ccc?key=0AuoZASu-9lUAdHNDSDIxVU4zVENoLTVnc1JTbEZEZUE#gid=8
-- 
insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) values (KRIM_ROLE_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT ROLE_ID FROM KRIM_ROLE_T where ROLE_NM = 'KS Org Maintenance' and nmspc_cd = 'KS-SYS'), (SELECT perm_id from krim_perm_t where nm = 'Can Invoke Service Method Read Organization Position Restriction Data' and nmspc_cd = 'KS-SYS'), 'Y')
/
insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) values (KRIM_ROLE_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT ROLE_ID FROM KRIM_ROLE_T where ROLE_NM = 'KS Org Maintenance' and nmspc_cd = 'KS-SYS'), (SELECT perm_id from krim_perm_t where nm = 'Can Invoke Service Method Update Organization Position Restriction Data' and nmspc_cd = 'KS-SYS'), 'Y')
/
insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) values (KRIM_ROLE_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT ROLE_ID FROM KRIM_ROLE_T where ROLE_NM = 'KS Org Maintenance' and nmspc_cd = 'KS-SYS'), (SELECT perm_id from krim_perm_t where nm = 'Can Invoke Service Method Delete Organization Position Restriction Data' and nmspc_cd = 'KS-SYS'), 'Y')
/
insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) values (KRIM_ROLE_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT ROLE_ID FROM KRIM_ROLE_T where ROLE_NM = 'Kuali Student CM User' and nmspc_cd = 'KS-SYS'), (SELECT perm_id from krim_perm_t where nm = 'Can Invoke Service Method Read Organization Position Restriction Data' and nmspc_cd = 'KS-SYS'), 'Y')
/
insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) values (KRIM_ROLE_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT ROLE_ID FROM KRIM_ROLE_T where ROLE_NM = 'KS Org View' and nmspc_cd = 'KS-SYS'), (SELECT perm_id from krim_perm_t where nm = 'Can Invoke Service Method Read Organization Position Restriction Data' and nmspc_cd = 'KS-SYS'), 'Y')
/