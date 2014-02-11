When /^I add a course offering to my registration cart$/ do
  @reg_request = make RegistrationRequest, :student_id=>"student",
                                           :term_code=>"201301",
                                           :term_descr=>"Spring 2013",
                                           :course_code=>"CHEM231",
                                           :reg_group_code=>"1001"
  @reg_request.create
end

When /^I add a course to my registration cart and specify course options$/ do
  course_options_list << (make CourseOptions, :credit_option => "4", :grading_option => "Pass/Fail")
  @reg_request = create RegistrationRequest, :student_id => "student", :term_code => "201301", :course_options_list => course_options_list, :modify_course_options => true
  # above will include entering course_code, reg_group_code (& term if nec), and clicking Add to Cart, then changing the 2 options, and clicking Add to Cart again
end

When /^I drop the course from my registration cart$/ do
  page.target_list_item_by_course(@reg_request.course_code, @reg_request.reg_group_code).remove_from_cart   #?? need to chg. "target"s
end

When /^I edit a course in my registration cart$/ do
  pending
end

Then /^the course is (present|not present) in my cart$/  do |presence|
  on RegistrationCart do |page|
    if presence == "present"
      page.course_title(@reg_request.course_code, @reg_request.reg_group_code).should_not be_nil
    else
      page.target_list_item_by_course(@reg_request.course_code, @reg_request.reg_group_code).should be_nil
    end
  end
end

Then /^the course is present in my cart, with the correct options$/  do
  on CourseRegistration do |page|
    page.get_credit_option(@reg_request).should == course_options_list.credit_option
    page.get_grading_option(@reg_request).should == course_options_list.grading_option
    #do we need to quit or remove course?
  end
end

Then /^the modified course is present in my cart$/  do
  pending
end