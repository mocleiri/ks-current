Given /^I have a course proposal created as Curriculum Specialist$/ do
  steps %{Given I am logged in as Curriculum Specialist}
  @course_proposal = create CmCourseProposalObject
end

Given /^I have a course admin proposal created as Curriculum Specialist$/ do
  steps %{Given I am logged in as Curriculum Specialist}
  @course_proposal = create CmCourseProposalObject, :curriculum_review_process => "Yes"
end

Given /^I have a course proposal created as Faculty$/ do
  steps %{Given I am logged in as Faculty}
  @course_proposal = create CmCourseProposalObject
end

Given /^I have a admin course proposal created as Curriculum Specialist and course proposal created as Faculty$/ do
  steps %{Given I am logged in as Curriculum Specialist}
  @course_proposal_cs = create CmCourseProposalObject, :proposal_title => "Alice Math class #{random_alphanums(10,'test proposal title ') }"
  puts "CS proposal title is #{@course_proposal_cs.proposal_title}"

  steps %{Given I am logged in as Faculty}
  @course_proposal_faculty = create CmCourseProposalObject, :proposal_title => "Freds Math class #{random_alphanums(10,'test proposal title ') }"
  puts "Faculty proposal title is #{@course_proposal_faculty.proposal_title}"
end

Given /^I have a course proposal created as Faculty and logged in as Curriculum Specialist$/ do
  steps %{Given I am logged in as Faculty}
  @course_proposal_faculty = create CmCourseProposalObject, :proposal_title => "Freds Math class #{random_alphanums(10,'test proposal title ') }"
  puts "Faculty proposal title is #{@course_proposal_faculty.proposal_title}"

  steps %{Given I am logged in as Curriculum Specialist}
end

Given /^I have a course admin proposal created as Curriculum Specialist and logged in as Faculty$/ do
  steps %{Given I am logged in as Curriculum Specialist}
  @course_proposal_cs = create CmCourseProposalObject, :proposal_title => "Alice Math class #{random_alphanums(10,'test proposal title ') }"
  puts "CS proposal title is #{@course_proposal_cs.proposal_title}"

  steps %{Given I am logged in as Faculty}
end

When /^I perform a full search for the course proposal$/ do
  navigate_to_cm_home
  @course_proposal.search(@course_proposal.proposal_title)
end


When /^I perform a search for the course proposal$/ do
  navigate_rice_to_cm_home
  @course_proposal.search(@course_proposal.proposal_title)
end

And /^I perform a complete search for the course proposal$/ do
  navigate_rice_to_cm_home
  @course_proposal_faculty.search(@course_proposal_faculty.proposal_title)
end

And /^I perform a complete search for the course admin proposal$/ do
  navigate_rice_to_cm_home
  @course_proposal_cs.search(@course_proposal_cs.proposal_title)
end

Then /^I should see my proposal listed in the search result$/ do
  on FindProposalPage do |page|
    page.proposal_title_element(@course_proposal.proposal_title).should exist
  end
end


Then /^I should see both proposals listed in the search result$/ do
  on FindProposalPage do |page|
    page.proposal_title_element(@course_proposal_cs.proposal_title).should exist
    page.proposal_title_element(@course_proposal_faculty.proposal_title).should exist
  end
end

And /^I perform a partial search for Course Proposals$/ do
  navigate_rice_to_cm_home
  #using part of the text that is common across both test proposals
  search_text = @course_proposal_cs.proposal_title.slice(6,15)
  @course_proposal_cs.search(search_text)
end


And /^I can review the proposal created by (.*?)$/ do |proposal_to_review|

  if proposal_to_review == "Curriculum Specialist"
    @course_proposal_cs.review_proposal_action
    on CmReviewProposal do |page|
        page.proposal_title_review_read_only.should == @course_proposal_cs.proposal_title
        page.course_title_review_read_only.should == @course_proposal_cs.course_title
        page.page_header_text.should == "#{@course_proposal_cs.proposal_title} (Admin Proposal)"
    end
  else
    @course_proposal_faculty.review_proposal_action
    on CmReviewProposal do |page|
      page.proposal_title_review.should == @course_proposal_faculty.proposal_title
      page.course_title_review.should == @course_proposal_faculty.course_title
      page.page_header_text.should == "#{@course_proposal_faculty.proposal_title} (Proposal)"
    end
  end
end

