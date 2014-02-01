package com.sigmasys.kuali.ksa.util;


import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.service.impl.SimpleAccountVisitor;

/**
 * This class represents a number of different methods on Account class and its subclasses.
 *
 * @author Michael Ivanov
 */
public class AccountUtils {


    private AccountUtils() {
    }

    @SuppressWarnings("unchecked")
    public static <T extends Account> T cast(Account account, Class<T> subClass) {

        SimpleAccountVisitor visitor = SimpleAccountVisitor.getInstance();
        account.accept(visitor);

        Account originalAccount = null;

        if (DirectChargeAccount.class.equals(subClass)) {
            originalAccount = visitor.getDirectChargeAccount();
        } else if (ThirdPartyAccount.class.equals(subClass)) {
            originalAccount = visitor.getThirdPartyAccount();
        } else if (DelegateAccount.class.equals(subClass)) {
            originalAccount = visitor.getDelegateAccount();
        } else if (CollectionAgencyAccount.class.equals(subClass)) {
            originalAccount = visitor.getCollectionAgencyAccount();
        }

        return (T) originalAccount;
    }


}
