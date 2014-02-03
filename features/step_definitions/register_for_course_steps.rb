When /^I add a course offering to my registration cart$/ do
  @reg_request = make RegistrationRequest :student_id=>"student",
                                          :term_code=>"201301",
                                          :term_descr=>"Spring 2013",
                                          :course_code=>"CHEM231",
                                          :reg_group=>"1001"
end

When /^I add a course to my registration cart and specify course options$/ do
  pending
end

When /^I drop a course from my registration cart$/ do
  pending
end

When /^I edit a course in my registration cart$/ do
  pending
end

Then /^the course is (present|not present) in my cart/  do |isPresent|
  on RegisterForCourseResults do |page|
    page.target_list_item_by_course(@reg_request.course_code, @reg_request.reg_group).should_not be_nil
  end
end

Then /^the course is present in my schedule, with the correct options$/  do
  pending
end

Then /^the modified course is present in my schedule$/  do
  pending
end