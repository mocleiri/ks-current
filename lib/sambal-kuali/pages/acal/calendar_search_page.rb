class CalendarSearch < BasePage

  expected_element :name

  wrapper_elements
  frame_element

  ###### confirm delete dialog
  element(:delete_dialog_div) { |b| b.frm.div(id: "KS-AcademicCalendar-ConfirmDelete-Dialog") }
  action(:confirm_delete) { |b| b.delete_dialog_div.radio(index: 0).click; b.loading.wait_while_present }
  action(:cancel_delete) { |b| b.delete_dialog_div.radio(index: 1).click ; b.loading.wait_while_present}
  ########

  ###### confirm delete dialog
  element(:hcal_delete_dialog_div) { |b| b.frm.div(id: "KS-CalendarSearch-ConfirmDelete-Dialog") }
  action(:hcal_confirm_delete) { |b| b.hcal_delete_dialog_div.radio(index: 0).click; b.loading.wait_while_present }
  action(:hcal_cancel_delete) { |b| b.hcal_delete_dialog_div.radio(index: 1).click ; b.loading.wait_while_present}
  ########

  CALENDAR_NAME = 0
  CALENDAR_START_DATE = 1
  CALENDAR_END_DATE = 2
  CALENDAR_STATUS = 3
  CALENDAR_ACTION = 4

  element(:search_for_select)  { |b| b.frm.select(name: "calendarType") }
  element(:name) { |b| b.frm.text_field(name: "name") }
  element(:year) { |b| b.frm.text_field(name: "year") }
  element(:results_table) { |b| b.frm.div(id: /KS-CalendarSearch-.*ResultSection/).table }

  value(:table_info) { |b| b.frm.div(class: "dataTables_info").text }
  element(:pagination_info_div) { |b| b.frm.div(class: "dataTables_paginate paging_full_numbers") }
  element(:pagination_info) { |b| b.pagination_info_div.link(class: "paginate_active") }
  element(:right_arrow) { |b| b.pagination_info_div.link(class: "next paginate_button") }
  element(:left_arrow) { |b| b.pagination_info_div.link(class: "previous paginate_button") }

  action(:search) { |b| b.frm.button(text: "Search").click; b.loading.wait_while_present }
  action(:next) { |b| b.frm.link(text: "Next").click }
  action(:previous) { |b| b.frm.link(text: "Previous").click }

  def total_results
    table_info[/(?<=of )\d+/]
  end

  def showing_up_to
    table_info[/(?<=to )\d+/]
  end

  def search_for cal_or_term, nm, yr=""
    search_for_select.select cal_or_term
    setnameyear nm, yr
    loading.wait_while_present
  end

  def view calendar
    results_table.row(text: /\b#{calendar}\b/).link(text: "View").click
    loading.wait_while_present
  end

  def edit calendar
    results_table.row(text: /\b#{calendar}\b/).link(text: "Edit").click
    loading.wait_while_present
  end

  def copy calendar
    results_table.row(text: /\b#{calendar}\b/).link(text: "Copy").click
    loading.wait_while_present
  end

  def delete calendar
    results_table.row(text: /\b#{calendar}\b/).link(text: "Delete").click
    confirm_delete
    loading.wait_while_present
  end

  def hcal_delete calendar
    results_table.row(text: /\b#{calendar}\b/).link(text: "Delete").click
    hcal_confirm_delete
    loading.wait_while_present
  end

  def calendar_status calendar
    results_table.row(text: /\b#{calendar}\b/)[CALENDAR_STATUS].text
  end

  def calendar_action_text calendar
    results_table.row(text: /\b#{calendar}\b/)[CALENDAR_ACTION].text
  end

  def results_list
    list = []
    results_table.rows.each do |row|
      list << row[CALENDAR_NAME].text
    end
    list.delete_if { |item| item == "Name" }
    list
  end

  private

  def setnameyear nm, yr
    name.set nm
    year.set yr
    begin
      search
    rescue Timeout::Error => e
      puts "rescued search..."
    end
  end

end