class KradActiveDates < BasePage

  wrapper_elements
  krad_elements

  # Example of options 'Spring 1980', 'Fall 1985'
  element(:start_term) {|b| b.select_list(name: 'document.newMaintainableObject.dataObject.startTerm') }
  element(:pilot_course) { |b| b.checkbox(name: 'document.newMaintainableObject.dataObject.pilotCourse') }
  action(:set_pilot_course) { |b| b.pilot_course.set; b.loading_wait }
  element(:end_term) { |b| b.select_list(name: 'document.newMaintainableObject.dataObject.endTerm') }
end
