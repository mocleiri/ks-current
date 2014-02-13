class KradCourseRequisites < BasePage

  wrapper_elements
  krad_elements

  action(:expand_all_rule_sections) { |b| b.link(text: '[+] expand all').click; b.add_rule_link.wait_until_present }
  element(:add_rule_link) { |b| b.link(text: 'Add Rule') }
  action(:collapse_all_rule_sections) { |b| b.link(text: '[-] collapse all').click }

  action(:add_rule_student_eligibility) { |b| b.div(id: 'KS-AgendaManage-RulePrototype_ruleA').link(text: 'Add Rule').click; b.loading_wait }
  action(:edit_rule_student_eligibility) { |b| b.div(id: 'KS-AgendaManage-RulePrototype_ruleA').link(text: 'Edit Rule').click; b.loading_wait }
  action(:delete_rule_student_eligibility) { |b| b.div(id: 'KS-AgendaManage-RulePrototype_ruleA').link(text: 'Delete Rule').click; b.loading_wait }

  action(:add_rule_corequisite) { |b| b.div(id: 'KS-AgendaManage-RulePrototype_ruleB').link(text: 'Add Rule').click; b.loading_wait }
  action(:edit_rule_corequisite) { |b| b.div(id: 'KS-AgendaManage-RulePrototype_ruleB').link(text: 'Edit Rule').click; b.loading_wait }
  action(:delete_rule_corequisite) { |b| b.div(id: 'KS-AgendaManage-RulePrototype_ruleB').link(text: 'Delete Rule').click; b.loading_wait }

  action(:add_rule_recommended_prep) { |b| b.div(id: 'KS-AgendaManage-RulePrototype_ruleC').link(text: 'Add Rule').click; b.loading_wait }
  action(:edit_rule_recommended_prep) { |b| b.div(id: 'KS-AgendaManage-RulePrototype_ruleC').link(text: 'Edit Rule').click; b.loading_wait }
  action(:delete_rule_recommended_prep) { |b| b.div(id: 'KS-AgendaManage-RulePrototype_ruleC').link(text: 'Delete Rule').click; b.loading_wait }

  action(:add_rule_antirequisite) { |b| b.div(id: 'KS-AgendaManage-RulePrototype_ruleD').link(text: 'Add Rule').click; b.loading_wait }
  action(:edit_rule_antirequisite) { |b| b.div(id: 'KS-AgendaManage-RulePrototype_ruleD').link(text: 'Edit Rule').click; b.loading_wait }
  action(:delete_rule_antirequisite) { |b| b.div(id: 'KS-AgendaManage-RulePrototype_ruleD').link(text: 'Delete Rule').click; b.loading_wait }

  action(:add_rule_repeatable_for_credit) { |b| b.div(id: 'KS-AgendaManage-RulePrototype_ruleE').link(text: 'Add Rule').click; b.loading_wait }
  action(:edit_rule_repeatable_for_credit) { |b| b.div(id: 'KS-AgendaManage-RulePrototype_ruleE').link(text: 'Edit Rule').click; b.loading_wait }
  action(:delete_rule_repeatable_for_credit) { |b| b.div(id: 'KS-AgendaManage-RulePrototype_ruleE').link(text: 'Delete Rule').click; b.loading_wait }

  action(:add_rule_restricts_credits) { |b| b.div(id: 'KS-AgendaManage-RulePrototype_ruleF').link(text: 'Add Rule').click; b.loading_wait }
  action(:edit_rule_restricts_credits) { |b| b.div(id: 'KS-AgendaManage-RulePrototype_ruleF').link(text: 'Edit Rule').click; b.loading_wait }
  action(:delete_rule_restricts_credits) { |b| b.div(id: 'KS-AgendaManage-RulePrototype_ruleF').link(text: 'Delete Rule').click; b.loading_wait }

  #ADD RULE SECTION

  # This is the drop down for rule statement
  element(:rule_statement_option) {|b| b.select_list(id: 'KRMS-PropositionType-Dropdown_node_0_parent_root_control') }

  action(:add_statement) { |b| b.button(text: 'Add Statement').click; b.loading_wait}
  action(:edit_rule) {|b| b.button(text: 'Edit').click }
  action(:create_group_rule) {|b| b.button(text: 'Create Group').click }
  action(:move_out_rule) {|b| b.button(text: 'Move Out').click }
  action(:move_in_rule) {|b| b.button(text: 'Move In').click }
  action(:move_up_rule) {|b| b.button(text: 'Move Up').click }
  action(:move_down_rule) {|b| b.button(text: 'Move Down').click }
  action(:copy_rule) {|b| b.button(text: 'Copy').click }
  action(:cut_rule) {|b| b.button(text: 'Cut').click }
  action(:paste_rule) {|b| b.button(text: 'Paste').click }
  action(:delete_rule) {|b| b.button(text: 'Delete').click }

  action(:cancel_statement) { |b| b.link(id: 'KS-CancelProp-Button_node_0_parent_root').click; b.loading_wait }

  action(:select_rule_a_for_action) {|b| b.div(id: 'editWithObjectTree').span(text: /^A\./).click }
  action(:select_rule_b_for_action) {|b| b.div(id: 'editWithObjectTree').span(text: /^B\./).click }
  action(:select_rule_c_for_action) {|b| b.div(id: 'editWithObjectTree').span(text: /^C\./).click }
  action(:select_rule_d_for_action) {|b| b.div(id: 'editWithObjectTree').span(text: /^D\./).click }
  action(:select_rule_x_for_action) {|any_letter='E', b| b.div(id: 'editWithObjectTree').span(text: /^#{any_letter}\./).click }

  element(:select_rule_type) { |b| b.select_list(id: 'KRMS-PropositionType-Dropdown_node_0_parent_root_control') }
  action(:preview_change) { |b| b.button(text: 'Preview Change').click; b.loading_wait }

  action(:update_rule) { |b| b.button(id: 'KS-UpdateRule-Button').click; b.loading_wait }
  action(:cancel_rule) { |b| b.link(id: 'KS-CancelRule-Button').click; b.loading_wait }

  #repeatable_for_credit_rule: ['May be repeated for a maximum of <n> credits', 'Free Form Text']

  #course_that_restricts_credits_rule: ['Must not have successfully completed <course>', 'Must not have successfully completed any courses from <courses>', 'Free Form Text']

  element(:rule_course_field) {|b| b.text_field(name: 'document.newMaintainableObject.editTree.rootElement.children[0].data.proposition.courseInfo.code').when_present }
  element(:free_form_text) {|b| b.text_field(name: /^document\.newMaintainableObject\.editTree\.rootElement\.children/) }
  element(:rule_credit) { |b| b.text_field(name: 'document.newMaintainableObject.editTree.rootElement.children[0].data.proposition.parameters[1].value') }

  action(:added_rule_restricts_credits_text) {|rule_text='you forgot to pass text', b| b.div(id: 'KS-ViewTree-Group_ruleF').span(text: /#{rule_text}/) }

  element(:search_results_table) {|b| b.frame(class: 'fancybox-iframe').div(class: 'dataTables_wrapper').table }


  element(:course_requisite_added_rule){ |rule_name,b| b.span(text: rule_name)}

  element(:student_eligibility_prerequisite_added_rule){ |rule_name,b| b.span(text: rule_name)}
  element(:corequisite_added_rule){ |rule_name,b| b.span(text: rule_name)}
  element(:recommended_preparation_added_rule){ |rule_name,b| b.span(text: rule_name)}
  element(:antirequisite_added_rule){ |rule_name,b| b.span(text: rule_name)}
  element(:repeatable_for_credit_added_rule){ |rule_name,b| b.span(text: rule_name)}
  element(:course_that_restricts_credits_added_rule){ |rule_name,b| b.span(text: rule_name)}



#RECOMMENDED PREPARTATION

  #page.rule_credit.set '2'
  #page.rule_multicourse_type.set 'Approved Courses'
  #page.rule_course_code_multiple.fit @multiple_course1
  #page.add_course_code
  #page.rule_course_code_multiple.fit @multiple_course2
  #page.preview_change



       #ADDING RULE COURSE THAT RESTRICTS CREDIT
  element(:rule_multicourse_type){ |b| b.select_list(id: 'KRMS-MultiCourse-Type-Field_control') }
  # 'Approved Courses', 'Course Sets', 'Course Ranges (Course numbers, common learning objectives, etc)'
  element(:rule_course_code_multiple) { |b| b.text_field(name: 'newCollectionLines[\'document.newMaintainableObject.editTree.rootElement.children_0_.data.proposition.cluSet.clus\'].code') }
  action(:add_course_code) {|b| b.button(id: 'KRMS-ApprovedCourseStackedCollectionGroup_add').click; b.loading_wait }
            #STACK TRACE WHEN PREVIEWING RULE CHANGE




end #class

