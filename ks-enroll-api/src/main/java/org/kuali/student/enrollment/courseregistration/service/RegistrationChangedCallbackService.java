/*
 * Copyright 2011 The Kuali Foundation 
 *
 * Licensed under the Educational Community License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.kuali.student.enrollment.courseregistration.service;

import java.util.List;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * A callback service that must be implemented by a consumer and is called when a change has been made to a registration request
 * and it's item.
 *
 * Note: the same callback object may be registered to listen for multiple changes or the consumer may choose to create separate
 * callback objects to handle each.
 *
 * @author Kuali Student Team (norm)
 */
@WebService(name = "RegistrationChangedCallbackService", targetNamespace = RegistrationChangedCallbackNamespaceConstants.NAMESPACE)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface RegistrationChangedCallbackService {

    /**
     * Callback for when a student's registration has changed
     *
     * Multiple changes may be coalesced together for efficiency into lists
     *
     * @param personIds ids of the people who's registrations were changed
     * @param termIds paired ids of the term in which the person's registrations were changed
     * @return nothing
     */
    public void changedRegistration(@WebParam(name = "personIds") List<String> personIds, 
            @WebParam(name = "termIds") List<String> termIds);


}
