class CmGovernance < BasePage

  wrapper_elements
  cm_elements

#CAMPUS LOCATIONS
  element(:location_north) { |b| b.checkbox(value: 'NO') }
  element(:location_south) { |b| b.checkbox(value: 'SO') }
  element(:location_extended) { |b| b.checkbox(value: 'EX') }
  element(:location_all) { |b| b.checkbox(value: 'AL') }

#CURRICULUM OVERSIGHT
  element(:curriculum_oversight) { |b| b.select_list(id: /KS-Course-CurriculumOversight-input-field/) }
  action(:add_organization) {|b| b.button(id: "KS-Course-CurriculumOversight-input-field").click; b.loading_wait }
  element(:add_oversight_button) { |b| b.button(id: 'addCurriculumOversight')}
  action(:add_oversight) { |b| b.add_oversight_button.click; b.loading_wait }
  action(:curriculum_oversight_when_added) {|added_org, b| b.div(text: added_org) }

#ADMINISTERING ORGANIZATION
  element(:admin_org_name) {|admin_org_level,b| b.text_field(id: "organizationName_line#{admin_org_level-1}_control") }
  action(:organization_add) {|b| b.button(id: "addAdministeringOrganization").click; b.loading_wait }
  action(:delete_admin_org) { |admin_org_level,b| b.a(id: "delete_administeringorganization_line#{admin_org_level-1}").i(class: "ks-fontello-icon-cancel").click; b.loading_wait }

  # 0 is default for the first ADDED organization
  action(:added_administering_organization) {|org_added='0', b| b.text_field(name: /#{org_added}\]\.organizationName$/) }
  action(:adv_search_admin_org) {|b| b.link(text: 'Advanced Search').click; b.adv_search_button.wait_until_present }


end