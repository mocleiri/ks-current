class StatePropagationTest < BasePage

  page_url "#{$test_site}/kr-krad/testStatePropagation?viewId=testStatePropagationView&pageId=testStatePropagationPageId&methodToCall=start"

  expected_element :perform_test_button

  wrapper_elements
  frame_element

  element(:frame_only) { |b| b.frm}
  element(:header) { |b| b.frm.text_field(name: "Propagation") }
  element(:perform_test_button) { |b| b.frm.button(text: "Test State Propagation") }
  action(:perform_test) { |b| b.perform_test_button.click; b.loading.wait_while_present(900)}

  #element(:results_table_div) { |b| b.frm.div(id: "KS-TestStatePropagationTable") }
  element(:ao_results_table_div) { |b| b.frm.div(id: "aoTestResultsSection") }
  element(:ao_results_table_header) { |b| b.ao_results_table_div.span(class: "uif-headerText-span") }
  element(:ao_results_table) { |b| b.ao_results_table_div.table }

  element(:fo_results_table_div) { |b| b.frm.div(id: "foTestResultsSection") }
  element(:fo_results_table_header) { |b| b.fo_results_table_div.span(class: "uif-headerText-span") }
  element(:fo_results_table) { |b| b.fo_results_table_div.table }

  element(:co_results_table_div) { |b| b.frm.div(id: "coTestResultsSection") }
  element(:co_results_table_header) { |b| b.co_results_table_div.span(class: "uif-headerText-span") }
  element(:co_results_table) { |b| b.co_results_table_div.table }

  element(:rg_ao_results_table_div) { |b| b.frm.div(id: "rgAoListTestResultsSection") }
  element(:rg_ao_results_table_header) { |b| b.rg_ao_results_table_div.span(class: "uif-headerText-span") }
  element(:rg_ao_results_table) { |b| b.rg_ao_results_table_div.table }

  element(:rg_invalid_results_table_div) { |b| b.frm.div(id: "rgInvalidTestResultsSection") }
  element(:rg_invalid_results_table_header) { |b| b.rg_invalid_results_table_div.span(class: "uif-headerText-span") }
  element(:rg_invalid_results_table) { |b| b.rg_invalid_results_table_div.table }

  #These are for the first 3 tables
  AO_TABLE_SOC_STATE_COLUMN = 0
  AO_TABLE_AO_FROM_COLUMN = 1
  AO_TABLE_AO_TO_COLUMN = 2
  AO_TABLE_SECOND_AO_COLUMN = 3
  AO_TABLE_EXPECTED_COLUMN = 4
  AO_TABLE_ACTUAL_COLUMN = 5
  AO_TABLE_STATUS_COLUMN = 6

  #For the 4th table
  RG_AO_TABLE_AO_1_COLUMN = 0
  RG_AO_TABLE_AO_2_COLUMN = 1
  RG_AO_TABLE_AO_3_COLUMN = 2
  RG_AO_TABLE_EXPECTED_COLUMN = 3
  RG_AO_TABLE_ACTUAL_COLUMN = 4
  RG_AO_TABLE_STATUS_COLUMN = 5

  #For the 5th table
  RG_INVALID_TABLE_FROM_COLUMN = 0
  RG_INVALID_TABLE_TO_COLUMN = 1
  RG_INVALID_TABLE_EXPECTED_COLUMN = 2
  RG_INVALID_TABLE_ACTUAL_COLUMN = 3
  RG_INVALID_TABLE_STATUS_COLUMN = 4

end
