When /^I add a course offering to my registration cart$/ do
  @reg_request = make RegistrationRequest, :student_id=>"student",
                                           :term_code=>"201201",
                                           :term_descr=>"Spring 2012",
                                           :course_code=>"CHEM231",
                                           :reg_group_code=>"1001"
  @reg_request.create
end

When /^I add a course to my registration cart and specify course options$/ do
  course_options = (make CourseOptions, :credit_option => "4.0", :grading_option => "Pass/Fail")
  @reg_request = create RegistrationRequest, :student_id => "student", :term_code => "201201",
                        :term_descr=>"Spring 2012",
                        :course_code=>"CHEM237",
                        :reg_group_code=>"1001", :course_options => course_options, :modify_course_options => true
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
      page.course_title(@reg_request.course_code, @reg_request.reg_group_code).should be_nil
    end
  end
end

Then /^the course is present in my cart, with the correct options$/  do
  on RegistrationCart do |page|
    page.course_info(@reg_request.course_code, @reg_request.reg_group_code).should include "#{@reg_request.course_options.credit_option[0]} credits"
    page.course_info(@reg_request.course_code, @reg_request.reg_group_code).should include "#{@reg_request.course_options.grading_option}"
    #do we need to quit or remove course?
  end
end

Then /^the modified course is present in my cart$/  do
  pending
end

And /^I? ?can view the details of my selections?$/ do
  on RegistrationCart do |page|
    page.course_title(@reg_request.course_code, @reg_request.reg_group_code).should == "Organic Chemistry I"
    page.course_info(@reg_request.course_code, @reg_request.reg_group_code).should include "#{@reg_request.course_options.credit_option[0]} credits"
    page.course_info(@reg_request.course_code, @reg_request.reg_group_code).should include "#{@reg_request.course_options.grading_option}"
    page.course_schedule(@reg_request.course_code, @reg_request.reg_group_code,0).should include "DIS"
    page.course_schedule(@reg_request.course_code, @reg_request.reg_group_code,0).should include "M 3:00 pm - 3:50 pm CHM"
    page.course_schedule(@reg_request.course_code, @reg_request.reg_group_code,1).should include "LEC"
    page.course_schedule(@reg_request.course_code, @reg_request.reg_group_code,1).should include "TH 11:00 am - 12:15 pm EGR"
  end
end