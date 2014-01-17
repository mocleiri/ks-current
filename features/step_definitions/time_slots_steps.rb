When /^I add (\d*) different time slots to a single term type$/ do |nbr|
  @time_slots = create TimeSlots
  @time_slots.add_unused_time_slots(nbr.to_i)
end

When /^I add a single time slot per 2 different term types$/ do
  term_types = [ "Fall - Full", "Spring - Full" ]
  @time_slots = create TimeSlots, :term_types => term_types
  @time_slots.add_unused_time_slots( term_types[0], 1 )
  @time_slots.add_unused_time_slots( term_types[1], 1 )
end

When /^I add a duplicate time slot$/ do
  @time_slots = create TimeSlots
  @time_slots.add_duplicate_time_slot(1000)
end

When /^I add a new time slot but omit the (start time|end time|days)$/ do |data_to_omit|
  time_slot = make TimeSlots::TimeSlot

  case data_to_omit
    when "start_time"
      time_slot.start_time = nil
    when "end time"
      time_slot.end_time = nil
    else
      time_slot.days = nil
  end

  @time_slots = create TimeSlots
  @time_slots.add_new_time_slot_without_validation(time_slot)
end

Then /^the timeslots are saved$/ do
  @time_slots.show_time_slots
  @time_slots.new_time_slots.each do |time_slot|
    on TimeSlotMaintenance do |page|
      row = page.target_results_row time_slot.code
      row[TimeSlotMaintenance::TIME_SLOT_RESULTS_CODE].text == time_slot.code
      row[TimeSlotMaintenance::TIME_SLOT_RESULTS_TERM_TYPE].text == time_slot.term_type
      row[TimeSlotMaintenance::TIME_SLOT_RESULTS_DAYS].text == time_slot.days
      row[TimeSlotMaintenance::TIME_SLOT_RESULTS_START_TIME].text.split(' ')[0] == time_slot.start_time
      row[TimeSlotMaintenance::TIME_SLOT_RESULTS_START_TIME].text.split(' ')[1] == time_slot.start_time_am_pm
      row[TimeSlotMaintenance::TIME_SLOT_RESULTS_END_TIME].text.split(' ')[0] == time_slot.end_time
      row[TimeSlotMaintenance::TIME_SLOT_RESULTS_END_TIME].text.split(' ')[1] == time_slot.end_time_am_pm
    end
  end
end

Then /^an error message is displayed about the duplicate timeslot$/ do
  on(TimeSlotMaintenance).time_slot_error_message.text.should match /Duplicate time slots are not allowed/
end

Then /^an error is displayed about the missing data$/ do
  on(TimeSlotMaintenance).first_msg.should match /Invalid \bdays|Start time|End time\b/
end

When /^I show time slots for a single term type$/  do
  @time_slots = create TimeSlots
end

When /^I show time slots for multiple term types$/ do
  @time_slots = create TimeSlots, :term_types => ['Fall - Full', 'Spring - Full']
end

Then /^only time slots of that term type appear$/ do
  on TimeSlotMaintenance do |page|
    page.time_slot_search_results_table.rows[1..-2].each do |row|
      row.cells[TimeSlotMaintenance::TIME_SLOT_RESULTS_TERM_TYPE].text.should == @time_slots.term_types[0].to_s
    end
  end
end

When /^I? ?add a Time Slot with (one of)? ?the chosen Term Types?$/ do |arg1|
  @time_slots.add_unused_time_slots(1)
end

When /^I? ?attempt to (.*) a Time Slot which is used in scheduling information$/ do |action|
  @code_list = ["1002"]
  @time_slots = create TimeSlots
  if action == "delete"
    @time_slots.delete(@code_list)
  elsif action == "edit"
    @new_days = "H"
    @time_slots.edit_time_slot(:code => @code_list[0], :days => @new_days)
  end
end

