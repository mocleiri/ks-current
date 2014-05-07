class OrgLookupPopUp < BasePage

  wrapper_elements
  green_search_buttons

  expected_element :short_name

  element(:short_name) { |b| b.frm.text_field(name: "lookupCriteria[shortName]") }
  element(:long_name) { |b| b.frm.text_field(name: "lookupCriteria[longName]") }
  element(:results_table) { |b| b.frm.table(id: 'uLookupResults_layout')}

  def frm
    self.iframe(class: "fancybox-iframe") # Persistent ID needed!
  end

  def return_value(id)
    target_org_row(id).wait_until_present
    target_org_row(id).link(text: "Select").wait_until_present
    begin
      target_org_row(id).link(text: "Select").click
      rescue Timeout::Error => e
      puts "rescued Select timeout"
    end
    loading.wait_while_present
  end

  def target_org_row(id)
    results_table.rows.each do |row|
      return row if row.cells[1].text.match /^#{Regexp.escape(id.to_s)}$/
    end
    return nil
  end

end