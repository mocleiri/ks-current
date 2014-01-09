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
DELETE FROM
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
                    fo.CLU_ID=courses.fid
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
    newmatch.CO_ID=oldmatch.co_ID
AND newmatch.atype=oldmatch.atype
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
    newmatch.CO_ID=oldmatch.co_ID
AND newmatch.atype=oldmatch.atype)
/
ALTER TABLE KSEN_SCHED_RQST ADD SCHED_RQST_SET_ID VARCHAR2(255)
/
ALTER TABLE KSEN_SCHED_RQST ADD SCHED_ID VARCHAR2(255)
/