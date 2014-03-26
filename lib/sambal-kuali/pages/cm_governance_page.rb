class CmGovernance < BasePage

  wrapper_elements
  cm_elements

#CAMPUS LOCATIONS
  element(:location_north) { |b| b.checkbox(value: 'NO') }
  element(:location_south) { |b| b.checkbox(value: 'SO') }
  element(:location_extended) { |b| b.checkbox(value: 'EX') }
  element(:location_all) { |b| b.checkbox(value: 'AL') }

#CURRICULUM OVERSIGHT
  element(:curriculum_oversight) { |b| b.select_list(name: 'document.newMaintainableObject.dataObject.unitsContentOwnerToAdd') }
  element(:add_oversight_button) { |b| b.button(id: 'addCurriculumOversight')}
  action(:add_oversight) { |b| b.add_oversight_button.click; b.loading_wait }
  action(:curriculum_oversight_when_added) {|added_org, b| b.span(text: added_org) }

#ADMINISTERING ORGANIZATION
  element(:administering_organization) {|b| b.text_field(name: 'newCollectionLines[\'document.newMaintainableObject.administeringOrganizations\'].organizationName') }
  action(:organization_add) {|b| b.button(id: 'KS-AdministeringOrganization-Section_add').click; b.adding_line_wait }

  # 0 is default for the first ADDED organization
  action(:added_administering_organization) {|org_added='0', b| b.text_field(name: /#{org_added}\]\.organizationName$/) }
  action(:adv_search_admin_org) {|b| b.link(text: 'Advanced Search').click; b.adv_search_button.wait_until_present }

 # GOVERNANCE REVIEW FIELDS
  action(:edit_governance) { |b| b.a(id: "Governance-Review-Edit-link").click }
  value(:campus_locations_review) { |b| b.div(id: 'Governance-Review-section').div(data_label: 'Campus Locations').span(class: "uif-readOnlyContent").text }
  value(:curriculum_oversight_review) { |b| b.div(id: 'Governance-Review-section').div(data_label: 'Curriculum Oversight').span(class: "uif-readOnlyContent").text }

end