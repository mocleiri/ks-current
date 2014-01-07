package com.sigmasys.kuali.ksa.util;

import com.sigmasys.kuali.ksa.krad.model.InformationModel;
import com.sigmasys.kuali.ksa.model.Information;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InformationUtils {

    public static List<Information> orderByEffectiveDate(List<Information> informations, boolean ascending) {
        return orderInformations(informations, new EffectiveDateComparator(), ascending);
    }

    private static List<Information> orderInformations(List<Information> informations,
                                                       Comparator<Information> comparator,
                                                       boolean ascending) {
        if (!ascending) {
            comparator = Collections.reverseOrder(comparator);
        }

        Collections.sort(informations, comparator);

        return informations;

    }

    public static List<InformationModel> orderModelsByEffectiveDate(List<InformationModel> informations, boolean ascending) {
        return orderInformationModels(informations, new EffectiveDateModelComparator(), ascending);
    }

    private static List<InformationModel> orderInformationModels(List<InformationModel> informations,
                                                       Comparator<InformationModel> comparator,
                                                       boolean ascending) {
        if (!ascending) {
            comparator = Collections.reverseOrder(comparator);
        }

        Collections.sort(informations, comparator);

        return informations;

    }




    // ------------- Private classes for internal usage -------------------------------------
    private static class EffectiveDateModelComparator implements Comparator<InformationModel> {
        @Override
        public int compare(InformationModel i1, InformationModel i2) {
            return i1.getEffectiveDate().compareTo(i2.getEffectiveDate());
        }
    }




    // ------------- Private classes for internal usage -------------------------------------
    private static class EffectiveDateComparator implements Comparator<Information> {
        @Override
        public int compare(Information i1, Information i2) {
            return i1.getEffectiveDate().compareTo(i2.getEffectiveDate());
        }
    }



}
