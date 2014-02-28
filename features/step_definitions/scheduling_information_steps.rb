When /^I add requested scheduling information to an activity offering$/ do
  course_offering = make CourseOffering
  course_offering.manage
  @activity_offering = create ActivityOfferingObject, :requested_scheduling_information_list => {"default"=> (make SchedulingInformation)}
end

Then /^actual scheduling information are created with the activity offering$/ do
  step "the activity offering is updated when saved"
end

And /^I confirm that the activity offering is changed to "(.*?)"$/ do |aoState|
  @course_offering = make CourseOffering
  @course_offering.manage
  on ManageCourseOfferings do |page|
    page.ao_status(@activity_offering.code).should == aoState
  end

end