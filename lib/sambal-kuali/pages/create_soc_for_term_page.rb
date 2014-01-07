class CreateSocForTerm < BasePage

  page_url "#{$test_site}/kr-krad/createSoc?viewId=createSocView&pageId=selectTermForSocCreation&methodToCall=start"

  wrapper_elements
  frame_element

  expected_element :term_code_container

  #element(:frm) { |b| b.frame(id: /easyXDM/).frame(id: "iframeportlet") }

  element(:term_code_container) { |b| b.frm.div(data_label: "Term") }
  element(:term_code){|b| b.term_code_container.text_field }

  element(:submit) { |b| b.frm.button(text: "Go").click; b.loading.wait_while_present }
end