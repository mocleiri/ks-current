-- RICE
-- no problems

-- KS-CORE
ALTER TABLE KSCO_REFERENCE RENAME CONSTRAINT SYS_C0033779 TO KSCO_REFERENCE_U1
/
ALTER INDEX SYS_C0033779 RENAME TO KSCO_REFERENCE_U1
/
ALTER TABLE KSOR_ORG_PERS_RELTN RENAME CONSTRAINT SYS_C0011691 TO KSOR_ORG_PERS_RELTN_U1
/
ALTER INDEX SYS_C0011691 RENAME TO KSOR_ORG_PERS_RELTN_U1
/
ALTER TABLE KSOR_ORG_POS_RESTR RENAME CONSTRAINT SYS_C0011701 TO KSOR_ORG_POS_RESTR_U1
/
ALTER INDEX SYS_C0011701 RENAME TO KSOR_ORG_POS_RESTR_U1
/
ALTER TABLE KSST_OBJ_TYP_JN_OBJ_SUB_TYP RENAME CONSTRAINT SYS_C0011792 TO KSST_OBJ_TYP_JN_OBJ_SUB_TYP_U1
/
ALTER INDEX SYS_C0011792 RENAME TO KSST_OBJ_TYP_JN_OBJ_SUB_TYP_U1
/
ALTER TABLE KSST_RC_JN_RC_FIELD RENAME CONSTRAINT SYS_C0011797 TO KSST_RC_JN_RC_FIELD_U1
/
ALTER INDEX SYS_C0011797 RENAME TO KSST_RC_JN_RC_FIELD_U1
/
ALTER TABLE KSST_STMT_JN_STMT RENAME CONSTRAINT SYS_C0011839 TO KSST_STMT_JN_STMT_U1
/
ALTER INDEX SYS_C0011839 RENAME TO KSST_STMT_JN_STMT_U1
/
ALTER TABLE KSEM_CTX_T RENAME CONSTRAINT SYS_C00285110 TO KSEM_CTX_T_U1
/
ALTER INDEX SYS_C00285110 RENAME TO KSEM_CTX_T_U1
/
ALTER TABLE KSEN_MSTONE_ATTR RENAME CONSTRAINT FK3DFA6EE1BA0FC113 TO KSEN_MSTONE_ATTR_FK1
/


-- KS-LUM
ALTER TABLE KSLO_LO_JN_LOCATEGORY RENAME CONSTRAINT SYS_C0011301 TO KSLO_LO_JN_LOCATEGORY_U1
/
ALTER INDEX SYS_C0011301 RENAME TO KSLO_LO_JN_LOCATEGORY_U1
/
ALTER TABLE KSLU_CLU RENAME CONSTRAINT SYS_C0011370 TO KSLU_CLU_U1
/
ALTER INDEX SYS_C0011370 RENAME TO KSLU_CLU_U1
/
ALTER TABLE KSLU_CLURES_JN_RESOPT RENAME CONSTRAINT SYS_C0011378 TO KSLU_CLURES_JN_RESOPT_U1
/
ALTER INDEX SYS_C0011378 RENAME TO KSLU_CLURES_JN_RESOPT_U1
/
ALTER TABLE KSLU_CLU_JN_ACCRED RENAME CONSTRAINT SYS_C0011436 TO KSLU_CLU_JN_ACCRED_U1
/
ALTER INDEX SYS_C0011436 RENAME TO KSLU_CLU_JN_ACCRED_U1
/
ALTER TABLE KSLU_CLU_JN_CLU_IDENT RENAME CONSTRAINT SYS_C0011441 TO KSLU_CLU_JN_CLU_IDENT_U1
/
ALTER INDEX SYS_C0011441 RENAME TO KSLU_CLU_JN_CLU_IDENT_U1
/
ALTER TABLE KSLU_CLU_JN_CLU_INSTR RENAME CONSTRAINT SYS_C0011444 TO KSLU_CLU_JN_CLU_INSTR_U1
/
ALTER INDEX SYS_C0011444 RENAME TO KSLU_CLU_JN_CLU_INSTR_U1
/
ALTER TABLE KSLU_CLU_PUBL_JN_CLU_INSTR RENAME CONSTRAINT SYS_C009456 TO KSLU_CLU_PUBL_JN_CLU_INSTR_U1
/
ALTER INDEX SYS_C009456 RENAME TO KSLU_CLU_PUBL_JN_CLU_INSTR_U1
/
ALTER TABLE KSLU_CLU_PUBL_VARI RENAME CONSTRAINT SYS_C0011470 TO KSLU_CLU_PUBL_VARI_U1
/
ALTER INDEX SYS_C0011470 RENAME TO KSLU_CLU_PUBL_VARI_U1
/
ALTER TABLE KSLU_MEMSHIP_KSLU_SPARAM RENAME CONSTRAINT SYS_C0011543 TO KSLU_MEMSHIP_KSLU_SPARAM_U1
/
ALTER INDEX SYS_C0011543 RENAME TO KSLU_MEMSHIP_KSLU_SPARAM_U1
/
ALTER TABLE KSLU_RSRC RENAME CONSTRAINT SYS_C0011561 TO KSLU_RSRC_U1
/
ALTER INDEX SYS_C0011561 RENAME TO KSLU_RSRC_U1
/
ALTER TABLE KSLU_SPARAM_KSLU_SPVALUE RENAME CONSTRAINT SYS_C0011566 TO KSLU_SPARAM_KSLU_SPVALUE_U1
/
ALTER INDEX SYS_C0011566 RENAME TO KSLU_SPARAM_KSLU_SPVALUE_U1
/



