-- REM INSERTING into KREW_DOC_TYP_T
Insert into KREW_DOC_TYP_T (DOC_TYP_ID,PARNT_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,ACTV_IND,CUR_IND,DOC_TYP_DESC,LBL,PREV_DOC_TYP_VER_NBR,DOC_HDR_ID,DOC_HDLR_URL,HELP_DEF_URL,DOC_SEARCH_HELP_URL,POST_PRCSR,GRP_ID,BLNKT_APPR_GRP_ID,BLNKT_APPR_PLCY,RPT_GRP_ID,RTE_VER_NBR,NOTIFY_ADDR,SEC_XML,EMAIL_XSL,APPL_ID,OBJ_ID,VER_NBR) values (
	KREW_DOC_HDR_S.NEXTVAL,
	(SELECT MAX(KREW_DOC_TYP_T.DOC_TYP_ID)
    FROM KREW_DOC_TYP_T
    WHERE KREW_DOC_TYP_T.DOC_TYP_NM='RiceDocument'
    )
	,'OrgInfoAdminMaintenanceDocument',0,1,1,'Create a New Org Maintenance Document','Org Info Maintenance Document',null,null,'${application.url}/kr-krad/maintenance?methodToCall=docHandler&dataObjectClassName=org.kuali.student.r2.core.organization.dto.OrgInfo',null,null,'org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor','1','1',null,null,'1',null, EMPTY_CLOB(),null,null,SYS_GUID(),1);


-- REM INSERTING into KREW_RTE_NODE_T
Insert into KREW_RTE_NODE_T (RTE_NODE_ID,DOC_TYP_ID,NM,TYP,RTE_MTHD_NM,FNL_APRVR_IND,MNDTRY_RTE_IND,GRP_ID,RTE_MTHD_CD,ACTVN_TYP,BRCH_PROTO_ID,NEXT_DOC_STAT,VER_NBR) values (KREW_RTE_NODE_S.NEXTVAL,(SELECT MAX(KREW_DOC_TYP_T.DOC_TYP_ID) FROM KREW_DOC_TYP_T WHERE KREW_DOC_TYP_T.DOC_TYP_NM='OrgInfoAdminMaintenanceDocument'),'Initiated','org.kuali.rice.kew.engine.node.InitialNode',null,0,0,'1',null,'P',null,null,1);


-- REM INSERTING into KREW_RTE_NODE_CFG_PARM_T
Insert into KREW_RTE_NODE_CFG_PARM_T (RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,KEY_CD,VAL) 
values (KREW_RTE_NODE_CFG_PARM_S.NEXTVAL,KREW_RTE_NODE_S.CURRVAL,'contentFragment','<start name="Initiated">
<activationType>P</activationType>
<mandatoryRoute>false</mandatoryRoute>
<finalApproval>false</finalApproval>
</start>
')
/

Insert into KREW_RTE_NODE_CFG_PARM_T (RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,KEY_CD,VAL) 
values (KREW_RTE_NODE_CFG_PARM_S.NEXTVAL,KREW_RTE_NODE_S.CURRVAL,'activationType','P')
/

Insert into KREW_RTE_NODE_CFG_PARM_T (RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,KEY_CD,VAL) 
values (KREW_RTE_NODE_CFG_PARM_S.NEXTVAL,KREW_RTE_NODE_S.CURRVAL,'mandatoryRoute','false')
/

Insert into KREW_RTE_NODE_CFG_PARM_T (RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,KEY_CD,VAL) 
values (KREW_RTE_NODE_CFG_PARM_S.NEXTVAL,KREW_RTE_NODE_S.CURRVAL,'finalApproval','false')
/

Insert into KREW_RTE_NODE_CFG_PARM_T (RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,KEY_CD,VAL) 
values (KREW_RTE_NODE_CFG_PARM_S.NEXTVAL,KREW_RTE_NODE_S.CURRVAL,'ruleSelector','Template')
/

