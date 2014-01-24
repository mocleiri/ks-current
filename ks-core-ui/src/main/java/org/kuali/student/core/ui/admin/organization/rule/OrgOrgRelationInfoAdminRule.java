package org.kuali.student.core.ui.admin.organization.rule;

import org.kuali.rice.krad.maintenance.MaintenanceDocument;
import org.kuali.rice.krad.rules.MaintenanceDocumentRuleBase;

public class OrgOrgRelationInfoAdminRule extends MaintenanceDocumentRuleBase {
    
    @Override
    protected boolean processGlobalSaveDocumentBusinessRules(MaintenanceDocument document) {
        return true;
    }

}
