class ManualSocStateChangePage < BasePage

  page_url "#{$test_site}/kr-krad/testStatePropagation?viewId=testStatePropagationView&pageId=aft-changeSocStatePage&methodToCall=start"

  wrapper_elements
  frame_element
  expected_element :change_soc_state_termCode

  #element(:frm) { |b| b.frame(id: /easyXDM/).frame(id: "iframeportlet") }

  element(:change_soc_state_termCode) { |b| b.frm.text_field(name: "termCodeForSocStateChange") }
  element(:change_soc_state_newSocState) { |b| b.frm.text_field(name: "newSocStateForSocStateChange") }
  element(:change_soc_state_button) { |b| b.frm.button(text: "Change SOC State") }
  action(:change_soc_state) { |b| b.change_soc_state_button.click; b.loading.wait_while_present(60) }

end