When /^I? ?attempt to (.*) the Time Slot added above$/ do |action|
  @code_list = [@time_slots.new_time_slots[0].code]
  @time_slots = create TimeSlots
  if action == "delete"
    @time_slots.delete(@code_list)
  elsif action == "edit"
    @new_days = "H"
    @time_slots.edit_time_slot(:code => @code_list[0], :days => @new_days)
  end
end

And /^I? ?attempt to delete the Time Slot added above and also a Time Slot used in scheduling information$/ do
  @code_list = [@time_slots.new_time_slots[0].code]
  @code_list << "1002"
  @time_slots.delete(@code_list)
end

Then /^the (first|second)? ?Time Slot (is|is not) deleted$/  do |tsOrdinality, isIsNot|
  index = (!tsOrdinality.nil? && tsOrdinality=="second")?1:0
  on TimeSlotMaintenance do |page|
    page.get_time_slot_code_list.should_not include @code_list[index]if isIsNot=="is"
    page.get_time_slot_code_list.should include @code_list[index]if isIsNot=="is not"
  end
end

When /^I? ?attempt to delete both the Time Slots added above in the same action$/ do
  @code_list = []
  @time_slots.new_time_slots.each do |ts|
    @code_list << ts.code
  end
  @time_slots = create TimeSlots
  @time_slots.delete(@code_list)
end

Then /^an error message is displayed stating that the Time Slot may not be (deleted|edited)$/ do |msgType|
  on(TimeSlotMaintenance).growl_text.should match /^Time slot #{@code_list[-1]} is already associated with scheduling information, so cannot be #{msgType}$/
end

Then /^the Time Slot edits (are|are not) saved$/ do |areAreNot|
  @time_slots.show_time_slots
  on TimeSlotMaintenance do |page|
    row = page.target_results_row @code_list[0]
    row[TimeSlotMaintenance::TIME_SLOT_RESULTS_DAYS].text.should == @new_days if areAreNot=="are"
    row[TimeSlotMaintenance::TIME_SLOT_RESULTS_DAYS].text.should_not == @new_days if areAreNot=="are not"
  end
end

Then /^the Time Slot type edits (are|are not) saved$/ do |areAreNot|
  @time_slots.show_time_slots
  on TimeSlotMaintenance do |page|
    row = page.target_results_row @code_list[0]
    row[TimeSlotMaintenance::TIME_SLOT_RESULTS_TERM_TYPE].text.should == @new_type if areAreNot=="are"
    row[TimeSlotMaintenance::TIME_SLOT_RESULTS_TERM_TYPE].text.should_not == @new_type if areAreNot=="are not"
  end
end

And /^I? ?attempt to edit the Time Slot omitting the (days|start time|end time)$/ do |omittedField|
  @code_list = [@time_slots.new_time_slots[0].code]
  @time_slots = create TimeSlots

  # initialize days, start & end times, then blank out one depending on the value in the step def
  new_days, new_start, new_end = "S", "2:42", "2:49"
  case omittedField
    when "days"
      new_days = ""
    when "start time"
      new_start = ""
    when "end time"
      new_end = ""
  end

  @time_slots.edit_time_slot(:code => @code_list[0], :days => new_days, :start_time => new_start, :end_time => new_end)
end

And /^I? ?edit the second Time Slot to duplicate the first Time Slot$/ do
  @time_slots.show_time_slots
  ts0 = @time_slots.new_time_slots[0]
  ts1 = @time_slots.new_time_slots[1]
  @time_slots.edit_time_slot(:code => ts1.code, :days => ts0.days, :start_time => ts0.start_time, :start_time_ampm => ts0.start_time_am_pm, :end_time => ts0.end_time, :end_time_ampm => ts0.end_time_am_pm)
end

And /^I edit the Time Slot added above to use the other chosen Term Type$/ do
  @code_list = [@time_slots.new_time_slots[0].code]
  @time_slots = create TimeSlots, :term_types => ['Fall - Full', 'Spring - Full']
  @new_type = "Spring - Full"
  @time_slots.edit_time_slot(:code => @code_list[0], :term_type => @new_type)
end


