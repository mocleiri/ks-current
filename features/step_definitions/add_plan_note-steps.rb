
Given /^there are courses listed in the search list$/ do
    @course_offering = make CourseOffering,  :planned_term=>"2014Spring", :course_code => "ENGL206"
    @course_offering.course_search
end

When /^I add the course with notes and term to myplan$/ do
    @course_offering.select_add_to_plan
end


Then /^the course should appear under current term with updated notes$/ do
  on CoursePlannerPage do |page|
    sleep 2
     page.close_popup.wait_until_present(3)
     page.notes_content.should == @course_offering.notes
     page.close_popup.wait_until_present
     page.close_popup_click
     sleep 2
     @course_offering.delete_item
  end
end

Given /^I work on scheduled planner$/ do
     @course_offering = make CourseOffering, :planned_term=>"2014Spring",:course_code => "ENGL206"
     @course_offering.add_course_to_future_term
end


 When /^I edit the notes of a course under a current term$/ do
     @course_offering = make CourseOffering,  :planned_term=>"2014Spring",:course_code => "ENGL206"
     @course_offering.edit_plan_item
 end



Then  /^the course details with notes are displayed under view summary$/  do
     @course_offering.verify_course_myplan
end

