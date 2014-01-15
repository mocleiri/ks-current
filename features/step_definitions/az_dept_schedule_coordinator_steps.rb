When /^I attempt to edit an activity offering for a course offering in my admin org/ do
  @course_offering = make CourseOffering, :course=>"ENGL362" , :term=>@term_for_test
  @course_offering.manage
  @course_offering.edit_ao(:ao_code => "A")

  on ActivityOfferingMaintenance do |page|
    page.submit_button.enabled?.should == true
  end

end

Then /^I do not have access to add or edit seat pools$/ do
  on ActivityOfferingMaintenance do |page|
    page.add_pool_element.present?.should be_false
  end
end

Then /^I do not have access to seat pools$/ do
  on ActivityOfferingMaintenance do |page|
    page.seat_pools_table.button.present?.should be_false
  end
end

Then /^I do not have access to edit the course offering$/ do
  on ManageCourseOfferings do |page|
    page.edit_course_offering_link.present?.should be_false
  end
end

Then /^I do not have access to edit the listed course offering$/ do
  on ManageCourseOfferingList do |page|
    page.edit_link(@course_offering.course).present?.should be_false
  end
end

Then /^I do not have access to copy the listed course offering$/ do
  on ManageCourseOfferingList do |page|
    page.copy_link(@course_offering.course).present?.should be_false
  end
end

Then /^I do not have access to add course offerings$/ do
  on ManageCourseOfferingList do |page|
    page.create_course_offering_button.enabled?.should be_false
  end
end

Then /^an authorization error is displayed when I attempt to create the course offering$/ do
  on CreateCourseOffering do |page|
    page.choose_from_catalog
    page.continue
  end

  on CreateCourseOffering do |page|
    page.auth_error.present?.should == true
    page.catalogue_course_code.set ""
    page.target_term.set ""
    page.cancel
  end

  #on ErrorPage do |page|
  #  page.error_401.should == true
  #end
end

When /^I manage a course offering for a subject code not in my admin org$/ do
  @term_for_test = Rollover::OPEN_SOC_TERM unless @term_for_test != nil
  @course_offering = make CourseOffering, :course=>"CHEM611", :term=>@term_for_test
  @course_offering.manage
  @activity_offering = make ActivityOffering, :code=>"A"
end

When /^I manage the course offering$/ do
     @course_offering.manage
end

When /^I list the course offerings for that subject code$/ do
  @course_offering.search_by_subjectcode
end

When /^I manage course offerings for a subject code not in my admin org$/ do
  @term_for_test = Rollover::OPEN_SOC_TERM unless @term_for_test != nil
  @course_offering = make CourseOffering, :course=>"CHEM611", :term=>@term_for_test
  @course_offering.search_by_subjectcode
end

When /^I manage course offerings for a subject code in my admin org$/ do
  @term_for_test = Rollover::OPEN_SOC_TERM unless @term_for_test != nil
  @course_offering = make CourseOffering, :course=>"ENGL222", :term=>@term_for_test
  @course_offering.search_by_subjectcode
end

When /^I manage a course offering in my admin org$/ do
  @term_for_test = Rollover::OPEN_SOC_TERM unless @term_for_test != nil
  @course_offering = make CourseOffering, :course=>"ENGL245", :term=>@term_for_test
  @course_offering.manage
  @activity_offering = make ActivityOffering, :code=>"A"
end

Then /^I have access to view course offering details$/ do
  on ManageCourseOfferingList do |page|
    page.view_course_offering_link(@course_offering.course).present?.should be_true
  end
end

Then /^I have access to manage course offerings$/ do
  on ManageCourseOfferingList do |page|
    page.manage_link(@course_offering.course).present?.should be_true
  end
end

Then /^I have access to add new course offerings$/ do
  on ManageCourseOfferingList do |page|
    page.create_course_offering_button.enabled?.should == true
  end
end

Then /^I have access to approve course offerings for scheduling$/ do
  on ManageCourseOfferingList do |page|
    @course_offering.check_course_in_status(:co_status => CourseOffering::DRAFT_STATUS, :select_co => true )
    page.approve_course_offering_button.enabled?.should be_true
    page.deselect_cos([@course_offering.course])
  end
