class ViewPopulationDiag < PopulationsBase

  expected_element :close_button_element

  def frm
    self.iframe(class: "fancybox-iframe")
  end

  include PopulationsSearch

  value(:name) { |b| b.frm.div(id: 'name_inquiry').pre.text }
  value(:description) { |b| b.frm.div(id: 'desc_inquiry').text[/(?<=\n).*/] }
  value(:state) { |b| b.frm.div(id: 'state_inquiry').text[/(?<=\n).*/] }
  value(:rule) { |b| b.frm.div(id: 'rule_inquiry').text[/(?<=\n).*/] }
  element(:close_button_element) { |b| b.frm.button(text: "Close")}
  action(:close) { |b| b.close_button_element.click;b.loading.wait_while_present}

end