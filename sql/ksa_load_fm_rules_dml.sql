


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
'on each session signup fire "Fee Management 1" rule set
')!

Insert into KSSA_RULE_SET (ID, NAME, RULE_TYPE_ID_FK, HEADER) values (101, 'Fee Management 1', 3,
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
'(signup date is on or before atp milestone "kuali.atp.milestone.lateRegistration" and signup operation is "ADD")',
'charge incidental rate "late.registration", "1" using id "late.registration"
 mark signup as taken
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5002, 'FM Rule 2', 3, 10, null,
'(signup operation is "ADD_WITHOUT_PENALTY,TRANSFER_IN")',
'mark signup as taken
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5003, 'FM Rule 3', 3, 10, null,
'(signup operation is "DROP_WITHOUT_PENALTY,TRANSFER_OUT")',
'mark preceding offerings as complete
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5004, 'FM Rule 4', 3, 10, null,
'(signup date is after atp milestone "kuali.atp.milestone.lateRegistration" and signup date is before atp milestone "kuali.atp.milestone.lastDayForPenaltyAddDrop" and signup operation is "DROP")',
'mark preceding offerings as not taken
 mark signup as not taken
 on signup remove rates ".*fee.*"
 charge incidental rate "late.registration", "1" using id "late.registration"
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5005, 'FM Rule 5', 3, 10, null,
'(signup date is after atp milestone "kuali.atp.milestone.lastDayForPenaltyAddDrop" and signup operation is "DROP")',
'mark preceding offerings as not taken
 mark signup as not taken
 mark signup as complete
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5006, 'FM Rule 6', 3, 10, null,
'(signup operation is "WITHDRAW")',
'mark all signups as not taken
 set session key "withdrawn" to "true"
 on signup including preceding offerings remove rates ".*fee.*"
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5007, 'FM Rule 7', 3, 9, null,
'(Context is initialized)',
'set session key "chargeable.credits" to number of units where signup operation is "ADD,ADD_WITHOUT_PENALTY,TRANSFER_IN" minus "DROP,DROP_WITHOUT_PENALTY,TRANSFER_OUT"
 set session key "taken.credits" to number of units where signup is taken
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5008, 'FM Rule TEST', 3, 8, null,
'(Context is initialized)',
'set session key "chargeable.credits" to number of units where signup operation is "ADD,ADD_WITHOUT_PENALTY,TRANSFER_IN" minus "DROP,DROP_WITHOUT_PENALTY,TRANSFER_OUT"
 set session key "taken.credits" to number of units where signup is taken
 mark preceding offerings as not taken
 mark signup as not taken
 mark signup as not complete
 on signup including preceding offerings remove rates ".*fee.*"
 charge incidental rate "late.registration", "1" using id "late.registration"
 discount rate "late.registration", "1" by $10.67
 discount rate "late.registration", "1" by 99.91%
 on signup replace rates ".*,late.fee,2", "" with "late.registration", "1"
 on signup remove rates ".*"
 on signup add rate "late.registration", "1"
')!

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


-----------------------------------------------------------------------------------------------------------------------