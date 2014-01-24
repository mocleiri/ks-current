INSERT INTO KRIM_PERM_T (ACTV_IND, DESC_TXT, NM, NMSPC_CD, OBJ_ID, PERM_ID, PERM_TMPL_ID, VER_NBR) 
VALUES ('Y', 'Create permission to initiate AuthorizationRequestDocument.', 'AuthorizationRequestDocument Initiate Document', 'KS-SYS', sys_guid() , 'KS20001', '10', '1')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, ATTR_VAL, KIM_ATTR_DEFN_ID, KIM_TYP_ID, OBJ_ID, PERM_ID, VER_NBR) 
VALUES ('KS20002', 'AuthorizationRequestDocument', '13', '3', sys_guid() , 'KS20001', '1')
/

INSERT INTO KRIM_ROLE_PERM_T (ACTV_IND, OBJ_ID, PERM_ID, ROLE_ID, ROLE_PERM_ID, VER_NBR) 
VALUES ('Y', sys_guid() , 'KS20001', (SELECT ROLE_ID FROM krim_role_t WHERE nmspc_cd='KS-SYS' AND role_nm='Kuali Student CM Admin'), 'KS20003', '1')
/


INSERT INTO krim_rsp_t (RSP_ID,OBJ_ID,VER_NBR,RSP_TMPL_ID,NMSPC_CD,NM,DESC_TXT,ACTV_IND) 
VALUES ('KS20004', sys_guid(),1,'1','KS-SYS','AuthorizationRequestDocument Review Responsibility','AuthorizationRequestDocument Review Responsibility','Y')
/
INSERT INTO krim_rsp_attr_data_t (ATTR_DATA_ID,OBJ_ID,VER_NBR,RSP_ID,KIM_TYP_ID,KIM_ATTR_DEFN_ID,ATTR_VAL) 
VALUES ('KS20005', sys_guid(),1,'KS20004','7','41','false')
/
INSERT INTO krim_rsp_attr_data_t (ATTR_DATA_ID,OBJ_ID,VER_NBR,RSP_ID,KIM_TYP_ID,KIM_ATTR_DEFN_ID,ATTR_VAL) 
VALUES ('KS20006', sys_guid(),1,'KS20004','7','16','Kuali Student CM Admin')
/
INSERT INTO krim_rsp_attr_data_t (ATTR_DATA_ID,OBJ_ID,VER_NBR,RSP_ID,KIM_TYP_ID,KIM_ATTR_DEFN_ID,ATTR_VAL) 
VALUES ('KS20007', sys_guid(),1,'KS20004','7','13','AuthorizationRequestDocument')
/
INSERT INTO krim_rsp_attr_data_t (ATTR_DATA_ID,OBJ_ID,VER_NBR,RSP_ID,KIM_TYP_ID,KIM_ATTR_DEFN_ID,ATTR_VAL) 
VALUES ('KS20008', sys_guid(),1,'KS20004','7','40','false')
/
INSERT INTO krim_role_rsp_t (ROLE_RSP_ID,OBJ_ID,VER_NBR,ROLE_ID,RSP_ID,ACTV_IND) 
VALUES ('KS20009', sys_guid(),3,(SELECT ROLE_ID FROM krim_role_t WHERE nmspc_cd='KS-SYS' AND role_nm='Kuali Student CM Admin'),'KS20004','Y')
/
INSERT INTO krim_role_rsp_actn_t (ROLE_RSP_ACTN_ID,OBJ_ID,VER_NBR,ACTN_TYP_CD,PRIORITY_NBR,ACTN_PLCY_CD,ROLE_MBR_ID,ROLE_RSP_ID,FRC_ACTN)
 VALUES ('KS20010', sys_guid(),1,'A',1,'F','*','KS20009','Y')
/
