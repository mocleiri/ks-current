class CmActiveDates < BasePage

  wrapper_elements
  cm_elements

  # Example of options 'Spring 1980', 'Fall 1985'
  element(:start_term) {|b| b.select_list(name: 'document.newMaintainableObject.dataObject.courseInfo.startTerm') }
  element(:pilot_course) { |b| b.checkbox(name: 'document.newMaintainableObject.dataObject.pilotCourse') }
  action(:set_pilot_course) { |b| b.pilot_course.set; b.loading_wait }
  element(:end_term) { |b| b.select_list(name: 'document.newMaintainableObject.dataObject.endTerm') }


  # ACTIVE DATES REVIEW FIELDS
  action(:edit_course_logistics) { |b| b.a(id: 'ActiveDates-Review-Edit-link').click }
  value(:start_term_review) { |b| b.textarea(id: "activeDatesSection_startTerm_control").text }
end
