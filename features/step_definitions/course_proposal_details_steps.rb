When(/^I add Optional\-Other details to the course proposal$/) do
  #Add 2 Instructors
  #Add Governance --> Administering Orgs

  #Add Course Logistics -->Term - select three of the options
  #Add Course Logistics -->Duration Type - select one of the options
  #Add Course Logistics -->Duration Count - enter any 1-3 digit number
  #Add Course Logistics -->Audit - select
  #Add Course Logistics -->Pass Fail Transcript Grade - select

  #Add Active Date --> Pilot Course - select
  #Add Active Date --> End Date - select

  #Add Financials --> Justification of Fees

end


And(/^I perform a search for the course proposal with given proposal ID$/) do
  navigate_to_cm_home
  @course_proposal.search(@course_proposal.proposal_title)
  on FindProposalPage do |page|
    page.review_proposal_action_link(@course_proposal.proposal_title)
    page.loading_wait
  end
  sleep 1
end

Then(/^I should see Optional\-Other details on the course proposal$/) do
  @course_proposal.review_proposal_action
  on CmReviewProposal do |page|
    page.proposal_title_review.should == @course_proposal.proposal_title
    page.course_title_review.should == @course_proposal.course_title
    page.instructors_review.should == @course_proposal.optional_fields[0].instructor_list[0].instructor_name
    page.administering_org_review.should == @course_proposal.optional_fields[0].admin_org_list[0].admin_org_name

    page.terms_review.should include "Any" if @course_proposal.optional_fields[0].term_any == :set
    page.terms_review.should include "Fall" if @course_proposal.optional_fields[0].term_fall == :set
    page.terms_review.should include "Any" if @course_proposal.optional_fields[0].term_spring == :set
    page.terms_review.should include "Any" if @course_proposal.optional_fields[0].term_summer == :set

    page.duration_review.should include "#{@course_proposal.optional_fields[0].duration_type}"
    page.duration_review.should include "#{@course_proposal.optional_fields[0].duration_count}"

    page.audit_review.should == "Yes" if @course_proposal.optional_fields[0].audit == :set
    page.pass_fail_transcript_review.should == "Yes" if @course_proposal.optional_fields[0].pass_fail_transcript_grade == :set

    page.pilot_course_review.should == "Yes" if @course_proposal.optional_fields[0].pilot_course == :set
    page.end_term_review.should == @course_proposal.optional_fields[0].end_term
    page.fee_justification_review.should == @course_proposal.optional_fields[0].justification_of_fees


  end
end

When(/^I create a course proposal with Optional\-Other fields$/) do
  @course_proposal = create CmCourseProposalObject, subject_code: nil,
                                                    course_number: nil,
                                                    optional_fields: [(make CmOptionalFieldsObject)],
                                                    cross_listed_course_list: nil,
                                                    jointly_offered_course_list: nil,
                                                    version_code_list: nil,
                                                    transcript_course_title: nil,
                                                    description_rationale: nil,
                                                    proposal_rationale: nil,
                                                    campus_location: nil,
                                                    curriculum_oversight: nil,
                                                    assessment_scale: nil,
                                                    final_exam_type: nil,
                                                    final_exam_rationale: nil,
                                                    outcome_list: nil,
                                                    format_list: nil,
                                                    start_term: nil,
                                                    defer_save: true,
                                                    create_new_proposal: false,
                                                    create_optional_fields: true

end

Given(/^I have a basic course admin proposal with Optional\-Other details created as CS$/) do
  pending # express the regexp above with the code you wish you had
end

When(/^I update Optional\-Other details on the course proposal$/) do
  pending # express the regexp above with the code you wish you had
end

Given(/^I have a basic course proposal with Optional\-Other details created as Faculty$/) do
  pending # express the regexp above with the code you wish you had
end

When(/^I delete Optional\-Other details on the course proposal$/) do
  pending # express the regexp above with the code you wish you had
end
