package com.sigmasys.kuali.ksa.krad.helper;

import com.sigmasys.kuali.ksa.model.Account;
import com.sigmasys.kuali.ksa.model.CreditType;
import com.sigmasys.kuali.ksa.model.DebitType;
import com.sigmasys.kuali.ksa.model.Tag;
import com.sigmasys.kuali.ksa.service.AccountService;
import com.sigmasys.kuali.ksa.service.AuditableEntityService;
import com.sigmasys.kuali.ksa.service.TransactionService;
import com.sigmasys.kuali.ksa.util.ContextUtils;
import org.kuali.rice.krad.uif.service.impl.ViewHelperServiceImpl;

import java.util.List;


public class SettingsHelper extends ViewHelperServiceImpl {

    private AccountService accountService;
    private AuditableEntityService auditableEntityService;
    private TransactionService transactionService;

    public List<Account> getAccountsForSuggest(String suggest) {
        return getAccountService().getAccountsByNamePattern(suggest);
    }

    public List<Tag> getTagsForSuggest(String suggest) {

        List<Tag> tags = getAuditableEntityService().getAuditableEntitiesByNamePattern(suggest, Tag.class);

        for (Tag tag : tags) {
            tag.setDescription(tag.isAdministrative() ? "<b>" + tag.getCode() + " (A)</b>" : tag.getCode());
        }

        return tags;
    }

    public List<CreditType> getPaymentsForSuggest(String suggest) {
        return getTransactionService().getTransactionTypesByNamePattern(suggest, CreditType.class);
    }

    public List<DebitType> getChargesForSuggest(String suggest) {
        return getTransactionService().getTransactionTypesByNamePattern(suggest, DebitType.class);
    }


    private AccountService getAccountService() {
        if (accountService == null) {
            accountService = ContextUtils.getBean(AccountService.class);
        }
        return accountService;
    }

    private AuditableEntityService getAuditableEntityService() {
        if (auditableEntityService == null) {
            auditableEntityService = ContextUtils.getBean(AuditableEntityService.class);
        }
        return auditableEntityService;
    }

    private TransactionService getTransactionService() {
        if (transactionService == null) {
            transactionService = ContextUtils.getBean(TransactionService.class);
        }
        return transactionService;
    }

}