-- KS-ENROLL
ALTER TABLE KSEN_LUI_IDENT ADD IDENT_LEVEL varchar2(255)
/
ALTER TABLE KSEN_LUI_IDENT ADD ORGID varchar2(255)
/
--String aggregation Function (needed to group formats by the activity types)
create or replace type vcArray as table of varchar2(4000)
/
create or replace type string_agg_type as object
    (
       data  vcArray,

     static function
          ODCIAggregateInitialize(sctx IN OUT string_agg_type )
          return number,

     member function
          ODCIAggregateIterate(self IN OUT string_agg_type ,
                               value IN varchar2 )
         return number,

     member function
          ODCIAggregateTerminate(self IN string_agg_type,
                                 returnValue OUT  varchar2,
                                 flags IN number)
          return number,

     member function
          ODCIAggregateMerge(self IN OUT string_agg_type,
                             ctx2 IN string_agg_type)
          return number
);
/
create or replace type body string_agg_type
   is

   static function ODCIAggregateInitialize(sctx IN OUT string_agg_type)
   return number
   is
   begin
       sctx := string_agg_type( vcArray() );
       return ODCIConst.Success;
   end;

   member function ODCIAggregateIterate(self IN OUT string_agg_type,
                                        value IN varchar2 )
   return number
   is
   begin
       data.extend;
       data(data.count) := value;
       return ODCIConst.Success;
   end;

   member function ODCIAggregateTerminate(self IN string_agg_type,
                                          returnValue OUT varchar2,
                                          flags IN number)
   return number
   is
       l_data varchar2(4000);
   begin
       for x in ( select column_value from TABLE(data) order by 1 )
       loop
               l_data := l_data || ',' || x.column_value;
       end loop;
       returnValue := ltrim(l_data,',');
       return ODCIConst.Success;
   end;

   member function ODCIAggregateMerge(self IN OUT string_agg_type,
                                      ctx2 IN string_agg_type)
   return number
   is
   begin -- not really tested ;)
       for i in 1 .. ctx2.data.count
       loop
               data.extend;
               data(data.count) := ctx2.data(i);
       end loop;
       return ODCIConst.Success;
   end;


   end;
/
CREATE or replace
   FUNCTION stragg(input varchar2 )
   RETURN varchar2
   PARALLEL_ENABLE AGGREGATE USING string_agg_type;
/
--Delete all the duplicated formats that don't have any LUIs attached to them
DELETE
    KSLU_CLUCLU_RELTN cr
