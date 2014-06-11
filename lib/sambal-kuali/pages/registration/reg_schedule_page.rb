class StudentSchedule < RegisterForCourseBase

  page_url "#{$test_site}/registration/index.jsp#/mySchedule"

  STATUS_SCHEDULE = "schedule"
  STATUS_WAITLIST = "waitlist"

  element(:reg_credit_count) { |b| b.span(id: "reg_credit_count").text }
  element(:user_message_div) { |status=STATUS_SCHEDULE,b| prefix = (status==STATUS_SCHEDULE)?"":"waitlist_"; b.div(id: "#{prefix}course_status_message") }
  element(:user_message) { |status=STATUS_SCHEDULE,b| b.user_message_div(status).div(index: 0).text }

  element(:course_code) { |course_code,reg_group_code,status=STATUS_SCHEDULE,b| prefix = (status==STATUS_SCHEDULE)?"":"waitlist_"; b.span(id: "#{prefix}course_code_#{course_code}_#{reg_group_code}") }

  element(:course_title_div) { |course_code,reg_group_code,b| b.div(id: "title_#{course_code}_#{reg_group_code}") }
  element(:course_title) { |course_code,reg_group_code,b| b.div(id: "title_#{course_code}_#{reg_group_code}").text }
  element(:course_info_div) { |course_code,reg_group_code,status=STATUS_SCHEDULE,b| prefix = (status==STATUS_SCHEDULE)?"":"waitlist_"; b.div(id: "#{prefix}course_info_#{course_code}_#{reg_group_code}") }
  element(:course_info) { |course_code,reg_group_code,status=STATUS_SCHEDULE,b| b.course_info_div(course_code,reg_group_code,status).text }
  element(:edit_course_options_button) { |course_code,reg_group_code,status=STATUS_SCHEDULE,b| prefix = (status==STATUS_SCHEDULE)?"":"waitlist_"; b.button(id: "#{prefix}edit_#{course_code}_#{reg_group_code}") }
  action(:edit_course_options) { |course_code,reg_group_code,status=STATUS_SCHEDULE,b| b.edit_course_options_button(course_code,reg_group_code,status).click }
  element(:ao_type) { |course_code,reg_group_code,index,b| b.span(id: "ao_type_long_#{course_code}_#{reg_group_code}_#{index}").text }
  element(:course_schedule) { |course_code,reg_group_code,ao_index,index,b| b.div(id: "schedule_long_#{course_code}_#{reg_group_code}_#{ao_index}_#{index}").text }
  element(:confirm_drop) { |course_code,reg_group_code,b| b.button(id: "dropRegGroup_#{course_code}_#{reg_group_code}") }
  element(:cancel_drop) { |b| b.button(id: "dropRegGroupCancel") }

  element(:credit_options_more) { |course_code,reg_group_code,status=STATUS_SCHEDULE,b| b.div(id: "#{status}_credits_#{course_code}_#{reg_group_code}_more") }
  action(:more_credit_options) { |course_code,reg_group_code,status=STATUS_SCHEDULE,b| b.credit_options_more(course_code,reg_group_code,status).click }
  element(:credit_option_selection) { |course_code,reg_group_code,status=STATUS_SCHEDULE,credits,b| b.i(id: "#{status}_credits_#{course_code}_#{reg_group_code}_#{credits}") }
  action(:select_credit_option) { |course_code,reg_group_code,status=STATUS_SCHEDULE,credits,b| b.credit_option_selection(course_code,reg_group_code,status,credits).click}
  element(:grading_audit) { |course_code,reg_group_code,status=STATUS_SCHEDULE,b| b.i(id: "#{status}_grading_#{course_code}_#{reg_group_code}_Audit") }
  element(:grading_letter) { |course_code,reg_group_code,status=STATUS_SCHEDULE,b| b.i(id: "#{status}_grading_#{course_code}_#{reg_group_code}_Letter") }
  element(:grading_pass_fail) { |course_code,reg_group_code,status=STATUS_SCHEDULE,b| b.i(id: "#{status}_grading_#{course_code}_#{reg_group_code}_Pass/Fail") }
  element(:grading_option_badge) { |course_code,reg_group_code,status=STATUS_SCHEDULE,b| prefix = (status==STATUS_SCHEDULE)?"":"waitlist_"; b.span(id: "#{prefix}grading_badge_#{course_code}_#{reg_group_code}") }
  element(:grading_option) { |course_code,reg_group_code,status=STATUS_SCHEDULE,b| prefix = (status==STATUS_SCHEDULE)?"":"waitlist_"; b.grading_option_badge(course_code,reg_group_code,status).text }
  element(:edit_save_button) { |course_code,reg_group_code,status=STATUS_SCHEDULE,b| b.button(id: "#{status}_save_#{course_code}_#{reg_group_code}") }
  action(:save_edits) { |course_code,reg_group_code,status=STATUS_SCHEDULE,b| b.edit_save_button(course_code,reg_group_code,status).click }
  element(:edit_cancel_button) { |course_code,reg_group_code,status=STATUS_SCHEDULE,b| b.button(id: "#{status}_cancel_#{course_code}_#{reg_group_code}") }
  action(:cancel_edits) { |course_code,reg_group_code,status=STATUS_SCHEDULE,b| b.edit_cancel_button(course_code,reg_group_code,status).click }
  element(:remove_course_button) { |course_code,reg_group_code,status=STATUS_SCHEDULE,b| prefix = (status==STATUS_SCHEDULE)?"":"waitlist_"; b.button(id: "#{prefix}remove_#{course_code}_#{reg_group_code}") }
  action(:remove_the_course) { |course_code,reg_group_code,status=STATUS_SCHEDULE,b| b.remove_course_button(course_code,reg_group_code,status).click }

  element(:confirm_remove_waitlist_button) { |b| b.button(id: "removeWaitlist") }
  element(:cancel_remove_waitlist_button) { |b| b.button(id: "removeWaitlistCancel") }

  def toggle_course_details(course_code, reg_group_code, course_status=STATUS_SCHEDULE)
    course_code(course_code,reg_group_code,course_status).wait_until_present
    course_code(course_code,reg_group_code,course_status).click
  end

  def show_course_details(course_code, reg_group_code, course_status=STATUS_SCHEDULE)
    sleep 1
    toggle_course_details(course_code, reg_group_code, course_status) unless remove_course_button(course_code, reg_group_code, course_status).visible?
  end

  def hide_course_details(course_code, reg_group_code, course_status=STATUS_SCHEDULE)
    sleep 1
    toggle_course_details(course_code, reg_group_code, course_status) if remove_course_button(course_code, reg_group_code, course_status).visible?
  end

  def select_credits(course_code, reg_group_code, credits, status=STATUS_SCHEDULE)
    # Firefox workaround
    toggle_course_details course_code,reg_group_code,status
    sleep 1
    show_course_details course_code,reg_group_code,status
    sleep 1

    more_credit_options(course_code, reg_group_code, status) if credit_options_more(course_code, reg_group_code, status).visible?
    select_credit_option(course_code, reg_group_code, status, credits)
  end

  def select_grading(course_code, reg_group_code, grading_option, status=STATUS_SCHEDULE)
    case grading_option
      when "Audit" then grading_audit(course_code,reg_group_code,status).click
      when "Letter" then grading_letter(course_code,reg_group_code,status).click
      when "Pass/Fail" then grading_pass_fail(course_code,reg_group_code,status).click
    end
  end

  def remove_course(course_code,reg_group_code,status=STATUS_SCHEDULE)
    remove_course_button(course_code,reg_group_code,status).wait_until_present
    remove_the_course(course_code,reg_group_code,status)
    case status
      when STATUS_SCHEDULE then
        confirm_drop(course_code,reg_group_code).wait_until_present
        confirm_drop(course_code,reg_group_code).click
      when STATUS_WAITLIST then
        confirm_remove_waitlist_button.wait_until_present
        confirm_remove_waitlist_button.click
    end
  end

  def cancel_drop_course(course_code,reg_group_code)
    remove_course_button(course_code,reg_group_code).wait_until_present
    remove_course_button(course_code,reg_group_code).click
    sleep 2.5
    cancel_drop.wait_until_present
    cancel_drop.click
  end

end