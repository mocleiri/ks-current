Then /^I am given the option to add myself to a waitlist for the course$/ do
  on RegistrationCart do |page|
    page.add_to_waitlist_button(@reg_request.course_code,@reg_request.reg_group_code).wait_until_present
  end
end

When /^I add myself to a waitlist for the course$/ do
  on RegistrationCart do |page|
    page.add_to_waitlist(@reg_request.course_code,@reg_request.reg_group_code)
  end
end

Then /^there is a message indicating that I have been added to the waitlist$/  do
  on RegistrationCart do |page|
    page.waitlist_status(@reg_request.course_code,@reg_request.reg_group_code).wait_until_present
    page.waitlist_status(@reg_request.course_code,@reg_request.reg_group_code).text.should match /added to waitlist/i
  end
end

Then /^there is a message indicating that registration failed$/  do
  on RegistrationCart do |page|
    page.course_code(@reg_request.course_code,@reg_request.reg_group_code).wait_until_present
    page.course_code(@reg_request.course_code,@reg_request.reg_group_code).text.should match /failed/i
  end
end

And /^there is a message indicating that no waitlist is offered$/  do
  on RegistrationCart do |page|
    page_status = page.result_status(@reg_request.course_code,@reg_request.reg_group_code)
    page_status.should match /waitlist not offered/i
  end
end

And /^there is a message indicating that the waitlist is full$/  do
  on RegistrationCart do |page|
    page_status = page.result_status(@reg_request.course_code,@reg_request.reg_group_code)
    puts "Page status: #{page_status}"
    page_status.should match /waitlist full/i
  end
end

And /^I can verify I am on the waitlist$/  do
  #check that course appears as a waitlisted course
  on StudentSchedule do |page|
    page.course_code(@reg_request.course_code,@reg_request.reg_group_code,"waitlist").wait_until_present
  end
end

Given /^I register for an? full (\w+) course offering that (has|does not have) a waitlist$/ do |subj,waitlist|
  #course has 1 remaining seat, so first fill it as student, then try to register as student1
  steps %{
    When I add an #{subj} course offering to my registration cart
    And I register for the course
    Then I log out from student registration
    And I log in to student registration as student1
    And I add an #{subj} course offering to my registration cart
    And I register for the course
  }
end


When /^I register for an? full (\w+) course offering and add myself to a waitlist$/  do |subj|
  steps %{
    When I add an #{subj} course offering to my registration cart
    And I register for the course
    And I view my schedule
    Then the course is present in my schedule
    Then I log out from student registration
    And I log in to student registration as student1
    And I add an #{subj} course offering to my registration cart
    And I register for the course
    And I add myself to a waitlist for the course
  }
end

Then /^there is an option to edit the waitlisted course$/ do
  on StudentSchedule do |page|
    page.show_course_details(@reg_request.course_code,@reg_request.reg_group_code,"waitlist")
    page.edit_course_options_button(@reg_request.course_code,@reg_request.reg_group_code,"waitlist").wait_until_present
    page.hide_course_details(@reg_request.course_code,@reg_request.reg_group_code,"waitlist")
  end
end

When /^I edit the waitlisted course$/ do
  @reg_request.course_options.credit_option = "1.5"
  @reg_request.course_options.grading_option = "Pass/Fail"
  @reg_request.edit_course_options  :grading_option => @reg_request.course_options.grading_option,
                                    :credit_option => @reg_request.course_options.credit_option,
                                    :context => "waitlist"
end

Then /^the course is present in my waitlist, with the updated options$/ do
  on StudentSchedule do |page|
    page.course_info_div(@reg_request.course_code,@reg_request.reg_group_code,"waitlist").wait_until_present
    sleep 1
    page.course_info(@reg_request.course_code, @reg_request.reg_group_code,"waitlist").downcase.should include "#{@reg_request.course_options.credit_option} cr"
    # grading option badge is not displayed for Letter grade (the assumed default), only displayed for non-standard options e.g., Audit or Pass/Fail
    unless @reg_request.course_options.grading_option == "Letter"
      page.grading_option_badge(@reg_request.course_code,@reg_request.reg_group_code,"waitlist").wait_until_present
      page.grading_option(@reg_request.course_code,@reg_request.reg_group_code,"waitlist").should include "#{@reg_request.course_options.grading_option}"
    end
  end
end

And /^I remove myself from the waitlist$/ do
  @reg_request.remove_course("waitlist")
end

Then /^I can verify I am not on the waitlist$/ do
  on StudentSchedule do |page|
    sleep 1
    page.user_message_div("waitlist").wait_until_present
    page.user_message("waitlist").should include "Removed from waitlist for #{@reg_request.course_code} (#{@reg_request.reg_group_code})"
    page.course_code(@reg_request.course_code, @reg_request.reg_group_code,"waitlist").exists?.should be_false
  end
end

Then /^I can go to My Schedule and verify I am not on the waitlist$/ do
  visit StudentSchedule
  on StudentSchedule do |page|
    #this is a wait, until dev gets in a notification that processing is finished
    @reg_request.change_term_and_return(@reg_request.term_descr, "Spring 2012")
    page.course_code(@reg_request.course_code, @reg_request.reg_group_code,"waitlist").exists?.should be_false
  end
end