end

Then /^I do not have access to approve course offerings for scheduling$/ do
  on ManageCourseOfferingList do |page|
    page.select_cos([@course_offering.course])
    page.approve_course_offering_button.enabled?.should be_false
    page.deselect_cos([@course_offering.course])
  end
end

Then /^I have access to delete the listed course offerings?$/ do
  on ManageCourseOfferingList do |page|
    page.select_cos([@course_offering.course])
    page.delete_cos_button.enabled?.should == true
    page.deselect_cos([@course_offering.course])
  end
end

Then /^I have access to edit the listed course offerings?$/ do
  on ManageCourseOfferingList do |page|
    page.edit_link(@course_offering.course).present?.should be_true
  end
end

Then /^I have access to copy the listed course offerings?$/ do
  on ManageCourseOfferingList do |page|
    page.copy_link(@course_offering.course).present?.should be_true
  end
end

Then /^I have access to delete the course offering$/ do
  on ManageCourseOfferings do |page|
    page.delete_course_offering_link.present?.should be_true
    page.delete_course_offering_link.attribute_value("class").should_not match /disabled/
  end
end

Then /^I have access to view all registration groups$/ do
  on ManageCourseOfferings do |page|
    page.view_all_reg_groups_link.present?.should be_true
    page.view_all_reg_groups_link.attribute_value("class").should_not match /disabled/
  end
end

Then /^I have access to manage activity offering clusters$/ do
  on ManageCourseOfferings do |page|
    page.add_cluster_button.present?.should be_true
    page.add_cluster_button.enabled?.should be_true

    page.select_ao("A")

    page.move_aos_button.present?.should be_true
    page.move_aos_button.enabled?.should be_true

    page.deselect_ao("A")

    page.rename_cluster_link(:default_cluster).present?.should be_true
    page.rename_cluster_link(:default_cluster).attribute_value("class").should_not match /disabled/

    page.remove_cluster_link(:default_cluster).present?.should be_true
    page.remove_cluster_link(:default_cluster).attribute_value("class").should_not match /disabled/

  end
end

Then /^I do not have access to manage activity offering clusters$/ do
  on ManageCourseOfferings do |page|
    page.add_cluster_button.present?.should be_true
    page.add_cluster_button.enabled?.should be_false

    page.move_aos_button.present?.should be_true
    page.move_aos_button.enabled?.should be_false

    page.rename_cluster_link(:default_cluster).present?.should be_false

    page.remove_cluster_link(:default_cluster).present?.should be_false

  end
end

Then /^I do not have access to delete the course offering$/ do
  on ManageCourseOfferings do |page|
    page.delete_course_offering_link.present?.should be_false
    #page.delete_course_offering_link.attribute_value("class").should_not match /disabled/
  end
end

Then /^I have access to edit the course offering$/ do
  on ManageCourseOfferings do |page|
    page.edit_course_offering_link.present?.should be_true
    page.edit_course_offering_link.attribute_value("class").should_not match /disabled/
  end
end

Then /^I have access to view the course offering details$/ do
  on ManageCourseOfferings do |page|
    page.view_co_details_link.present?.should be_true
    page.view_co_details_link.attribute_value("class").should_not match /disabled/
  end
end

Then /^I have access to view the activity offering details$/ do
  on ManageCourseOfferings do |page|
    page.view_activity_offering_link(@activity_offering.code).present?.should be_true
    page.view_activity_offering_link(@activity_offering.code).attribute_value("class").should_not match /disabled/
  end
end

Then /^the next, previous and list all course offering links are enabled$/ do
  on ManageCourseOfferings do |page|
    page.previous_course_link.present?.should be_true
    page.list_all_course_link.present?.should be_true
    page.next_course_link.present?.should be_true
  end
end

Then /^I have access to add a new activity offering$/ do
  on ManageCourseOfferings do |page|
    page.add_activity_button.enabled?.should be_true
  end
end

Then /^I do not have access to add a new activity offering$/ do
  on ManageCourseOfferings do |page|
    page.add_activity_button.enabled?.should be_false
  end
