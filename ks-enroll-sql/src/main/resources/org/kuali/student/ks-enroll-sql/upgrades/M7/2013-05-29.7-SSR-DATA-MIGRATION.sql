-- Drop redundant columns from KSEN_SCHED_RQST and KSEN_LUI
ALTER TABLE KSEN_SCHED_RQST DROP COLUMN REF_OBJECT_ID
/

ALTER TABLE KSEN_SCHED_RQST DROP COLUMN REF_OBJECT_TYPE
/

ALTER TABLE KSEN_LUI DROP COLUMN SCHEDULE_ID
/
