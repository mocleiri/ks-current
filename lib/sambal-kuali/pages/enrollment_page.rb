class Enrollment < BasePage

  page_url "#{$test_site}/kr-krad/launch?viewId=enrollmentHomeView&methodToCall=start&viewId=enrollmentHomeView"
  expected_element :home_link

  wrapper_elements
  frame_element

  element(:enrol_home_view) { |b| b.frm.div(id: "enrollmentHomeView") }
#  element(:home_link) { |b| b.frm.link(text: "Home") }
  element(:home_link_section) {|b| b.frm.div(id: "Uif-BreadcrumbWrapper")}
#  element(:home_link) {|b| b.frm.div(id: "Uif-BreadcrumbWrapper")}
  element(:home_link) { |b| b.frm.link(id: "KS-HomewardPathBreadcrumbs-Home")  }

  #Course Offerings
  action(:create_course_offerings) { |p| p.frm.link(text: "Create Course Offerings").click }
  action(:manage_course_offerings) { |p| p.frm.link(text: "Manage Course Offerings").click }
  action(:schedule_of_classes) { |p| p.frm.link(text: "Schedule of Classes").click }

  #Calendars
  action(:search_for_calendar_or_term) { |p| p.frm.link(text: "Search for Calendar or Term").click }
  action(:create_academic_calendar) { |p| p.frm.link(text: "Create Academic Calendar").click }
  action(:create_holiday_calendar) { |p| p.frm.link(text: "Create Holiday Calendar").click }

  #Administration
  action(:perform_rollover) { |p| p.frm.link(text: "Perform Rollover").click }
  action(:view_rollover_details) { |p| p.frm.link(text: "View Rollover Details").click }
  action(:manage_soc) { |p| p.frm.link(text: "Manage Set of Courses").click }
  action(:manage_final_exam_matrix) { |b| b.frm.link(text: "Manage Final Exam Matrix").click }
  action(:manage_time_slots) { |p| p.frm.link(text: "Manage Time Slots").click }
  action(:manage_populations) { |p| p.frm.link(text: "Manage Populations").click }
end