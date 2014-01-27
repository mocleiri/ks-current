class CoursePlannerPage < BasePage

  page_url = "#{$test_site}kr-krad/planner"

  wrapper_elements
  frame_element


  expected_element :course_planner_header
  element(:course_planner_header) { |b| b.div(text: "Fall 2013 - Summer I 2014") }

  #10 - planner page elements
  action(:add_to_term) { |term,b| b.div(id: "#{term}_planned_add").a(class: "uif-actionLink uif-boxLayoutHorizontalItem").click }

  #20 - add to plan popover elements
  element(:course_code_text) { |b| b.frm.text_field(name: "courseCd") }
  element(:credit) { |b| b.frm.text_field(name: "courseCredit") }
  element(:notes) { |b| b.frm.text_field(name: "courseNote") }
  action(:course_code_term) { |term,code,b| b.div(id:"kuali-atp-#{term}_planned_#{code}_code").span(class: "uif-message").text }
  action(:course_code_term_click) { |term,code,b| b.div(id:"kuali-atp-#{term}_planned_#{code}_code").span(class: "uif-message").click }
  action(:add_to_plan) { |b| b.frm.button(text: "Add to Plan").click }

  #30 - right click operations

  action(:edit_plan_item_click) { |b| b.td(class: "jquerybubblepopup-innerHtml").a(:id => /planner_menu_edit_plan_item*/).click }
  action(:course_code_delete_click) { |b| b.td(class: "jquerybubblepopup-innerHtml").a(:id => /planner_menu_delete_plan_item*/).click }

  #40 - view course details popover elements
  element(:course_code_current_term_credit) { |b| b.div(class: "uif-messageField credit ks-plan-Bucket-itemCreditCount uif-boxLayoutHorizontalItem").span(class: "uif-message").text }
  element(:view_notes_popover) { |b| b.textarea(name: "courseNote").text }
  element(:view_variable_credit_popover) { |b| b.input(name: "courseCredit").value }
  element(:edit_plan_cancel_link) { |b| b.frm.link(text: "Cancel") }
  action(:edit_plan_cancel) { |b| b.edit_plan_cancel_link.click }
  element(:save) { |b| b.frm.button(text: "Save")}
  action(:save_click) { |b| b.save.click}

  #50 - delete course popover
  element(:delete_course) { |b| b.frm.button(text: "Delete") }
  action(:delete_course_click) { |b| b.delete_course.click }


  #60 - Verify the course code availability in my plan

  #element(:course_code_term_myplan)  { |b| b.div(id:"kuali-atp-2014Fall_planned_ENGL201_code").span(class: "uif-message") }
  action(:course_code_term_myplan_click) { |b| b.course_code_term_myplan.click}
  element(:course_info_icon) { |b| b.div(class:"uif-group uif-boxGroup uif-verticalBoxGroup uif-boxSection ksap-term-future open ks-plan-TermsList-item ks-plan-TermsList-item--future ks-plan-TermsList-item--open")}
  element(:info_icon) { |b| b.div(class:"uif-group uif-boxGroup uif-verticalBoxGroup uif-boxSection ksap-term-future open ks-plan-TermsList-item ks-plan-TermsList-item--future ks-plan-TermsList-item--open").img(class:"uif-helpImage coursenote uif-boxLayoutHorizontalItem")}
  element(:view_course_summary) {|b| b.td(class:"jquerybubblepopup-innerHtml").a(class:"uif-actionLink uif-boxLayoutVerticalItem clearfix")}
  action(:view_course_summary_click) {|b| b.view_course_summary.click}
  element(:notes_content) {|b| b.textarea(class:"uif-textAreaControl ksap-characterCount ks-plan-Note-input").text}
  element(:close_popup) {|b| b.td(class:"jquerypopover-innerHtml").img(class:"ksap-popup-close")}
  action (:close_popup_click) {|b| b.close_popup.click}
end

