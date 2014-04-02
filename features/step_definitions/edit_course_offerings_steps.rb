When /^I edit a course offering with the waitlists active option selected$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :course=>"ENGL271")
end

When /^I can return to manage course offering using the cancel button$/ do
  on(CourseOfferingCreateEdit).cancel
  on(ManageCourseOfferings).course_title.should == @course_offering.course
end

When /^I edit a course offering with multiple format types$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :course=>"ENGL271")
  existing_delivery_format =  make DeliveryFormat,
                                   :format=>"Lecture Only",
                                   :grade_format => "Lecture",
                                   :final_exam_activity => "Lecture"
  @course_offering.delivery_format_list[0] = existing_delivery_format
end

When /^I edit a course offering with 2 format types$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :course=>"ENGL271")
  existing_delivery_format =  make DeliveryFormat,
                                   :format=>"Lecture Only",
                                   :grade_format => "Course Offering",
                                   :final_exam_activity => "Lecture"
  @course_offering.delivery_format_list[0] = existing_delivery_format
  @course_offering.manage
end

When /^I edit a course offering with multiple registration options and credit types$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :course=>"CHEM399A")
end

When /^I edit a course offering with multiple grading options$/ do
    @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :course=>"ENGL899", :term => "201205")
end

And /^I clear the existing registration options$/ do
  @course_offering.edit :pass_fail_flag => false, :audit_flag => false
end

When /^I change the credit type from multiple to fixed$/ do
  @course_offering.edit :credit_type => "fixed", :defer_save => true
end

And /^I change the number of credits$/ do
  @course_offering.edit :fixed_credit_count => "2.5", :start_edit => false
end

And /^I change the multiple credit values$/ do
  credit_selections = {"1.0" => true, "1.5" => false, "2.0" => true, "2.5" => false, "3.0" => true}
  @course_offering.edit :multiple_credit_list => credit_selections
end

Then /^after I update the credit options are changed$/ do
  @course_offering.search_by_subjectcode
  @course_offering.view_course_details
  on CourseOfferingInquiry do  |page|
    page.course_credit_count.should == @course_offering.fixed_credit_count
    page.close
  end
end

Then /^after I update the credit values are changed$/ do
  @course_offering.search_by_subjectcode
  @course_offering.view_course_details

  on CourseOfferingInquiry do  |page|
    page.course_credit_count.should == @course_offering.formatted_multiple_credits_list
    page.close
  end
end

When /^I change the grade roster level value$/ do
  updated_delivery_format = []
  updated_delivery_format[0] =  make DeliveryFormat,
                                   :format=>"Lecture Only",
                                   :grade_format => "Lecture",
                                   :final_exam_activity => "Lecture"

  @course_offering.edit :delivery_format_list => updated_delivery_format
end

And /^I add a delivery format option$/ do
  delivery_format = make DeliveryFormat,
                         :format => "Discussion/Lecture",
                         :grade_format => "Lecture",
                         :final_exam_activity => "Lecture"
  @course_offering.add_delivery_format delivery_format
  @course_offering.save
end

And /^I add a delivery format option of Discussion Lecture$/ do
  delivery_format = make DeliveryFormat,
                         :format => "Discussion/Lecture",
                         :grade_format => "Course Offering",
                         :final_exam_activity => "Lecture"
  @course_offering.add_delivery_format delivery_format
  @course_offering.save
end

Then /^I delete the added delivery format offering$/ do
  @course_offering.manage
  @course_offering.edit :defer_save => true
  @course_offering.delete_delivery_format("Discussion/Lecture")
  @course_offering.save
end

Then /^the honors flag (is|is not) persisted when I view the Course Offering details$/ do |persisted|
  expect_persisted = persisted == 'is'

  @course_offering.search_by_subjectcode
  @course_offering.view_course_details

  on CourseOfferingInquiry do  |page|
    page.honors_flag.should == expect_persisted
  end
end

Then /^after I update the course offering the waitlist option is updated$/ do
  #validate the success-growl is being shown
  on ManageCourseOfferings do |page|
    page.growl_text.should include "#{@course_offering.course} was successfully updated"
  end

  @course_offering.search_by_subjectcode
  @course_offering.view_course_details

  on CourseOfferingInquiry do  |page|
    page.waitlist_state.should == @course_offering.waitlist
    page.close
  end
end

Then /^after I update the course offering exam options and delivery format are updated$/ do
  on ManageCourseOfferings do |page|
    page.growl_text.should include "#{@course_offering.course} was successfully updated"
  end

  @course_offering.search_by_subjectcode
  @course_offering.view_course_details

  on CourseOfferingInquiry do  |page|
    page.final_exam_type.should == @course_offering.final_exam_type

    @course_offering.delivery_format_list.each do |del_format|
      page.get_grade_roster_level(del_format.format).should == del_format.grade_format
      page.get_final_exam_activity(del_format.format).should == del_format.final_exam_activity
    end

    page.close
  end
end

Then /^after I update the new delivery format offering is present$/ do
  on ManageCourseOfferings do |page|
    page.growl_text.should include "#{@course_offering.course} was successfully updated"
  end

  @course_offering.view_course_details

  on CourseOfferingInquiry do  |page|
    @course_offering.delivery_format_list.each do |del_format|
      page.get_grade_roster_level(del_format.format).should == del_format.grade_format
      page.get_final_exam_activity(del_format.format).should == del_format.final_exam_activity
    end
    page.close
  end
end

