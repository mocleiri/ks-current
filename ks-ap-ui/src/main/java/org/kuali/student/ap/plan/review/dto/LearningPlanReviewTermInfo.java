package org.kuali.student.ap.plan.review.dto;

import java.util.List;

import org.kuali.student.ap.plan.review.LearningPlanReviewTerm;

public class LearningPlanReviewTermInfo implements LearningPlanReviewTerm {

	private String termId;
	private List<String> courseIds;
	
	@Override
	public String getTermId() {
		return termId;
	}

	public void setTermId(String termId) {
		this.termId = termId;
	}

	@Override
	public List<String> getCourseIds() {
		return courseIds;
	}

	public void setCourseIds(List<String> courseIds) {
		this.courseIds = courseIds;
	}

}
