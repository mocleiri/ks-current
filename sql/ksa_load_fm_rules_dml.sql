


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
 on each session signup fire "FM Signup 2" rule set
 on session fire "FM Session 2" rule set
 on session fire "FM Session 3" rule set
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

--Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (9099, 'FM Rule TEST', 3, 8, null,
--'(Context is initialized)',
--'set session key "chargeable.credits" to number of units where signup operation is "ADD,ADD_WITHOUT_PENALTY,TRANSFER_IN" minus "DROP,DROP_WITHOUT_PENALTY,TRANSFER_OUT"
-- set session key "taken.credits" to number of units where signup is taken
--mark preceding offerings as not taken
--mark signup as not taken
--mark signup as not complete
--remove rates ".*fee.*" from preceding offerings
--charge incidental rate "late.registration", "1" using id "late.registration"
--charge rates "late.registration", "1" in amount of 34.56% with types "", catalogs "", signup operations ""
--charge rates "late.registration", "" with types "", catalogs "", signup operations ""
--discount rate "late.registration", "1" by $10.67
--discount rate "late.registration", "1" by 99.91%
--replace signup rates ".*,late.fee,2", "" with "late.registration", "1"
--remove signup rates ".*"
--add signup rate "late.registration", "1"
--replace signup rates ".*,late.fee,2", "" with "late.registration", "1"
--remove signup rates ".*"
--add signup rate "late.registration", "1"
--mark signup rates ".*", "1" as not complete
--mark signup rates "late.registration", "1" as complete with types "", catalogs "", signup operations ""
--mark signup rates ".*", "1" as complete
--mark signup rates "late.registration", "" as not complete
--mark signup rates ".*", "" as not complete
--add rate "late.registration", "1" to signups with rates ".*", ".*", types "", catalogs "", signup operations ""
--mark session as review required
--mark session as review complete
--')!

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
'(number of taken units gte 12 with rates "", types "", signup operations "" and student is undergraduate)',
'set session key "study.load" to "ft"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6003, 'FM Session 1_3', 3, 10, null,
'(number of taken units gte 9 with rates "", types "", signup operations "" and student is graduate)',
'set session key "study.load" to "ft"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6004, 'FM Session 1_4', 3, 10, null,
'(number of taken units gte 9 with rates "", types "", signup operations "" and session key "study.level" is "doctoral")',
'set session key "study.load" to "ft"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6005, 'FM Session 1_5', 3, 10, null,
'(number of taken units lt 12 with rates "", types "", signup operations "" and student is undergraduate)',
'set session key "study.load" to "pt"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6006, 'FM Session 1_6', 3, 10, null,
'(number of taken units lt 9 with rates "", types "", signup operations "" and student is graduate)',
'set session key "study.load" to "pt"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6007, 'FM Session 1_7', 3, 10, null,
'(number of taken units lt 9 with rates "", types "", signup operations "" and session key "study.level" is "doctoral")',
'set session key "study.load" to "pt"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6008, 'FM Session 1_8', 3, 9, null,
'(session key "major.code" is "engf")',
'replace signup rates "regular", "" with "enpm", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6009, 'FM Session 1_9', 3, 8, null,
'(student is full-time and student is resident and student is undergraduate and session key "campus" is "cp")',
'replace signup rates "regular", "" with "cp.undergraduate.resident.ft", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6010, 'FM Session 1_10', 3, 8, null,
'(student is full-time and student is resident and student is graduate and session key "campus" is "cp")',
'replace signup rates "regular", "" with "cp.graduate.resident.ft", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6011, 'FM Session 1_11', 3, 8, null,
'(student is full-time and student is nonresident and student is undergraduate and session key "campus" is "cp")',
'replace signup rates "regular", "" with "cp.undergraduate.nonresident.ft", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6012, 'FM Session 1_12', 3, 8, null,
'(student is full-time and student is nonresident and student is graduate and session key "campus" is "cp")',
'replace signup rates "regular", "" with "cp.graduate.nonresident.ft", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6013, 'FM Session 1_13', 3, 8, null,
'(student is part-time and student is resident and student is undergraduate and session key "campus" is "cp")',
'replace signup rates "regular", "" with "cp.undergraduate.resident.pt", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6014, 'FM Session 1_14', 3, 8, null,
'(student is part-time and student is resident and student is graduate and session key "campus" is "cp")',
'replace signup rates "regular", "" with "cp.graduate.resident.pt", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6015, 'FM Session 1_15', 3, 8, null,
'(student is part-time and student is nonresident and student is undergraduate and session key "campus" is "cp")',
'replace signup rates "regular", "" with "cp.undergraduate.nonresident.pt", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6016, 'FM Session 1_16', 3, 8, null,
'(student is part-time and student is nonresident and student is graduate and session key "campus" is "cp")',
'replace signup rates "regular", "" with "cp.graduate.nonresident.pt", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6017, 'FM Session 1_17', 3, 8, null,
'(student is resident and student is undergraduate and session key "campus" is "sg")',
'replace signup rates "regular", "" with "sg.undergrad.resident", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6018, 'FM Session 1_18', 3, 8, null,
'(student is nonresident and student is undergraduate and session key "campus" is "sg")',
'replace signup rates "regular", "" with "sg.undergrad.nonresident", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6019, 'FM Session 1_19', 3, 8, null,
'(student is resident and student is graduate and session key "campus" is "sg")',
'replace signup rates "regular", "" with "sg.graduate.resident", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6020, 'FM Session 1_20', 3, 8, null,
'(student is nonresident and student is graduate and session key "campus" is "sg")',
'replace signup rates "regular", "" with "sg.graduate.nonresident", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6021, 'FM Session 1_21', 3, 8, null,
'(session key "cohort.code" is "EM11" and session key "major.code" is ".*MBA")',
'replace signup rates "mba.cohort.flag", "" with "mba.cohort.em11", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6022, 'FM Session 1_22', 3, 8, null,
'(session key "cohort.code" is "EM12" and session key "major.code" is ".*MBA")',
'replace signup rates "mba.cohort.flag", "" with "mba.cohort.em12", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6023, 'FM Session 1_23', 3, 10, null,
'(session key "major.code" is "z077" and signup has rates "cybersecurity.leadership")',
'remove signup rates "regular"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6024, 'FM Session 1_24', 3, 8, null,
'(student is full-time and student is resident)',
'replace signup rates "cp.mandatory.fee.flag", "" with "cp.resident.ft", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6025, 'FM Session 1_25', 3, 8, null,
'(student is full-time and student is nonresident)',
'replace signup rates "cp.mandatory.fee.flag", "" with "cp.nonresident.ft", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6026, 'FM Session 1_26', 3, 8, null,
'(student is part-time and student is resident)',
'replace signup rates "cp.mandatory.fee.flag", "" with "cp.resident.pt", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6027, 'FM Session 1_27', 3, 8, null,
'(student is part-time and student is nonresident)',
'replace signup rates "cp.mandatory.fee.flag", "" with "cp.nonresident.pt", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6028, 'FM Session 1_28', 3, 8, null,
'(signup has rates "tech.fee.flag" and student is full-time)',
'replace signup rates "tech.fee.flag", "" with "tech.fee.ft", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6029, 'FM Session 1_29', 3, 8, null,
'(signup has rates "tech.fee.flag" and student is part-time)',
'replace signup rates "tech.fee.flag", "" with "tech.fee.pt", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6030, 'FM Session 1_30', 3, 8, null,
'(student is graduate and signup has rates "art.ug")',
'replace signup rates "art.ug", "" with "art.grad", "default"')!


