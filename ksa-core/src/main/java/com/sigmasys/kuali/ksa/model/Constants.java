package com.sigmasys.kuali.ksa.model;

import java.math.BigDecimal;

/**
 * KSA commonly used constants.
 *
 * @author Michael Ivanov
 */
public interface Constants {

    // Generic constants
    String MODULE_NAME = "KSA";

    // Persistence units
    String KS_PERSISTENCE_UNIT = "ks";
    String KSA_PERSISTENCE_UNIT = "ksa";

    BigDecimal BIG_DECIMAL_HUNDRED = new BigDecimal(100);

    // ID Generator constants
    String ID_GENERATOR_CLASS = "com.sigmasys.kuali.ksa.util.LongIdGenerator";
    String ID_GENERATOR_NAME = "idGenerator";

    // KSA parameter names
    String LOGGING_OPERATION = "ksa.logging.operation";

    String DEFAULT_WRITE_OFF_ROLLUP = "ksa.writeoff.rollup";
    String DEFAULT_MEMO_LEVEL = "ksa.memo.level";
    String LOCALE_LANG = "ksa.locale.lang";
    String LOCALE_COUNTRY = "ksa.locale.country";
    String IMPORT_SINGLE_BATCH_FAILURE = "ksa.import.single.batch.failure";
    String DEFAULT_GL_TYPE = "ksa.general.ledger.type";
    String DEFAULT_GL_MODE = "ksa.general.ledger.mode";
    String DEFAULT_GL_PA_STATEMENT = "ksa.general.ledger.payment.application.statement";
    String CONTEST_PAYMENT_TYPE = "ksa.payment.contest.type.id";

    // Cash tracking constants
    String CASH_TRACKING_SYSTEM = "ksa.cash.tracking.system";
    String CASH_TRACKING_NOTIFICATION = "ksa.cash.tracking.notification";
    String CASH_TRACKING_AMOUNT = "ksa.cash.tracking.amount";
    String CASH_TRACKING_DAYS = "ksa.cash.tracking.days";
    String CASH_TRACKING_TAG = "ksa.cash.tracking.tag";
    String CASH_TRACKING_EMAIL_RECIPIENT = "ksa.cash.tracking.email.recipient";
    String CASH_TRACKING_EMAIL_SUBJECT = "ksa.cash.tracking.email.subject";

    // Charge constants
    String CHARGE_DEFAULT_CANCELLATION_RULE = "ksa.charge.cancellation.rule";

    // BRM parameters
    String BRM_CLASSPATH = "drools";
    String BRM_FM_MAIN_RULE_SET_NAME = "Fee Management Main";
    String BRM_PA_RULE_SET_NAME = "Payment Application";
    String BRM_AB_RULE_SET_NAME = "Account Blocking";
    String BRM_PB_RULE_SET_NAME = "Payment Bouncing";

    // DB transaction parameters
    String TRANSACTION_BATCH_ENABLED = "ksa.transaction.batch.enabled";
    String TRANSACTION_BATCH_SIZE = "ksa.transaction.batch.size";

    // BRM parameter names
    String BRM_BLOCK_NAMES = "blockNames";
    String BRM_PERMISSION_NAMES = "permissionNames";
    String BRM_HOLD_ISSUE_NAMES = "holdIssueNames";
    String BRM_ATP_IDS = "atpIds";
    String BRM_TRANSACTION_TYPE_IDS = "transactionTypeIds";
    String BRM_ACCOUNT_TYPE_NAMES = "accountTypeNames";
    String BRM_FLAG_TYPE_CODES = "flagTypeCodes";
    String BRM_TRANSACTION_AMOUNT = "transactionAmount";

    // Quick View parameters
    String QUICKVIEW_INFORMATION_COUNT = "ksa.quickview.information.count";

    // WS constants
    String WS_NAMESPACE = "http://sigmasys.com/";

    // Rice constants
    String RICE_APPLICATION_HOST = "application.host";
    String RICE_MESSAGING_ENABLED = "rice.messaging.enabled";

    // Fee management constants
    String FM_PRECLEAR_MANIFEST = "ksa.fm.preclear.manifest";

