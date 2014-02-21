CREATE  INDEX KSEN_SCHED_RQST_SET_I1 ON KSEN_SCHED_RQST_SET
(SCHED_RQST_SET_TYPE   ASC)
/


CREATE  INDEX KSEN_SCHED_RQST_SET_I2 ON KSEN_SCHED_RQST_SET
(REF_OBJECT_TYPE   ASC)
/


CREATE  INDEX KSEN_SCHED_RQST_SET_ATTR_IF1 ON KSEN_SCHED_RQST_SET_ATTR
(OWNER_ID   ASC)
/

CREATE  INDEX KSEN_SCHED_REF_OBJECT_IF1 ON KSEN_SCHED_REF_OBJECT
(SCHED_RQST_SET_ID   ASC)
/


ALTER TABLE KSEN_SCHED_REF_OBJECT
        ADD (CONSTRAINT KSEN_SCHED_REF_OBJECT_FK1 FOREIGN KEY (SCHED_RQST_SET_ID) REFERENCES KSEN_SCHED_RQST_SET (ID))
/

ALTER TABLE KSEN_SCHED_RQST
        ADD (CONSTRAINT KSEN_SCHED_RQST_FK2 FOREIGN KEY (SCHED_RQST_SET_ID) REFERENCES KSEN_SCHED_RQST_SET (ID))
/

ALTER TABLE KSEN_SCHED_RQST_SET_ATTR
        ADD (CONSTRAINT KSEN_SCHED_RQST_SET_ATTR_FK1 FOREIGN KEY (OWNER_ID) REFERENCES KSEN_SCHED_RQST_SET (ID))
/

