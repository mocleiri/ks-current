class ManageCourseOfferingList < BasePage

  wrapper_elements
  frame_element

  expected_element :subject_code

  element(:subject_code) { |b| b.frm.div(class: "uif-viewHeader-contentWrapper uif-sticky").h1.span(index: 1) }

  element(:co_results_div) { |b| b.frm.div(id: "Uif-ViewContentWrapper") }

  element(:approve_subject_code_for_scheduling_link) { |b| b.frm.co_results_div.link(id: "KS-CourseOfferingManagement-ApproveSubj") }

  element(:approve_course_offering_button) { |b| b.frm.co_results_div.button(id: "KS-CourseOfferingManagement-ToolBar-Approve-CO") }
  action(:approve_course_offering) { |b| b.approve_course_offering_button.click; b.loading.wait_while_present(120) }

  element(:create_course_offering_button) { |b| b.frm.co_results_div.button(id: "KS-CourseOfferingManagement-ToolBar-Add-CO") }
  action(:create_course_offering) { |b| b.create_course_offering_element.click; b.loading.wait_while_present }

  element(:delete_cos_button) { |b|b.frm.button(id: "KS-CourseOfferingManagement-ToolBar-Delete-CO")}
  action(:delete_cos) { |b|b.delete_cos_button.click; b.loading.wait_while_present(300) }

  #element(:approve_dialog){|b| b.frm.div(id:"approveCODialog")}
  #element(:approve_yes_element) { |b| b.approve_dialog.radio(index:0)}
  #action(:approve_yes) { |b| b.approve_yes_element.click;b.loading.wait_while_present(300)}
  #
  #element(:no_button_element) { |b| b.approve_dialog.radio(index:1)}
  #action(:no) { |b| b.no_button_element.click;b.loading.wait_while_present(300)}

  SELECT_COLUMN = 0
  CO_CODE_COLUMN = 1
  CO_STATUS_COLUMN = 2
  CO_TITLE_COLUMN = 3
  CO_CREDITS_COLUMN = 4
  CO_GRADING_COLUMN = 5
  ACTIONS_COLUMN = 6

  element(:course_offering_results_table) { |b| b.frm.div(id: "KS-CourseOfferingManagement-CourseOfferingListSection").table() }

  element(:selected_offering_actions) { |b| b.frm.select(id: "KS-CourseOfferingManagement-CourseOfferingActionSection-Select_control") }

  action(:go) { |b| b.frm.button(id: "KS-CourseOfferingManagement-CourseOfferingActionSection-Go").click}

  def go_click
     go
     @browser.alert.ok
     loading.wait_while_present
  end

  def approve_subject_code
    approve_subject_code_for_scheduling_link.click
    @browser.alert.ok
    sleep 2
    loading.wait_while_present(600)
  end

  def view_course_offering_link(co_code)
    course_offering_results_table.link(text: co_code)
  end

  def view_course_offering(co_code)
    view_course_offering_link(co_code).click
    loading.wait_while_present
  end

  def row_by_status(costatus)
    course_offering_results_table.row(text: /\b#{Regexp.escape(costatus)}\b/)
  end

  def select_co_by_status(costatus)
    if row_by_status(costatus).exists? then
      row_by_status(costatus).checkbox.set
      return row_by_status(costatus).cells[CO_CODE_COLUMN].text
    else
      return nil
    end
  end

  def co_status(co_code)
     @browser.span(id: "co_status_#{co_code}_control").text
  end

  def crosslist_tooltip_text(co_code)
    retVal = ""

    crosslist_tooltip_text = div(id: "view_co_#{co_code}").img.alt
    if crosslist_tooltip_text != nil
      retVal = crosslist_tooltip_text
    end

    retVal
  end

  def crosslisted_codes(co_code)
    crosslist_tooltip_text = crosslist_tooltip_text(co_code)
    crosslist_tooltip_text = crosslist_tooltip_text.gsub( "This course is crosslisted with:", "")
    crosslist_tooltip_text.split( "<br>" ).reject! { |e| e.empty? }
  end

  def select_co_checkbox(co_code)
    @browser.checkbox(id: "checkbx_co_#{co_code}")
  end

  def copy(co_code)
    copy_link(co_code).wait_until_present
    copy_link(co_code).focus
    copy_link(co_code).click
    loading.wait_while_present(30)
  end

  def copy_link(co_code)
    @browser.link(id: "copy_co_#{co_code}")
  end

  def edit(co_code)
    edit_link(co_code).focus
    edit_link(co_code).click
    loading.wait_while_present(120)
  end

  def edit_link(co_code)
    @browser.link(id: "edit_co_#{co_code}")
  end

  def manage_link(co_code)
    @browser.link(id: "manage_co_#{co_code}")
  end

  def manage(co_code)
    manage_link(co_code).focus
    manage_link(co_code).click
    loading.wait_while_present(120)
  end

  def co_list
    co_codes = []
    course_offering_results_table.rows.each { |row| co_codes << row[CO_CODE_COLUMN].text }
    co_codes.delete_if { |co_code| co_code.strip == "" }
    co_codes
  end

  def select_cos(code_list)
    code_list.each do |code|
      select_co_checkbox(code).focus
      select_co_checkbox(code).set
      #target_row(code.upcase).checkbox.set
    end
  end

  def select_all_cos()
    course_offering_results_table.rows[0].checkbox.set
  end

  def deselect_all_cos()
    #TODO - revert if KSENROLL-7434 fixed
    #course_offering_results_table.rows[0].checkbox.set
    #course_offering_results_table.rows[0].checkbox.clear
    code_list = co_list
    deselect_cos(code_list)
  end


  def deselect_cos(code_list)
    code_list.each do |code|
      select_co_checkbox(code).focus
      select_co_checkbox(code).clear
    end
  end

  def select_co(code)
    select_co_checkbox(code).focus
    select_co_checkbox(code).set
  end

  def deselect_co(code)
    select_co_checkbox(code).focus
    select_co_checkbox(code).clear
  end
end