-- FM Signup 2 rule set --

Insert into KSSA_RULE_SET (ID, NAME, RULE_TYPE_ID_FK, HEADER) values (103, 'FM Signup 2', 3,
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

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7001, 'FM Signup 2_1', 3, 10, null,
'(signup is taken and signup does not have rates "academic.service.fee")',
'set session key "academic.service.remove" to "y"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7002, 'FM Signup 2_2', 3, 10, null,
'(session key "campus" is "sg" and signup has rates "sg.fee" and student is full-time and student is undergraduate)',
'add signup rate "sg.facilities.ft", "default"
 add signup rate "sg.administrative.fee", "default"
 ')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7003, 'FM Signup 2_3', 3, 10, null,
'(session key "campus" is "sg" and signup has rates "sg.fee" and student is part-time and student is undergraduate)',
'add signup rate "sg.facilities.pt", "default"
 add signup rate "sg.administrative.fee", "default"
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7004, 'FM Signup 2_4', 3, 10, null,
'(session key "campus" is "sg" and signup has rates "sg.fee" and student is full-time and student is graduate)',
'add signup rate "sg.facilities.ft", "default"
 add signup rate "sg.fee.graduate.ft", "default"
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7005, 'FM Signup 2_5', 3, 10, null,
'(session key "campus" is "sg" and signup has rates "sg.fee" and student is part-time and student is graduate)',
'add signup rate "sg.facilities.pt", "default"
 add signup rate "sg.fee.graduate.pt", "default"
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7006, 'FM Signup 2_6', 3, 10, null,
'(session key "major.code" is "0909F" and signup has rates "cp.undergrad.resident.ft")',
'add signup rate "cp.undergrad.resident.ft.frostburg.discount", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7007, 'FM Signup 2_7', 3, 10, null,
'(session key "major.code" is "0909F" and signup has rates "cp.undergrad.resident.pt")',
'add signup rate "cp.undergrad.resident.pt.frostburg.discount", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7008, 'FM Signup 2_8', 3, 10, null,
'(session key "major.code" is "0909F" and signup has rates "cp.undergrad.nonresident.ft")',
'add signup rate "cp.undergrad.nonresident.ft.frostburg.discount", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7009, 'FM Signup 2_9', 3, 10, null,
'(session key "major.code" is "0909F" and signup has rates "cp.undergrad.nonresident.pt")',
'add signup rate "cp.undergrad.nonresident.pt.frostburg.discount", "default"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7010, 'FM Signup 2_10', 3, 10, null,
'(signup has rates "tech.fee.[..]" and signup has rates "cp.[.*graduate].[..]")',
'remove signup rates "tech.fee.[..]"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7011, 'FM Signup 2_11', 3, 9, null,
'(session key "academic.service.remove" is "y")',
'remove signup rates "academic.service.fee"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7012, 'FM Signup 2_12', 3, 5, null,
'(signup operation is "DROP" and signup key "late.drop.penalty" is "yes" and signup has rates "cp.undergrad.resident.ft,cp.graduate.resident.ft,cp.undergrad.nonresident.ft")',
'mark signup as complete')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7013, 'FM Signup 2_13', 3, 5, null,
'(signup operation is "DROP" and signup key "late.drop.penalty" is "yes" and signup has rates "mba.cohort.em11" and manifest has rates "mba.cohort.em11")',
'mark signup as complete')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7014, 'FM Signup 2_14', 3, 5, null,
'(signup operation is "DROP" and signup key "late.drop.penalty" is "yes" and signup has rates "mba.cohort.em12" and manifest has rates "mba.cohort.em12")',
'mark signup as complete')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7015, 'FM Signup 2_15', 3, 5, null,
'(signup operation is "DROP" and signup key "late.drop.penalty" is "yes" and signup has rates "mba.cohort.em13" and manifest has rates "mba.cohort.em13")',
'mark signup as complete')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7016, 'FM Signup 2_16', 3, 5, null,
'(signup operation is "DROP" and signup key "late.drop.penalty" is "yes" and signup has rates "mba.cohort.ac01" and manifest has rates "mba.cohort.ac01")',
'mark signup as complete')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7017, 'FM Signup 2_17', 3, 4, null,
'(signup operation is "DROP" and signup key "late.drop.penalty" is "yes" and signup has rates "mba.cohort.ac01")',
'charge rates "mba.cohort.ac01", "" in amount of 20% with types "", catalogs "", signup operations "DROP"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7018, 'FM Signup 2_18', 3, 4, null,
'(signup operation is "DROP" and signup key "late.drop.penalty" is "yes" and signup has rates "mba.cohort.em11")',
'charge rates "mba.cohort.em11", "" in amount of 20% with types "", catalogs "", signup operations "DROP"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7019, 'FM Signup 2_19', 3, 4, null,
'(signup operation is "DROP" and signup key "late.drop.penalty" is "yes" and signup has rates "mba.cohort.em12")',
'charge rates "mba.cohort.em12", "" in amount of 20% with types "", catalogs "", signup operations "DROP"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7020, 'FM Signup 2_20', 3, 4, null,
'(signup operation is "DROP" and signup key "late.drop.penalty" is "yes" and signup has rates "mba.cohort.em13")',
'charge rates "mba.cohort.em13", "" in amount of 20% with types "", catalogs "", signup operations "DROP"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7021, 'FM Signup 2_21', 3, 5, null,
'(signup operation is "DROP" and signup key "late.drop.penalty" is "yes" and signup has rates "cp.undergrad.resident.pt")',
'charge rates "cp.undergrad.resident.pt", "" in amount of 20% with types "", catalogs "", signup operations "DROP"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7022, 'FM Signup 2_22', 3, 5, null,
'(signup operation is "DROP" and signup key "late.drop.penalty" is "yes" and signup has rates "cp.graduate.resident.pt")',
'charge rates "cp.graduate.resident.pt", "" in amount of 20% with types "", catalogs "", signup operations "DROP"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7023, 'FM Signup 2_23', 3, 5, null,
'(signup operation is "DROP" and signup key "late.drop.penalty" is "yes" and signup has rates "cp.undergrad.nonresident.pt")',
'charge rates "cp.undergrad.nonresident.pt", "" in amount of 20% with types "", catalogs "", signup operations "DROP"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7024, 'FM Signup 2_24', 3, 5, null,
'(signup operation is "DROP" and signup key "late.drop.penalty" is "yes" and signup has rates "cp.graduate.nonresident.pt")',
'charge rates "cp.graduate.nonresident.pt", "" in amount of 20% with types "", catalogs "", signup operations "DROP"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7025, 'FM Signup 2_25', 3, 5, null,
'(signup operation is "DROP" and signup key "late.drop.penalty" is "yes" and signup has rates "sg.grad.resident")',
'charge rates "sg.grad.resident", "" in amount of 20% with types "", catalogs "", signup operations "DROP"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7026, 'FM Signup 2_26', 3, 5, null,
'(signup operation is "DROP" and signup key "late.drop.penalty" is "yes" and signup has rates "sg.grad.nonresident")',
'charge rates "sg.grad.nonresident", "" in amount of 20% with types "", catalogs "", signup operations "DROP"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7027, 'FM Signup 2_27', 3, 5, null,
'(signup operation is "DROP" and signup key "late.drop.penalty" is "yes" and signup has rates "baltimore.mba")',
'charge rates "baltimore.mba", "" in amount of 20% with types "", catalogs "", signup operations "DROP"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7028, 'FM Signup 2_28', 3, 5, null,
'(signup operation is "DROP" and signup key "late.drop.penalty" is "yes" and signup has rates "public.policy.resident")',
'charge rates "public.policy.resident", "" in amount of 20% with types "", catalogs "", signup operations "DROP"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7029, 'FM Signup 2_29', 3, 5, null,
'(signup operation is "DROP" and signup key "late.drop.penalty" is "yes" and signup has rates "public.policy.resident.differential")',
'charge rates "public.policy.resident.differential", "" in amount of 20% with types "", catalogs "", signup operations "DROP"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7030, 'FM Signup 2_30', 3, 5, null,
'(signup operation is "DROP" and signup key "late.drop.penalty" is "yes" and signup has rates "public.policy.nonresident")',
'charge rates "public.policy.nonresident", "" in amount of 20% with types "", catalogs "", signup operations "DROP"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7031, 'FM Signup 2_31', 3, 5, null,
'(signup operation is "DROP" and signup key "late.drop.penalty" is "yes" and signup has rates "public.policy.nonresident.differential")',
'charge rates "public.policy.nonresident.differential", "" in amount of 20% with types "", catalogs "", signup operations "DROP"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7032, 'FM Signup 2_32', 3, 5, null,
'(signup operation is "DROP" and signup key "late.drop.penalty" is "yes" and signup has rates "cybersecurity.leadership")',
'charge rates "cybersecurity.leadership", "" in amount of 20% with types "", catalogs "", signup operations "DROP"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7033, 'FM Signup 2_33', 3, 5, null,
'(signup operation is "DROP" and signup key "late.drop.penalty" is "yes" and signup has rates "enpm")',
'charge rates "enpm", "" in amount of 20% with types "", catalogs "", signup operations "DROP"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7034, 'FM Signup 2_34', 3, 5, null,
'(signup operation is "DROP" and signup key "late.drop.penalty" is "yes" and signup has rates "dc.weekend")',
'charge rates "dc.weekend", "" in amount of 20% with types "", catalogs "", signup operations "DROP"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7035, 'FM Signup 2_35', 3, 5, null,
'(signup operation is "DROP" and signup key "late.drop.penalty" is "yes" and signup has rates "dc.weeknight")',
'charge rates "dc.weeknight", "" in amount of 20% with types "", catalogs "", signup operations "DROP"')!


