package com.sigmasys.kuali.ksa.service;


import com.sigmasys.kuali.ksa.annotation.Url;
import com.sigmasys.kuali.ksa.model.Constants;

import javax.jws.WebService;

/**
 * Transaction Import Service.
 *
 * @author Michael Ivanov
 */
@Url(TransactionImportService.SERVICE_URL)
@WebService(serviceName = TransactionImportService.SERVICE_NAME, portName = TransactionImportService.PORT_NAME,
        targetNamespace = Constants.WS_NAMESPACE)
public interface TransactionImportService {


    String SERVICE_URL = "transactionImport.webservice";
    String SERVICE_NAME = "TransactionImportService";
    String PORT_NAME = SERVICE_NAME + "Port";

    /**
     * This is the service method exposed to up load and process transactions
     *
     * @param xml XML content
     * @return XML response
     */
    String importTransactions(String xml);


}
