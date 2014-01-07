class EditAcademicCalendar < BasePage

  wrapper_elements
  frame_element
  include CalendarStickyFooter

  expected_element :header_calendar_name

  element(:header_calendar_name) { |b| b.frm.div(class: "ks-unified-header ks-unified-header").h1.span}

  element(:page_validation_list_exists) { |b| b.frm.ul(id: "pageValidationList").exists?}
  element(:page_error_message_exists) { |b| b.frm.ul(id: "pageValidationList").li(class: "uif-errorMessageItem").exists?}
  element(:page_info_message) { |b| b.frm.ul(id: "pageValidationList").exists? and b.frm.ul(id: "pageValidationList").li(class: "uif-infoMessageItem").exists?}
  value(:page_info_message_text) { |b| b.frm.ul(id: "pageValidationList").li(class: "uif-infoMessageItem").text}
  action(:calendar_tab) { |b| b.frm.link(text: "Calendar").click }
  action(:terms_tab) { |b| b.frm.link(text: "Terms").click }
  
  element(:academic_calendar_name) { |b| b.frm.text_field(name: "academicCalendarInfo.name") }
  element(:calendar_start_date) { |b| b.frm.text_field(name: "academicCalendarInfo.startDate") }
  element(:calendar_end_date) { |b| b.frm.text_field(name: "academicCalendarInfo.endDate") }

  element(:event_toggle_link) { |b| b.frm.link(id: "acal-info-event_toggle") }

  def open_events_section()
    if event_toggle_link.image(alt: "collapse").visible? then # collapse means collapsed
      event_toggle_link.click
      sleep 1
    end
  end


  element(:event_type) { |b| b.frm.select(name: "newCollectionLines['events'].eventTypeKey") }
  element(:event_start_date) { |b| b.frm.text_field(name: "newCollectionLines['events'].startDate") }
  element(:event_end_date) { |b| b.frm.text_field(name: "newCollectionLines['events'].endDate") }
  element(:event_start_time) { |b| b.frm.text_field(name: "newCollectionLines['events'].startTime") }
  element(:event_end_time) { |b| b.frm.text_field(name: "newCollectionLines['events'].endTime") }
  element(:event_start_am) { |b| b.frm.radio(name: "newCollectionLines['events'].startTimeAmPm", value: "AM") }
  element(:event_start_pm) { |b| b.frm.radio(name: "newCollectionLines['events'].startTimeAmPm", value: "PM") }
  element(:event_end_am) { |b| b.frm.radio(name: "newCollectionLines['events'].endTimeAmPm", value: "AM") }
  element(:event_end_pm) { |b| b.frm.radio(name: "newCollectionLines['events'].endTimeAmPm", value: "PM") }
  action(:event_start_am_set) { |b| b.event_start_am.set; b.loading.wait_while_present}
  action(:event_start_pm_set) { |b| b.event_start_pm.set; b.loading.wait_while_present}
  action(:event_end_am_set) { |b| b.event_end_am.set; b.loading.wait_while_present}
  action(:event_end_pm_set) { |b| b.event_end_pm.set; b.loading.wait_while_present}
  element(:add_event) { |b| b.frm.link(id: "acal-info-event_add") }
  element(:acal_event_list_div) { |b| b.frm.div(id: "acal-info-event") }
  element(:acal_event_list_link) { |b| b.acal_event_list_div.link(text: "Events") }
  element(:calendar_events_table) { |b| b.acal_event_list_div.table }

  element(:make_official_link) { |b| b.frm.link(id: "acal_Official") }
  action(:make_official) { |b| b.make_official_link.click; b.loading.wait_while_present }

  ###### confirm make official dialog
  element(:make_official_dialog_div) { |b| b.frm.div(id: "KS-AcademicCalendar-ConfirmCalendarOfficial-Dialog") }
  action(:make_offical_confirm) { |b| b.make_official_dialog_div.radio(index: 0).click; b.loading.wait_while_present }
  action(:make_offical_cancel) { |b| b.make_official_dialog_div.radio(index: 1).click ; b.loading.wait_while_present}
  ########

  ###### confirm delete dialog
  element(:delete_dialog_div) { |b| b.frm.div(id: "KS-AcademicCalendar-ConfirmDelete-Dialog") }
  action(:confirm_delete) { |b| b.delete_dialog_div.radio(index: 0).click; b.loading.wait_while_present }
  action(:cancel_delete) { |b| b.delete_dialog_div.radio(index: 1).click ; b.loading.wait_while_present}
  ########

  element(:add_holiday_calendar_div) { |b| b.frm.div(id: "acal-holidays_disclosureContent") }
  element(:add_holiday_calendar_select) { |b| b.add_holiday_calendar_div.select }
  element(:add_holiday_calendar_button) { |b| b.add_holiday_calendar_div.button(id: "acal-holidays_add") }
  action(:add_holiday_calendar)  { |b| b.add_holiday_calendar_button.click; b.loading.wait_while_present }

  def edit_start_date(row, value); row.cells[EDIT_START_DATE_COL].text_field.set(value); end
  def edit_start_time(row, value); row.cells[EDIT_START_TIME_COL].text_field.set(value); end
  def edit_start_ampm(row, value); row.cells[EDIT_START_TIME_AMPM_COL].radio.select(value.downcase); end
  def edit_end_date(row, value); row.cells[EDIT_END_DATE_COL].text_field.set(value); end
  def edit_end_time(row, value); row.cells[EDIT_END_TIME_COL].text_field.set(value); end
  def edit_end_ampm(row, value); row.cells[EDIT_END_TIME_AMPM_COL].radio.select(value.downcase); end
  def delete(row); row.cells[EDIT_ACTION_COL].button(id: /acal-info-event_del_line*/).click; end

  #identify the row containing this event
  def target_event_row_in_edit(event_name)
    calendar_events_table.rows.each do |row|
      if row.cells[EDIT_EVENT_COL].text == event_name then
        return row
      end
    end
    return nil
  end

  EDIT_EVENT_COL = 0
  EDIT_START_DATE_COL = 1
  EDIT_START_TIME_COL = 2
  EDIT_START_TIME_AMPM_COL = 3
  EDIT_END_DATE_COL = 4
  EDIT_END_TIME_COL = 5
  EDIT_END_TIME_AMPM_COL = 6
  EDIT_ACTION_COL = 7

  def holiday_cal_index_by_name(cal_name)
    add_holiday_calendar_div.link(text: /^#{cal_name}$/).id[/\d+(?=_toggle)/]
  end

  def delete_holiday_cal(cal_name)
    hcal_index = holiday_cal_index_by_name(cal_name)
    #            need persistent id:
    add_holiday_calendar_div.div(id: /u.*_line#{hcal_index}.*/).link(text: /Delete/).click
    loading.wait_while_present
  end

end