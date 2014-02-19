class RegistrationCart < RegisterForCourseBase

  expected_element :item_count

  element(:item_count) { |b| b.span(id: "course_count") }
  element(:register_button) { |b| b.button(id: "register") }
  action(:register) { |b| b.register_button.click }

  element(:course_code) { |b| b.text_field(id: "courseCode") }
  element(:reg_group_code) { |b| b.text_field(id: "regCode") }
  element(:submit_button) { |b| b.button(id: "submit") }
  action(:add_to_cart) { |b| b.submit_button.click }

  element(:remove_course_link) { |course_code,reg_group_code,b| b.link(id: "remove_#{course_code}_#{reg_group_code}") }
  element(:course_title) { |course_code,reg_group_code,b| b.p(id: "title_#{course_code}_#{reg_group_code}").text }
  element(:course_info) { |course_code,reg_group_code,b| b.div(id: "course_info_#{course_code}_#{reg_group_code}").text }
  element(:edit_course_options_link) { |course_code,reg_group_code,b| b.link(id: "edit_#{course_code}_#{reg_group_code}") }
  action(:edit_course_options) { |course_code,reg_group_code,b| b.edit_course_options_link(course_code,reg_group_code).click }
  element(:course_schedule) { |course_code,reg_group_code,index,b| b.div(id: "schedule_#{course_code}_#{reg_group_code}_#{index}").text }

  # EDIT COURSE OPTIONS DISCLOSURE
  element(:credits_selection) { |course_code,reg_group_code,b| b.select(id: "credits_#{course_code}_#{reg_group_code}") }
  element(:grading_selection) { |course_code,reg_group_code,b| b.select(id: "grading_#{course_code}_#{reg_group_code}") }
  element(:edit_save_button) { |course_code,reg_group_code,b| b.button(id: "save_#{course_code}_#{reg_group_code}") }
  #action(:save_edits) { |course_code,reg_group_code,b| b.edit_save_button(course_code,reg_group_code).click }
  element(:edit_cancel_link) { |course_code,reg_group_code,b| b.link(id: "cancel_#{course_code}_#{reg_group_code}") }
  action(:cancel_edits) { |course_code,reg_group_code,b| b.edit_cancel_link(course_code,reg_group_code).click }

  def remove_from_cart(course_code, reg_group_code)
    remove_course_link(course_code,reg_group_code).click
  end

  def select_credits(course_code, reg_group_code, credits)
    credits_selection(course_code, reg_group_code).select(credits)
  end

  def select_grading(course_code, reg_group_code, grading_option)
    grading_selection(course_code, reg_group_code).select(grading_option)
  end

  def save_edits(course_code, reg_group_code)
    sleep 1
    edit_save_button(course_code,reg_group_code).click
  end
end