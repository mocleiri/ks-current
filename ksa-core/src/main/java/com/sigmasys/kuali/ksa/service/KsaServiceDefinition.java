package com.sigmasys.kuali.ksa.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.ksb.api.bus.support.SoapServiceDefinition;

/**
 * KSA service definition. It extends KSB's SoapServiceDefinition and sets
 * the correct endpoint service URL
 *
 * @author Michael Ivanov
 *         Date: 5/14/12
 */
public class KsaServiceDefinition extends SoapServiceDefinition {

    private static final Log logger = LogFactory.getLog(KsaServiceDefinition.class);

    @Override
    public void validate() {
        super.validate();
        logger.info("Registered Endpoint URL = " + getEndpointUrl());
    }
}
