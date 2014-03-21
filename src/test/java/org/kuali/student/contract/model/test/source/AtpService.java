/**
 * Copyright 2004-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.student.contract.model.test.source;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


/**
 * ATp service
 * 
 * @version 1.0 (Dev)
 *
 * @Author Kamal
 * @Since Tue Mar 01 15:53:51 PST 2011
 * @See <a href="https://wiki.kuali.org/display/KULSTU/LUI+Person+Relation+Service">LuiPersonRelationService</>
 */
@WebService(name = "AtpService", targetNamespace = "http://student.kuali.org/wsdl/atp")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface AtpService extends TypeService, StateService {

    /**
     * Retrieves the atp
     *
     * @param atpKey Identifier for the ATP Person Relation
     * @param context             Context information containing the principalId and locale information about the caller of service operation
     * @return atp information
     * @throws DoesNotExistException     luiPersonRelationId not found
     * @throws InvalidParameterException invalid luiPersonRelationId
     * @throws MissingParameterException missing luiPersonRelationId
     * @throws OperationFailedException  unable to complete request
     * @throws PermissionDeniedException authorization failure
     * @impl this is an implementation note
     * @impl this is another
     */
    public AtpInfo getAtp(@WebParam(name = "atpKey") String atpKey, @WebParam(name = "context") ContextInfo context) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException;
}
