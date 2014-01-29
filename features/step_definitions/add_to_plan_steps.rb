
Given /^there are courses listed in the search list$/ do
    @course_offering = make CourseOffering,  :planned_term=>"2014Spring", :course_code => "ENGL206"
    @course_offering.course_search
end

When /^I add the course with notes and term to myplan$/ do
    @course_offering.select_add_to_plan
   end


Then /^the course should appear under current term with updated notes$/ do
  on CoursePlannerPage do |page|
     page.close_popup.wait_until_present
     page.notes_content.should == @course_offering.notes
     page.close_popup.wait_until_present
     page.close_popup_click
     sleep 2
  end
  @course_offering.remove_code_from_term
end

Given /^I work on scheduled planner$/ do
     @course_offering = make CourseOffering, :planned_term=>"2014Spring",:course_code => "ENGL206"
     @course_offering.add_course_to_term
end


 When /^I edit the notes of a course under a current term$/ do
     @course_offering = make CourseOffering,  :planned_term=>"2014Spring",:course_code => "ENGL206"
     @course_offering.edit_plan_item
 end



Then  /^the course with notes appears under the term on the planner$/ do
   on CoursePlannerPage do |page|
        page.course_code_term(planned_term="2014Spring", course_code= "ENGL206") != nil?

       #*********** Checking whether the information icon exists?************

     if page.info_icon.exists?
        page.course_code_term_click(planned_term="2014Spring", course_code= "ENGL206")
        page.view_course_summary_click
        page.close_popup.wait_until_present
        page.notes_content.should == @course_offering.notes
        page.close_popup_click
     end
     end
     @course_offering.remove_code_from_term

end