WHERE
    RELATED_CLU_ID IN
    (
        SELECT
            fid
        FROM
            (
                SELECT
                    COUNT(fo.id) foc,
                    courses.cid,
                    courses.fid,
                    courses.activities,
                    ROW_NUMBER() over (partition BY courses.cid, activities ORDER BY activities,
                    COUNT( fo.id) DESC) r
                FROM
                    KSEN_LUI fo,
                    (
                        SELECT
                            cid,
                            fid,
                            stragg(atype) activities
                        FROM
                            (
                                SELECT
                                    c.id cid,
                                    f.id fid,
                                    a.LUTYPE_ID atype
                                FROM
                                    KSLU_CLU c,
                                    KSLU_CLU f,
                                    KSLU_CLU a,
                                    KSLU_CLUCLU_RELTN cfr,
                                    KSLU_CLUCLU_RELTN far
                                WHERE
                                    c.id = cfr.CLU_ID
                                AND f.id = cfr.RELATED_CLU_ID
                                AND f.id = far.clu_id
                                AND a.id = far.RELATED_CLU_ID
                                AND f.LUTYPE_ID='kuali.lu.type.CreditCourseFormatShell'
                                ORDER BY
                                    cid,
                                    fid,
                                    atype
                            )
                        GROUP BY
                            cid,
                            fid
                    )
                    courses
                WHERE
                    fo.CLU_ID(+)=courses.fid
                GROUP BY
                    courses.cid,
                    courses.fid,
                    courses.activities
            )
        WHERE
            r>1
        AND foc=0
    )
/
--This script should remove orphaned formats and their activities

--Delete orphaned activities
--Delete activity attributes
DELETE KSLU_CLU_ATTR WHERE KSLU_CLU_ATTR.OWNER IN (SELECT far.RELATED_CLU_ID FROM KSLU_CLUCLU_RELTN far where far.CLU_ID IN(SELECT orphanF.ID FROM KSLU_CLU orphanf WHERE orphanf.LUTYPE_ID='kuali.lu.type.CreditCourseFormatShell' AND NOT EXISTS (SELECT * FROM KSLU_CLUCLU_RELTN orphanfrel WHERE orphanfrel.RELATED_CLU_ID=orphanf.id)))
/
--Delete activity identifiers
DELETE KSLU_CLU_IDENT WHERE KSLU_CLU_IDENT.ID IN (SELECT a.OFFIC_CLU_ID FROM KSLU_CLUCLU_RELTN far, KSLU_CLU a WHERE far.RELATED_CLU_ID = a.ID and far.CLU_ID in(SELECT orphanF.ID FROM KSLU_CLU orphanf WHERE orphanf.LUTYPE_ID='kuali.lu.type.CreditCourseFormatShell' AND NOT EXISTS (SELECT * FROM KSLU_CLUCLU_RELTN orphanfrel WHERE orphanfrel.RELATED_CLU_ID=orphanf.id)))
/
--DELETE activity relation for orphaned formats
DELETE KSLU_CLUCLU_RELTN WHERE CLU_ID IN (SELECT orphanF.ID FROM KSLU_CLU orphanf WHERE orphanf.LUTYPE_ID='kuali.lu.type.CreditCourseFormatShell' AND NOT EXISTS (SELECT * FROM KSLU_CLUCLU_RELTN orphanfrel WHERE orphanfrel.RELATED_CLU_ID=orphanf.id))
/
--Delete activities that are orphaned
DELETE KSLU_CLU orphana WHERE LUTYPE_ID LIKE 'kuali.lu.type.activity.%' AND NOT EXISTS (SELECT * FROM KSLU_CLUCLU_RELTN orphanarel WHERE orphanarel.RELATED_CLU_ID=orphana.id)
/
--Delete orphaned Formats
--Delete format attributes
DELETE KSLU_CLU_ATTR WHERE KSLU_CLU_ATTR.OWNER IN (SELECT orphanF.ID FROM KSLU_CLU orphanf WHERE orphanf.LUTYPE_ID='kuali.lu.type.CreditCourseFormatShell' AND NOT EXISTS (SELECT * FROM KSLU_CLUCLU_RELTN orphanfrel WHERE orphanfrel.RELATED_CLU_ID=orphanf.id))
/
--Delete activity identifiers
DELETE KSLU_CLU_IDENT WHERE KSLU_CLU_IDENT.ID IN (SELECT orphanF.OFFIC_CLU_ID FROM KSLU_CLU orphanf WHERE orphanf.LUTYPE_ID='kuali.lu.type.CreditCourseFormatShell' AND NOT EXISTS (SELECT * FROM KSLU_CLUCLU_RELTN orphanfrel WHERE orphanfrel.RELATED_CLU_ID=orphanf.id))
/
--Delete activities that are orphaned
DELETE KSLU_CLU orphanf WHERE LUTYPE_ID = 'kuali.lu.type.CreditCourseFormatShell' AND NOT EXISTS (SELECT * FROM KSLU_CLUCLU_RELTN orphanfrel WHERE orphanfrel.RELATED_CLU_ID=orphanf.id)
/
CREATE TABLE co_to_new_clu
(
  CO_ID VARCHAR2(255),
  NEWCLU_ID VARCHAR2(255)
)
/
INSERT INTO co_to_new_clu (CO_ID, NEWCLU_ID)
          SELECT
                  co.ID CO_ID,
                  newC.id newClu_ID
  FROM
          KSLU_CLU oldC,
                  KSLU_CLU newC,
                  KSEN_LUI co,
                  KSEN_ATP coAtp,
                  KSEN_ATP newCStartTerm,
                  KSEN_ATP newCEndTerm
  WHERE
          co.CLU_ID = oldC.ID
                  AND co.ATP_ID = coAtp.ID
                  AND oldC.VER_IND_ID = newC.VER_IND_ID
                  AND newC.EXP_FIRST_ATP = newCStartTerm.ID
                  AND newC.LAST_ATP = newCEndTerm.ID(+)
                  AND coAtp.START_DT >= newCStartTerm.START_DT
                  AND
                  (
                          newCEndTerm.END_DT IS NULL
                                  OR newCEndTerm.END_DT>coAtp.START_DT
                          )
                  AND oldC.ID!=newC.ID
