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
package org.kuali.student.contract.model.validation;

import org.kuali.student.contract.exception.DictionaryException;

/**
 * Exception thrown when encounter a problem with the spreadsheet model while
 * generating the dictionary
 * @author nwright
 */
public class DictionaryValidationException extends DictionaryException {

    public DictionaryValidationException(Throwable cause) {
        super(cause);
    }

    public DictionaryValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DictionaryValidationException(String message) {
        super(message);
    }

    public DictionaryValidationException() {
    }
}
