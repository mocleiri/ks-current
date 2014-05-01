class PersonnelLookup < BasePage

  wrapper_elements
  green_search_buttons
  expected_element :principal_name

  def frm
    self.iframe(class: "fancybox-iframe")
  end

  element(:principal_name) { |b| b.frm.div(data_label: "Name").text_field }
  element(:id_field) { |b| b.frm.div(data_label: "ID").text_field }
  element(:last_name) { |b| b.frm.div(data_label: "Last Name").text_field }
  element(:results_table) { |b| b.frm.table(id: 'uLookupResults_layout') }

  element(:paginate_links_span) { |b| b.frm.div(class: "dataTables_paginate paging_full_numbers").span() }

  # Clicks the 'Select' link for the named row
  def return_value(principal_name)
    target_row(principal_name).wait_until_present
    target_row(principal_name).link(text: "Select").wait_until_present
    begin
      target_row(principal_name).link(text: "Select").click
    rescue Timeout::Error => e
      puts "rescued target_row personnel lookup"
    end
    loading.wait_while_present
  end

  NAME_COLUMN = 1
  ID_COLUMN = 2
  def get_long_name(principal_name)
    target_row(principal_name).wait_until_present
    target_row(principal_name).cells[NAME_COLUMN].text
  end

  def target_row(id)
    results_table.wait_until_present
    results_table.rows.each do |r|
      if (r.cells[ID_COLUMN].text == id)
        return r
      end
    end
  end

  def change_results_page(page_number)
    results_table.wait_until_present
   paginate_links_span.link(text: "#{page_number}").click
  end
end