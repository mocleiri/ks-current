When /^I add a course offering to my registration cart$/ do
  @reg_request = make RegistrationRequest :student_id=>"student",
                                          :term_code=>"201301",
                                          :term_descr=>"Spring 2013",
                                          :course_code=>"CHEM231",
                                          :reg_group=>"1001"
end

When /^I add a course to my registration cart and specify course options$/ do
  # want to specify credits and grading option
  on CourseRegistration do
    course_options_list << (make CourseOptions, :credit_option => "4", :grading_option => "Pass/Fail")
    @registration_request = create RegistrationRequest, :course_code => "", :reg_group => "", :course_options_list => course_options_list, :modify_course_options => true
    # above will include entering course_code, reg_group (& term if nec), and clicking Add to Cart, then changing the 2 options, and clicking Add to Cart again
  end
end

When /^I drop a course from my registration cart$/ do
  pending
end

When /^I edit a course in my registration cart$/ do
  pending
end

Then /^the course is (present|not present) in my cart$/  do |isPresent|
  on RegisterForCourseResults do |page|
    page.target_list_item_by_course(@reg_request.course_code, @reg_request.reg_group).should_not be_nil
  end
end

Then /^the course is present in my cart, with the correct options$/  do
  on CourseRegistration do |page|
    page.get_credit_option(@registration_request).should == course_options_list.credit_option
    page.get_grading_option(@registration_request).should == course_options_list.grading_option
    #do we need to quit or remove course?
  end
end

Then /^the modified course is present in my schedule$/  do
  pending
end