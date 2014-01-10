class CourseOfferingRequisites < BasePage

  expected_element :cancel_btn

  wrapper_elements
  krms_frame_elements

  element(:agenda_management_section) { |b| b.frm.div(id: "KSCO-AgendaMaintenance-Page")}
  element(:preview_tree) { |b| b.frm.div(id: "KRMS-PreviewTree-Group")}

  element(:antirequisite_section) { |b| b.frm.a(id: "KSCO-AgendaManage-RulePrototype_ruleD_toggle") }
  action(:antirequisite) { |b| b.antirequisite_section.click; b.loading.wait_while_present }
  element(:corequisite_section) { |b| b.frm.a(id: "KSCO-AgendaManage-RulePrototype_ruleB_toggle") }
  action(:corequisite) { |b| b.corequisite_section.click; b.loading.wait_while_present }
  element(:recommended_prep_section) { |b| b.frm.a(id: "KSCO-AgendaManage-RulePrototype_ruleC_toggle") }
  action(:recommended_prep) { |b| b.recommended_prep_section.click; b.loading.wait_while_present }
  element(:eligibility_prereq_section) { |b| b.frm.a(id: "KSCO-AgendaManage-RulePrototype_ruleA_toggle") }
  action(:eligibility_prereq) { |b| b.eligibility_prereq_section.click; b.loading.wait_while_present }
  element(:repeatable_credit_section) { |b| b.frm.a(id: "KSCO-AgendaManage-RulePrototype_ruleE_toggle") }
  action(:repeatable_credit) { |b| b.repeatable_credit_section.click; b.loading.wait_while_present }
  element(:restricted_credit_section) { |b| b.frm.a(id: "KSCO-AgendaManage-RulePrototype_ruleF_toggle") }
  action(:restricted_credit) { |b| b.resctricted_credit_section.click; b.loading.wait_while_present }

  element(:eligibility_prereq_edit_links) {|b| b.frm.div( id: "KSCO-RuleEdit-ActionLinks_ruleA")}
  element(:eligibility_prereq_add_links) {|b| b.frm.div( id: "KSCO-RuleAdd-ActionLink_ruleA")}
  element(:prereq_compare_link) { |b| b.div( id: /KSCO-Rule.+-ActionLinks_ruleA/).a(:text => /Compare to Catalog/)}
  action(:prereq_compare) { |b| b.prereq_compare_link.click; b.loading.wait_while_present(60) }
  element(:prereq_edit_link) { |b| b.eligibility_prereq_edit_links.a(:text => /Edit Rule/)}
  action(:prereq_edit) { |b| b.prereq_edit_link.click; b.loading.wait_while_present(60) }
  element(:prereq_delete_link) { |b| b.eligibility_prereq_edit_links.a(:text => /Delete Rule/)}
  action(:prereq_delete) { |b| b.prereq_delete_link.click; b.loading.wait_while_present }
  element(:prereq_add_link) { |b| b.eligibility_prereq_add_links.a(:text => /Add Rule/)}
  action(:prereq_add) { |b| b.prereq_add_link.click; b.loading.wait_while_present }

  element(:corequisite_edit_links) {|b| b.frm.div( id: "KSCO-RuleEdit-ActionLinks_ruleB")}
  element(:corequisite_add_links) {|b| b.frm.div( id: "KSCO-RuleAdd-ActionLink_ruleB")}
  element(:coreq_compare_link) { |b| b.div( id: /KSCO-Rule.+-ActionLinks_ruleB/).a(:text => /Compare to Catalog/)}
  action(:coreq_compare) { |b| b.coreq_compare_link.click; b.loading.wait_while_present(60) }
  element(:coreq_edit_link) { |b| b.corequisite_edit_links.a(:text => /Edit Rule/)}
  action(:coreq_edit) { |b| b.coreq_edit_link.click; b.loading.wait_while_present(60) }
  element(:coreq_delete_link) { |b| b.corequisite_edit_links.a(:text => /Delete Rule/)}
  action(:coreq_delete) { |b| b.coreq_delete_link.click; b.loading.wait_while_present }
  element(:coreq_add_link) { |b| b.corequisite_add_links.a(:text => /Add Rule/)}
  action(:coreq_add) { |b| b.coreq_add_link.click; b.loading.wait_while_present }

  element(:recommended_prep_edit_links) {|b| b.frm.div( id: "KSCO-RuleEdit-ActionLinks_ruleC")}
  element(:recommended_prep_add_links) {|b| b.frm.div( id: "KSCO-RuleAdd-ActionLink_ruleC")}
  element(:prep_compare_link) { |b| b.div( id: /KSCO-Rule.+-ActionLinks_ruleC/).a(:text => /Compare to Catalog/)}
  action(:prep_compare) { |b| b.prep_compare_link.click; b.loading.wait_while_present(60) }
  element(:prep_edit_link) { |b| b.recommended_prep_edit_links.a(:text => /Edit Rule/)}
  action(:prep_edit) { |b| b.prep_edit_link.click; b.loading.wait_while_present(60) }
  element(:prep_delete_link) { |b| b.recommended_prep_edit_links.a(:text => /Delete Rule/)}
  action(:prep_delete) { |b| b.prep_delete_link.click; b.loading.wait_while_present }
  element(:prep_add_link) { |b| b.recommended_prep_add_links.a(:text => /Add Rule/)}
  action(:prep_add) { |b| b.prep_add_link.click; b.loading.wait_while_present }

  element(:antirequisite_edit_links) {|b| b.frm.div( id: "KSCO-RuleEdit-ActionLinks_ruleD")}
  element(:antirequisite_add_links) {|b| b.frm.div( id: "KSCO-RuleAdd-ActionLink_ruleD")}
  element(:antireq_compare_link) { |b| b.div( id: /KSCO-Rule.+-ActionLinks_ruleD/).a(:text => /Compare to Catalog/)}
  action(:antireq_compare) { |b| b.antireq_compare_link.click; b.loading.wait_while_present(60) }
  element(:antireq_edit_link) { |b| b.antirequisite_edit_links.a(:text => /Edit Rule/)}
  action(:antireq_edit) { |b| b.antireq_edit_link.click; b.loading.wait_while_present(60) }
  element(:antireq_delete_link) { |b| b.antirequisite_edit_links.a(:text => /Delete Rule/)}
  action(:antireq_delete) { |b| b.antireq_delete_link.click; b.loading.wait_while_present }
  element(:antireq_add_link) { |b| b.antirequisite_add_links.a(:text => /Add Rule/)}
  action(:antireq_add) { |b| b.antireq_add_link.click; b.loading.wait_while_present }

  element(:repeatable_credit_edit_links) {|b| b.frm.div( id: "KSCO-RuleEdit-ActionLinks_ruleE")}
  element(:repeatable_credit_add_links) {|b| b.frm.div( id: "KSCO-RuleAdd-ActionLink_ruleE")}
  element(:repeat_compare_link) { |b| b.div( id: /KSCO-Rule.+-ActionLinks_ruleE/).a(:text => /Compare to Catalog/)}
  action(:repeat_compare) { |b| b.repeat_compare_link.click; b.loading.wait_while_present(60) }
  element(:repeat_edit_link) { |b| b.repeatable_credit_edit_links.a(:text => /Edit Rule/)}
  action(:repeat_edit) { |b| b.repeat_edit_link.click; b.loading.wait_while_present(60) }
  element(:repeat_delete_link) { |b| b.repeatable_credit_edit_links.a(:text => /Delete Rule/)}
  action(:repeat_delete) { |b| b.repeat_delete_link.click; b.loading.wait_while_present }
  element(:repeat_add_link) { |b| b.repeatable_credit_add_links.a(:text => /Add Rule/)}
  action(:repeat_add) { |b| b.repeat_add_link.click; b.loading.wait_while_present }

  element(:restricted_credit_edit_links) {|b| b.frm.div( id: "KSCO-RuleEdit-ActionLinks_ruleF")}
  element(:restricted_credit_add_links) {|b| b.frm.div( id: "KSCO-RuleAdd-ActionLink_ruleF")}
  element(:restrict_compare_link) { |b| b.div( id: /KSCO-Rule.+-ActionLinks_ruleF/).a(:text => /Compare to Catalog/)}
  action(:restrict_compare) { |b| b.restrict_compare_link.click; b.loading.wait_while_present(60) }
  element(:restrict_edit_link) { |b| b.resctricted_credit_edit_links.a(:text => /Edit Rule/)}
  action(:restrict_edit) { |b| b.restrict_edit_link.click; b.loading.wait_while_present(60) }
  element(:restrict_delete_link) { |b| b.resctricted_credit_edit_links.a(:text => /Delete Rule/)}
  action(:restrict_delete) { |b| b.restrict_delete_link.click; b.loading.wait_while_present }
  element(:restrict_add_link) { |b| b.resctricted_credit_add_links.a(:text => /Add Rule/)}
  action(:restrict_add) { |b| b.restrict_add_link.click; b.loading.wait_while_present }

  element(:compare_section) { |b| b.frm.div(id: "compareCluAndCoRuleLightBox")}
  element(:compare_tree) { |b| b.compare_section.div( id: "KRMS-CompareTree-Section")}
  element(:compare_cancel_link) { |b| b.compare_section.a(:text => /Cancel/)}
  action(:compare_cancel) { |b| b.compare_cancel_link.click; b.loading.wait_while_present}

  element(:submit_btn) { |b| b.frm.button(:text => /Save/)}
  action(:submit) { |b| b.submit_btn.click; b.loading.wait_while_present }
  element(:cancel_btn) { |b| b.frm.a(:text => /Cancel/)}
  action(:cancel) { |b| b.cancel_btn.click; b.loading.wait_while_present }
end