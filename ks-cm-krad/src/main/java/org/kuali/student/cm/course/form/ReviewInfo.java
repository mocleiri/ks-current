/**
 * Copyright 2005-2013 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 1.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kuali.student.cm.course.form;

import java.util.LinkedList;
import java.util.List;

/**
 * Marker interface for having arbitrary course data in a collection displayed in stacks.
 *
 * @author OpenCollab/rSmart KRAD CM Conversion Alliance!
 */
public class ReviewInfo implements java.io.Serializable {

    private CourseInfoWrapper courseInfo;
    private GovernanceInfoWrapper governanceInfo;
    private CourseLogisticsInfoWrapper courseLogisticsInfo;
    private LearningObjectivesInfoWrapper learningObjectivesInfo;
    private CourseRequisitesInfoWrapper courseRequisitesInfo;
    private ActiveDatesInfoWrapper activeDatesInfo;
    private FinancialsInfoWrapper financialsInfo;
    private SupportingDocumentsInfoWrapper supportingDocumentsInfo;

    public CourseInfoWrapper getCourseInfo() {
        if (this.courseInfo == null) {
            courseInfo = new CourseInfoWrapper();
        }
        return courseInfo;
    }

    public GovernanceInfoWrapper getGovernanceInfo() {
        if (this.governanceInfo == null) {
            governanceInfo = new GovernanceInfoWrapper();
        }
        return governanceInfo;
    }

    public CourseLogisticsInfoWrapper getCourseLogisticsInfo() {
        if (this.courseLogisticsInfo == null) {
            courseLogisticsInfo = new CourseLogisticsInfoWrapper();
        }
        return courseLogisticsInfo;
    }


    public LearningObjectivesInfoWrapper getLearningObjectivesInfo() {
        if (this.learningObjectivesInfo == null) {
            learningObjectivesInfo = new LearningObjectivesInfoWrapper();
        }
        return learningObjectivesInfo;
    }

    public ActiveDatesInfoWrapper getActiveDatesInfo() {
        if (this.activeDatesInfo == null) {
            activeDatesInfo = new ActiveDatesInfoWrapper();
        }
        return activeDatesInfo;
    }

    public CourseRequisitesInfoWrapper getCourseRequisitesInfo() {
        if (this.courseRequisitesInfo == null) {
            courseRequisitesInfo = new CourseRequisitesInfoWrapper();
        }
        return courseRequisitesInfo;
    
    }

    public FinancialsInfoWrapper getFinancialsInfo() {
        if (this.financialsInfo == null) {
            financialsInfo = new FinancialsInfoWrapper();
        }
        return financialsInfo;
    
    }

    public SupportingDocumentsInfoWrapper getSupportingDocumentsInfo() {
        if (this.supportingDocumentsInfo == null) {
            supportingDocumentsInfo = new SupportingDocumentsInfoWrapper();
        }
        return supportingDocumentsInfo;
    
    }

    public class CourseInfoWrapper implements java.io.Serializable {
        private String proposalName;
        private String courseTitle;
        private String transcriptTitle;
        private String subjectArea;
        private String courseNumberSuffix;
        private List<String> instructors;

        private String description;
        private String rationale;
        private List<String> crossListings;
        private List<String> variations;

        public String getProposalName() {
            return proposalName;
        }

        public void setProposalName(final String proposalName) {
            this.proposalName = proposalName;
        }

        public String getCourseTitle() {
            return courseTitle;
        }

        public void setCourseTitle(final String courseTitle) {
            this.courseTitle = courseTitle;
        }

        public String getTranscriptTitle() {
            return transcriptTitle;
        }

        public void setTranscriptTitle(final String transcriptTitle) {
            this.transcriptTitle = transcriptTitle;
        }

        public String getSubjectArea() {
            return subjectArea;
        }

        public void setSubjectArea(final String subjectArea) {
            this.subjectArea = subjectArea;
        }

