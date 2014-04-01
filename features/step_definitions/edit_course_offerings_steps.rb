When /^I search course offerings by subject code$/ do
  @course_offering = make CourseOffering, :course=>"CHEM132"
  @course_offering.search_by_subjectcode
end

Then /^I view the course offering details$/ do
  @course_offering.view_course_details
  on CourseOfferingInquiry do |page|
    page.close
  end
end

When /^I can edit the course offering$/ do
  @course_offering.manage
  @course_offering.edit :defer_save => true
end

When /^I can return to search using the cancel button$/ do
  on(CourseOfferingCreateEdit).cancel
end

When /^I edit a course offering with multiple format types$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :course=>"ENGL271")
  existing_delivery_format =  make DeliveryFormat,
                                   :format=>"Lecture Only",
                                   :grade_format => "Course Offering",
                                   :final_exam_activity => "Lecture"
  @course_offering.delivery_format_list[0] = existing_delivery_format
  @course_offering.manage
  @course_offering.edit
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

When /^I edit a course offering with multiple (\w+) options$/ do |opt|
  if opt=="grading" then
    @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :course=>"ENGL899", :term => "201505")
  else
    @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :course=>"CHEM399A", :pass_fail_flag=>true, :audit_flag=>true, :credit_type => "multiple")
  end
  @course_offering.manage
end

When /^I edit a course offering with multiple delivery format types$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :course=>"ENGL222")
  existing_delivery_format =  make DeliveryFormat,
                                   :format=>"Lecture/Discussion",
                                   :grade_format => "Course Offering",
                                   :final_exam_activity => "Lecture"
  @course_offering.delivery_format_list[0] = existing_delivery_format
  @course_offering.manage
end

When /^I select a final exam type of "([^"]*)"$/ do |final_option|
     @course_offering.edit :final_exam_type => final_option, :defer_save => true
end

And /^I clear the registration options$/ do
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

Then /^after I submit the credit options are changed$/ do
  @course_offering.search_by_subjectcode
  @course_offering.view_course_details
  on CourseOfferingInquiry do  |page|
    page.course_credit_count.should == @course_offering.fixed_credit_count
    page.close
  end
end

Then /^after I submit the credit values are changed$/ do
  @course_offering.search_by_subjectcode
  @course_offering.view_course_details

  on CourseOfferingInquiry do  |page|
    page.course_credit_count.should == @course_offering.formatted_multiple_credits_list
    page.close
  end
end

When /^I change the delivery format options$/ do
  updated_delivery_format = []
  updated_delivery_format[0] =  make DeliveryFormat,
                                   :format=>"Lecture Only",
                                   :grade_format => "Lecture",
                                   :final_exam_activity => "Lecture"

  @course_offering.edit :delivery_format_list => updated_delivery_format, :start_edit => false
end

And /^I add a delivery format option$/ do
  delivery_format = make DeliveryFormat,
                         :format => "Lecture",
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

And /^I modify a delivery format option$/ do
  updated_delivery_format = []
  updated_delivery_format[0] =  make DeliveryFormat,
                                     :format=>"Lecture/Discussion",
                                     :grade_format => "Lecture",
                                     :final_exam_activity => "Lecture"

  @course_offering.edit :delivery_format_list => updated_delivery_format
end

Then /^I delete the added delivery format option$/ do
  @course_offering.manage
  @course_offering.edit :defer_save => true
  @course_offering.delete_delivery_format("Lecture Only")
  @course_offering.save
end


Then /^after I submit the course offering is updated$/ do
  #validate the success-growl is being shown
  on ManageCourseOfferings do |page|
    page.growl_text.should include "#{@course_offering.course} was successfully updated"
  end

  @course_offering.search_by_subjectcode
  @course_offering.view_course_details

  on CourseOfferingInquiry do  |page|
    #page.registration_options.should == @course_offering.reg_options
    page.final_exam_type.should == @course_offering.final_exam_type
    page.waitlist_state.should == @course_offering.waitlist
    page.honors_flag.should == @course_offering.honors_flag
    page.close
  end