    // ---------------------------------------------------------------
    // DATE FORMATS
    // ---------------------------------------------------------------
    String DATE_FORMAT_US = "MM/dd/yyyy";
    String DATE_FORMAT_EXPORT = "yyyy-MM-dd";

    String TIMESTAMP_FORMAT = "MM/dd/yyyy HH:mm:ss.SSS";
    String TIMESTAMP_FORMAT_NO_MS = "MM/dd/yyyy HH:mm:ss";
    String DB_TIMESTAMP_FORMAT_NO_MS = "MM/dd/yyyy hh24:mi:ss";

    String TIME_FORMAT = "HH:mm:ss.SSS";
    String TIME_FORMAT_NO_MS = "HH:mm:ss";
    String TIME_FORMAT_MINUTES = "HH:mm";

    // Activity constants
    Long ACTIVITY_TYPE_DATA_CHANGE_ID = 1L;

    // Refund constants:
    String REFUND_ACCOUNT_SYSTEM_NAME = "ksa.refund.account.system.name";
    String REFUND_CHECK_GROUP = "ksa.refund.check.group";
    String REFUND_CHECK_SYSTEM_NAME = "ksa.refund.check.system.name";
   
    String REFUND_CHECK_GROUP_ROLLUP = "ksa.refund.check.group.rollup";
    String REFUND_ACH_GROUP = "ksa.refund.ach.group";

    String REFUND_ACH_SYSTEM_NAME = "ksa.refund.ach.system.name";
    String REFUND_ACH_BANK_TYPE = "ksa.refund.ach.bank.type";
    String REFUND_ACH_GROUP_ROLLUP = "ksa.refund.ach.group.rollup";

    String REFUND_METHOD_OVERRIDE = "ksa.refund.override.method";

    // Refund types
    String REFUND_TYPE_CASH = "ksa.refund.type.cash";
    String REFUND_TYPE_SOURCE = "ksa.refund.type.source";
    String REFUND_TYPE_ACCOUNT = "ksa.refund.type.account";
    String REFUND_TYPE_CHECK = "ksa.refund.type.check";
    String REFUND_TYPE_ACH = "ksa.refund.type.ach";

    // Payment Billing constants
    String PAYMENT_BILLING_ROUNDING_PROBLEM_MEMO = "ksa.payment.billing.rounding.problem.memo";

    // Bill Records constants
    String BILL_DELIVERY_METHOD = "ksa.bill.delivery.method";

    // Memo constants
    String MEMO_DEFAULT_ACCESS_LEVEL = "ksa.memo.level";

    // Account service constants
    String AGE_DEBT_METHOD = "ksa.account.age.debt.method";

    // KFS constants (mostly used by Transaction XML export services)
    String KFS_CHART_OF_ACCOUNTS_CODE_PARAM_NAME = "kfs.coa.code";
    String KFS_ORGANIZATION_CODE_PARAM_NAME = "kfs.organization.code";
    String KFS_DOCUMENT_NUMBER_PREFIX_PARAM_NAME = "kfs.document.number.prefix";
    String KFS_EMAIL_ADDRESS_PARAM_NAME = "kfs.email.address";
    String KFS_POSTAL_ADDRESS_PARAM_NAME = "kfs.postal.address";
    String KFS_PHONE_NUMBER_PARAM_NAME = "kfs.phone.number";
    String KFS_CAMPUS_CODE_PARAM_NAME = "kfs.campus.code";
    String KFS_DEPARTMENT_NAME_PARAM_NAME = "kfs.department.name";
    String KFS_BALANCE_TYPE_CODE_PARAM_NAME = "kfs.balance.type.code";
    String KFS_DOCUMENT_TYPE_CODE_PARAM_NAME = "kfs.document.type.code";
    String KFS_OBJECT_TYPE_CODE_PARAM_NAME = "kfs.object.type.code";
    String KFS_ORIGINATION_CODE_PARAM_NAME = "kfs.origination.code";
    String KFS_TRANSACTION_GL_ENTRY_DESCRIPTION_PARAM_NAME = "kfs.transaction.gl.entry.description";

