class RegisterForCourseSearch < RegisterForCourseSearchBase
  page_url "#{$test_site}/kscr-poc/index.jsp"
  expected_element :cr_header_div

  def frm
    self.frame(id: "frame")
  end

  element(:course_input_div){ |b| b.frm.div(class: "kscr-Search-item kscr-Query util-Grid") }
  element(:course_input){ |b| b.frm.text_field(id: "searchInput") }
  element(:course_input_button) { |b| b.course_input_div.button }
  action(:begin_course_search) { |b| b.course_input_button.click}

  element(:return_to_results_link) { |b| b.frm.link(text: "Return to your results") }
  action(:return_to_results) { |b| b.return_to_results_link.click; b.loading.wait_while_present }

  def select_term(term)
    term_select.select(term)
  end

  def search_for_a_course(course)
    course_input.set course
    begin_course_search
  end
end