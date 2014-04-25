/*
 * Copyright 2006-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.student.enrollment.class2.examoffering.batch;

import java.util.ArrayList;
import java.util.Date;

import org.kuali.student.common.util.security.ContextUtils;
import org.kuali.student.enrollment.class2.examoffering.service.facade.ExamOfferingResult;
import org.kuali.student.enrollment.class2.examoffering.service.facade.ExamOfferingServiceFacade;
import org.kuali.student.enrollment.courseoffering.dto.CourseOfferingInfo;
import org.kuali.student.enrollment.courseoffering.infc.CourseOffering;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.mail.SimpleMailMessage;

/**
 * @author Dan Garrette
 * @author Dave Syer
 * 
 * @since 2.1
 */
public class ExamOfferingGenerationProcessor implements
        ItemProcessor<CourseOfferingInfo, ExamOfferingResult> {

    private ExamOfferingServiceFacade examOfferingServiceFacade;

    /**
     * @see org.springframework.batch.item.ItemProcessor#process(Object)
     */
    public ExamOfferingResult process(CourseOfferingInfo courseOfferingInfo) throws Exception {
        return examOfferingServiceFacade.generateFinalExamOffering(courseOfferingInfo, new ArrayList<String>(),
                ContextUtils.createDefaultContextInfo());
    }

    public ExamOfferingServiceFacade getExamOfferingServiceFacade() {
        return examOfferingServiceFacade;
    }

    public void setExamOfferingServiceFacade(ExamOfferingServiceFacade examOfferingServiceFacade) {
        this.examOfferingServiceFacade = examOfferingServiceFacade;
    }
}
