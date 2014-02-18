class ActivityOfferingMaintenance < BasePage

  wrapper_elements
  validation_elements
  frame_element

  expected_element :mainpage_section

  element(:ao_header_text) { |b| b.frm.div(class: "uif-viewHeader-contentWrapper uif-sticky").h1.span.text }

  action(:prev_ao) { |b| b.frm.link(id: "edit_ao_prev").click; b.loading.wait_while_present }
  element(:prev_ao_text) { |b| b.frm.link(id: "edit_ao_prev").text }
  element(:select_ao_menu) { |b| b.frm.select(id: "edit_ao_select_control") }
  action(:next_ao) { |b| b.frm.link(id: "edit_ao_next").click; b.loading.wait_while_present }
  element(:next_ao_text) { |b| b.frm.link(id: "edit_ao_next").text }

  def jump_to_ao(ao_name)
    select_ao_menu.select(ao_name)
  end

  #sticky footer
  element(:sticky_footer) { |b| b.frm.div(class: "ks-uif-footer uif-stickyFooter uif-stickyButtonFooter") }
  element(:save_cancel_div) { |b| b.frm.div(id: "ActivityOfferingEdit_SubmitCancel") }
  element(:save_button) { |b| b.save_cancel_div.button(text: "Save Progress") }
  action(:save) { |b| sleep 2; b.loading.wait_while_present; sleep 2; b.save_button.click; b.loading.wait_while_present(120) }
  action(:send_to_scheduler_checkbox) { |b| b.frm.checkbox(id: "send_RDLs_to_scheduler_control") }
  value(:send_RSIs_to_scheduler_msg) { |b| b.label(id: "send_RDLs_to_scheduler_label").text }
  action(:send_to_scheduler) { |b| b.send_to_scheduler_checkbox.set }

  element(:submit_button) { |b| b.save_cancel_div.button(text: "Update") }
  action(:submit) { |b| sleep 2; b.loading.wait_while_present; sleep 2; b.submit_button.click; b.loading.wait_while_present(120) }
  action(:cancel) { |b| b.save_cancel_div.link(text: "Cancel").click; b.loading.wait_while_present }
  element(:sticky_footer_div) { |b| b.frm.div(class: "ks-uif-footer uif-stickyFooter uif-stickyButtonFooter", index: 1) }
  element(:send_revised_scheduling_information_checkbox) { |b| b.sticky_footer_div.checkbox(name: "document.newMaintainableObject.dataObject.sendRDLsToSchedulerAfterMSE") }
  action(:send_revised_scheduling_information) { |b| b.send_revised_scheduling_information_checkbox.click }
  #end sticky footer

  #unsaved changes dialog - appears when navigating between AOs
  element(:save_continue_alert_div) { |b| b.frm.div(id: "ActivityOfferingEdit-NavigationConfirmation") }
  element(:cancel_save_link) { |b| b.save_continue_alert_div.link(text: "Cancel") }
  action(:cancel_save) { |b| b.cancel_save_link.click }
  element(:save_and_continue_button) { |b| b.frm.button(id: "edit_ao_save_and_continue") }
  action(:save_and_continue) { |b| b.save_and_continue_button.click; b.loading.wait_while_present }
  element(:continue_without_saving_button) { |b| b.frm.button(id: "edit_ao_cancel") }
  action(:continue_without_saving) { |b| b.continue_without_saving_button.click; b.loading.wait_while_present }
  #end of dialog elements

  element(:activity_code) { |b| b.frm.text_field(name: "document.newMaintainableObject.dataObject.aoInfo.activityCode") }

  value(:subterm) { |b| b.frm.div(id: "subterm_name").span.text }
  element(:change_subterm_element) { |b| b.frm.link(id: "change_link") }
  element(:change_subterm_popup) { |b| b.frm.div(id: "KS-ActivityOfferingEdit-SubtermsPopupForm") }

  def change_subterm(subterm_type)
    change_subterm_element.click
    change_subterm_popup.select.select subterm_type
  end

  # Co-located
  element(:colocated_checkbox) { |b| b.frm.checkbox(id: "is_co_located_control") }
  action(:select_colocated_checkbox) { |b| b.colocated_checkbox.click() }
  element(:colocated_co_input_field) { |b| b.frm.text_field(id: "ActivityOfferingEdit-CoLocatedActivities-CoInputField_add_control") }
  element(:colocated_ao_input_field) { |b| b.frm.text_field(id: "ActivityOfferingEdit-CoLocatedActivities-AoInputField_add_control") }
  element(:colocated_add_button) { |b| b.frm.div(id: "ActivityOfferingEdit-CoLocatedActivities").button(text: /add/i) }
  element(:add_colocate_ao_confirmation_dialog) { |b| b.div(id: "ActivityOfferingEdit-CoLocatedAddLineDialog") }
  action(:add_colocate_ao_confirmation_add) { |b| b.add_colocate_ao_confirmation_dialog.button(id: "edit_ao_add_colocate_line").click; b.loading.wait_while_present }

  action(:add_colocated) { |b| b.colocated_add_button.click(); b.adding.wait_while_present; b.colocated_co_input_field.wait_until_present; b.colocated_ao_input_field.wait_until_present; b.colocated_add_button.wait_until_present; }
  element(:jointly_share_enrollment_radio) { |b| b.radio(id: "share_seats_control_0") }
  action(:select_jointly_share_enrollment_radio) { |b| b.jointly_share_enrollment_radio.set }
  element(:separately_manage_enrollment_radio) { |b| b.radio(id: "share_seats_control_1") }
  action(:select_separately_manage_enrollment_radio) { |b| b.separately_manage_enrollment_radio.wait_until_present; b.separately_manage_enrollment_radio.set; b.colocated_separate_max_enrollment_table.wait_until_present }
  element(:colocated_shared_max_enrollment_input_field) { |b| b.frm.text_field(id: "shared_max_enr_control") }
  element(:colocated_separate_max_enrollment_table) { |b| b.frm.div(id: "enr_shared_table").table() }

  def edit_separate_max_enr co_code, ao_code, max_enr
    co_ao_code = "#{co_code} #{ao_code}"
    row = colocated_separate_max_enrollment_table.row(text: /\b#{Regexp.escape(co_ao_code)}\b/)
    row.cells[1].text_field.set max_enr
  end

  element(:total_maximum_enrollment) { |b| b.frm.text_field(id: "maximumEnrollment_control") }

  element(:mainpage_section){|b| b.frm.div(id:"ActivityOffering-MaintenanceView")}

  #END Scheduling Info section
  element(:actual_sched_info_div) { |b| b.frm.div(id: /^ActivityOffering-DeliveryLogistic.*-Actuals$/) }
  element(:actual_sched_info_table) { |b| b.actual_sched_info_div.table() }

  TBA_COLUMN = 0
  DAYS_COLUMN = 1
  ST_TIME_COLUMN = 2
  END_TIME_COLUMN = 3
  FACILITY_COLUMN = 4
  ROOM_COLUMN = 5
  FEATURES_COLUMN = 6
  SCHED_INFO_ACTION_COLUMN = 7

  element(:add_sched_info_div) { |b| b.frm.div(id: "ActivityOffering-DeliveryLogistic-New") }
  element(:non_std_ts_control) { |b| b.span(id: "isApprovedForNonStandardTimeSlots_control") }
  element(:non_std_ts_checkbox) { |b| b.checkbox(id: "isApprovedForNonStandardTimeSlots_control") }
  element(:non_std_ts_checkbox_text) { |b| b.span(id: "isApprovedForNonStandardTimeSlots_control").text }
  element(:non_std_ts_text) { |b| b.label(id: "isApprovedForNonStandardTimeSlots_label").text }
  action(:approve_non_std_ts) { |b| b.non_std_ts_checkbox.set }
  action(:disallow_non_std_ts) { |b| b.non_std_ts_checkbox.clear }
  element(:add_tba){ |b|b.add_sched_info_div.checkbox(id: 'rdl_tba_control')}
  element(:add_days) { |b| b.add_sched_info_div.text_field(id: 'rdl_days_control') }
  element(:add_start_time) { |b| b.add_sched_info_div.text_field(id: 'rdl_starttime_control') }

  # END TIME WIDGETS
  # This field comes in 2 variants: 1) an "Input"-field, 2) a "Select"-list
  # Only 1 variant is displayed for any given variation of these variables: 1) the logged-in user, 2) whether or not the AO has been approved for "non standard"
  # See the application code/documentation for more detail
  #
  # Note: the difference between #1 and #2 is a bit subtle.  But if you log in as CSC(admin) you'll see it renders as an "<input>" in the DOM (which when you
  #       press the "down"-key you see the DOM gets dynamically populated with a "<ul>"-tag (widget #1).  However, if you log in as DSC(carol) and you edit
  #       an AO which has been "approved for non-standard timeslots", you'll see it instead renders in the DOM as a "<select>" ()widget #2).

  # Widget #1 (input field)
  element(:add_end_time_div) { |b| b.add_sched_info_div.div(id: "rdl_endtime") }
  element(:add_end_time) { |b| b.add_sched_info_div.text_field(id: 'rdl_endtime_control') }

  # Widget #2 (select list)
  element(:end_time_select) { |b| b.select(id: "rdl_endtime_control") }
  action(:end_time_select_populate_list) { |b| b.add_start_time.fire_event "onblur"; sleep 3; }
  action(:select_end_time) { |time, b| b.link(text: /#{time}/).wait_until_present;b.link(text: /#{time}/).click }

  element(:end_time_error_msg) { |b| b.div(id: 'rdl_endtime_errors').ul.li(class: "uif-errorMessageItem-field").text}

  element(:add_facility) { |b| b.add_sched_info_div.text_field(id: 'rdl_bldg_control') }
  action(:lookup_facility) { |b| b.add_sched_info_div.div(id: 'rdl_bldg').button().click; b.loading.wait_while_present }
  element(:add_room) { |b| b.add_sched_info_div.text_field(id: 'rdl_room_control') }
  action(:lookup_room) { |b| b.add_sched_info_div.div(id: 'rdl_room').button().click; b.loading.wait_while_present }

  element(:add_new_scheduling_information_button) { |b| b.button(id: "add_rdl_button") }
  action(:add_new_scheduling_information) { |b| b.add_new_scheduling_information_button.click; b.adding.wait_while_present }

  element(:view_requested_scheduling_information_toggle_open) { |b| b.frm.image(id: "ActivityOffering-ManageSchedulingInformationSection_toggle_exp") }
  element(:view_requested_scheduling_information_link) { |b| b.frm.link(id: "ActivityOffering-ManageSchedulingInformationSection_toggle") }

  def view_requested_scheduling_information
    if view_requested_scheduling_information_link.present? && !view_requested_scheduling_information_toggle_open.present?
      view_requested_scheduling_information_link.click
      loading.wait_while_present
    end
  end

  element(:delete_requested_scheduling_information_element) { |b| b.requested_sched_info_table.link(id: /delete_schedule_request/) } #used only for AZ test

  element(:requested_sched_info_div) { |b| b.frm.div(id: "ActivityOffering-DeliveryLogistic-Requested") }
  element(:requested_sched_info_table) { |b| b.requested_sched_info_div.table() }

  action(:get_actual_sched_info_tba) { |row,b| row.cells[TBA_COLUMN].text }
  action(:get_actual_sched_info_days) { |row,b| row.cells[DAYS_COLUMN].text }
  action(:get_actual_sched_info_start_time) { |row,b| row.cells[ST_TIME_COLUMN].text }
  action(:get_actual_sched_info_end_time) { |row,b| row.cells[END_TIME_COLUMN].text }
  action(:get_actual_sched_info_facility) { |row,b| row.cells[FACILITY_COLUMN].text }
  action(:get_actual_sched_info_room) { |row,b| row.cells[ROOM_COLUMN].text }
  action(:get_actual_sched_info_features) { |row,b| row.cells[FEATURES_COLUMN].text }

  action(:get_requested_sched_info_tba) { |row,b| row.cells[TBA_COLUMN].text }
  action(:get_requested_sched_info_days) { |row,b| row.cells[DAYS_COLUMN].text }
  action(:get_requested_sched_info_start_time) { |row,b| row.cells[ST_TIME_COLUMN].text }
  action(:get_requested_sched_info_end_time) { |row,b| row.cells[END_TIME_COLUMN].text }
  action(:get_requested_sched_info_facility) { |row,b| row.cells[FACILITY_COLUMN].text }
  action(:get_requested_sched_info_room) { |row,b| row.cells[ROOM_COLUMN].text }
  action(:get_requested_sched_info_features) { |row,b| row.cells[FEATURES_COLUMN].text }

  def target_rsi_row (key)
    view_requested_scheduling_information
    requested_sched_info_table.rows.each do |row|
      row_key = "#{row.cells[DAYS_COLUMN].text.upcase.delete(' ')}#{row.cells[ST_TIME_COLUMN].text.upcase.delete(' ')}"
      return row unless row_key != key
    end
    return nil
  end

  def edit_rsi_row(row)
    row.cells[ACTIONS].link(id: /edit_schedule_request_action_link/).click
    loading.wait_while_present(120)
  end

  def delete_rsi_row(row)
    row.cells[SCHED_INFO_ACTION_COLUMN].link(id: /delete_schedule_request/).click
  end
  #END Scheduling Info section

  #Personnel section
  element(:personnel_div) { |b| b.frm.div(id: "ao-personnelgroup") }
  element(:personnel_table) { |b| b.frm.personnel_div.table() }

  ID_COLUMN = 0
  PERS_NAME_COLUMN = 1
  AFFILIATION_COLUMN = 2
  INST_EFFORT_COLUMN = 3
  PERS_ACTION_COLUMN = 4

  element(:add_personnel_id) { |b| b.personnel_table.rows[-2].text_field(id: /personnel_id_line/) }
  action(:add_lookup_person) { |b| b.personnel_table.rows[-2].div(id: /personnel_id_line/).button().click; b.loading.wait_while_present }
  element(:add_personnel_name) { |b| b.personnel_table.rows[-2].text_field(id: /personnel_name_line/) }
  element(:add_personnel_affiliation) { |b| b.personnel_table.rows[-2].select(id: /personnel_affiliation_line/) }
  element(:add_personnel_inst_effort) { |b| b.personnel_table.rows[-2].text_field(id: /personnel_allocation_line/) }

  element(:add_personnel_element) { |b| b.frm.button(id: "ao-personnelgroup-addline") }
  action(:add_personnel) { |b| b.add_personnel_element.click; b.loading.wait_while_present }

  action(:delete_personnel_element) { |id,b| b.target_person_row(id).link(id: /delete_personnel_line/) }
  action(:delete_personnel) { |id,b| b.delete_personnel_element(id).click; b.loading.wait_while_present }

  def target_person_row(id)
    row = nil
    personnel_table.rows[1..-2].each do |row|
      row_key = row.text_field(id: /personnel_id_line/).value
      return row if row_key == id
    end
    return nil
  end

  action(:person_name) { |id,b| b.target_person_row(id).text_field(id: /personnel_name_line/).value }
  action(:update_person_name) { |id,name,b| b.target_person_row(id).text_field(id: /personnel_name_line/).set name }

  action(:person_affiliation) { |id,b| b.target_person_row(id).select(id: /personnel_affiliation_line/).selected_options[0].text }
  action(:update_person_affiliation) { |id,affiliation,b| b.target_person_row(id)..select(id: /personnel_affiliation_line/).select(affiliation) }

  action(:person_inst_effort) { |id,b| b.target_person_row(id).text_field(id: /personnel_allocation_line/).value }
  action(:update_person_inst_effort) { |id,effort,b| b.target_person_row(id).text_field(id: /personnel_allocation_line/).set effort }
  #END Personnel section

  value(:no_waitlists_msg) { |b| b.frm.div(id: "ActivityOfferingEdit-NoWaitlist").span(text: /Waitlists are deactivated/).text }
  element(:waitlist_section) { |b| b.frm.div(id: "ActivityOfferingEdit-WailtList-Info-Section") }
  element(:waitlist_checkbox) { |b| b.frm.checkbox(id: "ActivityOfferingEdit-HasWaitlist_control") }
  element(:waitlist_confirmation_radio) { |b| b.div(id: 'wait_list_type').radio(value: 'kuali.waitlist.type.Confirmation') }
  element(:waitlist_automatic_radio) { |b| b.div(id: 'wait_list_type').radio(value: 'kuali.waitlist.type.automatic') }
  element(:waitlist_manual_radio) { |b| b.div(id: 'wait_list_type').radio(value: 'kuali.waitlist.type.manual') }
  element(:waitlist_limit_checkbox) { |b| b.waitlist_section.div(data_label: "Limit Waitlist Size").checkbox }
  element(:waitlist_limit) { |b| b.waitlist_section.text_field(id: "limit_waitlist_size_text_control") }
  element(:waitlist_allow_hold_checkbox) { |b| b.waitlist_section.checkbox(id: 'allow-hold-list-checkbox_control') }

  def waitlist_processing_type
    radio_list = waitlist_section.div(data_label: "Waitlist Type").fieldset.radios
    radio_list.each do |radio|
      return radio.parent.label.text if radio.set?
    end
  end

  #for some users/soc states waitlists are read-only
  value(:waitlists_active?) { |b| b.frm.div(id: 'waitlist_active_ro').span(text: "Yes").exists? }
  value(:waitlists_processing) { |b| b.frm.div(id: 'waitlist_proc_ro').span(data_parent: 'waitlist_proc_ro').text }
  value(:waitlists_max_size) { |b| b.frm.div(id: 'waitlist_max_size_ro').span(data_parent: 'waitlist_max_size_ro').text }
  value(:waitlists_allow_holds?) { |b| b.frm.div(id: 'waitlist_holds_ro').span(data_parent: 'waitlist_holds_ro').exists? }

  element(:seat_pools_div) { |b| b.frm.div(id: "ao-seatpoolgroup") }
  element(:seat_pools_table) { |b| b.frm.table(id: "ao-seatpoolgroup-table") }

  PRIORITY_COLUMN = 0
  SEATS_COLUMN = 1
  PERCENT_COLUMN = 2
  POP_NAME_COLUMN = 3
  EXP_MILESTONE_COLUMN = 4
  SEATS_ACTION_COLUMN = 5

  value(:seat_pool_count) { |b| b.frm.div(id: 'seatpoolCount').span(data_parent: 'seatpoolCount').text }
  value(:seats_remaining_span) { |b| b.frm.div(id: "seatsRemaining").span(data_parent: "seatsRemaining") }
  value(:seats_remaining) { |b| b.seats_remaining_span.text }
  value(:percent_seats_remaining) { |b| b.seats_remaining_span.text[/\d+(?=%)/] }
  value(:seat_count_remaining) { |b| b.seats_remaining_span.text[/\d+(?=.S)/] }
  value(:max_enrollment_count) { |b| b.frm.div(id: "seatsRemaining").text[/\d+(?=\))/] }
  value(:seatpoolname){|b| b.frm.div(id: "ao-seatpoolgroup").table.rows[1].cells[3].text_field.value}

  #seat pool validation elements
  element(:seatpool_error_list) { |b| b.frm.ul(class: "uif-validationMessagesList") }
  element(:seatpool_info_list) { |b| b.frm.ul(class: "uif-validationMessagesList") }
  value(:seatpool_first_msg) { |b| b.seatpool_info_list.li.text }

  element(:add_pool_priority) { |b| b.seat_pools_table.rows[-2].cells[PRIORITY_COLUMN].text_field() }
  element(:add_pool_seats) { |b| b.seat_pools_table.rows[-2].cells[SEATS_COLUMN].text_field() }
  value(:add_pool_name)  { |b| b.seat_pools_table.rows[-2].cells[POP_NAME_COLUMN].text_field().value }
  
  action(:add_lookup_population_name) { |b| b.seat_pools_table.rows[-2].button(title: "Search Field").click; b.loading.wait_while_present }
  element(:add_pool_expiration_milestone) { |b| b.seat_pools_table.rows[-2].cells[EXP_MILESTONE_COLUMN].select() }

  element(:add_pool_element) { |b| b.frm.button(id: "ao-seatpoolgroupZ_add")}
  action(:add_seat_pool) { |b| b.add_pool_element.click; b.loading.wait_while_present }

  def target_pool_row(pop_name)
    seat_pools_table.rows[1..seat_pools_table.rows.count].each do |row|
        if (row.cells[POP_NAME_COLUMN].text_field.value =~ /#{pop_name}/)
          return row
        end
      end
  end
  def pool_percentage(pop_name)
    target_pool_row(pop_name).div(id: /seatLimitPercent_line/).text
  end

  def remove_seatpool(pop_name)
    target_pool_row(pop_name).i(class: "ks-fontello-icon-cancel").click    #link(text: "delete").click
    loading.wait_while_present
  end

  def remove_seatpool_by_index(sp_index)
    seat_pools_table.rows[sp_index].link(class: /btn-link/).click
    loading.wait_while_present
  end

  def update_priority(pop_name, priority)
    target_pool_row(pop_name).cells[PRIORITY_COLUMN].text_field.set priority
    target_pool_row(pop_name).cells[PRIORITY_COLUMN].text_field.fire_event "onblur"
  end

  def update_seats(pop_name, seats)
    target_pool_row(pop_name).cells[SEATS_COLUMN].text_field.set seats
    target_pool_row(pop_name).cells[SEATS_COLUMN].text_field.fire_event "onblur"
  end

   def update_expiration_milestone(pop_name, milestone)
    target_pool_row(pop_name).cells[EXP_MILESTONE_COLUMN].select.select(milestone)
  end

  def get_priority(pop_name)
    target_pool_row(pop_name).cells[PRIORITY_COLUMN].text_field.value #cell is hard-coded, getting this value was very problematic
  end

  def get_seats(pop_name)
    target_pool_row(pop_name).cells[SEATS_COLUMN].text_field.value #cell is hard-coded, getting this value was very problematic
  end

  def get_expiration_milestone(pop_name)
    target_pool_row(pop_name).cells[EXP_MILESTONE_COLUMN].select.selected_options[0].text #cell is hard-coded, getting this value was very problematic
  end

  element(:course_url) { |b| b.frm.text_field(id: "course_url_control") }
  element(:requires_evaluation) { |b| b.frm.checkbox(id: "is_evaluated_control") }
  element(:honors_flag) { |b| b.frm.checkbox(id: "is_honors_offering_control") }

  #validation error dialog
  element(:validation_error_dialog_div)  { |b| b.frm.div(class: "fancybox-wrap fancybox-desktop fancybox-type-html fancybox-opened") }
  value(:validation_error_dialog_text) { |b| b.validation_error_dialog_div.div(index: 2).text }
  action(:close_validation_error_dialog) { |b| b.validation_error_dialog_div.div(title: "Close").click}
  #validation error dialog

  #break colo dialog
  element(:break_colocation_dialog_div)  { |b| b.frm.div(class: /fancybox-opened/) }
  value(:break_colocation_dialog_text) { |b| b.break_colocation_dialog_div.div(index: 2).text } #TODO
  action(:break_colocation) { |b| b.break_colocation_dialog_div.button(id: "edit_ao_breakcolo").click; b.loading.wait_while_present}
  action(:cancel_break_colocation) { |b| b.break_colocation_dialog_div.link(id: "button_close").click}
  #validation break colo dialog
end