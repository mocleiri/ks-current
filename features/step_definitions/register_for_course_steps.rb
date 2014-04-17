When /^I add an? (\w+) course offering to my registration cart$/ do |subj|
  # Set course code and credits
  course_code = case
                  when subj=="BSCI1" then "BSCI106"
                  when subj=="BSCI2" then "BSCI201"
                  when subj=="BSCI3" then "BSCI120"
                  when subj=="CHEM" then "CHEM231"
                  when subj=="CHEM3" then "CHEM399A"
                  when subj=="ENGL1" then "ENGL211"
                  when subj=="ENGL2" then "ENGL101"
                  when subj=="ENGL3" then "ENGL101"
                  when subj=="ENGL4" then "ENGL101"
                  when subj=="HIST" then "HIST111"
                  when subj=="PHYS" then "PHYS102"
                  when subj=="WMST" then "WMST360"
                  else ""
                end
  credit_option = case
                    when subj.match("BSCI") then "4.0"
                    else "3.0"
                  end

  # Get original counts before adding course to cart
  if subj=="WMST" || subj=="BSCI2"
    visit RegistrationCart do |page|
      sleep 1
      @orig_cart_course_count = page.credit_count_title.text.match('(\d*) course')[1].to_i
      @orig_cart_credit_count = page.credit_count_title.text.match('\((.*) credit')[1].to_f
    end
  end

  case subj
    when "BSCI3" then
      reg_group_code = "1001"
      term_code = "201208"
      term_descr = "Fall 2012"
      course_has_options = true
    when "CHEM3" then
      reg_group_code = "1001"
      term_code = "201208"
      term_descr = "Fall 2012"
      course_has_options = true
    when "ENGL2" then
      reg_group_code = "1001"
      term_code = "201208"
      term_descr = "Fall 2012"
      course_has_options = false
    when "ENGL3" then
      reg_group_code = "1009"
      term_code = "201208"
      term_descr = "Fall 2012"
      course_has_options = false
    when "ENGL4" then
      reg_group_code = "1003"
      term_code = "201208"
      term_descr = "Fall 2012"
      course_has_options = false
    else
      reg_group_code = "1001"
      term_code = "201201"
      term_descr = "Spring 2012"
      course_has_options = true
  end

  course_options = (make CourseOptions, :credit_option => credit_option)
  @reg_request = make RegistrationRequest, :student_id=>"student",
                      :term_code=> term_code,
                      :term_descr=> term_descr,
                      :course_code=>course_code,
                      :reg_group_code=>reg_group_code,
                      :course_options => course_options,
                      :course_has_options=> course_has_options
  @reg_request.create
end

When /^I add a course offering having multiple credit options to my registration cart$/ do
  @reg_request = make RegistrationRequest, :student_id=>"student",
                                           :term_code=>"201201",
                                           :term_descr=>"Spring 2012",
                                           :course_code=>"WMST498B",
                                           :reg_group_code=>"1001"
  @reg_request.create
end

When /^I add a course to my registration cart and specify course options$/ do
  course_options = (make CourseOptions, :credit_option => "2.5", :grading_option => "Pass/Fail")
  @reg_request = create RegistrationRequest, :student_id => "student", :term_code => "201201",
                        :term_descr=>"Spring 2012",
                        :course_code=>"WMST298G",
                        :reg_group_code=>"1001", :course_options => course_options, :modify_course_options => true
  # above will include entering course_code, reg_group_code and clicking Add to Cart, then changing the 2 options, and clicking Save
end

When /^I remove the course from my registration cart$/ do
  @reg_request.remove_from_cart
end

And /^I edit the course in my registration cart$/ do
  @reg_request.course_options.credit_option = "1.5"
  @reg_request.course_options.grading_option = "Pass/Fail"
  @reg_request.edit_course_options_in_cart :credit_option => @reg_request.course_options.credit_option,
                                           :grading_option => @reg_request.course_options.grading_option
end

When /^I edit the course in my schedule$/ do
  @reg_request.course_options.credit_option = "2.5"
  @reg_request.course_options.grading_option = "Audit"
  @reg_request.edit_course_options_in_schedule :credit_option => @reg_request.course_options.credit_option,
                                               :grading_option => @reg_request.course_options.grading_option
end

Then /^the course is (present|not present) in my cart$/  do |presence|
  on RegistrationCart do |page|
    if presence == "present"
      sleep 2
      page.course_title(@reg_request.course_code, @reg_request.reg_group_code).should_not be_nil
    else
      begin
        sleep 1
        page.user_message.should include "#{@reg_request.course_code}(#{@reg_request.reg_group_code}) has been successfully removed from your cart"
        page.course_code(@reg_request.course_code, @reg_request.reg_group_code).present?.should be_false
      rescue Watir::Exception::UnknownObjectException
        # the course is not there: good
      end
    end
  end
