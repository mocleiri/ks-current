--This removes all the cluset joins to non-existing clus
delete from KSLU_CLU_SET_JN_CLU csj
where csj.id in (Select csj.id
from KSLU_CLU_SET_JN_CLU csj,KSLU_CLU clu
where csj.clu_ver_ind_id = clu.ver_ind_id(+)
and clu.id is null)
/

--Add 2 clus to each of the 10 ad-hoc clusets(referenced by term parameters) that are empty
insert into KSLU_CLU_SET_JN_CLU (CLU_SET_ID, CLU_VER_IND_ID, ID, VER_NBR, OBJ_ID) values ('416bb4d4-3b60-4d52-a1f9-b1f04dcdaa5d', '2ab6f617-d0b8-4fcd-88f1-fef5b4803da3', 'DEDE25CD-F054-33C5-E040-007F01011157', 0, 'DEDE25CD-F154-33C5-E040-007F01011157')
/
insert into KSLU_CLU_SET_JN_CLU (CLU_SET_ID, CLU_VER_IND_ID, ID, VER_NBR, OBJ_ID) values ('416bb4d4-3b60-4d52-a1f9-b1f04dcdaa5d', '64d318d2-15f3-4f00-aa38-8c40dee543b0', 'DEDE25CD-F055-33C5-E040-007F01011157', 0, 'DEDE25CD-F155-33C5-E040-007F01011157')
/
insert into KSLU_CLU_SET_JN_CLU (CLU_SET_ID, CLU_VER_IND_ID, ID, VER_NBR, OBJ_ID) values ('192b03ad-075d-4a1d-84dd-774dec6e314e', '2ab6f617-d0b8-4fcd-88f1-fef5b4803da3', 'DEDE25CD-F059-33C5-E040-007F01011157', 0, 'DEDE25CD-F159-33C5-E040-007F01011157')
/
insert into KSLU_CLU_SET_JN_CLU (CLU_SET_ID, CLU_VER_IND_ID, ID, VER_NBR, OBJ_ID) values ('192b03ad-075d-4a1d-84dd-774dec6e314e', '64d318d2-15f3-4f00-aa38-8c40dee543b0', 'DEDE25CD-F05a-33C5-E040-007F01011157', 0, 'DEDE25CD-F15a-33C5-E040-007F01011157')
/
insert into KSLU_CLU_SET_JN_CLU (CLU_SET_ID, CLU_VER_IND_ID, ID, VER_NBR, OBJ_ID) values ('3e7aa9f0-6fed-42dc-81bb-435a69f7b2ce', '2ab6f617-d0b8-4fcd-88f1-fef5b4803da3', 'DEDE25CD-F05e-33C5-E040-007F01011157', 0, 'DEDE25CD-F15e-33C5-E040-007F01011157')
/
insert into KSLU_CLU_SET_JN_CLU (CLU_SET_ID, CLU_VER_IND_ID, ID, VER_NBR, OBJ_ID) values ('3e7aa9f0-6fed-42dc-81bb-435a69f7b2ce', '64d318d2-15f3-4f00-aa38-8c40dee543b0', 'DEDE25CD-F05f-33C5-E040-007F01011157', 0, 'DEDE25CD-F15f-33C5-E040-007F01011157')
/
insert into KSLU_CLU_SET_JN_CLU (CLU_SET_ID, CLU_VER_IND_ID, ID, VER_NBR, OBJ_ID) values ('a41ae744-0eb5-448e-b7e0-415755192ed4', '2ab6f617-d0b8-4fcd-88f1-fef5b4803da3', 'DEDE25CD-F063-33C5-E040-007F01011157', 0, 'DEDE25CD-F163-33C5-E040-007F01011157')
/
insert into KSLU_CLU_SET_JN_CLU (CLU_SET_ID, CLU_VER_IND_ID, ID, VER_NBR, OBJ_ID) values ('a41ae744-0eb5-448e-b7e0-415755192ed4', '64d318d2-15f3-4f00-aa38-8c40dee543b0', 'DEDE25CD-F064-33C5-E040-007F01011157', 0, 'DEDE25CD-F164-33C5-E040-007F01011157')
/
insert into KSLU_CLU_SET_JN_CLU (CLU_SET_ID, CLU_VER_IND_ID, ID, VER_NBR, OBJ_ID) values ('f5ba2fc1-4668-4a81-814b-9241057193ff', '2ab6f617-d0b8-4fcd-88f1-fef5b4803da3', 'DEDE25CD-F068-33C5-E040-007F01011157', 0, 'DEDE25CD-F168-33C5-E040-007F01011157')
/
insert into KSLU_CLU_SET_JN_CLU (CLU_SET_ID, CLU_VER_IND_ID, ID, VER_NBR, OBJ_ID) values ('f5ba2fc1-4668-4a81-814b-9241057193ff', '64d318d2-15f3-4f00-aa38-8c40dee543b0', 'DEDE25CD-F069-33C5-E040-007F01011157', 0, 'DEDE25CD-F169-33C5-E040-007F01011157')
/
insert into KSLU_CLU_SET_JN_CLU (CLU_SET_ID, CLU_VER_IND_ID, ID, VER_NBR, OBJ_ID) values ('fa4fed31-cf28-4cd0-b125-3445e3302b94', '2ab6f617-d0b8-4fcd-88f1-fef5b4803da3', 'DEDE25CD-F06d-33C5-E040-007F01011157', 0, 'DEDE25CD-F16d-33C5-E040-007F01011157')
/
insert into KSLU_CLU_SET_JN_CLU (CLU_SET_ID, CLU_VER_IND_ID, ID, VER_NBR, OBJ_ID) values ('fa4fed31-cf28-4cd0-b125-3445e3302b94', '64d318d2-15f3-4f00-aa38-8c40dee543b0', 'DEDE25CD-F06e-33C5-E040-007F01011157', 0, 'DEDE25CD-F16e-33C5-E040-007F01011157')
/
insert into KSLU_CLU_SET_JN_CLU (CLU_SET_ID, CLU_VER_IND_ID, ID, VER_NBR, OBJ_ID) values ('47649970-a003-4c45-9693-6679d93581ca', '2ab6f617-d0b8-4fcd-88f1-fef5b4803da3', 'DEDE25CD-F072-33C5-E040-007F01011157', 0, 'DEDE25CD-F172-33C5-E040-007F01011157')
/
insert into KSLU_CLU_SET_JN_CLU (CLU_SET_ID, CLU_VER_IND_ID, ID, VER_NBR, OBJ_ID) values ('47649970-a003-4c45-9693-6679d93581ca', '64d318d2-15f3-4f00-aa38-8c40dee543b0', 'DEDE25CD-F073-33C5-E040-007F01011157', 0, 'DEDE25CD-F173-33C5-E040-007F01011157')
/
insert into KSLU_CLU_SET_JN_CLU (CLU_SET_ID, CLU_VER_IND_ID, ID, VER_NBR, OBJ_ID) values ('cbc17b3d-1a75-414a-8f3b-580a02360a2f', '2ab6f617-d0b8-4fcd-88f1-fef5b4803da3', 'DEDE25CD-F077-33C5-E040-007F01011157', 0, 'DEDE25CD-F177-33C5-E040-007F01011157')
/
insert into KSLU_CLU_SET_JN_CLU (CLU_SET_ID, CLU_VER_IND_ID, ID, VER_NBR, OBJ_ID) values ('cbc17b3d-1a75-414a-8f3b-580a02360a2f', '64d318d2-15f3-4f00-aa38-8c40dee543b0', 'DEDE25CD-F078-33C5-E040-007F01011157', 0, 'DEDE25CD-F178-33C5-E040-007F01011157')
/
insert into KSLU_CLU_SET_JN_CLU (CLU_SET_ID, CLU_VER_IND_ID, ID, VER_NBR, OBJ_ID) values ('6dc1a13e-d4b0-48a7-a166-0ee60e411295', '2ab6f617-d0b8-4fcd-88f1-fef5b4803da3', 'DEDE25CD-F07c-33C5-E040-007F01011157', 0, 'DEDE25CD-F17c-33C5-E040-007F01011157')
/
insert into KSLU_CLU_SET_JN_CLU (CLU_SET_ID, CLU_VER_IND_ID, ID, VER_NBR, OBJ_ID) values ('6dc1a13e-d4b0-48a7-a166-0ee60e411295', '64d318d2-15f3-4f00-aa38-8c40dee543b0', 'DEDE25CD-F07d-33C5-E040-007F01011157', 0, 'DEDE25CD-F17d-33C5-E040-007F01011157')
/
insert into KSLU_CLU_SET_JN_CLU (CLU_SET_ID, CLU_VER_IND_ID, ID, VER_NBR, OBJ_ID) values ('01c9b77e-9aaa-4c3e-a6b5-a20db8ccc207', '2ab6f617-d0b8-4fcd-88f1-fef5b4803da3', 'DEDE25CD-F081-33C5-E040-007F01011157', 0, 'DEDE25CD-F181-33C5-E040-007F01011157')
/
insert into KSLU_CLU_SET_JN_CLU (CLU_SET_ID, CLU_VER_IND_ID, ID, VER_NBR, OBJ_ID) values ('01c9b77e-9aaa-4c3e-a6b5-a20db8ccc207', '64d318d2-15f3-4f00-aa38-8c40dee543b0', 'DEDE25CD-F082-33C5-E040-007F01011157', 0, 'DEDE25CD-F182-33C5-E040-007F01011157')
/