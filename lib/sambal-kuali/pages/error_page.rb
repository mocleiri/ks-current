class ErrorPage < BasePage
  wrapper_elements
  frame_element

  expected_element :error_message_element

  value(:error_message){|b| b.error_message_element.text}
  element(:error_message_element) { |b| b.frm.div(id: "Uif-Application").div(class: "error_403 uif-validationMessages")}

  def error_401
    present = error_message.include? "Error 403: This page contains restricted content"
    return present
  end
end