-- FM Session 2 rule set --

Insert into KSSA_RULE_SET (ID, NAME, RULE_TYPE_ID_FK, HEADER) values (104, 'FM Session 2', 3,
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

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (8001, 'FM Session 2_1', 3, 6, null,
'(Context is initialized)',
'charge rates "", "" with types "tuition.credits.fixed,tuition.flat", catalogs "", signup operations "ADD,ADD_WITHOUT_PENALTY,TRANSFER_IN"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (8002, 'FM Session 2_2', 3, 8, null,
'(Context is initialized)',
'charge rates "", "" with types ".*fee.*", catalogs "", signup operations "ADD,ADD_WITHOUT_PENALTY,TRANSFER_IN"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (8003, 'FM Session 2_3', 3, 10, null,
'(session key "80percent" is "yes")',
'charge rates "", "" in amount of 20% with types "tuition.credits.fixed", catalogs "", signup operations "WITHDRAW"
 charge rates "mba.cohort..*", "" in amount of 20% with types "", catalogs "", signup operations "WITHDRAW"
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (8004, 'FM Session 2_4', 3, 10, null,
'(session key "60percent" is "yes")',
'charge rates "", "" in amount of 40% with types "tuition.credits.fixed", catalogs "", signup operations "WITHDRAW"
 charge rates "mba.cohort..*", "" in amount of 40% with types "", catalogs "", signup operations "WITHDRAW"
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (8005, 'FM Session 2_5', 3, 10, null,
'(session key "40percent" is "yes")',
'charge rates "", "" in amount of 60% with types "tuition.credits.fixed", catalogs "", signup operations "WITHDRAW"
 charge rates "mba.cohort..*", "" in amount of 60% with types "", catalogs "", signup operations "WITHDRAW"
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (8006, 'FM Session 2_6', 3, 10, null,
'(session key "20percent" is "yes")',
'charge rates "", "" in amount of 80% with types "tuition.credits.fixed", catalogs "", signup operations "WITHDRAW"
 charge rates "mba.cohort..*", "" in amount of 80% with types "", catalogs "", signup operations "WITHDRAW"
')!

-- FM Session 3 Java-based rule set --

Insert into KSSA_RULE_SET (ID, NAME, RULE_TYPE_ID_FK, HEADER) values (105, 'FM Session 3', 2,
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

global FeeManagementSession fmSession;

')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (9001, 'FM Session 3_1', 2, 10, null,
'context : BrmContext(fmService.compareSessionKeyPair("late.drop.penalty","yes","==",context))',
'
        String[] rateCodes = {
                          "cp.undergrad.resident.pt",
                          "cp.undergrad.nonresident.pt",
                          "cp.graduate.resident.pt",
                          "cp.graduate.nonresident.pt"
                 };

        final UnitNumber threshold = new UnitNumber(12);

        final UnitNumber five = new UnitNumber(5);

        for (int i = 0; i < rateCodes.length; i++) {

            UnitNumber numberOfDroppedUnits = context.getFmService().countDroppedUnits(rateCodes[i], context);
            UnitNumber numberOfTakenUnits = context.getFmService().countTakenUnits(rateCodes[i], context);

            UnitNumber numberOfUnits = numberOfTakenUnits.add(numberOfDroppedUnits);

            if (numberOfUnits.compareTo(threshold) > 0) {
                numberOfDroppedUnits = numberOfDroppedUnits.subtract(numberOfUnits.subtract(threshold));
            }

            UnitNumber numberOfUnitsToCharge = numberOfDroppedUnits.divide(five);

            if (numberOfUnitsToCharge.compareTo(UnitNumber.ZERO) > 0) {
               context.getFmService().chargeIncidentalRate(rateCodes[i], "default", rateCodes[i] + ".default", numberOfUnitsToCharge, null, context);
            }
        }
')!

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
--Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (101, 9099)!

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
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (102, 6025)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (102, 6026)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (102, 6027)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (102, 6028)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (102, 6029)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (102, 6030)!

-- FM Signup 2 rule set --
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7001)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7002)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7003)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7004)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7005)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7006)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7007)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7008)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7009)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7010)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7011)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7012)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7013)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7014)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7015)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7016)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7017)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7018)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7019)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7020)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7021)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7022)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7023)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7024)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7025)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7026)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7027)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7028)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7029)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7030)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7031)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7032)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7033)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7034)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (103, 7035)!

-- FM Session 2 rule set --
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (104, 8001)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (104, 8002)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (104, 8003)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (104, 8004)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (104, 8005)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (104, 8006)!

-- FM Session 3 rule set --
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (105, 9001)!


-----------------------------------------------------------------------------------------------------------------------