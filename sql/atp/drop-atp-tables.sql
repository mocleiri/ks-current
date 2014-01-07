DECLARE
  tables SYS.DBMS_DEBUG_VC2COLL := SYS.DBMS_DEBUG_VC2COLL(
         'ksen_atp',
         'ksen_atpatp_reltn',
         'ksen_atpatp_reltn_attr',
         'ksen_atpmstone_reltn',
         'ksen_atp_attr',
         'ksen_mstone',
         'ksen_mstone_attr');
  drop_sql VARCHAR2(2000);
BEGIN
  FOR i IN tables.first..tables.last
  LOOP
    BEGIN
       drop_sql := 'DROP TABLE "'
                                || tables(i)
                                || '" CASCADE CONSTRAINTS';
       EXECUTE IMMEDIATE drop_sql;
    EXCEPTION
       WHEN OTHERS THEN
          
          IF SQLCODE != -942 THEN
             RAISE;
          END IF;
    END;
  END LOOP;
END;

