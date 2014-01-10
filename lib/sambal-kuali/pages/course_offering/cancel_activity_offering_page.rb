class CancelActivityOffering < BasePage

  #expected_title /Kuali :: Course Offerings/

  wrapper_elements
  frame_element
  validation_elements

  expected_element :cancel_activity_button

  element(:warning_message_div) { |b| b.frm.div(id: "KS-SelectedAoCancelConfirmation-WarningMessage") }

  element(:sticky_footer_div) { |b| b.frm.div(class: "ks-uif-footer uif-stickyFooter uif-stickyButtonFooter") }

  element(:cancel_activity_button) { |b| b.frm.button(id: "AoCancelConfirmationCancelButton") }
  action(:cancel_activity) { |b| b.cancel_activity_button.click; b.loading.wait_while_present(120) }
  element(:cancel_button) { |b| b.sticky_footer_div.link(text: "Cancel") }
  action(:cancel) { |b| b.cancel_button.click; b.loading.wait_while_present(120) }

  def warning_msg_present(message)
    return warning_message_div.span(text: message).exists?
  end

end