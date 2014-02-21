TRUNCATE TABLE KREW_RULE_EXT_T DROP STORAGE
/
-- orig id 1045
INSERT INTO KREW_RULE_EXT_T (RULE_EXT_ID,RULE_ID,RULE_TMPL_ATTR_ID,VER_NBR)
  VALUES (CONCAT('KS-', KS_RICE_ID_S.NEXTVAL), '1044', (SELECT RULE_TMPL_ATTR_ID FROM KREW_RULE_TMPL_ATTR_T WHERE RULE_ATTR_ID IN (SELECT RULE_ATTR_ID FROM KREW_RULE_ATTR_T WHERE NM = 'ChannelReviewerRoleAttribute') AND RULE_TMPL_ID IN (SELECT RULE_TMPL_ID from KREW_RULE_TMPL_T where NM = 'ReviewersRouting')), 1)
/

-- The following rows refer to a RULE_ID that does not exist in the KREW_RULE_T table
INSERT INTO KREW_RULE_EXT_T (RULE_EXT_ID,RULE_ID,RULE_TMPL_ATTR_ID,VER_NBR)
  VALUES ('1047','1046','1027',1)
/
INSERT INTO KREW_RULE_EXT_T (RULE_EXT_ID,RULE_ID,RULE_TMPL_ATTR_ID,VER_NBR)
  VALUES ('1104','1103','1102',1)
/
INSERT INTO KREW_RULE_EXT_T (RULE_EXT_ID,RULE_ID,RULE_TMPL_ATTR_ID,VER_NBR)
  VALUES ('1107','1106','1102',1)
/
