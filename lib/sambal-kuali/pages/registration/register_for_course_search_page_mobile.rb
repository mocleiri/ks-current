class RegisterForCourseSearchMobile < BasePage

  page_url "#{$test_site}/kscr-poc/index.jsp"
  expected_element :cr_header_div

  element(:cr_header_div) { |b| b.frm.div(class: "kscr-Header") }
  element(:search_link) { |b| b.cr_header_div.link(text: "Search") }
  action(:search) { |b| b.search_link.click; b.loading.wait_while_present }
  element(:cart_link) { |b| b.cr_header_div.link(text: "Cart") }
  element(:schedule_link) { |b| b.cr_header_div.link(text: "Schedule") }
  element(:change_term_link) { |b| b.frm.link(class: "kscr-TermContext-currentChange") }
  action(:change_term) { |b| b.change_term_link.click; b.loading.wait_while_present }

  element(:term_select) { |b| b.frm.select(id: "searchTerm") }

  element(:course_input_div){ |b| b.frm.div(class: "kscr-Search-query") }
  element(:course_input){ |b| b.frm.text_field(id: "searchCourses") }
  element(:course_input_button_span){ |b| b.course_input_div.span(class: "kscr-Search-query") }
  element(:course_input_button) { |b| b.course_input_button_span.button }
  action(:search_for_course) { |b| b.course_input_button.click; b.loading.wait_while_present }

  element(:return_to_results_link) { |b| b.frm.link(text: "Return to your results") }
  action(:return_to_results) { |b| b.return_to_results_link.click; b.loading.wait_while_present }

  def select_term(term)
    term_select.select(term)
  end

  def search_for_a_course(course)
    course_input.set course
    search_for_course
  end
end