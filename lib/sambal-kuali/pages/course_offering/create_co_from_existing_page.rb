class CreateCOFromExisting < BasePage

  wrapper_elements
  frame_element
  validation_elements

  expected_element :exclude_instructor

  action(:create) { |b| b.frm.button(id: "createFromCopy_btn").click; b.loading.wait_while_present(120) }
  #element(:sticky_footer_div) { |b| b.frm.div(id: "u8") } #static id required
  element(:sticky_footer_div) { |b| b.frm.div(class: "ks-uif-footer uif-stickyFooter uif-stickyButtonFooter") }
  action(:cancel) { |b| b.sticky_footer_div.link(text: "Cancel").click; b.loading.wait_while_present }

  element(:exclude_cancelled_aos) { |b| b.frm.label(text: /Exclude cancelled Activity Offerings/) }
  action(:select_exclude_cancelled_aos) { |b| b.exclude_cancelled_aos.wait_until_present; b.exclude_cancelled_aos.click }

  element(:exclude_scheduling) { |b| b.frm.label(text: /Exclude scheduling information/) }
  action(:select_exclude_scheduling) { |b| b.exclude_scheduling.wait_until_present; b.exclude_scheduling.click }

  element(:exclude_instructor) { |b| b.frm.label(text: /Exclude instructor information/) }
  action(:select_exclude_instructor) { |b| b.exclude_instructor.wait_until_present; b.exclude_instructor.click }

  element(:course_offering_existing_table) { |b| b.frm.div(id: "KS-ExistingOffering-ListCOs").table() }

  #TODO just selects the first row - needs to be deprecated
  element(:course_offering_copy_element) {|b| b.frm.course_offering_existing_table.rows[1].cells[ACTIONS_COLUMN_CO].radio.click  }
  action(:course_offering_copy) {|b| b.course_offering_copy_element.click }

  ACTIONS_COLUMN = 0
  CO_CODE_COLUMN = 1
  OFFERED_TERM_COLUMN = 2

 def existing_co_target_row(term, course)
    course_offering_existing_table.row(text: /#{Regexp.escape(course)}[\S\s]#{Regexp.escape(term)}/)
  end

  def select_copy_for_existing_course(term, course)
    #if there is only one row, the radio button is not displayed
    sleep 10
    existing_co_target_row(term, course).cells[ACTIONS_COLUMN].radio.click if existing_co_target_row(term, course).cells[ACTIONS_COLUMN].radio.exists?
  end

end