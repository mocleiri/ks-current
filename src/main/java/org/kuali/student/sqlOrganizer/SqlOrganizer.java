package org.kuali.student.sqlOrganizer;

import com.akiban.sql.*;
import com.akiban.sql.parser.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.kuali.common.jdbc.reader.DefaultSqlReader;
import org.kuali.common.jdbc.reader.SqlReader;
import org.kuali.common.jdbc.reader.model.Comments;
import org.kuali.common.jdbc.reader.model.Delimiter;
import org.kuali.common.jdbc.reader.model.LineSeparator;
import org.kuali.common.util.LocationUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: lsymms
 * Date: 12/30/13
 * Time: 2:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class SqlOrganizer {

    public String subProject;
    public String module;
    public List<String> unparsableStmts;
    private static final String PROJECT_PATH = "C:\\data\\development\\intellijProjects\\enr-aggregate";

    public static final String OUTPUT_DIR_PATH = "C:\\data\\development\\sql-organizer-output\\";
    private static final Pattern CREATE_SEQ_PATTERN = Pattern.compile("\\s*(create|CREATE)\\s*(sequence|SEQUENCE)\\s*(\\w*)");
    private static final Pattern DROP_SEQ_PATTERN = Pattern.compile("\\s*(drop|DROP)\\s*(sequence|SEQUENCE)\\s*(\\w*)");
    private static final Pattern NDX_RENAME_PATTERN = Pattern.compile("\\s*(alter|ALTER)\\s*(index|INDEX)\\s*(\\w*)\\s*(rename|RENAME)\\s*(to|TO)\\s*(\\w*)");
    private static final Pattern CNSTRT_RENAME_PATTERN = Pattern.compile("\\s*(alter|ALTER)\\s*(table|TABLE)\\s*(\\w*)\\s*(rename|RENAME)\\s*(constraint|CONSTRAINT)\\s*(\\w*)\\s*(to|TO)\\s*(\\w*)");
    private static final Pattern CREATE_TABLE_PATTERN = Pattern.compile("\\s*(create|CREATE)\\s*(table|TABLE)\\s*(\\w*)");
    private static final Pattern COMPLEX_DROP_TABLE_PATTERN = Pattern.compile("IF\\s*TEMP\\s*>\\s*0\\s*THEN\\s*EXECUTE\\s*IMMEDIATE\\s*'DROP\\s*TABLE\\s*(\\w*)\\s*CASCADE\\s*CONSTRAINTS\\s*PURGE'");
    private static Map<DatabaseDataType, Set<String>> dataTypeTableSets;

     public String[] splitStatements (String sqlStatements) {
        return sqlStatements.split("\n\\s*" + "/" + "\\s*" + "(\n|\\Z)");
    }

    // TODO: convert to config
    public void init() {
        this.unparsableStmts = new ArrayList<String>();
        dataTypeTableSets = new HashMap<DatabaseDataType, Set<String>>();
        dataTypeTableSets.put(DatabaseDataType.BOOTSTRAP, new HashSet<String>(Arrays.asList("KRAD_MSG_T","KRCR_CMPNT_SET_T","KRCR_CMPNT_T","KRCR_DRVD_CMPNT_T","KRCR_NMSPC_T","KRCR_PARM_T","KRCR_PARM_TYP_T","KRCR_STYLE_T","KREN_CHNL_PRODCR_T","KREN_CHNL_SUBSCRP_T","KREN_CHNL_T","KREN_CNTNT_TYP_T","KREN_MSG_DELIV_T","KREN_MSG_T","KREN_NTFCTN_MSG_DELIV_T","KREN_NTFCTN_T","KREN_PRIO_T","KREN_PRODCR_T","KREN_RECIP_DELIV_T","KREN_RECIP_LIST_T","KREN_RECIP_PREFS_T","KREN_RECIP_T","KREN_RVWER_T","KREN_SNDR_T","KREW_DOC_HDR_EXT_DT_T","KREW_DOC_HDR_EXT_FLT_T","KREW_DOC_HDR_EXT_LONG_T","KREW_DOC_HDR_EXT_T","KREW_DOC_LNK_T","KREW_DOC_NTE_T","KREW_DOC_TYP_APP_DOC_STAT_T","KREW_DOC_TYP_APP_STAT_CAT_T","KREW_DOC_TYP_ATTR_T","KREW_DOC_TYP_PLCY_RELN_T","KREW_DOC_TYP_PROC_T","KREW_DOC_TYP_T","KREW_EDL_ASSCTN_T","KREW_EDL_DEF_T","KREW_EDL_DMP_T","KREW_EDL_FLD_DMP_T","KREW_OUT_BOX_ITM_T","KREW_PPL_FLW_ATTR_T","KREW_PPL_FLW_DLGT_T","KREW_PPL_FLW_MBR_T","KREW_PPL_FLW_T","KREW_RTE_BRCH_PROTO_T","KREW_RTE_BRCH_ST_T","KREW_RTE_BRCH_T","KREW_RTE_NODE_CFG_PARM_T","KREW_RTE_NODE_INSTN_LNK_T","KREW_RTE_NODE_INSTN_ST_T","KREW_RTE_NODE_INSTN_T","KREW_RTE_NODE_LNK_T","KREW_RTE_NODE_T","KREW_RULE_ATTR_T","KREW_RULE_EXPR_T","KREW_RULE_EXT_T","KREW_RULE_EXT_VAL_T","KREW_RULE_RSP_T","KREW_RULE_T","KREW_RULE_TMPL_ATTR_T","KREW_RULE_TMPL_OPTN_T","KREW_RULE_TMPL_T","KREW_TYP_ATTR_T","KREW_TYP_T","KREW_USR_OPTN_T","KRIM_ADDR_TYP_T","KRIM_AFLTN_TYP_T","KRIM_ATTR_DEFN_T","KRIM_CTZNSHP_STAT_T","KRIM_DLGN_MBR_ATTR_DATA_T","KRIM_DLGN_MBR_T","KRIM_DLGN_T","KRIM_EMAIL_TYP_T","KRIM_EMP_STAT_T","KRIM_EMP_TYP_T","KRIM_ENT_NM_TYP_T","KRIM_ENT_TYP_T","KRIM_EXT_ID_TYP_T","KRIM_GRP_ATTR_DATA_T","KRIM_GRP_DOCUMENT_T","KRIM_GRP_MBR_T","KRIM_GRP_T","KRIM_PERM_ATTR_DATA_T","KRIM_PERM_T","KRIM_PERM_TMPL_T","KRIM_PERSON_DOCUMENT_T","KRIM_PHONE_TYP_T","KRIM_PND_ADDR_MT","KRIM_PND_AFLTN_MT","KRIM_PND_CTZNSHP_MT","KRIM_PND_DLGN_MBR_ATTR_DATA_T","KRIM_PND_DLGN_MBR_T","KRIM_PND_DLGN_T","KRIM_PND_EMAIL_MT","KRIM_PND_EMP_INFO_MT","KRIM_PND_GRP_ATTR_DATA_T","KRIM_PND_GRP_MBR_T","KRIM_PND_GRP_PRNCPL_MT","KRIM_PND_NM_MT","KRIM_PND_PHONE_MT","KRIM_PND_PRIV_PREF_MT","KRIM_PND_ROLE_MBR_ATTR_DATA_MT","KRIM_PND_ROLE_MBR_MT","KRIM_PND_ROLE_MT","KRIM_PND_ROLE_PERM_T","KRIM_PND_ROLE_RSP_ACTN_MT","KRIM_PND_ROLE_RSP_T","KRIM_ROLE_DOCUMENT_T","KRIM_ROLE_PERM_T","KRIM_ROLE_RSP_ACTN_T","KRIM_ROLE_RSP_T","KRIM_ROLE_T","KRIM_RSP_ATTR_DATA_T","KRIM_RSP_T","KRIM_RSP_TMPL_T","KRIM_TYP_ATTR_T","KRIM_TYP_T","KRLC_CMP_T","KRLC_CMP_TYP_T","KRLC_CNTRY_T","KRLC_CNTY_T","KRLC_PSTL_CD_T","KRLC_ST_T","KRMS_ACTN_ATTR_T","KRMS_ACTN_T","KRMS_ATTR_DEFN_T","KRMS_CNTXT_ATTR_T","KRMS_CNTXT_T","KRMS_CNTXT_VLD_ACTN_TYP_T","KRMS_CNTXT_VLD_AGENDA_TYP_T","KRMS_CNTXT_VLD_FUNC_T","KRMS_CNTXT_VLD_RULE_TYP_T","KRMS_CNTXT_VLD_TERM_SPEC_T","KRMS_CTGRY_T","KRMS_FUNC_CTGRY_T","KRMS_FUNC_PARM_T","KRMS_FUNC_T","KRMS_NL_TMPL_ATTR_T","KRMS_NL_TMPL_T","KRMS_NL_USAGE_ATTR_T","KRMS_NL_USAGE_T","KRMS_TERM_RSLVR_ATTR_T","KRMS_TERM_RSLVR_INPUT_SPEC_T","KRMS_TERM_RSLVR_PARM_SPEC_T","KRMS_TERM_RSLVR_T","KRMS_TERM_SPEC_CTGRY_T","KRMS_TERM_SPEC_T","KRMS_TYP_ATTR_T","KRMS_TYP_RELN_T","KRMS_TYP_T","KRNS_ADHOC_RTE_ACTN_RECIP_T","KRNS_ATT_T","KRNS_DOC_HDR_T","KRNS_LOOKUP_RSLT_T","KRNS_LOOKUP_SEL_T","KRNS_MAINT_DOC_ATT_LST_T","KRNS_MAINT_DOC_ATT_T","KRNS_MAINT_DOC_T","KRNS_MAINT_LOCK_T","KRNS_NTE_T","KRNS_NTE_TYP_T","KRNS_PESSIMISTIC_LOCK_T","KRNS_SESN_DOC_T","KRSB_BAM_PARM_T","KRSB_BAM_T","KRSB_MSG_PYLD_T","KRSB_MSG_QUE_T","KRSB_QRTZ_BLOB_TRIGGERS","KRSB_QRTZ_CALENDARS","KRSB_QRTZ_CRON_TRIGGERS","KRSB_QRTZ_FIRED_TRIGGERS","KRSB_QRTZ_JOB_DETAILS","KRSB_QRTZ_JOB_LISTENERS","KRSB_QRTZ_LOCKS","KRSB_QRTZ_PAUSED_TRIGGER_GRPS","KRSB_QRTZ_SCHEDULER_STATE","KRSB_QRTZ_SIMPLE_TRIGGERS","KRSB_QRTZ_TRIGGERS","KRSB_QRTZ_TRIGGER_LISTENERS","KRSB_SVC_DEF_T","KRSB_SVC_DSCRPTR_T","KSCO_COMMENT_TYPE","KSCO_COMMENT_TYPE_ATTR","KSCO_REFERENCE_TYPE","KSCO_REFERENCE_TYPE_ATTR","KSCO_TAG_TYPE","KSCO_TAG_TYPE_ATTR","KSDO_DOCUMENT_TYPE","KSDO_DOCUMENT_TYPE_ATTR","KSDO_REF_DOC_RELTN_TYPE","KSDO_REF_DOC_RELTN_TYPE_ATTR","KSDO_REF_DOC_REL_ATTR","KSDO_REF_OBJ_SUB_TYPE","KSDO_REF_OBJ_SUB_TYPE_ATTR","KSDO_REF_OBJ_TYPE","KSDO_REF_OBJ_TYPE_ATTR","KSDO_REF_REL_TYP_JN_SUB_TYP","KSEM_CTX_JN_ENUM_VAL_T","KSEM_CTX_T","KSEM_ENUM_ATTR","KSEN_CODE_GENERATOR_LOCKS","KSEN_LRC_RESULT_SCALE","KSEN_LRC_RESULT_SCALE_ATTR","KSEN_LRC_RESULT_VALUE","KSEN_LRC_RESULT_VALUE_ATTR","KSEN_LRC_RVG","KSEN_LRC_RVG_ATTR","KSEN_LRC_RVG_RESULT_VALUE","KSEN_LRR_RES_SOURCE","KSEN_LRR_RES_SOURCE_ATTR","KSEN_LRR_TYPE","KSEN_POPULATION_CAT","KSEN_POPULATION_CAT_ATTR","KSEN_PROCESS","KSEN_PROCESS_ATTR","KSEN_PROCESS_CATEGORY","KSEN_PROCESS_CATEGORY_ATTR","KSEN_PROCESS_CATEGORY_RELTN","KSEN_PROCESS_CHECK","KSEN_PROCESS_CHECK_ATTR","KSEN_PROCESS_INSTRN","KSEN_PROCESS_INSTRN_AAT","KSEN_PROCESS_INSTRN_ATTR","KSEN_RICH_TEXT_T","KSEN_STATE","KSEN_STATE_ATTR","KSEN_STATE_CHG","KSEN_STATE_CHG_ATTR","KSEN_STATE_CHG_CNSTRNT","KSEN_STATE_CHG_PROPAGT","KSEN_STATE_CNSTRNT","KSEN_STATE_CNSTRNT_ATTR","KSEN_STATE_CNSTRNT_ROS","KSEN_STATE_LIFECYCLE","KSEN_STATE_LIFECYCLE_ATTR","KSEN_STATE_PROCESS","KSEN_STATE_PROPAGT","KSEN_STATE_PROPAGT_ATTR","KSEN_STATE_PROPAGT_CNSTRNT","KSEN_TYPE","KSEN_TYPETYPE_RELTN","KSEN_TYPETYPE_RELTN_ATTR","KSEN_TYPE_ATTR","KSLO_LO_ALLOWED_RELTN_TYPE","KSLO_LO_CATEGORY_TYPE","KSLO_LO_CATEGORY_TYPE_ATTR","KSLO_LO_RELTN_TYPE","KSLO_LO_RELTN_TYPE_ATTR","KSLO_LO_REPOSITORY","KSLO_LO_REPOSITORY_ATTR","KSLO_LO_TYPE","","KSLO_LO_TYPE_ATTR","KSLU_CLU_LO_ALOW_RELTN_TYPE","KSLU_CLU_LO_RELTN_TYPE","KSLU_CLU_LO_RELTN_TYPE_ATTR","KSLU_CLU_PUBL_TYPE","KSLU_CLU_PUBL_TYPE_ATTR","KSLU_CLU_PUB_TYPE","KSLU_CLU_PUB_TYPE_ATTR","KSLU_CLU_RESULT_TYPE_ATTR","KSLU_CLU_RSLT_LU_ALOW_TYPE","KSLU_CLU_RSLT_TYP","KSLU_CLU_SET_TYPE","KSLU_CLU_SET_TYPE_ATTR","KSLU_DLVMTHD_TYPE","KSLU_DLVMTHD_TYPE_ATTR","KSLU_INSTFRMT_TYPE","KSLU_INSTFRMT_TYPE_ATTR","KSLU_LULU_RELTN_TYPE","KSLU_LULU_RELTN_TYPE_ATTR","KSLU_LULU_RELTN_TYPE_JN_LU_TYP","KSLU_LUTYPE","KSLU_LU_CD_TYPE","KSLU_LU_CD_TYPE_ATTR","KSLU_LU_LU_ALOW_RELTN_TYPE","KSLU_LU_PUBL_TYPE","KSLU_LU_PUBL_TYPE_ATTR","KSLU_LU_TYPE_ATTR","KSLU_RSLTUSAGE_LU_ALOW_TYPE","KSLU_RSLT_COMP_USG_ALOW_TYPE","KSLU_RSLT_USG_TYPE","KSLU_RSLT_USG_TYPE_ATTR","KSMG_MESSAGE","KSOR_ORG_HIRCHY","KSOR_ORG_HIRCHY_ATTR","KSOR_ORG_HIRCHY_JN_ORG_TYPE","KSOR_ORG_JN_ORG_PERS_REL_TYPE","KSOR_ORG_ORG_RELTN_TYPE","KSOR_ORG_ORG_RELTN_TYPE_ATTR","KSOR_ORG_PERS_RELTN_TYPE","KSOR_ORG_PERS_RELTN_TYPE_ATTR","KSOR_ORG_TYPE","KSOR_ORG_TYPE_ATTR","KSOR_ORG_TYPE_JN_ORG_PERRL_TYP","KSPR_PROPOSAL_TYPE","KSPR_PROPOSAL_TYPE_ATTR","KSSC_SUBJ_CD_TYPE","KSST_NL_USAGE_TYPE","KSST_OBJECT_SUB_TYPE","KSST_OBJECT_SUB_TYPE_ATTR","KSST_OBJECT_TYPE","KSST_OBJECT_TYPE_ATTR","KSST_OBJ_TYP_JN_OBJ_SUB_TYP","KSST_RCTYP_JN_RCFLDTYP","KSST_REF_STMT_REL_TYPE","KSST_REF_STMT_REL_TYPE_ATTR","KSST_REQ_COM_FIELD_TYPE","KSST_REQ_COM_TYPE","KSST_REQ_COM_TYPE_ATTR","KSST_REQ_COM_TYPE_NL_TMPL","KSST_RSTMT_RTYP_JN_OSUB_TYP","KSST_RSTMT_RTYP_JN_STMT_TYP","KSST_STMT_TYPE","KSST_STMT_TYPE_ATTR","KSST_STMT_TYP_JN_RC_TYP","KSST_STMT_TYP_JN_STMT_TYP","KSST_USAGE_TYPE_ATTR","KS_DB_VERSION")));
        dataTypeTableSets.put(DatabaseDataType.REFERENCE, new HashSet<String>(Arrays.asList("KREW_ACTN_ITM_T","KREW_ACTN_RQST_T","KREW_ACTN_TKN_T","KREW_APP_DOC_STAT_TRAN_T","KREW_ATTR_DEFN_T","KREW_ATT_T","KREW_DLGN_RSP_T","KREW_DOC_HDR_CNTNT_T","KREW_DOC_HDR_T","KREW_INIT_RTE_NODE_INSTN_T","KRIM_ENTITY_ADDR_T","KRIM_ENTITY_AFLTN_T","KRIM_ENTITY_BIO_T","KRIM_ENTITY_CACHE_T","KRIM_ENTITY_CTZNSHP_T","KRIM_ENTITY_EMAIL_T","KRIM_ENTITY_EMP_INFO_T","KRIM_ENTITY_ETHNIC_T","KRIM_ENTITY_EXT_ID_T","KRIM_ENTITY_NM_T","KRIM_ENTITY_PHONE_T","KRIM_ENTITY_PRIV_PREF_T","KRIM_ENTITY_RESIDENCY_T","KRIM_ENTITY_VISA_T","KRIM_ROLE_MBR_ATTR_DATA_T","KRIM_ROLE_MBR_T","KRMS_AGENDA_ATTR_T","KRMS_AGENDA_ITM_T","KRMS_AGENDA_T","KRMS_CMPND_PROP_PROPS_T","KRMS_PROP_PARM_T","KRMS_PROP_T","KRMS_REF_OBJ_KRMS_OBJ_T","KRMS_RULE_ATTR_T","KRMS_RULE_T","KRMS_TERM_PARM_T","KRMS_TERM_T","KSCO_COMMENT","KSCO_COMMENT_ATTR","KSCO_REFERENCE","KSCO_RICH_TEXT_T","KSCO_TAG","KSCO_TAG_ATTR","KSDO_DOCUMENT","KSDO_DOCUMENT_ATTR","KSDO_DOCUMENT_CATEGORY","KSDO_DOCUMENT_CATEGORY_ATTR","KSDO_DOCUMENT_JN_DOC_CATEGORY","KSDO_REF_DOC_RELTN","KSDO_RICH_TEXT_T","KSEN_APPT","KSEN_APPT_ATTR","KSEN_APPT_SLOT","KSEN_APPT_SLOT_ATTR","KSEN_APPT_WINDOW","KSEN_APPT_WINDOW_ATTR","KSEN_ATP","KSEN_ATPATP_RELTN","KSEN_ATPATP_RELTN_ATTR","KSEN_ATPMSTONE_RELTN","KSEN_ATP_ATTR","KSEN_CO_AO_CLUSTER","KSEN_CO_AO_CLUSTER_ATTR","KSEN_CO_AO_CLUSTER_SET","KSEN_CO_AO_CLUSTER_SET_AO","KSEN_CO_SEAT_POOL_DEFN","KSEN_CO_SEAT_POOL_DEFN_ATTR","KSEN_CWL","KSEN_CWL_ACTIV_OFFER","KSEN_CWL_ATTR","KSEN_CWL_FORMAT_OFFER","KSEN_CWL_WLIST_ENTRY","KSEN_CWL_WLIST_ENTRY_ATTR","KSEN_ENROLLMENT_FEE","KSEN_ENROLLMENT_FEE_ATTR","KSEN_HOLD","KSEN_HOLD_ATTR","KSEN_HOLD_ISSUE","KSEN_HOLD_ISSUE_ATTR","KSEN_LPR","KSEN_LPR_ATTR","KSEN_LPR_RESULT_VAL_GRP","KSEN_LPR_ROSTER","KSEN_LPR_ROSTER_ASSO_LUI_ID","KSEN_LPR_ROSTER_ATTR","KSEN_LPR_ROSTER_ENTRY","KSEN_LPR_ROSTER_ENTRY_ATTR","KSEN_LPR_TRANS","KSEN_LPR_TRANS_ATTR","KSEN_LPR_TRANS_ITEM","KSEN_LPR_TRANS_ITEM_ATTR","KSEN_LPR_TRANS_ITEM_RQST_OPT","KSEN_LPR_TRANS_ITEM_RVG","KSEN_LRR","KSEN_LRR_ATTR","KSEN_LRR_RES_SRC_RELTN","KSEN_LUI","KSEN_LUICAPACITY_RELTN","KSEN_LUILUI_RELTN","KSEN_LUILUI_RELTN_ATTR","KSEN_LUI_ATTR","KSEN_LUI_CAMPUS_LOC","KSEN_LUI_CAPACITY","KSEN_LUI_CAPACITY_ATTR","KSEN_LUI_CLUCLU_RELTN","KSEN_LUI_IDENT","KSEN_LUI_IDENT_ATTR","KSEN_LUI_LU_CD","KSEN_LUI_LU_CD_ATTR","KSEN_LUI_RELATED_LUI_TYPES","KSEN_LUI_RESULT_VAL_GRP","KSEN_LUI_SCHEDULE","KSEN_LUI_SET","KSEN_LUI_SET_ATTR","KSEN_LUI_SET_LUI","KSEN_LUI_UNITS_CONT_OWNER","KSEN_LUI_UNITS_DEPLOYMENT","KSEN_MSTONE","KSEN_MSTONE_ATTR","KSEN_POPULATION","KSEN_POPULATION_ATTR","KSEN_POPULATION_CAT_RELTN","KSEN_POPULATION_RULE","KSEN_POPULATION_RULE_AGENDA","KSEN_POPULATION_RULE_ATTR","KSEN_POPULATION_RULE_CHILD_POP","KSEN_POPULATION_RULE_GRP","KSEN_POPULATION_RULE_PERS","KSEN_POPULATION_RULE_SOT","KSEN_ROOM","KSEN_ROOM_ACCESS_TYPE","KSEN_ROOM_ATTR","KSEN_ROOM_BUILDING","KSEN_ROOM_BUILDING_ATTR","KSEN_ROOM_FIXED_RSRC","KSEN_ROOM_FIXED_RSRC_ATTR","KSEN_ROOM_RESP_ORG","KSEN_ROOM_RESP_ORG_ATTR","KSEN_ROOM_USAGE","KSEN_ROOM_USAGE_ATTR","KSEN_SCHED","KSEN_SCHED_ATTR","KSEN_SCHED_CMP","KSEN_SCHED_CMP_TMSLOT","KSEN_SCHED_REF_OBJECT","KSEN_SCHED_RQST","KSEN_SCHED_RQST_ATTR","KSEN_SCHED_RQST_CMP","KSEN_SCHED_RQST_CMP_BLDG","KSEN_SCHED_RQST_CMP_CAMPUS","KSEN_SCHED_RQST_CMP_ORG","KSEN_SCHED_RQST_CMP_ROOM","KSEN_SCHED_RQST_CMP_RT","KSEN_SCHED_RQST_CMP_TMSLOT","KSEN_SCHED_RQST_SET","KSEN_SCHED_RQST_SET_ATTR","KSEN_SCHED_TMSLOT","KSEN_SCHED_TMSLOT_ATTR","KSEN_SOC","KSEN_SOC_ATTR","KSEN_SOC_ROR","KSEN_SOC_ROR_ATTR","KSEN_SOC_ROR_ITEM","KSEN_SOC_ROR_ITEM_ATTR","KSEN_SOC_ROR_OPTION","KSLO_ATTR","KSLO_LO","KSLO_LO_CATEGORY","KSLO_LO_CATEGORY_ATTR","KSLO_LO_JN_LOCATEGORY","KSLO_LO_RELTN","KSLO_LO_RELTN_ATTR","KSLU_CLU","KSLU_CLUCLU_RELTN","KSLU_CLUCLU_RELTN_ATTR","KSLU_CLURES_JN_RESOPT","KSLU_CLU_ACCRED","KSLU_CLU_ACCRED_ATTR","KSLU_CLU_ACCT","KSLU_CLU_ACCT_ATTR","KSLU_CLU_ACCT_JN_AFFIL_ORG","KSLU_CLU_ADMIN_ORG","KSLU_CLU_ADMIN_ORG_ATTR","KSLU_CLU_AFFIL_ORG","KSLU_CLU_ATP_TYPE_KEY","KSLU_CLU_ATTR","KSLU_CLU_CR","KSLU_CLU_FEE","KSLU_CLU_FEEREC_JN_AFFIL_ORG","KSLU_CLU_FEE_AMOUNT","KSLU_CLU_FEE_ATTR","KSLU_CLU_FEE_JN_CLU_FEE_REC","KSLU_CLU_FEE_REC","KSLU_CLU_FEE_REC_ATTR","KSLU_CLU_IDENT","KSLU_CLU_IDENT_ATTR","KSLU_CLU_INSTR","KSLU_CLU_INSTR_ATTR","KSLU_CLU_JN_ACCRED","KSLU_CLU_JN_CAMP_LOC","KSLU_CLU_JN_CLU_IDENT","KSLU_CLU_JN_CLU_INSTR","KSLU_CLU_JN_SUBJ_ORG","KSLU_CLU_LO_RELTN","KSLU_CLU_LO_RELTN_ATTR","KSLU_CLU_PUBL","KSLU_CLU_PUBL_ATTR","KSLU_CLU_PUBL_JN_CLU_INSTR","KSLU_CLU_PUBL_VARI","KSLU_CLU_RSLT","KSLU_CLU_SET","KSLU_CLU_SET_ATTR","KSLU_CLU_SET_JN_CLU","KSLU_CLU_SET_JN_CLU_SET","KSLU_LU_CODE","KSLU_LU_CODE_ATTR","KSLU_MEMSHIP","KSLU_MEMSHIP_KSLU_SPARAM","KSLU_RSLT_OPT","KSLU_RSRC","KSLU_SPARAM","KSLU_SPARAM_KSLU_SPVALUE","KSLU_SPVALUE","KSOR_ORG","KSOR_ORG_ATTR","KSOR_ORG_ORG_RELTN","KSOR_ORG_ORG_RELTN_ATTR","KSOR_ORG_PERS_RELTN","KSOR_ORG_PERS_RELTN_ATTR","KSOR_ORG_POS_RESTR","KSOR_ORG_POS_RESTR_ATTR","KSPR_PROPOSAL","KSPR_PROPOSAL_ATTR","KSPR_PROPOSAL_JN_ORG","KSPR_PROPOSAL_JN_PERSON","KSPR_PROPOSAL_JN_REFERENCE","KSPR_PROPOSAL_REFERENCE","KSPR_PROPOSAL_REFTYPE","KSPR_PROPOSAL_REFTYPE_ATTR","KSPR_RICH_TEXT_T","KSSC_SUBJ_CD","KSSC_SUBJ_CD_JN_ORG","KSST_RC_JN_RC_FIELD","KSST_REF_STMT_REL","KSST_REF_STMT_REL_ATTR","KSST_REQ_COM","KSST_REQ_COM_FIELD","KSST_RICH_TEXT_T","KSST_STMT","KSST_STMT_ATTR","KSST_STMT_JN_REQ_COM","KSST_STMT_JN_STMT")));
        dataTypeTableSets.put(DatabaseDataType.MANUAL, new HashSet<String>(Arrays.asList("KRIM_ENTITY_ENT_TYP_T","KRIM_ENTITY_T","KRIM_PRNCPL_T","KSEM_ENUM_T","KSEM_ENUM_VAL_T","KSLO_RICH_TEXT_T","KSLU_RICH_TEXT_T")));
    }

    public void printSummary() {
        if (this.unparsableStmts.size() > 0) {
            System.out.println("\n\nUnparsable Statements");
            StringBuilder sbStmts = new StringBuilder();
            for (String stmt : this.unparsableStmts) {
                sbStmts.append(stmt + "\n/\n");
            }
            System.out.println(sbStmts);
            System.out.println("\nNumber of Unparsable Statements: " + this.unparsableStmts.size());
        }

    }

    // TODO: genericize these, move to configuration
    public void organizeAggregateFiles() throws IOException {
        File root = new File(OUTPUT_DIR_PATH);
        DirManipulationUtils.cascadeDelDir(root);
        DirManipulationUtils.mkDirCascaded(root);
        organizeRiceProcessFiles();
        organizeCoreProcessFiles();
        organizeLumProcessFiles();
        organizeEnrollProcessFiles();
    }

    public void organizeEnrollProcessFiles() throws IOException {
        subProject = "ks-enroll";
        module = subProject + "-sql";
        organizeProcessFiles(PROJECT_PATH);
    }

    public void organizeLumProcessFiles() throws IOException {
        subProject = "ks-lum";
        module = subProject + "-sql";
        organizeProcessFiles(PROJECT_PATH);
    }

    public void organizeCoreProcessFiles() throws IOException {
        subProject = "ks-core";
        module = subProject + "-sql";
        organizeProcessFiles(PROJECT_PATH);
    }

    public void organizeRiceProcessFiles() throws IOException {
        subProject = "ks-core";
        module = "ks-rice-sql";
        organizeProcessFiles(PROJECT_PATH);
    }

    private void organizeProcessFiles(String projectPath) throws IOException {
        String modulePath = projectPath + "\\" + subProject + "\\" + module;
        // TODO: move to configuration
        String resourceListingFile =  modulePath + "\\target\\classes\\META-INF\\org\\kuali\\student\\" + module + "\\oracle\\other.resources";


        BufferedReader br = new BufferedReader(new FileReader(resourceListingFile));
        String sqlFile;
        while ((sqlFile = br.readLine()) != null) {
            // use contents of resource file to get list of file names
            sqlFile = sqlFile.replace("classpath:", modulePath + "\\src\\main\\resources\\");
            processSqlFile(sqlFile);
        }
        br.close();
    }

    public void processSqlFile(String sqlFile) {
        try {
            processSqlStatements(sqlFile);
        } catch (IOException e) {
            System.out.println ("**************************************\n" +
                    "*         ERROR READING FILE         *\n" +
                    "**************************************\n" +
                    "\n");
            e.printStackTrace();

        }
    }

    public static String extractMilestone(String filePathName) {
        int lastSlashPos = filePathName.lastIndexOf('\\');
        boolean windowsPath = true;

        if (lastSlashPos == -1) {
            windowsPath = false;
            lastSlashPos = filePathName.lastIndexOf('/');
        }

        if (lastSlashPos != -1) {
            String trimmedFilePath = filePathName.substring(0,lastSlashPos);
            int prevSlashPos = 0;
            if (windowsPath) {
                prevSlashPos = trimmedFilePath.lastIndexOf('\\');
            } else {
                prevSlashPos = trimmedFilePath.lastIndexOf('/');
            }

            if (lastSlashPos <= 0 || prevSlashPos <= 0) {
                return "NO_MILESTONE_IN_PATH";
            } else {
                return filePathName.substring(prevSlashPos+1,lastSlashPos);
            }
        } else {
            return "Unknown_Milestone";
        }
    }

    public SqlReader getSqlReader(){
        return new DefaultSqlReader(Delimiter.DEFAULT_DELIMITER, LineSeparator.DEFAULT_VALUE, DefaultSqlReader.DEFAULT_TRIM, new Comments(false));
    }


    // excepts fully qualified paths only at this time
    private void processSqlStatements(String sqlFile) throws IOException {
        String filename = DirManipulationUtils.extractFileName(sqlFile);
        String milestone = extractMilestone(sqlFile);
        Map<DatabaseModule, List<String>> locationMap = new HashMap<DatabaseModule, List<String>>();
        SqlReader sqlReader = getSqlReader();
        BufferedReader reader = LocationUtils.getBufferedReader(sqlFile);
        DatabaseModule defaultModule = getDatabaseModule();
        StringBuilder fileReport = new StringBuilder();
        boolean printedHeader = false;

        Map<DatabaseDataType, Map<DatabaseModule,StringBuilder>> statementBuckets = initBuckets();

        fileReport.append("\n" +
                "parsing file: " + filename + "\n" +
                "encoded: {" + InsecureStringEncoder.encode(filename) + "}\n");

        String statement = sqlReader.getSql(reader);
        String firstStatement = statement;

        if (ControlMappingUtils.skipSqlOrganization(firstStatement)) {
            System.out.print(fileReport.toString());

            TypeMapping typeMapping = new TypeMapping();
            ControlMappingValidationStatus status = ControlMappingUtils.setControlDataType(firstStatement, filename, typeMapping);
            if (status == ControlMappingValidationStatus.VALID) {
                System.out.println("file control comment found.  Moving file to " + typeMapping.getType().toString());
                copyFile(sqlFile, milestone, typeMapping.getType(), getDatabaseModule(), filename);
            } else {
                System.out.println("error processing control comment: " + status.toString());
                File newFile = copyFile(sqlFile, milestone, DatabaseDataType.EXCEPTION, getDatabaseModule(), filename);
                FileWriter f = new FileWriter(newFile, true);
                f.write(status.toString());
                f.close();
            }
        } else {
            int i;
            for (i=0; statement != null; statement = sqlReader.getSql(reader)) {
                i++;
                boolean wrongModule = false;
                String cleanStmt = cleanStmt(statement);

                StatementInfo statementInfo = getStatementInfoForStatement(cleanStmt);

                DatabaseDataType dataType = getDataType(statementInfo);
                DatabaseModule module = getModule(statementInfo.getTableNames(), defaultModule);
                statementBuckets.get(dataType).get(module).append(statement + "\n/\n");

            }
            fileReport.append("num of statements in file: " + i + "\n");

            //write the additional files
            List<String> descriptors = splitFile(statementBuckets, milestone, filename);

            if (descriptors.size() > 1) {
                fileReport.append("Type:Module mappings: ");
                for (String descriptor : descriptors) {
                    fileReport.append(descriptor.toString() + " ");
                }
                fileReport.append("\n");
                System.out.print(fileReport.toString());
                printedHeader = true;
            }
        }
        if (printedHeader) {
            System.out.println("");
        }

    }

    private File copyFile(String origFile, String milestone, DatabaseDataType type, DatabaseModule module, String filename) throws IOException {
        File newFile = new File(OUTPUT_DIR_PATH + milestone + "//" + type.toString() + "//" + module.getEndsWith() + "//" + filename);
        File parent = newFile.getParentFile();
        DirManipulationUtils.mkDirCascaded(parent);
        FileUtils.copyFile(new File(origFile), newFile);
        return newFile;

    }


    // returns list of strings that describes which files were created
    private List<String> splitFile(Map<DatabaseDataType, Map<DatabaseModule, StringBuilder>> statementBuckets, String milestone,String filename) {
        List<String> fileDescriptors = new ArrayList<String>();
        Set<Map.Entry<DatabaseDataType,Map<DatabaseModule,StringBuilder>>> typeEntries = statementBuckets.entrySet();
        for (Map.Entry<DatabaseDataType, Map<DatabaseModule, StringBuilder>> typeEntry : typeEntries) {
            Set<Map.Entry<DatabaseModule,StringBuilder>> moduleEntries = typeEntry.getValue().entrySet();
            for (Map.Entry<DatabaseModule, StringBuilder> moduleEntry : moduleEntries) {
                if (moduleEntry.getValue().length() > 0) {
                    writeToFile(milestone, typeEntry.getKey(), moduleEntry.getKey(), filename, moduleEntry.getValue().toString());
                    fileDescriptors.add(typeEntry.getKey().toString() + ":" + moduleEntry.getKey().toString());
                }
            }
        }
        return fileDescriptors;
    }




    public DatabaseModule getModule(List<String> tableNames, DatabaseModule defaultModule) {
        DatabaseModule ret = null;
        Map<DatabaseModule, List> tableMap = new HashMap<DatabaseModule, List>();
        for (String table : tableNames) {
            addTableToModuleMap(tableMap, table);
        }

        Set<Map.Entry<DatabaseModule,List>> entries = tableMap.entrySet();
        if (entries.size() == 1) {
            Map.Entry<DatabaseModule,List> entry = entries.iterator().next();
            if (entry.getKey() != defaultModule) {
                if (entry.getKey() == DatabaseModule.EXCEPTION) {
                    List<String> tables = entry.getValue();
                    //System.out.println("Exception mapping table(s) to module: " + tables);
                    ret = DatabaseModule.EXCEPTION;
                } else {
                    //System.out.println("Stmt moving to: " + entry.getKey().getEndsWith());
                    ret = entry.getKey();
                }
            }
        } else if (entries.size() == 0) {
            //System.out.println("No module references found");
        } else {
            //System.out.println("Stmt contains more than one module reference: " + tableNames.toString());
            ret = DatabaseModule.EXCEPTION;
        }

        if (ret == null) {
            ret = defaultModule;
        }
        return ret;
    }

    private Map<DatabaseDataType, Map<DatabaseModule, StringBuilder>> initBuckets() {
        Map statementBuckets = new HashMap<DatabaseDataType, Map<DatabaseModule,StringBuilder>>();

        for (DatabaseDataType dataTypeIter : DatabaseDataType.values()) {
            Map moduleBuckets = new HashMap<DatabaseModule, StringBuilder>();
            for (DatabaseModule moduleIter : DatabaseModule.values()) {
                moduleBuckets.put(moduleIter, new StringBuilder());
            }
            statementBuckets.put(dataTypeIter, moduleBuckets);
        }
        return statementBuckets;
    }


    private DatabaseModule getDatabaseModule() {
        for (DatabaseModule moduleIter : DatabaseModule.values()) {
            if (module.endsWith(moduleIter.getEndsWith())) {
                return moduleIter;
            }
        }
        return null;
    }

    private void writeToFile(String milestone, DatabaseDataType type, DatabaseModule module, String filename, String content) {

        try {
            File file = new File(OUTPUT_DIR_PATH + milestone + "\\" + type.toString() + "\\" + module.getEndsWith() + "\\" + filename);
            File parent = file.getParentFile();

            if (!parent.exists()) {
                DirManipulationUtils.mkDirCascaded(parent);
            }

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();

            //System.out.println("Done writing to file ..\\" + type.toString() + "\\" + module.getEndsWith() + "\\" + filename);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private String cleanStmt(String statement) {
        String cleanStmt;
        BufferedReader reader = new BufferedReader(new StringReader(statement));
        String stmtLine;
        StringBuilder sb = new StringBuilder();

        try {
            while ((stmtLine = reader.readLine()) != null) {
                if (!stmtLine.startsWith("--")) {
                    sb.append(stmtLine + "\n");
                }
            }
        } catch (IOException e) {
            // should never happen
        }

        cleanStmt = sb.toString();

        cleanStmt = StringUtils.replaceEach(statement, new String[] {"VARCHAR2","varchar2", "Varchar2"}, new String[] {"VARCHAR", "varchar", "Varchar"});
        cleanStmt = StringUtils.replace(cleanStmt,"(+)", "");
        cleanStmt = cleanStmt.replaceAll("\\)\\s*(GROUP\\s*BY|group\\s*by)", ") alias GROUP_BY");
        return cleanStmt;
    }


    private boolean isCMTable(String table) {
        return (table.startsWith("KSLU") || table.startsWith("KSLO"));
    }

    private boolean isEnrTable(String table) {
        return table.startsWith("KSEN");
    }

    private boolean isRiceTable(String table) {
        return table.startsWith("KR");
    }

    private boolean isKSTable(String table) {
        return table.startsWith("KS");
    }


    private String getTableReport(Map<DatabaseModule, List> tableMap) {
        Set<Map.Entry<DatabaseModule,List>> entries = tableMap.entrySet();
        StringBuilder report = new StringBuilder();
        if (entries.size() == 1) {
            Map.Entry<DatabaseModule,List> entry = entries.iterator().next();
            if (entry.getKey() == getDatabaseModule()) {
                List<String> tables = entry.getValue();
                report.append ("statement contains tables that belong in another module. " + tables.toString() + "\n");
            }
        } else if (entries.size() == 0 ) {
            report.append ("No table found in statement");
        } else {
            report.append("ERROR statement contains multiple table modules." + "\n");
            for (Map.Entry<DatabaseModule,List> entry : entries) {
                List<String> tables = tableMap.get(entry.getKey());
                report.append(entry.getKey().getLabel() + " " + tables.toString() + "\n");
            }

        }

        return report.toString();
    }

    private void addTableToModuleMap(Map<DatabaseModule, List> tableReport, String table) {
        DatabaseModule category = getTableModule(table);


        List contents = tableReport.get(category);
        if (contents == null) {
            contents = new ArrayList<String>();
        }
        contents.add(table);
        tableReport.put(category,contents);
    }

    private DatabaseModule getTableModule(String table) {
        if (isRiceTable(table)) {
            return DatabaseModule.RICE;
        } else if (isCMTable(table)) {
            return DatabaseModule.KSCM;
        } else if (isEnrTable(table)) {
            return DatabaseModule.KSENR;
        } else if (isKSTable(table)) {
            return DatabaseModule.KSCORE;
        } else {
            return DatabaseModule.EXCEPTION;
        }


    }

    private DatabaseDataType getDataType(StatementInfo statementInfo) {
        if (statementInfo.getStatementType() == StatementType.DDL) {
            return DatabaseDataType.STRUCTURE;
        } else {
            List<String> tableNames = statementInfo.getTableNames();
            Map<DatabaseDataType, Boolean> tableMap = new HashMap<DatabaseDataType, Boolean>();
            for (String table : tableNames) {
                addTableToTypeMap(tableMap, table);
            }
            if (tableMap.get(DatabaseDataType.MANUAL) != null) {
                return DatabaseDataType.MANUAL;
            } else if (tableMap.get(DatabaseDataType.REFERENCE) != null) {
                return DatabaseDataType.REFERENCE;
            } else if (tableMap.get(DatabaseDataType.BOOTSTRAP) != null) {
                return DatabaseDataType.BOOTSTRAP;
            }
            return DatabaseDataType.EXCEPTION;
        }
    }

    private void addTableToTypeMap(Map<DatabaseDataType, Boolean> tableMap, String table) {
        DatabaseDataType category = getTableDataType(table);
        tableMap.put(category,true);
    }

    private DatabaseDataType getTableDataType(String table) {
        Set<Map.Entry<DatabaseDataType,Set<String>>> entries = dataTypeTableSets.entrySet();
        for (Map.Entry<DatabaseDataType,Set<String>> entry : entries) {
            if (entry.getValue().contains(table)) {
                return entry.getKey();
            }
        }
        return DatabaseDataType.EXCEPTION;
    }


    public StatementInfo getStatementInfoForStatement(String statement){
        StatementType statementType = null;
        List<String> tableNames = null;
        SQLParser parser = new SQLParser();
        SqlParserNodeVisitor nodeVisitor = new SqlParserNodeVisitor();
        try {
            StatementNode stmt = parser.parseStatement(statement);
            nodeVisitor.traverse(stmt);
            tableNames = nodeVisitor.getTableNames();
            statementType = nodeVisitor.getStatementType();
            // sql parser had an issue so run attempt to handle some common scenarios.  the groups are based on the regex
        } catch (StandardException e) {
            tableNames = new ArrayList<String>();
            Matcher matcher = CNSTRT_RENAME_PATTERN.matcher(statement);
            if (matcher.find()) {
                statementType = StatementType.DDL;
                tableNames.add(matcher.group(3));
                tableNames.add(matcher.group(6));
                tableNames.add(matcher.group(8));
            } else {
                matcher = NDX_RENAME_PATTERN.matcher(statement);
                if (matcher.find()){
                    statementType = StatementType.DDL;
                    tableNames.add(matcher.group(3));
                    tableNames.add(matcher.group(6));
                } else {
                    matcher = CREATE_SEQ_PATTERN.matcher(statement);
                    if (matcher.find()) {
                        statementType = StatementType.DDL;
                        tableNames.add(matcher.group(3));
                    } else {
                        matcher = DROP_SEQ_PATTERN.matcher(statement);
                        if (matcher.find()) {
                            statementType = StatementType.DDL;
                            tableNames.add(matcher.group(3));
                        } else {
                            matcher = CREATE_TABLE_PATTERN.matcher(statement);
                            if (matcher.find()) {
                                statementType = StatementType.DDL;
                                tableNames.add(matcher.group(3));
                            } else {
                                matcher = COMPLEX_DROP_TABLE_PATTERN.matcher(statement);
                                if (matcher.find()) {
                                    statementType = StatementType.DDL;
                                    tableNames.add(matcher.group(1));
                                }
                            }
                        }
                    }
                }
            }
            if (tableNames.size() > 0) {
                this.unparsableStmts.add(statement);
                //System.out.println("found non-ansi statement: " + statement + "\nusing objects:" + tableNames );
            } else {
                //System.out.println("Error parsing statement: " + statement);
                this.unparsableStmts.add(statement);
                tableNames.add("EXCEPTION");
                //System.out.println(e);  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        StatementInfo statementInfo = new StatementInfo(tableNames, statementType);
        return statementInfo;
    }


    public void removeEmptyOutputDirs() {
        File root = new File(OUTPUT_DIR_PATH);
        DirManipulationUtils.delEmptyDirs(root);
    }


}
