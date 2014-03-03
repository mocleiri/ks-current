And /^I am editing an AO with RSIs$/ do
  course_offering = create CourseOffering, :create_by_copy => (make CourseOffering, :term => "201208", :course=>"ENGL211")
  course_offering.manage_and_init
  @activity_offering = course_offering.get_ao_obj_by_code("A")
end

And /^I am editing an AO with RSIs in an open term$/ do
  course_offering = create CourseOffering, :create_by_copy => (make CourseOffering, :term => "201301", :course=>"ENGL211")
  course_offering.manage_and_init
  @activity_offering = course_offering.get_ao_obj_by_code("A")
end

When /^I revise an AO's requested scheduling information$/ do
  # capture the RSIs for editing
  #@new_rsis = @activity_offering.requested_scheduling_information_list.values[0]
  # edit RSIs
  @activity_offering.edit
  orig_key = @activity_offering.requested_scheduling_information_list.keys[0]
  @activity_offering.requested_scheduling_information_list.values[0].edit :days => "SU", :start_time => "10:00", :start_time_ampm => "am", :end_time => "10:50", :end_time_ampm => "am",
                                                                      :facility => "PHYS", :facility_long_name => "PHYS",:room => "4102"
  @activity_offering.requested_scheduling_information_list["SU10:00AM"] = @activity_offering.requested_scheduling_information_list.values[0]
  @activity_offering.requested_scheduling_information_list.delete(orig_key)
  @activity_offering.save
end

When /^I add RSIs for an AO specifying (times|times and facility|times and room) only$/ do |optional_field|

  # determine which optional-field the user wants
  case optional_field
    when "times and facility"
      optional_field_facility = "PHYS"
      optional_field_room = ""
    when "times and room"
      optional_field_facility = ""
      optional_field_room = "0152"
    else    # "times only"
      optional_field_facility = ""
      optional_field_room = ""
  end

  # capture the RSIs
  #@new_rsis =  @activity_offering.requested_scheduling_information_list.values[0]

  # add new RSI row
  @activity_offering.edit
  si_obj = create SchedulingInformationObject, :days => "TH",
                  :facility => optional_field_facility,
                  :facility_long_name => optional_field_facility, :room => optional_field_room
  @activity_offering.requested_scheduling_information_list[si_obj.si_key] = si_obj
  # if entering an invalid combination, need to stay on page to see the error message, so skip the page submit
  @activity_offering.save unless optional_field == "times and room"
end

Then /^the AO's scheduling information shows the new schedule$/ do
  # reload saved CO and AO data
  course_offering = make CourseOffering, :term => @activity_offering.parent_course_offering.term, :course => @activity_offering.parent_course_offering.course
  course_offering.manage
  #@activity_offering = course_offering.get_ao_obj_by_code("A")
  @activity_offering.edit
#  norm_st, norm_et = @new_rsis.normalize_start_and_end_times

  on ActivityOfferingMaintenance do |page|
    page.view_requested_scheduling_information
    page.requested_sched_info_table.wait_until_present
    page.requested_sched_info_table.rows.size.should be > 2 #should be more than header/footer rows
    page.requested_sched_info_table.rows[1..-2].each do |row|
      days = page.get_requested_sched_info_days(row).delete(' ')
      start_time = page.get_requested_sched_info_start_time(row).delete(' ')
      si_key = "#{days}#{start_time}"
      #get the corresponding ASI by key
      del_sched_info = @activity_offering.requested_scheduling_information_list[si_key]
      exp_room = (del_sched_info.room.nil?)?"":del_sched_info.room
      page.get_requested_sched_info_days(row).delete(' ').should == del_sched_info.days
      page.get_requested_sched_info_start_time(row).delete(' ').should == "#{del_sched_info.start_time}#{del_sched_info.start_time_ampm}"
      page.get_requested_sched_info_end_time(row).delete(' ').should == "#{del_sched_info.end_time}#{del_sched_info.end_time_ampm}"
      page.get_requested_sched_info_facility(row).should == del_sched_info.facility_long_name
      page.get_requested_sched_info_room(row).should == exp_room
    end
    page.cancel
  end
end

Then /^the AO's scheduling information shows the new schedule as TBA$/ do
  @activity_offering.parent_course_offering.manage
  @activity_offering.edit

  sched_info = @activity_offering.requested_scheduling_information_list[""]
  on ActivityOfferingMaintenance do |page|
    row = page.target_rsi_row("")
    isTBA = page.get_requested_sched_info_tba(row) == "TBA"
    isTBA.should == sched_info.tba
  end
