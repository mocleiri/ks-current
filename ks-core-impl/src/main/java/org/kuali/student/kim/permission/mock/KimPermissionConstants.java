/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kuali.student.kim.permission.mock;

/**
 *
 * @author nwright
 */
public class KimPermissionConstants {

    /**
     * Kim Type IDs
     * These are used to control the comparison of qualifiers
     * The default one does a simple match, i.e. no hierarchy checks
     */
    public static final String DEFAULT_KIM_TYPE_ID = "0";
    public static final String KRAD_VIEW_KIM_TYPE_ID = "68";

    /**
     * Namespaces
     */
    public static final String KS_ENROLLMENT_NAMESPACE = "KS-ENRL";
    public static final String KS_SYS_NAMESPACE = "KS-SYS";
    public static final String KS_LUM_NAMESPACE = "KS-LUM";
    public static final String KUALI_NAMESPACE = "KUALI";
    public static final String KR_WORKFLOW_NAMESPACE = "KR-WKFLW";
    public static final String KR_IDM_NAMESPACE = "KR-IDM";

    /**
     * Attribute definition
     */
    public static final String VIEW_ID_ATTR_DEFINITION = "viewId";

    /**
     * org permission templates
     */
    public static final String CAN_INVOKE_SERVICE_METHOD_TEMPLATE_NAME = "Can Invoke Service Method";
    /**
     * org permissions
     */
    public static final String CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_DATA_PERMISSION = "Can Invoke Service Method Read Organization Data";
    public static final String CAN_INVOKE_SERVICE_METHOD_UPDATE_ORGANIZATION_DATA_PERMISSION = "Can Invoke Service Method Update Organization Data";
    public static final String CAN_INVOKE_SERVICE_METHOD_DELETE_ORGANIZATION_DATA_PERMISSION = "Can Invoke Service Method Delete Organization Data";
    public static final String CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_HIERARCHY_DATA_PERMISSION = "Can Invoke Service Method Read Organization Hierarchy Data";
    public static final String CAN_INVOKE_SERVICE_METHOD_UPDATE_ORGANIZATION_HIERARCHY_DATA_PERMISSION = "Can Invoke Service Method Update Organization Hierarchy Data";
    public static final String CAN_INVOKE_SERVICE_METHOD_DELETE_ORGANIZATION_HIERARCHY_DATA_PERMISSION = "Can Invoke Service Method Delete Organization Hierarchy Data";
    public static final String CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_PERSON_RELATION_DATA_PERMISSION = "Can Invoke Service Method Read Organization Person Relation Data";
    public static final String CAN_INVOKE_SERVICE_METHOD_UPDATE_ORGANIZATION_PERSON_RELATION_DATA_PERMISSION = "Can Invoke Service Method Update Organization Person Relation Data";
    public static final String CAN_INVOKE_SERVICE_METHOD_DELETE_ORGANIZATION_PERSON_RELATION_DATA_PERMISSION = "Can Invoke Service Method Delete Organization Person Relation Data";
    public static final String CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_POSITION_RESTRICTION_DATA_PERMISSION = "Can Invoke Service Method Read Organization Position Restriction Data";
    public static final String CAN_INVOKE_SERVICE_METHOD_UPDATE_ORGANIZATION_POSITION_RESTRICTION_DATA_PERMISSION = "Can Invoke Service Method Update Organization Position Restriction Data";
    public static final String CAN_INVOKE_SERVICE_METHOD_DELETE_ORGANIZATION_POSITION_RESTRICTION_DATA_PERMISSION = "Can Invoke Service Method Delete Organization Position Restriction Data";
    
    /**
     * org role constants
     */
    public static final String KS_ORG_MAINTENANCE_ROLE_NAME = "KS Org Maintenance";
    public static final String KS_ORG_VIEW_ROLE_NAME = "KS Org View";
}
