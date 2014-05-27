When /^I add a fixed credit course with notes to current term$/ do
  @course_search_result = make CourseSearchResults, :course_code => "BSCI430"
  @course_search_result.add_course_to_term
end



When /^I add a variable credit course with notes to a future term$/ do
  @course_search_result = make CourseSearchResults, :course_code => "ENGL388", :credit => 5, :planned_term => "2014Summer1"
  @course_search_result.add_course_to_term
end



And /^I view the details of the added course from edit plan item$/ do
  @course_search_result.edit_plan_item_verify_notes
end


Then /^fixed credit and note details are displayed under the current term$/ do
  on CoursePlannerPage do |page|
    page.edit_plan_cancel_link.wait_until_present
    page.view_notes_popover.should == @course_search_result.notes
    page.course_code_current_term_credit.should == "#{@course_search_result.credit}"
    page.edit_plan_cancel
    end
end


Then /^the course should be added to the current term$/ do
  on CoursePlannerPage do |page|
    page.growl_text.should include "#{@course_search_result.course_code} was successfully added to your plan"
  end
end


Then /^the course should be added to the future term$/ do
  on CoursePlannerPage do |page|
    page.growl_text.should include "#{@course_search_result.course_code} was successfully added to your plan"
  end
end


Then /^variable credit and note details are displayed under the future term$/ do
  on CoursePlannerPage do |page|
    page.edit_plan_cancel_link.wait_until_present
    page.view_notes_popover.should == @course_search_result.notes
    page.view_variable_credit_popover.should == "#{@course_search_result.credit}"
    page.edit_plan_cancel
  end
end