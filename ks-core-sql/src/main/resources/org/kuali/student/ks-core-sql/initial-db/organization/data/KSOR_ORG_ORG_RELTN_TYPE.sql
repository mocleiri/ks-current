TRUNCATE TABLE KSOR_ORG_ORG_RELTN_TYPE DROP STORAGE
/
INSERT INTO KSOR_ORG_ORG_RELTN_TYPE (EFF_DT,NAME,OBJ_ID,ORG_HIRCHY,TYPE_DESC,TYPE_KEY,VER_NBR)
  VALUES (TO_DATE( '20121024191132', 'YYYYMMDDHH24MISS' ),'Parent 2 Child','E6F45138A7394EB89969CC6891357BCC','kuali.org.hierarchy.Curriculum','Parent to Child','kuali.org.Parent2CurriculumChild',0)
/
