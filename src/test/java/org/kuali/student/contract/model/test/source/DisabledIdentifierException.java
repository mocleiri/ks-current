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

import javax.xml.ws.WebFault;

@WebFault(faultBean="org.kuali.student.r2.common.exceptions.jaxws.DisabledIdentifierExceptionBean")
public class DisabledIdentifierException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;


	public DisabledIdentifierException() {
        super();
    }

    public DisabledIdentifierException(String message) {
        super(message);
    }

}