And /^I can review the course (.*?)$/ do |proposal_type|
  @course_proposal.review_proposal_action
  on CmReviewProposal do |page|
    #COURSE INFORMATION SECTION
    page.proposal_title_review.should == @course_proposal.proposal_title
    page.course_title_review.should == @course_proposal.course_title
    page.page_header_text.should == "#{@course_proposal.proposal_title} (Proposal)" if proposal_type == "proposal"
    page.page_header_text.should == "#{@course_proposal.proposal_title} (Admin Proposal)" if proposal_type == "admin proposal"
    page.subject_code_review.should == "#{@course_proposal.subject_code}"
    page.description_review.should == "#{@course_proposal.description_rationale}"
    page.proposal_rationale_review.should == "#{@course_proposal.proposal_rationale}"

    #GOVERNANCE SECTION
    page.curriculum_oversight_review.should == @course_proposal.curriculum_oversight unless @course_proposal.curriculum_oversight.nil?

    #COURSE LOGISTICS SECTION
    #ASSESSMENT SCALE
    page.assessment_scale_review.should == "A-F with Plus/Minus Grading" if @course_proposal.assessment_a_f == :set
    page.assessment_scale_review.should == "Accepts a completed notation" if @course_proposal.assessment_notation == :set
    page.assessment_scale_review.should == "Letter" if @course_proposal.assessment_letter == :set
    page.assessment_scale_review.should == "Pass/Fail Grading" if @course_proposal.assessment_pass_fail == :set
    page.assessment_scale_review.should == "Percentage Grading 0-100%" if @course_proposal.assessment_percentage == :set
    page.assessment_scale_review.should == "Administrative Grade of Satisfactory" if @course_proposal.assessment_satisfactory == :set

    #FINAL EXAM
    page.final_exam_status_review.should == "Standard Final Exam" unless @course_proposal.exam_standard.nil?
    page.final_exam_status_review.should == "Alternate Final Assessment" unless @course_proposal.exam_alternate.nil?
    page.final_exam_status_review.should == "No Final Exam or Assessment" unless @course_proposal.exam_none.nil?
    page.final_exam_rationale_review.should == @course_proposal.final_exam_rationale unless @course_proposal.exam_standard == :set

    #FIXED OUTCOME
    page.outcome_level_review(1).should == "Outcome #{@course_proposal.outcome_list[0].outcome_level.to_i+1}" unless @course_proposal.outcome_list[0].outcome_level.nil?
    page.outcome_type_review(1).should == "Fixed" unless @course_proposal.outcome_list[0].outcome_type.nil?
    page.outcome_credit_review(1) == "#{@course_proposal.outcome_list[0].credit_value}" unless @course_proposal.outcome_list[0].credit_value.nil?

    #RANGE OUTCOME
    page.outcome_level_review(2).should == "Outcome #{@course_proposal.outcome_list[1].outcome_level.to_i+1 }" unless @course_proposal.outcome_list[1].outcome_level.nil?
    page.outcome_type_review(2).should == "Range" unless @course_proposal.outcome_list[1].outcome_type.nil?
    page.outcome_credit_review(2) == "#{@course_proposal.outcome_list[1].credit_value}" unless @course_proposal.outcome_list[0].credit_value.nil?

    #MULTIPLE OUTCOME
    page.outcome_level_review(3).should == "Outcome #{@course_proposal.outcome_list[2].outcome_level.to_i+1}" unless @course_proposal.outcome_list[1].outcome_level.nil?
    page.outcome_type_review(3).should == "Multiple" unless @course_proposal.outcome_list[2].outcome_type.nil?
    page.outcome_credit_review(3) == "#{@course_proposal.outcome_list[2].credit_value}" unless @course_proposal.outcome_list[0].credit_value.nil?

    #ACTIVE DATES SECTION
    page.start_term_review.should == @course_proposal.start_term unless @course_proposal.start_term.nil?

    end

  end

