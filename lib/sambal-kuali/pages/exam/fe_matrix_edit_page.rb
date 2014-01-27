class FEMatrixEdit < BasePage

  wrapper_elements
  krms_frame_elements

  expected_element :cancel_rule_btn

  element(:fe_agenda_view_page) { |b| b.frm.div( id: "KSFE-AgendaManagement-View")}
  element(:fe_rule_maintenance_page) { |b| b.fe_agenda_view_page.div( id: "KSFE-RuleMaintenance-Page")}

  element(:fe_rules_manage_section) { |b| b.frm.div( id: "KSFE-Rules-ManageSection")}
  element(:toolbar_section) { |b| b.frm.div( id: "KSFE-EditRule-ToolbarSection")}
  element(:expand_collapse_section) { |b| b.frm.div( id: "KRMS-RuleEditor-TreeExpandCollapse")}
  element(:rule_tree_section) { |b| b.frm.div( id: "KRMS-RuleEditor-TreeGroup")}
  element(:scheduling_information_section) { |b| b.frm.div( id: "FinalExam-DeliveryLogistic-New-Section")}
  element(:proposition_section) { |b| b.frm.div( id: "editWithObjectTree")}

  value(:validation_message_text) { |b| b.frm.li( class: "uif-errorMessageItem").text}

  element(:add_statement_btn) { |b| b.toolbar_section.button( text: "Add Statement")}
  action(:add_statement) { |b| b.add_statement_btn.click; b.loading.wait_while_present}
  element(:edit_btn) { |b| b.toolbar_section.button( text: "Edit")}
  action(:edit) { |b| b.edit_btn.click; b.loading.wait_while_present}
  element(:move_up_btn) { |b| b.toolbar_section.button( text: "Move Up")}
  action(:move_up) { |b| b.move_up_btn.click; b.loading.wait_while_present}
  element(:move_down_btn) { |b| b.toolbar_section.button( text: "Move Down")}
  action(:move_down) { |b| b.move_down_btn.click; b.loading.wait_while_present}
  element(:delete_btn) { |b| b.toolbar_section.button( text: "Delete")}
  action(:delete) { |b| b.delete_btn.click; b.loading.wait_while_present}

  element(:rule_dropdown) { |b| b.frm.select( name: /editTree.*proposition\.typeId/)}
  element(:rule_days) { |b| b.proposition_section.text_field( name: /editTree.*proposition\.weekdays/)}
  element(:rule_starttime) { |b| b.proposition_section.text_field( name: /editTree.*proposition\.startTime/)}
  element(:rule_starttime_ampm) { |b| b.proposition_section.select( name: /editTree.*proposition\.startTimeAMPM/)}
  element(:rule_endtime) { |b| b.proposition_section.text_field( name: /editTree.*proposition\.endTime/)}
  element(:rule_endtime_ampm) { |b| b.proposition_section.select( name: /editTree.*proposition\.endTimeAMPM/)}
  element(:rule_freeformtext) { |b| b.frm.text_field( name: /editTree.*proposition\.termParameter/)}
  element(:courses_type_dropdown) { |b| b.proposition_section.select( name: /editTree.*proposition\.multipleCourseType/)}

  element(:add_line_btn) { |b| b.frm.button( text: "Add")} #( id: "KRMS-ApprovedCourseStackedCollectionGroup_add")}
  action(:add_line) { |b| b.add_line_btn.click}
  element(:preview_change_btn) { |b| b.proposition_section.button( text: /Preview Change/)}
  action(:preview_change) { |b| b.preview_change_btn.click; b.loading.wait_while_present}
  element(:cancel_proposition_link) { |b| b.proposition_section.a( text: /Cancel/)}
  action(:cancel_proposition) { |b| b.cancel_proposition_link.click; b.loading.wait_while_present}

  element(:rsi_tba_checkbox) { |b| b.scheduling_information_section.checkbox( id: "FinalExam_tba_control")}
  action(:rsi_tba) { |b| b.rsi_tba_checkbox.click}
  element(:rsi_days) { |b| b.scheduling_information_section.select( id: "FinalExam_days_control")}
  element(:rsi_starttime) { |b| b.scheduling_information_section.text_field( id: "FinalExam_starttime_control")}
  element(:rsi_starttime_ampm) { |b| b.scheduling_information_section.select( id: "FinalExam_starttime_ampm_control")}
  element(:rsi_endtime) { |b| b.scheduling_information_section.text_field( id: "FinalExam_endtime_control")}
  element(:rsi_endtime_ampm) { |b| b.scheduling_information_section.select( id: "FinalExam_endtime_ampm_control")}
  element(:rsi_facility) { |b| b.scheduling_information_section.text_field( id: "FinalExam_building_control")}
  element(:rsi_room) { |b| b.scheduling_information_section.text_field( id: "FinalExam_room_control")}

  element(:update_rule_btn) { |b| b.fe_rule_maintenance_page.button( id: "KSFE-UpdateRule-Button")}
  action(:update_rule) { |b| b.update_rule_btn.click}
  element(:cancel_rule_btn) { |b| b.fe_rule_maintenance_page.link( id: "KSFE-CancelRule-Button")}
  action(:cancel_rule) { |b| b.cancel_rule_btn.click; ; b.loading.wait_while_present}

  element(:lookup_results_section) { |b| b.frm_popup.div( id: "uLookupResults")}
  element(:lookup_course_code) { |b| b.frm_popup.text_field( name: "lookupCriteria[code]")}
  element(:lookup_results_table) { |b| b.lookup_results_section.table}

  def return_course_code course
    lookup_results_table.row(text: /#{course}/)
  end

  element(:lookup_search_btn) { |b| b.frm_popup.button( id: "button_search")}
  action(:lookup_search) { |b| b.lookup_search_btn.click}

end