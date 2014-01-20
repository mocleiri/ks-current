class CoursePlannerPage < BasePage

  page_url = "#{$test_site}myplan/course?methodToCall=start&viewId=CourseSearch-FormView"

  wrapper_elements
  frame_element

  #ELEMENTS ADD COURSE POPUP
  expected_element :course_planner_header

  element(:course_planner_header) { |b| b.div(text: "Fall 2013 - Summer I 2014") }

  #10 - planner page elements
  action(:current_term_add) { |b| b.div(id: "2013Fall_planned_add").a(class: "uif-actionLink uif-boxLayoutHorizontalItem").click }
  action(:future_term_add) { |b| b.div(id: "2014Summer1_planned_add").a(class: "uif-actionLink uif-boxLayoutHorizontalItem").click }

  #20 - add to plan popover elements
  element(:course_code_text) { |b| b.frm.text_field(name: "courseCd") }
  element(:credit) { |b| b.frm.text_field(name: "courseCredit") }
  element(:notes) { |b| b.frm.text_field(name: "courseNote") }
  element(:course_code_current_term) { |b| b.div(id:"kuali-atp-2013Fall_planned_BSCI430_code").span(class: "uif-message") }
  element(:course_code_future_term) { |b| b.div(id:"kuali-atp-2014Summer1_planned_ENGL388_code" ).span(class: "uif-message") }
  action(:course_code_current_term_click) { |b| b.div(id:"kuali-atp-2013Fall_planned_BSCI430_code").span(class: "uif-message").click }
  action(:course_code_future_term_click) { |b| b.div(id:"kuali-atp-2014Summer1_planned_ENGL388_code" ).span(class: "uif-message").click }
  action(:add_to_plan) { |b| b.frm.button(text: "Add to Plan").click }

  #30 - right click operations
  action(:edit_plan_item_click) { |b| b.td(class: "jquerybubblepopup-innerHtml").a(:id => /planner_menu_edit_plan_item*/).click }
  action(:current_term_edit_plan_item_click) { |b| b.course_code_current_term.click; b.edit_plan_item.click }
  action(:future_term_edit_plan_item_click) { |b| b.course_code_future_term.click; b.edit_plan_item.click }
  action(:course_code_delete_click) { |b| b.td(class: "jquerybubblepopup-innerHtml").a(:id => /planner_menu_delete_plan_item*/).click }

  #40 - view course details popover elements
  element(:course_code_current_term_credit) { |b| b.div(class: "uif-messageField credit ks-plan-Bucket-itemCreditCount uif-boxLayoutHorizontalItem").span(class: "uif-message").text }
  element(:view_notes_popover) { |b| b.textarea(name: "courseNote").text }
  element(:view_variable_credit_popover) { |b| b.input(name: "courseCredit").value }
  element(:edit_plan_cancel_link) { |b| b.frm.link(text: "Cancel") }
  action(:edit_plan_cancel) { |b| b.edit_plan_cancel_link.click }

  #50 - delete course popover
  element(:delete_course) { |b| b.frm.button(text: "Delete") }
  action(:delete_course_click) { |b| b.delete_course.click }

end

