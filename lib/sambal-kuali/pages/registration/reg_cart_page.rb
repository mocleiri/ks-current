class RegistrationCart < RegisterForCourseBase

  expected_element :item_count

  element(:item_count) { |b| b.span(id: "course_count").text }
  element(:register_button) { |b| b.button(id: "register") }
  action(:register) { |b| b.register_button.click }

  element(:course_code) { |b| b.input(id: "courseCode") }
  element(:reg_code) { |b| b.input(id: "regCode") }
  element(:submit_button) { |b| b.button(id: "submit") }
  action(:add_to_cart) { |b| b.submit_button.click }

  element(:remove_course_link) { |b,course_code,reg_code| b.link(id: "remove_#{course_code}_#{reg_code}") }
  element(:course_title) { |b,course_code,reg_code| b.p(id: "title_#{course_code}_#{reg_code}").text }
  element(:course_info) { |b,course_code,reg_code| b.div(id: "course_info_#{course_code}_#{reg_code}").text }
  element(:edit_course_options_link) { |b,course_code,reg_code| b.link(id: "edit_#{course_code}_#{reg_code}") }
  action(:edit_course_options) { |b,course_code,reg_code| b.edit_course_options_link(course_code,reg_code).click }
  element(:course_schedule) { |b,course_code,reg_code,index| b.div(id: "schedule_#{course_code}_#{reg_code}_#{index}").text }

  # EDIT COURSE OPTIONS DISCLOSURE
  element(:credits_selection) { |b,course_code,reg_code| b.select(id: "credits_#{course_code}_#{reg_code}") }
  element(:grading_selection) { |b,course_code,reg_code| b.select(id: "grading_#{course_code}_#{reg_code}") }
  element(:edit_save_button) { |b,course_code,reg_code| b.button(id: "save_#{course_code}_#{reg_code}") }
  action(:save_edits) { |b,course_code,reg_code| b.edit_save_button(course_code,reg_code).click }
  element(:edit_cancel_link) { |b,course_code,reg_code| b.link(id: "cancel_#{course_code}_#{reg_code}") }
  action(:cancel_edits) { |b,course_code,reg_code| b.edit_cancel_link(course_code,reg_code).click }

  def remove_from_cart(course_code, reg_code)
    remove_course_link(course_code,reg_code).click
  end

  def select_credits(course_code, reg_code, credits)
    credits_selection(course_code, reg_code).select(credits)
  end

  def select_grading(course_code, reg_code, grading_option)
    grading_selection(course_code, reg_code).select(grading_option)
  end

end