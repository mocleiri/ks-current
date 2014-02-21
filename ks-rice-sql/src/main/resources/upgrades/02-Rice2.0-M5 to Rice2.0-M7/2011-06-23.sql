CREATE SEQUENCE KRMS_CTGRY_S INCREMENT BY 1 START WITH 1 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

CREATE TABLE KRMS_CTGRY_T
(
    CTGRY_ID VARCHAR2(40) NOT NULL
      , NM VARCHAR2(255) NOT NULL
      , NMSPC_CD VARCHAR2(40) NOT NULL
      , VER_NBR NUMBER(8) DEFAULT 0
    , PRIMARY KEY (CTGRY_ID)
    , CONSTRAINT KRMS_CTGRY_TC0 UNIQUE (NM, NMSPC_CD)
)
/

CREATE TABLE KRMS_TERM_SPEC_CTGRY_T
(
  TERM_SPEC_ID VARCHAR2(40) NOT NULL
      , CTGRY_ID VARCHAR2(40) NOT NULL
  , PRIMARY KEY (TERM_SPEC_ID, CTGRY_ID)
  , CONSTRAINT KRMS_TERM_SPEC_CTGRY_FK1 FOREIGN KEY (TERM_SPEC_ID) REFERENCES KRMS_TERM_SPEC_T (TERM_SPEC_ID)
  , CONSTRAINT KRMS_TERM_SPEC_CTGRY_FK2 FOREIGN KEY (CTGRY_ID) REFERENCES KRMS_CTGRY_T (CTGRY_ID)
)
/

CREATE TABLE KRMS_FUNC_CTGRY_T
(
  FUNC_ID VARCHAR2(40) NOT NULL
  , CTGRY_ID VARCHAR2(40) NOT NULL
  , PRIMARY KEY (FUNC_ID, CTGRY_ID)
  , CONSTRAINT KRMS_FUNC_CTGRY_FK1 FOREIGN KEY (FUNC_ID) REFERENCES KRMS_FUNC_T (FUNC_ID)
  , CONSTRAINT KRMS_FUNC_CTGRY_FK2 FOREIGN KEY (CTGRY_ID) REFERENCES KRMS_CTGRY_T (CTGRY_ID)
)
/