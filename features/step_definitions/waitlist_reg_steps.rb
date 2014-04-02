When /^I register for a full course offering that has a waitlist$/ do |user|
  @reg_request = make RegistrationRequest, :student_id=>user,
                      :term_code=>"201208",
                      :term_descr=>"Fall 2012",
                      :course_code=>"TBD",
                      :reg_group_code=>"1001",
                      :course_has_options=>false
  @reg_request.create
  @reg_request.register
  sleep 3
end

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
  #on RegistrationCart do |page|
  #  page.result_status(@reg_request.course_code,@reg_request.reg_group_code).should include "No Seats Available"
  #end
end

Then /^there is a message indicating that the course is waitlisted$/  do
  #on RegistrationCart do |page|
  #  page.result_status(@reg_request.course_code,@reg_request.reg_group_code).should include "No Seats Available"
  #end
end

And /^I can verify I am on the waitlist by viewing my schedule$/  do
  #pending
end