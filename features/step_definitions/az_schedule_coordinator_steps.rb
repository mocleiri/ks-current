When /^I manage course offerings for a subject code$/ do
  @term_for_test = Rollover::OPEN_SOC_TERM unless @term_for_test != nil
  @course_offering = make CourseOffering, :course=>"CHEM611", :term=>@term_for_test
  @course_offering.search_by_subjectcode
end

#When /^I manage course offerings for a course with the first activity offering in draft state and the second activity offering in approved state$/ do
#  @term_for_test = Rollover::OPEN_SOC_TERM unless @term_for_test != nil
#  @course_offering = make CourseOffering, :course=>"ENGL206", :term=>@term_for_test
#  @course_offering.manage
#  on ManageCourseOfferings do |page|
#    page.copy("A")
#    if page.ao_status("B") != "Approved"
#      if page.select_ao("B") then
#        page.approve_activity_button.wait_until_present(5)
#        page.approve_activity
#      end
#    end
#  end
#end

#When /^I manage course offerings for a course with the first activity offering in draft state and the second activity offering in draft state$/ do
#  @term_for_test = Rollover::OPEN_SOC_TERM unless @term_for_test != nil
#  @course_offering = make CourseOffering, :course=>"ENGL243", :term=>@term_for_test
#  @course_offering.manage
#  on ManageCourseOfferings do |page|
#    page.copy("A")
#  end
#end

#When /^I manage a course offering with activity offerings in approved and draft status$/ do
#  @term_for_test = Rollover::OPEN_SOC_TERM unless @term_for_test != nil
#  @course_offering = make CourseOffering, :course=>"ENGL245", :term=>@term_for_test
#  @course_offering.ensure_activity_offering_in_status("Draft")
#  @course_offering.ensure_activity_offering_in_status("Approved")
#end

#When /^I manage course offerings for a course with 2 activity offerings present$/ do
#  @term_for_test = Rollover::OPEN_SOC_TERM unless @term_for_test != nil
#  @course_offering = make CourseOffering, :course=>"ENGL245", :term=>@term_for_test
#  @course_offering.manage
#end

When /^I manage a course offering in the specified states?$/ do
  @course_offering.manage
end

When /^I manage course offerings for the specified subject code$/ do
  @course_offering.search_by_subjectcode
end

When /^I manage a course offering$/ do
  @term_for_test = Rollover::OPEN_SOC_TERM unless @term_for_test != nil
  @course_offering = make CourseOffering, :course=>"CHEM611", :term=>@term_for_test
  @course_offering.manage
  @activity_offering = make ActivityOfferingObject, :code=>"A", :parent_course_offering => @course_offering
end

Then /^I have access to delete an activity offering in a "([^"]*)" state$/ do |aostate|
  @course_offering.manage_and_init
  @course_offering.attempt_ao_delete_by_status(aostate).should == true
end

Then /^I have access to delete a course offering in a "([^"]*)" state$/ do |costate|
  @course_offering.search_by_subjectcode
  @course_offering.attempt_co_delete_by_status(costate).should == true
end

Then /^I do not have access to select an activity offering in a "([^"]*)" state$/ do |aostate|
  @course_offering.manage
  @course_offering.ao_has_checkbox_by_status(aostate).should == false
end

Then /^I have access to create a new joint offered course offering$/ do
  on CreateCourseOffering do |page|
    page.create_new_joint_defined_course_first_row.exists?.should == true
  end
end

Then /^the expected.*state of the CO toolbar is: Create: "([^"]*)"; Approve: "([^"]*)"; Delete: "([^"]*)"$/ do |create_button_state, approve_button_state, delete_button_state|
  on ManageCourseOfferingList do |page|
    if create_button_state == "enabled"
      page.create_course_offering_button.enabled?.should be_true
    elsif create_button_state == "disabled"
      page.create_course_offering_button.enabled?.should be_false
    else
      raise 'Invalid button state. Allowed values are \'enabled\' and \'disabled\''
    end

    if approve_button_state == "enabled"
      page.approve_course_offering_button.enabled?.should be_true
    elsif approve_button_state == "disabled"
      page.approve_course_offering_button.enabled?.should be_false
    else
      raise 'Invalid button state. Allowed values are \'enabled\' and \'disabled\''
    end

    if delete_button_state == "enabled"
      page.delete_cos_button.enabled?.should be_true
    elsif delete_button_state == "disabled"
      page.delete_cos_button.enabled?.should be_false
    else
      raise 'Invalid button state. Allowed values are \'enabled\' and \'disabled\''
    end
    page.deselect_all_cos
  end
end