end

Then /^an error message is displayed about the required RSI fields$/ do
  on ActivityOfferingMaintenance do |page|
    page.growl_text.should == "The form contains errors. Please correct these errors and try again."
    page.cancel
  end
end

When /^I add RSIs for an AO$/ do
  # add new RSI row
  @activity_offering.edit
  si_obj = create SchedulingInformationObject, :days => "TH"
  @activity_offering.requested_scheduling_information_list[si_obj.si_key] = si_obj
  @activity_offering.save
end

When /^I add RSIs for an AO checking the TBA flag$/ do
  # add new TBA RSI row
  @activity_offering.edit
  si_obj = create SchedulingInformationObject,  :tba => true, :days => nil, :start_time => nil, :start_time_ampm => nil, :end_time => nil, :end_time_ampm => nil
  @activity_offering.requested_scheduling_information_list[si_obj.si_key] = si_obj
  @activity_offering.save
end

And /^I delete the original RSIs$/ do
  @activity_offering.parent_course_offering.manage
  @activity_offering.edit
  rsi_key = @activity_offering.requested_scheduling_information_list.keys[0]
  @activity_offering.requested_scheduling_information_list.values[0].delete_rsi
  @activity_offering.requested_scheduling_information_list.delete(rsi_key)
  @activity_offering.save
end

When /^I add (standard|non-standard) RSIs for an AO$/ do |tsType|
  # add new RSI row
  @activity_offering.edit
  if tsType=="standard"
    si_obj = create SchedulingInformationObject, :use_std_ts => true,
                    :days => "MWF", :start_time => "01:00", :start_time_ampm => "pm", :end_time => "01:50", :end_time_ampm => "pm"
    @activity_offering.requested_scheduling_information_list[si_obj.si_key] = si_obj
  elsif tsType=="non-standard"
    si_obj = create SchedulingInformationObject, :use_std_ts => false, :days => "TH",
                    :start_time => "08:21", :start_time_ampm => "pm", :end_time => "09:04", :end_time_ampm => "pm"
    @activity_offering.requested_scheduling_information_list[si_obj.si_key] = si_obj
  end
  @activity_offering.save
end

When /^I check the "approved for non-standard time slots" flag$/ do
  @activity_offering.edit :allow_non_std_timeslots => true, :defer_save => false
end

Then /^the "approved for non-standard time slots" flag is set$/ do
  @activity_offering.edit
  on ActivityOfferingMaintenance do |page|
    page.view_requested_scheduling_information
    page.non_std_ts_checkbox.should be_checked
  end
end

When /^I edit an Activity Offering with non-standard time slots (approved|not approved)$/ do |approval|
  if approval=="not approved"
    course_offering = create CourseOffering, :create_by_copy => (make CourseOffering, :term => "201301", :course=>"ENGL202")
  else
    course_offering = create CourseOffering, :create_by_copy => (make CourseOffering, :term => "201301", :course=>"ENGL262")
  end
  @activity_offering = make ActivityOfferingObject, :parent_course_offering => course_offering, :code => 'A'
  @activity_offering.get_existing_info_from_page
end

Then /^there is a validation error on the EndTime field$/  do
  @activity_offering.edit
  @si_obj.end_time_ampm.upcase! unless @si_obj.end_time_ampm.nil?
  @si_obj.start_time_ampm.upcase! unless @si_obj.start_time_ampm.nil?
  on ActivityOfferingMaintenance do |page|
    page.view_requested_scheduling_information

    if @si_obj.days != nil then
      page.add_start_time.click
      page.loading.wait_while_present
      page.add_days.set @si_obj.days
      sleep 2
    end

    if @si_obj.start_time != nil then
      page.add_start_time.click
      page.loading.wait_while_present
      page.add_start_time.set @si_obj.start_time + " " + @si_obj.start_time_ampm
      page.add_start_time.click
      page.loading.wait_while_present
    end

    page.end_time_select_populate_list
    page.end_time_select.click
    page.end_time_select.attribute_value('class').should match /error/
    page.end_time_error_msg.should match /Days and Start Time combo does not match an existing Standard Time Slot/

    page.cancel   # cleanup to prevent browser's "you have unsaved changes" modal-dialog (which causes any subsequent tests executing in the same thread to fail)
  end
end

And /^I attempt to add non-standard RSIs for an AO$/ do
  @si_obj = make SchedulingInformationObject, :use_std_ts => false, :days => "TH",
                 :start_time => "08:21", :start_time_ampm => "pm", :end_time => "09:04", :end_time_ampm => "pm"
end



