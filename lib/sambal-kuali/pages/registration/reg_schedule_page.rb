class StudentSchedule < RegisterForCourseBase


  element(:remove_course_button) { |course_code,reg_group_code,b| b.button(id: "remove_#{course_code}_#{reg_group_code}") }
  element(:course_code) { |course_code,reg_group_code,b| b.span(id: "course_code_#{course_code}_#{reg_group_code}") }
  element(:course_title) { |course_code,reg_group_code,b| b.div(id: "title_#{course_code}_#{reg_group_code}").text }
  #element(:course_info) { |course_code,reg_group_code,b| b.div(id: "course_info_#{course_code}_#{reg_group_code}").text }
  element(:edit_course_options_button) { |course_code,reg_group_code,b| b.button(id: "edit_#{course_code}_#{reg_group_code}") }
  action(:edit_course_options) { |course_code,reg_group_code,b| b.edit_course_options_button(course_code,reg_group_code).click }
  #element(:ao_type) { |course_code,reg_group_code,index,b| b.div(id: "ao_type_#{course_code}_#{reg_group_code}_#{index}").text }
  #element(:course_schedule) { |course_code,reg_group_code,ao_index,index,b| b.div(id: "schedule_#{course_code}_#{reg_group_code}_#{ao_index}_#{index}").text }
  element(:confirm_drop) { |course_code,reg_group_code,b| b.button(id: "dropRegGroup_#{course_code}_#{reg_group_code}") }
  element(:cancel_drop) { |b| b.button(id: "dropRegGroupCancel") }

  def toggle_course_details(course_code, reg_group_code)
    course_code(course_code,reg_group_code).click
  end

  def remove_course_from_schedule(course_code,reg_group_code)
    remove_course_button(course_code,reg_group_code).click
    confirm_drop(course_code,reg_group_code).wait_until_present
    confirm_drop(course_code,reg_group_code).click
  end

  def cancel_drop(course_code,reg_group_code)
    remove_course_button(course_code,reg_group_code).click
    cancel_drop.wait_until_present
    cancel_drop.click
  end

end