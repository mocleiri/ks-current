class ActivityOfferingRequisites < BasePage

  expected_element :cancel_btn

  wrapper_elements
  krms_frame_elements

  element(:agenda_management_section) { |b| b.frm.div(id: "KSAO-AgendaMaintenance-Page")}
  element(:preview_tree) { |b| b.frm.div(id: "KRMS-PreviewTree-Group")}

  element(:eligibility_prereq_section) { |b| b.frm.div(id: "KSAO-AgendaManage-RulePrototype_ruleA") }
  element(:corequisite_section) { |b| b.frm.div(id: "KSAO-AgendaManage-RulePrototype_ruleB") }
  element(:recommended_prep_section) { |b| b.frm.div(id: "KSAO-AgendaManage-RulePrototype_ruleC") }
  element(:antirequisite_section) { |b| b.frm.div(id: "KSAO-AgendaManage-RulePrototype_ruleD") }
  element(:repeatable_credit_section) { |b| b.frm.div(id: "KSAO-AgendaManage-RulePrototype_ruleE") }
  element(:restricted_credit_section) { |b| b.frm.div(id: "KSAO-AgendaManage-RulePrototype_ruleF") }

  element(:eligibility_prereq_section_link) { |b| b.frm.a(id: "KSAO-AgendaManage-RulePrototype_ruleA_toggle") }
  action(:eligibility_prereq) { |b| b.eligibility_prereq_section_link.click; b.loading.wait_while_present }
  element(:corequisite_section_link) { |b| b.frm.a(id: "KSAO-AgendaManage-RulePrototype_ruleB_toggle") }
  action(:corequisite) { |b| b.corequisite_section_link.click; b.loading.wait_while_present }
  element(:recommended_prep_section_link) { |b| b.frm.a(id: "KSAO-AgendaManage-RulePrototype_ruleC_toggle") }
  action(:recommended_prep) { |b| b.recommended_prep_section_link.click; b.loading.wait_while_present }
  element(:antirequisite_section_link) { |b| b.frm.a(id: "KSAO-AgendaManage-RulePrototype_ruleD_toggle") }
  action(:antirequisite) { |b| b.antirequisite_section_link.click; b.loading.wait_while_present }
  element(:repeatable_credit_section_link) { |b| b.frm.a(id: "KSAO-AgendaManage-RulePrototype_ruleE_toggle") }
  action(:repeatable_credit) { |b| b.repeatable_credit_section_link.click; b.loading.wait_while_present }
  element(:restricted_credit_section_link) { |b| b.frm.a(id: "KSAO-AgendaManage-RulePrototype_ruleF_toggle") }
  action(:restricted_credit) { |b| b.resctricted_credit_section_link.click; b.loading.wait_while_present }

  element(:eligibility_prereq_edit_links) {|b| b.frm.div( id: "KSAO-RuleEdit-ActionLinks_ruleA")}
  element(:eligibility_prereq_add_links) {|b| b.frm.div( id: "KSAO-RuleAdd-ActionLink_ruleA")}
  element(:prereq_compare_link) { |b| b.eligibility_prereq_edit_links.a(:text => /Compare/)}
  action(:prereq_compare) { |b| b.prereq_compare_link.click; b.loading.wait_while_present(60) }
  element(:prereq_edit_link) { |b| b.eligibility_prereq_edit_links.a(:text => /Edit Rule/)}
  action(:prereq_edit) { |b| b.prereq_edit_link.click; b.loading.wait_while_present(60) }
  element(:prereq_edit_suppress_link) { |b| b.eligibility_prereq_edit_links.a(:text => /Suppress Rule/)}
  action(:prereq_edit_suppress) { |b| b.prereq_edit_suppress_link.click; b.loading.wait_while_present }
  element(:prereq_revert_link) { |b| b.eligibility_prereq_edit_links.a(:text => /Revert to Course Offering/)}
  action(:prereq_revert) { |b| b.prereq_revert_link.click; b.loading.wait_while_present }
  element(:prereq_add_link) { |b| b.eligibility_prereq_add_links.a(:text => /Add Rule/)}
  action(:prereq_add) { |b| b.prereq_add_link.click; b.loading.wait_while_present }
  element(:prereq_view_link) { |b| b.div(:id => /KSAO-Rule.+-ActionLink_ruleA/).a(:text => /View Catalog and Course Offering Rule/)}
  action(:prereq_view) { |b| b.prereq_view_link.click; b.loading.wait_while_present }
  element(:prereq_copy_edit_link) { |b| b.eligibility_prereq_add_links.a(:text => /Copy and Edit Course Offering Rule/)}
  action(:prereq_copy_edit) { |b| b.prereq_copy_edit_link.click; b.loading.wait_while_present }
  element(:prereq_replace_link) { |b| b.eligibility_prereq_add_links.a(:text => /Replace Rule/)}
  action(:prereq_replace) { |b| b.prereq_replace_link.click; b.loading.wait_while_present }
  element(:prereq_suppress_link) { |b| b.eligibility_prereq_add_links.a(:text => /Suppress Rule/)}
  action(:prereq_suppress) { |b| b.prereq_suppress_link.click; b.loading.wait_while_present }
  element(:prereq_section) { |b| b.frm.div( id: "KSAO-AgendaManage-RulePrototype_ruleA_disclosureContent")}
  element(:prereq_message_section_warning) { |b| b.prereq_section.li( class: "uif-warningMessageItem")}
  element(:prereq_message_section_info) { |b| b.prereq_section.li( class: "uif-infoMessageItem")}

  element(:corequisite_edit_links) {|b| b.frm.div( id: "KSAO-RuleEdit-ActionLinks_ruleB")}
  element(:corequisite_add_links) {|b| b.frm.div( id: "KSAO-RuleAdd-ActionLink_ruleB")}
  element(:coreq_compare_link) { |b| b.corequisite_edit_links.a(:text => /Compare/)}
  action(:coreq_compare) { |b| b.coreq_compare_link.click; b.loading.wait_while_present(60) }
  element(:coreq_edit_link) { |b| b.corequisite_edit_links.a(:text => /Edit Rule/)}
  action(:coreq_edit) { |b| b.coreq_edit_link.click; b.loading.wait_while_present(60) }
  element(:coreq_edit_suppress_link) { |b| b.corequisite_edit_links.a(:text => /Suppress Rule/)}
  action(:coreq_edit_suppress) { |b| b.coreq_edit_suppress_link.click; b.loading.wait_while_present }
  element(:coreq_revert_link) { |b| b.corequisite_edit_links.a(:text => /Revert to Course Offering/)}
  action(:coreq_revert) { |b| b.coreq_revert_link.click; b.loading.wait_while_present }
  element(:coreq_add_link) { |b| b.corequisite_add_links.a(:text => /Add Rule/)}
  action(:coreq_add) { |b| b.coreq_add_link.click; b.loading.wait_while_present }
  element(:coreq_view_link) { |b| b.div(:id => /KSAO-Rule.+-ActionLink_ruleB/).a(:text => /View Catalog and Course Offering Rule/)}
  action(:coreq_view) { |b| b.coreq_view_link.click; b.loading.wait_while_present }
  element(:coreq_copy_edit_link) { |b| b.corequisite_add_links.a(:text => /Copy and Edit Course Offering Rule/)}
  action(:coreq_copy_edit) { |b| b.coreq_copy_edit_link.click; b.loading.wait_while_present }
  element(:coreq_replace_link) { |b| b.corequisite_add_links.a(:text => /Replace Rule/)}
  action(:coreq_replace) { |b| b.coreq_replace_link.click; b.loading.wait_while_present }
  element(:coreq_suppress_link) { |b| b.corequisite_add_links.a(:text => /Suppress Rule/)}
  action(:coreq_suppress) { |b| b.coreq_suppress_link.click; b.loading.wait_while_present }
  element(:coreq_section) { |b| b.frm.div( id: "KSAO-AgendaManage-RulePrototype_ruleB_disclosureContent")}
  element(:coreq_message_section_warning) { |b| b.coreq_section.li( class: "uif-warningMessageItem")}
  element(:coreq_message_section_info) { |b| b.coreq_section.li( class: "uif-infoMessageItem")}

  element(:recommended_prep_edit_links) {|b| b.frm.div( id: "KSAO-RuleEdit-ActionLinks_ruleC")}
  element(:recommended_prep_add_links) {|b| b.frm.div( id: "KSAO-RuleAdd-ActionLink_ruleC")}
  element(:prep_compare_link) { |b| b.recommended_prep_edit_links.a(:text => /Compare/)}
  action(:prep_compare) { |b| b.prep_compare_link.click; b.loading.wait_while_present(60) }
  element(:prep_edit_link) { |b| b.recommended_prep_edit_links.a(:text => /Edit Rule/)}
  action(:prep_edit) { |b| b.prep_edit_link.click; b.loading.wait_while_present(60) }
  element(:prep_edit_suppress_link) { |b| b.recommended_prep_edit_links.a(:text => /Suppress Rule/)}
  action(:prep_edit_suppress) { |b| b.prep_edit_suppress_link.click; b.loading.wait_while_present }
  element(:prep_revert_link) { |b| b.recommended_prep_edit_links.a(:text => /Revert to Course Offering/)}
  action(:prep_revert) { |b| b.prep_revert_link.click; b.loading.wait_while_present }
  element(:prep_add_link) { |b| b.recommended_prep_add_links.a(:text => /Add Rule/)}
  action(:prep_add) { |b| b.prep_add_link.click; b.loading.wait_while_present }
  element(:prep_view_link) { |b| b.div(:id => /KSAO-Rule.+-ActionLink_ruleC/).a(:text => /View Catalog and Course Offering Rule/)}
  action(:prep_view) { |b| b.prep_view_link.click; b.loading.wait_while_present }
  element(:prep_copy_edit_link) { |b| b.recommended_prep_add_links.a(:text => /Copy and Edit Course Offering Rule/)}
  action(:prep_copy_edit) { |b| b.prep_copy_edit_link.click; b.loading.wait_while_present }
  element(:prep_replace_link) { |b| b.recommended_prep_links.a(:text => /Replace Rule/)}
  action(:prep_replace) { |b| b.prep_replace_link.click; b.loading.wait_while_present }
  element(:prep_suppress_link) { |b| b.recommended_prep_add_links.a(:text => /Suppress Rule/)}
  action(:prep_suppress) { |b| b.prep_suppress_link.click; b.loading.wait_while_present }
  element(:prep_message_section) { |b| b.frm.div( id: "KSAO-AgendaManage-RulePrototype_ruleC_disclosureContent")}

  element(:antirequisite_edit_links) {|b| b.frm.div( id: "KSAO-RuleEdit-ActionLinks_ruleD")}
  element(:antirequisite_add_links) {|b| b.frm.div( id: "KSAO-RuleAdd-ActionLink_ruleD")}
  element(:antireq_compare_link) { |b| b.antirequisite_edit_links.a(:text => /Compare/)}
  action(:antireq_compare) { |b| b.antireq_compare_link.click; b.loading.wait_while_present(60) }
  element(:antireq_edit_link) { |b| b.antirequisite_edit_links.a(:text => /Edit Rule/)}
  action(:antireq_edit) { |b| b.antireq_edit_link.click; b.loading.wait_while_present(60) }
  element(:antireq_edit_suppress_link) { |b| b.antirequisite_edit_links.a(:text => /Suppress Rule/)}
  action(:antireq_edit_suppress) { |b| b.antireq_edit_suppress_link.click; b.loading.wait_while_present }
  element(:antireq_revert_link) { |b| b.antirequisite_edit_links.a(:text => /Revert to Course Offering/)}
  action(:antireq_revert) { |b| b.antireq_revert_link.click; b.loading.wait_while_present }
  element(:antireq_add_link) { |b| b.antirequisite_add_links.a(:text => /Add Rule/)}
  action(:antireq_add) { |b| b.antireq_add_link.click; b.loading.wait_while_present }
  element(:antireq_view_link) { |b| b.div(:id => /KSAO-Rule.+-ActionLink_ruleD/).a(:text => /View Catalog and Course Offering Rule/)}
  action(:antireq_view) { |b| b.antireq_view_link.click; b.loading.wait_while_present }
  element(:antireq_copy_edit_link) { |b| b.antirequisite_add_links.a(:text => /Copy and Edit Course Offering Rule/)}
  action(:antireq_copy_edit) { |b| b.antireq_copy_edit_link.click; b.loading.wait_while_present }
  element(:antireq_replace_link) { |b| b.antirequisite_add_links.a(:text => /Replace Rule/)}
  action(:antireq_replace) { |b| b.antireq_replace_link.click; b.loading.wait_while_present }
  element(:antireq_suppress_link) { |b| b.antirequisite_add_links.a(:text => /Suppress Rule/)}
  action(:antireq_suppress) { |b| b.antireq_suppress_link.click; b.loading.wait_while_present }
  element(:antireq_message_section) { |b| b.frm.div( id: "KSAO-AgendaManage-RulePrototype_ruleD_disclosureContent")}

  element(:repeatable_credit_edit_links) {|b| b.frm.div( id: "KSAO-RuleEdit-ActionLinks_ruleE")}
  element(:repeatable_credit_add_links) {|b| b.frm.div( id: "KSAO-RuleAdd-ActionLink_ruleE")}
  element(:repeat_compare_link) { |b| b.repeatable_credit_edit_links.a(:text => /Compare/)}
  action(:repeat_compare) { |b| b.repeat_compare_link.click; b.loading.wait_while_present(60) }
  element(:repeat_edit_link) { |b| b.repeatable_credit_edit_links.a(:text => /Edit Rule/)}
  action(:repeat_edit) { |b| b.repeat_edit_link.click; b.loading.wait_while_present(60) }
  element(:repeat_edit_suppress_link) { |b| b.repeatable_credit_edit_links.a(:text => /Suppress Rule/)}
  action(:repeat_edit_suppress) { |b| b.repeat_edit_suppress_link.click; b.loading.wait_while_present }
  element(:repeat_revert_link) { |b| b.repeatable_credit_edit_links.a(:text => /Revert to Course Offering/)}
  action(:repeat_revert) { |b| b.repeat_revert_link.click; b.loading.wait_while_present }
  element(:repeat_add_link) { |b| b.repeatable_credit_add_links.a(:text => /Add Rule/)}
  action(:repeat_add) { |b| b.repeat_add_link.click; b.loading.wait_while_present }
  element(:repeat_view_link) { |b| b.div(:id => /KSAO-Rule.+-ActionLink_ruleE/).a(:text => /View Catalog and Course Offering Rule/)}
  action(:repeat_view) { |b| b.repeat_view_link.click; b.loading.wait_while_present }
  element(:repeat_copy_edit_link) { |b| b.repeatable_credit_add_links.a(:text => /Copy and Edit Course Offering Rule/)}
  action(:repeat_copy_edit) { |b| b.repeat_copy_edit_link.click; b.loading.wait_while_present }
  element(:repeat_replace_link) { |b| b.repeatable_credit_add_links.a(:text => /Replace Rule/)}
  action(:repeat_replace) { |b| b.repeat_replace_link.click; b.loading.wait_while_present }
  element(:repeat_suppress_link) { |b| b.repeatable_credit_add_links.a(:text => /Suppress Rule/)}
  action(:repeat_suppress) { |b| b.repeat_suppress_link.click; b.loading.wait_while_present }
  element(:repeat_message_section) { |b| b.frm.div( id: "KSAO-AgendaManage-RulePrototype_ruleE_disclosureContent")}

  element(:restricted_credit_edit_links) {|b| b.frm.div( id: "KSAO-RuleEdit-ActionLinks_ruleF")}
  element(:restricted_credit_add_links) {|b| b.frm.div( id: "KSAO-RuleAdd-ActionLink_ruleF")}
  element(:restrict_compare_link) { |b| b.restricted_credit_edit_links.a(:text => /Compare/)}
  action(:restrict_compare) { |b| b.restrict_compare_link.click; b.loading.wait_while_present(60) }
  element(:restrict_edit_link) { |b| b.resctricted_credit_edit_links.a(:text => /Edit Rule/)}
  action(:restrict_edit) { |b| b.restrict_edit_link.click; b.loading.wait_while_present(60) }
  element(:restrict_edit_suppress_link) { |b| b.resctricted_credit_edit_links.a(:text => /Suppress Rule/)}
  action(:restrict_edit_suppress) { |b| b.restrict_edit_suppress_link.click; b.loading.wait_while_present }
  element(:restrict_revert_link) { |b| b.restricted_credit_edit_links.a(:text => /Revert to Course Offering/)}
  action(:restrict_revert) { |b| b.restrict_revert_link.click; b.loading.wait_while_present }
  element(:restrict_add_link) { |b| b.resctricted_credit_add_links.a(:text => /Add Rule/)}
  action(:restrict_add) { |b| b.restrict_add_link.click; b.loading.wait_while_present }
  element(:restrict_view_link) { |b| b.div(:id => /KSAO-Rule.+-ActionLink_ruleF/).a(:text => /View Catalog and Course Offering Rule/)}
  action(:restrict_view) { |b| b.restrict_view_link.click; b.loading.wait_while_present }
  element(:restrict_copy_edit_link) { |b| b.restricted_credit_add_links.a(:text => /Copy and Edit Course Offering Rule/)}
  action(:restrict_copy_edit) { |b| b.restrict_copy_edit_link.click; b.loading.wait_while_present }
  element(:restrict_replace_link) { |b| b.restricted_credit_add_links.a(:text => /Replace Rule/)}
  action(:restrict_replace) { |b| b.restrict_replace_link.click; b.loading.wait_while_present }
  element(:restrict_suppress_link) { |b| b.restricted_credit_add_links.a(:text => /Suppress Rule/)}
  action(:restrict_suppress) { |b| b.restrict_suppress_link.click; b.loading.wait_while_present }
  element(:restrict_message_section) { |b| b.frm.div( id: "KSAO-AgendaManage-RulePrototype_ruleF_disclosureContent")}

  element(:compare_section) { |b| b.frm.div(id: "compareCluCoAndAoRuleLightBox")}
  element(:compare_tree) { |b| b.compare_section.div( id: "KRMS-AoCompareTree-Section")}
  element(:compare_cancel_link) { |b| b.compare_section.button(:text => /Close/)}
  action(:compare_cancel) { |b| b.compare_cancel_link.click; b.loading.wait_while_present}

  element(:view_section) { |b| b.frm.div(id: "viewCluAndCoRuleLightBox")}
  element(:view_tree) { |b| b.view_section.div( id: "KRMS-CompareTree-Section")}
  element(:view_cancel_link) { |b| b.view_section.button(:text => /Close/)}
  action(:view_cancel) { |b| b.view_cancel_link.click; b.loading.wait_while_present}

  element(:submit_btn) { |b| b.frm.button(:text => /Save/)}
  action(:submit) { |b| b.submit_btn.click; b.loading.wait_while_present }
  element(:cancel_btn) { |b| b.frm.a(:text => /Cancel/)}
  action(:cancel) { |b| b.cancel_btn.click; b.loading.wait_while_present }
end