end

Then /^after I submit the course offering exam options and delivery format are updated$/ do
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

Then /^after I submit the registration options are changed$/ do
  @course_offering.search_by_subjectcode
  @course_offering.view_course_details
  on CourseOfferingInquiry do  |page|
    page.registration_options.should == @course_offering.reg_options
    page.close
  end
end

Then /^after I submit the delivery formats are updated$/ do
  @course_offering.search_by_subjectcode
       @course_offering.view_course_details
       on CourseOfferingInquiry do  |page|
         page.get_delivery_format("Lecture Only").should == "Lecture Only"
         page.get_grade_roster_level("Lecture Only").should == "Lecture"
         page.get_final_exam_activity("Lecture Only").should == "Lecture"
         page.close
  end
end

Then /^after I submit the modified delivery formats are updated$/ do
  @course_offering.search_by_subjectcode
       @course_offering.view_course_details
       on CourseOfferingInquiry do  |page|
         page.get_delivery_format("Lecture/Discussion").should == "Lecture/Discussion"
         page.get_grade_roster_level("Lecture/Discussion").should == "Lecture"
         #page.get_final_exam_activity("Lecture/Discussion").should == "Lecture"
         page.close
  end
end

Then /^after I submit the added delivery format is not present$/ do
  @course_offering.search_by_subjectcode
  @course_offering.view_course_details
  on CourseOfferingInquiry do  |page|
    page.delivery_format_row("Lecture Only").should == nil
    page.close
  end

end

Then /^I edit the same course offering$/ do
  @course_offering.manage
  @course_offering.edit :defer_save => true
end

When /^I edit a course offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :course=>"CHEM132")
  @course_offering.manage
  @course_offering.edit :defer_save => true
end

When /^I add an affiliated person$/ do
  personnel_list = {}
  personnel_list[0] = make PersonnelObject, :id=> "admin", :affiliation =>"Instructor"

  @course_offering.edit :affiliated_person_list => personnel_list, :start_edit => false
end

Then /^the changes of the affiliated person are persisted$/ do
  @course_offering.manage
  @course_offering.edit :defer_save => true
  on CourseOfferingCreateEdit do |page|
    page.personnel_id.value.should == "admin"
    page.personnel_name.value.should == "admin, admin"
    page.personnel_affiliation.value.should == "kuali.lpr.type.instructor.main"
  end
end

When /^I (activate|deactivate) the wait list$/ do |activate|
    @course_offering.edit :waitlist => true, :start_edit => false
end

When /^I add an administering organization and activate the honors flag$/ do
 organization_list = {}
 organization_list[0] = make AffiliatedOrg

 @course_offering.edit  :honors_flag => "YES", :affiliated_org_list=> organization_list, :start_edit => false

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

When /^I (set|clear) the Honors Course selection$/ do |shouldSetHonorsCourse|
  honors_flag = "NO"
  if shouldSetHonorsCourse == "set"
    honors_flag = "YES"
  end
  @course_offering.edit :honors_flag => honors_flag, :start_edit => false,  :defer_save => true
end

And /^I save the changes and remain on the Edit CO page$/ do
  on(CourseOfferingCreateEdit).save_progress
end

And /^I jump to (the previous|the next|an arbitrary) CO while (saving|not saving) changes$/ do |coDirection, shouldSaveChanges|
  case coDirection
    when "the previous"
      on(CourseOfferingCreateEdit).edit_previous_co
    when "the next"
      on(CourseOfferingCreateEdit).edit_next_co
    else # nav to an arbitrary CO via the drop-down selector
      on(CourseOfferingCreateEdit).edit_arbitrary_co
  end

  # whether changes should be saved
  if shouldSaveChanges == "saving"
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


When /^I? ?change the grading option$/ do
  @course_offering.edit :grade_options => "Satisfactory"
end

Then /^the grading option is changed$/ do
  @course_offering.view_course_details
  on CourseOfferingInquiry do |page|
    page.grading_options.should include @course_offering.grade_options
    page.close
  end
end