/
--Fix formats to point to correct version
UPDATE KSEN_LUI updatef SET CLU_ID=
(SELECT
    newMatch.fid newformatId
FROM
    (
        SELECT
            CO_ID,
            newCLU_ID,
            fid,
            stragg(atype) atype
        FROM
            (
                SELECT
                    CO_ID,
                    newCLU_ID,
                    f.id fid,
                    a.LUTYPE_ID atype
                FROM
                    KSLU_CLU f,
                    KSLU_CLU a,
                    KSLU_CLUCLU_RELTN cfr,
                    KSLU_CLUCLU_RELTN far,
                    co_to_new_clu newmap
                WHERE
                    newCLU_ID = cfr.CLU_ID
                AND F.ID = cfr.RELATED_CLU_ID
                AND F.ID = far.CLU_ID
                AND A.ID = far.RELATED_CLU_ID
                ORDER BY
                    CO_ID,
                    newCLU_ID,
                    fid,
                    atype
            )
        GROUP BY
            CO_ID,
            newCLU_ID,
            fid
    )
    newMatch,
    (
        SELECT
            CO_ID,
            foid,
            fid,
            stragg(atype) atype
        FROM
            (
                SELECT
                    CO_ID,
                    fo.id foid,
                    f.id fid,
                    a.LUTYPE_ID atype
                FROM
                    KSEN_LUI fo,
                    KSEN_LUILUI_RELTN cofor,
                    KSLU_CLU f,
                    KSLU_CLU a,
                    KSLU_CLUCLU_RELTN far,
                    co_to_new_clu newmap
                WHERE
                    newmap.CO_ID = cofor.LUI_ID
                AND fo.ID = cofor.RELATED_LUI_ID
                AND F.ID = fo.CLU_ID
                AND F.ID = far.CLU_ID
                AND A.ID = far.RELATED_CLU_ID
                ORDER BY
                    CO_ID,
                    FOID,
                    FID,
                    ATYPE
            )
        GROUP BY
            CO_ID,
            FOID,
            FID
    )
    oldMatch
WHERE
    newmatch.CO_ID(+)=oldmatch.co_ID
AND newmatch.atype(+)=oldmatch.atype
and oldmatch.foid=updatef.id)
where
updatef.id in
(SELECT
    oldMatch.foid
FROM
    (
        SELECT
            CO_ID,
            newCLU_ID,
            fid,
            stragg(atype) atype
        FROM
            (
                SELECT
                    CO_ID,
                    newCLU_ID,
                    f.id fid,
                    a.LUTYPE_ID atype
                FROM
                    KSLU_CLU f,
                    KSLU_CLU a,
                    KSLU_CLUCLU_RELTN cfr,
                    KSLU_CLUCLU_RELTN far,
                    co_to_new_clu newmap
                WHERE
                    newCLU_ID = cfr.CLU_ID
                AND F.ID = cfr.RELATED_CLU_ID
                AND F.ID = far.CLU_ID
                AND A.ID = far.RELATED_CLU_ID
                ORDER BY
                    CO_ID,
                    newCLU_ID,
                    fid,
                    atype
            )
        GROUP BY
            CO_ID,
            newCLU_ID,
            fid
    )
    newMatch,
    (
        SELECT
            CO_ID,
            foid,
            fid,
            stragg(atype) atype
        FROM
            (
                SELECT
                    CO_ID,
                    fo.id foid,
                    f.id fid,
                    a.LUTYPE_ID atype
                FROM
                    KSEN_LUI fo,
                    KSEN_LUILUI_RELTN cofor,
                    KSLU_CLU f,
                    KSLU_CLU a,
                    KSLU_CLUCLU_RELTN far,
                    co_to_new_clu newmap
                WHERE
                    newmap.CO_ID = cofor.LUI_ID
                AND fo.ID = cofor.RELATED_LUI_ID
                AND F.ID = fo.CLU_ID
                AND F.ID = far.CLU_ID
                AND A.ID = far.RELATED_CLU_ID
                ORDER BY
                    CO_ID,
                    FOID,
                    FID,
                    ATYPE
            )
        GROUP BY
            CO_ID,
            FOID,
            FID
    )
    oldMatch
WHERE
    newmatch.CO_ID(+)=oldmatch.co_ID
AND newmatch.atype(+)=oldmatch.atype)
/
--This script updates COs to point to courses with
UPDATE KSEN_LUI l set CLU_ID=(
SELECT
    newC.id
FROM
    KSLU_CLU oldC,
    KSLU_CLU newC,
    KSEN_LUI co,
    KSEN_ATP coAtp,
    KSEN_ATP newCStartTerm,
    KSEN_ATP newCEndTerm
WHERE
    l.id=co.id
AND co.CLU_ID = oldC.ID
AND co.ATP_ID = coAtp.ID
AND oldC.VER_IND_ID = newC.VER_IND_ID
AND newC.EXP_FIRST_ATP = newCStartTerm.ID
AND newC.LAST_ATP = newCEndTerm.ID(+)
AND coAtp.START_DT >= newCStartTerm.START_DT
AND
    (
        newCEndTerm.END_DT IS NULL
     OR newCEndTerm.END_DT>coAtp.START_DT
    )
and oldC.ID!=newC.ID)
WHERE
l.LUI_TYPE='kuali.lui.type.course.offering' and
 EXISTS(
SELECT
    co.ID CO_ID, oldC.ID oldClu, newC.id newClu_ID, co.LUI_TYPE, co.ATP_ID LUI_ATP, newCStartTerm.ID,newCEndTerm.ID
FROM
    KSLU_CLU oldC,
    KSLU_CLU newC,
    KSEN_LUI co,
    KSEN_ATP coAtp,
    KSEN_ATP newCStartTerm,
    KSEN_ATP newCEndTerm
WHERE
    l.id=co.id
AND co.CLU_ID = oldC.ID
AND co.ATP_ID = coAtp.ID
AND oldC.VER_IND_ID = newC.VER_IND_ID
AND newC.EXP_FIRST_ATP = newCStartTerm.ID
AND newC.LAST_ATP = newCEndTerm.ID(+)
AND coAtp.START_DT >= newCStartTerm.START_DT
AND
    (
        newCEndTerm.END_DT IS NULL
     OR newCEndTerm.END_DT>coAtp.START_DT
    )
and oldC.ID!=newC.ID)
/
-- Delete the weird LRC result values
DELETE
    KSEN_LRC_RESULT_VALUE rv
