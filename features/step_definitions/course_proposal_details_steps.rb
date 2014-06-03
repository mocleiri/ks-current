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
  on CmCourseFinancials do |page|
    page.financials unless page.current_page('Financials').exists?
    page.loading_wait
    page.financial_fee_text.set random_alphanums(10, 'test course fees ')

    page.save_progress

  end

end

And(/^I perform a search for the course proposal with given proposal ID$/) do
  navigate_to_cm_home
  @course_proposal.search(@course_proposal.proposal_title)
  on FindProposalPage do |page|
    page.edit_proposal_element(@course_proposal.proposal_title)
  end
  sleep 1
end

Then(/^I should see Optional\-Other details on the course proposal$/) do
  pending # express the regexp above with the code you wish you had
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

Then(/^I should no longer see Optional-Other details on the course proposal$/) do

end