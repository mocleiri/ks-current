class ManagePopulations < PopulationsBase

  page_url "#{$test_site}/kr-krad/lookup?viewId=KS-Population-LookupView&methodToCall=start&dataObjectClassName=org.kuali.student.enrollment.class2.population.dto.PopulationWrapper&hideReturnLink=true&showMaintenanceLinks=true&viewName=Manage%20Populations"

  expected_element :keyword

  frame_element
  population_lookup_elements
  green_search_buttons
  include PopulationsSearch

  action(:create_new) { |b| b.frm.link(text: "Create New").click }
end