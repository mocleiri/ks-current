Then /^I am given the option to add myself to a waitlist for the course$/ do
  on RegistrationCart do |page|
    page.add_to_waitlist_button(@reg_request.course_code,@reg_request.reg_group_code).wait_until_present
  end
end

When /^I add myself to a waitlist for the course$/ do
  on RegistrationCart do |page|
    add_to_waitlist(@reg_request.course_code,@reg_request.reg_group_code)
  end
end

Then /^there is a message indicating that I have been added to the waitlist$/  do
  on RegistrationCart do |page|
    page.result_status(@reg_request.course_code,@reg_request.reg_group_code).should include "Added to Waitlist"
  end
end

Then /^there is a message indicating that the course is waitlisted$/  do
  on RegistrationCart do |page|
    page.course_code(@reg_request.course_code,@reg_request.reg_group_code).text.should include "Waitlist"
  end
end

And /^I can verify I am on the waitlist$/  do
  #pending
end

Given /^I register for an? full (\w+) course offering that has a waitlist$/ do |subj|
  steps %{
    When I add an #{subj} course offering to my registration cart
    And I register for the course
    And I view my schedule
    Then the course is present in my schedule
    When I log out
    And I am logged in as a Student
    And I add an #{subj} course offering to my registration cart
    And I register for the course
  }
end

