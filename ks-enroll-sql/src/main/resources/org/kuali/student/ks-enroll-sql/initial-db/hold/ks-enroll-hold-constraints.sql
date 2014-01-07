

ALTER TABLE KSEN_HOLD
    ADD CONSTRAINT KSEN_HOLD_FK1 FOREIGN KEY (ISSUE_ID)
    REFERENCES KSEN_HOLD_ISSUE (ID)
/


ALTER TABLE KSEN_HOLD_ATTR
    ADD CONSTRAINT KSEN_HOLD_ATTR_FK1 FOREIGN KEY (OWNER_ID)
    REFERENCES KSEN_HOLD (ID)
/



ALTER TABLE KSEN_HOLD_ISSUE_ATTR
    ADD CONSTRAINT KSEN_HOLD_ISSUE_ATTR_FK1 FOREIGN KEY (OWNER_ID)
    REFERENCES KSEN_HOLD_ISSUE (ID)
/