Then /^the expected.*state of the AO toolbar is: Create: "([^"]*)"; Approve: "([^"]*)"; SetAsDraft: "([^"]*)"; Delete: "([^"]*)"; AddCluster: "([^"]*)"; Move: "([^"]*)"$/ do |create_button_state, approve_button_state, setasdraft_button_state, delete_button_state, addcluster_button_state, move_button_state|
  on ManageCourseOfferings do |page|
    if create_button_state == "enabled"
      page.add_activity_button.enabled?.should be_true
    elsif create_button_state == "disabled"
      page.add_activity_button.enabled?.should be_false
    else
      raise 'Invalid button state. Allowed values are \'enabled\' and \'disabled\''
    end

    if approve_button_state == "enabled"
      page.approve_activity_button.enabled?.should be_true
    elsif approve_button_state == "disabled"
      page.approve_activity_button.enabled?.should be_false
    else
      raise 'Invalid button state. Allowed values are \'enabled\' and \'disabled\''
    end

    if setasdraft_button_state == "enabled"
      page.draft_activity_button.enabled?.should be_true
    elsif setasdraft_button_state == "disabled"
      page.draft_activity_button.enabled?.should be_false
    else
      raise 'Invalid button state. Allowed values are \'enabled\' and \'disabled\''
    end

    if delete_button_state == "enabled"
      page.delete_aos_button.enabled?.should be_true
    elsif delete_button_state == "disabled"
      page.delete_aos_button.enabled?.should be_false
    else
      raise 'Invalid button state. Allowed values are \'enabled\' and \'disabled\''
    end

    if addcluster_button_state == "enabled"
      page.add_cluster_button.enabled?.should be_true
    elsif addcluster_button_state == "disabled"
      page.add_cluster_button.enabled?.should be_false
    else
      raise 'Invalid button state. Allowed values are \'enabled\' and \'disabled\''
    end

    if move_button_state == "enabled"
      page.move_aos_button.enabled?.should be_true
    elsif move_button_state == "disabled"
      page.move_aos_button.enabled?.should be_false
    else
      raise 'Invalid button state. Allowed values are \'enabled\' and \'disabled\''
    end
    page.cluster_deselect_all_aos
  end
end

When /^I select a course offering in "([^"]*)" status$/ do |co_status|
  on ManageCourseOfferingList do |page|
    page.select_co_by_status(co_status)
  end
end

When /^I select an activity offering in "([^"]*)" status$/ do |ao_status|
  on ManageCourseOfferings do |page|
    page.select_ao_by_status(ao_status)
  end
end

Then /^I edit the activity offering I do not have access to change the subterm$/ do
  @activity_offering.edit  :defer_save => true
  on ActivityOfferingMaintenance do |page|
    page.change_subterm_element.present?.should == false
    page.cancel
  end

end

Then /^I have access to suspend an Activity Offering$/ do
  on ManageCourseOfferings do |page|
    #page.select_ao("A")
    @course_offering.copy_ao :ao_code=> "A"
    page.select_ao(@course_offering.activity_offering_cluster_list[0].ao_list.last.code)
    page.suspend_ao
  end

  on SuspendActivityOffering do |page|
    page.suspend_activity_button.present?.should == true
    #page.cancel
    page.suspend_activity
  end
end

Then /^I do not have access to suspend an Activity Offering$/ do
  on ManageCourseOfferings do |page|
    if page.target_row("A").checkbox.present?
      page.select_ao("A")
    end

    page.suspend_ao_button.enabled?.should == false

    if page.target_row("A").checkbox.present?
      page.deselect_ao("A")
    end
  end
end

Then /^I have access to cancel an Activity Offering$/ do
  on ManageCourseOfferings do |page|
    @course_offering.copy_ao :ao_code=> "A"
    page.select_ao(@course_offering.activity_offering_cluster_list[0].ao_list.last.code)
    page.cancel_ao
  end

  on CancelActivityOffering do |page|
    page.cancel_activity_button.present?.should == true
    #page.cancel
    page.cancel_activity
  end
end

Then /^I do not have access to cancel an Activity Offering$/ do
  on ManageCourseOfferings do |page|
    if page.target_row("A").checkbox.present?
       page.select_ao("A")
    end

    page.cancel_ao_button.enabled?.should == false

    if page.target_row("A").checkbox.present?
     page.deselect_ao("A")
    end
  end
end

Then /^I have access to reinstate an Activity Offering$/ do
    on ManageCourseOfferings do |page|
      page.select_ao(@course_offering.activity_offering_cluster_list[0].ao_list.last.code)
      page.reinstate_ao
    end

    on ReinstateActivityOffering do |page|
      if page.reinstate_activity_button.enabled?.should == true
        page.reinstate_activity
      end
    end
end

Then /^I do not have access to reinstate an Activity Offering$/ do
  on ManageCourseOfferings do |page|
    if page.target_row("A").checkbox.present?
      page.select_ao("A")
    end

    page.reinstate_ao_button.enabled?.should == false

    if page.target_row("A").checkbox.present?
      page.deselect_ao("A")
    end
  end
end