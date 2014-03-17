-- REM INSERTING into KREW_DOC_TYP_T
INSERT
INTO KREW_DOC_TYP_T (DOC_TYP_ID, PARNT_ID, DOC_TYP_NM, DOC_TYP_VER_NBR, ACTV_IND, CUR_IND, DOC_TYP_DESC, LBL, PREV_DOC_TYP_VER_NBR, DOC_HDR_ID, DOC_HDLR_URL, HELP_DEF_URL, DOC_SEARCH_HELP_URL, POST_PRCSR, GRP_ID, BLNKT_APPR_GRP_ID, BLNKT_APPR_PLCY, RPT_GRP_ID, RTE_VER_NBR, NOTIFY_ADDR, SEC_XML, EMAIL_XSL, APPL_ID, OBJ_ID, VER_NBR)
  VALUES
  (
    KREW_DOC_HDR_S.NEXTVAL,
    (SELECT MAX(KREW_DOC_TYP_T.DOC_TYP_ID)
    FROM KREW_DOC_TYP_T
    WHERE KREW_DOC_TYP_T.DOC_TYP_NM='kuali.proposal.type.course.modify'),
    'kuali.proposal.type.course.modify.current.version',
    0,1,1,
    'Credit Course Edit Current Version',
    'Credit Course Edit Current Version',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'org.kuali.student.lum.workflow.CoursePostProcessorBase',
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