WHERE
    rv.RESULT_SCALE_ID='kuali.result.scale.credit.degree'
AND id NOT LIKE 'kuali.result.value.credit.degree.%'
/
ALTER TABLE KSEN_STATE
ADD IS_INITIAL_STATE number(1) default 0 NOT NULL
/
DROP SEQUENCE KRMS_AGENDA_S
/
DROP SEQUENCE KRMS_AGENDA_ITM_S
/
DROP SEQUENCE KRMS_CMPND_PROP_PROPS_S
/
DROP SEQUENCE KRMS_CNTXT_S
/
DROP SEQUENCE KRMS_PROP_S
/
DROP SEQUENCE KRMS_PROP_PARM_S
/
DROP SEQUENCE KRMS_REF_OBJ_KRMS_OBJ_S
/
DROP SEQUENCE KRMS_RULE_S
/
DROP SEQUENCE KRMS_TERM_S
/
DROP SEQUENCE KRMS_TERM_PARM_S
/
DROP SEQUENCE KRMS_TERM_SPEC_S
/
DROP SEQUENCE KRMS_TYP_S
/
DROP SEQUENCE KRMS_TYP_RELN_S
/
CREATE SEQUENCE KRMS_AGENDA_S MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 10143 NOCACHE ORDER NOCYCLE
/
CREATE SEQUENCE KRMS_AGENDA_ITM_S MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 10149 NOCACHE ORDER NOCYCLE
/
CREATE SEQUENCE KRMS_CMPND_PROP_PROPS_S MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 10252 NOCACHE ORDER NOCYCLE
/
CREATE SEQUENCE KRMS_CNTXT_S MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 10002 NOCACHE ORDER NOCYCLE
/
CREATE SEQUENCE KRMS_PROP_S MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 10254 NOCACHE ORDER NOCYCLE
/
CREATE SEQUENCE KRMS_PROP_PARM_S MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 10601 NOCACHE ORDER NOCYCLE
/
CREATE SEQUENCE KRMS_REF_OBJ_KRMS_OBJ_S MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 10143 NOCACHE ORDER NOCYCLE
/
CREATE SEQUENCE KRMS_RULE_S MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 10149 NOCACHE ORDER NOCYCLE
/
CREATE SEQUENCE KRMS_TERM_S MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 10201 NOCACHE ORDER NOCYCLE
/
CREATE SEQUENCE KRMS_TERM_PARM_S MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 10214 NOCACHE ORDER NOCYCLE
/
CREATE SEQUENCE KRMS_TERM_SPEC_S MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 10015 NOCACHE ORDER NOCYCLE
/
CREATE SEQUENCE KRMS_TYP_S MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 10077 NOCACHE ORDER NOCYCLE
/
CREATE SEQUENCE KRMS_TYP_RELN_S MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 10117 NOCACHE ORDER NOCYCLE
/
CREATE TABLE KSEN_LUI_SCHEDULE
(
	LUI_ID               VARCHAR2(255) NOT NULL ,
	SCHED_ID             VARCHAR2(255) NOT NULL
)
/
ALTER TABLE KSEN_SCHED_RQST ADD SCHED_RQST_SET_ID VARCHAR2(255 BYTE)
/
ALTER TABLE KSEN_SCHED_RQST ADD SCHED_ID VARCHAR2(255 BYTE)
/
DECLARE TEMP NUMBER;
BEGIN
  SELECT COUNT(*) INTO TEMP FROM USER_TABLES WHERE TABLE_NAME = 'KSEN_SCHED_RQST_SET_';
  IF TEMP > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KSEN_SCHED_RQST_SET  CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
