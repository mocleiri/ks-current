<%--
 Copyright 2007-2009 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/rice-portal/jsp/sys/riceTldHeader.jsp" %>

<channel:portalChannelTop channelTitle="MyPlan Applications"/>
<div class="body">
    <strong>MyPlan Views</strong>
    <ul class="chan">
        <li><a href="${ConfigProperties.application.url}/myplan/plan?methodToCall=start&viewId=PlannedCourses-FormView"
               target="_new">Your Plan</a></li>
        <li>
            <a href="${ConfigProperties.application.url}/myplan/lookup?methodToCall=search&viewId=SavedCoursesDetail-LookupView"
               target="_new">Bookmarked Courses</a></li>
        <li><a href="${ConfigProperties.application.url}/myplan/course?methodToCall=start&viewId=CourseSearch-FormView"
               target="_new">Course Search</a></li>
        <li><a href="${ConfigProperties.application.url}/myplan/audit?methodToCall=audit&viewId=PlanAudit-FormView"
               target="_new">Plan Audit</a></li>
        <li><a href="${ConfigProperties.application.url}/myplan/audit?methodToCall=audit&viewId=DegreeAudit-FormView"
               target="_new">Degree Audit</a></li>
        <li>
            <a href="${ConfigProperties.application.url}/myplan/lookup?methodToCall=search&viewId=MessagesDetail-LookupView"
               target="_new">Messages</a></li>
        <li><a href="${ConfigProperties.application.url}/myplan/advise/${UserSession.principalId}" target="_new">Adviser
            Access</a></li>
    </ul>
    <strong>MyPlan Widgets</strong>
    <ul class="chan">
        <li>
            <a href="${ConfigProperties.application.url}/myplan/lookup?methodToCall=search&viewId=SavedCoursesSummary-LookupView"
               target="_new">Bookmarked Courses Summary List</a></li>
        <li>
            <a href="${ConfigProperties.application.url}/myplan/lookup?methodToCall=search&viewId=DegreeAuditsSummary-LookupView"
               target="_new">Degree Audit Summary List</a></li>
        <li>
            <a href="${ConfigProperties.application.url}/myplan/lookup?methodToCall=search&viewId=PlanAuditsSummary-LookupView"
               target="_new">Plan Audit Summary List</a></li>
    </ul>
</div>
<channel:portalChannelBottom/>
