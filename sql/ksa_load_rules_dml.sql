
-----------------------------------------------------------------------------------------------------------------------
--- Inserting Payment Application rules ---

insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values
(99, 'Payment Application Rule', 3, 0, null, '(Context is initialized)',
'
Initialize list of GL transactions as "glTransactions"
Initialize list of GL transactions as "glTransactionsForRemovedAllocations"

Get list of transactions from "01/01/2011" to "12/31/2013", store result in "transactions"

Remove allocations from "transactions", add result to "glTransactionsForRemovedAllocations"

Allocate reversals for "transactions"  , add result to "glTransactions"

Get payments from "transactions", store result in "allPayments"
Get charges from "transactions", store result in "allCharges"
Sort "allCharges" by effective date in ascending order

Get payments from "allPayments" for 2011 year, store result in "payments2011"
Get payments from "allPayments" for 2012 year, store result in "payments2012"
Get payments from "allPayments" for 2013 year, store result in "payments2013"

Get charges from "allCharges" for 2011 year, store result in "charges2011"
Sort "charges2011" by priority in descending order

Get charges from "allCharges" for 2012 year    , store result in "charges2012"
Sort "charges2012" by priority in descending order

Get charges from "allCharges" for 2013 year    , store result in "charges2013"
Sort "charges2013" by priority in descending order

Get payments with tag "FinAid" from "allPayments" for 2011 year, store result in "finaidPayments2011"
Sort "finaidPayments2011" by effective date in ascending order
Sort "finaidPayments2011" by priority in descending order
Get payments with tag "FinAid" from "allPayments" for 2012 year, store result in "finaidPayments2012"
Sort "finaidPayments2012" by effective date in ascending order
Sort "finaidPayments2012" by priority in descending order
Get payments with tag "FinAid" from "allPayments" for 2013 year, store result in "finaidPayments2013"
Sort "finaidPayments2013" by effective date in ascending order
Sort "finaidPayments2013" by priority in descending order

Apply payments for "finaidPayments2011, charges2011", add result          to "glTransactions"
Apply payments for "finaidPayments2012, charges2012", add result to "glTransactions"
Apply payments for "finaidPayments2013, charges2013", add result to "glTransactions"

Apply payments with maximum amount $200 for "finaidPayments2012, allCharges", add result to "glTransactions"
Apply payments with maximum amount $200 for "finaidPayments2011,allCharges", add result to "glTransactions"
Apply payments with maximum amount $200 for "finaidPayments2013,allCharges", add result to "glTransactions"

Remove "finaidPayments2011, finaidPayments2012, finaidPayments2013" from "transactions"

Calculate matrix scores for "transactions"
Sort "transactions" by matrix score in ascending order

Apply payments for "transactions", add result to "glTransactions"

Summarize GL transactions "glTransactions"

Set global variable "resultList" to "glTransactions"

')!

insert into KSSA_RULE_SET (ID, NAME, RULE_TYPE_ID_FK, HEADER) values (99, 'Payment Application', 3,
'
import java.util.*;
import java.math.*;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.model.rule.*;
import com.sigmasys.kuali.ksa.service.brm.*;
import com.sigmasys.kuali.ksa.util.*;

expander ksa.dsl

global List resultList;

')!

insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (99, 99)!


-----------------------------------------------------------------------------------------------------------------------
-- Inserting Fee Management rules --

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (1, 'Rule 1', 3, 0, null,
'(Major is "EMBA" and Section is "EM11" with status "")',
'Use code "1179" to charge $31280.36
 Use code "1121" to charge $386.36
 Set status to "C", key pair "MAND_FEES" to "Y" where section is "EM11"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (2, 'Rule 2', 3, 1, null,
'(Key pair "member-code" is "12,22,42")',
'Use code "1060" to charge $199.38')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (3, 'Rule 3', 3, 20, null,
'(LU code is "ENTS[0-9A-Z]{4}" with status "")',
'Use code "1180" to charge $950
 Set status to "C", key pair "MAND_FEES" to "Y" where code is "ENTS[0-9A-Z]{4}"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (4, 'Rule 4', 3, 0, null,
 '(Section is "BR[0-9A-Z]{2}" with status "" and Student is resident)',
 'Use "1186" to charge $525 per credit where section is "BR[0-9A-Z]{2}" with status ""
  Use code "1187" to charge $53
  Set status to "C", key pair "MAND_FEES" to "Y" where section is "BR[0-9A-Z]{2}"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (5, 'Rule 5', 3, 0, null,
 '(Section is "BR[0-9A-Z]{2}" with status "" and Student is not resident)',
 'Use "1186" to charge $1131.00 per credit where section is "BR[0-9A-Z]{2}" with status ""
  Use code "1187" to charge $53
  Set status to "C", key pair "MAND_FEES" to "Y" where section is "BR[0-9A-Z]{2}"')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (6, 'Rule 6', 3, 0, null,
 '(Key pair "member-code" is "10,11,12,13,14,15,16,17,18,19,20" and Student is resident and Number of credits > 9 with status "")',
 'Use code "1000" to charge $3483.00
  Set status to "C", key pair "MAND_FEES" to "Y" where status is ""')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (7, 'Rule 7', 3, 0, null,
 '(Key pair "member-code" is "10,11,12,13,14,15,16,17,18,19,20" and Student is not resident and Number of credits > 9 with status "")',
 'Use code "1020" to charge $12168.48
  Set status to "C", key pair "MAND_FEES" to "Y" where status is ""')!


Insert into KSSA_RULE_SET (ID, NAME, RULE_TYPE_ID_FK, HEADER) values (1, 'Fee Management', 3,
'import java.util.*;
import java.math.*;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.model.rule.*;
import com.sigmasys.kuali.ksa.service.brm.*;

expander ksa.dsl

global FeeBase feeBase;

')!

-----------------------------------------------------------------------------------------------------------------------
--- Inserting Account Blocking rules ---

Insert into KSSA_RULE_SET (ID, NAME, RULE_TYPE_ID_FK, HEADER) values (2, 'Account Blocking', 3,
'import java.util.*;
import java.math.*;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.model.rule.*;
import com.sigmasys.kuali.ksa.service.brm.*;
import com.sigmasys.kuali.ksa.model.security.*;
import com.sigmasys.kuali.ksa.util.*;
import org.apache.commons.lang.*;

expander ksa.dsl

global Set blockNames;

global List permissionNames;
global List transactionTypeIds;
global List atpIds;
global List holdIssueNames;

')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (8, 'Block 1', 3, 0, null,
 '(Account ID is "user1" and Permission is "CREATE_PAYMENT, CREATE_CHARGE" and Transaction type is "cash, finaid")', 'Apply block')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (9, 'Block 2', 3, 0, null,
 '(ATP is "20002001HOLIDAYCALENDAR" and Hold issue is "Unpaid Library Fine")', 'Apply block')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (10, 'Block 3', 3, 0, null,
 '(ATP is "20122" and Hold issue is "Disciplinary Suspension" and Permission is "REQUEST_REFUND")', 'Apply block')!


-----------------------------------------------------------------------------------------------------------------------
--- Inserting Payment Bouncing rules ---

Insert into KSSA_RULE_SET (ID, NAME, RULE_TYPE_ID_FK, HEADER) values (3, 'Payment Bouncing', 3,
'import java.util.*;
import java.math.*;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.model.rule.*;
import com.sigmasys.kuali.ksa.service.brm.*;
import com.sigmasys.kuali.ksa.model.security.*;
import com.sigmasys.kuali.ksa.util.*;
import org.apache.commons.lang.*;

expander ksa.dsl

global List permissionNames;
global List transactionTypeIds;
global List holdIssueNames;
global List flagTypeCodes;
global List accountTypeNames;
global BigDecimal transactionAmount;


')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (11, 'Bounce 1', 3, 0, null,
'(Transaction type is "cash, finaid" and Transaction amount > $2000)',
'Use flag type "OverLimit", access level "DEF_FLAG_LEVEL_CD", severity 10 to create flag expiring in 90 days
 Use code "1020" to charge $850.45
 ')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (12, 'Bounce 2', 3, 0, null,
'(Hold issue is "Unpaid Library Fine")',
'Use access level "DEF_MEMO_LEVEL_CD" to create memo "Pay Library Fine" expiring in 30 days
 Use access level "DEF_ALERT_LEVEL_CD" to create alert "Library Fine Alert" expiring in 60 days
 Use hold issue type "kuali.hold.issue.type.financial", hold issue name "Collections" to create hold "Library Fine" with description "Unpaid Library Fine" expiring in 14 days
')!

Insert into KSSA_RULE (ID, NAME, RULE_TYPE_ID_FK, PRIORITY, HEADER, LHS, RHS) values (13, 'Bounce 3', 3, 0, null,
'(Account type is "Undergraduate Student" and Transaction amount < $100)',
'Use flag type "BadCheck", access level "DEF_FLAG_LEVEL_CD", severity 1 to create flag expiring in 5 days
 Use hold issue type "kuali.hold.issue.type.financial", hold issue name "Unpaid Tuition Prior Term" to create hold "Unpaid Tuition" with description "" expiring in 17 days
')!


--- PA associations
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (1, 1)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (1, 2)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (1, 3)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (1, 4)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (1, 5)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (1, 6)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (1, 7)!

-- AB associations
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (2, 8)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (2, 9)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (2, 10)!

-- PB associations
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (3, 11)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (3, 12)!
Insert into KSSA_RULE_SET_RULE ( RULE_SET_ID_FK, RULE_ID_FK ) values (3, 13)!


-----------------------------------------------------------------------------------------------------------------------