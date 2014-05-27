Given /^There is an existing unplanned course$/ do
  navigate_to_maintenance_portal
  navigate_to_course_search_home
  @course_search_result = make CourseSearchResults,  :planned_term=>"2014Spring", :course_code => "ENGL206"
  @course_search_result.course_search_to_planner
end

When /^I search for a course from course search$/ do
    @course_search_result = make CourseSearchResults,  :planned_term=>"2014Spring", :course_code => "ENGL206"
    @course_search_result.set_search_entry
end

When /^I add the course with notes and term to myplan$/ do
    @course_search_result.select_add_to_plan
   end


Then /^the course should appear under current term with updated notes$/ do
  on CoursePlannerPage do |page|
     page.close_popup.wait_until_present
  #***************Checking  whether the edited notes is updated*********************************************************
     page.notes_content.should == @course_search_result.notes
     page.close_popup.wait_until_present
     page.close_popup_click
     sleep 2

  #********Steps to verify, whether the course code is saved along with updated notes in the planner********************
     @course_search_result.verify_saved_course_code_notes
     page.notes_content.should == @course_search_result.notes
     page.close_popup_click

  end
end

Given /^I work on scheduled planner$/ do
     @course_search_result = make CourseSearchResults, :planned_term=>"2014Spring",:course_code => "ENGL206"
     @course_search_result.add_course_to_term
end


 When /^I edit the notes of a course under a current term$/ do
     @course_search_result = make CourseSearchResults,  :planned_term=>"2014Spring",:course_code => "ENGL206"
     @course_search_result.edit_plan_item
 end



Then  /^the course with notes appears under the term on the planner$/ do
   on CoursePlannerPage do |page|
       #*********** Checking whether the information icon exists?************
        page.info_icon(@course_search_result.planned_term, @course_search_result.course_code).exists?.should == true
        page.course_code_term_click(@course_search_result.planned_term, @course_search_result.course_code)
        page.view_course_summary_click
        page.close_popup.wait_until_present
        page.notes_content.should == @course_search_result.notes
        page.close_popup_click

     end
end