    // 1098T Report constants
    String KSA_1098_SSN_TAX_TYPE = "ksa.1098.us.ssn.tax.type";
    String KSA_1098_SSN_DISPLAY_DIGITS = "ksa.1098.us.ssn.display.digits";
    String KSA_1098_FILER_NAME = "ksa.1098.filer.name";
    String KSA_1098_FILER_ADDRESS1 = "ksa.1098.filer.address1";
    String KSA_1098_FILER_ADDRESS2 = "ksa.1098.filer.address2";
    String KSA_1098_FILER_ADDRESS3 = "ksa.1098.filer.address3";
    String KSA_1098_FILER_CITY = "ksa.1098.filer.city";
    String KSA_1098_FILER_STATE = "ksa.1098.filer.state";
    String KSA_1098_FILER_COUNTRY = "ksa.1098.filer.country";
    String KSA_1098_FILER_ZIP = "ksa.1098.filer.zip";
    String KSA_1098_FILER_FEIN = "ksa.1098.filer.fein";
    String KSA_1098_FILER_PHONE = "ksa.1098.filer.phone";
    String KSA_1098_TAG_BILLED_AMOUNT = "ksa.1098.tag.amount.billed";
    String KSA_1098_TAG_INSURANCE_REFUND = "ksa.1098.tag.insurance.refund";
    String KSA_1098_TAG_GRANTS = "ksa.1098.tag.grants";
    String KSA_1098_REPORTING_METHOD_CHANGE = "ksa.1098.reporting.method.change";

    // 8300 Report constants
    String KSA_8300_DEFAULT_TYPE = "ksa.8300.default.type";
    String KSA_8300_SSN_TAX_TYPE = "ksa.8300.us.ssn.tax.type";
    String KSA_8300_FILER_NAME = "ksa.8300.filer.name";
    String KSA_8300_FILER_ADDRESS1 = "ksa.8300.filer.address1";
    String KSA_8300_FILER_ADDRESS2 = "ksa.8300.filer.address2";
    String KSA_8300_FILER_ADDRESS3 = "ksa.8300.filer.address3";
    String KSA_8300_FILER_CITY = "ksa.8300.filer.city";
    String KSA_8300_FILER_STATE = "ksa.8300.filer.state";
    String KSA_8300_FILER_COUNTRY = "ksa.8300.filer.country";
    String KSA_8300_FILER_ZIP = "ksa.8300.filer.zip";
    String KSA_8300_FILER_FEIN = "ksa.8300.filer.fein";
    String KSA_8300_BUSINESS_NATURE = "ksa.8300.business.nature";

    // KSA Transaction constants
    String KSA_TRANSACTION_RECOGNITION_YEAR = "ksa.transaction.recognition.year";
    String KSA_TRANSACTION_DEFAULT_START_DATE = "ksa.transaction.default.startdate";
    String KSA_TRANSACTION_DEFAULT_END_DATE = "ksa.transaction.default.enddate";

    // Hold constants
    String HOLD_ISSUE_TYPE_FINANCIAL_OVERDUE_PERIOD1 = "kuali.hold.issue.type.financial.overdue.period1";
    String HOLD_ISSUE_TYPE_FINANCIAL_OVERDUE_PERIOD2 = "kuali.hold.issue.type.financial.overdue.period2";
    String HOLD_ISSUE_TYPE_FINANCIAL_OVERDUE_PERIOD3 = "kuali.hold.issue.type.financial.overdue.period3";

    // KSA mail client constants
    String MAIL_HOST = "ksa.mail.host";
    String MAIL_PORT = "ksa.mail.port";
    String MAIL_PROTOCOL = "ksa.mail.protocol";
    String MAIL_AUTH = "ksa.mail.auth";
    String MAIL_USER = "ksa.mail.user";
    String MAIL_PASSWORD = "ksa.mail.password";
    String MAIL_TLS_ENABLED = "ksa.mail.tls.enabled";
    String MAIL_ADDRESS_FROM = "ksa.mail.address.from";

    // KIM API constants
    String KIM_ENTITY_TYPE_CODE = "PERSON";
    String KIM_DEFAULT_NAME_TYPE = "PRM";
    String KIM_DEFAULT_ADDRESS_TYPE = "HM";

}
