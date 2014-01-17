When /^I add a fixed credit course with notes to current term$/ do
  @course_offering = make CourseOffering, :course_code => "BSCI430"
  @course_offering.add_course_to_current_term
end



When /^I add a variable credit course with notes to a future term$/ do
  @course_offering = make CourseOffering, :course_code => "ENGL388", :credit => 5
  @course_offering.add_course_to_future_term
end



And /^I view the details of the added course from edit plan item$/ do
  @course_offering.edit_plan_item
end


Then /^fixed credit and note details are displayed under the current term$/ do
  on CoursePlannerPage do |page|
    page.view_notes_popover.should == @course_offering.notes
    page.edit_plan_popover_cancel
    #sleep 2
    #fix this later
    #page.course_code_current_term_credit.should == "#{@course_offering.credit}"
  end
end


Then /^the course should be added to the current term$/ do
  on CoursePlannerPage do |page|
    page.growl_text.should include "#{@course_offering.course_code} was successfully added to your plan"
    #sleep 2
  end
end


Then /^the course should be added to the future term$/ do
  on CoursePlannerPage do |page|
    page.growl_text.should include "#{@course_offering.course_code} was successfully added to your plan"
    sleep 2
  end
end


Then /^variable credit and note details are displayed under the future term$/ do
  on CoursePlannerPage do |page|
    page.view_notes_popover.should == @course_offering.notes
    page.view_variable_credit_popover.should == "#{@course_offering.credit}"
    page.edit_plan_popover_cancel
  end
end