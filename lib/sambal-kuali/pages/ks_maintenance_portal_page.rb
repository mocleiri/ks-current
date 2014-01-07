class KSMaintenancePortal < BasePage

  page_url "#{$test_site}/portal.do"
  #no expected_element - could land on or login page first

  value(:copyright) { |b| b.div(id: "footer-copyright").text }
  action(:acknowledgements) { |b| b.link(href: "acknowledgments.jsp").click }
  value(:build) { |b| b.div(id: "build").text }
  action(:doc_search) { |b| b.link(title: "Document Search").click }
  action(:provide_feedback) { |b| b.link(title: "Provide Feedback").click }
  action(:action_list) { |b| b.link(title: "Action List").click }

  element(:enrollment_home_link) { |b| b.link(text: "Enrollment") }

  action(:enrollment_home) { |b| b.enrollment_home_link.click }
  action(:kuali_student_home) { |b| b.link(text: "Kuali Student Home").click }
  action(:curriculum_management) { |b| b.link(text: "Curriculum Management").click }
  action(:manage_reg_windows) { |b| b.link(text: "Manage Registration Windows And Appointments").click }

  action(:atp) { |b| b.link(title: "ATP").click }
  action(:date_range) { |b| b.link(title: "Date Range").click }
  action(:milestone) { |b| b.link(title: "Milestone").click }
  action(:enumeration) { |b| b.link(title: "Enumeration").click }
  action(:enumerated_value) { |b| b.link(title: "Enumerated Value").click }
  action(:message) { |b| b.link(title: "Message").click }
  action(:atp_type) { |b| b.link(title: "ATP Type").click }
  action(:atp_duration_type) { |b| b.link(title: "ATP Duration Type").click }
  action(:atp_seasonal_type) { |b| b.link(title: "ATP Seasonal Type").click }
  action(:date_range_type) { |b| b.link(title: "Date Range Type").click }
  action(:milestone_type) { |b| b.link(title: "Milestone Type").click }
  action(:user_preferences) { |b| b.link(title: "User Preferences").click }
  action(:quicklinks) { |b| b.link(title: "Quicklinks").click }
  action(:routing_report) { |b| b.link(title: "Routing Report").click }
  action(:routing_rules) { |b| b.link(title: "Routing Rules").click }
  action(:routing_rules_delegation) { |b| b.link(title: "Routing Rules Delegation").click }
  action(:routing_and_id_mgmt_doc_heirarchy) { |b| b.link(title: "Routing and Identity Management Document Type Hierarchy").click }
  action(:edoc_lite) { |b| b.link(title: "eDoc Lite").click }
  action(:people_flow) { |b| b.link(title: "People Flow").click }
  action(:notification_search) { |b| b.link(title: "Notification Search").click }
  action(:channel_subscriptions) { |b| b.link(title: "Channel Subscriptions").click }
  action(:delivery_types) { |b| b.link(title: "Delivery Types").click }
  action(:create_new_agenda) { |b| b.link(title: "Create New Agenda").click }
  action(:agenda_lookup) { |b| b.link(title: "Agenda Lookup").click }
  action(:attribute_definition_lookup) { |b| b.link(title: "Attribute Definition Lookup").click }
  action(:term_lookup) { |b| b.link(title: "Term Lookup").click }
  action(:term_specification_lookup) { |b| b.link(title: "Term Specification Lookup").click }
  action(:category_lookup) { |b| b.link(title: "Category Lookup").click }
  action(:manage_population) { |b| b.link(title: "Manage Population (Lookup)").click }
  action(:population_maintenance_edoc) { |b| b.link(title: "Population Maintenance eDoc (New)").click }

  #Dev Utilities
  action(:test_state_propagation) { |b| b.link(text: "Test State Propagation").click }
  action(:create_soc_for_term) { |b| b.link(text: "Create Soc for Term").click }
  action(:test_soc_state_change) { |b| b.link(text: "Test SOC State Change").click }

  #KRMS Links
  action(:krms_components) { |b| b.link(text: "KRMS Components").click }
  action(:krms_edit_agenda) { |b| b.link(text: "Edit Agenda").click }
  action(:krms_manage_co_agendas) { |b| b.link(text: "Manage Course Offering Agendas").click}


end