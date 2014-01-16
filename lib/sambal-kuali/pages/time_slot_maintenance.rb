class TimeSlotMaintenance < BasePage

  wrapper_elements
  frame_element
  validation_elements

  expected_element :time_slot_type_selector_list

  element(:time_slot_error_message) { |b| b.frm.div(id: "timeSlotPage").li(class: "uif-errorMessageItem") }

  element(:time_slot_type_selector_div) { |b| b.frm.div(id: "TimeSlotTypeSelect-Section") }
  element(:time_slot_type_selector_list) { |b| b.time_slot_type_selector_div.select_list }
  action(:show_time_slots) { |b| b.time_slot_type_selector_div.button(text: "Show").click; b.loading.wait_while_present }

  element(:time_slot_toolbar_div) { |b| b.frm.div(id: "TimeSlotToolBar-Section") }
  action(:initiate_add_time_slot) { |b| b.time_slot_toolbar_div.button(text: "Add Time Slot").click }
  
  element(:add_time_slot_form) { |b| b.frm.div(id: /jquerybubblepopup/) }

  element(:add_time_slot_popup_field_termType) { |b| b.add_time_slot_form.div(id: "addOrEditTermKey").select_list }
  element(:add_time_slot_popup_field_days) { |b| b.add_time_slot_form.div(id: "addOrEditDays").text_field }
  element(:add_time_slot_popup_field_startTime_div) { |b| b.add_time_slot_form.div(id: "addOrEditStartTime") }
  element(:add_time_slot_popup_field_startTime) { |b| b.add_time_slot_popup_field_startTime_div.text_field }
  element(:add_time_slot_popup_field_startTime_am_pm) { |b| b.add_time_slot_form.div(id: "addOrEditStartTimeAmPm") }
  element(:add_time_slot_popup_field_endTime) { |b| b.add_time_slot_form.div(id: "addOrEditEndTime").text_field }
  element(:add_time_slot_popup_field_endTime_am_pm) { |b| b.add_time_slot_form.div(id: "addOrEditEndTimeAmPm") }
  action(:save_add_time_slot) { |b| b.add_time_slot_form.button(id: "addOrEdit_action").click; b.loading.wait_while_present }

  element(:edit_time_slot_form) { |b| b.frm.div(id: "KS-TimeSlot-EditTimeSlotPopupForm") }

  element(:edit_time_slot_popup_field_termType) { |b| b.edit_time_slot_form.div(id: "addOrEditTermKey").select_list }
  element(:edit_time_slot_popup_field_days) { |b| b.edit_time_slot_form.div(id: "addOrEditDays").text_field }
  element(:edit_time_slot_popup_field_startTime_div) { |b| b.edit_time_slot_form.div(id: "addOrEditStartTime") }
  element(:edit_time_slot_popup_field_startTime) { |b| b.edit_time_slot_popup_field_startTime_div.text_field }
  element(:edit_time_slot_popup_field_startTime_am_pm) { |b| b.edit_time_slot_form.div(id: "addOrEditStartTimeAmPm") }
  element(:edit_time_slot_popup_field_endTime) { |b| b.edit_time_slot_form.div(id: "addOrEditEndTime").text_field }
  element(:edit_time_slot_popup_field_endTime_am_pm) { |b| b.edit_time_slot_form.div(id: "addOrEditEndTimeAmPm") }
  action(:save_edit_time_slot) { |b| b.edit_time_slot_form.button(id: "addOrEdit_action").click; b.loading.wait_while_present }

  action(:initiate_delete) { |b| b.time_slot_toolbar_div.button(text: "Delete").click }
  element(:delete_confirmation_dialog) { |b| b.frm.div(id: "deleteTimeSlotsConfirmationDialog-lightbox") }
  action(:confirm_delete) { |b| b.delete_confirmation_dialog.button(id: "timeslot_delete_confirm").click; b.loading.wait_while_present }

  element(:time_slot_search_results_table) { |b| b.frm.div(id: "TimeSlotSearchResultsDisplayTable").table() }

  TIME_SLOT_RESULTS_SELECT = 0
  TIME_SLOT_RESULTS_CODE = 1
  TIME_SLOT_RESULTS_TERM_TYPE = 2
  TIME_SLOT_RESULTS_DAYS = 3
  TIME_SLOT_RESULTS_START_TIME = 4
  TIME_SLOT_RESULTS_END_TIME = 5
  TIME_SLOT_RESULTS_ACTIONS = 6

  def select_time_slot_types( array_of_time_slot_types_to_select )
    array_of_time_slot_types_to_select.each do |time_slot_type_to_select|
      time_slot_type_selector_list.select time_slot_type_to_select
    end
  end

  def get_time_slot_code_list
    time_slot_code_list = []
    time_slot_search_results_table.rows.each_with_index do |row, index|
      time_slot_code_list << row[TIME_SLOT_RESULTS_CODE].text unless index == 0 || index == time_slot_search_results_table.rows.length-1
    end
    time_slot_code_list
  end

  def target_results_row(code)
    row = time_slot_search_results_table.row(text: /\b#{Regexp.escape(code.to_s)}\b/)
    return row unless row.nil?
    raise "error in target_time_slot_results_row: #{code} not found"
  end

  def add_new_time_slot(new_time_slot)
    original_time_slots = time_slot_search_results_table.to_a

    add_time_slot_without_validation(new_time_slot)

    if time_slot_error_message.exists?
      raise "Error adding new time-slot -> " << time_slot_error_message.text
    end

    # return the newly-added timeslot-code
    newly_added_time_slot = time_slot_search_results_table.to_a - original_time_slots
    if newly_added_time_slot.length < 1
      raise "Unable to find newly-added time-slot in results table"
    elsif newly_added_time_slot.length > 1
      raise "Unexpectedly found more than 1 newly-added time-slot in results-table; found -> " << newly_added_time_slot.join(",")
    end
    newly_added_time_slot[0][TIME_SLOT_RESULTS_CODE]
  end

  def add_time_slot_without_validation(new_time_slot)
    initiate_add_time_slot

    add_time_slot_popup_field_termType.select new_time_slot.term_type

    add_time_slot_popup_field_days.set new_time_slot.days

    add_time_slot_popup_field_startTime.set new_time_slot.start_time

    unless new_time_slot.start_time_am_pm == nil
      add_time_slot_popup_field_startTime_am_pm.radio(value: "#{new_time_slot.start_time_am_pm.upcase}").set
    end

    add_time_slot_popup_field_endTime.set new_time_slot.end_time

    add_time_slot_popup_field_endTime_am_pm.radio(value: "#{new_time_slot.end_time_am_pm.upcase}").set

    save_add_time_slot
  end

  def edit_time_slot(opts)
    if opts[:code].nil?
      raise "edit_time_slot called without supplying time slot code"
    end
    row = target_results_row(opts[:code])
    row.cells[TIME_SLOT_RESULTS_ACTIONS].link.click
    sleep(1)
    if !opts[:term_type].nil?
      edit_time_slot_popup_field_termType.select opts[:term_type]
    end
    if !opts[:days].nil?
      edit_time_slot_popup_field_days.set opts[:days]
    end
    if !opts[:start_time].nil?
      edit_time_slot_popup_field_startTime.set opts[:start_time]
    end
    if !opts[:start_time_am_pm].nil?
      edit_time_slot_popup_field_startTime_am_pm.radio(value: "#{opts[:start_time_am_pm].upcase}").set 
    end
    if !opts[:end_time].nil?
      edit_time_slot_popup_field_endTime.set opts[:end_time]
    end
    if !opts[:end_time_am_pm].nil?
      edit_time_slot_popup_field_endTime_am_pm.radio(value: "#{opts[:end_time_am_pm].upcase}").set
    end
    save_edit_time_slot
  end

  def delete_time_slot (codes)
    codes.each do |code|
      row = target_results_row(code)
      row.cells[TIME_SLOT_RESULTS_SELECT].checkbox.set
    end

    initiate_delete
    sleep(1)
    confirm_delete
  end

  def get_start_time_of_last_row
    table_array = time_slot_search_results_table.to_a
    table_array[table_array.length-2][TIME_SLOT_RESULTS_START_TIME]
  end

end