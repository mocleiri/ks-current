class FEMatrixView < BasePage

  wrapper_elements
  frame_element

  expected_element :fe_agenda_view_page

  element(:fe_agenda_view_page) { |b| b.frm.div( id: "KSFE-AgendaManagement-View")}
  element(:fe_agenda_maintenance_page) { |b| b.fe_agenda_view_page.div( id: "KSFE-AgendaMaintenance-Page")}

  element(:term_type_select) { |b| b.frm.select( name: "document.newMaintainableObject.dataObject.termToUse")}

  element(:submit_btn) { |b| b.div( id: "KSFE-DocumentPageFooter-SubmitCancel").button( text: /Save/)}
  action(:submit) { |b| b.submit_btn.click}
  element(:cancel_link) { |b| b.div( id: "KSFE-DocumentPageFooter-SubmitCancel").a( text: /Cancel/)}
  action(:cancel) { |b| b.cancel_link.click; b.loading.wait_while_present}

  element(:info_validation_message) { |b| b.frm.div(class: /uif-validationMessages.*uif-pageValidationMessages-info/)}
  value(:info_validation_message_text) { |b| b.info_validation_message.text}

  COURSE_REQUIREMENTS = 0
  EXAM_DAY = 1
  EXAM_TIME = 2
  COMMON_EXAM_BLDG = 3
  COMMON_EXAM_ROOM = 4
  STANDARD_EXAM_ACTIONS = 3
  COMMON_EXAM_ACTIONS = 5

  element(:standard_final_exam_section) { |b| b.frm.div( id: "ruledefinitions_agenda0")}
  element(:standard_final_exam_table) { |b| b.standard_final_exam_section.table}
  action(:add_standard_fe_rule) { |b| b.standard_final_exam_section.a( text: "Add").click; b.loading.wait_while_present}
  element(:set_standard_exam_location) { |b| b.frm.checkbox( id: "KSFE_location_agenda0_control")}

  element(:common_final_exam_section) { |b| b.frm.div( id: "ruledefinitions_agenda1")}
  element(:common_final_exam_table) { |b| b.common_final_exam_section.table}
  action(:add_common_fe_rule) { |b| b.common_final_exam_section.a( text: "Add").click; b.loading.wait_while_present}

  def standard_fe_target_row( requirements)
    row = nil
    if standard_final_exam_table.row(text: /#{requirements}/).exists?
      row = standard_final_exam_table.row(text: /#{requirements}/)
    end
    return row
  end

  def common_fe_target_row( requirements)
    row = nil
    if common_final_exam_table.row(text: /#{requirements}/).exists?
      row = common_final_exam_table.row(text: /#{requirements}/)
    end
    return row
  end

  def get_standard_fe_requirements( requirements)
    standard_fe_target_row( requirements).cells[COURSE_REQUIREMENTS].text
  end

  def get_standard_fe_day( requirements)
    standard_fe_target_row( requirements).cells[EXAM_DAY].text
  end

  def get_standard_fe_time( requirements)
    standard_fe_target_row( requirements).cells[EXAM_TIME].text
  end

  def get_standard_fe_actions( requirements)
    standard_fe_target_row( requirements).cells[STANDARD_EXAM_ACTIONS].text
  end

  def get_common_fe_requirements( requirements)
    common_fe_target_row( requirements).cells[COURSE_REQUIREMENTS].text
  end

  def get_common_fe_day( requirements)
    common_fe_target_row( requirements).cells[EXAM_DAY].text
  end

  def get_common_fe_time( requirements)
    common_fe_target_row( requirements).cells[EXAM_TIME].text
  end

  def get_common_fe_facility( requirements)
    common_fe_target_row( requirements).cells[COMMON_EXAM_BLDG].text
  end

  def get_common_fe_room( requirements)
    common_fe_target_row( requirements).cells[COMMON_EXAM_ROOM].text
  end

  def get_common_fe_actions( requirements)
    common_fe_target_row( requirements).cells[COMMON_EXAM_ACTIONS].text
  end

  def edit( requirements, exam_type)
    if exam_type == "Standard"
      standard_fe_target_row( requirements).i(class: "ks-fontello-icon-pencil").click
    else
      common_fe_target_row( requirements).i(class: "ks-fontello-icon-pencil").click
    end
    loading.wait_while_present
  end

  def delete( requirements, exam_type)
    loading.wait_while_present
    if exam_type == "Standard"
      standard_fe_target_row( requirements).i(class: "ks-fontello-icon-cancel").click
    else
      common_fe_target_row( requirements).i(class: "ks-fontello-icon-cancel").click
    end
    loading.wait_while_present
  end

  def get_all_standard_fe_days
    array = []
    standard_final_exam_table.rows.each do |row|
      array << row.cells[EXAM_DAY].text
    end
    return array
  end

  def get_all_standard_fe_times_for_day( day)
    array = []
    standard_final_exam_table.rows.each do |row|
      array << "#{row.cells[EXAM_TIME].text}" if row.cells[EXAM_DAY].text == day
    end
    return array
  end
end