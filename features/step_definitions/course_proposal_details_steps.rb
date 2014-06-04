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
  @course_proposal.course_fees = random_alphanums(10, 'test course fees ')
  on CmCourseFinancials do |page|
    page.financials unless page.current_page('Financials').exists?
    page.loading_wait
    page.financial_fee_text.set @course_proposal.course_fees
    page.save_progress
  end

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
  on CmReviewProposal do |page|
    #puts "Original Proposal Title is #{page.proposal_title_review}"
    #puts "Original Course Title is #{page.course_title_review}"
    page.proposal_title_review.should == @course_proposal.proposal_title
    page.course_title_review.should == @course_proposal.course_title
    page.financial_fee_text.should == @course_proposal.course_fees

  end
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

When(/^I delete alternate identifier details to the course proposal$/) do
  pending # express the regexp above with the code you wish you had
end
