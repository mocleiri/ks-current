--------------------------------------------------------
--  File created - Tuesday-April-10-2012   
--------------------------------------------------------

--  SYSTEM CONFIGURATION PARAMETERS --
-- Rice-specific parameters --
insert into KSSA_CONFIG (NAME, VALUE) values ('application.host', 'localhost');
insert into KSSA_CONFIG (NAME, VALUE) values ('rice.messaging.enabled', 'true');

-- KSA-specific parameters --
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.locale.lang', 'en');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.locale.country', 'US');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.import.single.batch.failure', 'false');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.logging.operation', 'true');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.deferment.type.id', 'DEF');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.payment.contest.type.id', 'cash');

-- General Ledger settings
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.general.ledger.type', 'SAR');

-- The GL mode can be Individual, Batch, BatchRollup
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.general.ledger.mode', 'Batch');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.general.ledger.payment.application.statement', 'PA Transaction');

-- Payment Billing parameters
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.payment.billing.rounding.problem.memo', 'PB rounding problem occurred');

-- Bill Record parameters
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.bill.delivery.method', 'mail');


-- Combined Cash Limit Tracking Settings
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.cash.tracking.system', 'ON');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.cash.tracking.notification', 'OFF');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.cash.tracking.amount', '10000');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.cash.tracking.days', '365');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.cash.tracking.tag', 'Cash');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.cash.tracking.email.address.from', 'ksa.sigmasys@gmail.com');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.cash.tracking.email.recipient', 'mivanov@sigmasys.com');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.cash.tracking.email.subject', 'Cash Limit Event');

-- SMTP service parameters
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.mail.host', 'smtp.gmail.com');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.mail.port', '587');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.mail.protocol', 'smtp');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.mail.auth', 'true');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.mail.user', 'ksa.sigmasys');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.mail.password', 'sigmasys2013_');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.mail.tls.enabled', 'true');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.mail.address.from', 'ksa.sigmasys@gmail.com');

-- Memo settings
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.memo.level', 'DEF_MEMO_LEVEL_CD');

-- Account settings
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.account.status.type', 'N/A');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.account.credit.limit', '20000');

-- Age Debt method can either be "BALANCE_FORWARD" or "OPEN_ITEM"
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.account.age.debt.method', 'BALANCE_FORWARD');

-- View parameters
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.quickview.information.count', '4');


-- KFS constants
insert into KSSA_CONFIG (NAME, VALUE) values ('kfs.coa.code', '01');
insert into KSSA_CONFIG (NAME, VALUE) values ('kfs.organization.code', 'KS');
insert into KSSA_CONFIG (NAME, VALUE) values ('kfs.document.number.prefix', 'KSAJV');
insert into KSSA_CONFIG (NAME, VALUE) values ('kfs.email.address', 'bursar@sigmauniversity.edu');
insert into KSSA_CONFIG (NAME, VALUE) values ('kfs.postal.address', '1000 Campus ave UMD 20009');
insert into KSSA_CONFIG (NAME, VALUE) values ('kfs.phone.number', '8882345678');
insert into KSSA_CONFIG (NAME, VALUE) values ('kfs.campus.code', '01');
insert into KSSA_CONFIG (NAME, VALUE) values ('kfs.department.name', 'Bursar');
insert into KSSA_CONFIG (NAME, VALUE) values ('kfs.balance.type.code', 'TR');
insert into KSSA_CONFIG (NAME, VALUE) values ('kfs.document.type.code', 'JV');
-- TODO: figure out what the correct 'kfs.object.type.code' value should be
insert into KSSA_CONFIG (NAME, VALUE) values ('kfs.object.type.code', '');
insert into KSSA_CONFIG (NAME, VALUE) values ('kfs.origination.code', 'BR');
insert into KSSA_CONFIG (NAME, VALUE) values ('kfs.transaction.gl.entry.description', 'KSA General Ledger Transaction');

-- KSA Transaction settings
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.transaction.recognition.year', '2014');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.transaction.default.startdate', '2010-01-01');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.transaction.default.enddate', '2014-07-01');

-- IRS 1098T report settings
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.1098.filer.name', 'Sigma University');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.1098.filer.address1', '2305 S Colorado Blvd');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.1098.filer.address2', '');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.1098.filer.address3', '');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.1098.filer.city', 'Denver');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.1098.filer.state', 'CO');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.1098.filer.country', 'US');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.1098.filer.zip', '80222');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.1098.filer.fein', '55-5555555');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.1098.filer.phone', '703-555-5555');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.1098.reporting.method.change', 'false');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.1098.tag.amount.billed', 'Billed1098');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.1098.tag.insurance.refund', 'InsRef1098');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.1098.tag.grants', 'Scholarship1098');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.1098.us.ssn.tax.type', 'SSN');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.1098.us.ssn.display.digits', '4');

-- IRS 8300 report settings
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.8300.filer.name', 'Sigma University');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.8300.filer.address1', '2305 S Colorado Blvd');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.8300.filer.address2', '');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.8300.filer.address3', '');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.8300.filer.city', 'Denver');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.8300.filer.state', 'CO');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.8300.filer.country', 'US');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.8300.filer.zip', '80222');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.8300.filer.fein', '55-5555555');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.8300.business.nature', 'Soul trading');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.8300.us.ssn.tax.type', 'SSN');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.8300.default.type', 'PERSONAL_SERVICE');

-- Refund constants
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.refund.ach.bank.type', 'ACH');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.refund.type.cash', 'cash');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.refund.type.account', 'account');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.refund.type.source', 'source');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.refund.type.check', 'check');
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.refund.type.ach', 'ach');

-- Charge constants
insert into KSSA_CONFIG (NAME, VALUE) values ('ksa.charge.cancellation.rule', 'DAYS(10)PERCENTAGE(50)');

-- BRM (Drools) rule types
insert into KSSA_RULE_TYPE (ID, NAME, DESCRIPTION) values (1, 'DSL', 'Drools DSL');
insert into KSSA_RULE_TYPE (ID, NAME, DESCRIPTION) values (2, 'DRL', 'Drools Rule Language');
insert into KSSA_RULE_TYPE (ID, NAME, DESCRIPTION) values (3, 'DSLR', 'Drools DSL Rule');
insert into KSSA_RULE_TYPE (ID, NAME, DESCRIPTION) values (4, 'XDRL', 'Drools XML Rule Language');

-- Allowable GL account values --
insert into KSSA_ALLOWABLE_GL_ACCOUNT (ID, PATTERN) values (1,'\d{2}-\d-\d{6}\s\d{4}');
insert into KSSA_ALLOWABLE_GL_ACCOUNT (ID, PATTERN) values (2, '.*');








