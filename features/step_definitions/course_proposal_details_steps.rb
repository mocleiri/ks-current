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

    @course_proposal.optional_fields[0].instructor_list.each do |instructor|
      page.instructors_review.should include instructor.instructor_name
    end

    @course_proposal.optional_fields[0].admin_org_list.each do |admin_org|
      page.administering_org_review.should include admin_org.admin_org_name
    end

    page.terms_review.should include "Any" if @course_proposal.optional_fields[0].term_any == :set
    page.terms_review.should include "Fall" if @course_proposal.optional_fields[0].term_fall == :set
    page.terms_review.should include "Spring" if @course_proposal.optional_fields[0].term_spring == :set
    page.terms_review.should include "Summer" if @course_proposal.optional_fields[0].term_summer == :set

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
  #put some code here
end

When(/^I update the Optional Other details on the course proposal$/) do
  navigate_to_cm_home
  @course_proposal.search(@course_proposal.proposal_title)
  @course_proposal.edit_proposal_action

  @course_proposal.optional_fields[0].instructor_list[0].delete :instructor_level => 1, :defer_save => true
  @course_proposal.optional_fields[0].instructor_list[1].edit :instructor_name => "MILES", :instructor_level => 1, :defer_save => true

  @course_proposal.optional_fields[0].admin_org_list[0].delete :admin_org_level => 1, :defer_save => true
  @course_proposal.optional_fields[0].admin_org_list[1].edit :admin_org_name => "Avian", :admin_org_level => 1, :defer_save => true

  @course_proposal.optional_fields[0].edit :term_any => :clear,
                                           :term_fall => :clear,
                                           :term_summer => :set,
                                           :duration_type => '::random::',
                                           :duration_count => (1..999).to_a.sample,
                                           :pilot_course => :clear,
                                           :defer_save => true

  @course_proposal.edit :start_term => "Spring 1980", :defer_save => true

  @course_proposal.optional_fields[0].edit :justification_of_fees => "updated justification text"


end

Then(/^I should see updated Optional Other details on the course proposal$/) do
  @course_proposal.review_proposal_action
  on CmReviewProposal do |page|
    page.proposal_title_review.should == @course_proposal.proposal_title
    page.course_title_review.should == @course_proposal.course_title

    page.instructors_review.should include @course_proposal.optional_fields[0].instructor_list[1].instructor_name
    page.administering_org_review.should include @course_proposal.optional_fields[0].admin_org_list[1].admin_org_name


    page.terms_review.should include "Any" if @course_proposal.optional_fields[0].term_any == :set
    page.terms_review.should include "Fall" if @course_proposal.optional_fields[0].term_fall == :set
    page.terms_review.should include "Spring" if @course_proposal.optional_fields[0].term_spring == :set
    page.terms_review.should include "Summer" if @course_proposal.optional_fields[0].term_summer == :set

    page.duration_review.should include "#{@course_proposal.optional_fields[0].duration_type}"
    page.duration_review.should include "#{@course_proposal.optional_fields[0].duration_count}"

    page.audit_review.should == "Yes" if @course_proposal.optional_fields[0].audit == :set
    page.pass_fail_transcript_review.should == "Yes" if @course_proposal.optional_fields[0].pass_fail_transcript_grade == :set

    page.pilot_course_review.should == "Yes" if @course_proposal.optional_fields[0].pilot_course == :set

    page.start_term_review.should == @course_proposal.start_term
    page.end_term_review.should_not == @course_proposal.optional_fields[0].end_term
    page.fee_justification_review.should == @course_proposal.optional_fields[0].justification_of_fees


  end
end

And(/^I have a basic course proposal created with Optional-Other fields$/) do
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

When(/^I delete Optional\-Other details on the course proposal$/) do
  navigate_to_cm_home
  @course_proposal.search(@course_proposal.proposal_title)
  @course_proposal.edit_proposal_action

  @course_proposal.optional_fields[0].instructor_list[0].delete :instructor_level => 1, :defer_save => true
  @course_proposal.optional_fields[0].instructor_list[0].delete :instructor_level => 1, :defer_save => true
  @course_proposal.optional_fields[0].admin_org_list[0].delete :admin_org_level => 1, :defer_save => true
  @course_proposal.optional_fields[0].admin_org_list[0].delete :admin_org_level => 1, :defer_save => true

  @course_proposal.optional_fields[0].edit :term_any => :clear,
                                           :term_fall => :clear,
                                           :term_spring => :clear,
                                           :term_summer => :clear,
                                           :duration_type => '::random::',
                                           :duration_count => (1..999).to_a.sample,
                                           :duration_type => "",
                                           :duration_count => "",
                                           :audit => :clear,
                                           :pass_fail_transcript_grade => :clear,
                                           :pilot_course => :clear,
                                           :justification_of_fees => " "

end

Then(/^I should no longer see Optional\-Other details on the course proposal$/) do
  @course_proposal.review_proposal_action
  on CmReviewProposal do |page|
    page.proposal_title_review.should == @course_proposal.proposal_title
    page.course_title_review.should == @course_proposal.course_title

    @course_proposal.optional_fields[0].instructor_list.each do |instructor|
      page.instructors_review.should_not include instructor.instructor_name
    end

    @course_proposal.optional_fields[0].admin_org_list.each do |admin_org|
      page.administering_org_review.should_not include admin_org.admin_org_name
    end

    page.terms_review.should_not include "Any"
    page.terms_review.should_not include "Fall"
    page.terms_review.should_not include "Spring"
    page.terms_review.should_not include "Summer"

    page.duration_review.should include "#{@course_proposal.optional_fields[0].duration_type}"
    page.duration_review.should include "#{@course_proposal.optional_fields[0].duration_count}"

    page.audit_review.should_not == "Yes"
    page.pass_fail_transcript_review.should_not == "Yes"

    page.pilot_course_review.should_not == "Yes"
    page.end_term_review.should_not == @course_proposal.end_term
    page.fee_justification_review.should == ""

  end
end

And(/^I have Learning Objective Categories created$/) do
  navigate_rice_to_cm_home
  @learning_objective = create CmLoCategoryObject
end


When(/^I create a basic course proposal with Learning Objectives$/) do
  pending # express the regexp above with the code you wish you had
end


Then(/^I should see Learning Objective details on the course proposal$/) do
  pending # express the regexp above with the code you wish you had
end