        public String getCourseNumberSuffix() {
            return courseNumberSuffix;
        }

        public void setCourseNumberSuffix(final String courseNumberSuffix) {
            this.courseNumberSuffix = courseNumberSuffix;
        }

        public List<String> getInstructors() {
            if (this.instructors == null) {
                instructors = new LinkedList<String>();
            }
            return instructors;
        }

        public List<String> getCrossListings() {
            if (this.crossListings == null) {
                crossListings = new LinkedList<String>();
            }
            return crossListings;
        }

        public List<String> getVariations() {
            if (this.variations == null) {
                variations = new LinkedList<String>();
            }
            return variations;
        }

        public String getDescription() {
            return description;
        }

        public String getRationale() {
            return rationale;
        }
    }

    public class GovernanceInfoWrapper implements java.io.Serializable {
        private List<String> campusLocations;
        private List<String> curriculumOversight;
        private List<String> administeringOrganization;

        public List<String> getCampusLocations() {
            if (this.campusLocations == null) {
                campusLocations = new LinkedList<String>();
            }
            return campusLocations;
        }

        public List<String> getCurriculumOversight() {
            if (this.curriculumOversight == null) {
                curriculumOversight = new LinkedList<String>();
            }
            return curriculumOversight;
        }

        public List<String> getAdministeringOrganization() {
            if (this.administeringOrganization == null) {
                administeringOrganization = new LinkedList<String>();
            }
            return administeringOrganization;
        }
    }
    

    public class CourseLogisticsInfoWrapper implements java.io.Serializable {
        private String term;
        private String gradingOptions;
        private String atpDurationType;
        private String timeQuantity;
        private String passFail;
        private String audit;

        public String getTerm() {
            return term;
        }

        public String getGradingOptions() {
            return gradingOptions;
        }

        public String getAtpDurationType() {
            return atpDurationType;
        }

        public String getTimeQuantity() {
            return timeQuantity;
        }

        public String getPassFail() {
            return passFail;
        }

        public void setAudit(final String audit) {
            this.audit = audit;
        }

        public String getAudit() {
            return audit;
        }
    }

    public class LearningObjectivesInfoWrapper implements java.io.Serializable {


    }

    public class CourseRequisitesInfoWrapper implements java.io.Serializable {

    }

    public class ActiveDatesInfoWrapper implements java.io.Serializable {
        private String startTerm;
        private String endTerm;
        private String pilotCourse;

        public void setStartTerm(final String startTerm) {
            this.startTerm = startTerm;
        }

        public String getStartTerm() {
            return startTerm;
        }
 
        public void setEndTerm(final String endTerm) {
            this.endTerm = endTerm;
        }

        public String getEndTerm() {
            return endTerm;
        }
        
        public void setPilotCourse(final String pilotCourse) {
            this.pilotCourse = pilotCourse;
        }

        public String getPilotCourse() {
            return pilotCourse;
        }
        
    }

    public class FinancialsInfoWrapper implements java.io.Serializable {
        private String justificationOfFees;
        private String fee;
        private String revenueSource;
        private String expendingOrganization;

        
        public void setJustificationOfFees(final String justificationOfFees) {
            this.justificationOfFees = justificationOfFees;
        }

        public String getJustificationOfFees() {
            return justificationOfFees;
        }

        public void setFee(final String fee) {
            this.fee = fee;
        }

        public String getFee() {
            return fee;
        }

        public void setRevenueSource(final String revenueSource) {
            this.revenueSource = revenueSource;
        }

        public String getRevenueSource() {
            return revenueSource;
        }

        public void setExpendingOrganization(final String expendingOrganization) {
            this.expendingOrganization = expendingOrganization;
        }

        public String getExpendingOrganization() {
            return expendingOrganization;
        }
    }

    public class SupportingDocumentsInfoWrapper implements java.io.Serializable {
        
    }
}
