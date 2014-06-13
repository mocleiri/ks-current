When /^I attempt to register for two courses whose times conflict$/ do
  steps %{
   When I add a HIST2 course offering to my registration cart
   And I register for the course
   Then I add a HIST3 course offering to my registration cart
   And I register for the course
   }
end

And /^there is a message indicating a time conflict$/ do
  on RegistrationCart do |page|
    page_status = page.result_status(@reg_request.course_code, @reg_request.reg_group_code)
    page_status.should =~ /time conflict/i
  end
end
