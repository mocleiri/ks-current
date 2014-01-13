


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

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5001, 'FM Rule 1', 3, 10, null,
'(signup date is on or before atp milestone "kuali.atp.milestone.firstDayOfClass" and signup operation is "ADD")',
'set session key "early.registration" to "true"
 mark signup as taken
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5002, 'FM Rule 2', 3, 10, null,
'(signup operation is "ADD_WITHOUT_PENALTY,TRANSFER_IN")',
'set session key "early.registration" to "true"
 mark signup as taken
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5003, 'FM Rule 3', 3, 10, null,
'(signup operation is "DROP_WITHOUT_PENALTY,TRANSFER_OUT")',
'mark preceding offerings as not taken
 mark signup as not taken
 mark preceding offerings as complete
 mark signup as complete
 remove signup rates ".*fee.*"
 remove rates ".*fee.*" from preceding offerings
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5004, 'FM Rule 4', 3, 10, null,
'(signup operation is "WITHDRAW" and signup date is after atp milestone "kuali.atp.milestone.withdraw80")',
'set session key "withdrawn" to "yes"
 set session key "80percent" to "yes"
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5005, 'FM Rule 5', 3, 10, null,
'(signup operation is "WITHDRAW" and signup date is after atp milestone "kuali.atp.milestone.withdraw60")',
'set session key "withdrawn" to "yes"
 set session key "60percent" to "yes"
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5006, 'FM Rule 6', 3, 10, null,
'(signup operation is "WITHDRAW" and signup date is after atp milestone "kuali.atp.milestone.withdraw40")',
'set session key "withdrawn" to "yes"
 set session key "40percent" to "yes"
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5007, 'FM Rule 7', 3, 10, null,
'(signup operation is "WITHDRAW" and signup date is after atp milestone "kuali.atp.milestone.withdraw20")',
'set session key "withdrawn" to "yes"
 set session key "20percent" to "yes"
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5008, 'FM Rule 8', 3, 10, null,
'(signup operation is "WITHDRAW" and signup date is after atp milestone "kuali.atp.milestone.withdraw0")',
'set session key "withdrawn" to "yes"
 set session key "0percent" to "yes"
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5009, 'FM Rule 9', 3, 10, null,
'(signup operation is "DROP" and signup date is before atp milestone "kuali.atp.milestone.firstDayOfClass")',
'mark preceding offerings as not taken
 mark signup as not taken
 mark preceding offerings as complete
 mark signup as complete
 remove signup rates ".*fee.*"
 remove rates ".*fee.*" from preceding offerings
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5010, 'FM Rule 10', 3, 10, null,
'(signup operation is "DROP" and signup date is on or after atp milestone "kuali.atp.milestone.firstDayOfClass" and signup date is on or before atp milestone "kuali.atp.milestone.lastDayForPenaltyDrop")',
'mark preceding offerings as not taken
 mark signup as not taken
 set signup key "late.penalty" to "yes"
 set session key "late.penalty" to "yes"
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5011, 'FM Rule 11', 3, 10, null,
'(signup operation is "DROP" and signup date is after atp milestone "kuali.atp.milestone.lastDayForPenaltyDrop")',
'mark signup as complete
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5012, 'FM Rule 12', 3, 10, null,
'(signup operation is "WITHDRAW")',
'set session key "withdrawn" to "yes"
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5013, 'FM Rule 13', 3, 10, null,
'(signup has rates "mba.cohort.flag, regular" and session key "cohort.code" is "EM.." and session key "major.code" is ".*MBA")',
'remove signup rates "regular"
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5014, 'FM Rule 14', 3, 10, null,
'(signup has rates "mba.cohort.flag, regular" and session key "cohort.code" is not "EM..")',
'remove signup rates "mba.cohort.flag"
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5015, 'FM Rule 15', 3, 10, null,
'(session key "major.code" is not ".*MBA")',
'remove signup rates "mba.cohort.flag"
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5016, 'FM Rule 16', 3, 10, null,
'(session key "major.code" is not "z077")',
'remove signup rates "cybersecurity.leadership"
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5099, 'FM Rule TEST', 3, 8, null,
'(Context is initialized)',
'set session key "chargeable.credits" to number of units where signup operation is "ADD,ADD_WITHOUT_PENALTY,TRANSFER_IN" minus "DROP,DROP_WITHOUT_PENALTY,TRANSFER_OUT"
 set session key "taken.credits" to number of units where signup is taken
 mark preceding offerings as not taken
 mark signup as not taken
 mark signup as not complete
 remove rates ".*fee.*" from preceding offerings
 charge incidental rate "late.registration", "1" using id "late.registration"
 discount rate "late.registration", "1" by $10.67
 discount rate "late.registration", "1" by 99.91%
 replace signup rates ".*,late.fee,2", "" with "late.registration", "1"
 remove signup rates ".*"
 add signup rate "late.registration", "1"
 replace signup rates ".*,late.fee,2", "" with "late.registration", "1"
 remove signup rates ".*"
 add signup rate "late.registration", "1"
')!

-- FM Signup 2 rule set --

-- FM rule associations
-- Main rule set
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (100, 5000)!

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
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (101, 5099)!


-----------------------------------------------------------------------------------------------------------------------