end

Then /^I have access to delete an activity offering$/ do
  on ManageCourseOfferings do |page|
    page.select_aos([@activity_offering.code])
    page.delete_aos_button.enabled?.should be_true
    page.deselect_aos([@activity_offering.code])
  end
end

Then /^I have access to approve an activity offering$/ do
  on ManageCourseOfferings do |page|
    page.select_aos([@activity_offering.code])
    page.approve_activity_button.enabled?.should be_true
    page.deselect_aos([@activity_offering.code])
  end
end

Then /^I do not have access to approve an activity offering$/ do
  on ManageCourseOfferings do |page|
    page.select_aos([@activity_offering.code])
    page.approve_activity_button.enabled?.should be_false
    page.deselect_aos([@activity_offering.code])
  end
end

Then /^I have access to edit an activity offering$/ do
  on ManageCourseOfferings do |page|
    page.edit_link(@activity_offering.code).present?.should be_true
    page.view_activity_offering_link(@activity_offering.code).attribute_value("class").should_not match /disabled/
  end
end

Then /^I have access to copy an activity offering$/ do
  on ManageCourseOfferings do |page|
    page.copy_link(@activity_offering.code).present?.should be_true
    page.copy_link(@activity_offering.code).attribute_value("class").should_not match /disabled/
  end
end

Then /^I do not have access to manage the course offering$/ do
  #on ManageCourseOfferingList do |page|
  #  page.target_row(@course_offering.course).link(text: "Manage").click
  #  page.loading.wait_while_present
  #end
  on ManageCourseOfferings do |page|
    page.auth_error.present?.should == true
  end
end

Then /^I do not have access to create the course offering from (catalog|existing)$/ do |create_type|
  create_from_catalog = (create_type == "catalog")
  on CreateCourseOffering do |page|
    if create_from_catalog then
      page.choose_from_catalog
    else
      page.choose_from_existing
    end
    page.continue
  end

  on CreateCourseOffering do |page|
    page.auth_error.present?.should == true
  end
end

When /^I attempt to create a course offering for a subject not in my admin org$/ do
  @course_offering = make CourseOffering, :term=> @term_for_test, :course => "CHEM132"
  @course_offering.start_create_by_search
end

When /^I attempt to create a course offering for a subject in my admin org$/ do
  @course_offering = make CourseOffering, :term=> @term_for_test, :course => "ENGL310"
  @course_offering.start_create_by_search
end

Then /^I have access to create the course offering from catalog$/ do
  on CreateCourseOffering do |page|
    page.choose_from_catalog
    page.continue
  end
  on CourseOfferingCreateEdit do |page|
    page.suffix.present?.should == true
    page.cancel
  end
end

When /^I have access to create the course from an existing offering$/ do
  on CreateCourseOffering do |page|
    page.choose_from_existing
    page.continue
  end
  on CreateCOFromExisting do |page|
    page.exclude_instructor.present?.should == true
    page.cancel
  end
end

When /^there is an? "([^"]*)" course offering in my admin org/ do |co_status|
  step "I am logged in as a Schedule Coordinator"
  @course_offering = make CourseOffering, :term=> @term_for_test, :course => "ENGL222"
  @course_offering.check_course_in_status(:co_status => co_status)
  step "I am logged in as a Department Schedule Coordinator"
end

When /^there is a "([^"]*)" course offering not in my admin org/ do |co_status|
  step "I am logged in as a Schedule Coordinator"
  @course_offering = make CourseOffering, :term=> @term_for_test, :course => "CHEM612"
  @course_offering.check_course_in_status(:co_status => co_status)
  step "I am logged in as a Department Schedule Coordinator"
end


When /^there is an? "([^"]*)" course offering present/ do |co_status|
  step "I am logged in as a Schedule Coordinator"
  @course_offering = make CourseOffering, :term=> @term_for_test, :course => "ENGL222"
  @course_offering.check_course_in_status(:co_status => co_status)
end

When /^there is an activity in "([^"]*)" status/ do |ao_status|
  @course_offering.check_activity_offering_in_status(:ao_status => ao_status)
