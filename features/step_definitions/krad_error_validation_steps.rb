Given(/^I set the proposal title and course title to blank$/) do
  @course_proposal = make KradCourseProposalObject, proposal_title: '', course_title: ''

  on(KradCurriculum).course_information
  on KradCourseInformation do |page|
    page.proposal_title.fit @proposal_title
    page.course_title.fit @course_title
    page.save_and_continue
  end

end

Then(/^I should see the error message for proposal title$/) do
  on(KradCourseInformation).proposal_title_error_state.should be_true
end

Then(/^I should see the error message for course title$/) do
  on(KradCourseInformation).course_title_error_state.should be_true
end

Given(/^I have a course proposal with a missing required field$/) do
  @course_proposal = make KradCourseProposalObject, [course_number: nil, description_rationale: nil,
          proposal_rationale: nil, curriculum_oversight: nil,#].sample
          assessment_scale: nil, final_exam_status: nil].sample
          #[description_rationale: '', proposal_rationale: ''].sample


  @course_proposal.KradCourseProposalRequired
end

When(/^I submit the course proposal on the review proposal page$/) do
  on(KradCurriculum).review_proposal
  on KradReviewProposal do |page|
    page.submit
  end
end

Then(/^I should see an error on the review proposal page$/) do
  pending # express the regexp above with the code you wish you had
end

