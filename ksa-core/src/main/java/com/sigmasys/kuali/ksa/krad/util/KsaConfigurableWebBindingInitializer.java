package com.sigmasys.kuali.ksa.krad.util;

import org.kuali.rice.krad.web.bind.UifConfigurableWebBindingInitializer;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.WebRequest;

import java.math.BigDecimal;

/**
 * User: tbornholtz
 * Date: 2/20/13
 * Time: 6:29 PM
 */
public class KsaConfigurableWebBindingInitializer extends UifConfigurableWebBindingInitializer {

    @Override
    public void initBinder(WebDataBinder binder, WebRequest request) {
        super.initBinder(binder, request);
        binder.registerCustomEditor(BigDecimal.class, new CurrencyEditor());
    }

}
