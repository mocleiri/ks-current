


-----------------------------------------------------------------------------------------------------------------------
--- Inserting Fee Managements rules ---
Insert into KSSA_RULE_SET (ID, NAME, RULE_TYPE_ID_FK, HEADER) values (100, 'Fee Management Main', 3,
'import java.util.*;
import java.math.*;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.model.fm.*;
import com.sigmasys.kuali.ksa.model.rule.*;
import com.sigmasys.kuali.ksa.service.brm.*;
import com.sigmasys.kuali.ksa.service.fm.*;
import com.sigmasys.kuali.ksa.model.security.*;
import com.sigmasys.kuali.ksa.util.*;
import org.apache.commons.lang.*;

expander ksa.dsl

global FeeManagementSession fmSession;

')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5000, 'FM Main Rule', 3, 20, null,
'(Context is initialized)',
'on each session signup fire "FM Signup 1" rule set
 on session fire "FM Session 1" rule set
')!

-- FM Signup 1 rule set --

Insert into KSSA_RULE_SET (ID, NAME, RULE_TYPE_ID_FK, HEADER) values (101, 'FM Signup 1', 3,
'import java.util.*;
import java.math.*;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.model.fm.*;
import com.sigmasys.kuali.ksa.model.rule.*;
import com.sigmasys.kuali.ksa.service.brm.*;
import com.sigmasys.kuali.ksa.service.fm.*;
import com.sigmasys.kuali.ksa.model.security.*;
import com.sigmasys.kuali.ksa.util.*;
import org.apache.commons.lang.*;

expander ksa.dsl

global FeeManagementSession fmSession;
global FeeManagementSignup fmSignup;

')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5001, 'FM Signup 1_1', 3, 10, null,
'(signup date is on or before atp milestone "kuali.atp.milestone.firstDayOfClass" and signup operation is "ADD")',
'set session key "early.registration" to "true"
 mark signup as taken
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5002, 'FM Signup 1_2', 3, 10, null,
'(signup operation is "ADD_WITHOUT_PENALTY,TRANSFER_IN")',
'set session key "early.registration" to "true"
 mark signup as taken
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5003, 'FM Signup 1_3', 3, 10, null,
'(signup operation is "DROP_WITHOUT_PENALTY,TRANSFER_OUT")',
'mark preceding offerings as not taken
 mark signup as not taken
 mark preceding offerings as complete
 mark signup as complete
 remove signup rates ".*fee.*"
 remove rates ".*fee.*" from preceding offerings
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5004, 'FM Signup 1_4', 3, 10, null,
'(signup operation is "WITHDRAW" and signup date is after atp milestone "kuali.atp.milestone.withdraw80")',
'set session key "withdrawn" to "yes"
 set session key "80percent" to "yes"
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5005, 'FM Signup 1_5', 3, 10, null,
'(signup operation is "WITHDRAW" and signup date is after atp milestone "kuali.atp.milestone.withdraw60")',
'set session key "withdrawn" to "yes"
 set session key "60percent" to "yes"
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5006, 'FM Signup 1_6', 3, 10, null,
'(signup operation is "WITHDRAW" and signup date is after atp milestone "kuali.atp.milestone.withdraw40")',
'set session key "withdrawn" to "yes"
 set session key "40percent" to "yes"
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5007, 'FM Signup 1_7', 3, 10, null,
'(signup operation is "WITHDRAW" and signup date is after atp milestone "kuali.atp.milestone.withdraw20")',
'set session key "withdrawn" to "yes"
 set session key "20percent" to "yes"
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5008, 'FM Signup 1_8', 3, 10, null,
'(signup operation is "WITHDRAW" and signup date is after atp milestone "kuali.atp.milestone.withdraw0")',
'set session key "withdrawn" to "yes"
 set session key "0percent" to "yes"
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5009, 'FM Signup 1_9', 3, 10, null,
'(signup operation is "DROP" and signup date is before atp milestone "kuali.atp.milestone.firstDayOfClass")',
'mark preceding offerings as not taken
 mark signup as not taken
 mark preceding offerings as complete
 mark signup as complete
 remove signup rates ".*fee.*"
 remove rates ".*fee.*" from preceding offerings
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5010, 'FM Signup 1_10', 3, 10, null,
'(signup operation is "DROP" and signup date is on or after atp milestone "kuali.atp.milestone.firstDayOfClass" and signup date is on or before atp milestone "kuali.atp.milestone.lastDayForPenaltyDrop")',
'mark preceding offerings as not taken
 mark signup as not taken
 set signup key "late.penalty" to "yes"
 set session key "late.penalty" to "yes"
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5011, 'FM Signup 1_11', 3, 10, null,
'(signup operation is "DROP" and signup date is after atp milestone "kuali.atp.milestone.lastDayForPenaltyDrop")',
'mark signup as complete')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5012, 'FM Signup 1_12', 3, 10, null,
'(signup operation is "WITHDRAW")',
'set session key "withdrawn" to "yes"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5013, 'FM Signup 1_13', 3, 10, null,
'(signup has rates "mba.cohort.flag, regular" and session key "cohort.code" is "EM.." and session key "major.code" is ".*MBA")',
'remove signup rates "regular"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5014, 'FM Signup 1_14', 3, 10, null,
'(signup has rates "mba.cohort.flag, regular" and session key "cohort.code" is not "EM..")',
'remove signup rates "mba.cohort.flag"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5015, 'FM Signup 1_15', 3, 10, null,
'(session key "major.code" is not ".*MBA")',
'remove signup rates "mba.cohort.flag"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5016, 'FM Signup 1_16', 3, 10, null,
'(session key "major.code" is not "z077")',
'remove signup rates "cybersecurity.leadership"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5017, 'FM Signup 1_17', 3, 10, null,
'(session key "major.code" is "mapo|mamg|mpps|bmpo|posi|lmpo" and signup key "residency" is "in.state")',
'remove signup rates "regular"
 add signup rate "public.policy.resident", "default"
 add signup rate "public.policy.resident.differential", "default"
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5018, 'FM Signup 1_18', 3, 10, null,
'(session key "major.code" is "mapo|mamg|mpps|bmpo|posi|lmpo" and signup key "residency" is "out.of.state")',
'remove signup rates "regular"
 add signup rate "public.policy.resident", "default"
 add signup rate "public.policy.resident.differential", "default"
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (9099, 'FM Rule TEST', 3, 8, null,
'(Context is initialized)',
'set session key "chargeable.credits" to number of units where signup operation is "ADD,ADD_WITHOUT_PENALTY,TRANSFER_IN" minus "DROP,DROP_WITHOUT_PENALTY,TRANSFER_OUT"
 set session key "taken.credits" to number of units where signup is taken
 mark preceding offerings as not taken
 mark signup as not taken
 mark signup as not complete
 remove rates ".*fee.*" from preceding offerings
 charge incidental rate "late.registration", "1" using id "late.registration"
 charge rates "late.registration", "1" in amount of 34.56% with types "", catalogs "", signup operations ""
 charge rates "late.registration", "" with types "", catalogs "", signup operations ""
 discount rate "late.registration", "1" by $10.67
 discount rate "late.registration", "1" by 99.91%
 replace signup rates ".*,late.fee,2", "" with "late.registration", "1"
 remove signup rates ".*"
 add signup rate "late.registration", "1"
 replace signup rates ".*,late.fee,2", "" with "late.registration", "1"
 remove signup rates ".*"
 add signup rate "late.registration", "1"
')!

-- FM Session 1 rule set --

Insert into KSSA_RULE_SET (ID, NAME, RULE_TYPE_ID_FK, HEADER) values (102, 'FM Session 1', 3,
'import java.util.*;
import java.math.*;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.model.fm.*;
import com.sigmasys.kuali.ksa.model.rule.*;
import com.sigmasys.kuali.ksa.service.brm.*;
import com.sigmasys.kuali.ksa.service.fm.*;
import com.sigmasys.kuali.ksa.model.security.*;
import com.sigmasys.kuali.ksa.util.*;
import org.apache.commons.lang.*;

expander ksa.dsl

global FeeManagementSession fmSession;

')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6001, 'FM Session 1_1', 3, 10, null,
'(signup operation is "ADD" and signup date is after atp milestone "kuali.atp.milestone.firstDayOfClass" and session key "early.registration" is not "true")',
'charge incidental rate "late.fee", "default" using id "late.fee"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6002, 'FM Session 1_2', 3, 10, null,
'(number of taken units gte 12 with rates "", types "", signup operations "" and session key "study.level" is "undergraduate")',
'set session key "study.load" to "ft"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6003, 'FM Session 1_3', 3, 10, null,
'(number of taken units gte 9 with rates "", types "", signup operations "" and session key "study.level" is "graduate")',
'set session key "study.load" to "ft"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6004, 'FM Session 1_4', 3, 10, null,
'(number of taken units gte 9 with rates "", types "", signup operations "" and session key "study.level" is "doctoral")',
'set session key "study.load" to "ft"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6005, 'FM Session 1_5', 3, 10, null,
'(number of taken units lt 12 with rates "", types "", signup operations "" and session key "study.level" is "undergraduate")',
'set session key "study.load" to "pt"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6006, 'FM Session 1_6', 3, 10, null,
'(number of taken units lt 9 with rates "", types "", signup operations "" and session key "study.level" is "graduate")',
'set session key "study.load" to "pt"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6007, 'FM Session 1_7', 3, 10, null,
'(number of taken units lt 9 with rates "", types "", signup operations "" and session key "study.level" is "doctoral")',
'set session key "study.load" to "pt"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6008, 'FM Session 1_8', 3, 9, null,
'(session key "major.code" is "engf")',
'replace signup rates "regular", "" with "enpm", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6009, 'FM Session 1_9', 3, 8, null,
'(session key "study.load" is "ft" and session key "residency" is "in.state" and session key "study.level" is "undergraduate" and session key "campus" is "cp")',
'replace signup rates "regular", "" with "cp.undergraduate.resident.ft", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6010, 'FM Session 1_10', 3, 8, null,
'(session key "study.load" is "ft" and session key "residency" is "in.state" and session key "study.level" is "graduate" and session key "campus" is "cp")',
'replace signup rates "regular", "" with "cp.graduate.resident.ft", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6011, 'FM Session 1_11', 3, 8, null,
'(session key "study.load" is "ft" and session key "residency" is "out.of.state" and session key "study.level" is "undergraduate" and session key "campus" is "cp")',
'replace signup rates "regular", "" with "cp.undergraduate.nonresident.ft", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6012, 'FM Session 1_12', 3, 8, null,
'(session key "study.load" is "ft" and session key "residency" is "out.of.state" and session key "study.level" is "graduate" and session key "campus" is "cp")',
'replace signup rates "regular", "" with "cp.graduate.nonresident.ft", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6013, 'FM Session 1_13', 3, 8, null,
'(session key "study.load" is "pt" and session key "residency" is "in.state" and session key "study.level" is "undergraduate" and session key "campus" is "cp")',
'replace signup rates "regular", "" with "cp.undergraduate.resident.pt", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6014, 'FM Session 1_14', 3, 8, null,
'(session key "study.load" is "pt" and session key "residency" is "in.state" and session key "study.level" is "graduate" and session key "campus" is "cp")',
'replace signup rates "regular", "" with "cp.graduate.resident.pt", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6015, 'FM Session 1_15', 3, 8, null,
'(session key "study.load" is "pt" and session key "residency" is "out.of.state" and session key "study.level" is "undergraduate" and session key "campus" is "cp")',
'replace signup rates "regular", "" with "cp.undergraduate.nonresident.pt", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6016, 'FM Session 1_16', 3, 8, null,
'(session key "study.load" is "pt" and session key "residency" is "out.of.state" and session key "study.level" is "graduate" and session key "campus" is "cp")',
'replace signup rates "regular", "" with "cp.graduate.nonresident.pt", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6017, 'FM Session 1_17', 3, 8, null,
'(session key "residency" is "in.state" and session key "study.level" is "undergraduate" and session key "campus" is "sg")',
'replace signup rates "regular", "" with "sg.undergrad.resident", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6018, 'FM Session 1_18', 3, 8, null,
'(session key "residency" is "out.of.state" and session key "study.level" is "undergraduate" and session key "campus" is "sg")',
'replace signup rates "regular", "" with "sg.undergrad.nonresident", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6019, 'FM Session 1_19', 3, 8, null,
'(session key "residency" is "in.state" and session key "study.level" is "graduate" and session key "campus" is "sg")',
'replace signup rates "regular", "" with "sg.graduate.resident", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6020, 'FM Session 1_20', 3, 8, null,
'(session key "residency" is "out.of.state" and session key "study.level" is "graduate" and session key "campus" is "sg")',
'replace signup rates "regular", "" with "sg.graduate.nonresident", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6021, 'FM Session 1_21', 3, 8, null,
'(session key "cohort.code" is "EM11" and session key "major.code" is ".*MBA")',
'replace signup rates "mba.cohort.flag", "" with "mba.cohort.em11", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6022, 'FM Session 1_22', 3, 8, null,
'(session key "cohort.code" is "EM12" and session key "major.code" is ".*MBA")',
'replace signup rates "mba.cohort.flag", "" with "mba.cohort.em12", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6023, 'FM Session 1_23', 3, 10, null,
'(session key "major.code" is "z077" and signup has rates "cybersecurity.leadership")',
'replace signup rates "mba.cohort.flag", "" with "mba.cohort.em12", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6024, 'FM Session 1_24', 3, 7, null,
'(session key "major.code" is "0909F" and signup has rates "cp.undergrad.resident.ft")',
'add signup rate "cp.undergrad.resident.ft.frostburg.discount", "default"')!


-- FM rule associations
-- Main rule set
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (100, 5000)!

-- FM Signup 1 rule set --
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (101, 5001)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (101, 5002)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (101, 5003)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (101, 5004)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (101, 5005)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (101, 5006)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (101, 5007)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (101, 5008)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (101, 5009)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (101, 5010)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (101, 5011)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (101, 5012)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (101, 5013)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (101, 5014)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (101, 5015)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (101, 5016)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (101, 5017)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (101, 5018)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (101, 9099)!

-- FM Session 1 rule set --
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (102, 6001)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (102, 6002)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (102, 6003)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (102, 6004)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (102, 6005)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (102, 6006)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (102, 6007)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (102, 6008)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (102, 6009)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (102, 6010)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (102, 6011)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (102, 6012)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (102, 6013)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (102, 6014)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (102, 6015)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (102, 6016)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (102, 6017)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (102, 6018)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (102, 6019)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (102, 6020)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (102, 6021)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (102, 6022)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (102, 6023)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (102, 6024)!


-----------------------------------------------------------------------------------------------------------------------