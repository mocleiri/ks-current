class CmAdvancedSearchPage < BasePage

  expected_element :jointly_offered_given_name

  def frm
    self.frame(class: "fancybox-iframe")
  end


  element(:jointly_offered_search_by) { |b| b.frm.select(id: "joints_search.searchBy_control") }
  element(:jointly_offered_given_name) { |b| b.frm.text_field(id: "joints_search.courseTitle_control"); b.wait_until_present(120) }
  element(:jointly_offered_course_code) { |b| b.frm.text_field(id: "joints_search.courseCode_control") }
  element(:jointly_offered_text) { |b| b.frm.text_field(id: "joints_search.descr.plain_control") }
  action(:jointly_offered_search) { |b| b.frm.button(id: "button_search").click}
  action(:select_result) { |b| b.frm.a(id: /line0/).click}
  element(:results_table) { |b| b.frm.table(id: "uLookupResults_layout")}



  def return_value(search_text)
    target_row(search_text).wait_until_present(60)
    target_row(search_text).link(text: "Select").wait_until_present(60)
    begin
      target_row(search_text).link(text: "Select").click
    rescue Timeout::Error => e
      puts "rescued target_row dept lookup"
    end
    #loading.wait_while_present
  end

  def target_row(search_text)
    results_table.row(text: /#{search_text}/)
  end

  def select_first_row
    results_table.link(text:"Select").wait_until_present(60)
  end

end