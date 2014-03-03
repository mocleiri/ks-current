When /^I add an? (\w+) course offering to my registration cart$/ do |subj|
  course_code = case
                  when subj=="BSCI" then "BSCI106"
                  when subj=="CHEM" then "CHEM231"
                  when subj=="ENGL" then "ENGL211"
                  when subj=="HIST" then "HIST111"
                  when subj=="PHYS" then "PHYS102"
                  else ""
                end
  @reg_request = make RegistrationRequest, :student_id=>"student",
                                           :term_code=>"201201",
                                           :term_descr=>"Spring 2012",
                                           :course_code=>course_code,
                                           :reg_group_code=>"1001"
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
  @reg_request.edit_course_options_in_cart
end

Then /^the course is (present|not present) in my cart$/  do |presence|
  on RegistrationCart do |page|
    if presence == "present"
      sleep 2
      page.course_title(@reg_request.course_code, @reg_request.reg_group_code).should_not be_nil
    else
      begin
        puts "User Message: #{page.user_message}"
        page.user_message.should include "#{@reg_request.course_code}(#{@reg_request.reg_group_code}) has been successfully removed from your cart"
        page.course_code(@reg_request.course_code, @reg_request.reg_group_code).present?.should be_false
      rescue Watir::Exception::UnknownObjectException
        # the course is not there: good
      end
    end
  end
end

Then /^the course is present in my cart, with the correct options$/  do
  on RegistrationCart do |page|
    page.course_info_div(@reg_request.course_code,@reg_request.reg_group_code).wait_until_present
    page.course_info(@reg_request.course_code, @reg_request.reg_group_code).should include "#{@reg_request.course_options.credit_option} credits"
    page.course_info(@reg_request.course_code, @reg_request.reg_group_code).should include "#{@reg_request.course_options.grading_option}"
    #do we need to quit or remove course?
  end
end

And /^I register for the course$/ do
  @reg_request.register
end

Then /^there is a message indicating registration submittal$/ do
  on RegistrationCart do |page|
    register_message_text = "Cart was submitted"
    begin
      page.wait_until { page.user_message.include? register_message_text }
    rescue
      raise "\"#{register_message_text}\" not found in user message"
    end
    puts "User Message: |#{page.user_message}|"
    page.user_message.should include register_message_text
  end
end

When /^I remove the course from my schedule$/ do
  @reg_request.remove_from_schedule
end

When /^I view my schedule$/ do
  on RegistrationCart do |page1|
    page1.schedule_link.click
  end
end

And /^the course is (present|not present) in my schedule$/  do |presence|
  on StudentSchedule do |page2|
    sleep 2
    if presence == "present"
      page2.course_title(@reg_request.course_code, @reg_request.reg_group_code).should_not be_nil
    else
      begin
        page2.course_code(@reg_request.course_code, @reg_request.reg_group_code).present?.should be_false
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

And /^I? ?can view the details of my selections?$/ do
  on RegistrationCart do |page|
    page.toggle_course_details(@reg_request.course_code, @reg_request.reg_group_code)
    page.wait_until { page.ao_type(@reg_request.course_code, @reg_request.reg_group_code,0) != "" }
    page.course_title(@reg_request.course_code, @reg_request.reg_group_code).should == "Organic Chemistry I"
    page.course_info(@reg_request.course_code, @reg_request.reg_group_code).should include "#{@reg_request.course_options.credit_option[0]} credits"
    page.course_info(@reg_request.course_code, @reg_request.reg_group_code).should include "#{@reg_request.course_options.grading_option}"
    page.ao_type(@reg_request.course_code, @reg_request.reg_group_code,0).should include "DIS"
    page.course_schedule(@reg_request.course_code, @reg_request.reg_group_code,0,0).should include "M 3:00 pm - 3:50 pm CHM"
    page.ao_type(@reg_request.course_code, @reg_request.reg_group_code,1).should include "LEC"
    page.course_schedule(@reg_request.course_code, @reg_request.reg_group_code,1,0).should include "TH 11:00 am - 12:15 pm EGR"
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
    # wait in case list has not loaded yet
    page.wait_until {page.term_select.include? term_descr }
    page.select_term term_descr
  end
end
