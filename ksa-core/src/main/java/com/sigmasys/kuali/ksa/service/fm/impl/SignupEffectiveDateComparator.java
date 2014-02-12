package com.sigmasys.kuali.ksa.service.fm.impl;

import com.sigmasys.kuali.ksa.model.fm.FeeManagementSignup;

import java.util.Comparator;
import java.util.Date;

/**
 * A comparator for FeeManagementSignup class that is used to sort a collection of signups by effective date.
 *
 * @author Michael Ivanov
 */
public class SignupEffectiveDateComparator implements Comparator<FeeManagementSignup> {

    private static final SignupEffectiveDateComparator comparator = new SignupEffectiveDateComparator();

    public static SignupEffectiveDateComparator getInstance() {
        return comparator;
    }

    @Override
    public int compare(FeeManagementSignup signup1, FeeManagementSignup signup2) {

        if (signup1 == null && signup2 == null) {
            return 0;
        } else if (signup1 != null && signup2 == null) {
            return 1;
        } else if (signup1 == null) {
            return -1;
        }

        Date effectiveDate1 = signup1.getEffectiveDate();
        Date effectiveDate2 = signup2.getEffectiveDate();

        if (effectiveDate1 == null && effectiveDate2 == null) {
            return 0;
        } else if (effectiveDate1 != null && effectiveDate2 == null) {
            return 1;
        } else if (effectiveDate1 == null) {
            return -1;
        }

        return effectiveDate1.compareTo(effectiveDate2);
    }

}
