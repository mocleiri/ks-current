When /^I copy an CO with AOs that have ASIs to a new CO in the different term with RSIs in its AOs$/ do
  @source_course_offering = make CourseOffering, :term=> Rollover::SOC_STATES_SOURCE_TERM, :course => "ENGL462"
  @course_offering = create CourseOffering, :term=> Rollover::PUBLISHED_SOC_TERM , :create_from_existing => @source_course_offering
end

Then /^The new CO and AOs are Successfully created$/ do
  @course_offering.manage_and_init
  tgt_activity_offering = @course_offering.get_ao_obj_by_code("A")
  tgt_activity_offering.status.should == ActivityOfferingObject::DRAFT_STATUS
  tgt_activity_offering.requested_scheduling_information_list.size.should_not == 0
end

And /^The ASIs are Successfully copied to RSIs in the new AOs of the newly created CO$/ do
  @source_course_offering.manage_and_init
  source_activity_offering = @source_course_offering.get_ao_obj_by_code("A")
  source_activity_offering.actual_scheduling_information_list.size.should_not == 0

  #now navigate to course offering copy and validate RSIs
  @course_offering.manage
  @course_offering.edit_ao :ao_code => "A"

  on ActivityOfferingMaintenance do |page|
    page.actual_sched_info_div.exists?.should == false  #should not be any ASIs
    page.requested_sched_info_table.rows.size.should be > 2 #should be more than header/footer rows
    page.requested_sched_info_table.rows[1..-2].each do |row|
      page.view_requested_scheduling_information
      days = page.get_requested_sched_info_days(row).delete(' ')
      start_time = page.get_requested_sched_info_start_time(row).delete(' ')
      si_key = "#{days}#{start_time}"
      #get the corresponding ASI by key
      asi = source_activity_offering.actual_scheduling_information_list.by_key(si_key)
      page.get_requested_sched_info_days(row).delete(' ').should == asi.days
      page.get_requested_sched_info_start_time(row).delete(' ').should == "#{asi.start_time}#{asi.start_time_ampm}"
      page.get_requested_sched_info_end_time(row).delete(' ').should == "#{asi.end_time}#{asi.end_time_ampm}"
      page.get_requested_sched_info_facility(row).should == asi.facility_long_name
      page.get_requested_sched_info_room(row).should == asi.room
    end

  end
end