And /^I can review the required fields on the (.*?)$/ do |proposal_type|
  @course_proposal.review_proposal_action

  #COURSE INFORMATION SECTION
  on CmReviewProposal do |page|
    page.proposal_title_review.should == @course_proposal.proposal_title
    page.course_title_review.should == @course_proposal.course_title
    page.page_header_text.should == "#{@course_proposal.proposal_title} (Admin Proposal)" if proposal_type == "admin proposal"
    page.page_header_text.should == "#{@course_proposal.proposal_title} (Proposal)" if proposal_type == "course proposal"
    page.subject_code_review.should == @course_proposal.subject_code
    page.course_number_review.should == @course_proposal.course_number
    page.description_review.should == @course_proposal.description_rationale
    page.proposal_rationale_review.should == @course_proposal.proposal_rationale


  #GOVERNANCE SECTION

    page.campus_locations_review.should == "North" if @course_proposal.location_north == :set
    page.campus_locations_review.should == "South" if @course_proposal.location_south == :set
    page.campus_locations_review.should == "Extended" if @course_proposal.location_extended == :set
    page.campus_locations_review.should == "All" if @course_proposal.location_all == :set
    page.curriculum_oversight_review.should == @course_proposal.curriculum_oversight unless @course_proposal.curriculum_oversight.nil?



  #LOGISTICS SECTION


    #ASSESSMENT SCALE
    page.assessment_scale_review.should == "A-F with Plus/Minus Grading" if @course_proposal.assessment_a_f == :set
    page.assessment_scale_review.should == "Accepts a completed notation" if @course_proposal.assessment_notation == :set
    page.assessment_scale_review.should == "Letter" if @course_proposal.assessment_letter == :set
    page.assessment_scale_review.should == "Pass/Fail Grading" if @course_proposal.assessment_pass_fail == :set
    page.assessment_scale_review.should == "Percentage Grading 0-100%" if @course_proposal.assessment_percentage == :set
    page.assessment_scale_review.should == "Administrative Grade of Satisfactory" if @course_proposal.assessment_satisfactory == :set

    #FINAL EXAM
    page.final_exam_status_review.should == "Standard Final Exam" unless @course_proposal.exam_standard.nil?
    page.final_exam_status_review.should == "Alternate Final Assessment" unless @course_proposal.exam_alternate.nil?
    page.final_exam_status_review.should == "No Final Exam or Assessment" unless @course_proposal.exam_none.nil?
    page.final_exam_rationale_review.should == @course_proposal.final_exam_rationale unless @course_proposal.exam_standard == :set


    #FIXED OUTCOME
    page.outcome_level_review(1).should == "Outcome #{@course_proposal.outcome_list[0].outcome_level.to_i+1}" unless @course_proposal.outcome_list[0].outcome_level.nil?
    page.outcome_type_review(1).should == "Fixed" unless @course_proposal.outcome_list[0].outcome_type.nil?
    page.outcome_credit_review(1) == "#{@course_proposal.outcome_list[0].credit_value}" unless @course_proposal.outcome_list[0].credit_value.nil?

    #RANGE OUTCOME
    page.outcome_level_review(2).should == "Outcome #{@course_proposal.outcome_list[1].outcome_level.to_i+1 }" unless @course_proposal.outcome_list[1].outcome_level.nil?
    page.outcome_type_review(2).should == "Range" unless @course_proposal.outcome_list[1].outcome_type.nil?
    page.outcome_credit_review(2) == "#{@course_proposal.outcome_list[1].credit_value}" unless @course_proposal.outcome_list[0].credit_value.nil?

    #MULTIPLE OUTCOME
    page.outcome_level_review(3).should == "Outcome #{@course_proposal.outcome_list[2].outcome_level.to_i+1}" unless @course_proposal.outcome_list[1].outcome_level.nil?
    page.outcome_type_review(3).should == "Multiple" unless @course_proposal.outcome_list[2].outcome_type.nil?
    page.outcome_credit_review(3) == "#{@course_proposal.outcome_list[2].credit_value}" unless @course_proposal.outcome_list[0].credit_value.nil?

    #ACTIVITY FORMAT
    page.activity_level_review(1).should == "Format 1"
    page.activity_type_review(1).should include "#{@course_proposal.format_list[0].type}".gsub(/\s+/, "")
    page.activity_contact_hours_frequency_review(1).should include "#{@course_proposal.format_list[0].contacted_hours}"
    page.activity_contact_hours_frequency_review(1).should include "#{@course_proposal.format_list[0].contact_frequency}"
    page.activity_duration_type_count_review(1).should include "#{@course_proposal.format_list[0].duration_type}"
    page.activity_duration_type_count_review(1).should include "#{@course_proposal.format_list[0].duration_count}"
    page.activity_class_size_review(1).should == "#{@course_proposal.format_list[0].class_size}"

    #ACTIVE DATES SECTION
    page.start_term_review.should == @course_proposal.start_term unless @course_proposal.start_term.nil?



end


end