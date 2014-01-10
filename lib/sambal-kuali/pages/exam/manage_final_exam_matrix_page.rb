class ManageFEMatrix < BasePage

  wrapper_elements
  frame_element

  expected_element :term_type_select

  element(:fe_search_page) { |b| b.frm.div( id: "finalExamMatrixManagementView")}

  element(:term_type_select) { |b| b.frm.select( id: "termType_control")}
  #action(:term_type) { |b| b.term_type_select.select}
  element(:show_btn) { |b| b.frm.button( id: "show_button")}
  action(:show) { |b| b.show_btn.click; b.loading.wait_while_present}
end