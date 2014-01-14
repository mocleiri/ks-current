package com.sigmasys.kuali.ksa.krad.helper;

import com.sigmasys.kuali.ksa.krad.model.AtpModel;
import org.kuali.rice.krad.uif.service.impl.ViewHelperServiceImpl;

import java.util.Arrays;
import java.util.List;

/**
 * Assists in looking up ATP codes.
 *
 * Created with IntelliJ IDEA.
 * User: Sergey
 * Date: 1/14/14
 * Time: 2:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class RateRolloverHelper extends ViewHelperServiceImpl {


    /**
     * Returns suggested values for the "Rollover From" ATP lookup field.
     *
     * @param suggest Partial ATP id.
     * @return List of suggested values.
     */
    public List<AtpModel> getAtpRolloverFromForSuggest(String suggest) {
        AtpModel atp = new AtpModel();
        atp.setAtpId("test");
        return Arrays.asList(atp);
    }

    /**
     * Returns suggested values for the "Rollover To" ATP lookup field.
     *
     * @param suggest Partial ATP id.
     * @return List of suggested values.
     */
    public List<AtpModel> getAtpRolloverToForSuggest(String suggest) {
        AtpModel atp = new AtpModel();
        atp.setAtpId("test");
        return Arrays.asList(atp);
    }

}