end

Then /^the course is present in my cart, with the updated options$/  do
  on RegistrationCart do |page|
    page.course_info_div(@reg_request.course_code,@reg_request.reg_group_code).wait_until_present
    unless @reg_request.course_options.grading_option == "Letter"
      page.grading_option_badge(@reg_request.course_code, @reg_request.reg_group_code).wait_until_present
      page.grading_option(@reg_request.course_code, @reg_request.reg_group_code).should include "#{@reg_request.course_options.grading_option}"
    end
  end
end

And /^I register for the course$/ do
  @reg_request.register
  sleep 3
end

Then /^there is a message indicating successful registration$/ do
  on RegistrationCart do |page|
    page.course_code(@reg_request.course_code,@reg_request.reg_group_code).text.should include "Success"
  end
end

When /^I remove the ?(BSCI2)? course from my schedule$/ do |phys|
  @reg_request.remove_from_schedule
end

When /^I? ?remove the course from my schedule and cancel the drop$/ do
  @reg_request.remove_from_schedule_and_cancel
end

When /^I view my schedule$/ do
  on RegistrationCart do |page|
    page.menu
    page.schedule_link.wait_until_present
    page.schedule_link.click
  end
end

And /^the course is (present|not present) in my schedule$/ do |presence|
  sleep 1
  on StudentSchedule do |page|
    if presence == "present"
      page.course_title_div(@reg_request.course_code, @reg_request.reg_group_code).wait_until_present
      page.course_title(@reg_request.course_code, @reg_request.reg_group_code).should_not be_nil
    else
      begin
        page.course_code(@reg_request.course_code, @reg_request.reg_group_code).present?.should be_false
      rescue Watir::Exception::UnknownObjectException
        # the course is not there: good
      end
    end
  end
end

Then /^the modified course is present in my cart$/  do
  #pending
end

And /^A successfully removed message appears$/ do
  #pending ?? for CR 1.4
end

And /^I? ?can view the details of my selection in the registration cart$/ do
  on RegistrationCart do |page|
    page.toggle_course_details(@reg_request.course_code, @reg_request.reg_group_code)
    page.wait_until { page.ao_type(@reg_request.course_code, @reg_request.reg_group_code,0) != "" }
    page.course_title(@reg_request.course_code, @reg_request.reg_group_code).should == "Organic Chemistry I"
    page.course_info(@reg_request.course_code, @reg_request.reg_group_code).should include "#{@reg_request.course_options.credit_option[0]} credits"
    unless @reg_request.course_options.grading_option == "Letter"
      page.grading_option_badge(@reg_request.course_code, @reg_request.reg_group_code).wait_until_present
      page.grading_option(@reg_request.course_code, @reg_request.reg_group_code).should include "#{@reg_request.course_options.grading_option}"
    end
    page.ao_type(@reg_request.course_code, @reg_request.reg_group_code,0).should include "DIS"
    page.course_schedule(@reg_request.course_code, @reg_request.reg_group_code,0,0).should match /M 3:00 pm - 3:50 pm(\s+)CHM/i
    page.ao_type(@reg_request.course_code, @reg_request.reg_group_code,1).should include "LEC"
    page.course_schedule(@reg_request.course_code, @reg_request.reg_group_code,1,0).should match /TuTh 11:00 am - 12:15 pm(\s+)EGR/i
  end
end

And /^I? ?can view the details of my selection in my schedule$/ do
  on StudentSchedule do |page|
    page.toggle_course_details(@reg_request.course_code, @reg_request.reg_group_code, "registered")
    page.wait_until { page.ao_type(@reg_request.course_code, @reg_request.reg_group_code,0) != "" }
    page.course_title(@reg_request.course_code, @reg_request.reg_group_code).should == "The Medieval World"
    page.course_info(@reg_request.course_code, @reg_request.reg_group_code).should include "#{@reg_request.course_options.credit_option[0]} credits"
    unless @reg_request.course_options.grading_option == "Letter"
      page.grading_option_badge(@reg_request.course_code, @reg_request.reg_group_code).wait_until_present
      page.grading_option(@reg_request.course_code, @reg_request.reg_group_code).should include "#{@reg_request.course_options.grading_option}"
    end
    page.ao_type(@reg_request.course_code, @reg_request.reg_group_code,0).should include "LEC"
    page.course_schedule(@reg_request.course_code, @reg_request.reg_group_code,0,0).should match /TuTh 14:00 - 14:50(\s+)KEY 0106/i
    page.ao_type(@reg_request.course_code, @reg_request.reg_group_code,1).should include "DIS"
    page.course_schedule(@reg_request.course_code, @reg_request.reg_group_code,1,0).should match /Th 11:00 - 11:50(\s+)LEF 1222/
  end