end

When /^I have access to delete an activity offering in "([^"]*)" status for the course offering$/ do |aostate|
  @course_offering.manage_and_init
  @course_offering.attempt_ao_delete_by_status(aostate).should be_true
end

Then /^I do not have access to view the activity offerings$/ do
  on ManageCourseOfferings do |page|
    page.auth_error.present?.should == true
  end
end

When /^I edit a course offering in my admin org that has multiple credit types$/ do
  @term_for_test = Rollover::OPEN_SOC_TERM unless @term_for_test != nil
  @course_offering = make CourseOffering, :term => @term_for_test, :course=>"ENGL369D"
  @course_offering.manage
  on ManageCourseOfferings do |page|
    page.edit_course_offering
  end
end

When /^I edit an existing course offering$/ do
  @course_offering = make CourseOffering, :term => @term_for_test, :course=>"CHEM612"
  @course_offering.manage
  on ManageCourseOfferings do |page|
    page.edit_course_offering
  end
end

Then /^I have access to edit the grading options$/ do
  on CourseOfferingCreateEdit do |page|
    page.grading_option_div.radio.present?.should be_true
  end
end

Then /^I have access to edit the course code suffix$/ do
  on CourseOfferingCreateEdit do |page|
    page.suffix.present?.should be_true
  end
end

Then /^I have access to edit the registration options$/ do
  on CourseOfferingCreateEdit do |page|
    page.pass_fail_checkbox.enabled?.should be_true
  end
end

Then /^I have access to edit the credit type$/ do
  on CourseOfferingCreateEdit do |page|
    page.credit_type_option_fixed.enabled?.should be_true
  end
end

Then /^I have access to edit the delivery format type$/ do
  on CourseOfferingCreateEdit do |page|
    if page.add_format_btn.present?
      page.add_format_btn.enabled?.should be_true
      page.new_grade_roster_level_select.enabled?.should be_true
      page.new_final_exam_activity_select.enabled?.should be_true
      page.new_delivery_format_delete.enabled?.should be_true
    else #this is the case when only one format offering is possible
      page.new_delivery_format_delete.click
      page.add_format_btn.wait_until_present
      page.add_format
      page.new_format_select.select "Lecture"
      page.new_grade_roster_level_select.enabled?.should be_true
      page.new_final_exam_activity_select.enabled?.should be_true
    end

    #page.select_grade_roster_level_add.enabled?.should be_true - need to select type first
    #page.delivery_format_add_element.enabled?.should be_true - need to select type first
  end
end

Then /^I do not have access to edit the delivery format type$/ do
  on CourseOfferingCreateEdit do |page|
    page.delivery_format_add_element.present?.should be_false
    #TODO: page.select_grade_roster_level_add.present?.should be_false
    page.delivery_format_add_element.present?.should be_false
  end
end

Then /^I have access to edit the waitlists flag$/ do
  on CourseOfferingCreateEdit do |page|
    page.waitlist_checkbox.enabled?.should be_true
  end
end

Then /^I have access to edit the affiliated personnel$/ do
  on CourseOfferingCreateEdit do |page|
    page.add_personnel_button_element.enabled?.should be_true
  end
end

Then /^I have access to edit the administrating org$/ do
  on CourseOfferingCreateEdit do |page|
    page.add_org_button.enabled?.should be_true
  end
end

Then /^I have access to edit the CO honors flag$/ do
  on CourseOfferingCreateEdit do |page|
    page.honors_flag.enabled?.should be_true
  end
end

Then /^I do not have access to edit the final exam type$/ do
  on CourseOfferingCreateEdit do |page|
    page.final_exam_option_div.radio.present?.should be_false
  end
end
#Then /^I have access to edit the $/ do
#  on CourseOfferingCreateEdit do |page|
#    page..present?.should be_true
#  end
#end

Then /^I attempt to perform a rollover$/ do
  go_to_perform_rollover
end

Then /^I attempt to view rollover details$/ do
  go_to_rollover_details
end

