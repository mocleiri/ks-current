/**
 * Copyright 2014 The Kuali Foundation Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * Created by pauldanielrichardson on 6/9/14
 */
package org.kuali.student.enrollment.registration.client.service.impl.util;

import org.codehaus.jackson.map.ObjectMapper;
import org.kuali.student.enrollment.registration.client.service.dto.ConflictCourseResult;
import org.kuali.student.enrollment.registration.client.service.dto.RegistrationValidationConflictCourseResult;
import org.kuali.student.enrollment.registration.client.service.dto.RegistrationValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * This class contains utility methods for passing complex messaging out of ValidationResultInfo objects
 *
 * @author Kuali Student Team
 */
public class RegistrationValidationResultsUtil {
    public static final Logger LOGGER = LoggerFactory.getLogger(RegistrationValidationResultsUtil.class);

    private RegistrationValidationResultsUtil() {
    }

    public static String marshallSimpleMessage(String messageKey) {
        return marshallResult(new RegistrationValidationResult(messageKey));
    }

    public static String marshallConflictCourseMessage(String messageKey, List<ConflictCourseResult> conflictingCourses) {
        return marshallResult(new RegistrationValidationConflictCourseResult(messageKey, conflictingCourses));
    }

    public static String marshallResult(Object result) {
        ObjectMapper mapper=new ObjectMapper();
        String json= null;
        try {
            json = mapper.writeValueAsString(result);
        } catch (IOException ex) {
            LOGGER.error("Unable to marshall result object", ex);
        }
        return json;
    }
}
