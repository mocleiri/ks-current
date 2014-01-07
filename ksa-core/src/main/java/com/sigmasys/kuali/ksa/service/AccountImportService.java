package com.sigmasys.kuali.ksa.service;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.sigmasys.kuali.ksa.annotation.Url;
import com.sigmasys.kuali.ksa.model.Constants;
import com.sigmasys.kuali.ksa.jaxb.StudentProfile;

/**
 * Represents a facility to import student profiles based on a StudentProfile entity data.
 * 
 * The logic of importing of student profiles is explained by Paul H. in the
 * "Process Diagrams" design document.
 * 
 * @author Sergey G.
 *
 */
@Url(AccountImportService.SERVICE_URL)
@WebService(serviceName = AccountImportService.SERVICE_NAME, portName = AccountImportService.PORT_NAME, targetNamespace = Constants.WS_NAMESPACE)
public interface AccountImportService {

    String SERVICE_URL = "accountImport.webservice";
    String SERVICE_NAME = "AccountImportService";
    String PORT_NAME = SERVICE_NAME + "Port";
    
	/**
	 * Performs import of a student profile. 
	 * Refer to the "Process Diagrams" design document for a detailed depiction of the process and logic.
	 * 
	 * @param studentProfile A student's profile object. 
	 */
    @WebMethod(exclude=true)
	void importStudentProfile(StudentProfile studentProfile);
	
	/**
	 * Performs import of a student profile from the specified XML content of the profile.
	 * 
	 * @param xml XML content of a Student profile.
	 */
	void importStudentProfile(String xml);
}