CREATE TABLE KSEN_SCHED_RQST_SET
(
  ID                   VARCHAR2(255) NOT NULL ,
  OBJ_ID               VARCHAR2(36) NULL ,
  SCHED_RQST_SET_TYPE  VARCHAR2(255) NOT NULL ,
  SCHED_RQST_SET_STATE VARCHAR2(255) NOT NULL ,
  NAME                 VARCHAR2(255) NULL ,
  DESCR_PLAIN          VARCHAR2(4000) NULL ,
  DESCR_FORMATTED      VARCHAR2(4000) NULL ,
  REF_OBJECT_TYPE      VARCHAR2(255) NOT NULL ,
  MAX_ENRL_SHARED_IND  NUMBER NULL  CONSTRAINT  CHECK_BOOLEAN_908602616 CHECK (MAX_ENRL_SHARED_IND IN (1, 0)),
  MAX_ENRL             NUMBER NULL ,
  VER_NBR              NUMBER(19) NOT NULL ,
  CREATETIME           TIMESTAMP(6) NOT NULL ,
  CREATEID             VARCHAR2(255) NOT NULL ,
  UPDATETIME           TIMESTAMP(6) NULL ,
  UPDATEID             VARCHAR2(255) NULL
)
/
DECLARE TEMP NUMBER;
BEGIN
  SELECT COUNT(*) INTO TEMP FROM USER_TABLES WHERE TABLE_NAME = 'KSEN_SCHED_REF_OBJECT';
  IF TEMP > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KSEN_SCHED_REF_OBJECT CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