When /^I do not have access to the page$/ do
  on ErrorPage do |page|
    page.error_401.should == true
  end
end

Then /^I do not have access to add new delivery logistics$/ do
  on ActivityOfferingMaintenance do |page|
    page.add_new_delivery_logistics_button.present?.should be_false
  end
end

Then /^I have access to edit the activity code$/ do
  on ActivityOfferingMaintenance do |page|
    page.activity_code.enabled?.should be_true
  end
end

Then /^I do not have access to edit the activity code$/ do
  on ActivityOfferingMaintenance do |page|
    page.activity_code.present?.should be_false
  end
end

Then /^I have access to edit total maximum enrollment$/ do
  on ActivityOfferingMaintenance do |page|
    page.total_maximum_enrollment.enabled?.should be_true
  end
end


Then /^I have access to add or edit affiliated personnel$/ do
  on ActivityOfferingMaintenance do |page|
    page.add_personnel_element.present?.should be_true
  end
end

Then /^I have access to edit waitlist options$/ do
  on ActivityOfferingMaintenance do |page|
    page.waitlist_checkbox.enabled?.should be_true
    page.waitlist_checkbox.set
    page.waitlist_confirmation_radio.present?.should be_true
    page.waitlist_automatic_radio.present?.should be_true
    page.waitlist_manual_radio.present?.should be_true
    page.waitlist_limit_checkbox.enabled?.should be_true
    page.waitlist_limit_checkbox.set
    page.waitlist_limit.enabled?.should be_true
    page.waitlist_allow_hold_checkbox.enabled?.should be_true
    page.waitlist_limit_checkbox.clear
  end
end

Then /^I do not have access to edit waitlist options$/ do
  on ActivityOfferingMaintenance do |page|
    page.waitlist_checkbox.present?.should be_false
    page.waitlist_confirmation_radio.present?.should be_false
    page.waitlist_automatic_radio.present?.should be_false
    page.waitlist_manual_radio.present?.should be_false
    page.waitlist_limit_checkbox.present?.should be_false
    page.waitlist_limit.present?.should be_false
    page.waitlist_allow_hold_checkbox.present?.should be_false
  end
end

Then /^I have access to view waitlist options$/ do
  on ActivityOfferingMaintenance do |page|
    page.waitlists_active?.should be_true
    page.waitlists_processing.should == "Confirmation"
    page.waitlists_max_size.should == "Unlimited"
    page.waitlists_allow_holds?.should be_true
  end
end

Then /^I have access to edit the evaluation flag$/ do
  on ActivityOfferingMaintenance do |page|
    page.requires_evaluation.enabled?.should be_true
  end
end

Then /^I have access to edit the course url$/ do
  on ActivityOfferingMaintenance do |page|
    page.course_url.present?.should be_true
  end
end

Then /^I have access to edit the honors flag$/ do
  on ActivityOfferingMaintenance do |page|
    page.honors_flag.enabled?.should be_true
  end
end

And /^Cancel the Activity Offering Edit to avoid a dirty page warning$/ do
  on ActivityOfferingMaintenance do |page|
    page.cancel
  end
end

And /^test cleanup - Cancel the Course Offering Edit$/ do
  on(CourseOfferingCreateEdit).cancel
end

Then /^I do not have access to edit activity offerings$/ do
  on ManageCourseOfferings do |page|
    page.codes_list.each do |ao_code|
      page.edit_link(ao_code).present?.should be_false
    end
  end
end

Then /^I do not have access to copy activity offerings$/ do
  on ManageCourseOfferings do |page|
    page.codes_list.each do |ao_code|
      page.copy_link(ao_code).present?.should be_false
    end
  end
end


Then /^I do not have access to select activity offerings for add, approve, delete$/ do
  on ManageCourseOfferings do |page|
    page.delete_aos_button.enabled?.should be_false
    page.approve_activity_button.enabled?.should be_false
    page.add_activity_button.enabled?.should be_false
    page.draft_activity_button.enabled?.should be_false
    page.codes_list.each do |ao_code|
      page.target_row(ao_code).checkbox.present?.should be_false
    end
  end
end

