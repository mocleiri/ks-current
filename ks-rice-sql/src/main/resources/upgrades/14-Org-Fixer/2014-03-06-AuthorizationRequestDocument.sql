
REM INSERTING into KREW_DOC_TYP_T
SET DEFINE OFF;
INSERT
INTO KREW_DOC_TYP_T  (DOC_TYP_ID, PARNT_ID, DOC_TYP_NM, DOC_TYP_VER_NBR, ACTV_IND, CUR_IND, DOC_TYP_DESC, LBL, PREV_DOC_TYP_VER_NBR, DOC_HDR_ID, DOC_HDLR_URL, HELP_DEF_URL, DOC_SEARCH_HELP_URL, POST_PRCSR, GRP_ID, BLNKT_APPR_GRP_ID, BLNKT_APPR_PLCY, RPT_GRP_ID, RTE_VER_NBR, NOTIFY_ADDR, SEC_XML, EMAIL_XSL, APPL_ID, OBJ_ID, VER_NBR)
  VALUES
  (
    KREW_DOC_HDR_S.NEXTVAL,
    (SELECT MAX(KREW_DOC_TYP_T.DOC_TYP_ID)
    FROM KREW_DOC_TYP_T
    WHERE KREW_DOC_TYP_T.DOC_TYP_NM='KualiStudentDocument'
    ),
    'AuthorizationRequestDocument',
    0,1,1,
    'Person Authorization Request',
    'Kuali Student Person Authorization',
    NULL,
    NULL,
    '${application.url}/portal.do?channelTitle=Person%20Authorization%20Request&channelUrl=${application.url}/kr-krad/personAuthorization?viewId=personAuthorizationRequestView&methodToCall=start',
    NULL,
    NULL,
    'org.kuali.student.lum.workflow.PersonAuthorizationPostProcessor',
    NULL,
    NULL,
    NULL,
    NULL,
    '2',
    NULL,
    EMPTY_CLOB(),
    NULL,
    NULL,
    SYS_GUID(),
    1
  )
/
  
REM INSERTING into KREW_RTE_NODE_T
SET DEFINE OFF;
---
Insert into KREW_RTE_NODE_T (RTE_NODE_ID,DOC_TYP_ID,NM,TYP,RTE_MTHD_NM,FNL_APRVR_IND,MNDTRY_RTE_IND,GRP_ID,RTE_MTHD_CD,ACTVN_TYP,BRCH_PROTO_ID,NEXT_DOC_STAT,VER_NBR) 
values (KREW_RTE_NODE_S.NEXTVAL,(SELECT MAX(KREW_DOC_TYP_T.DOC_TYP_ID) FROM KREW_DOC_TYP_T WHERE KREW_DOC_TYP_T.DOC_TYP_NM='AuthorizationRequestDocument'),'PreRoute','org.kuali.rice.kew.engine.node.InitialNode',null,0,0,null,null,'P',null,null,1)
/

REM INSERTING into KREW_RTE_NODE_CFG_PARM_T
SET DEFINE OFF;
Insert into KREW_RTE_NODE_CFG_PARM_T (RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,KEY_CD,VAL) 
values (KREW_RTE_NODE_CFG_PARM_S.NEXTVAL,KREW_RTE_NODE_S.CURRVAL,'contentFragment','<start name="PreRoute">
<activationType>P</activationType>
</start>
')
/
Insert into KREW_RTE_NODE_CFG_PARM_T (RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,KEY_CD,VAL) 
values (KREW_RTE_NODE_CFG_PARM_S.NEXTVAL,KREW_RTE_NODE_S.CURRVAL,'activationType','P')
/
Insert into KREW_RTE_NODE_CFG_PARM_T (RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,KEY_CD,VAL) 
values (KREW_RTE_NODE_CFG_PARM_S.NEXTVAL,KREW_RTE_NODE_S.CURRVAL,'ruleSelector','Template')
/

REM INSERTING into KREW_DOC_TYP_PROC_T
SET DEFINE OFF;
Insert into KREW_DOC_TYP_PROC_T (DOC_TYP_PROC_ID,DOC_TYP_ID,INIT_RTE_NODE_ID,NM,INIT_IND,VER_NBR) 
values (KREW_DOC_HDR_S.NEXTVAL,(SELECT MAX(KREW_DOC_TYP_T.DOC_TYP_ID) FROM KREW_DOC_TYP_T WHERE KREW_DOC_TYP_T.DOC_TYP_NM='AuthorizationRequestDocument'),KREW_RTE_NODE_S.CURRVAL,'PRIMARY',1,1)
/

----
Insert into KREW_RTE_NODE_T (RTE_NODE_ID,DOC_TYP_ID,NM,TYP,RTE_MTHD_NM,FNL_APRVR_IND,MNDTRY_RTE_IND,GRP_ID,RTE_MTHD_CD,ACTVN_TYP,BRCH_PROTO_ID,NEXT_DOC_STAT,VER_NBR) 
values (KREW_RTE_NODE_S.NEXTVAL,(SELECT MAX(KREW_DOC_TYP_T.DOC_TYP_ID) FROM KREW_DOC_TYP_T WHERE KREW_DOC_TYP_T.DOC_TYP_NM='AuthorizationRequestDocument'),'Kuali Student CM Admin','org.kuali.rice.kew.engine.node.RoleNode','org.kuali.rice.kew.role.RoleRouteModule',0,0,null,'RM','P',null,null,1)
/

Insert into KREW_RTE_NODE_CFG_PARM_T (RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,KEY_CD,VAL) 
values (KREW_RTE_NODE_CFG_PARM_S.NEXTVAL,KREW_RTE_NODE_S.CURRVAL,'contentFragment','<role name="Kuali Student CM Admin">
<activationType>P</activationType>
</role>
')
/

Insert into KREW_RTE_NODE_CFG_PARM_T (RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,KEY_CD,VAL) 
values (KREW_RTE_NODE_CFG_PARM_S.NEXTVAL,KREW_RTE_NODE_S.CURRVAL,'activationType','P')
/
Insert into KREW_RTE_NODE_CFG_PARM_T (RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,KEY_CD,VAL) 
values (KREW_RTE_NODE_CFG_PARM_S.NEXTVAL,KREW_RTE_NODE_S.CURRVAL,'ruleSelector','Template')
/

	
