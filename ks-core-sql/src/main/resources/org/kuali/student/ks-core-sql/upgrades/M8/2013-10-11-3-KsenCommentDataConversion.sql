INSERT INTO KSEN_COMMENT
(ID, OBJ_ID, COMMENT_TYPE, COMMENT_STATE, COMMENT_PLAIN, COMMENT_FORMATTED, COMMENTER_ID,
	REF_OBJECT_TYPE, REF_OBJECT_ID, EFF_DT, EXPIR_DT, VER_NBR,
	CREATETIME, CREATEID, UPDATETIME, UPDATEID)
SELECT CO.ID, CO.OBJ_ID, CO.TYPE, CO.STATE, TEXT.PLAIN, TEXT.FORMATTED,
       CO.CREATEID, REF.REFERENCE_TYPE, REF.REFERENCE_ID, CO.EFF_DT, CO.EXPIR_DT, CO.VER_NBR, CO.CREATETIME,
       CO.CREATEID, CO.UPDATETIME, CO.UPDATEID
FROM KSCO_COMMENT CO, KSCO_REFERENCE REF, KSCO_RICH_TEXT_T TEXT
WHERE CO.REFERENCE = REF.ID AND CO.RT_DESCR_ID = TEXT.ID
/

INSERT INTO KSEN_COMMENT_ATTR
(ID, OBJ_ID, ATTR_KEY, ATTR_VALUE, OWNER_ID)
  SELECT
    ID,
    OBJ_ID,
    ATTR_NAME,
    ATTR_VALUE,
    OWNER
  FROM KSCO_COMMENT_ATTR
/
DROP TABLE KSCO_RICH_TEXT_T CASCADE CONSTRAINTS PURGE
/
DROP TABLE KSCO_REFERENCE_TYPE_ATTR CASCADE CONSTRAINTS PURGE
/
DROP TABLE KSCO_REFERENCE_TYPE CASCADE CONSTRAINTS PURGE
/
DROP TABLE KSCO_REFERENCE CASCADE CONSTRAINTS PURGE
/
DROP TABLE KSCO_COMMENT_ATTR CASCADE CONSTRAINTS PURGE
/
DROP TABLE KSCO_COMMENT_TYPE_ATTR CASCADE CONSTRAINTS PURGE
/
DROP TABLE KSCO_COMMENT_TYPE CASCADE CONSTRAINTS PURGE
/
DROP TABLE KSCO_COMMENT CASCADE CONSTRAINTS PURGE
/

--DROP the TAG Tables
DROP TABLE KSCO_TAG_TYPE_ATTR CASCADE CONSTRAINTS PURGE
/
DROP TABLE KSCO_TAG_TYPE CASCADE CONSTRAINTS PURGE
/
DROP TABLE KSCO_TAG_ATTR CASCADE CONSTRAINTS PURGE
/
DROP TABLE KSCO_TAG CASCADE CONSTRAINTS PURGE
/