CREATE TABLE KSEN_SCHED_REF_OBJECT
(
  SCHED_RQST_SET_ID    VARCHAR2(255) NOT NULL ,
  REF_OBJECT_ID        VARCHAR2(255) NOT NULL
)
/
DECLARE TEMP NUMBER;
BEGIN
  SELECT COUNT(*) INTO TEMP FROM USER_TABLES WHERE TABLE_NAME = 'KSEN_SCHED_RQST_SET_ATTR';
  IF TEMP > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KSEN_SCHED_RQST_SET_ATTR CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
CREATE TABLE KSEN_SCHED_RQST_SET_ATTR
(
  OBJ_ID               VARCHAR2(36) NULL ,
  ATTR_KEY             VARCHAR2(255) NULL ,
  ATTR_VALUE           VARCHAR2(4000) NULL ,
  ID                   VARCHAR2(255) NOT NULL ,
  OWNER_ID             VARCHAR2(255) NULL
)
/
-- Populate SCHED_ID in KSEN_SCHED_RQST
create table SSRTEMP1_T as (select SCHED_ID, SCHED_RQST_SET_ID from KSEN_LUI_SCHEDULE a inner join KSEN_SCHED_REF_OBJECT b on a.LUI_ID = b.REF_OBJECT_ID)
/
-- Create a temp table for colosets
create table COLOSETS_T as (select REF_OBJECT_ID, SCHED_RQST_SET_ID from KSEN_SCHED_RQST where REF_OBJECT_TYPE = 'kuali.luiset.type.colocated.offering.set')
/
-- Create temp table with SRSID, MAXIND boolean
create table MAXIND_T as (select b.SCHED_RQST_SET_ID as SRSID, a.ATTR_VALUE as MAXIND from KSEN_LUI_SET_ATTR a, COLOSETS_T b where a.OWNER_ID = b.REF_OBJECT_ID and a.ATTR_KEY = 'kuali.attribute.colocatedset.is.max.enrollment.shared')
/
-- Create temp table with SRSID, MAXENR
create table MAXENR_T as (select b.SCHED_RQST_SET_ID as SRSID, a.ATTR_VALUE as MAXENR from KSEN_LUI_SET_ATTR a, COLOSETS_T b where a.OWNER_ID = b.REF_OBJECT_ID and a.ATTR_KEY = 'kuali.attribute.colocatedset.max.enrollment')
/
-- Drop redundant columns from KSEN_SCHED_RQST and KSEN_LUI
ALTER TABLE KSEN_SCHED_RQST DROP (REF_OBJECT_ID, REF_OBJECT_TYPE)
/
DECLARE TEMP NUMBER;
BEGIN
  SELECT COUNT(*) INTO TEMP FROM USER_TABLES WHERE TABLE_NAME = 'KSEN_SCHED_RQST_SET_ATTR';
        IF TEMP > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KSEN_SCHED_RQST_SET_ATTR CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
