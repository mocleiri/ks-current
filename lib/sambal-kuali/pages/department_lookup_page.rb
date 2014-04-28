class DepartmentLookup < BasePage

  wrapper_elements
  green_search_buttons
  expected_element :short_name

  def frm
    self.iframe(class: "fancybox-iframe")
  end

  element(:short_name) { |b| b.frm.div(data_label: "Abbreviation").text_field }
  element(:long_name) { |b| b.frm.div(data_label: "Name").text_field }
  element(:results_table) { |b| b.frm.div(id: "uLookupResults").table(index: 0) }

  element(:paginate_links_span) { |b| b.frm.div(class: "dataTables_paginate paging_full_numbers").span() }

  # Clicks the 'Select' link for the named row
  def return_value(long_name)
    target_row(long_name).wait_until_present
    target_row(long_name).link(text: "Select").wait_until_present
    begin
      target_row(long_name).link(text: "Select").click
    rescue Timeout::Error => e
      puts "rescued target_row dept lookup"
    end
    loading.wait_while_present
  end

  def target_row(long_name)
    results_table.row(text: /^\b#{long_name}\b$/)
  end

  def change_results_page(page_number)
    results_table.wait_until_present
   paginate_links_span.link(text: "#{page_number}").click
  end
end