class ReinstateActivityOffering < BasePage

  #expected_title /Kuali :: Course Offerings/

  wrapper_elements
  frame_element
  validation_elements

  expected_element :reinstate_activity_button

  element(:warning_message_div) { |b| b.frm.div(id: "KS-SelectedAoReinstateConfirmation-WarningMessage") }

  element(:sticky_footer_div) { |b| b.frm.div(class: "ks-uif-footer uif-stickyFooter uif-stickyButtonFooter") }

  element(:reinstate_activity_button) { |b| b.frm.button(id: "AoReinstateConfirmationCancelButton") }
  action(:reinstate_activity) { |b| b.reinstate_activity_button.click; b.loading.wait_while_present(120) }
  element(:cancel_button) { |b| b.sticky_footer_div.button(text: "Cancel") }
  action(:cancel) { |b| b.cancel_button.click; b.loading.wait_while_present(120) }

  def warning_msg_present(message)
    return warning_message_div.span(text: /#{Regexp.escape(message)}/).exists?
  end

end