CREATE TABLE KSEN_SCHED_RQST_SET_ATTR
(
        OBJ_ID               VARCHAR2(36) NULL ,
        ATTR_KEY             VARCHAR2(255) NULL ,
        ATTR_VALUE           VARCHAR2(4000) NULL ,
        ID                   VARCHAR2(255) NOT NULL ,
        OWNER_ID             VARCHAR2(255) NULL
)
/
ALTER TABLE KSEN_SCHED_REF_OBJECT
        ADD CONSTRAINT KSEN_SCHED_REF_OBJECT_FK1 FOREIGN KEY (SCHED_RQST_SET_ID) REFERENCES KSEN_SCHED_RQST_SET (ID)
/

ALTER TABLE KSEN_SCHED_RQST
        ADD CONSTRAINT KSEN_SCHED_RQST_FK2 FOREIGN KEY (SCHED_RQST_SET_ID) REFERENCES KSEN_SCHED_RQST_SET (ID)
/

ALTER TABLE KSEN_SCHED_RQST_SET_ATTR
        ADD CONSTRAINT KSEN_SCHED_RQST_SET_ATTR_FK1 FOREIGN KEY (OWNER_ID) REFERENCES KSEN_SCHED_RQST_SET (ID)
/
--This removes all the cluset joins to non-existing clus
delete from KSLU_CLU_SET_JN_CLU csj
where csj.id in (Select csj.id
from KSLU_CLU_SET_JN_CLU csj,KSLU_CLU clu
where csj.clu_ver_ind_id = clu.ver_ind_id(+)
and clu.id is null)
/
---
-- Rename foreign key constraints with auto-generated names
---

ALTER TABLE KSEN_LRR RENAME CONSTRAINT FK1BE15Q7B1D2EA121 TO KSEN_LRR_FK1
/
ALTER TABLE KSEN_LRR RENAME CONSTRAINT FK1BE15Q7B1D2EF131 TO KSEN_LRR_FK2
/
ALTER TABLE KSEN_LRR_ATTR RENAME CONSTRAINT FKSF4BE635DC3CD510 TO KSEN_LRR_ATTR_FK1
/
create table jira7294_f_atypes as (select a.clu_id as fId2,  t.related_type_id as aotype2 from KSLU_CLUCLU_RELTN a inner join KSLU_CLU b on a.related_clu_id = b.id inner join KSEN_TYPETYPE_RELTN t on t.owner_type_id = b.lutype_id where a.lu_reltn_type_id = 'luLuRelationType.contains')
/
create table jira7294_fo_aotypes as (select lui_id as foId, subquery.clu_id as fId, related_lui_type as aotype  from ksen_lui_related_lui_types, (select id, clu_id from ksen_lui where lui_type = 'kuali.lui.type.course.format.offering') subquery where subquery.id = ksen_lui_related_lui_types.lui_id group by lui_id, subquery.clu_id,related_lui_type)
/
create table jira7294_missing as (select fo.foid, f.aotype2 as aotype from jira7294_fo_aotypes fo, jira7294_f_atypes f where fo.fid = f.fid2 and fo.aotype != f.aotype2 and (not exists (select * from jira7294_fo_aotypes fo2 where fo2.foid = fo.foid and fo2.fid = fo.fid and fo2.aotype = f.aotype2)))
/
CREATE TABLE KSEN_LUI_CAPACITY_ATTR
(
	ID                   VARCHAR2(255) NOT NULL ,
	OBJ_ID               VARCHAR2(36) NULL ,
	ATTR_KEY             VARCHAR2(255) NULL ,
	ATTR_VALUE           VARCHAR2(4000) NULL ,
	OWNER_ID             VARCHAR2(255) NULL
)
/