Then /^I do not have access to select the "([^"]*)" course offering for approve, delete$/ do |co_status|
  @course_offering.search_by_subjectcode
  on ManageCourseOfferingList do |page|
    page.delete_cos_button.enabled?.should be_false
    page.approve_course_offering_button.enabled?.should be_false
    #page.create_course_offering_button.enabled?.should be_false
    #page.draft_activity_button.enabled?.should be_false
    co_row = page.target_row(@course_offering.course)
    page.co_row_status(co_row).should == co_status
    co_row.checkbox.present?.should be_false
  end
end

Then /^I do not have access to select course offerings for approve, delete$/ do
  @course_offering.search_by_subjectcode
  on ManageCourseOfferingList do |page|
    page.delete_cos_button.enabled?.should be_false
    page.approve_course_offering_button.enabled?.should be_false
    #page.create_course_offering_button.enabled?.should be_false
    #page.draft_activity_button.enabled?.should be_false
    page.co_list.each do |co_code|
      page.target_row(co_code).checkbox.present?.should be_false
    end
  end
end

Then /^I do not have access to select activity offerings for delete, approve$/ do
  on ManageCourseOfferings do |page|
    cluster_div = page.target_cluster(:default_cluster)
    page.get_cluster_div_ao_rows(cluster_div).each do |ao_row|
      ao_row.cells[0].checkbox.present?.should be_false
    end
  end
end



Then /^I do not have access to select course offerings for approve$/ do
  @course_offering.search_by_subjectcode
  on ManageCourseOfferingList do |page|
    page.approve_course_offering_button.enabled?.should be_false
    page.co_list.each do |co_code|
      checkbox = page.target_row(co_code).checkbox
      if checkbox.present? then
        checkbox.set
        page.approve_course_offering_button.enabled?.should be_false
      end
    end
  end
end

#TODO - need to add check for the state of the buttons here?
Then /^I have access to select activity offerings for add, approve, delete$/ do
  on ManageCourseOfferings do |page|
    page.add_activity_button.enabled?.should be_true
    page.codes_list.each do |ao_code|
      page.target_row(ao_code).checkbox.present?.should be_true
    end
  end
end

When /^I attempt to create a joint offered course offering for a subject in my admin org$/ do
  @course_offering = make CourseOffering, :term=> @term_for_test, :course => "ENGL316"
  @course_offering.start_create_by_search
end


Then /^I do not have access to create a new joint offered course offering$/ do
  on CreateCourseOffering do |page|
  page.create_new_joint_defined_course_first_row.exists?.should == false
  end
end

When /^there is a course with a co-located DL in my admin org/ do
  step "I am logged in as a Schedule Coordinator"
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :course=>"ENGL462", :term=>@term_for_test)
  @course_offering.manage_and_init
  @course_offering.activity_offering_cluster_list[0].ao_list[0].edit :colocated => true,
                                                                    :colocate_ao_list => Array.new(1){make ActivityOffering, :code=> "A",
                                                                    :parent_course_offering => (make CourseOffering, :course => "ENGL295", :term => @term_for_test)},
                                                                    :max_enrollment=>25,
                                                                    :colocate_shared_enrollment=> true
  @course_offering.activity_offering_cluster_list[0].ao_list[0].save
  step "I am logged in as a Department Schedule Coordinator"
end

Then /^I do not have access to edit the co-located Activity Offering$/ do
  @course_offering.manage_and_init
  on ManageCourseOfferings do |page|
    page.edit_link("A").present?.should == false
  end
end

Then /^I have access to add new delivery logistics$/ do
  on ActivityOfferingMaintenance do |page|
    page.view_requested_delivery_logistics
    page.add_new_delivery_logistics_button.present?.should == true
  end
end

When /^I have access to view requested delivery logistics$/ do
  on ActivityOfferingMaintenance do |page|
    page.view_requested_delivery_logistics
    page.requested_logistics_table.present?.should == true
  end
end
When /^I have access to delete requested delivery logistics$/ do
  on ActivityOfferingMaintenance do |page|
    page.delete_requested_delivery_logistics_element.present?.should == true
  end
end