package com.sigmasys.kuali.ksa.krad.controller.rules;

import com.sigmasys.kuali.ksa.krad.controller.GenericSearchController;
import com.sigmasys.kuali.ksa.krad.form.rules.AbstractRuleViewModel;
import com.sigmasys.kuali.ksa.service.brm.BrmPersistenceService;
import com.sigmasys.kuali.ksa.service.brm.BrmService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;


/**
 * A generic rule controller for managing KSA business rules.
 *
 * @author Michael Ivanov
 */
public abstract class AbstractRuleController extends GenericSearchController {

    private static final Log logger = LogFactory.getLog(AbstractRuleController.class);


    @Autowired
    protected BrmService brmService;

    @Autowired
    protected BrmPersistenceService brmPersistenceService;


}
