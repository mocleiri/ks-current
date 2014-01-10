class CreateCourseOffering < BasePage

  #expected_title /Kuali :: Create Course Offering/

  wrapper_elements
  frame_element
  validation_elements

  expected_element :target_term

  element(:target_term) { |b| b.frm.text_field(id: "targetTermCode_control")}
  element(:catalogue_course_code) { |b| b.frm.text_field(id: "catalogCourseCode_control") }

  element(:create_from_div) { |b| b.frm.div(id: "KS-CourseOffering-CreateChoiceSection") }
  action(:choose_from_catalog) { |b| b.frm.radio(index: 0).click; b.loading.wait_while_present(120) }
  action(:choose_from_existing) { |b| b.frm.radio(index: 1).click; b.loading.wait_while_present(120) }

  action(:continue) { |b| b.frm.button(id: "continueFromCreate_btn").click; b.loading.wait_while_present(120) }
  element(:sticky_footer_div) { |b| b.frm.div(class: "ks-uif-footer uif-stickyFooter uif-stickyButtonFooter") }
  action(:cancel) { |b| b.sticky_footer_div.link(text: "Cancel").click; b.loading.wait_while_present(120) }
end