Then /^after I update the registration options are changed$/ do
  @course_offering.search_by_subjectcode
  @course_offering.view_course_details
  on CourseOfferingInquiry do  |page|
    page.registration_options.should == @course_offering.reg_options
    page.close
  end
end

Then /^after I update the grade roster level reflects the changes$/ do
  @course_offering.search_by_subjectcode
       @course_offering.view_course_details
       on CourseOfferingInquiry do  |page|
         page.get_delivery_format("Lecture Only").should == "Lecture Only"
         page.get_grade_roster_level("Lecture Only").should == "Lecture"
         page.get_final_exam_activity("Lecture Only").should == "Lecture"
         page.close
  end
end

Then /^after I update the added delivery format offering is not present$/ do
  @course_offering.search_by_subjectcode
  @course_offering.view_course_details
  on CourseOfferingInquiry do  |page|
    page.delivery_format_row("Discussion/Lecture").should == nil
    page.close
  end

end

Then /^I edit the same course offering$/ do
  @course_offering.manage
#edit is actually started in subsequent step
end

When /^I edit a course offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :course=>"CHEM132")
  @course_offering.edit :defer_save => true
end

When /^I add an entry to the personnel section$/ do
  personnel_list = {}
  personnel_list[0] = make PersonnelObject, :id=> "admin", :affiliation =>"Instructor"

  @course_offering.edit :affiliated_person_list => personnel_list, :start_edit => false
end

Then /^after I update the course offering the new personnel is present$/ do
  @course_offering.manage
  @course_offering.edit :defer_save => true
  on CourseOfferingCreateEdit do |page|
    page.personnel_id.value.should == "admin"
    page.personnel_name.value.should == "admin, admin"
    page.personnel_affiliation.value.should == "kuali.lpr.type.instructor.main"
  end
end

Then /^after I update the course offering the new administering organization is present$/ do
  @course_offering.manage
  @course_offering.edit :defer_save => true
  on CourseOfferingCreateEdit do |page|
    page.get_org_name('ORGID-BIOL').should == 'Biology'
  end
end


When /^I (activate|deactivate) waitlists$/ do |activate|
  if activate == activate
    @course_offering.edit :waitlist => true
  else
    @course_offering.edit :waitlist => false
  end
end

When /^I add an administering organization to the course offering$/ do
 organization_list = {}
 organization_list[0] = make AffiliatedOrg

 @course_offering.edit :affiliated_org_list=> organization_list, :start_edit => false

end

When /^I manually change a given soc-state to "(Open|Publishing|In Progress)"$/ do |newSocState|
  @manage_soc = make ManageSoc, :term_code => Rollover::SOC_STATES_SOURCE_TERM
  @manage_soc.perform_manual_soc_state_change(newSocState)
end

Then /^I verify that I "(can|cannot)" manage course offerings$/ do |can_manage|
  course_offering = make CourseOffering, :term => @manage_soc.term_code
  course_offering.search_by_subjectcode

  case can_manage
    when "cannot"
      expected_errMsg = "Access to course offerings is not permitted while this term's Set of Courses (SOC) is"
      on(ManageCourseOfferings).first_msg.should match /.*#{Regexp.escape(expected_errMsg)}.*/
    else  # "can"
      on(ManageCourseOfferings).info_list.should_not be_present   # an error-message should not be displayed
  end
end

When /^I (set|clear) the honors flag option$/ do |shouldSetHonorsCourse|
  honors_flag = shouldSetHonorsCourse == "set"
  @course_offering.edit :honors_flag => honors_flag, :start_edit => false,  :defer_save => true
end

And /^I save progress and remain on the Edit Course Offering page$/ do
  on(CourseOfferingCreateEdit).save_progress
end

And /^I navigate to (the previous|the next|a different) Course Offering and select (save and continue|continue without saving) when prompted$/ do |direction, save_changes|
  case direction
    when "the previous"
      on(CourseOfferingCreateEdit).edit_previous_co
    when "the next"
      on(CourseOfferingCreateEdit).edit_next_co
    else # nav to an arbitrary CO via the drop-down selector
      on(CourseOfferingCreateEdit).edit_arbitrary_co
  end

  shouldSaveChanges =  save_changes == 'save and continue'
  # whether changes should be saved
  if shouldSaveChanges
    on(CourseOfferingCreateEdit).navigation_save_and_continue
  else
    on(CourseOfferingCreateEdit).navigation_cancel_and_continue
  end

end

Then /^the Honors Course setting is (set|not set)$/ do |shouldHonorsCourseBeSet|
  honors_flag = "NO"
  if shouldHonorsCourseBeSet == "set"
    honors_flag = "YES"
  end

  @course_offering.view_course_details
  on CourseOfferingInquiry do |page|
    page.honors_flag.should == honors_flag
    page.close
  end
end


When /^I? ?change the grading options$/ do
  @course_offering.edit :grade_options => "Satisfactory"
end

Then /^after I update the credit type, credit value and registration options reflect the changes$/ do
  @course_offering.view_course_details
  on CourseOfferingInquiry do |page|
    #page.credit_type.should == @course_offering.credit_type
    page.course_credit_count.should == @course_offering.fixed_credit_count
    page.registration_options.should == @course_offering.reg_options
    page.close
  end
end

Then /^the grading options are changed$/ do
  @course_offering.view_course_details
  on CourseOfferingInquiry do |page|
    page.grading_options.should include @course_offering.grade_options
    page.close
  end
end