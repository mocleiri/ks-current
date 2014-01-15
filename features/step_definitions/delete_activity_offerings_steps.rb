When /^I create a Course Offering and add (\d+) Activity Offerings$/ do |number_of_aos_to_create|
  @course_offering = create CourseOffering, :create_by_copy => (make CourseOffering, :course=>"CHEM401")
  @course_offering.manage_and_init
  @orig_number_aos = @course_offering.get_ao_list.count
  @activity_offering = make ActivityOffering, :format => "Lecture Only"
  @course_offering.create_list_aos :ao_object => @activity_offering, :number_aos_to_create=>number_of_aos_to_create
  @course_offering.manage_and_init
  @total_number =  @course_offering.get_ao_list.count
end



And /^I delete (\d+) Activity Offerings$/ do |number_of_aos_to_delete|
  @aos_to_delete = []
  @course_offering.get_ao_list[-(number_of_aos_to_delete.to_i)..-1].each do |ao|
    @aos_to_delete << ao.code
  end
  @course_offering.delete_ao_list :code_list => @aos_to_delete
end

Then /^the Course Offering should contain (\d+) additional Activity Offering$/ do |nbr_addl_aos|
  @course_offering.manage
  on ManageCourseOfferings do |page|
    page.codes_list.length.should == @orig_number_aos + nbr_addl_aos.to_i
  end
end

Then /^the (\d+) AOs are Successfully deleted$/ do |number_of_aos_deleted|
  @course_offering.manage
  on ManageCourseOfferings do |page|
    page.codes_list.length.should == @total_number.to_i - number_of_aos_deleted.to_i
    for code in @aos_to_delete
      page.codes_list.should_not include code
    end
  end
end


When /^I designate a valid term and cross\-listed Course Offering Code$/ do
  @course_offering = create CourseOffering, :create_by_copy => (make CourseOffering, :course => "ENGL250")
end

And /^I? ?add an Activity Offering to the cross-listed CO$/ do
  @course_offering.manage_and_init
  @orig_total = @course_offering.get_ao_list.count
  @course_offering.create_ao  :ao_obj => (make ActivityOffering, :format => "Lecture Only"), :navigate_to_page => false
end

Then /^the AO count reflects the added AO$/ do
  @course_offering.manage
  on ManageCourseOfferings do |page|
    page.codes_list.length.should == @orig_total + 1
  end
end

And /^I delete the added AO$/ do
  @total_number = @course_offering.get_ao_list.count
  @deleted_ao_codes = [@course_offering.get_ao_list[-1].code]
  @confirm_message = @course_offering.delete_ao_list :code_list =>  @deleted_ao_codes
end

And /^I receive a warning that the course is cross-listed$/ do
  expect_result = "Crosslisted as: WMST255"
  @confirm_message.should include expect_result
end

Then /^the AO is Successfully deleted$/ do
  @course_offering.manage
  on ManageCourseOfferings do |page|
    page.codes_list.length.should == @total_number - 1
    page.codes_list.should_not include @deleted_ao_codes
  end
end
