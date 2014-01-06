/**
 * Copyright 2012 The Kuali Foundation Licensed under the
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
 * Created by David Yin on 5/11/12
 */
package org.kuali.student.enrollment.class2.courseoffering.service.impl;

import org.kuali.rice.krad.inquiry.InquirableImpl;
import org.kuali.student.enrollment.class2.courseoffering.util.CourseOfferingManagementUtil;
import org.kuali.student.enrollment.courseofferingset.dto.SocRolloverResultItemInfo;
import org.kuali.student.r2.common.util.ContextUtils;

import javax.xml.namespace.QName;
import java.util.Map;

/**
 * This class provides a Inquirable implementation for SocRolloverResultItems
 *
 * @author Kuali Student Team
 */
public class SocRolloverResultItemInfoInquirableImpl extends InquirableImpl {

    private static final long serialVersionUID = 1L;
    public final static String ID = "id";

    @Override
    public SocRolloverResultItemInfo retrieveDataObject(Map<String, String> parameters) {
        String id = parameters.get(ID);
        try {
            SocRolloverResultItemInfo socRolloverResultItemInfo = CourseOfferingManagementUtil.getCourseOfferingSetService().getSocRolloverResultItem(id, ContextUtils.createDefaultContextInfo());
            return socRolloverResultItemInfo;
        } catch (Exception e) {
            throw new RuntimeException("socRolloverResultItemInfo inquiry has failed. ", e);
        }
    }
}
