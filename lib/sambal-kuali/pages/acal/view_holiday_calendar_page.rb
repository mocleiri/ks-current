class ViewHolidayCalendar < BasePage

  frame_element
  wrapper_elements

  expected_element :hcal_overview_div

  element(:hcal_overview_div) { |b| b.frm.div(id: "KS-HolidayCalendar-MetaSection") }
  value(:hcal_name) { |b| b.hcal_overview_div.div(data_label: "Holiday Calendar Name").span(index: 1).text }
  value(:hcal_start_date) { |b| b.hcal_overview_div.div(data_label: "Start Date").span(index: 1).text }
  value(:hcal_end_date) { |b| b.hcal_overview_div.div(data_label: "End Date").span(index: 1).text }
  #value(:hcal_state) { |b| b.hcal_overview_div.div(data_label: "State").span(index: 0).text }

  element(:make_official_link) { |b| b.frm.link(id: "hcal_Official") }
  action(:make_official) { |b| b.make_official_link.click; b.loading.wait_while_present }

  value(:hcal_status) { |b| b.span(id: "HCAL-Official-Lozenge_span").text }

  #element(:hcal_info_link) { |b| b.frm.link(id: "KS-HolidayCalendar-View-Meta_toggle") }

  def open_hcal_info_section
    link =  hcal_info_link
    if link.image.attribute_value("alt") == "collapse" then # expand means is already expanded
      link.click
    end
  end

  element(:holiday_list_table) { |b| b.frm.div(id: "KS-HolidayCalendar-HolidaySection").table }
  HOLIDAY_TYPE_COL = 0
  HOLIDAY_START_DATE_COL = 1
  HOLIDAY_END_DATE_COL = 2
  HOLIDAY_INSTRUCTIONAL_COL = 3

  def target_row holiday_type
    holiday_list_table.row(text: /^\b#{holiday_type}\b$/)
  end

  def holiday_start_date holiday_type
    target_row(holiday_type).cells[HOLIDAY_START_DATE_COL].text
  end

  def holiday_end_date holiday_type
    target_row(holiday_type).cells[HOLIDAY_END_DATE_COL].text
  end

  def holiday_instructional_status holiday_type
    true_false_text = target_row(holiday_type).cells[HOLIDAY_INSTRUCTIONAL_COL].text
    return true_false_text == "Yes"
  end

end
