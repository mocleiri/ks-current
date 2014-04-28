class ManageCORequisites < BasePage

  expected_element :cancel_update_link

  wrapper_elements
  krms_frame_elements

  element(:rule_maintenance_section) { |b| b.frm.div(id: "KSCO-RuleMaintenance-Page")}
  element(:tab_section) { |b| b.frm.div(id: "KSCO-RuleEdit-TabSection")}
  element(:edit_tree_section) { |b| b.frm.div(id: "KRMS-RuleEditor-TreeGroup")}
  element(:logic_tab_section) { |b| b.frm.div(id: "KSCO-EditWithLogic-EditGroup")}
  element(:preview_tree_section) { |b| b.logic_tab_section.div(id: "KSCO-EditWithLogic-TreeGroup")}
  element(:compare_rule_section) { |b| b.frm.div(id: "compareRuleLightBox")}
  element(:preview_rule_section) { |b| b.frm.div(id: "KSCO-RulePreview-ActionLinks")}
  element(:grade_section) { |b| b.edit_tree_section.div(:id => /KRMS-GradeScale-Field/)}

  element(:background_div) { |b| b.frm.li(:id => /^u\d+_node_0_parent_root$/)}
  element(:logic_tab) { |b| b.tab_section.a(:text => /Edit Rule Logic/)}
  element(:object_tab) { |b| b.tab_section.a(:text => /Edit Rule/)}
  element(:logic_text) { |b| b.tab_section.text_field(name: "document.newMaintainableObject.dataObject.logicArea")}

  action(:add_btn) { |b| b.frm.button(:text => /Add Statement/).when_present.click; b.edit_loading.wait_while_present; b.loading.wait_while_present}
  action(:edit_btn) { |b| b.frm.button(:text => /Edit/).when_present.click; b.edit_loading.wait_while_present; b.loading.wait_while_present}
  action(:group_btn) { |b| b.frm.button(:text => /Create Group/).when_present.click; b.edit_loading.wait_while_present; b.loading.wait_while_present}
  action(:down_btn) { |b| b.frm.button(:text => /Move Down/).when_present.click; b.edit_loading.wait_while_present; b.loading.wait_while_present}
  action(:up_btn) { |b| b.frm.button(:text => /Move Up/).when_present.click; b.edit_loading.wait_while_present; b.loading.wait_while_present}
  action(:left_btn) { |b| b.frm.button(:text => /Move Out/).when_present.click; b.edit_loading.wait_while_present; b.loading.wait_while_present}
  action(:right_btn) { |b| b.frm.button(:text => /Move In/).when_present.click; b.edit_loading.wait_while_present; b.loading.wait_while_present}
  action(:preview_btn) { |b| b.frm.button(:text => /Preview Change/).when_present.click; b.edit_loading.wait_while_present; b.loading.wait_while_present(60)}
  action(:copy_btn) { |b| b.frm.button(:text => /Copy/).when_present.click; b.edit_loading.wait_while_present; b.loading.wait_while_present}
  action(:cut_btn) { |b| b.frm.button(:text => /Cut/).when_present.click; b.edit_loading.wait_while_present; b.loading.wait_while_present}
  action(:paste_btn) { |b| b.frm.button(:text => /Paste/).when_present.click; b.edit_loading.wait_while_present; b.loading.wait_while_present}
  action(:del_btn) { |b| b.frm.button(:text => /Delete/).when_present.click; b.edit_loading.wait_while_present; b.loading.wait_while_present}
  action(:add_line_btn) { |b| b.frm.button(:text => /^\bAdd\b$/).when_present.click; b.edit_loading.wait_while_present; b.loading.wait_while_present}
  action(:delete_line_btn) { |b| b.frm.button(:text => /delete/).when_present.click; b.edit_loading.wait_while_present; b.loading.wait_while_present}
  action(:update_rule_btn) { |b| b.frm.button(text: "Update Rule").when_present.click; b.edit_loading.wait_while_present; b.loading.wait_while_present}

  element(:right_btn_element) { |b| b.frm.button(:text => /Move In/) }
  element(:cancel_update_link) { |b| b.frm.a(id: "KSCO-CancelRule-Button")}

  action(:search_link) { |b| b.edit_tree_section.a(:text => /Advanced Search/).click; b.edit_loading.wait_while_present; b.loading.wait_while_present}

  element(:rule_dropdown) { |b| b.edit_tree_section.select(:name => /.*editTree.*proposition.typeId/)}
  element(:multi_course_dropdown) { |b| b.edit_tree_section.select(:name => /.*editTree.*proposition.multipleCourseType/)}
  element(:course_field) { |b| b.frm.text_field(:name => /.*editTree.*proposition\.courseInfo\.code/)}
  element(:free_text_field) { |b| b.frm.text_field(:name => /.*editTree.*proposition\.termParameter/)}
  element(:courses_field) { |b| b.frm.text_field(:name => /.*editTree.*proposition\.cluSet\.clus\'\]\.code/)}
  element(:integer_field) { |b| b.frm.text_field(:name => /.*editTree.*proposition\.parameters\[1\]\.value/)}
  element(:grade_dropdown) { |b| b.frm.select(:name => /.*editTree.*proposition\.termParameter/)}
  element(:duration_field) { |b| b.frm.text_field(:name => /.*editTree.*proposition\.duration/)}
  element(:duration_dropdown) { |b| b.frm.select(:name => /.*editTree.*proposition\.durationType/)}
  element(:program_dropdown) { |b| b.frm.select(:name => /.*editTree.*proposition\.programType/)}
  element(:term_field) { |b| b.frm.text_field(:name => /.*editTree.*proposition\.termCode/)}
  element(:term_two_field) { |b| b.frm.text_field(:name => /.*editTree.*proposition\.termCode2/)}
  element(:population_field) { |b| b.frm.text_field(:name => /.*editTree.*proposition\.population\.name/)}

  action(:completed) { |b| b.grade_section.radio(:id => /KRMS-GradeScale-Field.*_control_0/).when_present.click}
  action(:letter) { |b| b.grade_section.radio(:id => /KRMS-GradeScale-Field.*_control_1/).when_present.click}
  action(:pass_fail) { |b| b.grade_section.radio(:id => /KRMS-GradeScale-Field.*_control_2/).when_present.click}
  action(:percentage) { |b| b.grade_section.radio(:id => /KRMS-GradeScale-Field.*_control_3/).when_present.click}
  action(:grade) { |b| b.grade_section.radio(:id => /KRMS-GradeScale-Field.*_control_4/).when_present.click}

  element(:lookup_section) { |b| b.frm_popup.div(id: "CourseLookupView")}
  element(:lookup_results) { |b| b.frm_popup.div(id: "uLookupResults")}
  element(:lookup_course_title) { |b| b.frm_popup.text_field(name: "lookupCriteria[title]")}
  element(:lookup_course_code) { |b| b.frm_popup.text_field(name: "lookupCriteria[code]")}
  element(:lookup_description) { |b| b.frm_popup.text_field(name: "lookupCriteria[description]")}
  element(:lookup_set_name) { |b| b.frm_popup.text_field(name: "lookupCriteria[name]")}
  element(:lookup_population) { |b| b.frm_popup.text_field(name: "lookupCriteria[keyword]")}
  element(:lookup_abrev_org) { |b| b.frm_popup.text_field(name: "lookupCriteria[shortName]")}
  action(:lookup_search_button) { |b| b.frm_popup.button(text: "Search").click; b.edit_loading.wait_while_present; b.loading.wait_while_present}
  element(:lookup_results_table) { |b| b.frm_popup.div(id: "uLookupResults").table() }

  element(:info_message) { |b| b.rule_maintenance_section.li(class: "uif-infoMessageItem")}
  element(:edit_loading) { |b| b.rule_maintenance_section.image(alt: "Loading...") }

  def return_course_code code
    lookup_results_table.row(text: /#{code}/)
  end
end