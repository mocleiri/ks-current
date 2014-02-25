class CmCurriculum < BasePage

  wrapper_elements
  cm_elements

  links('Create a Course', 'Find a Course', 'Find a Course Proposal', 'Course Set Management',
        'Learning Objective Categories')

  element(:cmcurriculum_header) { |b| b.span(text: "Curriculum Management KRAD") }

end