end

Then /^I? ?undo the drop action$/ do
  on RegistrationCart do |page|
    @reg_request.undo_remove_from_cart
  end
end

And /^I? ?view my registration cart$/ do
  visit RegistrationCart do |page|
    term_descr = "Spring 2012"
    page.menu_button.wait_until_present
    page.menu
    page.wait_until {page.term_select.include? term_descr }
    page.select_term term_descr
    page.menu
  end
end


Given /^I have registered for an? (\w+) course$/ do |subj|
  steps %{
    When I add an #{subj} course offering to my registration cart
    And I register for the course
    And I view my schedule
    Then the course is present in my schedule
  }
end

Given /^I have registered for a course having multiple credit options$/ do
  @reg_request = make RegistrationRequest, :student_id=>"student",
                      :term_code=>"201201",
                      :term_descr=>"Spring 2012",
                      :course_code=>"CHEM399B",
                      :reg_group_code=>"1001"
  @reg_request.create

  steps %{
    Then the course is present in my cart
    And I register for the course
    And I view my schedule
    Then the course is present in my schedule
  }
end

Then /^the course is present in my schedule, with the updated options$/ do
  on StudentSchedule do |page|
    page.course_info_div(@reg_request.course_code,@reg_request.reg_group_code).wait_until_present
    sleep 1
    page.course_info(@reg_request.course_code, @reg_request.reg_group_code).should include "#{@reg_request.course_options.credit_option} credits"
    unless @reg_request.course_options.grading_option == "Letter"
      page.grading_option_badge(@reg_request.course_code, @reg_request.reg_group_code).wait_until_present
      page.grading_option(@reg_request.course_code, @reg_request.reg_group_code).should include "#{@reg_request.course_options.grading_option}"
    end
  end
end

When /^I register as (\w+) for a course offering with a seat capacity of one$/ do |user|
  @reg_request = make RegistrationRequest, :student_id=>user,
                      :term_code=>"201208",
                      :term_descr=>"Fall 2012",
                      :course_code=>"ENGL101",
                      :reg_group_code=>"1009",
                      :course_has_options=>false
  @reg_request.create
  @reg_request.register
  sleep 3
end

Then /^there is a message indicating that the course is full$/  do
  on RegistrationCart do |page|
    page_status = page.result_status(@reg_request.course_code,@reg_request.reg_group_code).downcase
    page_status.should include "no seats available"
  end
end

Then /^I can view the number of courses and credits I am registered for in my registration cart$/ do
  on RegistrationCart do |page|
    sleep 1
    @updated_cart_course_count = page.credit_count_title.text.match('(\d*) course')[1].to_i
    @updated_cart_course_count.should == (@orig_cart_course_count + 1)
    @updated_cart_credit_count = page.credit_count_title.text.match('\((.*) credit')[1].to_f
    p @reg_request
    @updated_cart_credit_count.should == (@orig_cart_credit_count + @reg_request.course_options.credit_option.to_f)
    
    cart_schedule_counts = page.schedule_counts.text
    @cart_reg_course_count = cart_schedule_counts.match('for (\d*) course')[1].to_i
    @cart_reg_credit_count = cart_schedule_counts.match('\((.*) credit')[1].to_f
  end
end

Then /^the number of courses and credits I am registered for is correctly updated in my registration cart$/ do
  on RegistrationCart do |page|
    page.schedule_counts.text.match('for (\d*) course')[1].to_i.should == (@cart_reg_course_count + @updated_cart_course_count)
    page.schedule_counts.text.match('\((.*) credit')[1].to_f.should == (@cart_reg_credit_count + @updated_cart_credit_count)
  end
end

Then /^the number of courses and credits I am registered for is correctly updated in my schedule ?(after the drop)?$/ do  |drop|
  on StudentSchedule do |page|
    expected_count = @cart_reg_credit_count + @updated_cart_credit_count
    if drop == "after the drop"
      credits_to_drop = @reg_request.course_options.credit_option
      expected_count -= credits_to_drop.to_f
    end
    sleep 1
    page.reg_credit_count.match('(.*) credits')[1].to_f.should == expected_count
  end
end

Then /^I log out from student registration$/ do
  on RegisterForCourseBase do |page|
    page.menu
    page.logout_button.wait_until_present
    page.logout
  end
end