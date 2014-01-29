class RegisterForCourseResults < RegisterForCourseSearchBase
  page_url "#{$test_site}/kscr-poc/index.jsp"

  # all resolutions
  element(:results_count_section) { |b| b.frm.section(class: "kscr-Results") }
  element(:results_count) { |b| b.results_count_section.p(class: "kscr-Results-Count").text }

  element(:results_list) { |b| b.frm.ol(class: "kscr-COList") }
  element(:results_list_collection) { |b| b.results_list.lis }
  element(:results_list_item) { |index, b| b.results_list.li[index] }
  element(:results_item_section)  { |index, b| b.results_list_item(index).section(class: "kscr-COItem") }

  # desktop resolution only
  element(:results_message_section)  { |b| b.frm.section(class: "kscr-Results medium-5 column") }
  element(:results_message_div)  { |b| b.frm.div(class: "medium-7 column hide-for-small-only") }
  element(:details_message) { |b| b.results_message_div.h2}

  # mobile resolution only
  element(:results_detail_section)  { |b| b.frm.section(class: "kscr-Article ng-scope") }
  element(:results_detail_course_code)  { |b| b.results_detail_section.h1(class: "kscr-Article-header ng-binding") }
  element(:results_detail_grading_options)  { |b| b.results_detail_section.p(index: 0) }

  def target_list_item_by_index(ind)
    results_list_collection[ind].wait_until_present
    results_list_collection[ind]
  end

  def results_link(index)
    item = target_list_item_by_index(index)
    item.link(class: "kscr-COItem-code")
  end

  def results_course_code(index)
    item = target_list_item_by_index (index)
    item.link(class: "kscr-COItem-code").text
  end

  def results_course_title index
    item = target_list_item_by_index (index)
    item.span(class: "kscr-COItem-title").text
  end

end