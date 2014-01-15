package com.sigmasys.kuali.ksa.krad.helper;

import com.sigmasys.kuali.ksa.krad.model.AtpModel;
import com.sigmasys.kuali.ksa.service.atp.AtpService;
import com.sigmasys.kuali.ksa.util.ContextUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.api.criteria.LikePredicate;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.PredicateFactory;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.krad.uif.service.impl.ViewHelperServiceImpl;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.core.atp.dto.AtpInfo;

import java.util.ArrayList;
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

    private static final Log logger = LogFactory.getLog(RateRolloverHelper.class);

    /**
     * AtpService to perform lookup.
     */
    private AtpService atpService;

    /**
     * Returns suggested values for the "Rollover From" ATP lookup field.
     *
     * @param suggest Partial ATP id.
     * @return List of suggested values.
     */
    public List<AtpModel> getAtpRolloverFromForSuggest(String suggest) {
        return searchForAtps(suggest);
    }

    /**
     * Returns suggested values for the "Rollover To" ATP lookup field.
     *
     * @param suggest Partial ATP id.
     * @return List of suggested values.
     */
    public List<AtpModel> getAtpRolloverToForSuggest(String suggest) {
        return searchForAtps(suggest);
    }

    /**
     * Performs ATP lookup by ATP name.
     *
     * @param suggest   Suggestion string.
     * @return List of matching ATPs by name.
     */
    private List<AtpModel> searchForAtps(String suggest) {

        List<AtpModel> result = new ArrayList<AtpModel>();

        // Perform search for ATP:
        try {
            ContextInfo contextInfo = getAtpService().getAtpContextInfo();
            Predicate like = PredicateFactory.like("name", String.format("*%s*", suggest));
            QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();

            builder.setPredicates(like);
            builder.setMaxResults(10);
            builder.setStartAtIndex(0);

            QueryByCriteria query = builder.build();
            List<AtpInfo> atpInfoList = getAtpService().searchForAtps(query, contextInfo);

            for (AtpInfo atpInfo : atpInfoList) {

                // Create a new AtpModel and add to the result:
                AtpModel atp = new AtpModel();

                atp.setAtpId(atpInfo.getName());
                result.add(atp);
            }

            return result;
        } catch (Exception e) {
            // Just log an error:
            logger.error("Cannot search for ATPs for suggest");
        }

        return result;
    }

    /**
     * Returns the local instance of the AtpService. Creates one if needed.
     *
     * @return The local instance of the AtpService.
     */
    private AtpService getAtpService() {
        if (atpService == null) {
            atpService = ContextUtils.getBean(AtpService.class);
        }

        return atpService;
    }

}
