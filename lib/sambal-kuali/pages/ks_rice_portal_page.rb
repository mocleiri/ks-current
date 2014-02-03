class KSRicePortal < BasePage

  page_url "#{$test_site}/portal.do"
  wrapper_elements

  element(:course_search_link) { |b| b.link(text: "KSAP Course Search") }
  action(:course_search_home) { |b| b.course_search_link.click }


  element(:course_planner_link) { |b| b.link(text:"KSAP Planner") }
  action(:course_planner_home) { |b| b.course_planner_link.click }

end