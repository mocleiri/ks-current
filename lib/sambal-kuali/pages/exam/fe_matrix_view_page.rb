class FEMatrixView < BasePage

  wrapper_elements
  frame_element

  expected_element :fe_agenda_view_page

  element(:fe_agenda_view_page) { |b| b.frm.div( id: "KSFE-AgendaManagement-View")}
  element(:fe_agenda_maintenance_page) { |b| b.fe_agenda_view_page.div( id: "KSFE-AgendaMaintenance-Page")}

  element(:term_type_select) { |b| b.frm.select( name: "document.newMaintainableObject.dataObject.termToUse")}

  element(:submit_btn) { |b| b.main( id: "KSFE-AgendaMaintenance-Page").div(class: /uif-stickyFooter/).button( text: /Save/)}
  action(:submit) { |b| b.submit_btn.click}
  element(:cancel_link) { |b| b.main( id: "KSFE-AgendaMaintenance-Page").div(class: /uif-stickyFooter/).a( text: /Cancel/)}
  action(:cancel) { |b| b.cancel_link.click; b.loading.wait_while_present}

  element(:info_validation_message) { |b| b.frm.div(id: 'KSFE-AgendaMaintenance-Page_messages') }
  value(:info_validation_message_text) { |b| b.info_validation_message.text}

  COURSE_REQUIREMENTS = 0
  EXAM_DAY = 1
  EXAM_TIME = 2
  COMMON_EXAM_BLDG = 3
  COMMON_EXAM_ROOM = 4
  STANDARD_EXAM_ACTIONS = 3
  COMMON_EXAM_ACTIONS = 5

  element(:standard_final_exam_section) { |b| b.frm.section( id: "ruledefinitions_agenda0")}
  element(:standard_final_exam_table) { |b| b.standard_final_exam_section.table}
  action(:add_standard_fe_rule) { |b| b.standard_final_exam_section.a( text: "Add").click; b.loading.wait_while_present}
  element(:set_standard_exam_location) { |b| b.frm.checkbox( id: "KSFE_location_agenda0_control")}

  element(:common_final_exam_section) { |b| b.frm.section( id: "ruledefinitions_agenda1")}
  element(:common_final_exam_table) { |b| b.common_final_exam_section.table}
  action(:add_common_fe_rule) { |b| b.common_final_exam_section.a( text: "Add").click; b.loading.wait_while_present}

  def standard_fe_target_row( rule_obj)
    rows = standard_final_exam_table.rows(text: /#{rule_obj.rule_requirements}/)
    rows.each do |row|
      if row.text =~ /#{Regexp.escape(rule_obj.rsi_days)}.*#{Regexp.escape(rule_obj.start_time)}.*#{Regexp.escape(rule_obj.end_time)}/m
        return row
      end
    end
    return nil
  end

  def common_fe_target_row( rule_obj)
    rows = common_final_exam_table.rows(text: /#{rule_obj.rule_requirements}/)
    rows.each do |row|
      if row.text =~ /#{Regexp.escape(rule_obj.rsi_days)}.*#{Regexp.escape(rule_obj.start_time)}.*#{Regexp.escape(rule_obj.end_time)}/m
        return row
      end
    end
    return nil
  end

  def get_standard_fe_requirements( rule_obj)
    standard_fe_target_row( rule_obj).cells[COURSE_REQUIREMENTS].text
  end

  def get_standard_fe_day( rule_obj)
    standard_fe_target_row( rule_obj).cells[EXAM_DAY].text
  end

  def get_standard_fe_time( rule_obj)
    standard_fe_target_row( rule_obj).cells[EXAM_TIME].text
  end

  def get_standard_fe_actions( rule_obj)
    standard_fe_target_row( rule_obj).cells[STANDARD_EXAM_ACTIONS].text
  end

  def get_standard_fe_actions_class( rule_obj, action_type)
    if action_type == "Edit"
      standard_fe_target_row( rule_obj).cells[STANDARD_EXAM_ACTIONS].i(class: "ks-fontello-icon-pencil")
    else
      standard_fe_target_row( rule_obj).cells[STANDARD_EXAM_ACTIONS].i(class: "ks-fontello-icon-cancel")
    end
  end

  def get_common_fe_requirements( rule_obj)
    common_fe_target_row( rule_obj).cells[COURSE_REQUIREMENTS].text
  end

  def get_common_fe_day( rule_obj)
    common_fe_target_row( rule_obj).cells[EXAM_DAY].text
  end

  def get_common_fe_time( rule_obj)
    common_fe_target_row( rule_obj).cells[EXAM_TIME].text
  end

  def get_common_fe_facility( rule_obj)
    common_fe_target_row( rule_obj).cells[COMMON_EXAM_BLDG].text
  end

  def get_common_fe_room( rule_obj)
    common_fe_target_row( rule_obj).cells[COMMON_EXAM_ROOM].text
  end

  def get_common_fe_actions( rule_obj)
    common_fe_target_row( rule_obj).cells[COMMON_EXAM_ACTIONS].text
  end

  def get_common_fe_actions_class( rule_obj, action_type)
    if action_type == "Edit"
      common_fe_target_row( rule_obj).cells[COMMON_EXAM_ACTIONS].i(class: "ks-fontello-icon-pencil")
    else
      common_fe_target_row( rule_obj).cells[COMMON_EXAM_ACTIONS].i(class: "ks-fontello-icon-cancel")
    end
  end

  def edit( rule_obj, exam_type)
    if exam_type == "Standard"
      standard_fe_target_row( rule_obj).i(class: "ks-fontello-icon-pencil").click
    else
      common_fe_target_row( rule_obj).i(class: "ks-fontello-icon-pencil").click
    end
    loading.wait_while_present
  end

  def delete( rule_obj, exam_type)
    loading.wait_while_present
    if exam_type == "Standard"
      standard_fe_target_row( rule_obj).i(class: "ks-fontello-icon-cancel").click
    else
      common_fe_target_row( rule_obj).i(class: "ks-fontello-icon-cancel").click
    end
    loading.wait_while_present
  end

  def get_all_standard_fe_days
    array = []
    standard_final_exam_table.rows.each do |row|
      array << row.cells[EXAM_DAY].text
    end
    array.delete_if{|item| !(item.match /Day/)}
    return array
  end

  def get_all_standard_fe_times_for_day( day)
    array = []
    standard_final_exam_table.rows.each do |row|
      if row.cells[EXAM_DAY].text == day
        time_str = row.cells[EXAM_TIME].text
        time = DateTime.strptime(time_str, '%I:%M %p')
        array << time
      end
    end
    return array
  end

  def get_row_array_by_rule_requirements( exam_type, requirements)
    arr = []
    if exam_type == "Standard"
      rows = standard_final_exam_table.rows(text: /#{Regexp.escape(requirements)}/)
      rows.each do |row|
        if row.cells[COURSE_REQUIREMENTS].text =~ /#{Regexp.escape(requirements)}/m
          arr << row.cells[COURSE_REQUIREMENTS].text
          arr << row.cells[EXAM_DAY].text
          arr << row.cells[EXAM_TIME].text
          return arr
        end
      end
    else
      rows = common_final_exam_table.rows(text: /#{Regexp.escape(requirements)}/)
      rows.each do |row|
        if row.cells[COURSE_REQUIREMENTS].text =~ /#{Regexp.escape(requirements)}/m
          arr << row.cells[COURSE_REQUIREMENTS].text
          arr << row.cells[EXAM_DAY].text
          arr << row.cells[EXAM_TIME].text
          return arr
        end
      end
    end
  end

end