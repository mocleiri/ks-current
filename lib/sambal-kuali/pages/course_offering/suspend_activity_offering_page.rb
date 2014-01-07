class SuspendActivityOffering < BasePage

  #expected_title /Kuali :: Course Offerings/

  wrapper_elements
  frame_element
  validation_elements

  expected_element :suspend_activity_button

  element(:sticky_footer_div) { |b| b.frm.div(class: "ks-uif-footer uif-stickyFooter uif-stickyButtonFooter") }

  element(:suspend_activity_button) { |b| b.frm.button(id: "AoSuspendConfirmationCancelButton") } # why's it called that?
  action(:suspend_activity) { |b| b.suspend_activity_button.click; b.loading.wait_while_present(120) }
  element(:cancel_button) { |b| b.sticky_footer_div.link(text: "Cancel") }
  action(:cancel) { |b| b.cancel_button.click; b.loading.wait_while_present(120) }

end