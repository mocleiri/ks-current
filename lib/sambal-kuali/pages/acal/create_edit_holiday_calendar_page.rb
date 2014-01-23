class CreateEditHolidayCalendar < BasePage #Create/Edit are the same page (only Header is different)

  wrapper_elements
  frame_element
  include CalendarStickyFooter

  expected_element :calendar_name

  element(:calendar_name) { |b| b.frm.div(data_label: "Holiday Calendar Name").text_field }
  element(:start_date) { |b| b.frm.div(data_label: "Start Date").text_field }
  element(:end_date) { |b| b.frm.div(data_label: "End Date").text_field }

  element(:make_official_link) { |b| b.frm.link(id: "hcal_Official") }
  action(:make_official) { |b| b.make_official_link.click; b.loading.wait_while_present }

  element(:holiday_calendar_validation_messages) { |b| b.frm.div(id: "KS-HolidayCalendar-MetaSection").ul(class: "uif-validationMessagesList").lis }
  element(:holiday_validation_messages) { |b| b.frm.div(id: "KS-HolidayCalendar-HolidaySection").ul(class: "uif-validationMessagesList").lis }
  element(:holiday_table) { |b| b.frm.div(id: "KS-HolidayCalendar-HolidaySection").table }

  element(:add_holiday_btn) { |b| b.frm.button(text: "Add Holiday")}
  action(:add_holiday) { |b| b.add_holiday_btn.click; b.loading.wait_while_present}

  HOLIDAY_TYPE = 0
  START_DATE = 1
  START_TIME = 2
  START_AMPM = 3
  END_DATE = 4
  END_TIME = 5
  END_AMPM = 6
  INSTRUCTIONAL = 7
  ACTIONS = 8
  #element(:add_event_type) { |b| b.calendar_events_table.rows[-2].select(id: /event_type_line/) }
  element(:holiday_type) { |b| b.holiday_table.rows[-2].cells[HOLIDAY_TYPE].select() }
  element(:holiday_start_date) { |b| b.holiday_table.rows[-2].cells[START_DATE].text_field() }
  element(:holiday_start_time) { |b| b.holiday_table.rows[-2].cells[START_TIME].text_field() }
  element(:holiday_start_am) { |b| b.holiday_table.rows[-2].cells[START_AMPM].radio() }
  element(:holiday_start_pm) { |b| b.holiday_table.rows[-2].cells[START_AMPM].radio(index: 1) }
  element(:holiday_end_date) { |b| b.holiday_table.rows[-2].cells[END_DATE].text_field() }
  element(:holiday_end_time) { |b| b.holiday_table.rows[-2].cells[END_TIME].text_field() }
  element(:holiday_end_meridian_am) { |b| b.holiday_table.rows[-2].cells[END_AMPM].radio() }
  element(:holiday_end_meridian_pm) { |b| b.holiday_table.rows[-2].cells[END_AMPM].radio(index: 1) }
  element(:instructional) { |b| b.holiday_table.rows[-2].cells[INSTRUCTIONAL].checkbox() }
  element(:delete_link) { |b| b.holiday_table.rows[-2].cells[ACTIONS].link() }

  def target_row(holiday_type)
    holiday_table.row(text: /^\b#{holiday_type}\b$/)
  end

  def delete_holiday(holiday_type)
    target_row(holiday_type).link(text: "Delete").click
    loading.wait_while_present
  end

  def edit_start_date(holiday_type, date)
    target_row(holiday_type).text_field(name: /startDate/).set date
  end

  def edit_start_time(holiday_type, time, meridian)
    target_row(holiday_type).text_field(name: /startTime/).set time
    if meridian == "am" then
      target_row(holiday_type).text_field(name: /startTimeAmPm/, value: "AM").set
    else
      target_row(holiday_type).text_field(name: /startTimeAmPm/, value: "PM").set
    end
  end

  def edit_end_date(holiday_type, date)
    target_row(holiday_type).text_field(name: /endDate/).set date
  end

  def edit_end_time(holiday_type, time, meridian)
    target_row(holiday_type).text_field(name: /endTime/).set time
    if meridian == "am" then
      target_row(holiday_type).text_field(name: /endTimeAmPm/, value: "AM").set
    else
      target_row(holiday_type).text_field(name: /endTimeAmPm/, value: "PM").set
    end
  end

  def edit_instructional(holiday_type, boolean_set )
    if boolean_set then
      target_row(holiday_type).checkbox(name: /instructional/).set
    else
      target_row(holiday_type).checkbox(name: /instructional/).clear
    end
  end

  def toggle_instructional(holiday_type)
    if target_row(holiday_type).checkbox(name: /instructional/).set?
      target_row(holiday_type).checkbox(name: /instructional/).clear
    else
      target_row(holiday_type).checkbox(name: /instructional/).set
    end
  end

  # Returns a random item from the list of holidays
  def select_random_holiday
    holidays = []
    #wait_until { holiday_type.enabled? }
    sleep 2
    holiday_type.options.each { |opt| holidays << opt.text }
    holidays.delete_if { |item| item == "Select holiday type" }
    holidays[rand(holidays.length)]
  end

end